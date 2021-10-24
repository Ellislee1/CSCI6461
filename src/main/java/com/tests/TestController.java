/**
 * This file implements a simple test controller to that implements a main
 * function to instantiate and test the main controller. I built this as a
 * standalone Java console application in NetBeans to test the backend classes
 * since I could not get the JavaFX GUI to execute on any IDE. Eventually this
 * should be replaced by the GUI controller.
 */
package com.tests;

import com.csci6461.ControlUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

/**
 *
 * @author imanuelportalatin
 */
public class TestController {

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
        System.out.println("Starting test controller");

        /* Create new control unit */
        ControlUnit cu = new ControlUnit(null,null,null, null);

        /* Print cache line 0 */
//        cu.printCacheLine(0);
    }
}
