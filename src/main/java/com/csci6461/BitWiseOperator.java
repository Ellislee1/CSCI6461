package com.csci6461;

public class BitWiseOperator extends Instruction{

    /**
     * Instruction class constructor that sets the instruction's name
     *
     * @param name String containing the instruction name (I.e. HLT, LDR, ADD, etc.)
     */
    BitWiseOperator(String name) {
        super(name);
    }

    @Override
    int[] getArguments() {
        String bits = String.format("%16s", Integer.toBinaryString(this.getInstruction())).replace(' ', '0');
        bits = bits.substring(bits.length()-16);
        System.out.printf("[Bitwise Operator::getArguments] Full instruction %s\n",
                bits);

        String gpr = bits.substring(6,8);
        String AL = bits.substring(8,9);
        String LR = bits.substring(9,10);
        String count = bits.substring(12,16);

        int[] args = new int[4];

        args[0] = Integer.parseInt(gpr,2);
        args[1] = Integer.parseInt(AL,2);
        args[2] = Integer.parseInt(LR,2);
        args[3] = Integer.parseInt(count,2);

        return args;
    }
}
