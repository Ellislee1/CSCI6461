package com.csci6461;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Class for controlling elements of the UI.
 * @author Ellis Thompson
 */
public class ComputerController {

    /**
     * Control unit for the system
     */
    private ControlUnit cu;

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
     * Holds the list view for the cache
     */
    @FXML
    private ListView<String> lstCache, lstMemory, lstOutput;

    /**
     * Label to output the hex code
     */
    @FXML
    private Label lblCode;

    @FXML
    private CheckBox ch_under, ch_over, ch_div, ch_eq;

    @FXML
    private TextField txtInput;

    @FXML Button btnSubmit;
    @FXML Label lblInput, lblOutput;

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

    private final String intReg = "(^[0-9]*$)|(^[a-zA-Z]{1})|(&)";

    private int inputInt;

    private ArrayList<Integer> outList;

    /**
     * Initializer for the program.
     * This will set up all the controllers.
     */
    @FXML
    private void initialize() {
        inputInt = 0;
        txtInput.textProperty().set("0");
        outList = new ArrayList<Integer>();


        cu = new ControlUnit(txtInput,btnSubmit,lblInput,outList);

        bitController = new ToggleButton[]{adr0, adr1, adr2, adr3, adr4, i5, ixr6, ixr7, gpr8, gpr9, ctlA, ctlB,
                ctlC, ctlD, ctlE, ctlF};

        pcController = new CheckBox[]{pc1, pc2, pc4, pc8, pc16, pc32, pc64, pc128, pc256, pc512, pc1024, pc2048};
        marController = new CheckBox[]{mar1, mar2, mar4, mar8, mar16, mar32, mar64, mar128, mar256, mar512, mar1024, mar2048};
        mbrController = new CheckBox[]{mbr1, mbr2, mbr4, mbr8, mbr16, mbr32, mbr64, mbr128, mbr256, mbr512, mbr1024, mbr2048,
                mbr4096, mbr8192, mbr16384, mbr32768};

        gpr0Controller = new CheckBox[]{gpr10, gpr20, gpr40, gpr80, gpr160, gpr320, gpr640, gpr1280, gpr2560, gpr5120,
                gpr10240, gpr20480, gpr40960, gpr81920, gpr163840, gpr327680};
        gpr1Controller = new CheckBox[]{gpr11, gpr21, gpr41, gpr81, gpr161, gpr321, gpr641, gpr1281, gpr2561,
                gpr5121, gpr10241, gpr20481, gpr40961, gpr81921, gpr163841, gpr327681};
        gpr2Controller = new CheckBox[]{gpr12, gpr22, gpr42, gpr82, gpr162, gpr322, gpr642, gpr1282, gpr2562, gpr5122,
                gpr10242, gpr20482, gpr40962, gpr81922, gpr163842, gpr327682};
        gpr3Controller = new CheckBox[]{gpr13, gpr23, gpr43, gpr83, gpr163, gpr323, gpr643, gpr1283, gpr2563,
                gpr5123, gpr10243, gpr20483, gpr40963, gpr81923, gpr163843, gpr327683};

        ixr0Controller = new CheckBox[]{ixr0_1, ixr0_2, ixr0_3, ixr0_4, ixr0_5, ixr0_6, ixr0_7, ixr0_8, ixr0_9, ixr0_10,
                ixr0_11, ixr0_12,
                ixr0_13, ixr0_14, ixr0_15, ixr0_16};
        ixr1Controller = new CheckBox[]{ixr1_1, ixr1_2, ixr1_3, ixr1_4, ixr1_5, ixr1_6, ixr1_7, ixr1_8, ixr1_9, ixr1_10,
                ixr1_11, ixr1_12,
                ixr1_13, ixr1_14, ixr1_15, ixr1_16};
        ixr2Controller = new CheckBox[]{ixr2_1, ixr2_2, ixr2_3, ixr2_4, ixr2_5, ixr2_6, ixr2_7, ixr2_8, ixr2_9, ixr2_10,
                ixr2_11, ixr2_12,
                ixr2_13, ixr2_14, ixr2_15, ixr2_16};

        irController = new CheckBox[]{ir_1, ir_2, ir_3, ir_4, ir_5, ir_6, ir_7, ir_8, ir_9, ir_10, ir_11, ir_12, ir_13, ir_14,
                ir_15, ir_16};
        // mfrController = new CheckBox[]{mfr_1,mfr_2,mfr_4,mfr_8};

        gpr = new CheckBox[][]{gpr0Controller, gpr1Controller, gpr2Controller, gpr3Controller};
        ixr = new CheckBox[][]{ixr0Controller, ixr1Controller, ixr2Controller};

        txtInput.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.printf("Text input listener receive new value: %s\n", newValue);
            if(Objects.equals(newValue, "")){
                System.out.println("Input is blank; Setting text input to 0.");
                txtInput.textProperty().set("0");
                inputInt = 0;
                return;
            }

            String newText;

           if(!newValue.matches(intReg)) {
                /* Invalid input received. Keep old value. */
                System.out.println("Input does not match Regex; Leaving old value");
//                txtInput.textProperty().set(oldValue);
                newText = oldValue;
            } else {
                if (!Character.isDigit(newValue.charAt(0))) {
                    /* Process new character input */
                    System.out.printf("Input is not digit. Saving character: %c\n", newValue.charAt(0));

                    /* If character is &, encode EOT instead so program knows input is over */
                    if (newValue.charAt(0) == '&') {
                        System.out.println("Input is &; Encoding EOF");
                        inputInt = 4;
                    } else {
                        System.out.println("Input is regular character; Encoding ASCII");
                        inputInt = (int) newValue.charAt(0);
                    }
                    newText = newValue;
                } else {
                    /* Process new numerical input */
                    System.out.printf("Input is number: %d\n", Integer.parseInt(newValue, 10));
                    if (Integer.parseInt(newValue, 10) > 32767) {
                        /* Value is too big! Set to max value. */
                        txtInput.textProperty().set("32767");
                        inputInt = 32767;
                    } else {
                        System.out.println("Input is valid number; Parsing Int");
                        inputInt = Integer.parseInt(newValue, 10);
                    }
                    newText = String.valueOf(inputInt);
                }
                System.out.printf("Input int after processing new text is: %d\n", inputInt);

            }
            /* Update text input value */
            txtInput.textProperty().set(newText);
        });

        updateCache();
        updateMemory();
    }

    /**
     * Handles loading of bits for the program counter.
     */
    @FXML
    protected void onPCLoadClick() throws IOException {

        // Load the byte array into the controller and set UI bits
        cu.pc.load(translateBits(pcController));
    }

    /**
     * Handles loading of bits for the MAR.
     */
    @FXML
    protected void onMARLoadClick() throws IOException{
        cu.mar.load(translateBits(marController));
        cu.read_mem();

        setUIElem(cu.mbr, mbrController);
    }

    /**
     * Handles loading of bits for the MBR.
     */
    @FXML
    protected void onMBRLoadClick() throws IOException{
        cu.mbr.load(translateBits(mbrController));
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR0LoadClick() throws IOException{
        cu.gpr[0].load(translateBits(gpr0Controller));
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR1LoadClick() throws IOException{
        cu.gpr[1].load(translateBits(gpr1Controller));
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR2LoadClick() throws IOException{

        cu.gpr[2].load(translateBits(gpr2Controller));
    }

    /**
     * Handles loading of bits for the GPR3.
     */
    @FXML
    protected void onGPR3LoadClick() throws IOException{
        cu.gpr[3].load(translateBits(gpr3Controller));
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR0LoadClick() throws IOException{
        cu.ixr[0].load(translateBits(ixr0Controller));
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR1LoadClick() throws IOException{
        cu.ixr[1].load(translateBits(ixr1Controller));
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR2LoadClick() throws IOException{
        cu.ixr[2].load(translateBits(ixr2Controller));
    }

    /**
     * Translates the flipped bits to hex code
     */
    @FXML
    protected void onTranslateClick(){
        boolean[] val = translateBits();

        // Build a boolean string
        StringBuilder s = new StringBuilder();
        for(boolean x: val){
            if (x){ s.append("1");}
            else{
                s.append("0");}
        }
        // Convert to hex
        short short_val = (short)Integer.parseInt(s.toString(),2);
        String hex = Integer.toHexString(short_val & 0xffff);

        // Set label to the hex code.
        hex = "0x"+hex;
        lblCode.setText(hex);
    }

    @FXML
    private void onInput() throws InterruptedException {
        boolean[] boolIn = cu.get_bool_array(Integer.toBinaryString(inputInt&0xFFFF));

        System.out.printf("\nRead input value from keyboard: %d\n\n", inputInt);

        cu.gpr[cu.inReg].set_bits(boolIn);
        updateUI();
        txtInput.disableProperty().set(true);
        lblInput.setVisible(false);
        btnSubmit.disableProperty().set(true);

        if(cu.run){
            this.onRunClick();
        }
    }



    /**
     * Gets the 16-bit array values and flips the bits before resetting the user selected bits.
        @param controller The checkbox controller
        @return A byte array
     */
    private boolean[] translateBits(CheckBox[] controller) {
        // Create a new bit set to track positions of 'on' bits
        boolean[] bits = new boolean[controller.length];

        // Loop through the controller setting matching bits and adding the correct bit to the controller, reset bit.
        for(int i=0; i<controller.length; i++) {
            boolean val = bitController[i].isSelected();
            controller[i].setSelected(val);
            bitController[i].setSelected(false);

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
        boolean[] bits = new boolean[16];

        // Loop through the controller setting matching bits and adding the correct bit to the controller, reset bit.
        for(int i=15; i>=0; i--) {
            boolean val = bitController[i].isSelected();
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
        final FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        // Try catch for opening a file
        try {
            System.out.printf("Returned from file chooser; File path is %s\n", file.getAbsolutePath());

            // Save the file to the path
            String file_path = file.getAbsolutePath();
            System.out.print("Have program file path.");
            // Create a file reader
            File thisFile = new File(file_path);
            Scanner reader = new Scanner(thisFile);

            // Loop over the reader until there are no new lines
            while (reader.hasNextLine()) {

                // Split the line into an array of [Address, Command]
                String[] data = reader.nextLine().split(" ",3);

                // Convert these to boolean arrays for storing
                short memory = toByteArray(data[0]);
                short value = toByteArray(data[1]);

                cu.writeDataToMemory(memory, value);

                // Update the program counters with the first command
                cu.getFirstCommand();
                // Update the UI elements
                setUIElem(cu.pc, pcController);
            }
        } catch (FileNotFoundException e){
            System.out.println("ERROR: File not found!");
        } catch (IOException e) {
            System.out.println("ERROR: Loading program into memory!");
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        updateMemory();

    }

    /**
     * For printing the memory to the stack
     */
    @FXML
    protected void onPrtMemClick(){
        cu.printMem();
    }

    /**
     * Advance the simulation 1 step
     * @throws IOException IO exception from parent
     */
    @FXML
    protected boolean onStepClick() throws IOException{
        boolean b = cu.singleStep();
        updateUI();
        return b;
    }

    /**
     * Runs a complete program on click of button
     * @throws InterruptedException Passes any IO exceptions up the stack.
     */
    @FXML
    protected void onRunClick() throws InterruptedException {
        boolean run  = true;
        cu.run = true;

        while(run){
            try {
                run = onStepClick();
            } catch (IOException e) {
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

        cu.writeDataToMemory();
        updateUI();
    }

    /**
     * Store the recorded value into the memory address in the MAR and increment the MAR by 1.
     */
    @FXML
    protected void onStorePClick() throws IOException {
        onStoreClick();
        cu.mar.load((short) (cu.mar.read()+1));
        updateUI();
    }

    /**
     * Loading a value into memory
     * @throws IOException If a value can not be loaded into memory
     */
    @FXML
    protected void onLoadClick() throws IOException {
        cu.mbr.load(cu.loadDataFromMemory(cu.mar.read()));
        setUIElem(cu.mbr, mbrController);
        updateUI();
    }

    /**
     * Update the entire UI after a step
     */
    private void updateUI() {
        setUIElems(cu.gpr, gpr);
        setUIElems(cu.ixr, ixr);

        setUIElem(cu.pc, pcController);
        setUIElem(cu.mar, marController);
        setUIElem(cu.ir, irController);

        setControlCode(cu.controlCode);
        // Memory Fault register
        // setUIElem(cu.mfr,mfrController);
        updateCache();
        updateMemory();
        updateOutput();
    }

    /**
     * Used to set the control code UI
     * @param code the current control code
     */
    private void setControlCode(CC code){
        ch_over.setSelected(false);
        ch_under.setSelected(false);
        ch_div.setSelected(false);
        ch_eq.setSelected(false);

        switch (code){
            case OVERFLOW -> ch_over.setSelected(true);
            case UNDERFLOW -> ch_under.setSelected(true);
            case DIVZERO -> ch_div.setSelected(true);
            case EQUALORNOT -> ch_eq.setSelected(true);
        }
    }

    /**
     * Function to convert to a 16-bit short
     * @param s The binary string
     * @return A short (byte array)
     */
    private short toByteArray(String s) {
        short it = (short) Integer.parseInt(s, 16);
        System.out.println("Hexadecimal String: " + s);
        return it;
    }

    /**
     * Set multiple UI elements
     * @param registers The array of registers
     * @param controllers The array of UI controllers
     */
    private void setUIElems(Register[] registers, CheckBox[][] controllers){
        for(int i = 0; i< registers.length;i++){
            CheckBox[] controller = controllers[i];
            resetUI(controller);
            int[] set_bits = registers[i].getSetBits();


            if(set_bits == null){
                continue;
            }

            for(int a: set_bits){
                controller[15-a].setSelected(true);
            }
        }
    }

    /**
     * Set a single UI element
     * @param register The register to set
     * @param controller The UI controller
     */
    private void setUIElem(Register register, CheckBox[] controller){
        resetUI(controller);
        int[] set_bits = register.getSetBits();

        if(set_bits == null){
            return;
        }

        int length = register.get_size()-1;

        for(int a: set_bits){
            controller[length-a].setSelected(true);
        }
    }

    /**
     * Resets the UI element.
     * @param controller Takes the check box controller
     */
    private void resetUI(CheckBox[] controller) {
        for(CheckBox x:controller){
            x.setSelected(false);
        }
    }

    /**
     * Cache view is now updated
     */
    private void updateCache(){
        // Reset the items
        lstCache.getItems().clear();
        lstCache.getItems().add("Tag\t\tBlock\tValue");

        for(int i=0; i<cu.mainMemory.getCacheSize();i++){
            try {
                short[] vals = cu.mainMemory.getCacheLine((short) i);
                for(int x=1; x<vals.length;x++){
                    if (vals[x] == 0){
                        continue;
                    }

                    String text = String.format("%d\t\t%d\t\t%s",vals[0],x,getHex(vals[x]));
                    lstCache.getItems().add(text);
                }
            } catch (NullPointerException ignored){

            }
        }

    }

    /**
     * Memory view is now updated
     */
    private void updateMemory(){
        // Reset the items
        lstMemory.getItems().clear();
        lstMemory.refresh();
        lstMemory.getItems().add("Location\t\tValue");
        short[] data = cu.mainMemory.getData();

        for(int i=0; i<data.length;i++){
            short val = data[i];
            if(val != 0){
                String text = String.format("%s\t\t%s",getHex(i),getHex(val));
                lstMemory.getItems().add(text);
            } else if(i <= 6){
                String text = String.format("%s\t\t%s",getHex(i),getHex(val));
                lstMemory.getItems().add(text);
            }
        }

    }

    /**
     * Output view is now updated
     */
    private void updateOutput(){
        // Reset the items
        lstOutput.getItems().clear();
        lstOutput.refresh();
        outList = cu.lstOutput;

        for(int x : outList){
            lstOutput.getItems().add("> "+String.valueOf(x));
        }

    }

    /**
     * Gets a formatted hex value
     * @param val the integer value
     * @return returns a formatted 16-bit hex string
     */
    private String getHex(int val) {
        String hex = Integer.toHexString(val & 0xFFFF);
        return formatHex(hex);
    }

    /**
     * Formats the string to look like a standard 2 byte hex
     * @param s The unformatted string
     * @return Returns the formatted string
     */
    private String formatHex(String s) {
        String newS = s;
        if (s.length() < 4) {
            for (int i = 0; i < 4 - s.length(); i++){
                newS = "0"+newS;
            }
        }
        return  "0x"+newS.toUpperCase();
    }
}