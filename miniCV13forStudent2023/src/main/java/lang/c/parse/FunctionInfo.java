package lang.c.parse;

import lang.c.CType;

public class FunctionInfo {
    private String name;
    private CType returnType;
    private boolean isExistReturn;
    private String returnLabel;

    public FunctionInfo(String name, CType returnType, String returnLabel) {
        this.name = name;
        this.returnType = returnType;
        this.isExistReturn = false;
        this.returnLabel = returnLabel;
    }

    public String getName() { return name; }
    public CType getReturnType() { return returnType; }
    public void setTrueToIsExistReturn() { this.isExistReturn = true; }
    public boolean getIsExistReturn() { return isExistReturn; }
    public String getReturnLabel() { return returnLabel; }
}
