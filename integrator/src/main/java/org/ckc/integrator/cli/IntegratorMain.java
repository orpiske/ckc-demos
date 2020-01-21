package org.ckc.integrator.cli;

import org.apache.camel.main.Main;

public class IntegratorMain {

    public static void main(String[] args) {
        // Read settings from the environment
        String jmsBroker = System.getenv("JMS_BROKER");
        String jmsQueue = System.getenv("JMS_QUEUE");

        String kafkaBroker = System.getenv("KAFKA_BROKER");
        String kafkaTopic = System.getenv("KAFKA_TOPIC");

        // Create the Camel main and add the route
        Main camelMain = new Main();

        camelMain.addRoutesBuilder(new JmsToKafkaRoute(jmsBroker, jmsQueue, kafkaBroker, kafkaTopic));

        try {
            // Start running the integration
            camelMain.run();
        } catch (Exception e) {
            System.err.println("Unable to start Camel: " + e.getMessage());
            e.printStackTrace();

            System.exit(1);
        }
    }

}
