package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * A node class for assignments.
 * 
 * @author karo of the shephongkrewe
 *
 */
public final class AssignmentNode extends ActionNode {

	private IdentNode ident;
	private ExpressionNode body;
	
	/**
	 * Constructor, chains to super class.
	 * 
	 * @param location the node's loaction.
	 * @param ident the name of the assigned function.
	 * @param body the rest of the assignment :)
	 */
	public AssignmentNode(ILocation location, IdentNode ident, ExpressionNode body) {
		super(location);
		this.ident = ident;
		this.body = body;
		if(this.body != null){
//			this.setStatic(this.body.isStatic()); // Check
			this.setContainsStatic(this.body.getContainsStatic() || this.body.isStatic());
		}
	}

	@Override
	public String toString() {
		return putInParentesis(ident.toString() + " = " + body.toString());
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			ShephongTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkAssignment(this, argument);
	}
	

	public IdentNode getIdent() {
		return ident;
	}

	public void setIdent(IdentNode ident) {
		this.ident = ident;
	}

	public ExpressionNode getExpression() {
		return body;
	}

	public void setExpression(ExpressionNode expression) {
		this.body = expression;
		this.setContainsStatic(this.body.getContainsStatic() || this.body.isStatic());
	}
}
