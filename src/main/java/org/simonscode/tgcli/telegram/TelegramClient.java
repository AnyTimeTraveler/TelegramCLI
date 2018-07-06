/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli.telegram;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class TelegramClient implements Client.ExceptionHandler, Client.ResultHandler {

    private final Client nativeClient;
    private final AuthHandler authHandler;
    private final MessageHandler messageHandler;

    public TelegramClient() {
        nativeClient = Client.create(this, this, this);
        authHandler = new AuthHandler(nativeClient);
        messageHandler = new MessageHandler(nativeClient);
    }

    @Override
    public void onException(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onResult(TdApi.Object object) {
        if (object.getConstructor() == TdApi.UpdateAuthorizationState.CONSTRUCTOR) {
            authHandler.onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
        } else {
            messageHandler.handle(object);
        }
    }

    public void sendMessage(long chatId, String message) {
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
        nativeClient.send(new TdApi.SendMessage(chatId, 0, false, false, null, content));
    }
}
