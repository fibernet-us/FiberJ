package us.fibernet.fiberj;
import javax.swing.JMenuItem;

public interface MenuItem {
    JMenuItem build();
    String getFullName();
    void setSurName(String name);
}
