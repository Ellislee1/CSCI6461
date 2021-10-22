/**
 * This file implements a simple Cache for the CSCI 6461 Computer simulation
 */
package com.csci6461;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The Cache class inherits from Memory but uses a smaller data storage to
 * hold copies of memory blocks of the specified size and overrides the read
 * method to check for data first in the cache and implement the replacement
 * algorithm in case of a miss. The write method is also overridden to implement
 * write-through write policy using a buffer in the cache.
 */
public class Cache extends Memory{
    /**
     * Parameter to hold cache size
     */
    private int cacheSize;
    /**
     * Parameter to hold number of words per memory block
     */
    private int blockSize;
    /**
     * Parameter to hold a hash map to be used as storage for the cache
     */
    private HashMap<Short, short[]> cache;
    /**
     * Parameter to hold size of byte offset
     */
    private int byteOffset;
    /**
     * Parameter to hold mask to extract byte offset field from address
     */
    private short offsetMask = 0;
    /**
     * Parameter to hold list of tags so we can implement replacement logic
     */
    private Short[] tagList;

    /**
     * This method is called by the various constructors to initialize the cache
     *
     * @param s   Int with size of memory array in number of 16-bit words
     * @param c   Int with size of cache in 16-bit words
     * @param b   Int with number words in a memory block
     * @param mar Register object to use as Memory Address Register (MAR)
     * @param mbr Register object to use as Memory Buffer Register (MBR)
     *
     * @throws IOException If an invalid memory size is specified
     */
    private void initializeCache(int s, int c, int b, Register mar, Register mbr) throws IOException {
        /* Initialize cache size and memory block size */
        this.cacheSize = c;
        this.blockSize = b;

        /* Check to make sure Memory size if divisible by cache size */
        if (s % this.cacheSize != 0) {
            String error = String.format(
                    "Invalid memory size: %d; Memory must be divisible by cache size of %d.",
                    s, c);
            throw new IOException(error);
        }

        /* Create cache storage array according to configured size */
        cache = new HashMap<Short, short[]>(this.cacheSize);
        System.out.printf("[Cache::Cache] Created Cache storage is size: %d\n", c);

        /* Calculate byteOffset field size from block size */
        byteOffset = (int) (Math.log(this.blockSize) / Math.log(2));
        System.out.printf("[Cache::Cache] Size of byte offset field is: %d\n", byteOffset);

        /* Get mask to extract byte offset field by successive shift */
        for (int i = 0; i < byteOffset; i++) {
            offsetMask = (short) (offsetMask + (0x0001 << i));
        }
        System.out.printf("[Cache::Cache] Offset mask is: %s\n", Integer.toBinaryString((int)offsetMask));

        /* Allocate storage for tag list */
        tagList = new Short[this.cacheSize];
    }

    /**
     * The cache class constructor creates a new two-dimensional data storage array
     * of the configured size for the cache storage and calls the superclass constructor.
     * to initialize the memory
     *
     * @param s   Int with size of memory array in number of 16-bit words
     * @param c   Int with size of cache in 16-bit words
     * @param b   Int with number words in a memory block
     * @param mar Register object to use as Memory Address Register (MAR)
     * @param mbr Register object to use as Memory Buffer Register (MBR)
     *
     * @throws IOException If an invalid memory size is specified
     */
    public Cache(int s, int c, int b, Register mar, Register mbr) throws IOException {
        /* Call superclass constructor to initialize memory */
        super(s, mar, mbr);

        /* Call method to initialize the cache */
        initializeCache(s, c, b, mar, mbr);
    }

