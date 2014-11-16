package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * A node class for our "magic operations" such as eval "`"
 * or print.
 * 
 * @author karo of the shephongkrewe
 *
 */
public final class MagicNode extends ActionNode {

		private ExpressionNode param;
	
	/**
	 * Constructor. Chains to super class.
	 * 
	 * @param location the node's location.
	 * @param param the call parameter(s).
	 */
	public MagicNode(ILocation location, ExpressionNode param) {
		super(location);
		this.param = param;
		if(this.param != null){
			this.setContainsStatic(this.param.getContainsStatic() || this.param.isStatic());
		}
	}

	public ExpressionNode getParam() {
		return param;
	}

	public void setParam(ExpressionNode param) {
		this.param = param;
		this.setContainsStatic(this.param.getContainsStatic() || this.param.isStatic());
	}

	@Override
	public String toString() {
		return "`" + putInParentesis(param.toString());
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			ShephongTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkMagic(this, argument);
	}
}