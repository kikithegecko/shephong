package de.luh.psue.cklab.shephong.semantical;

import java.util.ArrayList;

import de.luh.psue.cklab.shephong.il.AssignmentNode;
import de.luh.psue.cklab.shephong.il.IdentNode;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.cklab.shephong.il.ShephongNode;

/**
 * Helper class to search for idents in the IL tree of a program
 * 
 * @author shephongkrewe (salz)
 *
 */
public class SearchIdentHelper {
	private ProgramNode program;
	
	public SearchIdentHelper (ProgramNode program) {
		this.program = program;
	}

	/**
	 * Searches for an Ident in the ProgramNode given by the constructor
	 * 
	 * @param identName Ident to search for
	 * @return list of Assignments for this ident. null if not defined.
	 */
	public ArrayList<ShephongNode> find(String identName) {
		ArrayList<ShephongNode> occur = new ArrayList<ShephongNode>();
		for (ShephongNode entry : program.getExpressions()) {
			if (entry instanceof AssignmentNode) {
				IdentNode id = ((AssignmentNode) entry).getIdent();
				if (id != null && id.getName().equals(identName)) {
					occur.add(entry);
				}
			}
		}
		if (occur.size() > 0) {
			return occur;
		} else {
			return null;
		}
	}
}
