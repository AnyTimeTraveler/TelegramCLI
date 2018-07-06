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

public class SettingsPanel extends Panel {
    public SettingsPanel() {
        super(new GridLayout(2));
        this.addComponent(new Label("SettingsPanel"));
        this.addComponent(new Label("SettingsPanel"));
        this.addComponent(new Label("KEY"));
        this.addComponent(new Label("VALUE"));
    }
    //TODO
}
