/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli.state;

public enum ApplicationState {
    DISCONNECTED,
    WAITING_FOR_SERVER,
    LOGIN_ENTER_PHONE,
    LOGIN_ENTER_CODE,
    LOGIN_ENTER_PASSWORD,
    NORMAL,
    LOGGED_OUT,
    SHUTDOWN,
    ERROR,
}
