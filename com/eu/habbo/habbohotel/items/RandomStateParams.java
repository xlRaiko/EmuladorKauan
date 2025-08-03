/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomStateParams {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomStateParams.class);
    private int states = -1;
    private int delay = -1;

    public RandomStateParams(String customparams) throws Exception {
        Arrays.stream(customparams.split(",")).forEach(pair -> {
            String[] keyValue = pair.split("=");
            if (keyValue.length != 2) {
                return;
            }
            switch (keyValue[0]) {
                case "states": {
                    this.states = Integer.parseInt(keyValue[1]);
                    break;
                }
                case "delay": {
                    this.delay = Integer.parseInt(keyValue[1]);
                    break;
                }
                default: {
                    LOGGER.warn("RandomStateParams: unknown key: {}", (Object)keyValue[0]);
                }
            }
        });
        if (this.states < 0) {
            throw new Exception("RandomStateParams: states not defined");
        }
        if (this.delay < 0) {
            throw new Exception("RandomStateParams: states not defined");
        }
    }

    public int getStates() {
        return this.states;
    }

    public int getDelay() {
        return this.delay;
    }
}

