/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli.telegram;

import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdClient;
import org.simonscode.tgcli.Settings;
import org.simonscode.tgcli.state.GlobalStateHolder;

import static org.simonscode.tgcli.state.ApplicationState.*;

public class AuthHandler {

    private final TdClient nativeClient;

    public AuthHandler(TdClient nativeClient) {
        this.nativeClient = nativeClient;
    }

    public void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        //TODO: Handle null

        switch (authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = "tdlib";
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
                parameters.apiId = (int) Settings.get("app.id");
                parameters.apiHash = (String) Settings.get("app.hash");
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "Desktop";
                parameters.systemVersion = "Unknown";
                parameters.applicationVersion = (String) Settings.get("version");
                parameters.enableStorageOptimizer = true;
                parameters.useTestDc = Boolean.valueOf((String) Settings.get("testmode"));

                nativeClient.send(new TdApi.SetTdlibParameters(parameters));
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                nativeClient.send(new TdApi.CheckDatabaseEncryptionKey());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                GlobalStateHolder.setApplicationState(LOGIN_ENTER_PHONE);
                //                nativeClient.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, false, false));
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                GlobalStateHolder.setApplicationState(LOGIN_ENTER_CODE);
                //                nativeClient.send(new TdApi.CheckAuthenticationCode(code, "", ""));
                break;
            }
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
                GlobalStateHolder.setApplicationState(LOGIN_ENTER_PASSWORD);
                //                nativeClient.send(new TdApi.CheckAuthenticationPassword(password));
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                GlobalStateHolder.setApplicationState(NORMAL);
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                GlobalStateHolder.setApplicationState(LOGGED_OUT);
                System.out.println("Logging out");
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                GlobalStateHolder.setApplicationState(SHUTDOWN);
                System.out.println("Closing");
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                GlobalStateHolder.setApplicationState(DISCONNECTED);
                break;
            default:
                System.err.println("Unsupported authorization state:\n" + authorizationState);
        }
    }
}
