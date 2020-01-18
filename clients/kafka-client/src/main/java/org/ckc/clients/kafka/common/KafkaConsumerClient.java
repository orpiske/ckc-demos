/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ckc.clients.kafka.common;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumerClient<K, V> extends KafkaClient<K, V> {
    private final ConsumerPropertyFactory propertyProducer;
    private final KafkaConsumer<K, V> consumer;


    /**
     * Constructs the properties using the given bootstrap server
     *
     * @param bootstrapServer the address of the server in the format
     *                        PLAINTEXT://${address}:${port}
     */
    public KafkaConsumerClient(String bootstrapServer) {
        this.propertyProducer = new DefaultConsumerPropertyFactory(bootstrapServer);

        consumer = new KafkaConsumer<>(propertyProducer.getProperties());
    }

    /**
     * Constructs the properties using the given bootstrap server
     *
     * @param bootstrapServer the address of the server in the format
     *                        PLAINTEXT://${address}:${port}
     */
    public KafkaConsumerClient(String bootstrapServer, String offsetConfig) {
        this.propertyProducer = new DefaultConsumerPropertyFactory(bootstrapServer);

        Properties properties = propertyProducer.getProperties();

        if (offsetConfig != null) {
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetConfig);
        }

        consumer = new KafkaConsumer<>(properties);
    }


    public void subscribe(String topic) {
        consumer.subscribe(Arrays.asList(topic));
    }


    /**
     * Consumes message from the given topic
     */
    public ConsumerRecords<K, V> consume() {
        return consumer.poll(Duration.ofMillis(500));
    }
}
