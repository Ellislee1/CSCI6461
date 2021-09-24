/**
 * This file implements a simple test controller to that implements a main
 * function to instantiate and test the main controller. I built this as a
 * standalone Java console application in NetBeans to test the backend classes
 * since I could not get the JavaFX GUI to execute on any IDE. Eventually this
 * should be replaced by the GUI controller.
 */
package com.csci6461;

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
        ControlUnit cu = new ControlUnit();

        /* Test case 1: Execute TRAP */
        System.out.println("\n\nTest Case 1: Execute TRAP.\n");
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x07);
        buffer.put((byte) 0x78);  /* TRAP */
        ByteBuffer aBuffer = ByteBuffer.allocate(2);
        aBuffer.order(ByteOrder.LITTLE_ENDIAN);
        aBuffer.put((byte) 0x10);
        aBuffer.put((byte) 0x04);
        short testInstruction = buffer.getShort(0);
        short testAddress = aBuffer.getShort(0);

        /* Put test value on memory */
//        cu.load(6, (short) 0xffff);
        cu.writeDataToMemory(6, (short) 0xffff);

        System.out.printf("Instruction address set to: %d\n",
                testAddress);

        boolean[] address = get_bool_array(Integer.toBinaryString((int) testAddress));

//        cu.load(1040, testInstruction);
        cu.writeDataToMemory(1040, testInstruction);
        try {
            cu.pc.load(address);
        } catch (IOException ioe) {
            System.out.println("Exception while loading test address into pc...");
            ioe.printStackTrace();
        }

        /* Write memory address 6 to slot 1 for machine fault */
        short fault = 6;
//        cu.load(1, fault);
        cu.writeDataToMemory(1, fault);

        /* Call singleStep on CU to execute test instruction */
        try {
            cu.singleStep();
        } catch (IOException ioe) {
            System.out.println("Exception during single step execution...");
            ioe.printStackTrace();
        }

        /* Check IR after single step */
        System.out.printf("The IR is: %s\n", Arrays.toString(cu.ir.getSetBits()));

        /* Test Case 2: Load memory to register 0 without indirection or indexing */
        System.out.println("\n\nTest Case 2: Load memory to register 0 without indirection or indexing.\n");
        buffer.clear();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x07);  /* Set Address Field to 7 */
        buffer.put((byte) 0x04);  /* LDR Opcode = 1 + R = 00 */
        aBuffer.clear();
        aBuffer.order(ByteOrder.LITTLE_ENDIAN);
        aBuffer.put((byte) 0x10);
        aBuffer.put((byte) 0x04);
        testInstruction = buffer.getShort(0);
        testAddress = aBuffer.getShort(0);

        /* Put test value on memory */
//        cu.load(7, (short) 0xffff);
        cu.writeDataToMemory(7, (short) 0xffff);

        System.out.printf("Instruction address set to: %d\n",
                testAddress);

        address = get_bool_array(Integer.toBinaryString((int) testAddress));

//        cu.load(1040, testInstruction);
        cu.writeDataToMemory(1040, testInstruction);
        try {
            cu.pc.load(address);
        } catch (IOException ioe) {
            System.out.println("Exception while loading test address into pc...");
            ioe.printStackTrace();
        }

        /* Call singleStep on CU to execute test instruction */
//        try {
//            cu.singleStep();
//        } catch (IOException ioe) {
//            System.out.println("Exception during single step execution...");
//            ioe.printStackTrace();
//        }

        /* Read register 0 to make sure value was copied */
        System.out.printf("Value of GPR 0 after LDA #1: %s", Arrays.toString(cu.gpr[0].getSetBits()));

        /* Test Case 3: Load memory to register 1 without indirection but with indexing */
        System.out.println("\n\nTest Case 3: Load memory to register 1 without indirection but with indexing.\n");
        buffer.clear();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x47);  /* Set Address Field to 7 with IX = 1   */
        buffer.put((byte) 0x05);  /* Opcode for LDR = 1 + R = 01 */
        aBuffer.clear();
        aBuffer.order(ByteOrder.LITTLE_ENDIAN);
        aBuffer.put((byte) 0x10);
        aBuffer.put((byte) 0x04);
        testInstruction = buffer.getShort(0);
        testAddress = aBuffer.getShort(0);

        /* Put test value on memory */
        /* Save to address 8 since address = 7 and IXR1 = 1 */
//        cu.load(8, (short) 0xffff);
        cu.writeDataToMemory(8, (short) 0xffff);

        System.out.printf("Instruction address set to: %d\n",
                testAddress);

        address = get_bool_array(Integer.toBinaryString((int) testAddress));

//        cu.load(1040, testInstruction);
//        cu.writeDataToMemory(1040, testInstruction);
        cu.writeDataToMemory(1041, testInstruction);
        try {
            cu.pc.load(address);
        } catch (IOException ioe) {
            System.out.println("Exception while loading test address into pc...");
            ioe.printStackTrace();
        }

        /* Save value 1 to IXR1 */
        ByteBuffer ixBuffer = ByteBuffer.allocate(2);
        ixBuffer.order(ByteOrder.LITTLE_ENDIAN);
        ixBuffer.put((byte) 0x01);  /* Value of 1 */
        ixBuffer.put((byte) 0x00);
        short ixValue = ixBuffer.getShort(0);

        boolean[] ixArray = get_bool_array(Integer.toBinaryString((int) ixValue));

        try {
            cu.ixr[0].load(ixArray);
        } catch (IOException ioe) {
            System.out.println("Exception while loading data into IXR...");
            ioe.printStackTrace();
        }
        System.out.printf("IXR1 is set to: %d\n", cu.ixr[0].read());

        /* Call singleStep on CU to execute test instruction */
