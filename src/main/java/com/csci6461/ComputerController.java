package com.csci6461;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

/**
 * Class for controlling elements of the UI
 */
public class ComputerController {

    /**
     * Control unit for the system
     */
    private ControlUnit cu = new ControlUnit();

    @FXML
    private ToggleButton adr0, adr1, adr2, adr3, adr4;

    @FXML
    private ToggleButton i5;

    @FXML
    private ToggleButton ixr6, ixr7;

    @FXML
    private ToggleButton gpr8, gpr9;

    @FXML
    private ToggleButton ctlA, ctlB, ctlC, ctlD, ctlE, ctlF;

    @FXML
    private CheckBox pc1,pc2,pc4,pc8,pc16,pc32,pc64,pc128,pc256,pc512,pc1024,pc2048;

    @FXML
    private CheckBox mar1,mar2,mar4,mar8,mar16,mar32,mar64,mar128,mar256,mar512,mar1024,mar2048;

    @FXML
    private CheckBox mbr1,mbr2,mbr4,mbr8,mbr16,mbr32,mbr64,mbr128,mbr256,mbr512,mbr1024,mbr2048,mbr4096,
            mbr8192,mbr16384,mbr32768;

    @FXML
    private CheckBox gpr10,gpr20,gpr40,gpr80,gpr160,gpr320,gpr640,gpr1280,gpr2560,gpr5120,gpr10240,gpr20480,gpr40960,
            gpr81920,gpr163840,gpr327680;
    @FXML
    private CheckBox gpr11,gpr21,gpr41,gpr81,gpr161,gpr321,gpr641,gpr1281,gpr2561,gpr5121,gpr10241,gpr20481,gpr40961,
            gpr81921,gpr163841,gpr327681;
    @FXML
    private CheckBox gpr12,gpr22,gpr42,gpr82,gpr162,gpr322,gpr642,gpr1282,gpr2562,gpr5122,gpr10242,gpr20482,gpr40962,
            gpr81922,gpr163842,gpr327682;
    @FXML
    private CheckBox gpr13,gpr23,gpr43,gpr83,gpr163,gpr323,gpr643,gpr1283,gpr2563,gpr5123,gpr10243,gpr20483,gpr40963,
            gpr81923,gpr163843,gpr327683;

    @FXML
    private CheckBox ixr0_1, ixr0_2, ixr0_3, ixr0_4, ixr0_5,ixr0_6,ixr0_7,ixr0_8,ixr0_9,ixr0_10,ixr0_11,ixr0_12,
            ixr0_13,ixr0_14,ixr0_15,ixr0_16;
    @FXML
    private CheckBox ixr1_1, ixr1_2, ixr1_3, ixr1_4, ixr1_5,ixr1_6,ixr1_7,ixr1_8,ixr1_9,ixr1_10,ixr1_11,ixr1_12,
            ixr1_13,ixr1_14,ixr1_15,ixr1_16;
    @FXML
    private CheckBox ixr2_1, ixr2_2, ixr2_3, ixr2_4, ixr2_5,ixr2_6,ixr2_7,ixr2_8,ixr2_9,ixr2_10,ixr2_11,ixr2_12,
            ixr2_13,ixr2_14,ixr2_15,ixr2_16;

    @FXML
    private CheckBox mfr_1,mfr_2,mfr_4,mfr_8;

    @FXML
    private CheckBox ir_1,ir_2,ir_3,ir_4,ir_5,ir_6,ir_7,ir_8,ir_9,ir_10,ir_11,ir_12,ir_13,ir_14,ir_15,ir_16;

    private ToggleButton[] bitController;

    private CheckBox[] pcController;
    private CheckBox[] marController;
    private CheckBox[] mbrController;
    private CheckBox[] gpr0Controller;
    private CheckBox[] gpr1Controller;
    private CheckBox[] gpr2Controller;
    private CheckBox[] gpr3Controller;
    private CheckBox[] ixr0Controller;
    private CheckBox[] ixr1Controller;
    private CheckBox[] ixr2Controller;
    private CheckBox[] irController;
    private CheckBox[] mfrController;

