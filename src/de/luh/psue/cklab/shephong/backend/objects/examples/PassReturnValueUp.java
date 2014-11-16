package de.luh.psue.cklab.shephong.backend.objects.examples;

import de.luh.psue.cklab.shephong.backend.objects.ShephongCall;
import de.luh.psue.cklab.shephong.backend.objects.ShephongObject;
/**
 * This class helps to emulate multiple layers of callnodes.
 * 
 * @author Shephongkrewe(imp
 *
 */
public class PassReturnValueUp extends ShephongCall {

	public PassReturnValueUp(ShephongObject op, ShephongObject param) {
		super(op, param);
	}
	
	@Override
	public ShephongObject evaluate(ShephongObject param){
		System.out.println("bouncing the returned node up");
		/*
		 * skip all the magic to do the loop for calling this opnode until it returns something useable.
		 */
		return ((ShephongCall) super.op).evaluate(null);
	}

}
