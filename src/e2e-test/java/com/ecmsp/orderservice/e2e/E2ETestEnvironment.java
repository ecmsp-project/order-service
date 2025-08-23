package com.ecmsp.orderservice.e2e;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

public final class E2ETestEnvironment {

    private static final GenericContainer<?> ORDER_SERVICE_CONTAINER =
        new GenericContainer<>(
            new ImageFromDockerfile()
                .withFileFromPath("Dockerfile", Paths.get("Dockerfile"))
                .withFileFromPath("target/app.jar", Paths.get("target/app.jar"))
        )
            .withExposedPorts(8080)
            .waitingFor(
                Wait.forHttp("/health")
                    .forStatusCode(200)
                    .withStartupTimeout(java.time.Duration.ofSeconds(15))
            );

    static {
        ORDER_SERVICE_CONTAINER.start(); // start only once

        Runtime.getRuntime().addShutdownHook(new Thread(ORDER_SERVICE_CONTAINER::stop));
    }

    public static String getUrl(Containers container) {
        return switch (container) {
            case ORDER_SERVICE -> "http://" + ORDER_SERVICE_CONTAINER.getHost() + ":" + ORDER_SERVICE_CONTAINER.getMappedPort(8080);
        };
    }

    public enum Containers {
        ORDER_SERVICE
    }
}
