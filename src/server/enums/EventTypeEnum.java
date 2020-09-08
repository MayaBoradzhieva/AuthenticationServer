package server.enums;

public enum EventTypeEnum {

    FAILED_LOGIN("Failed Login"), 
    CONFIGURATION_CHANGE("Configuration Change");
    
    private String event;

    private EventTypeEnum(String event) {
        this.event = event;
    }

    public String getEventType() {
        return event;
    }
}
