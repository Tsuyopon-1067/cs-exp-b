package lang.c.parse;

import java.util.ArrayList;

import lang.c.CType;

public class FunctionInfo {
    private String name;
    private CType returnType;
    private boolean isExistReturn;
    private String returnLabel;
    private ArrayList<CType> argTypes;

    public FunctionInfo(String name, CType returnType, String returnLabel) {
        this.name = name;
        this.returnType = returnType;
        this.isExistReturn = false;
        this.returnLabel = returnLabel;
    }

    public FunctionInfo(String name, CType returnType, String returnLabel, ArrayList<CType> argTypes) {
        this(name, returnType, returnLabel);
        this.argTypes = argTypes;
    }

    public String getName() { return name; }
    public CType getReturnType() { return returnType; }
    public void setTrueToIsExistReturn() { this.isExistReturn = true; }
    public boolean getIsExistReturn() { return isExistReturn; }
    public String getReturnLabel() { return returnLabel; }
}
