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

package org.ckc.clients.kafka.cli;

import com.github.javafaker.Faker;
import org.apache.Person;
import org.apache.commons.cli.Options;
import org.ckc.clients.kafka.common.AvroProducerPropertyFactory;
import org.ckc.clients.kafka.common.KafkaProducerClient;
import org.ckc.common.cli.OptionReader;
import org.ckc.common.cli.ProduceAction;
import org.ckc.common.watermark.Watermark;

import java.util.concurrent.ExecutionException;

public class KafkaProduceAction extends ProduceAction {
    private enum Format {
        TEXT,
        AVRO,
    };

    private String topic;
    private Format format;


    public KafkaProduceAction(String name, String[] args) {
        super(name, args);
    }

    private void setTopic(String topic) {
        this.topic = topic;
    }

    public void setFormat(String formatName) {
        if (formatName == null || formatName.isEmpty()) {
            format = Format.TEXT;

            return;
        }

        switch (formatName.toLowerCase()) {
            case "avro": {
                format = Format.AVRO;
                break;
            }
            case "text":
            default: {
                format = Format.TEXT;
                break;
            }
        }

    }

    @Override
    protected Options setupOptions() {
        Options options = super.setupOptions();

        options.addOption("T", "topic", true, "the topic to send to");
        options.addOption("f", "format", true, "the message format to use");

        return options;
    }

    @Override
    protected void eval(OptionReader optionReader) {
        super.eval(optionReader);

        optionReader.readRequiredString("topic", this::setTopic);
        optionReader.readRequiredString("format", this::setFormat);
    }

    private void sendString() throws ExecutionException, InterruptedException {
        KafkaProducerClient<String, String> kafkaClient = new KafkaProducerClient<>(getAddress());

        for (int i = 0; i < getCount(); i++) {
            kafkaClient.produce(topic, Watermark.format("Kafka Client", getText(), i));
        }
    }

    private void sendAvroBinary() throws ExecutionException, InterruptedException {
        AvroProducerPropertyFactory avroProducerPropertyFactory = new AvroProducerPropertyFactory(getAddress());
        KafkaProducerClient<String, Person> kafkaClient = new KafkaProducerClient<>(avroProducerPropertyFactory);
        Faker faker = new Faker();

        for (int i = 0; i < getCount(); i++) {
            Person person = new Person();
            person.setFirstName(faker.name().firstName());
            person.setLastName(faker.name().lastName());
            person.setAge(faker.random().nextInt(0, 110));

            System.out.println("Sending data for " + person);
            kafkaClient.produce(topic, person);
        }
    }

    @Override
    public int run() {
        try {
            switch (format) {
                case AVRO: {
                    sendAvroBinary();
                    break;
                }
                case TEXT:
                default: {
                    sendString();
                    break;
                }
            }
        } catch (ExecutionException e) {
            System.err.println("Unable to send message: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while sending the message: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}
