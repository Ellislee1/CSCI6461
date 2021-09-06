/**
 * This file implements a simple test controller to that implements a main
 * function to instantiate and test the main controller. I built this as a 
 * standalone Java console application in NetBeans to test the backend classes 
 * since I could not get the JavaFX GUI to execute on any IDE. Eventually this 
 * should be replaced by the GUI controller.
 */
package com.csci6461;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author imanuelportalatin
 */
public class TestController {
    public static void main(String[] args) {
        System.out.println("Starting test controller");
        
        /* Create new control unit */
        ControlUnit cu = new ControlUnit();
        
        /* Test getting Program Counter (PC) properties */
        String pcName = cu.pc.getName();
        int pcSize = cu.pc.getSize();
        
        System.out.printf("PC register properties: name = %s, size = %d\n", 
                pcName, pcSize);
        
        /* Test loading a alternating zeros and ones into the pc register */
        byte[] value = new byte[]{ (byte) 0xff, (byte) 0xAA, (byte) 0x00 };
        System.out.printf("Loading value into pc: %s\n", Arrays.toString(value));
        
        try {
            cu.pc.load(value);
        } catch(IOException ioe) {
            System.out.println("Execption while loading test word into pc...");
            ioe.printStackTrace();
        }
        
        /* Test reading the pc register after load */
        byte [] outValue = cu.pc.read();
        
        int [] result = new int [2];
        result[0] = outValue[0] & 0xff;
        result[1] = outValue[1] & 0xff;
        System.out.printf("Read data from pc: %s %s\n",
                Integer.toBinaryString(result[1]),
                Integer.toBinaryString(result[0]));
        
        /* Test overflow protection on the pc register */
        value[2] = (byte) 0x01;
        
        try {
            cu.pc.load(value);
        } catch(IOException ioe) {
            System.out.println("Execption while loading test word into pc...");
            ioe.printStackTrace();
        }
        
        System.out.println("Finishing test controller execution");
    }
}