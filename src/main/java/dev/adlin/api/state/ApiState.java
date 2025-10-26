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

    public Status getApiStatus() {
        return this.currentStatus;
    }

    public void setApiStatus(Status apiStatus) {
        this.currentStatus = apiStatus;
    }
}
