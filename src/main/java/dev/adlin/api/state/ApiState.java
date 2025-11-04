package dev.adlin.api.state;

import org.springframework.stereotype.Component;

@Component
public class ApiState {
    public enum Status {
        RUNNING,
        LOADING,
        SHUTDOWN,
    }

    private Status currentStatus = Status.LOADING;

    public Status getStatus() {
        return this.currentStatus;
    }

    public void setStatus(Status apiStatus) {
        this.currentStatus = apiStatus;
    }
}
