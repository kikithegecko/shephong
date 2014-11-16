package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * This node class is for parameters and parameterlists.
 * It saves the function it belongs to as String.
 * 
 * @author shephongkrewe (salz)
 *
 */
public final class ParameterNode extends SimpleNode {

	private int paramNum;
	
	/**
	 * Constructor. Chains to superclass.
	 * 
	 * @param location the node's location.
	 * @param ref
	 * 			the referenced function
	 * @param num
	 * 			the number of the parameter in the list.
	 * 			0 means it is is the whole paramList.
	 */
	public ParameterNode(ILocation location, int num) {
		super(location);
		this.paramNum = num;
	}
	
	@Override
	public String toString() {
		if (this.isParamList ()) {
			return "$";
		} else {
			return "$" + this.paramNum;
		}
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(ShephongTreeWalker<ReturnType, ArgumentType> walker, ArgumentType argument) throws CompilerException{
		return walker.walkParameter(this, argument);
	}

	public boolean isParamList () {
		return this.paramNum == 0;
	}

	/**
	 * @return the paramNum
	 */
	public int getParamNum() {
		return paramNum;
	}

	/**
	 * @param paramNum the paramNum to set
	 */
	public void setParamNum(int paramNum) {
		this.paramNum = paramNum;
	}
}
