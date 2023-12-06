package lang.c;

import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry {
	private CType type; // この識別子に対して宣言された型
	private int size; // メモリ上に確保すべきワード数
	private boolean constp; // 定数宣言か？
	private boolean isGlobal; // 大域変数か？
	private int address; // 割り当て番地
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
}