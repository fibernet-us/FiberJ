/*
 * Copyright Billy Zheng. All rights reserved.
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

public class MenuHandlerMainImage extends MenuHandlerMainDefault {

    /*
     * mainImage handlers
     */
    public void mainImageCrop()           { super.mainImageCrop(); }

    /*
     * mainImageFlip handlers
     */
    public void mainImageFlipHorizontal() { super.mainImageFlipHorizontal(); }
    public void mainImageFlipVertical()   { super.mainImageFlipVertical();   }

    /*
     * mainImageRotate handlers
     */
    public void mainImageRotate90()       { super.mainImageRotate90();     }
    public void mainImageRotate180()      { super.mainImageRotate180();    }
    public void mainImageRotate270()      { super.mainImageRotate270();    }
    public void mainImageRotateCustom()   { super.mainImageRotateCustom(); }

    /*
     * Image -> Resize -> Actual
     */
    public void mainImageResizeActual() {
        PatternProcessor.executeCommand("actual");
    }
    
    /*
     * Image -> Resize -> Fit
     */
    public void mainImageResizeFit() {
        PatternProcessor.executeCommand("fit");
    }
    
    /*
     * Image -> Resize -> Custom
     */
    public void mainImageResizeCustom() {
        UIMain.setMessage("enter on right: width|height number");
    }
    
    
    public void mainImageRepair()         { super.mainImageRepair();       }
    public void mainImageRescale()        { super.mainImageRescale();      }
    
}
