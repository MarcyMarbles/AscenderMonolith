package kz.saya.project.ascender.Enums;

public enum TechResult {
    NONE("No technical result"),
    WIN("Technical win"),
    LOSS("Technical loss"),
    DRAW("Technical draw");

    private final String description;

    TechResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}