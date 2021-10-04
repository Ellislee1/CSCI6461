package com.csci6461;

public class MathMR extends Instruction{
    /**
     * Constructor for MiscInstruction class that calls abstract class constructor
     *
     * @param name String containing instruction's name
     */
    MathMR(String name){
        super(name);
    }

    /**
     * Breaks down the instruction getting the relevant arguments
     * @return Returns an array of arguments
     */
    @Override
    int[] getArguments() {
        String bits = String.format("%16s", Integer.toBinaryString(super.getInstruction())).replace(' ', '0');
        bits = bits.substring(bits.length()-16);
        System.out.printf("[MemOp::getArguments] Full instruction %s\n",
                bits);

        String gpr = bits.substring(6,8);
        String ixr = bits.substring(8,10);
        String i = bits.substring(10,11);
        String address = bits.substring(11,16);

        /* Save trap code to args array */
        int[] args = new int[4];

        args[0] = Integer.parseInt(gpr,2);
        args[1] = Integer.parseInt(ixr,2);
        args[2] = Integer.parseInt(i,2);
        args[3] = Integer.parseInt(address,2);

        /* Return args */
        return args;
    }
}
