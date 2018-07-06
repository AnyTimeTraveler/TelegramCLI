/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli.ui.panels;

import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;

public class ChatListPanel extends Panel {
    public ChatListPanel() {
        super(new GridLayout(1));
        this.addComponent(new Label("ChatListPanel"));
        this.addComponent(new Label("ChatListPanel"));
        this.addComponent(new Label("ChatListPanel"));
        this.addComponent(new Label("ChatListPanel"));
        this.addComponent(new Label("ChatListPanel"));
    }
    //TODO
}
