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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.function.Consumer;

public class OptionReader {
    private final Action action;
    private final CommandLine cmdLine;
    private final Options options;

    public OptionReader(Action action, CommandLine cmdLine, Options options) {
        this.action = action;
        this.cmdLine = cmdLine;
        this.options = options;
    }

    /**
     * Check if required value is provided
     * @param value
     * @param name
     */
    public void checkRequired(String value, String name) {
        if (value == null) {
            System.err.println(String.format("The '%s' is a required option", name));
            action.help(name, options, 1);
        }
    }

    public void readRequiredString(String name, Consumer<String> setter) {
        String value = cmdLine.getOptionValue(name);
        checkRequired(value, name);
        setter.accept(value);
    }

    public void readRequiredInt(String name, Consumer<Integer> setter) {
        String value = cmdLine.getOptionValue(name);
        checkRequired(value, name);
        setter.accept(Integer.valueOf(value));
    }

    public void readOptionalString(String name, Consumer<String> setter) {
        String value = cmdLine.getOptionValue(name);
        setter.accept(value);
    }
}
