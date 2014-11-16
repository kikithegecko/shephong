package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * Represents a char constant like "'a".
 * 
 * @author karo of the shephongkrewe
 *
 */
public class CharNode extends ConstantNode {
	
	private char value;

	/**
	 * Constructor. Chains to superclass.
	 * 
	 * @param location
	 * @param value
	 */
	public CharNode(ILocation location, char value) {
		super(location);
		this.value = value;
	}
	
	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}

	@Override
	public String toString(){
		return String.valueOf(this.value); 
	}

	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			ShephongTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkChar(this, argument);
	}

}
