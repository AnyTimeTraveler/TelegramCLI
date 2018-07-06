/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */

package org.simonscode.tgcli.ui.panels;

import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;

public class ComposePanel extends Panel {
    public ComposePanel() {
        super(new BorderLayout());
        this.addComponent(new Label("ComposePanel"), BorderLayout.Location.CENTER);
    }
    //TODO
}
