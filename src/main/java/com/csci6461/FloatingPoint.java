package com.csci6461;

public class FloatingPoint {
    private short exponent;
    private short mantissa;
    private short val;
    private boolean sign;

    public short getVal() {
        return val;
    }

    public void setVal(short val) {
        this.val = val;
    }

    public short getExponent() {
        return exponent;
    }

    public void setExponent(short exponent) {
        this.exponent = exponent;
    }

    public short getMantissa() {
        if(this.sign){
            return (short)(mantissa * -1);
        }
        return mantissa;
    }

    public void setMantissa(short mantissa) {
        if (mantissa < 0) {
            this.sign = true;
            mantissa = (short)(mantissa * -1);
        } else {
            this.sign = false;
        }
        this.mantissa = mantissa;
    }

    public FloatingPoint(short val) {
        this.val = val;
        String binaryVal = String.format("%16s", Integer.toBinaryString(val)).replace(' ', '0');

        this.sign = binaryVal.charAt(0) == '1';

        this.mantissa = (short)Integer.parseInt(binaryVal.substring(8),2);


        this.exponent = (short)Integer.parseInt(binaryVal.substring(2,8),2);
        if (binaryVal.charAt(1) == '1'){
            this.exponent = (short)(this.exponent * -1);
        }

    }

    public void ShiftL(int val){
        this.mantissa = (short)((this.mantissa << val) & 0x0000FFFF);
        this.exponent = (short)(this.exponent + val);
    }

    public short ToShort(){
       String boolVal = ToBool();

       return (short)Integer.parseInt(boolVal,2);
    }

    public String ToBool(){
        String out = "0";

        if (sign) {
            out = "1";
        }
        String exp = String.format("%8s", Integer.toBinaryString(exponent)).replace(' ', '0');
        String mant = String.format("%8s", Integer.toBinaryString(mantissa)).replace(' ', '0');
        out += exp+mant;


        return out;
    }
}
