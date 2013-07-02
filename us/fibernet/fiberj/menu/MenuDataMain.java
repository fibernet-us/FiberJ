package us.fibernet.fiberj.menu;

/**
 * A utility class containing the main window's menu data
 */
public final class MenuDataMain extends MenuDataImpl {

    private static final String[][] MENU_NAMES = {
        { "File",    /**/ "Open", "Save", "Close", "Exit" },     
        { "Image",   /**/ "Flip", "Rotate", "Resize", "Crop" },  
        { "Colormap",/**/ "Load", "Save", "Customize"},       
        { "Draw",    /**/ "Circle", "Resolution Circle", "Layerline", "Refresh", "Clear All" }, 
        { "Process", /**/ "Filter", "Plot", "Correction", "Transform", "Background"},       
        { "Window",  /**/ "Parameter", "Coordinates", "Pixel Viewer", "Log" },
        { "Help",    /**/ "About", "Resource" }
    };  
    
    // one handler per menu
    private static final MenuHandlerMain[] MENU_HANDLERS = {
        new MenuHandlerMainFile(),     // File      
        new MenuHandlerMain(),         // Image     // TODO
        new MenuHandlerMainColormap(), // Colormap 
        new MenuHandlerMain(),         // Draw      // TODO
        new MenuHandlerMain(),         // Process   // TODO
        new MenuHandlerMain(),         // Window    // TODO
        new MenuHandlerMain()          // Help      // TODO
    };

    public MenuDataMain() {
    }  
     
    @Override
    protected String[][] getMenuNames() { 
        return MENU_NAMES;     
    }
    
    @Override
    protected MenuHandler getMenuHandler(int menuID, int menuItemID) {  
        return MENU_HANDLERS[menuID];
    }

} // class MenuDataMain
