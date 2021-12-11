/**
 * This file implements a simple test controller to that implements a main
 * function to instantiate and test the main controller. I built this as a
 * standalone Java console application in NetBeans to test the backend classes
 * since I could not get the JavaFX GUI to execute on any IDE. Eventually this
 * should be replaced by the GUI controller.
 */
package com.tests;

import com.csci6461.Cache;
import com.csci6461.ControlUnit;
import com.csci6461.Register;

import java.io.IOException;

/**
 *
 * @author imanuelportalatin
 */
public class CacheTest {

    /* Define constants for cache size and number of words in memory block */
    private static final int CACHE_SIZE = 16;
    private static final int BLOCK_SIZE = 16;

    private static final int MEMORY_SIZE = 2048;     /* Size of main memory */

    public static boolean[] get_bool_array(String binaryString) {

        char[] binary = binaryString.toCharArray(); // Convert to character array
        boolean[] data = new boolean[binary.length]; // Create a new boolean array

        // Loop through array and flip bits where a 1 is present
        for (int x = 0; x < binary.length; x++) {
            if (binary[x] == '1') {
                data[x] = true;
            }
        }

        return data;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Starting Cache test...");

        /* Create a bunch of data to fill the cache */
        short[][] data = new short[CACHE_SIZE - 1][CACHE_SIZE];
        Short[] tags = new Short[CACHE_SIZE - 1];
        for (int i = 0; i < CACHE_SIZE - 1; i++) {
            /* Start at tag 100 (E.g. memory address 0x64 = 100 tag + 0000 word offset */
            tags[i] = (short)(i + 4);
            for (int j = 0; j < CACHE_SIZE; j++) {
                data[i][j] = (short) (i + j + 1);
            }
        }

        /* Create memory registers */
        Register mar = new Register("mar", 12);
        Register mbr = new Register("mbr", 16);

        /* Create a new cache and initialize to data */
        Cache cache = new Cache(MEMORY_SIZE, CACHE_SIZE, BLOCK_SIZE, mar, mbr, data, tags);

        /* Print cache lines to verify test data seed */
        for (Short i = 0; i < CACHE_SIZE; i++) {
            cache.printCacheLine(i);
        }

        /* Test #1: Cache write miss - Copy something to a location in memory NOT in Cache */
        /*                             This should result on new miss and new block being written to memory */
        System.out.println("\n\nTest #1: Cache write miss\n");

        /* Set the MAR to Block 0x16 and byte 0x8, which is in the middle of the next unseeded block */
        mar.load((short) (0x0168 & 0xffff));

        /* Set the MBR to alternating 1's and 0's */
        mbr.load((short) (0xAAAA & 0xffff));

        /* Do a memory load, which should result in Cache miss and write-through to memory but not cache */
        cache.write();

        /* Print the memory and last cache line to verify outcome */
        cache.printMemory();
        cache.printCacheLine((short) (CACHE_SIZE - 1));

        /* Test #2: Cache read miss - Try to read memory address from Test #1 */
        /*                            This should result on read miss and new line being added end of cache */
        System.out.println("\n\nTest #1: Cache read miss\n");

        /* Try to read the address we copied to MAR on test #1 */
        cache.read();

        /* Print the last line of the cache to verify new line was added */
        cache.printCacheLine((short) (CACHE_SIZE - 1));

        /* Read the MBR and make sure it is the value we put in there */
        short value = mbr.read();
        if (value != (short) (0xAAAA & 0xffff)) {
            System.out.printf("\n\nERROR: Expected MBR to have %s but got %s\n",
                    Integer.toBinaryString(0xAAAA & 0xffff),
                    Integer.toBinaryString(value));
            /* Exit with error status */
            System.exit(1);
        }

        /* Test #3: Cache write hit - Try to write to an address in the same block as Test #2 */
        /*                            This should result on the data being written to BOTH memory and cache line 16 */
        System.out.println("\n\nTest #3: Cache write hit\n");

        /* Set the MAR to Block 0x16 and byte 0x9, which is the next byte after the one loaded to cache above */
        /* NOTE: MBR should still have alternating 1's and 0's, so that will be loaded to BOTH memory and cache */
        mar.load((short) (0x0169 & 0xffff));

        /* Write word to memory */
        cache.write();

        /* Print memory contents to verify */
        cache.printMemory();

        /* Reset MBR and do a memory read to get value in memory */
        /* NOTE: This should result in cache hit! */
        mbr.load((short) (0x0000 & 0xffff));
        cache.read();

        /* Get value loaded into MBR and verify that intended value was written through to memory */
        value = mbr.read();
        if (value != (short) (0xAAAA & 0xffff)) {
            System.out.printf("\n\nERROR: Expected MBR to have %s but got %s\n",
                    Integer.toBinaryString(0xAAAA & 0xffff),
                    Integer.toBinaryString(value));
            /* Exit with error status */
            System.exit(1);
        }

        /* Print last line of the cache for verification */
        cache.printCacheLine((short)(CACHE_SIZE - 1));

        /* Get last line in cache and verify value was also updated in cache */
        short[] line = cache.getCacheLine((short)(CACHE_SIZE - 1));

        System.out.printf("Retrieved data for line 16 of cache with tag %s:\n",
                Integer.toBinaryString(line[0] & 0xffff));
        System.out.printf("Word 9 in line is %s\n", Integer.toBinaryString(line[9 + 1] & 0xffff));

        if ((line[9 + 1]) != (short)(0xAAAA & 0xffff)) {
            System.out.printf("\n\nERROR: Expected word 9 of cache line 16 to have %s but got %s\n",
                    Integer.toBinaryString(0xAAAA & 0xffff),
                    Integer.toBinaryString(line[9 + 1] & 0xffff));
            /* Exit with error status */
            System.exit(1);
        }

        /* Test #4: Cache replacement - Try to read a line not already in cache */
        /*                              This should in a cache miss and a random line being replaced */
        System.out.println("\n\nTest #4: Cache replacement\n");

        /* Set the MAR to Block 0x19 and byte 0x4, which should not be loaded cache yet */
        mar.load((short) (0x0194 & 0xffff));

        /* Do a memory read to trigger miss with replacement */
        cache.read();

        /* Iterate through cache lines until you find replaced line */
        int i;
        for (i = 0; i < CACHE_SIZE; i++) {
            line = cache.getCacheLine((short) i);

//            System.out.printf("Tag for cache line #%d is %s\n",
//                    i + 1, Integer.toBinaryString((int) (line[0]) & 0xffff));
            if ((line[0] & 0xffff) == (short)(0x0019 & 0xffff)) {
                System.out.printf("Cache line #%d was replaced!", i + 1);
                break;
            }
        }

        if (i == CACHE_SIZE) {
            System.out.println("ERROR: No cache was replaced after miss with full cache.");
            System.exit(1);
        }
    }
}
