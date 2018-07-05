/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli.state;

public class GlobalStateHolder {
    private static ApplicationState applicationState = ApplicationState.WAITING_FOR_SERVER;

    public static ApplicationState getApplicationState() {
        return applicationState;
    }

    public static void setApplicationState(ApplicationState applicationState) {
        GlobalStateHolder.applicationState = applicationState;
    }
}
