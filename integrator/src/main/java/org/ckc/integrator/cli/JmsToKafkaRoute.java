package org.ckc.integrator.cli;

import java.util.Properties;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sjms2.Sjms2Component;
import org.apache.qpid.jms.JmsConnectionFactory;

public class JmsToKafkaRoute extends RouteBuilder {
    private final String jmsBroker;
    private final String jmsQueue;
    private final String kafkaBroker;
    private final String kafkaTopic;

    public JmsToKafkaRoute(String jmsBroker, String jmsQueue, String kafkaBroker, String kafkaTopic) {
        this.jmsBroker = jmsBroker;
        this.jmsQueue = jmsQueue;
        this.kafkaBroker = kafkaBroker;
        this.kafkaTopic = kafkaTopic;
    }

    // Setup the connection factory for the Simple JMS2 component
    private void setupComponent() {
        Sjms2Component sjms2Component = new Sjms2Component();
        sjms2Component.setConnectionFactory(new JmsConnectionFactory(jmsBroker));
        getContext().addComponent("sjms2", sjms2Component);
    }


    @Override
    public void configure() throws Exception {
        setupComponent();

        String jmsUrl = String.format("sjms2://queue:%s", jmsQueue);
        String kafkaUrl = String.format("kafka:%s?brokers=%s", kafkaTopic, kafkaBroker);

        System.out.println(String.format("Creating Camel route from(%s).to(%s)", jmsUrl, kafkaUrl));

        // Define the route
        from(jmsUrl).to(kafkaUrl);
    }


}
