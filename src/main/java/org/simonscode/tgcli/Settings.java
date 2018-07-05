/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli;

import java.io.IOException;

public class Settings {

    private static final java.util.Properties properties;

    static {
        properties = new java.util.Properties();
        try {
            properties.load(Settings.class.getResourceAsStream("app.properties"));
            System.out.println(properties.getProperty("version"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object get(final Object key) {
        return properties.get(key);
    }
}
