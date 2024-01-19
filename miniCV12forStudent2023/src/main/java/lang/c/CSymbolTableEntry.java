package lang.c;

import lang.SymbolTableEntry;
import lang.c.parse.FunctionInfo;

public class CSymbolTableEntry extends SymbolTableEntry {
	private CType type; // この識別子に対して宣言された型
	private int size; // メモリ上に確保すべきワード数
	private boolean constp; // 定数宣言か？
	private boolean isGlobal; // 大域変数か？
	private int address; // 割り当て番地
	private boolean isFunction; // 関数か？
	private FunctionInfo functionInfo; // 関数の型とか

	public CSymbolTableEntry(CType type, int size, boolean constp, boolean isFunction) {
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.isFunction = isFunction;
		this.isGlobal = true;
	}

	public CSymbolTableEntry(CType type, int size, boolean constp) {
		this.type = type;
		this.size = size;
		this.constp = constp;
	}

	public String toExplainString() { // このエントリに関する情報を作り出す。記号表全体を出力するときに使う。
		return type.toString() + ", " + size + ", " + (constp ? "定数" : "変数");
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
	public boolean verificateFunction(CSymbolTableEntry e) {
		// todo すでに登録された関数と同じかどうかを確認する
		if (e.isFunction() && e.GetCType().equals(e.GetCType())) {
			return true;
		} else {
			return false;
		}
	}
}