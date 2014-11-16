package de.luh.psue.cklab.shephong.backend;

import de.luh.psue.cklab.shephong.il.AssignmentNode;
import de.luh.psue.cklab.shephong.il.CallNode;
import de.luh.psue.cklab.shephong.il.CharNode;
import de.luh.psue.cklab.shephong.il.IdentNode;
import de.luh.psue.cklab.shephong.il.ListNode;
import de.luh.psue.cklab.shephong.il.MagicNode;
import de.luh.psue.cklab.shephong.il.ModuleNode;
import de.luh.psue.cklab.shephong.il.NumberNode;
import de.luh.psue.cklab.shephong.il.OpNode;
import de.luh.psue.cklab.shephong.il.ParameterNode;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.cklab.shephong.il.ShephongTreeWalker;
import de.luh.psue.compiler.error.CompilerException;

/**
 * This class dumps the program tree as mostly readable text.
 * 
 * @author shephongkrewe (salz)
 *
 */
public class ToStringWalker extends
		ShephongTreeWalker<String, Object> {
	private StringBuilder buffer;
	private int indentDepth;
	private boolean indented;
	
	public ToStringWalker(){
		this.buffer = new StringBuilder ();
		this.indentDepth = 0;
		this.indented = true;
	}
	
	// Walker functions for every kind of node
	
	@Override
	public String walkProgram(ProgramNode node, Object argument)
			throws CompilerException {
		this.buffer = new StringBuilder ();
		this.indentDepth = 0;
		this.indented = true;
		
		// Module name, if exists
		if (node.getName () != null) {
			this.buffer.append (";name = ");
			this.buffer.append (node.getName ());
			this.buffer.append (";\n");
		}
		
		/*
		 * Add recorded functions#
		 *
		 * FIXME: Do not add the first expressions which belong to the stdlib.
		 * Get this number from the length of the stdlib list.
		 * This is probably not the best solution?
		 */
		for (int i = GenerateStdLib.getStdLib().size(); i < node.getExpressions ().size (); i++) {
			walk (node.getExpressions ().get (i), argument);
		}
		
		this.buffer.append ("\n");

		return buffer.toString();
	}

	@Override
	public String walkIdent(IdentNode node, Object argument)
			throws CompilerException {
		// indent after an identifier, if another identifier/constant follows
		addIndentation ();
		this.buffer.append (node.getName ());
		this.indented = false;
		
		return this.buffer.toString();
	}
	
	@Override
	public String walkNumber(NumberNode node, Object argument)
			throws CompilerException {
		// indent after a constant, if another identifier/constant follows
		addIndentation ();
		this.buffer.append ("#");
		this.buffer.append (node.getValue());
		this.indented = false;

		return this.buffer.toString();
	}

	@Override
	public String walkOp(OpNode node, Object argument)
			throws CompilerException {
		// indent after a constant, if another identifier/constant follows
		addIndentation ();
		this.buffer.append ("#");
		this.buffer.append (node.getValue());
		this.indented = false;

		return this.buffer.toString();
	}

	@Override
	public String walkChar(CharNode node, Object argument)
			throws CompilerException {
		// indent after a constant, if another identifier/constant follows
		addIndentation ();
		this.buffer.append ("'");
		this.buffer.append (node.getValue());
		this.indented = false;

		return this.buffer.toString();
	}

	@Override
	public String walkCall(CallNode node, Object argument)
			throws CompilerException {
		addIndentation ();

		// Test if this CallNode was a number
		String number = parseNumber(node);
		if (number != null) {
			this.buffer.append(number);
			this.indented = false;
			return this.buffer.toString();
		}

		// This is a simple list of expressions.
		openBracket (node.isStatic());
		if (node.getParam () != null) {
			walk (node.getParam (), argument);
		}
		walk (node.getOp (), argument);
		closeBracket (node.isStatic());
		this.indented = false;
		
		return this.buffer.toString();
	}

	/*
	 * Tries to parse a number in the format (((#0 1) 2) 3) => 123
	 * Returns null if it fails
	 * 
	 * TODO: does not work on "hex" notation like 0deadbeef
	 */
	private String parseNumber(CallNode node) {
		String digit;
		
		// The Op of this CallNode must be an IdentNode
		if (!(node.getOp() instanceof IdentNode)) {
			return null;
		}
		
		// The Identifiers Value must be one digit
		digit = ((IdentNode) node.getOp()).getName();
		if (digit.length() > 1
				|| ! "0123456789".contains(digit)) {
			return null;
		}
		
		// The param of this node must either be another CallNode,
		// in which case it is parsed recursively
		if (node.getParam() instanceof CallNode) {
			String numPart = parseNumber((CallNode) node.getParam());
			// When the parsing succeeds, this CallNode and siblings represent a valid number
			if (numPart != null) {
				return numPart + digit;
			}
		// The param may also be the number #0, in which case we reached the beginning of the number.
		} else if (node.getParam() instanceof NumberNode) {
			if (((NumberNode) node.getParam()).getValue() == 0) {
				return digit;
			}
		}
		
		// in all other cases return null and let the CallNode proceed
		return null;
	}

	@Override
	public String walkList(ListNode node, Object argument)
			throws CompilerException {
		// a list ends in ~)
		addIndentation ();
		openBracket (node.isStatic());
		// process the list
		for (int i = node.getContent ().size () - 1; i >= 0; i--) {
			walk (node.getContent ().get (i), argument);
		}
		this.buffer.append (" ~");
		closeBracket (node.isStatic());
		this.indented = false;
		
		return this.buffer.toString();
	}

	@Override
	public String walkMagic(MagicNode node, Object argument)
			throws CompilerException {
		// MagicNodes are marked with `)
		addIndentation ();
		openBracket (node.isStatic ());
		walk (node.getParam (), argument);
		this.buffer.append (" `");
		closeBracket (node.isStatic ());
		this.indented = false;
		
		return this.buffer.toString();
	}

	@Override
	public String walkAssignment(AssignmentNode node, Object argument)
			throws CompilerException {
		// Assignments end in :) smiley :]
		addIndentation ();
		openBracket (node.isStatic());
		walk (node.getExpression (), argument);
		walk (node.getIdent (), argument);
		this.buffer.append (" :");
		closeBracket (node.isStatic());
		this.indented = false;
		
		return this.buffer.toString();
	}

	@Override
	public String walkModule(ModuleNode node, Object argument)
			throws CompilerException {
		addIndentation ();
		openBracket (node.isStatic ());
		this.buffer.append (";include ");
		this.buffer.append (node.getName ());
		this.buffer.append (";");
		closeBracket (node.isStatic ());
		this.indented = false;
		
		return this.buffer.toString();
	}
	
	@Override
	public String walkParameter (ParameterNode node, Object argument)
			throws CompilerException {
		// indent after an parameter/paramlist, if another identifier/constant follows
		addIndentation ();
		this.buffer.append ('$');
		if (!node.isParamList ()) {
			this.buffer.append (node.getParamNum ());
		}
		this.indented = false;
		
		return this.buffer.toString();
	}
	
	/**
	 * The depth of the indentation is incremented for every
	 * level of brackets. Every identifier and constant should
	 * be in its own line to be readable. 
	 */
	private void addIndentation () {
		if (!this.indented) {
			this.buffer.append ("\n");
			for (int i = 0; i < this.indentDepth; i++) {
				this.buffer.append (" ");
			}
		}
		this.indented = true;
	}
	
	/**
	 * This puts an open bracket into the output. It also increases the
	 * indentation level by one an puts the bracket on a new line if
	 * neccessary.
	 * 
	 * @param isStatic set this to true to use straight brackets, false for round brackets.
	 */
	private void openBracket (boolean isStatic) {
		addIndentation ();
		if (isStatic) {
			this.buffer.append ("[");
		} else {
			this.buffer.append ("(");
		}
		this.indentDepth ++;
	}
	
	/**
	 * This puts a closed bracket into the buffer.
	 * 
	 * @param isStatic set this to true to use straight brackets, false for round brackets.
	 */
	private void closeBracket (boolean isStatic) {
		if (isStatic) {
			this.buffer.append ("]");
		} else {
			this.buffer.append (")");
		}
		this.indentDepth --;
	}

	// -------------------------------
	// getters and setters boilercode
	
	public StringBuilder getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuilder buffer) {
		this.buffer = buffer;
	}

	public int getIndentDepth() {
		return indentDepth;
	}

	public void setIndentDepth(int indentDepth) {
		this.indentDepth = indentDepth;
	}

	public boolean isBracketOpened() {
		return indented;
	}

	public void setBracketOpened(boolean bracketOpened) {
		this.indented = bracketOpened;
	}

}
