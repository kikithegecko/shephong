package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.input.ILocation;

/**
 * This is the parent class for IdentNode and ConstantNode.
 * It represents the "simplest" expressions in Shephong.
 * 
 * @author karo of the shephongkrewe
 *
 */
public abstract class SimpleNode extends ExpressionNode {

	/**
	 * Chain constructor.
	 * 
	 * @param location the node's location.
	 */
	public SimpleNode(ILocation location) {
		super(location);
	}

}
