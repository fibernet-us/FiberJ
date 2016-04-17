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

/**
 * A class to hold current values for various system wide settings
 *
 */
public final class SystemSettings {

    private static String homeDir;
    private static String workingDir;
    private static String inputImageType;
    private static String colormap;
    private static int cursorX;
    private static int cursorY;

    private SystemSettings() { }

    public static String getHomeDir()    { return homeDir;        }
    public static String getWorkingDir() { return workingDir;     }
    public static String getFileType()   { return inputImageType; }
    public static String getColormap()   { return colormap;       }
    public static int getCursorX()       { return cursorX;        }
    public static int getCursorY()       { return cursorY;        }

    public static void setHomeDir(String s)    { homeDir = s;        }
    public static void setWorkingDir(String s) { workingDir = s;     }
    public static void setFileType(String s)   { inputImageType = s; }
    public static void setColormap(String s)   { colormap = s;       }
    public static void setCursorX(int x)       { cursorX = x;        }
    public static void setCursorY(int y)       { cursorY = y;        }

    /**
     * Set system settings from a String array
     */
    public static void init(String... args) {
        homeDir = System.getProperty("user.home");
        workingDir = System.getProperty("user.dir");
        //System.out.println(homeDir);
        //System.out.println(workingDir);
    }

    /**
     * Set system settings from a settings file
     */
    public static void init(String filename) {
        homeDir = System.getProperty("user.home");
        workingDir = System.getProperty("user.dir");
    }

} // class SystemSettings
