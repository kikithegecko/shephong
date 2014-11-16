package de.luh.psue.cklab.shephong.funtab;

import java.util.ArrayList;
import java.util.Iterator;

import de.luh.psue.cklab.shephong.funtab.FunRow;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;

/**
 * This is the FunctionTable which saves parts of IL for
 * all identifiers in the table.
 * 
 * @author shephongkrewe (salz)
 *
 */
public final class FunTab<NodeType> implements Iterable<FunRow<NodeType>> {
	// Saves the entries in the function table.
	// TODO: choose better type
	// ATT: don't use a type which changes the order of the insertions
	//    at least the iterator must return the values in that order
	private ArrayList<FunRow<NodeType>> table;
	// If the entry is not found we might look it up in a parent table
	private FunTab<NodeType> parentTable;
	// for anonymous entries into the FunTab
	private int serialValue = 0; 

	private CompilerLogger logger;
	
	/**
	 * Constructor for the FunTab
	 * 
	 * @param context
	 * 			specify a compiler context to be able to access the logger.
	 * 			could probably be null if none available.
	 */
	public FunTab(CompilerContext context) {
		// on creation the FunTab is empty
		this.table = new ArrayList<FunRow<NodeType>> ();
		// A parent table to hand lookups down if not found
		this.parentTable = null;
		// to log errors
		this.logger = CompilerLogger.getInstance(context);
	}
	
	/**
	 * Constructor for the FunTab
	 * 
	 * @param context
	 * 			specify a compiler context to be able to access the logger.
	 * 			could probably be null if none available.
	 * @param parent
	 * 			specify a parent FunTab where symbols should be looked up
	 * 			if they can't be found in this FunTab.
	 */
	public FunTab (CompilerContext context, FunTab<NodeType> parent) {
		// on creation the FunTab is empty
		this.table = new ArrayList<FunRow<NodeType>> ();
		// A parent table to hand lookups down if not found
		this.parentTable = parent;
		// to log errors
		this.logger = CompilerLogger.getInstance(context);
	}
	
	public FunTab (FunTab<NodeType> parent) {
		// on creation the FunTab is empty
		this.table = new ArrayList<FunRow<NodeType>> ();
		// A parent table to hand lookups down if not found
		this.parentTable = parent;
		// to log errors to the same instance as the parent does
		this.logger = parent.logger;
	}
	
	/**
	 * Adds a new function to the function table.
	 * If it already is in there, overwrite it with new values.
	 * 
	 * @param name
	 * 			name of the function
	 * @param fun
	 * 			IL of the function to be added (maybe use an interface for the compiler)
	 * @param isStatic
	 * 			static status of the function
	 */
	public void addFun (String name, NodeType fun, boolean isStatic) {
		FunRow<NodeType> row = new FunRow<NodeType> (isStatic, name, fun);
		int index = this.getIndex (name);
		if (index == -1) {
			this.table.add (row);
		} else {
			this.table.set (index, row);
		}
	}
	
	public void addFun(String name, NodeType dataNode) {
		this.addFun(name, dataNode, false);
	}

	/**
	 * Adds a new unnamed entry to the function table.
	 *
	 * @param fun
	 * 			IL of the function to be added (maybe use an interface for the compiler)
	 * @param isStatic
	 * 			static status of the function
	 * @return
	 * 			the generated name for the function
	 */
	public String addUnnamedFun (NodeType fun, boolean isStatic) {
		String anonymousName = this.getAnonymousName ();
		FunRow<NodeType> row = new FunRow<NodeType> (isStatic, anonymousName, fun);
		this.table.add (row);
		return anonymousName;
	}

	/**
	 * Generates an anonymous name that is not yet in the
	 * FunTab. Currently it assumes, given names don't start
	 * with "anon::".
	 * 
	 * @return the generated anonymous name.
	 */
	public String getAnonymousName () {
		String anonName;
		do {
			this.serialValue++;
			anonName = "anon::" + this.serialValue;
		} while (this.getIndex (anonName) != -1);
		return anonName;
	}
	
	/**
	 * Returns an anonymous function name by prepending
	 * "anon::" and appending "::" and a serial number.
	 * 
	 * @param name the base name of the function
	 * @return the generated name.
	 */
	public String getAnonymousName (String name) {
		String anonName;
		do {
			this.serialValue++;
			anonName = "anon::" + name + "::" + serialValue; // what kind of magic do we do here?!
		} while (this.getIndex (anonName) != -1);
		return anonName;
	}
	
	/**
	 * Returns the IL that belongs to a function name
	 * 
	 * @param name
	 * 			name of the function
	 * @return
	 * 			IL of the function.
	 * @throws CompilerException
	 * 			Reason.Runtime if the function is not in the table
	 */
	public NodeType lookupFunEntry (String name) throws CompilerException {
		for (FunRow<NodeType> row : this) {
			if (row.compareTo (name) == 0) {
				return row.getEntry ();
			}
		}
		// name not in FunTab
		if (this.parentTable != null) {
			// if we have a parent table, lookup there
			if (hasLoop (this)) {
				this.logger.criticalError (Reason.Runtime, "Recursive FunTab found");
			}
			return this.parentTable.lookupFunEntry (name);
		} else {
			// TODO: should we return null instead?
			this.logger.criticalError (Reason.Runtime, "Function " + name + " not found on lookup entry");
			return null; // not reached
		}
	}

	/**
	 * Returns the static status of a function in the table name
	 * 
	 * @param name
	 * 			name of the function
	 * @return
	 * 			true for static, false for dynamic.
	 * @throws CompilerException
	 * 			Reason.Runtime if the function is not in the table
	 */
	public boolean lookupFunStatic (String name) throws CompilerException {
		for (FunRow<NodeType> row : this) {
			if (row.compareTo (name) == 0) {
				return row.isStatic ();
			}
		}
		// name not in FunTab
		if (this.parentTable != null) {
			// if we have a parent table, lookup there
			return this.parentTable.lookupFunStatic (name);
		} else {
			this.logger.criticalError (Reason.Runtime, "Function not found on lookup static");
			return false; // not reached
		}
	}
	
