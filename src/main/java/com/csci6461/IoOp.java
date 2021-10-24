package com.csci6461;

public class IoOp extends Instruction{

    /**
     * Instruction class constructor that sets the instruction's name
     *
     * @param name String containing the instruction name (I.e. HLT, LDR, ADD, etc.)
     */
    IoOp(String name) {
        super(name);
    }

    /**
     * Gets the arguments for the instruction
     * @return Returns the deconstructed arguments
     */
    @Override
    public int[] getArguments() {
        String bits = String.format("%16s", Integer.toBinaryString(this.getInstruction())).replace(' ', '0');
        bits = bits.substring(bits.length()-16);
        System.out.printf("[IoOp::getArguments] Full instruction %s\n",
                bits);

        String gpr = bits.substring(6,8);
        String device = bits.substring(11,16);

        int[] args = new int[2];
        args[0] = Integer.parseInt(gpr,2);
        args[1] = Integer.parseInt(device,2);

        return args;
    }
}
