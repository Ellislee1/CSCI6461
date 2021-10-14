package com.csci6461;

/**
 * This class will eventually implement the instruction decoder
 */
public class InstructionDecoder {
    /**
     * Parameter to hold decoder config
     */
    private final DecoderConfig config;

    /**
     * Default constructor
     */
    public InstructionDecoder() {
        /* Instantiate decoder config */
        this.config = new DecoderConfig();
    }

    /**
     * Helper method to extract a 16-bit String representation of a word in memory
     *
     * @param word Short containing the word of data to be translated
     *
     * @return A string containing the requested bits or the whole
     *         binary word (up to 16 bits) if index parameters are null
     */
    public String getBits(final short word) {
        final String s = Integer.toBinaryString(word);
        final String bits = String.format("%16s", s).replace(' ', '0');

        return bits.substring(bits.length()-16);
    }

    /**
     * Method to extract an instruction's Opcode
     *
     * @param word Short containing the un-decoded instruction
     *
     * @return Int with the instruction's Opcode
     */
    public int getOpCode(final short word) {
        /* First convert the input short into a 16-bit binary String representation */
        final String bits = this.getBits(word);

        /* Extract the top 6-bits of the binary Strings */
        final String opcode = bits.substring(0,6);
        System.out.printf("Extracted Opcode: Instruction = %s, Opcode = %s\n",
                bits, opcode);

        /* Return decimal representation of binary string */
        return Integer.parseInt(opcode,2);
    }

    /**
     * Method to decode instruction
     * @param word The instruction word to decode
     */
    public Instruction decode(final short word) {
        /* First, extract Opcode from instructions we know how to process */
        final int opcode = this.getOpCode(word);
        System.out.printf("Extracted opcode: %s\n", opcode);

        /* Look-up instruction by Opcode on decoder config */
        final Instruction instruction = this.config.getInstruction(opcode);
        if (instruction != null) {
            System.out.printf("[InstructionDecoder::decode] Have the param decoder class for Opcode %s: %s\n",
                    Integer.toOctalString(opcode), instruction.getName());

            /* Save the un-decoded word back into the instruction so we can get the arguments later */
            instruction.setInstruction(word);

            /* Return decoded instruction to Control Unit for further processing */
            return instruction;
        } else {
            System.out.printf("[InstructionDecoder::decode] Error: Instruction with Opcode %s not found in config!\n",
                    Integer.toOctalString(opcode));
            return null;
        }
    }
}
