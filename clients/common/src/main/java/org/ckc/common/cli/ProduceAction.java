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

package org.ckc.common.cli;

import org.apache.commons.cli.*;

public abstract class ProduceAction extends Action {
    private final String name;

    private CommandLine cmdLine;

    private String address;
    private String text;
    private int count;

    public ProduceAction(String name, String[] args) {
        this.name = name;

        processCommand(args);
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setText(String text) {
        this.text = text;
    }

    private void setCount(int count) {
        this.count = count;
    }

    protected String getAddress() {
        return address;
    }

    protected String getText() {
        return text;
    }

    protected int getCount() {
        return count;
    }

    protected Options setupOptions() {
        Options options = new Options();

        options.addOption("h", "help", false, "prints the help");
        options.addOption("a", "address", true, "the address to produce data to");
        options.addOption("t", "text", true, "the text to send");
        options.addOption("c", "count", true, "how many messages to send");

        return options;
    }

    protected void eval(OptionReader optionReader) {
        optionReader.readRequiredString("address", this::setAddress);
        optionReader.readOptionalString("text", this::setText);
        optionReader.readRequiredInt("count", this::setCount);
    }

    @Override
    protected void processCommand(String[] args) {
        CommandLineParser parser = new DefaultParser();

        final Options options = setupOptions();

        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            help(name, options, -1);
        }

        if (cmdLine.hasOption("help")) {
            help(name, options, 0);
        }

        OptionReader optionReader = new OptionReader(this, cmdLine, options);

        eval(optionReader);
    }
}
