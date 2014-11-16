package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * Class for constant functions.
 * Example: "#+".
 * 
 * @author shephongkrewe (salz)
 *
 */
public class OpNode extends ConstantNode{

	private Character value;
	
	/**
	 * Constructor. Chains to superclass.
	 * 
	 * @param location
	 * @param value the number value
	 */
	public OpNode(ILocation location, Character value) {
		super(location);
		this.value = value;
	}
	
	public Character getValue() {
		return value;
	}

	public void setValue(Character value) {
		this.value = value;
	}

	@Override
	public String toString(){
		return "#"+String.valueOf(this.value);
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			ShephongTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkOp(this, argument);
	}

}
