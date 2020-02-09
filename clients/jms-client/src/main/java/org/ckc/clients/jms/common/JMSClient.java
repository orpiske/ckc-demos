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

package org.ckc.clients.jms.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Enumeration;
import java.util.function.Function;

/**
 * A basic multi-protocol JMS client
 */
public class JMSClient {
    private static final Logger LOG = LoggerFactory.getLogger(JMSClient.class);

    private final String url;
    private Connection connection;
    private Session session;

    private final Function<String, ? extends ConnectionFactory> connectionFactory;
    private final Function<String, ? extends Queue> destinationFactory;

    public JMSClient(Function<String, ? extends ConnectionFactory> connectionFactory,
                     Function<String, ? extends Queue> destinationFactory,
                     String url) {
        this.connectionFactory = connectionFactory;
        this.destinationFactory = destinationFactory;
        this.url = url;
    }


    @SuppressWarnings("UnusedReturnValue")
    public static Throwable capturingClose(MessageProducer closeable) {
        LOG.debug("Closing the producer ");

        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable t) {
                LOG.warn("Error closing the producer: {}", t.getMessage(), t);
                return t;
            }
        }
        return null;
    }

    private static void capturingClose(Session closeable) {
        LOG.debug("Closing the session ");

        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable t) {
                LOG.warn("Error closing the session: {}", t.getMessage(), t);
            }
        }
    }

    private static void capturingClose(MessageConsumer closeable) {
        LOG.debug("Closing the consumer");

        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable t) {
                LOG.warn("Error closing the consumer: {}", t.getMessage(), t);
            }
        }
    }

    private static void capturingClose(Connection closeable) {
        LOG.debug("Closing the connection");

        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable t) {
                LOG.warn("Error closing the connection: {}", t.getMessage(), t);
            }
        }
    }


    public void start() throws Exception {
        LOG.debug("Starting the JMS client");

        try {
            final ConnectionFactory factory = connectionFactory.apply(url);

            LOG.debug("Creating the connection");

            int retries = 30;
            do {
                try {
                    connection = factory.createConnection();
                    LOG.debug("Connection created successfully");
                    break;
                } catch (JMSException e) {
                    LOG.warn("Unable to connect ... retrying");
                    retries--;

                    if (retries == 0) {
                        throw e;
                    }
                    Thread.sleep(1000);
                }
            } while (retries > 0);

            LOG.debug("Creating the JMS session");
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            LOG.debug("JMS session created successfully");
        } catch (Throwable t) {
            LOG.trace("Something wrong happened while initializing the JMS client: {}", t.getMessage(), t);

            capturingClose(connection);
            throw t;
        }

        connection.start();
    }

    public void stop() {
        try {
            LOG.debug("Stopping the JMS session");
            capturingClose(session);

            LOG.debug("Stopping the JMS connection");
            capturingClose(connection);
        } finally {
            session = null;
            connection = null;
        }
    }

    private Destination createDestination(final String destinationName) {
        return destinationFactory.apply(destinationName);
    }


    /**
     * Receives data from a JMS queue or topic
     *
     * @param queue the queue or topic to receive data from
     * @throws JMSException
     */
    public Message receive(final String queue) throws JMSException {
        final long timeout = 3000;

        MessageConsumer consumer = null;

        try {
            consumer = session.createConsumer(createDestination(queue));

            return consumer.receive(timeout);
        } finally {
            capturingClose(consumer);
        }
    }

    /**
     * Sends data to a JMS queue or topic
     *
     * @param request the queue or topic to send data to
     * @param data  the (string) data to send
     * @throws JMSException
     */
    public void replyTo(final Message request, final String data) throws JMSException {
        MessageProducer producer = null;

        try {
            Destination replyTo = request.getJMSReplyTo();
            producer = session.createProducer(replyTo);

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(0);

            Message reply = session.createTextMessage(data);
            reply.setJMSCorrelationID(request.getJMSCorrelationID());

            Enumeration properties = request.getPropertyNames();
            while (properties.hasMoreElements()) {
                Object current = properties.nextElement();
                reply.setObjectProperty((String) current, request.getObjectProperty((String) current));
            }

            producer.send(reply);
        } finally {
            capturingClose(producer);
        }
    }

    /**
     * Sends data to a JMS queue or topic
     *
     * @param destination the queue or topic to send data to
     * @param data  the (string) data to send
     * @throws JMSException
     */
    public void send(final Destination destination, final String data) throws JMSException {
        MessageProducer producer = null;

        try {
            producer = session.createProducer(destination);

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(0);

            Message message = session.createTextMessage(data);

            producer.send(message);
        } finally {
            capturingClose(producer);
        }
    }


    /**
     * Sends data to a JMS queue or topic
     *
     * @param queue the queue or topic to send data to
     * @param data  the (string) data to send
     * @throws JMSException
     */
    public void send(final String queue, final String data) throws JMSException {
        send(createDestination(queue), data);
    }

    /**
     * Sends data to a JMS queue or topic
     *
     * @param queue the queue or topic to send data to
     * @param data  the (string) data to send
     * @throws JMSException
     */
    public void send(final String queue, int data) throws JMSException {
        MessageProducer producer = null;

        try {
            producer = session.createProducer(createDestination(queue));

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(0);

            Message message = session.createObjectMessage(data);

            producer.send(message);
        } finally {
            capturingClose(producer);
        }
    }

    public static JMSClient createClient(String url) {
        return new JMSClient(org.apache.qpid.jms.JmsConnectionFactory::new,
                org.apache.qpid.jms.JmsQueue::new, url);
    }
}
