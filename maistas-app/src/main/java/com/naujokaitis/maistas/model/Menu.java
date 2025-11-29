package com.naujokaitis.maistas.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Menu {

    private final UUID id;
    @Getter(AccessLevel.NONE)
    private final List<MenuItem> items;

    public Menu(UUID id) {
        this(id, List.of());
    }

    public Menu(UUID id, List<MenuItem> items) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.items = new ArrayList<>(Objects.requireNonNullElse(items, List.of()));
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<MenuItem> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
    }

    public void addItem(MenuItem item) {
        items.add(Objects.requireNonNull(item, "item must not be null"));
    }

    public void removeItem(MenuItem item) {
        items.remove(item);
    }
}
