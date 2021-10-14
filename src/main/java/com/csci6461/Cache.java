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
     * Define constants for total cache size in memory blocks
     * NOTE: The size of the cache must be divisible by the size of the memory.
     *       Also, the size of the blocks is assumed to be equal to the cache size
     *       since this seems to work out for values divisible by memory size.
     *              For example: 2048 / 16 = 128 -> 128 * 16 = 2048
     *       The number of bits in the byte offset field is then:
     *                              log^2(16) = 4
     */
    private static final int CACHE_SIZE = 16;
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
     * The cache class constructor creates a new two-dimensional data storage array
     * of the configured size for the cache storage and calls the superclass constructor.
     * to initialize the memory
     *
     * @param s   Size of memory array in number of 16-bit words
     * @param mar Register object to use as Memory Address Register (MAR)
     * @param mbr Register object to use as Memory Buffer Register (MBR)
     * @throws IOException If an invalid memory size is specified
     */
    public Cache(int s, Register mar, Register mbr) throws IOException {
        /* Call superclass constructor to initialize memory */
        super(s, mar, mbr);

        /* Check to make sure Memory size if divisible by cache size */
        if (s % CACHE_SIZE != 0) {
            String error = String.format(
                    "Invalid memory size: %d; Memory must be divisible by cache size of %d.",
                    s, CACHE_SIZE);
            throw new IOException(error);
        }

        /* Create cache storage array according to configured size */
        cache = new HashMap<Short, short[]>(CACHE_SIZE);
        System.out.printf("[Cache::Cache] Created Cache storage is size: %d\n", CACHE_SIZE);

        /* Calculate byteOffset field size from block size (which is equal to cache size) */
        byteOffset = (int) (Math.log(CACHE_SIZE) / Math.log(2));
        System.out.printf("[Cache::Cache] Size of byte offset field is: %d\n", byteOffset);

        /* Get mask to extract byte offset field by successive shift */
        for (int i = 0; i < byteOffset; i++) {
            offsetMask = (short) (offsetMask + (0x0001 << i));
        }
        System.out.printf("[Cache::Cache] Offset mask is: %s\n", Integer.toBinaryString((int)offsetMask));

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
                , CACHE_SIZE, Integer.toBinaryString((int)(0xffff & tag)));

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
        short endAddress = (short) (startAddress + (Math.pow(2, byteOffset) - 1));
        System.out.printf("[Cache::getMemoryBlock] Memory Block ending address is: %s\n",
                Integer.toBinaryString((int) endAddress));

        return Arrays.copyOfRange(super.data, startAddress, endAddress);
    }

    /**
     * This method overrides the main memory's read() method to look in cache before trying to
     * read from main memory
     *
     * @throws IOException Whenever an IOExpection is throw by the superclass read()
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

            /* Get block corresponding to current tag from memory */
            line = getMemoryBlock(tag);

            /* Save memory block to cache */
            cache.put((Short) tag, line);
        }
    }
}
