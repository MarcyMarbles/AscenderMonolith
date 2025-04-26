package kz.saya.project.ascender.Enums;

public enum Status {
    ACTIVE("IN PROGRESS"),
    END("FINISHED"),
    CANCEL("CANCELED");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Status fromString(String status) {
        for (Status s : Status.values()) {
            if (s.status.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }

    public static Status fromStringIgnoreCase(String status) {
        for (Status s : Status.values()) {
            if (s.status.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }

    public static Status fromStringIgnoreCaseOrNull(String status) {
        for (Status s : Status.values()) {
            if (s.status.equalsIgnoreCase(status)) {
                return s;
            }
        }
        return null;
    }
}
