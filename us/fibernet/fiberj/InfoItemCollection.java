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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.Box;
import javax.swing.JPanel;

/**
 * A collection of InfoItems implementing InfoItem and Iterable interfaces.
 * Subclasses need to populate infoItemList themselves.
 */
abstract class InfoItemCollection implements InfoItem, Iterable<InfoItem> {
    
    protected ArrayList<InfoItem> infoItemList;
    
    /** create InfoItems and add them to infoItemList */
    protected abstract void populateInfoItemList();
    
    /** implements InfoItem */
    @Override
    public void addTo(JPanel panel) {
        for(InfoItem item : infoItemList) {
            item.addTo(panel);
            panel.add(Box.createHorizontalStrut(1)); 
        }
    }
    
    /** implements Iterable<InfoItem> */
    @Override
    public Iterator<InfoItem> iterator() {
        return  new InfoItemCollectionIterator();
    }
    

    private class InfoItemCollectionIterator implements Iterator<InfoItem> {
        
        private int currentIndex = 0; 
        
        public InfoItemCollectionIterator() {
            currentIndex = 0;
        }
        
        @Override
        public boolean hasNext() { 
            return currentIndex < infoItemList.size();
        } 

        @Override
        public InfoItem next() { 
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            return infoItemList.get(currentIndex++); 
        } 

        @Override
        public void remove() { 
            throw new UnsupportedOperationException(); 
        } 
    
    } // class InfoItemCollectionIterator
    
}
