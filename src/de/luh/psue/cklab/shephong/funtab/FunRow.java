package de.luh.psue.cklab.shephong.funtab;

/**
 * This is an entry in the FunctionTable.
 * 
 * @author shephongkrewe (salz)
 *
 */
public final class FunRow<NodeType> implements Comparable<FunRow<NodeType>> {

	private boolean isStatic;
	private String ident;
	private NodeType entry;
	
	public FunRow(boolean isStatic,
			String ident,
			NodeType entry) {
		this.isStatic = isStatic;
		this.ident = ident;
		this.entry = entry;
	}
	
	/**
	 * compare the node to another String, using
	 * String.compareTo () on the ident.
	 * 
	 * @param name String to compare the node to
	 * @return see String.compareTo ()
	 */
	public int compareTo (String name) {
		return this.ident.compareTo (name);
	}
	
	/**
	 * compare the node to another node. This is done by comparing
	 * both ident Strings using String.compareTo ()
	 * 
	 * @param entry the other node to compare to this one
	 * @return as in String.compareTo ()
	 */
	public int compareTo (FunRow<NodeType> entry) {
		return this.ident.compareTo (entry.getIdent ());
	}
	
	/**
	 * @return [ ident : static/dynamic : il as text ]
	 */
	public String toString () {
		return "[ " + this.ident +
			(this.isStatic? " : static : " : " : dynamic : ") +
			this.entry + " ]\n";
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public NodeType getEntry() {
		return entry;
	}

	public void setEntry(NodeType entry) {
		this.entry = entry;
	}

}
