package ru.david.NauJava.entity;

public enum ReportStatus {
    CREATED("создан"),
    COMPLETED("завершен"),
    ERROR("ошибка");

    private final String displayName;

    ReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}