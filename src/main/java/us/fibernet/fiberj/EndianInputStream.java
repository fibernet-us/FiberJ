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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A subclass of DataInputStream providing methods to read primitive data types
 * from files of both byte orders (BE for Big Endian, and LE for Little Endian).
 *
 * We don't test for byte oder in a method, instead, to make reading efficient,
 * user should do the test exact once outside a loop and call appropriate method(s).
 *
 */
public class EndianInputStream extends DataInputStream {

    public EndianInputStream(InputStream is) {
        super(is);
    }

    public EndianInputStream(String fileName) throws FileNotFoundException {
        super(new FileInputStream(fileName));
    }

    /**
     *  Read an unsigned short of Big Endian.
     *  Simply call super.readUnsignedShort() as Java is always Big Endian.
     *  This method is just for sake of symmetry with readUnsignedShortLE().
     */
    public int readUnsignedShortBE() throws IOException {
        return readUnsignedShort();
    }

    /**
     *  Read an unsigned short of Little Endian and convert it to Big Endian
     */
    public int readUnsignedShortLE() throws IOException {
        int A = readUnsignedByte();
        int B = readUnsignedByte();
        return (int) ((B << 8) | A);
    }


} // class EndianInputStream
