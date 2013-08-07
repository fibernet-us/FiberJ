package us.fibernet.fiberj;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
