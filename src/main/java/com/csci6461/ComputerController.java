package com.csci6461;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Class for controlling elements of the UI.
 * @author Ellis Thompson
 */
public class ComputerController {

    /**
     * Control unit for the system
     */
    private final ControlUnit cu = new ControlUnit();

    /**
     * Toggle buttons for the address
     */
    @FXML
    private ToggleButton adr0, adr1, adr2, adr3, adr4;
    /**
     * Toggle buttons for the indirect address
     */
    @FXML
    private ToggleButton i5;
    /**
     * Toggle buttons for the index register
     */
    @FXML
    private ToggleButton ixr6, ixr7;
    /**
     * toggle buttons for the general purpose register
     */
    @FXML
    private ToggleButton gpr8, gpr9;
    /**
     * Toggle buttons for the command
     */
    @FXML
    private ToggleButton ctlA, ctlB, ctlC, ctlD, ctlE, ctlF;
    /**
     * Program counter checkboxes
     */
    @FXML
    private CheckBox pc1,pc2,pc4,pc8,pc16,pc32,pc64,pc128,pc256,pc512,pc1024,pc2048;
    /**
     * MAR check boxes
     */
    @FXML
    private CheckBox mar1,mar2,mar4,mar8,mar16,mar32,mar64,mar128,mar256,mar512,mar1024,mar2048;
    /**
     * MBR checkboxes
     */
    @FXML
    private CheckBox mbr1,mbr2,mbr4,mbr8,mbr16,mbr32,mbr64,mbr128,mbr256,mbr512,mbr1024,mbr2048,mbr4096,
            mbr8192,mbr16384,mbr32768;

    /**
     * GPR0 checkboxes
     */
    @FXML
    private CheckBox gpr10,gpr20,gpr40,gpr80,gpr160,gpr320,gpr640,gpr1280,gpr2560,gpr5120,gpr10240,gpr20480,gpr40960,
            gpr81920,gpr163840,gpr327680;
    /**
     * GPR1 checkboxes
     */
    @FXML
    private CheckBox gpr11,gpr21,gpr41,gpr81,gpr161,gpr321,gpr641,gpr1281,gpr2561,gpr5121,gpr10241,gpr20481,gpr40961,
            gpr81921,gpr163841,gpr327681;
    /**
     * GPR2 checkboxes
     */
    @FXML
    private CheckBox gpr12,gpr22,gpr42,gpr82,gpr162,gpr322,gpr642,gpr1282,gpr2562,gpr5122,gpr10242,gpr20482,gpr40962,
            gpr81922,gpr163842,gpr327682;
    /**
     * GPR3 checkboxes
     */
    @FXML
    private CheckBox gpr13,gpr23,gpr43,gpr83,gpr163,gpr323,gpr643,gpr1283,gpr2563,gpr5123,gpr10243,gpr20483,gpr40963,
            gpr81923,gpr163843,gpr327683;
    /**
     * IXR0 checkboxes
     */
    @FXML
    private CheckBox ixr0_1, ixr0_2, ixr0_3, ixr0_4, ixr0_5,ixr0_6,ixr0_7,ixr0_8,ixr0_9,ixr0_10,ixr0_11,ixr0_12,
            ixr0_13,ixr0_14,ixr0_15,ixr0_16;
    /**
     * IXR1 checkboxes
     */
    @FXML
    private CheckBox ixr1_1, ixr1_2, ixr1_3, ixr1_4, ixr1_5,ixr1_6,ixr1_7,ixr1_8,ixr1_9,ixr1_10,ixr1_11,ixr1_12,
            ixr1_13,ixr1_14,ixr1_15,ixr1_16;
    /**
     * IXR2 checkboxes
     */
    @FXML
    private CheckBox ixr2_1, ixr2_2, ixr2_3, ixr2_4, ixr2_5,ixr2_6,ixr2_7,ixr2_8,ixr2_9,ixr2_10,ixr2_11,ixr2_12,
            ixr2_13,ixr2_14,ixr2_15,ixr2_16;

//    @FXML
//    private CheckBox mfr_1,mfr_2,mfr_4,mfr_8;

    /**
     * Instruction register checkboxes
     */
    @FXML
    private CheckBox ir_1,ir_2,ir_3,ir_4,ir_5,ir_6,ir_7,ir_8,ir_9,ir_10,ir_11,ir_12,ir_13,ir_14,ir_15,ir_16;

    /**
     * Label to output the hex code
     */
    @FXML
    private Label lblCode;

    @FXML
    private CheckBox ch_under, ch_over, ch_div, ch_eq;

    /**
     * Array for the toggle buttons
     */
    private ToggleButton[] bitController;

