/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli;

import org.drinkless.tdlib.Log;
import org.simonscode.tgcli.telegram.TelegramClient;
import org.simonscode.tgcli.ui.GUIController;

import java.io.IOError;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            System.loadLibrary("tdjni");
            // disable TDLib log
            Log.setVerbosityLevel(0);
            if (!Log.setFilePath("tdlib.log")) {
                throw new IOError(new IOException("Write access to the current directory is required"));
            }

            TelegramClient telegramClient = new TelegramClient();
            GUIController guiController = new GUIController(telegramClient);
            guiController.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
