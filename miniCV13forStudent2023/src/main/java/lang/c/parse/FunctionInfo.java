package lang.c.parse;

import java.util.ArrayList;

import lang.c.CType;

public class FunctionInfo {
    private String name;
    private CType returnType;
    private boolean isExistReturn;
    private String returnLabel;
    private ArrayList<ParameterInfo> paramInfoList;
    private boolean isExistPrototype = false;

    public FunctionInfo(String name, CType returnType, String returnLabel) {
        this.name = name;
        this.returnType = returnType;
        this.isExistReturn = false;
        this.returnLabel = returnLabel;
    }

    public FunctionInfo(String name, CType returnType, String returnLabel, ArrayList<ParameterInfo> paramInfoList) {
        this(name, returnType, returnLabel);
        this.paramInfoList = paramInfoList;
    }

    public String getName() { return name; }
    public CType getReturnType() { return returnType; }
    public void setTrueToIsExistReturn() { this.isExistReturn = true; }
    public boolean getIsExistReturn() { return isExistReturn; }
    public String getReturnLabel() { return returnLabel; }
    public int getParamSize() { return paramInfoList.size(); }
    public ArrayList<ParameterInfo> getParamInfoList() { return paramInfoList; }
    public void setExistPrototype() { this.isExistPrototype = true; }
    public boolean getIsExistPrototype() { return isExistPrototype; }

    @Override
    public String toString() {
        return String.format("name: %s, type: %s, label: %s, param: %d",
            this.name, this.returnType.toString(), this.returnLabel, this.paramInfoList.size());
    }
}