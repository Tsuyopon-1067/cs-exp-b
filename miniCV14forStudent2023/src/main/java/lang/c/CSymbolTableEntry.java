package lang.c;

import java.util.ArrayList;

import lang.SymbolTableEntry;
import lang.c.parse.FunctionInfo;
import lang.c.parse.ParameterInfo;

public class CSymbolTableEntry extends SymbolTableEntry {
	private CType type; // この識別子に対して宣言された型
	private int size; // メモリ上に確保すべきワード数
	private boolean constp; // 定数宣言か？
	private boolean isGlobal; // 大域変数か？
	private int address; // 割り当て番地
	private boolean isFunction = false; // 関数か？
	private FunctionInfo functionInfo; // 関数の型とか
	private int value; // 定数の値
	//private ArrayList<CType> argTypes; // 関数の引数の型のリスト

	//public CSymbolTableEntry(CType type, int size, boolean constp, boolean isFunction, ArrayList<CType> argTypes) {
	//	this.type = type;
	//	this.size = size;
	//	this.constp = constp;
	//	this.isFunction = isFunction;
	//	for (CType t : argTypes) {
	//		this.argTypes.add(t);
	//	}
	//}
	public CSymbolTableEntry(CType type, int size, boolean constp, boolean isFunction) {
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.isFunction = isFunction;
	}
	public CSymbolTableEntry(CType type, int size, boolean constp) {
		this.type = type;
		this.size = size;
		this.constp = constp;
	}
	public CSymbolTableEntry(CType type, int size, boolean constp, int value) {
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.value = value;
	}
	public String toExplainString() { // このエントリに関する情報を作り出す。記号表全体を出力するときに使う。
		// return type.toString() + ", " + size + ", " + (constp ? "定数" : "変数");
		return String.format("%s, size=%d, %s, address=%d", type.toString(), size, (constp ? "定数" : "変数"), address);
	}
	public CType GetCType() { return type; }
	public int getSize() { return size; }
	public boolean isConstant() { return constp; }
	public boolean isGlobal() { return isGlobal; }
	public void setAddress(int addr) { address = addr; }
	public void setIsGlobal(boolean isGlobal) { this.isGlobal = isGlobal; }
	public int getAddress() { return address; }
	public boolean isFunction() { return isFunction; }
	public void setFunctionInfo(FunctionInfo functionInfo) { this.functionInfo = functionInfo; }
	public FunctionInfo getFunctionInfo() { return functionInfo; }
	public int getValue() { return value; }
	public boolean verificateFunction(CSymbolTableEntry e) {
		if (this.getFunctionInfo().getIsExistPrototype() || e.getFunctionInfo().getIsExistPrototype()) {
			ArrayList<ParameterInfo> argInfoList = this.functionInfo.getParamInfoList();
			ArrayList<ParameterInfo> paramInfoList = e.getFunctionInfo().getParamInfoList(); // proto

			if (paramInfoList.size() != argInfoList.size()) {
				return false;
			}

			for (int i = 0; i < paramInfoList.size(); i++) {
				System.out.println(paramInfoList.get(i).toString());
				if (paramInfoList.get(i).getType().getType() != argInfoList.get(i).getType().getType()) {
					return false;
				}
			}
		}
		return e.isFunction() && e.GetCType().equals(e.GetCType());
	}
}