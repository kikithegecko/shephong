package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * Class for number constant representation.
 * Example: "#123".
 * 
 * @author karo of the shephongkrewe
 *
 */
public class NumberNode extends ConstantNode{

	private int value;
	
	/**
	 * Constructor. Chains to superclass.
	 * 
	 * @param location
	 * @param value the number value
	 */
	public NumberNode(ILocation location, int value) {
		super(location);
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
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
		return walker.walkNumber(this, argument);
	}

}
