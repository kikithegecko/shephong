package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * A node class for function calls, e.g. for the
 * "f"-call in "(1 2 f)".
 * 
 * @author karo of the shephongkrewe
 *
 */
public class CallNode extends ExpressionNode {
	
	private ExpressionNode op;
	private ExpressionNode param;
	
	/**
	 * Constructor. Chains to superclass and stores
	 * values.
	 * 
	 * @param location the node's location (chained)
	 * @param op the name of the called function
	 * @param param the parameter list (mostly a ListNode)
	 */
	public CallNode(ILocation location, ExpressionNode op, ExpressionNode param) {
		super(location);
		this.op = op;
		this.param = param;
		if(this.op != null){
			this.isStatic = op.isStatic || op.getContainsStatic();	
		}
		if(this.param != null){
			super.setContainsStatic(this.op.getContainsStatic() || this.param.getContainsStatic() || this.isStatic);
		}
	}

	@Override
	public String toString() {
		if(param == null){
			return op.toString() + putInParentesis("");
		}
		else{
			return op.toString() + putInParentesis(param.toString());
		}
	}
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			ShephongTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkCall(this, argument);
	}


	public ExpressionNode getOp() {
		return op;
	}

	public void setOp(ExpressionNode op) {
		this.op = op;
		this.setContainsStatic(this.getContainsStatic() || this.op.getContainsStatic() || this.op.isStatic());
	}

	public ExpressionNode getParam() {
		return param;
	}

	public void setParam(ExpressionNode param) {
		this.param = param;
		if(this.param != null){
			this.setContainsStatic(this.getContainsStatic() || this.param.getContainsStatic() || this.param.isStatic());
		}
	}
}
