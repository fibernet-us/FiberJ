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

/**
 * A default menu handler/adapter for MenuMain, auto generated by 
 * MenuMain.generateDefaultHandlerCode(). 
 */
public class MenuHandlerMainDefault implements MenuHandler {

    public void popupWarning(String what) {
        javax.swing.JOptionPane.showMessageDialog(null, what + " - not yet implemented!");
    }

    /*
     * mainFile handlers
     */
    public void mainFileOpen()         { popupWarning("mainFileOpen"); }
    public void mainFileSave()         { popupWarning("mainFileSave"); }

    /*
     * mainFileSaveAs handlers
     */
    public void mainFileSaveAsTIF()    { popupWarning("mainFileSaveAsTIF"); }
    public void mainFileSaveAsSMV()    { popupWarning("mainFileSaveAsSMV"); }
    public void mainFileClose()        { popupWarning("mainFileClose");     }
    public void mainFileBrowse()       { popupWarning("mainFileBrowse");    }
    public void mainFileConvert()      { popupWarning("mainFileConvert");   }
    public void mainFileMerge()        { popupWarning("mainFileMerge");     }

    /*
     * mainFileParameter handlers
     */
    public void mainFileParameterLoad()  { popupWarning("mainFileParameterLoad"); }
    public void mainFileParameterSave()  { popupWarning("mainFileParameterSave"); }
    public void mainFileExit()           { popupWarning("mainFileExit");          }

    
    /*
     * mainImage handlers
     */
    public void mainImageCrop()           { popupWarning("mainImageCrop"); }

    /*
     * mainImageFlip handlers
     */
    public void mainImageFlipHorizontal() { popupWarning("mainImageFlipHorizontal"); }
    public void mainImageFlipVertical()   { popupWarning("mainImageFlipVertical");   }

    /*
     * mainImageRotate handlers
     */
    public void mainImageRotate90()       { popupWarning("mainImageRotate90");     }
    public void mainImageRotate180()      { popupWarning("mainImageRotate180");    }
    public void mainImageRotate270()      { popupWarning("mainImageRotate270");    }
    public void mainImageRotateCustom()   { popupWarning("mainImageRotateCustom"); }

    /*
     * mainImageResize handlers
     */
    public void mainImageResizeActual()   { popupWarning("mainImageResizeActual"); }
    public void mainImageResizeFit()      { popupWarning("mainImageResizeFit");    }
    public void mainImageResizeCustom()   { popupWarning("mainImageResizeCustom"); }
    public void mainImageRepair()         { popupWarning("mainImageRepair");       }
    public void mainImageRescale()        { popupWarning("mainImageRescale");      }

    /*
     * mainColormap handlers
     */
    public void mainColormapCustomize()   { popupWarning("mainColormapCustomize"); }
    public void mainColormapLoad()        { popupWarning("mainColormapLoad");      }
    public void mainColormapSave()        { popupWarning("mainColormapSave");      }


    /*
     * mainDraw handlers
     */
    public void mainDrawCircle()            { popupWarning("mainDrawCircle");      }
    public void mainDrawResolution()        { popupWarning("mainDrawResolution");  }
    public void mainDrawLayerline()         { popupWarning("mainDrawLayerline");   }
    public void mainDrawMask()              { popupWarning("mainDrawMask");        }
    public void mainDrawRefresh()           { popupWarning("mainDrawRefresh");     }
    public void mainDrawClear()             { popupWarning("mainDrawClear");       }
    public void mainDrawClearAll()          { popupWarning("mainDrawClearAll");    }

    /*
     * mainProcess handlers
     */
    public void mainProcessBackground()     { popupWarning("mainProcessBackground");   }
    public void mainProcessCorrection()     { popupWarning("mainProcessCorrection");   }
    public void mainProcessFilter()         { popupWarning("mainProcessFilter");       }
    public void mainProcessPlot()           { popupWarning("mainProcessPlot");         }
    public void mainProcessTransform()      { popupWarning("mainProcessTransform");    }

    /*
     * mainWindow handlers
     */
    public void mainWindowParameter()       { popupWarning("mainWindowParameter");    }
    public void mainWindowReflection()     { popupWarning("mainWindowReflection");  }
    public void mainWindowPixelViewer()     { popupWarning("mainWindowPixelViewer");  }
    public void mainWindowComandLine()      { popupWarning("mainWindowComandLine");   }
    public void mainWindowLog()             { popupWarning("mainWindowLog");          }

    /*
     * mainHelp handlers
     */
    public void mainHelpAbout()             { popupWarning("mainHelpAbout");         }
    public void mainHelpResource()          { popupWarning("mainHelpResource");      }
}
