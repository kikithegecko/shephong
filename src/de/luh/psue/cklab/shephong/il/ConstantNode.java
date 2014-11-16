package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.input.ILocation;

/**
 * This node is a superclass for constants, like
 * "#4" or "'a".
 * 
 * @author karo of the shephongkrewe
 *
 */
public abstract class ConstantNode extends SimpleNode {
	
	/**
	 * Constructor. Chains to superclass.
	 * 
	 * @param location the node's location.
	 * @param value the value to be stored in the new node.
	 */
	public ConstantNode(ILocation location) {
		super(location);
		super.setStatic(false);
	}

}
