package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * This node class represents a "module", an external
 * lib or program whose definitions are to be included
 * in the "main" programm.
 * 
 * @author karo
 *
 */
public final class ModuleNode extends ExpressionNode{

	private String name;
	
	/**
	 * Constructor. Chains to super class and sets the
	 * module name
	 * 
	 * @param location the location.
	 * @param name the module name.
	 */
	public ModuleNode(ILocation location, String name) {
		super(location);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "module(" + name + ")"; //TODO implement
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			ShephongTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkModule(this, argument);
	}
	
}
