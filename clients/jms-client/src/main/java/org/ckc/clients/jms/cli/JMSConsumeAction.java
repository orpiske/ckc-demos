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

package org.ckc.clients.jms.cli;

import org.apache.commons.cli.Options;
import org.ckc.clients.jms.JMSClient;
import org.ckc.common.cli.ConsumeAction;
import org.ckc.common.cli.OptionReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class JMSConsumeAction extends ConsumeAction {
    private String queue;

    public JMSConsumeAction(String name, String[] args) {
        super(name, args);
    }

    private void setQueue(String queue) {
        this.queue = queue;
    }

    @Override
    protected Options setupOptions() {
        Options options = super.setupOptions();

        options.addOption("queue", "queue", true, "the queue to send to");

        return options;
    }

    @Override
    protected void processCommand(String[] args) {
        super.processCommand(args);
    }

    @Override
    protected void eval(OptionReader optionReader) {
        super.eval(optionReader);

        optionReader.readRequiredString("queue", this::setQueue);
    }

    @Override
    public int run() {
        JMSClient jmsClient = JMSClient.createClient(getAddress());

        try {
            jmsClient.start();

            for (int i = 0; i < getCount(); i++) {
                Message message = jmsClient.receive(queue);

                if (message == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (message instanceof TextMessage) {
                    String text = ((TextMessage) message).getText();
                    System.out.println(text);
                }
                else {
                    System.err.println("Unexpected message type");
                }
            }
        } catch (JMSException e) {
            System.err.println("Unable to receive message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unable to start the JMS client: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}
