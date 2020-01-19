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

import org.apache.commons.cli.Options;
import org.ckc.clients.kafka.common.KafkaProducerClient;
import org.ckc.common.cli.OptionReader;
import org.ckc.common.cli.ProduceAction;

import java.util.concurrent.ExecutionException;

public class KafkaProduceAction extends ProduceAction {
    private String topic;

    public KafkaProduceAction(String name, String[] args) {
        super(name, args);
    }

    private void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    protected Options setupOptions() {
        Options options = super.setupOptions();

        options.addOption("T", "topic", true, "the topic to send to");

        return options;
    }

    @Override
    protected void eval(OptionReader optionReader) {
        super.eval(optionReader);

        optionReader.readRequiredString("topic", this::setTopic);
    }

    @Override
    public int run() {
        try {
            KafkaProducerClient<String, String> kafkaClient = new KafkaProducerClient<>(getAddress());

            for (int i = 0; i < getCount(); i++) {
                kafkaClient.produce(topic, getText() + " " + i);

//                Thread.sleep(1000);
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
