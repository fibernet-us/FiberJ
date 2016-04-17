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

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


/**
 * A utility class for saving pattern data or image to a file
 */
public final class PatternWriter {

    private PatternWriter() {}

    /**
     * @param ptn   the pattern whose data or image is to be written to a file
     * @param args  the output file name and other attributes if applicable
     * @return      if the write is successful
     *
     * For type PNG/JPG/GIF, written is the pattern's displayed image (RGB color image);
     * For type TIF, written is the pattern's intensity (16-bit gray-scale);
     * For all other types, written is the pattern's intensity with appropriate header info.
     */
    public static boolean writePattern(Pattern ptn, String... args) {

        if(ptn == null || args == null || args.length < 1) {
            return false;
        }

        String fname = args[0];
        String ext = fname.substring(fname.lastIndexOf('.') + 1).toLowerCase();
        boolean writeStatus = false;

        if(args.length == 1) {
            if("smv".equals(ext) || "img".equals(ext)) {
                writeStatus = PatternSmv.writeSmv(ptn, fname);
            }
            else if("tif".equals(ext) || "tiff".equals(ext)) {
                writeStatus = TiffWriter.writeImage(ptn.getWidth(), ptn.getHeight(), ptn.getDataBytes(2), fname);
            }
            else if("dat".equals(ext) || "raw".equals(ext)) {
                // TODO
            }
            else if("png".equals(ext) || "gif".equals(ext) || "jpg".equals(ext) || "jpeg".equals(ext)) {
                writeStatus = writeRgbImage(ptn.getDisplayImage(), fname, ext);
            }
            else {
                // TODO
            }
        }
        else {
            // TODO
        }

        return writeStatus;
    }


    /**
     *  Write a BufferedImage to a file with ImageIO supported formats (PNG/JPG/GIF/BMP)
     */
    public static boolean writeRgbImage(BufferedImage bi, String fileName, String imgFormat) {

        try {
            if(bi != null) {
                File ofile = new File(fileName);
                ImageIO.write(bi, imgFormat, ofile);
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


} // class PatternWriter
