/*
 * Copyright Wen Bian. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list
 *   of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer listed in this license in the
 *   documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the copyright holders nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package us.fibernet.fiberj;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A composite MenuItem containing sub-MenuItems
 *
 */
public class MenuItemComposite implements MenuItem, Iterator<MenuItem> {

    private String name;
    private String surName;
    protected ArrayList<MenuItem> menuItemList;
    protected int currentIndex = 0;

    public MenuItemComposite(String surName, String name, MenuItem... menuItems) {
        this(name, menuItems);
        this.surName = surName;
    }

    public MenuItemComposite(String name, MenuItem... menuItems) {
        this.name = name;
        this.surName = "";
        menuItemList = new ArrayList<MenuItem>();
        for(MenuItem mi : menuItems) {
            menuItemList.add(mi);
        }
    }


    /** implement MenuItem's getFullName() */
    @Override
    public String getFullName() {
        return surName.replaceAll("\\s","") + name.replaceAll("\\s","");
    }

    /** implement MenuItem's setSurName */
    @Override
    public void setSurName(String surName) {
        this.surName = surName;
    }

    /** implement MenuItem's build() */
    @Override
    public JMenuItem build() {
        JMenu item = new JMenu(name);
        for(MenuItem mi : menuItemList) {
            mi.setSurName(getFullName()); // my full name is child's surname
            if(mi instanceof MenuItemSeperator) {
                item.addSeparator();
            }
            else {
                item.add(mi.build());
            }
        }
        return item;
    }


    /** implement Iterator's hasNext() */
    @Override
    public boolean hasNext() {
        return currentIndex < menuItemList.size();
    }

    /** implement Iterator's next() */
    @Override
    public MenuItem next() {
        if(hasNext()) {
            return menuItemList.get(currentIndex++);
        }
        currentIndex = 0;
        return null;
    }

    /** implement Iterator's remove() */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /** reset iterator to initial state */
    public void reset() {
        currentIndex = 0;
    }

}
