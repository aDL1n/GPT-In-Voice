package dev.adlin.api.listener;

import dev.adlin.api.state.ApiState;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class AppStatusListener {

    private final ApiState state;

    public AppStatusListener(ApiState state) {
        this.state = state;
    }

    @EventListener()
    private void onApplicationLoaded(ContextRefreshedEvent event) {
        this.state.setApiStatus(ApiState.Status.RUNNING);
    }

    @EventListener()
    private void onApplicationShuttingDown(ContextRefreshedEvent event) {
        this.state.setApiStatus(ApiState.Status.SHUTDOWN);
    }
}
