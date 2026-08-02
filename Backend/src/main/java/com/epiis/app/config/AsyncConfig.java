package com.epiis.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * ✅ Configurar thread pool para tareas asincrónicas
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);           // Threads mínimos
        executor.setMaxPoolSize(5);            // Threads máximos
        executor.setQueueCapacity(100);        // Cola de tareas
        executor.setThreadNamePrefix("POLARIS-ASYNC-");
        executor.initialize();
        return executor;
    }
}