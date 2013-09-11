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

import java.awt.Color;

/**
 * 
 * A utility class representing a layerline object in drawing 
 *
 */
public class Layerline {

        double repeat;
        double cx, cy; // center x, y
        Color color; 
        double thetaStep; 
        
        public Layerline(double r, double x, double y, double thetStep, Color color) {
            this(r, x, y);
            this.thetaStep = thetStep;
            this.color = color;
        }
        
        public Layerline(double r, double x, double y, Color color) {
            this(r, x, y);
            this.color = color;
        }
        
        public Layerline(double r, double x, double y) {
            repeat = r;
            cx = x;
            cy = y;
            thetaStep = 1; // what value should it be?
        }
        
        public String toString() {
            return "layerline: repeat=" + repeat + ", cx=" + cx + ", cy=" + cy;
        }
        
        public double getX()          { return cx;        }
        public double getY()          { return cy;        }
        public double getR()          { return repeat;    }
        public double getThetaStep()  { return thetaStep; }
        public Color getColor()       { return color;     }
        
        public void setX(double x)    { cx = x;          }
        public void setY(double y)    { cy = y;          }
        public void setColor(Color c) { color = c;       }
        
        public void setR(double r) { 
            repeat = r; 
            thetaStep = 1; // // what value should it be?
        }
        
        public int getIX()   { return (int)(cx + 0.5);     }
        public int getIY()   { return (int)(cy + 0.5);     }
        public int getIR()   { return (int)(repeat + 0.5); }

}
