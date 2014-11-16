package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.input.ILocation;

/**
 * A super class for all types of Shephong expressions
 * namely actions (assignments and "magic" calls), "ordinary" 
 * function calls, lists and Simple expressions (constants,
 * identifier).  
 * 
 * @author karo of the shephongkrewe
 *
 */
public abstract class ExpressionNode extends ShephongNode {

	public ExpressionNode(ILocation location) {
		super(location);
	}
	
}
