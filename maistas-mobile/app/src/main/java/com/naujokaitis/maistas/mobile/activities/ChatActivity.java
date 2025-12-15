package com.naujokaitis.maistas.mobile.activities;

import static com.naujokaitis.maistas.mobile.utils.Constants.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.*;
import com.naujokaitis.maistas.mobile.R;
import com.naujokaitis.maistas.mobile.model.MessageType;
import com.naujokaitis.maistas.mobile.utils.RestOperations;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private ListView messagesList;
    private EditText messageInput;
    private String orderId;
    private String userId;
    private String threadId;
    private ArrayAdapter<JsonObject> adapter;
    private List<JsonObject> messages = new ArrayList<>();
    private Handler pollHandler = new Handler(Looper.getMainLooper());
    private Runnable pollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        orderId = getIntent().getStringExtra("orderId");
        userId = getIntent().getStringExtra("userId");
        
        if (orderId == null || userId == null) {
            Toast.makeText(this, "Error: Missing order or user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        messagesList = findViewById(R.id.messagesList);
        messageInput = findViewById(R.id.messageInput);
        
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.sendButton).setOnClickListener(v -> sendMessage());

        adapter = new ArrayAdapter<JsonObject>(this, 0, messages) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                JsonObject msg = getItem(position);
                String authorId = msg.getAsJsonObject("author").get("id").getAsString();
                String content = msg.get("content").getAsString();
                String time = msg.get("sentAt").getAsString();
                // Simple time format (assuming ISO string, taking HH:MM part roughly or just showing as is)
                // In production, parse date properly.
                if (time.contains("T")) {
                   try {
                       time = time.split("T")[1].substring(0, 5);
                   } catch(Exception e) {}
                }

                if (authorId.equals(userId)) {
                    convertView = getLayoutInflater().inflate(R.layout.item_message_sent, parent, false);
                } else {
                    convertView = getLayoutInflater().inflate(R.layout.item_message_received, parent, false);
                    TextView authorView = convertView.findViewById(R.id.messageAuthor);
                    if (authorView != null) {
                        String authorName = msg.getAsJsonObject("author").get("username").getAsString();
                        authorView.setText(authorName);
                    }
                }
                
                TextView contentView = convertView.findViewById(R.id.messageContent);
                TextView timeView = convertView.findViewById(R.id.messageTime);
                
                contentView.setText(content);
                timeView.setText(time);
                
                return convertView;
            }
        };
        messagesList.setAdapter(adapter);

        initChat();
    }

    private void initChat() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Start or get existing chat thread
                // API expects POST /api/chat/start/{orderId}?initiatorId={userId}
                String url = CHAT_START_URL + orderId + "?initiatorId=" + userId;
                // Using POST with empty body as params are in URL
                String response = RestOperations.sendPost(url, "");
                
                if (response != null && !response.equals("Error")) {
                    JsonObject thread = new Gson().fromJson(response, JsonObject.class);
                    threadId = thread.get("id").getAsString();
                    new Handler(Looper.getMainLooper()).post(this::startPolling);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> 
                        Toast.makeText(this, "Failed to initialize chat", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                 e.printStackTrace();
            }
        });
    }

    private void startPolling() {
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                pollHandler.postDelayed(this, 3000); // Poll every 3 seconds
            }
        };
        pollHandler.post(pollRunnable);
    }

    private void loadMessages() {
        if (threadId == null) return;
        
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String response = RestOperations.sendGet(CHAT_MESSAGES_URL + threadId + "/messages");
                if (response != null && !response.equals("Error")) {
                    JsonArray jsonMessages = new Gson().fromJson(response, JsonArray.class);
                    new Handler(Looper.getMainLooper()).post(() -> updateMessages(jsonMessages));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateMessages(JsonArray newMessages) {
        messages.clear();
        for (JsonElement el : newMessages) {
            messages.add(el.getAsJsonObject());
        }
        adapter.notifyDataSetChanged();
        // Scroll to bottom?
        messagesList.setSelection(adapter.getCount() - 1);
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty() || threadId == null) return;

        JsonObject body = new JsonObject();
        body.addProperty("authorId", userId);
        body.addProperty("content", text);
        body.addProperty("messageType", MessageType.TEXT.name());

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String response = RestOperations.sendPost(CHAT_MESSAGES_URL + threadId + "/message", body.toString());
                if (response != null && !response.equals("Error")) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        messageInput.setText("");
                        loadMessages(); // Instant refresh
                    });
                }
            } catch (IOException e) {
                 new Handler(Looper.getMainLooper()).post(() -> 
                     Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollHandler != null && pollRunnable != null) {
            pollHandler.removeCallbacks(pollRunnable);
        }
    }
}