    /**
     * Test construction that takes a two-dimensional array of shorts that the
     * cache will be initialized to. This is to enable quick testing of replacement
     * and write logic without having to wait for the cache to fill up.
     *
     * @param s   Int with size of memory array in number of 16-bit words
     * @param c   Int with size of cache in 16-bit words
     * @param b   Int with number words in a memory block
     * @param mar Register object to use as Memory Address Register (MAR)
     * @param mbr Register object to use as Memory Buffer Register (MBR)
     * @param data Two-dimensional array of shorts to initialize the cache with
     *             NOTE: Each row i data must have the correct number of words for
     *                   a cache block, but data can have fewer rows in case we
     *                   want to leave some cache lines empty
     * @param tags An array of shorts with the tag for each row in data
     *             NOTE: If null or shorter than the number of row in the data,
     *                   the default tag is the row number
     *
     * @throws IOException If an invalid memory size is specified
     */
    public Cache(int s, int c, int b, Register mar, Register mbr, short[][] data, Short tags[]) throws IOException {
        /* Call superclass constructor to initialize memory */
        super(s, mar, mbr);

        System.out.println("[Cache::Cache] Initializing cache with seed data...");

        /* Call method to initialize the cache */
        initializeCache(s, c, b, mar, mbr);

        System.out.printf("[Cache::Cache] Seeding cache with %d lines of test data.", data.length);
        /* Initialize storage array in cache */
        for(int i = 0 ; i < this.cacheSize && i < data.length; i++){
            Short tag;
            if (tags == null || i >= tags.length) {
                tag = (short) i;
            } else {
                tag = tags[i];
            }
            cache.put((Short) tag, data[i]);
            tagList[i] = (short) tag;
        }
    }

    /**
     * This method gets the cache tag (I.e. address minus byte offset field) for a memory address
     * @param address Short containing the memory address
     * @return a short with the cache tag corresponding to the address
     */
    private short getTag(short address) {
        System.out.printf("[Cache::getTag] Getting tag for address: %s\n"
                , Integer.toBinaryString((int)(0xffff & address)));

        /* Shift address by length of byte offset field to get tag */
        short tag = (short) (address >>> byteOffset);
        System.out.printf("[Cache::getTag] Tag after shift by %s: %s\n"
                , byteOffset, Integer.toBinaryString((int)(0xffff & tag)));

        return tag;
    }

    private short getByteOffset(short address) {
        System.out.printf("[Cache::getByteOffset] Getting by offset for address: %s\n",
                Integer.toBinaryString((int)address));

        /* Mask off all address with offset mask */
        short offset = (short) (address & offsetMask);
        System.out.format("[Cache::getByteOffset] Byte offset is %s\n",
                Integer.toBinaryString((int) offset));

        return offset;
    }

    /**
     * This method gets a block in memory so we can cache it
     *
     * @param blockId A short with the tag of the block to get
     *
     * @return Short array with block in memory
     */
    private short [] getMemoryBlock(short blockId) {
        System.out.printf("[Cache::getMemoryBlock] Getting memory block with id: %s\n",
                Integer.toBinaryString((int) blockId));
        /* Shift tag by byteOffset to get calculate starting address of block */
        short startAddress = (short) (blockId << byteOffset);
        System.out.printf("[Cache::getMemoryBlock] Memory Block starting address is: %s\n",
                Integer.toBinaryString((int) startAddress));

        /* Add max block id - 1 to starting address to get ending address */
        short endAddress = (short) (startAddress + (Math.pow(2, byteOffset)));
        System.out.printf("[Cache::getMemoryBlock] Memory Block ending address is: %s\n",
                Integer.toBinaryString((int) endAddress));

        return Arrays.copyOfRange(super.data, startAddress, endAddress);
    }

    /**
     * This method saves a block of memory into the Cache after a miss
     *
     * @param tag
     */
    private void saveToCache(short tag) {
        int index;

        /* Check if cache is full and do replacement processing */
        int currentSize = cache.size();
        if (currentSize == this.cacheSize) {
            System.out.println("[Cache::saveToCache] Cache is full! Implementing replacement logic.");

            /* Get a random line number to replace */
            index = (int) (Math.random() * (this.cacheSize - 1));
            System.out.printf("[Cache::saveToCache] Random replacement of line #%d with tag %s.\n"
                    , index + 1, Integer.toBinaryString((int) tagList[index]));

            /* Get tag for that line from tag list and remove it from cache */
            cache.remove(tagList[index]);
        } else {
            /* If cache is not full set the index corresponding to the current size of the cache */
            index = currentSize;
            System.out.printf("[Cache::saveToCache] Cache is not full; Setting tag index to %d",index);
        }
        /* Get block corresponding to current tag from memory */
        short[] line = getMemoryBlock(tag);

        /* Save memory block to cache */
        cache.put((Short) tag, line);

        /* Save tag to tag list for random replacement as needed */
        tagList[index] = tag;
    }

