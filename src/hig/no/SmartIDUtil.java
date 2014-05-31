
package hig.no;

import javacard.framework.ISO7816;

/**
 * Generic helpers for the Applet.
 * 
 *
 */
public class SmartIDUtil implements ISO7816 {
    /**
     * Counts the number of set bits in a byte
     * 
     * @param b byte to be counted
     * @return 0 when number of bits in b is even
     */
    static byte evenBits(byte b) {
        short count = 0;
    
        for (short i = 0; i < 8; i++) {
            count += (b >>> i) & 0x1;
        }
    
        return (byte) (count % 2);
    }

    /**
     * Calculates the xor of byte arrays in1 and in2 into out. 
     * 
     * Arrays may be the same, but regions may not overlap.
     * 
     * 
     * @param in1 input array
     * @param in1_o offset of input array
     * @param in2 input array
     * @param in2_o offset of inputarray
     * @param out output array
     * @param out_o offset of output array
     * @param len length of xor
     */
    static void xor(byte[] in1, short in1_o, byte[] in2, 
    			short in2_o, byte[] out, short out_o, short len) {
        for(short s=0; s < len; s++) {
            out[(short)(out_o + s)] = (byte)(in1[(short)(in1_o + s)] ^ in2[(short)(in2_o + s)]);
        }
    }

    /**
     * Swaps two non-overlapping segments of the same length in the same byte array 
     * in place.
     * 
     * @param buffer a byte array 
     * @param offset1 offset to first byte array
     * @param offset2 offset to the second byte array
     * @param len length of the segments
     */
    static void swap(byte[] buffer, short offset1, short offset2, short len) {    
        byte byte1, byte2;
        for(short i=0; i<len; i++) {
            byte1 = buffer[(short)(offset1 + i)];
            byte2 = buffer[(short)(offset2 + i)];
            buffer[(short)(offset1 + i)] = byte2;
            buffer[(short)(offset2 + i)] = byte1;
        }
    }
    
    /**
     * Returns the sign bit of a short as a short.
     * @param a a short value
     * @return the sign bit of a as a short
     */
    static short sign(short a) {
        return (byte)((a >>> (short)15) & 1); 
    }
    
    /**
     * Returns the smallest unsigned short argument.
     * 
     * @param a a short
     * @param b another short
     * @return smallest unsigned value a or b.
     */
    static short min(short a, short b) {
        if(sign(a) == sign(b))
          return (a < b ? a : b);
        else if(sign(a) == 1)
            return b;
        else 
            return a;
    }

    /***
     * Pads an input buffer with max 8 and min 1 byte padding (0x80 followed by optional zeros) 
     * relative to the offset and length given. Always pad with at least a 0x80 byte.
     * 
     * See 6.2.3.1 in ISO7816-4
     * 
     * @param buffer array to pad
     * @param offset to data
     * @param length of data
     * @return new length, with padding, of data 
     * 
     */
    static short pad(byte[] buffer, short offset, short len) {
    	short padbytes = (short)(lengthWithPadding(len) - len);
                
        for(short i=0; i<padbytes; i++) {
            buffer[(short)(offset+len+i)] = (i == 0 ? (byte)0x80 : 0x00);
        }
        
        return (short)(len + padbytes);
    }
    
    static short lengthWithPadding(short inputLength) {
    	return (short)((((short)(inputLength + 8)) / 8) * 8); 
    }
    

    /***
     * Computes the actual length of a data block as byte value, without the padding.
     * 
     * @param apdu containing data
     * @param offset to data
     * @param length of data
     * @return new length of data, without padding
     */
    static byte calcLcFromPaddedData(byte[] apdu, short offset, short length) {
        for(short i=(short)(length - 1) ; i>=0; i--)
            if(apdu[(short)(offset + i)] != 0)
                if((apdu[(short)(offset + i)] & 0xff)!= 0x80)
                    // not padded
                    return (byte)(length & 0xff);       
                else
                    return (byte)(i & 0xff);       
        
        return 0;
    }
}  
