package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * This node class is for identifiers, for example a "f"
 * as it occurs in (f a).
 * 
 * @author karo of the shephongkrewe
 *
 */
public final class IdentNode extends SimpleNode {

	private String name;
	
	/**
	 * Constructor. Chains to superclass.
	 * 
	 * @param location the node's location.
	 * @param name the name of the identifier to be 
	 * stored in the node.
	 */
	public IdentNode(ILocation location, String name) {
		super(location);
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(ShephongTreeWalker<ReturnType, ArgumentType> walker, ArgumentType argument) throws CompilerException{
		return walker.walkIdent(this, argument);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
