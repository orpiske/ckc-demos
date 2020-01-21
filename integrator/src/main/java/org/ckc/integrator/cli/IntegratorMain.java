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