	/**
	 * delete one entry from the FunTab
	 * 
	 * @param name
	 * 			name of the function
	 * @throws CompilerException
	 * 			Reason.Runtime if the function is not in the table
	 */
	public void deleteFun (String name) throws CompilerException {
		int index = this.getIndex (name);
		if (index != -1) {
			this.table.remove (index);
		} else {
			this.logger.criticalError (Reason.Runtime, "Function not found on delete");
		}
	}

	/**
	 * checks if a function is in the FunTab
	 * 
	 * @param name
	 * 			name of the function
	 * @return
	 * 			result of the search
	 */
	public boolean isInFunTab (String name) {
		// looks in all chained tables, too
		return this.getIndex (name) != -1 ||
			(this.parentTable != null && this.parentTable.getIndex (name) != -1);
	}
	
	/**
	 * Returns the contents of FunTab as text.
	 */
	public String toString () {
		// TODO: should we output chained FunTabs here, too?
		StringBuilder result = new StringBuilder ();
		ArrayList<String> allFuns = new ArrayList<String> ();
		ArrayList<String> allCode = new ArrayList<String> ();
		int maxFunsLength = "Functions".length ();
		int maxCodeLength = "Code".length ();
		
		for (FunRow<NodeType> row : this) {
			String funs = row.getIdent ().toString ();
			String code = row.getEntry ().toString ();
			allFuns.add (funs);
			allCode.add (code);
			maxFunsLength = Math.max (maxFunsLength, funs.length ());
			maxCodeLength = Math.max (maxCodeLength, code.length ());
		}
		
		// format every row using this line and String.format ()
		String format = "| %-" + maxFunsLength + "s | %-7s | %-" + maxCodeLength + "s |\n";

		// build separator line at the top of the table
		// and save it for reuse later
		result.append ("+");
		for (int i = 0; i < maxFunsLength; i++)
			result.append ("-");
		result.append ("--+---------+--"); // static/dynamic
		for (int i = 0; i < maxCodeLength; i++)
			result.append ("-");
		result.append ("+\n");
		String seperator = result.toString ();
		
		// complete table head
		result.append (String.format (format, "Functions", "State", "Code"));
		result.append (seperator);		
		
		// table body
		for (int i = 0; i < allFuns.size (); i++) {
			FunRow<NodeType> row = this.table.get (i);
			result.append (String.format (format,
					row.getIdent (),
					( row.isStatic () ? "static" : "dynamic"),
					row.getEntry ().toString ()));
		}
		result.append (seperator);
		
		if (parentTable != null) {
			result.append ("Parent:\n");
			result.append (parentTable.toString ());
		}

		return result.toString ();
	}

	/**
	 * Returns one row of the FunTab
	 * @param name
	 * 			ident (key) of the row to return
	 */
	public FunRow<NodeType> getFunRow(String name) {
		int index = this.getIndex (name);
		if (index != -1) {
			return this.table.get (index);
		} else if (this.parentTable != null) {
			return this.parentTable.getFunRow (name);
		}
		return null;		
	}

	@Override
	public Iterator<FunRow<NodeType>> iterator() {
		return this.table.iterator ();
	}

	/**
	 * Gets the index of the function in the ArrayList table.
	 * 
	 * @param name
	 * 			name of the function
	 * @return
	 * 			index or -1 if not found
	 */
	private int getIndex (String name) {
		for (int index = 0; index < this.table.size(); index++) {
			if (this.table.get (index).compareTo (name) == 0) {
				return index;
			}
		}
		// name not in FunTab
		return -1;
	}

	// Dummy getters/setters
	/**
	 * @return the table
	 */
	public ArrayList<FunRow<NodeType>> getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(ArrayList<FunRow<NodeType>> table) {
		this.table = table;
	}

	/**
	 * @return the parentTable
	 */
	public FunTab<NodeType> getParentTable() {
		return parentTable;
	}

	/**
	 * @param parentTable the parentTable to set
	 */
	public void setParentTable(FunTab<NodeType> parentTable) {
		this.parentTable = parentTable;
	}

	/**
	 * @return the serialValue
	 */
	public int getSerialValue() {
		return serialValue;
	}

	/**
	 * @param serialValue the serialValue to set
	 */
	public void setSerialValue(int serialValue) {
		this.serialValue = serialValue;
	}

	/**
	 * @return the logger
	 */
	public CompilerLogger getLogger() {
		return logger;
	}

	/**
	 * @param logger the logger to set
	 */
	public void setLogger(CompilerLogger logger) {
		this.logger = logger;
	}
	
	/*
     * Find loops inside the FunTabs parent chain.
     * Also this counts the max. Depth and can optionally print each new max value.
     */
	public static int maxLoop = 0;
	public static boolean hasLoop (FunTab<?> hase) {
		int n = countLoop (hase);
		if (n > maxLoop) {
			//DebugLogger.log ("hasLoop depth "+n);
			maxLoop = n;
		}
		return n < 0;
	}
	public static int countLoop (FunTab<?> hase) {
		int n = 0;
		FunTab<?> igel = hase;
		while (hase != null) {
			hase = hase.getParentTable ();
			n++;
			if (hase == null) {
				return n;
			} else if (hase == igel) {
				return -n;
			}
			hase = hase.getParentTable ();
			igel = igel.getParentTable ();
			n++;
			if (hase == igel) {
				return -n;
			}
		}
		return n;
	}

}