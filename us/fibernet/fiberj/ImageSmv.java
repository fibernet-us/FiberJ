/*
 * Copyright Kate Wu. All rights reserved.
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * @Description utility class to read SMV image file
 * @author kate.b.wu@gmail.com
 * 
 */
public final class ImageSmv {

    private enum DataType { BYTE, UNSIGNED_BYTE, SHORT, UNSIGNED_SHORT, INT, UNSIGNED_INT, FLOAT, DOUBLE };

    private ImageSmv() {}

    /**
     * read SMV and return a Pattern with available parameters set
     */
    public static Pattern readSmvPattern(String fileName) {

        Map<String, String> parameters = readSmvHeader(fileName);

        try {
            Boolean isrecip = false;
            double pixsize  = 0;
            double wlength  = 0;
            double distance = 0;
            double offset   = 0;
            double centerx  = 0;
            double centery  = 0;

            try {
                // SIZE1, 2 always exist
                centerx = Double.parseDouble(parameters.get("SIZE1")) / 2;
                centery = Double.parseDouble(parameters.get("SIZE2")) / 2;
                
                // if BEAM_CENTER_X, Y do not exist, then centerx, y not updated
                centerx = Double.parseDouble(parameters.get("BEAM_CENTER_X")); 
                centery = Double.parseDouble(parameters.get("BEAM_CENTER_Y")); 
            }
            catch(Exception e) {}

            try { isrecip = parameters.get("IsRecip").equals("1"); } 
            catch(Exception e) {}

            try { pixsize = Double.parseDouble(parameters.get("PIXEL_SIZE")); } 
            catch(Exception e) {}

            try { wlength = Double.parseDouble(parameters.get("WAVELENGTH")); }
            catch(Exception e) {}

            try { distance = Double.parseDouble(parameters.get("DISTANCE")); }
            catch(Exception e) {} 

            try { offset = Double.parseDouble(parameters.get("ZeroOffset")); }
            catch(Exception e) {}

            int[][] data = readSmv(fileName);

            if(data != null) {
                Pattern p = new Pattern(data, new File(fileName).getName(), isrecip);
                p.setCenterX(centerx);
                p.setCenterY(centery);
                p.setPixelSize(pixsize);
                p.setWavelen(wlength);
                p.setSdd(distance);
                p.setOffset(offset);

                return p;
            }
        }
        catch(NumberFormatException e) {
            e.printStackTrace();
        }

        return null;
    }

    
    /**
     * read SMV file and return data as int[][]
     * 
     */
    public static int[][] readSmv(String fileName) {

        Map<String, String> parameters = readSmvHeader(fileName);

        try {
            // if any of the following parameters is not set/read correctly then we return null
            boolean isBigEndian = parameters.get("BYTE_ORDER").toLowerCase().startsWith("big");
            int headerBytes = Integer.parseInt(parameters.get("HEADER_BYTES"));
            int imageWidth  = Integer.parseInt(parameters.get("SIZE1"));
            int imageHeight = Integer.parseInt(parameters.get("SIZE2"));
            DataType type   = getDataType(parameters.get("TYPE"));

            return readSmvData(fileName, isBigEndian, type, headerBytes, imageWidth, imageHeight);
        }
        catch(Exception e) { 
            e.printStackTrace(); 
        }

        return null;
    }

    
    /**
     * dump the header information into string
     */
    public static String dumpSmvHeader(String fileName) {
        Map<String, String> parameters = readSmvHeader(fileName);
        return parameters.toString();
    }



    /*
     * helper method, read SMV imageData headerString and return a HashMap which store the
     * parameter name and value
     */
    private static Map<String, String> readSmvHeader(String fileName) {

        Map<String, String> hashMap = new HashMap<String, String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();// read the beginning "{" and pass

            while ((line = reader.readLine()) != null) {

                // break out of loop when read "}"
                if (line.contains("}")) {
                    break;
                }

                String[] s = line.split("[=; ]+");

                if (s.length == 1 && !s[0].isEmpty()) {
                    hashMap.put(s[0], "1");  // this parameter is set to be true
                } 
                else if (s.length > 1) {
                    hashMap.put(s[0], s[1]);
                } 
                else {
                    // TODO? ignore for now
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return hashMap;
    }

    
    /*
     * Read the binary image data in SMV file 
     * 
     * Use buffered stream to read all data in, then do appropriate conversion (data type & byte order)
     * 
     * Converting while reading takes too much time, for a 3072x3072 image it takes 25 seconds!!
     * With buffer reading, it takes 0.2 seconds.
     */
    private static int[][] readSmvData(String fileName, boolean isBigEndian, DataType type,
                                       int headerBytes, int imageWidth, int imageHeight)    {

        BufferedInputStream bis = null;  
        int[][] imageData = null;
        int bytesRead=0;
        
        //Timer t = new Timer();
        //t.startTimer();
 
        try {
            int nBytes = imageWidth * imageHeight * getPixelSize(type);
            byte[] bytes = new byte[nBytes];
  
            bis = new BufferedInputStream((new FileInputStream(new File(fileName))));
            bis.skip(headerBytes);
            bytesRead = bis.read(bytes, 0, nBytes);
            
            if(bytesRead == nBytes) {
                imageData = convertBytesToData(bytes, isBigEndian, type, imageWidth, imageHeight);
            }
            else {
                JOptionPane.showMessageDialog(null, "Image reading error. Expected bytes: " + 
                                                     nBytes + ". Read: " + bytesRead);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            if (bis != null) {
                try {
                    bis.close();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //t.endTimer();
        //t.printElapsedMilliSeconds();
        
        return imageData;
    }

    /*
     *  get the number of bytes in a pixel
     */
    private static int getPixelSize(DataType type) {
        int size = 2;
        switch(type) {
            case DOUBLE: 
                size = 8;
                break;        
                //
            case INT: 
            case FLOAT: 
                size = 4;
                break;        
                //
            case UNSIGNED_SHORT: 
            default: 
                size = 2;
                break;                         
        }
        return size;
    }
    
    /*
     *  convert raw array of bytes to image data (data type & byte order)
     */
    private static int[][] convertBytesToData(byte[] bytes, boolean isBigEndian, DataType type, 
            int imageWidth, int imageHeight) {

        int[][] data = new int[imageHeight][imageWidth];

        // these are to save a branch inside for loops
        int shiftA = isBigEndian ? 8 : 0;
        int shiftB = isBigEndian ? 0 : 8;

        // do switch before outside loop
        switch(type) {
            case INT: 
                // TODO
                break;        
                //
            case FLOAT: 
                // TODO
                break;        
                //
            case DOUBLE: 
                // TODO
                break;        
                //
            case UNSIGNED_SHORT: 
                // fall through. default SMV data type is UNSIGNED_SHORT
            default: 
                for(int i=0, n=0; i<imageHeight; i++) {
                    for(int j=0; j<imageWidth; j++) {
                        int A = bytes[n++] & 0xFF;  // convert to int as unsigned byte
                        int B = bytes[n++] & 0xFF;
                        data[i][j] = (A << shiftA) | (B << shiftB);
                    }
                }
                break;                         
        }

        return data;
    }
       
    
    /*
     * determine DataType of the image data given a type string
     */
    private static DataType getDataType(String type) {
        return DataType.valueOf(type.toUpperCase());
    }
}
