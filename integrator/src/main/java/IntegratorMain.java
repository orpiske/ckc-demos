import org.apache.camel.main.Main;

public class IntegratorMain {

    public static void main(String[] args) {
        String jmsBroker = System.getenv("JMS_BROKER");
        String jmsQueue = System.getenv("JMS_QUEUE");

        String kafkaBroker = System.getenv("KAFKA_BROKER");
        String kafkaTopic = System.getenv("KAFKA_TOPIC");

        Main camelMain = new Main();

        camelMain.addRoutesBuilder(new JmsToKafkaRoute(jmsBroker, jmsQueue, kafkaBroker, kafkaTopic));

        try {
            camelMain.start();

            camelMain.run();
        } catch (Exception e) {
            System.err.println("Unable to start Camel: " + e.getMessage());
            e.printStackTrace();

            System.exit(1);
        }
    }

}
