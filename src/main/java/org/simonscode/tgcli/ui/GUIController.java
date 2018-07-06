/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.simonscode.tgcli.telegram.TelegramClient;

import java.io.IOException;

public class GUIController implements Runnable {

    private final Terminal terminal;
    private final Screen screen;
    private final BasicWindow mainFrame;
    private final MultiWindowTextGUI gui;

    public GUIController(TelegramClient telegramClient) throws IOException {
        System.out.println("G");
        terminal = new DefaultTerminalFactory().setForceTextTerminal(true).createTerminal();
        System.out.println("F");
        terminal.enterPrivateMode();
        System.out.println("E");
        screen = new TerminalScreen(terminal);
        System.out.println("D");
        screen.startScreen();
        System.out.println("C");

        // Create window to hold the panel
        mainFrame = new MainFrame();

        System.out.println("B");
        // Create gui and start gui
        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        System.out.println("A");
    }

    @Override
    public void run() {
        gui.addWindowAndWait(mainFrame);
        try {
            terminal.exitPrivateMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");
    }
}
