package com.naujokaitis.maistas.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> items = new ArrayList<>();

    public Menu(UUID id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.items = new ArrayList<>();
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
