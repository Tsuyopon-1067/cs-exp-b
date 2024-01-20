package lang.c.parse;

import lang.c.CType;

public class ParameterInfo {
    private CType type;
    private String name;

    public ParameterInfo(CType type, String name) {
        this.type = type;
        this.name = name;
    }

    public CType getType() { return type; }
    public String getName() { return name; }
}
