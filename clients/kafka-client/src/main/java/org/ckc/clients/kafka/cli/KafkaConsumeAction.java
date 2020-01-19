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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.ckc.clients.kafka.common.KafkaConsumerClient;
import org.ckc.common.cli.ConsumeAction;
import org.ckc.common.cli.OptionReader;

public class KafkaConsumeAction extends ConsumeAction {
    private String topic;
    private String offset;

    public KafkaConsumeAction(String name, String[] args) {
        super(name, args);
    }

    private void setTopic(String topic) {
        this.topic = topic;
    }

    private void setOffset(String offset) {
        this.offset = offset;
    }

    @Override
    protected Options setupOptions() {
        Options options = super.setupOptions();

        options.addOption("T", "topic", true, "the topic to consume data from");
        options.addOption("", "offset", true, "the record offset config ('earliest' or 'latest')");

        return options;
    }

    @Override
    protected void eval(OptionReader optionReader) {
        super.eval(optionReader);

        optionReader.readRequiredString("topic", this::setTopic);
        optionReader.readOptionalString("offset", this::setOffset);
    }

    @Override
    public int run() {
        try {
            KafkaConsumerClient<String, String> kafkaClient = new KafkaConsumerClient<>(getAddress(), offset);

            kafkaClient.subscribe(topic);

            int i = 0;
            while (i != getCount()) {
                ConsumerRecords<String, String> records = kafkaClient.consume();

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value());

                    i++;
                    if (i == getCount()) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
