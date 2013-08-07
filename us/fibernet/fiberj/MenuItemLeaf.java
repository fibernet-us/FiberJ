package us.fibernet.fiberj;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class MenuItemLeaf implements MenuItem, ActionListener {
    
    String name;
    String surName;
    MenuHandler handler;
    
    public MenuItemLeaf(String surName, String name, MenuHandler handler) {
        this(name, handler);
        this.surName = surName;       
    }
    
    public MenuItemLeaf(String name, MenuHandler handler) {
        this.name = name;
        this.surName = "";
        this.handler = handler;        
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
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(this);         
        return item;
    }

    
    /** implement ActionListener actionPerformed() */
    @Override
    public void actionPerformed(ActionEvent event) {
        execute();
    }

    private void execute() {

        // look up the method in the handler class and call it if successful
        try {             
            java.lang.reflect.Method method = handler.getClass().getDeclaredMethod(getFullName());

            if(method != null) {
                try {
                    method.invoke(handler, (Object[]) null);  // invoke handler.method()
                } 
                catch (java.lang.reflect.InvocationTargetException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }   
            }
        } 
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }   

    } // execute()

}
