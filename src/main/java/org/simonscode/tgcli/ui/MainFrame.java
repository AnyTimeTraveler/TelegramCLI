/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli.ui;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Panel;
import org.simonscode.tgcli.ui.panels.*;

import java.util.Arrays;

public class MainFrame extends BasicWindow {

    public MainFrame() {
        this.setHints(Arrays.asList(Hint.FULL_SCREEN));
        Panel chatListPanel = new ChatListPanel();
        Panel contactListPanel = new ContactListPanel();
        Panel composePanel = new ComposePanel();
        Panel historyPanel = new HistoryPanel();
        Panel settingsPanel = new SettingsPanel();

        Panel chatPanel = new Panel(new BorderLayout());
        chatListPanel.addComponent(historyPanel, BorderLayout.Location.CENTER);
        chatListPanel.addComponent(composePanel, BorderLayout.Location.BOTTOM);

        Panel basePanel = new Panel(new BorderLayout());
        basePanel.addComponent(chatListPanel, BorderLayout.Location.LEFT);
        basePanel.addComponent(chatPanel, BorderLayout.Location.CENTER);

        //        this.addBasePaneListener(new BasePaneListener<Window>() {
        //            @Override
        //            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
        //                System.out.println("Handled: " + keyStroke.getCharacter());
        //            }
        //
        //            @Override
        //            public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
        //                System.out.println("Unhandled: " + keyStroke.getCharacter());
        //            }
        //        });
        this.setComponent(basePanel);
    }
}
