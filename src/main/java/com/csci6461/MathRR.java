package com.csci6461;

public class MathRR extends Instruction {
    MathRR(String name) {super(name);}

    @Override
    int[] getArguments() {
        String bits = String.format("%16s", Integer.toBinaryString(this.getInstruction())).replace(' ', '0');
        bits = bits.substring(bits.length() - 16);
        System.out.printf("[Logic::getArguments] Full instruction %s\n",
                bits);
        String rx = bits.substring(6, 8);
        String ry = bits.substring(8, 10);

        System.out.printf("[Logic::getArguments] Extracted arguments: %s, %s\n", rx, ry);

        int[] args = new int[2];

        args[0] = (short)Integer.parseInt(rx,2);
        args[1] = (short)Integer.parseInt(ry,2);

        return args;
    }
}