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

    /* Define constant for cache size */
    /* NOTE: This should match the size defined inside the Cache class! */
    private static final int CACHE_SIZE = 16;

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
        short data[][] = new short[CACHE_SIZE - 1][CACHE_SIZE];
        Short tags[] = new Short[CACHE_SIZE - 1];
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
        Cache cache = new Cache(MEMORY_SIZE, mar, mbr, data, tags);

        /* Print cache lines to verify test data seed */
//        for (Short i = 0; i < CACHE_SIZE; i++) {
//            cache.printCacheLine(i);
//        }

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
//        cache.printCacheLine((short) (CACHE_SIZE - 1));

        /* Test #2: Cache read miss - Try to read memory address from Test #1 */
        /*                             This should result on read miss and new line being added end of cache */
        System.out.println("\n\nTest #1: Cache write miss\n");

        /* Try to read the address we copied to MAR on test #1 */
        cache.read();

        /* Print the last line of the cache to verify new line was added */
        cache.printCacheLine((short) (CACHE_SIZE - 1));

        /* Read the MBR and make sure it is the value we put in there */
        short value = (short) mbr.read();
        if (value != (short) (0xAAAA & 0xffff)) {
            System.out.printf("\n\nERROR: Expected MBR to have %s but got %s\n",
                    Integer.toBinaryString((int)(0xAAAA & 0xffff)),
                    Integer.toBinaryString((int)value));
        }
    }
}
