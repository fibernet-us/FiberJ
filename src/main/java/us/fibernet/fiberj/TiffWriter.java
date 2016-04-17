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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A class for writing TIFF images. Currently only supports 16-bit gray-scale TIFF.
 */
public final class TiffWriter {

    private TiffWriter() {
    }

    /**
     * write a 16-bit TIFF image 
     */
    public static final boolean writeImage(int w, int h, byte[] pixels, String filename) {

        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(filename)));
            if(!writeHeader(dos, w, h, pixels.length)) {
                dos.close();
                return false;
            }
            
            dos.write(pixels);
            dos.close();
            return true;
        } 
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * only write 10 essential tags
     */
    private static boolean writeHeader(DataOutputStream dos, int width, int height, int bytes) {

        // data type
        final short BYTE = 1;
        final short ASCII = 2;
        final short SHORT = 3;
        final short LONG = 4;
        final short RATIONAL = 5;

        // tiff info
        final short TIFF_MAGIC_NUMBER = 42;
        final byte[] TIFF_BYTEORDER = {'M', 'M'};  // big endian
        
        // tag number
        final short NEW_SUBFILE_TYPE = 254;
        final short IMAGE_WIDTH = 256;
        final short IMAGE_LENGTH = 257;
        final short BITS_PER_SAMPLE = 258;
        final short COMPRESSION = 259;
        final short PHOTOMETRIC_INTERPOLATION = 262;
        final short STRIP_OFFSETS = 273;
        final short SAMPLES_PER_PIXEL = 277;
        final short ROWS_PER_STRIP = 278;
        final short STRIP_BYTE_COUNTS = 279;
        final short NTAGS = 10;
        
        final int HEADER_FIRST_OFFSET = 8;
        final int HEADER_SIZE = 8 + 2 + 12 * NTAGS + 4;

        try {
            dos.write(TIFF_BYTEORDER);
            dos.writeShort(TIFF_MAGIC_NUMBER);
            dos.writeInt(HEADER_FIRST_OFFSET);

            dos.writeShort(NTAGS);

            dos.writeShort(NEW_SUBFILE_TYPE); // 254
            dos.writeShort(LONG);
            dos.writeInt(1);
            dos.writeInt(0);

            dos.writeShort(IMAGE_WIDTH); // 256
            dos.writeShort(LONG);
            dos.writeInt(1);
            dos.writeInt(width);

            dos.writeShort(IMAGE_LENGTH); // 257
            dos.writeShort(LONG);
            dos.writeInt(1);
            dos.writeInt(height);

            dos.writeShort(BITS_PER_SAMPLE); // 258
            dos.writeShort(SHORT);
            dos.writeInt(1);
            dos.writeInt(0x00100000);  // 16

            dos.writeShort(COMPRESSION); // 259
            dos.writeShort(SHORT);
            dos.writeInt(1);
            dos.writeInt(0x00010000); // no compression

            dos.writeShort(PHOTOMETRIC_INTERPOLATION); // 262
            dos.writeShort(SHORT);
            dos.writeInt(1);
            dos.writeInt(0x00010000); // 1, black is 0, grayscale

            dos.writeShort(STRIP_OFFSETS); // 273
            dos.writeShort(LONG);
            dos.writeInt(1);
            dos.writeInt(HEADER_SIZE);

            dos.writeShort(SAMPLES_PER_PIXEL); // 277
            dos.writeShort(SHORT);
            dos.writeInt(1);
            dos.writeInt(0x00010000);  // 1

            dos.writeShort(ROWS_PER_STRIP);
            dos.writeShort(SHORT);
            dos.writeInt(1);
            dos.writeShort(height);
            dos.writeShort(0);

            dos.writeShort(STRIP_BYTE_COUNTS);
            dos.writeShort(LONG);
            dos.writeInt(1);
            dos.writeInt(bytes);

            dos.writeInt(0);  // end of tags
            
            return true;
        } 
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