    /**
     * Controllers for registers
     */
    private CheckBox[] pcController,marController,mbrController,gpr0Controller,gpr1Controller,gpr2Controller,
            gpr3Controller,ixr0Controller,ixr1Controller,ixr2Controller,irController;

    // private CheckBox[] mfrController;

    /**
     * Array of controllers
     */
    private CheckBox[][] gpr,ixr;

    /**
     * Initializer for the program.
     * This will set up all the controllers.
     */
    @FXML
    private void initialize() {
        this.bitController = new ToggleButton[]{this.adr0, this.adr1, this.adr2, this.adr3, this.adr4, this.i5, this.ixr6, this.ixr7, this.gpr8, this.gpr9, this.ctlA, this.ctlB,
                this.ctlC, this.ctlD, this.ctlE, this.ctlF};

        this.pcController = new CheckBox[]{this.pc1, this.pc2, this.pc4, this.pc8, this.pc16, this.pc32, this.pc64, this.pc128, this.pc256, this.pc512, this.pc1024, this.pc2048};
        this.marController = new CheckBox[]{this.mar1, this.mar2, this.mar4, this.mar8, this.mar16, this.mar32, this.mar64, this.mar128, this.mar256, this.mar512, this.mar1024, this.mar2048};
        this.mbrController = new CheckBox[]{this.mbr1, this.mbr2, this.mbr4, this.mbr8, this.mbr16, this.mbr32, this.mbr64, this.mbr128, this.mbr256, this.mbr512, this.mbr1024, this.mbr2048,
                this.mbr4096, this.mbr8192, this.mbr16384, this.mbr32768};

        this.gpr0Controller = new CheckBox[]{this.gpr10, this.gpr20, this.gpr40, this.gpr80, this.gpr160, this.gpr320, this.gpr640, this.gpr1280, this.gpr2560, this.gpr5120,
                this.gpr10240, this.gpr20480, this.gpr40960, this.gpr81920, this.gpr163840, this.gpr327680};
        this.gpr1Controller = new CheckBox[]{this.gpr11, this.gpr21, this.gpr41, this.gpr81, this.gpr161, this.gpr321, this.gpr641, this.gpr1281, this.gpr2561,
                this.gpr5121, this.gpr10241, this.gpr20481, this.gpr40961, this.gpr81921, this.gpr163841, this.gpr327681};
        this.gpr2Controller = new CheckBox[]{this.gpr12, this.gpr22, this.gpr42, this.gpr82, this.gpr162, this.gpr322, this.gpr642, this.gpr1282, this.gpr2562, this.gpr5122,
                this.gpr10242, this.gpr20482, this.gpr40962, this.gpr81922, this.gpr163842, this.gpr327682};
        this.gpr3Controller = new CheckBox[]{this.gpr13, this.gpr23, this.gpr43, this.gpr83, this.gpr163, this.gpr323, this.gpr643, this.gpr1283, this.gpr2563,
                this.gpr5123, this.gpr10243, this.gpr20483, this.gpr40963, this.gpr81923, this.gpr163843, this.gpr327683};

        this.ixr0Controller = new CheckBox[]{this.ixr0_1, this.ixr0_2, this.ixr0_3, this.ixr0_4, this.ixr0_5, this.ixr0_6, this.ixr0_7, this.ixr0_8, this.ixr0_9, this.ixr0_10,
                this.ixr0_11, this.ixr0_12,
                this.ixr0_13, this.ixr0_14, this.ixr0_15, this.ixr0_16};
        this.ixr1Controller = new CheckBox[]{this.ixr1_1, this.ixr1_2, this.ixr1_3, this.ixr1_4, this.ixr1_5, this.ixr1_6, this.ixr1_7, this.ixr1_8, this.ixr1_9, this.ixr1_10,
                this.ixr1_11, this.ixr1_12,
                this.ixr1_13, this.ixr1_14, this.ixr1_15, this.ixr1_16};
        this.ixr2Controller = new CheckBox[]{this.ixr2_1, this.ixr2_2, this.ixr2_3, this.ixr2_4, this.ixr2_5, this.ixr2_6, this.ixr2_7, this.ixr2_8, this.ixr2_9, this.ixr2_10,
                this.ixr2_11, this.ixr2_12,
                this.ixr2_13, this.ixr2_14, this.ixr2_15, this.ixr2_16};

        this.irController = new CheckBox[]{this.ir_1, this.ir_2, this.ir_3, this.ir_4, this.ir_5, this.ir_6, this.ir_7, this.ir_8, this.ir_9, this.ir_10, this.ir_11, this.ir_12, this.ir_13, this.ir_14,
                this.ir_15, this.ir_16};
        // mfrController = new CheckBox[]{mfr_1,mfr_2,mfr_4,mfr_8};

        this.gpr = new CheckBox[][]{this.gpr0Controller, this.gpr1Controller, this.gpr2Controller, this.gpr3Controller};
        this.ixr = new CheckBox[][]{this.ixr0Controller, this.ixr1Controller, this.ixr2Controller};

    }

