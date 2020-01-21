/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ckc.integrator.cli;

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
