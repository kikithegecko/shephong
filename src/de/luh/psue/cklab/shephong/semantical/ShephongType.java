package de.luh.psue.cklab.shephong.semantical;

import java.util.ArrayList;

/**
 * Contains the types interfered by the semantical analysis. This can get fairly complex,
 * but for now it is just an enum type.
 * 
 * @author shephongkrewe (salz)
 *
 */
public enum ShephongType {
	CHAR, NUMBER, LIST, CALL,
	// our head and headOfTail do have boolean meaning
	BOOLEAN,
	// the result of an reflection call
	JOBJECT,
	// if we weren't able to interfer the type it is UNKNOWN
	UNKNOWN,
	// if this node should only be on Toplevel, its type is TOPLEVEL
	TOPLEVEL;

	/**
	 * The LIST and CALL types can be a list OF something.
	 * Not used ATM.
	 */
	public ArrayList<ShephongType> of = null;
}
