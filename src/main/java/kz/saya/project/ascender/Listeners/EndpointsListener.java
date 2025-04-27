package kz.saya.project.ascender.Listeners;

import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Getter
    private List<String> endpoints = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods()
                .forEach(
                        (key, value) -> {
                            endpoints.add(key.toString());
                        }
                );

        if (initialized.compareAndSet(false, true)) {
            logEndpoints("Mapped Endpoints (initialized)");
        } else {
            logEndpoints("Mapped Endpoints (re-initialized)");
        }
    }

    private void logEndpoints(String title) {
        System.out.println("\n=== " + title + " ===");
        for (String endpoint : endpoints) {
            System.out.println("- " + endpoint);
        }
        System.out.println("============================\n");
    }

}