//        try {
//            cu.singleStep();
//        } catch (IOException ioe) {
//            System.out.println("Exception during single step execution...");
//            ioe.printStackTrace();
//        }

        /* Read register 1 to make sure value was copied */
        System.out.printf("Value of GPR 1 after LDA #2: %s", Arrays.toString(cu.gpr[1].getSetBits()));

        /* Test Case 4: Load memory to register 2 with indirection but without indexing */
        System.out.println("\n\nLoad memory to register 2 with indirection but without indexing.\n");
        buffer.clear();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x27);  /* Set Address Field to 7 and I to 1 and IX to 0 */
        buffer.put((byte) 0x06);  /* LDR opcode = 1 + R = 10 */
        aBuffer.clear();
        aBuffer.order(ByteOrder.LITTLE_ENDIAN);
        aBuffer.put((byte) 0x10);
        aBuffer.put((byte) 0x04);
        testInstruction = buffer.getShort(0);
        testAddress = aBuffer.getShort(0);

        /* Put test value on memory */
//        cu.load(9, (short) 0xffff);
        cu.writeDataToMemory(9, (short) 0xffff);

        /* Copy indirect address to memory 7 */
//        cu.load(7, (short) 0x0009);
        cu.writeDataToMemory(7, (short) 0x0009);

        System.out.printf("Instruction address set to: %d\n",
                testAddress);

        address = get_bool_array(Integer.toBinaryString((int) testAddress));

//        cu.load(1040, testInstruction);
//        cu.writeDataToMemory(1040, testInstruction);
        cu.writeDataToMemory(1042, testInstruction);
        try {
            cu.pc.load(address);
        } catch (IOException ioe) {
            System.out.println("Exception while loading test address into pc...");
            ioe.printStackTrace();
        }

        /* Call singleStep on CU to execute test instruction */
//        try {
//            cu.singleStep();
//        } catch (IOException ioe) {
//            System.out.println("Exception during single step execution...");
//            ioe.printStackTrace();
//        }

        /* Read register 2 to make sure value was copied */
        System.out.printf("Value of GPR 2 after LDA #3: %s", Arrays.toString(cu.gpr[2].getSetBits()));

        /* Test Case 5: Load memory to register 3 with indirection AND indexing */
        System.out.println("\n\nTest Case 3: Load memory to register 3 with indirection AND indexing.\n");
        buffer.clear();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0xA7);  /* Set Address Field to 7 with IX = 2, I = 1 */
        buffer.put((byte) 0x07);  /* LDR Opcode = 1 + R = 11 */
        aBuffer.clear();
        aBuffer.order(ByteOrder.LITTLE_ENDIAN);
        aBuffer.put((byte) 0x13);
        aBuffer.put((byte) 0x04);
        testInstruction = buffer.getShort(0);
        testAddress = aBuffer.getShort(0);

        /* Put test value on memory */
        /* Save to address 11 since address 7 has value of 9 and IXR2 will be set to 2 */
//        cu.load(11, (short) 0xffff);
        cu.writeDataToMemory(11, (short) 0xffff);

        System.out.printf("Instruction address set to: %d\n",
                testAddress);

        address = get_bool_array(Integer.toBinaryString((int) testAddress));

//        cu.load(1040, testInstruction);
//        cu.writeDataToMemory(1040,testInstruction);
        cu.writeDataToMemory(1043, testInstruction);
        cu.writeDataToMemory(1044, (short) 0x0000);

        try {
            cu.pc.load(address);
        } catch (IOException ioe) {
            System.out.println("Exception while loading test address into pc...");
            ioe.printStackTrace();
        }

        /* Save value 2 to IXR1 */
        ixBuffer.clear();
        ixBuffer.order(ByteOrder.LITTLE_ENDIAN);
        ixBuffer.put((byte) 0x02);  /* Value of 1 */
        ixBuffer.put((byte) 0x00);
        ixValue = ixBuffer.getShort(0);

        ixArray = get_bool_array(Integer.toBinaryString((int) ixValue));

        try {
            cu.ixr[1].load(ixArray);
        } catch (IOException ioe) {
            System.out.println("Exception while loading data into IXR...");
            ioe.printStackTrace();
        }
        System.out.printf("IXR2 is set to: %d\n", cu.ixr[1].read());

        /* Call singleStep on CU to execute test instruction */
//        try {
////            cu.singleStep();
//            cu.run();
//        } catch (InterruptedException ioe) {
//            System.out.println("Exception during single step execution...");
//            ioe.printStackTrace();
//        }
//
//        /* Read register 0 to make sure value was copied */
//        System.out.printf("Value of GPR 3 after LDA #2: %s",Arrays.toString(cu.gpr[3].getSetBits()));
//    }
    }
}
