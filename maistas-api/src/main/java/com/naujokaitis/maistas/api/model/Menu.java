package com.naujokaitis.maistas.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "menus")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonIgnoreProperties({"menu", "hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MenuItem> items = new ArrayList<>();

    public Menu(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.items = new ArrayList<>();
    }

    public static Menu create(String name) {
        return new Menu(UUID.randomUUID(), name);
    }
}
