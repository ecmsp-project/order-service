# order-service

## Local development

### Prerequisites

To run order-service locally you need:

- run Docker daemon on local machine

### Start service

To start order-service locally, you need to run the app from IJ with spring profile `local`. Both `Run`/`Debug` mode are
supported.

Using this profile the application:

- run automatically kafka in docker container during the app run using testcontainers
- run automatically kafka UI in docker container during the app run using testcontainers

Once the app is exited, the containers will be stopped and removed automatically. There is no manual step needed.

### Access Kafka UI

To access Kafka UI, open the browser and navigate to `http://localhost:8088`.

### Publishing test message to Kafka topic via Kafka UI

To publish a test message to Kafka topic via Kafka UI, follow these steps:

1. Open Kafka UI in your browser at `http://localhost:8088`.
2. Select the Kafka cluster you want to interact with.
3. Select `Topics` from the navigation panel on the left.
4. Select the given topic you want to publish a message to.
5. Click on the `Produce Message` button.
6. In the `Key` field, enter the key identifier for your message
7. In the `Value` field, enter the message content in JSON format. Example"
   ```json
   {
     "orderId": "7d03d5bf-06b5-4edb-a01e-dd155907254a",
     "paymentId": "f0d57ff4-1a3b-4405-9aa8-da17ae8bf4d1",
     "processedAt": "2025-08-24T12:00:00Z"
     }
   ```
7. In the `Headers` section, you need to provide FQDN of the class representing the event, so that service can
   deserialize the event from JSON payload properly. Example:
   ```json
    {
      "__TypeId__": "com.ecmsp.orderservice.api.kafka.PaymentProcessedKafkaEventSucceeded"
    }
    ```