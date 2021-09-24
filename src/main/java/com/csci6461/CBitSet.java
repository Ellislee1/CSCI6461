package com.csci6461;

import java.util.Arrays;

/**
 * A custom class to manage bits and bytes in Java
 */
class CBitSet {

    /**
     * Holds the number of bytes
     */
    private int no_bytes;
    /**
     * Holds the number of bits used.
     */
    private int no_bits;

    /**
     * The complete set of bits
     */
    private boolean[] bit_set;

    /**
     * Constructor using number of bits
     * @param no_bits Number of bits.
     */
    public CBitSet(int no_bits){
        this.no_bytes = find_bytes(no_bits);
        this.no_bits = no_bits;

        // Bit sets are held in number of bytes
        bit_set = new boolean[this.no_bytes*4];
    }

//    /**
//     * Create a bit set from a given boolean array;
//     * @param bit_set The boolean array
//     */
//    public CBitSet(boolean[] bit_set){
//        this.no_bits = bit_set.length;
//        this.no_bytes = find_bytes(this.no_bits);
//
//        this.bit_set = new boolean[this.no_bytes*4];
//
//        set_bits(bit_set);
//    }

    /**
     * Flips bits given an array of set bits.
     * @param new_bit_set The boolean bit array.
     * @throws IndexOutOfBoundsException Throws an error if the bit set is larger than what is allowed.
     */
    public void set_bits(boolean[] new_bit_set) throws IndexOutOfBoundsException{

        if (new_bit_set.length>no_bytes*4){

            new_bit_set = cast_down(new_bit_set);
        }

        set_zero();

        int offset = (no_bytes*4) - new_bit_set.length;

        for(int i= new_bit_set.length-1; i>=0;i--){
            this.bit_set[i+offset] = new_bit_set[i];
        }

    }

    /**
     * Reset the set to an array of zeros.
     */
    public void set_zero(){
        this.bit_set = new boolean[this.no_bytes*4];
    }

    /**
     * Find the number of bytes given the number of bits
     * @param no_bits Number of bits
     * @return Returns the number of bytes
     */
    private int find_bytes(int no_bits){
        return (int)Math.ceil(no_bits/4);
    }

    /**
     * Set a given bit based on position
     * @param position Position of bit to set
     * @param value Value to set that bit
     */
    public void set(int position, boolean value) {
        this.bit_set[position] = value;
    }

    /**
     * Set a bit to true based on position
     * @param position position to set.
     */
    public void set(int position) throws ArrayIndexOutOfBoundsException{
        this.bit_set[position] = true;
    }

    /**
     * Gets the number of set bits
     * @return Returns the number of set bits
     */
    protected int cardinality() {
        int count = 0;

        for(boolean val:this.bit_set){
            if(val){
                count ++;
            }
        }
        return count;
    }

    /**
     * Gets the size of in bits
     * @return Returns the size in bits
     */
    public int get_size(){
        return this.no_bits;
    }

    /**
     * Get the set bits as a string
     * @return returns a string of the set bits array.
     */
    @Override
    public String toString(){
        return Arrays.toString(get_set_bits());
    }

    /**
     * Gets the value as an integer
     * @return the value as an integer
     */
    public int read() {
        StringBuilder s = new StringBuilder();

        for(boolean val:this.bit_set){
            if(val){
                s.append("1");
            } else {
                s.append("0");
            }
        }

        return Integer.parseInt(s.toString(),2);
    }

    /**
     * Gets the set bits as an array
     * @return Returns array of set bit positions
     */
    protected int[] get_set_bits(){
        int[] set = new int[cardinality()];

        int position = set.length-1;
        for(int i = (this.no_bytes*4)-1;i>=0;i--){
            if (this.bit_set[i]){
                set[position] = i;
                position --;
            }
        }
        return set;
    }

    /**
     * Cast from higher dimensionality down
     * @param org the higher dimension array
     * @return The matching dimension array
     */
    private boolean[] cast_down(boolean[] org){
        boolean[] valid = new boolean[no_bytes*4];

        int offset = org.length-valid.length;

        for(int i=org.length-1;i>= offset;i--){
            valid[i-offset]=org[i];
        }

        return valid;
    }
}
