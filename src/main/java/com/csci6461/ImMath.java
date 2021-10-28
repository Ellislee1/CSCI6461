package com.csci6461;

public class ImMath extends Instruction{

    /**
     * Instruction class constructor that sets the instruction's name
     *
     * @param name String containing the instruction name (I.e. HLT, LDR, ADD, etc.)
     */
    ImMath(String name) {
        super(name);
    }

    @Override
    int[] getArguments() {
        String bits = String.format("%16s", Integer.toBinaryString(this.getInstruction())).replace(' ', '0');
        bits = bits.substring(bits.length()-16);
        System.out.printf("[ImMath::getArguments] Full instruction %s\n",
                bits);

        String gpr = bits.substring(6,8);
        String ixr = bits.substring(8,16);

        /* Save trap code to args array */
        int[] args = new int[2];

        args[0] = (short)Integer.parseInt(gpr,2);
        args[1] = (short)Integer.parseInt(ixr,2);

        /* Return args */
        return args;
    }
}
