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

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducerClient<K, V> extends KafkaClient<K, V> {
    private final ProducerPropertyFactory producerPropertyFactory;
    private final KafkaProducer<K, V> producer;

    /**
     * Constructs the properties using the given bootstrap server
     *
     * @param bootstrapServer the address of the server in the format
     *                        PLAINTEXT://${address}:${port}
     */
    public KafkaProducerClient(String bootstrapServer) {
        this.producerPropertyFactory = new DefaultProducerPropertyFactory(bootstrapServer);

        Properties props = producerPropertyFactory.getProperties();

        producer = new KafkaProducer<K, V>(props);
    }


    /**
     * Sends data to a topic
     *
     * @param topic   the topic to send data to
     * @param message the message to send
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void produce(String topic, V message) throws ExecutionException, InterruptedException {
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, message);

        Future<RecordMetadata> future = producer.send(record);

        future.get();
    }
}
