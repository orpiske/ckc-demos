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

import java.util.Properties;
import java.util.UUID;

import io.apicurio.registry.utils.serde.AvroKafkaSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class AvroProducerPropertyFactory implements ProducerPropertyFactory {
    private final String bootstrapServer;

    /**
     * Constructs the properties using the given bootstrap server
     * @param bootstrapServer the address of the server in the format
     *                       PLAINTEXT://${address}:${port}
     */
    public AvroProducerPropertyFactory(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    @Override
    public Properties getProperties() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put("apicurio.registry.url", System.getenv("APICURIO_REGISTRY_URL"));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                AvroKafkaSerializer.class.getName());
        props.put("apicurio.registry.artifact-id", "io.apicurio.registry.utils.serde.strategy.RecordIdStrategy");
        props.put("apicurio.registry.global-id", "io.apicurio.registry.utils.serde.strategy.GetOrCreateIdStrategy");
        props.put("apicurio.registry.avro-datum-provider", "io.apicurio.registry.utils.serde.avro.ReflectAvroDatumProvider");

        return props;
    }
}
