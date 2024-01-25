package lang.c;

import lang.*;

public abstract class CParseRule extends ParseRule<CParseContext> implements lang.Compiler<CParseContext>, LL1<CToken> {

	// この節点の（推測される）型
	private CType ctype;

	public void setCType(CType ctype) {
		this.ctype = ctype;
	}

	public CType getCType() {
		return ctype;
	}

	// この節点は定数を表しているか？
	private boolean isConstant;

	// 定数の値
	private int value = 0;

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}

	public boolean isConstant() {
		return isConstant;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public String toString() {
		return String.format("%s, value=%d, isConstant=%s", this.getClass().getName(), getValue(), isConstant());
	}
}