    /**
     * This method overrides the main memory's read() method to look in cache before trying to
     * read from main memory
     *
     * @throws IOException Whenever an IOExpection is thrown by the superclass read()
     */
    @Override
    public void read() throws IOException {
        /* Get address from MAR */
        short address = (short) mar.read();

        /* Get tag so we can search if line is cached */
        short tag = getTag(address);

        /* Check for a line with the appropriate tag in cache */
        short[] line = cache.get(tag);
        if (line != null) {
            System.out.printf("[Cache::read] Cache hit for address %d!\n", address);

            /* If data is found in cache, get byte offset */
            short offset = getByteOffset(address);

            /* Get appropriate word from line according to offset */
            short word = line[offset];
            System.out.printf("[Cache::read] Retrieved word from cache line: %s\n",
                    Integer.toBinaryString((int) word));

            /* Copy word to MBR */
            mbr.load(word);
        } else {
            System.out.printf("[Cache::read] Cache miss for address %s.\n", address);

            /* Call superclass read to get data from memory if not in cache */
            super.read();

            /* Save the block to the cache for future reads */
            saveToCache(tag);
        }
    }

    /**
     * This method overrides the main memory's write() method to look in cache before trying to
     * write into main memory
     *
     * @throws IOException Whenever an IOExpection is thrown by the superclass write()
     */
    @Override
    public void write() throws IOException {
        /* Get address from MAR */
        short address = (short) mar.read();

        /* Get tag so we can search if line is cached */
        short tag = getTag(address);

        /* Check for a line with the appropriate tag in cache */
        short[] line = cache.get(tag);
        if (line != null) {
            System.out.printf("[Cache::write] Cache hit for address %d!\n", address);

            /* If data is found in cache, get byte offset */
            short offset = getByteOffset(address);

            /* read the word from MBR and write into the cache */
            line[offset]=(short) mbr.read();
            System.out.printf("[Cache::write] successfully write into cache: %d\n", line[offset]);
        } else {
            System.out.printf("[Cache::write] Cache miss for address %s.\n", address);

            /* Do nothing (not write into cache) */
        }

        /* Write into memory no matter if it is in cache */
        super.write();
    }

    /**
     * This method prints out a line of the cache for test and debugging
     *
     * @param n int with line number to display
     */
    public void printCacheLine(Short n) {

        // |--------|------|-------------|
        // |   TAG  | WORD |    VALUE    |
        // |--------|------|-------------|
        System.out.printf("\n\n       CACHE LINE %d DUMP         \n", n+1);
        System.out.println("|---------|--------|-------------|");
        System.out.println("|   TAG   |  WORD  |    VALUE    |");
        System.out.println("|---------|--------|-------------|");

        /* Get the tag for the line */
        Short tag = tagList[n];

        /* Get the line from the cache */
        short[] line = cache.get(tag);

        /* Iterate through line and print values */
        for (int i = 0; line != null & i < this.cacheSize; i++) {
            System.out.printf("  %s     %s     %s\n",
                    String.format("0x%05X", (int) tag),
                    String.format("0x%01X", (int) i),
                    String.format("0x%06X", line[i]));
        }
        System.out.println("|---------|--------|-------------|");
    }

    /**
     * This method gets a line in the cache and returns an array of shorts where the
     * first element (I.e. index 0) is the tag and the rest of the elements are the values
     * in the corresponding line of the cache.
     *
     * @param n     Short with line number to get
     *
     * @return  An array of shorts where the first element is the tag and the rest are
     *          the values in the cache line
     */
    public short[] getCacheLine(Short n) throws NullPointerException{
        short[] output = new short[this.blockSize + 1];

        /* Get tag for corresponding line number */
        Short tag = tagList[n];

        /* Save tag to first element in the array */
        if(tag != null) {
            output[0] = (short) tag;
        } else {
            throw new NullPointerException("Tag is null");
        }

        /* Get line corresponding to tag */
        short[] line = cache.get(tag);

        /* Copy each word in line to output array */
        for (int i = 1; i <= this.blockSize; i++) {
            output[i] = line[i - 1];
        }

        /* Return output array */
        return output;
    }

    public int getCacheSize(){
        return cacheSize;
    }
}