    /**
     * Handles loading of bits for the program counter.
     */
    @FXML
    protected void onPCLoadClick() throws IOException {

        // Load the byte array into the controller and set UI bits
        this.cu.pc.load(this.translateBits(this.pcController));
    }

    /**
     * Handles loading of bits for the MAR.
     */
    @FXML
    protected void onMARLoadClick() throws IOException{
        this.cu.mar.load(this.translateBits(this.marController));
        this.cu.read_mem();

        this.setUIElem(this.cu.mbr, this.mbrController);
    }

    /**
     * Handles loading of bits for the MBR.
     */
    @FXML
    protected void onMBRLoadClick() throws IOException{
        this.cu.mbr.load(this.translateBits(this.mbrController));
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR0LoadClick() throws IOException{
        this.cu.gpr[0].load(this.translateBits(this.gpr0Controller));
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR1LoadClick() throws IOException{
        this.cu.gpr[1].load(this.translateBits(this.gpr1Controller));
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR2LoadClick() throws IOException{

        this.cu.gpr[2].load(this.translateBits(this.gpr2Controller));
    }

    /**
     * Handles loading of bits for the GPR3.
     */
    @FXML
    protected void onGPR3LoadClick() throws IOException{
        this.cu.gpr[3].load(this.translateBits(this.gpr3Controller));
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR0LoadClick() throws IOException{
        this.cu.ixr[0].load(this.translateBits(this.ixr0Controller));
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR1LoadClick() throws IOException{
        this.cu.ixr[1].load(this.translateBits(this.ixr1Controller));
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR2LoadClick() throws IOException{
        this.cu.ixr[2].load(this.translateBits(this.ixr2Controller));
    }

    /**
     * Translates the flipped bits to hex code
     */
    @FXML
    protected void onTranslateClick(){
        final boolean[] val = this.translateBits();

        // Build a boolean string
        final StringBuilder s = new StringBuilder();
        for(final boolean x: val){
            if (x){ s.append("1");}
            else{
                s.append("0");}
        }
        // Convert to hex
        final short short_val = (short)Integer.parseInt(s.toString(),2);
        String hex = Integer.toHexString(short_val & 0xffff);

        // Set label to the hex code.
        hex = "0x"+hex;
        this.lblCode.setText(hex);
    }



    /**
     * Gets the 16-bit array values and flips the bits before resetting the user selected bits.
        @param controller The checkbox controller
        @return A byte array
     */
    private boolean[] translateBits(final CheckBox[] controller) {
        // Create a new bit set to track positions of 'on' bits
        final boolean[] bits = new boolean[controller.length];

        // Loop through the controller setting matching bits and adding the correct bit to the controller, reset bit.
        for(int i=0; i<controller.length; i++) {
            final boolean val = this.bitController[i].isSelected();
            controller[i].setSelected(val);
            this.bitController[i].setSelected(false);

            // If bit is on add to bit set
            bits[controller.length-(1+i)] = val;
        }

        // Return the byte array
        return bits;

    }

    /**
     * Gets the 16-bit array values and flips the bits before resetting the user selected bits.
     @return A byte array
     */
    private boolean[] translateBits() {
        // Create a new bit set to track positions of 'on' bits
        final boolean[] bits = new boolean[16];

        // Loop through the controller setting matching bits and adding the correct bit to the controller, reset bit.
        for(int i=15; i>=0; i--) {
            final boolean val = this.bitController[i].isSelected();
            bits[15-i]=val;
        }

        // Return the byte array
        return bits;

    }

    /**
     * Allows the user to select a file and then load that file into memory.
     * This expects the file to be in the format "ADDRESS COMMAND" e.g. "0006 C268".
     */
    @FXML
    protected void onLoadFileClick() {
        // UI for loading a file
//        JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileChooser fc = new FileChooser();
        final File file = fc.showOpenDialog(null);

        // Try catch for opening a file
        try {
            System.out.printf("Returned from file chooser; File path is %s\n", file.getAbsolutePath());

            // Save the file to the path
            final String file_path = file.getAbsolutePath();
            System.out.print("Have program file path.");
            // Create a file reader
            final File thisFile = new File(file_path);
            final Scanner reader = new Scanner(thisFile);

            // Loop over the reader until there are no new lines
            while (reader.hasNextLine()) {

                // Split the line into an array of [Address, Command]
                final String[] data = reader.nextLine().split(" ",2);

                // Convert these to boolean arrays for storing
                final short memory = this.toByteArray(data[0]);
                final short value = this.toByteArray(data[1]);

                this.cu.writeDataToMemory(memory, value);

                // Update the program counters with the first command
                this.cu.get_first_command();
                // Update the UI elements
                this.setUIElem(this.cu.pc, this.pcController);
            }
        } catch (final FileNotFoundException | RuntimeException e){
            System.out.println("ERROR: File not found!");
        } catch (final IOException e) {
            System.out.println("ERROR: Loading program into memory!");
            e.printStackTrace();
        }

    }

    /**
     * For printing the memory to the stack
     */
    @FXML
    protected void onPrtMemClick(){
        this.cu.printMem();
    }

    /**
     * Advance the simulation 1 step
     * @throws IOException IO exception from parent
     */
    @FXML
    protected boolean onStepClick() throws IOException{
        final boolean b = this.cu.singleStep();
        this.updateUI();
        return b;
    }

    /**
     * Runs a complete program on click of button
     * @throws InterruptedException Passes any IO exceptions up the stack.
     */
    @FXML
    protected void onRunClick() throws InterruptedException {
        boolean run  = true;

        while(run){
            try {
                run = this.onStepClick();
            } catch (final IOException e) {
                System.out.println("ERROR :: There was an error running the program");
                e.printStackTrace();
                throw new InterruptedException();
            }
        }
    }

    /**
     * Store the recorded value into the memory address in the MAR
     */
    @FXML
    protected void onStoreClick() throws IOException {

        this.cu.writeDataToMemory();
        this.updateUI();
    }

    /**
     * Store the recorded value into the memory address in the MAR and increment the MAR by 1.
     */
    @FXML
    protected void onStorePClick() throws IOException {
        this.onStoreClick();
        this.cu.mar.load((short) (this.cu.mar.read()+1));
        this.updateUI();
    }

    @FXML
    protected void onLoadClick() throws IOException {
        this.cu.mbr.load(this.cu.loadDataFromMemory(this.cu.mar.read()));
        this.setUIElem(this.cu.mbr, this.mbrController);
        this.updateUI();
    }

    /**
     * Update the entire UI after a step
     */
    private void updateUI() {
        this.setUIElems(this.cu.gpr, this.gpr);
        this.setUIElems(this.cu.ixr, this.ixr);

        this.setUIElem(this.cu.pc, this.pcController);
        this.setUIElem(this.cu.mar, this.marController);
        this.setUIElem(this.cu.ir, this.irController);

        this.setControlCode(this.cu.controlCode);
        // Memory Fault register
        // setUIElem(cu.mfr,mfrController);
    }

    /**
     * Used to set the control code UI
     * @param code the current control code
     */
    private void setControlCode(final CC code){
        this.ch_over.setSelected(false);
        this.ch_under.setSelected(false);
        this.ch_div.setSelected(false);
        this.ch_eq.setSelected(false);

        switch (code){
            case OVERFLOW -> this.ch_over.setSelected(true);
            case UNDERFLOW -> this.ch_under.setSelected(true);
            case DIVZERO -> this.ch_div.setSelected(true);
            case EQUALORNOT -> this.ch_eq.setSelected(true);
        }
    }

    /**
     * Function to convert to a 16-bit short
     * @param s The binary string
     * @return A short (byte array)
     */
    private short toByteArray(final String s) {
        final short it = (short) Integer.parseInt(s, 16);
        System.out.println("Hexadecimal String: " + s);
        return it;
    }

    /**
     * Set multiple UI elements
     * @param registers The array of registers
     * @param controllers The array of UI controllers
     */
    private void setUIElems(final Register[] registers, final CheckBox[][] controllers){
        for(int i = 0; i< registers.length;i++){
            final CheckBox[] controller = controllers[i];
            this.resetUI(controller);
            final int[] set_bits = registers[i].getSetBits();


            if(set_bits == null){
                continue;
            }

            for(final int a: set_bits){
                controller[15-a].setSelected(true);
            }
        }
    }

    /**
     * Set a single UI element
     * @param register The register to set
     * @param controller The UI controller
     */
    private void setUIElem(final Register register, final CheckBox[] controller){
        this.resetUI(controller);
        final int[] set_bits = register.getSetBits();

        if(set_bits == null){
            return;
        }

        final int length = register.get_size()-1;

        for(final int a: set_bits){
            controller[length-a].setSelected(true);
        }
    }

    /**
     * Resets the UI element.
     * @param controller Takes the check box controller
     */
    private void resetUI(final CheckBox[] controller) {
        for(final CheckBox x:controller){
            x.setSelected(false);
        }
    }
}