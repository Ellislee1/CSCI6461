package com.csci6461;

public class FloatingPoint {
    private char exponent;
    private char mantissa;
    private char val;

    public char getVal() {
        return val;
    }

    public void setVal(char val) {
        this.val = val;
    }

    public char getExponent() {
        return exponent;
    }

    public void setExponent(char exponent) {
        this.exponent = exponent;
    }

    public char getMantissa() {
        return mantissa;
    }

    public void setMantissa(char mantissa) {
        this.mantissa = mantissa;
    }

    public FloatingPoint(char val) {
        this.val = val;
        String binaryVal = Integer.toBinaryString(val);
        binaryVal = binaryVal.substring(binaryVal.length()-16);

        boolean sign = binaryVal.charAt(0) == '1';

        this.mantissa = (char)Integer.parseInt(binaryVal.substring(2,8));
        if ( binaryVal.charAt(0) == '1'){
            this.mantissa = (char)(this.mantissa * -1);
        }

        this.exponent = (char)Integer.parseInt(binaryVal.substring(8));
        if (sign){
            this.exponent = (char)(this.mantissa * -1);
        }
    }
}
