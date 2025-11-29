package com.naujokaitis.maistas;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

/**
 * Basic sanity checks for the JavaFX application.
 */
public class AppTest {

    @Test
    public void applicationLaunchesWithoutException() {
        assertDoesNotThrow(() -> {
            Platform.startup(() -> {
                try {
                    App app = new App();
                    Stage stage = new Stage();
                    app.start(stage);
                    stage.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    Platform.exit();
                }
            });
        });
    }
}
