package lang.c;

import lang.SymbolTable;

public class CSymbolTable {
	private class OneSymbolTable extends SymbolTable<CSymbolTableEntry> {
		@Override
		public CSymbolTableEntry register(String name, CSymbolTableEntry e) { return put(name, e); }
		@Override
		public CSymbolTableEntry search(String name) { return get(name); }
	}
	private SymbolTable<CSymbolTableEntry> global; // 大域変数用
	private SymbolTable<CSymbolTableEntry> local; // 局所変数用
	private int addressOffset = 1; // 局所変数変数とフレームポインタの差
	private boolean isGlobalMode = true;

	public CSymbolTable() {
		global = new OneSymbolTable();
		local = new OneSymbolTable();
	}

	public boolean registerGlobal(String name, CSymbolTableEntry e) {
		e.setIsGlobal(true);
		if (searchGlobal(name) != null) {
			return false;
		}
		return global.register(name, e) == null; // putは以前に関連付けられていた値を返す
	}

	public boolean registerLocal(String name, CSymbolTableEntry e) {
		e.setIsGlobal(false);
		if (searchLocal(name) != null) {
			return false;
		}
		if (e != null) {
			e.setAddress(addressOffset);
			addressOffset += e.getSize();
		}
		return local.register(name, e) == null; // putは以前に関連付けられていた値を返す
	}

	public CSymbolTableEntry searchGlobal(String name) {
		return global.search(name);
	}

	public CSymbolTableEntry searchLocal(String name) {
		return local.search(name);
	}

	public void showLocal() {
		local.show();
	}

	public void showGlobal() {
		global.show();
	}

	public void setupLocalSymbolTable() {
		local = new OneSymbolTable();
		addressOffset = 1;
	}

	public void deleteLocalSymbolTable() {
		setupLocalSymbolTable();
	}
	public void setIsGlobalMode(boolean isGlobalMode) {
		this.isGlobalMode = isGlobalMode;
	}
	public boolean isGlobalMode() {
		return isGlobalMode;
	}
	public int getAddressOffset() {
		return addressOffset;
	}
}