    @FXML
    private void initialize() {
        bitController = new ToggleButton[]{adr0, adr1, adr2, adr3, adr4, i5, ixr6, ixr7, gpr8, gpr9, ctlA, ctlB,
                ctlC, ctlD, ctlE, ctlF};

        pcController = new CheckBox[]{pc1,pc2,pc4,pc8,pc16,pc32,pc64,pc128,pc256,pc512,pc1024,pc2048};
        marController = new CheckBox[]{mar1,mar2,mar4,mar8,mar16,mar32,mar64,mar128,mar256,mar512,mar1024,mar2048};
        mbrController = new CheckBox[]{mbr1,mbr2,mbr4,mbr8,mbr16,mbr32,mbr64,mbr128,mbr256,mbr512,mbr1024,mbr2048,
                mbr4096,mbr8192,mbr16384,mbr32768};

        gpr0Controller = new CheckBox[]{gpr10,gpr20,gpr40,gpr80,gpr160,gpr320,gpr640,gpr1280,gpr2560,gpr5120,
                gpr10240,gpr20480,gpr40960,gpr81920,gpr163840,gpr327680};
        gpr1Controller = new CheckBox[]{gpr11,gpr21,gpr41,gpr81,gpr161,gpr321,gpr641,gpr1281,gpr2561,
                gpr5121,gpr10241,gpr20481,gpr40961, gpr81921,gpr163841,gpr327681};
        gpr2Controller = new CheckBox[]{gpr12,gpr22,gpr42,gpr82,gpr162,gpr322,gpr642,gpr1282,gpr2562,gpr5122,
                gpr10242,gpr20482,gpr40962, gpr81922,gpr163842,gpr327682};
        gpr3Controller = new CheckBox[]{gpr13,gpr23,gpr43,gpr83,gpr163,gpr323,gpr643,gpr1283,gpr2563,
                gpr5123,gpr10243,gpr20483,gpr40963, gpr81923,gpr163843,gpr327683};

        ixr0Controller = new CheckBox[]{ixr0_1, ixr0_2, ixr0_3, ixr0_4, ixr0_5,ixr0_6,ixr0_7,ixr0_8,ixr0_9,ixr0_10,
                ixr0_11,ixr0_12,
                ixr0_13,ixr0_14,ixr0_15,ixr0_16};
        ixr1Controller = new CheckBox[]{ixr1_1, ixr1_2, ixr1_3, ixr1_4, ixr1_5,ixr1_6,ixr1_7,ixr1_8,ixr1_9,ixr1_10,
                ixr1_11,ixr1_12,
                ixr1_13,ixr1_14,ixr1_15,ixr1_16};
        ixr2Controller = new CheckBox[]{ixr2_1, ixr2_2, ixr2_3, ixr2_4, ixr2_5,ixr2_6,ixr2_7,ixr2_8,ixr2_9,ixr2_10,
                ixr2_11,ixr2_12,
                ixr2_13,ixr2_14,ixr2_15,ixr2_16};

        irController = new CheckBox[]{ir_1,ir_2,ir_3,ir_4,ir_5,ir_6,ir_7,ir_8,ir_9,ir_10,ir_11,ir_12,ir_13,ir_14,
                ir_15,ir_16};
        mfrController = new CheckBox[]{mfr_1,mfr_2,mfr_4,mfr_8};

    }

    /**
     * Handles loading of bits for the program counter.
     */
    @FXML
    protected void onPCLoadClick() {
        translateBits(pcController);
    }

    /**
     * Handles loading of bits for the MAR.
     */
    @FXML
    protected void onMARLoadClick() {
        translateBits(marController);
    }

    /**
     * Handles loading of bits for the MBR.
     */
    @FXML
    protected void onMBRLoadClick() {
        translateBits(mbrController);
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR0LoadClick() {
        translateBits(gpr0Controller);
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR1LoadClick() {
        translateBits(gpr1Controller);
    }

    /**
     * Handles loading of bits for the GPR0.
     */
    @FXML
    protected void onGPR2LoadClick() {

        translateBits(gpr2Controller);
    }

    /**
     * Handles loading of bits for the GPR3.
     */
    @FXML
    protected void onGPR3LoadClick() {
        translateBits(gpr3Controller);
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR0LoadClick() {
        translateBits(ixr0Controller);
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR1LoadClick() {
        translateBits(ixr1Controller);
    }

    /**
     * Handles loading of bits for the IXR0.
     */
    @FXML
    protected void onIXR2LoadClick() {
        translateBits(ixr2Controller);
    }



    /**
     * Gets the 16-bit array values and flips the bits before resetting the user selected bits.
        @param controller The checkbox controller
     */
    private void translateBits(CheckBox[] controller) {
       for(int i=0; i<controller.length; i++){
           controller[i].setSelected(bitController[i].isSelected());
           bitController[i].setSelected(false);
       }

    }

}