package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.input.ILocation;

/**
 * This abstract super class represents all nodes that
 * "do something", since we're really lazy. Children are
 * MagicNode and AssignmentNode.
 * 
 * @author karo of the shephongkrewe
 *
 */
public abstract class ActionNode extends ExpressionNode {

	public ActionNode(ILocation location) {
		super(location);
	}

}
