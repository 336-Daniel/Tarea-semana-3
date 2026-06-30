package com.uti.svc_rooms.model;


public enum RoomType {
    SINGLE("Habitación individual"),
    DOUBLE("Habitación doble"),
    SUITE("Suite de lujo");

    private final String description;

    RoomType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
