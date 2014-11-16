package de.luh.psue.cklab.shephong.backend.objects.examples;

import de.luh.psue.cklab.shephong.backend.objects.ShephongCall;
import de.luh.psue.cklab.shephong.backend.objects.ShephongNumber;
import de.luh.psue.cklab.shephong.backend.objects.ShephongObject;

/**
 * 
 * @author shephongkrewe(imp)
 *
 */
public class ExampleCallEvaluate {
	public static void main(String argv[]){
		/**
		 * The given code below equals an (unsugared) call on toplevel like:
		 * (#42 ( ... ( ...( ...( )))))
		 *                       ^
		 *                       |
		 *                     This actually returns an ident, the surrounding callnodes are just
		 *                     passing it up.
		 *  In a real world example the innermost callnode would return something like the head-operation,
		 *  and could be surrounded by lists with more operations.
		 *  Example:
		 *  (($ #^) head :)
		 *  (#42 ( (#_ print ~) ( (1 #^ ~) (('a #^ ~) (head)))))
		 *  0    1              2          3          4
		 *  Evaluating the callnode 0, the CallNode object will discover, it's op is also an CallNode,
		 *  so it'll start evaluating 1.
		 *  1 contains also an CallNode as op, therefore calling evaluate from this node as well and so on.
		 *  Note: if there's an CallNode found as Op, no parameterlist will be passed to it (at least not the 
		 *  one found inside the actual callnode.
		 *  Example
		 *  (a (b c)). 'a' is the param to (b c) or to be more exact, 'a' is the parameter to the result of (b c).
		 *  Evaluating (b c), 'b' is passed a parameter to 'c' (which will maybe, or maybe not) return an ShephongIdent,
		 *  which then will be evaluated with the parameter 'a' 
		 */
		new ShephongCall(
				new PassReturnValueUp(
						new PassReturnValueUp(
								new PassReturnValueUp( 
										new DummyNestedCallEmulator(
												null, 
												null),
										null),
								null),
						null),
				new ShephongNumber(42)).evaluate(null); /* note: we are passing null as argument on toplevel,
														 * causing the callnode to use it's own parameter to
														 * determine it's own returnvalue. */
	}
	
	public static ShephongObject print(ShephongObject paramlist){
		System.out.println("the param is: " + ((ShephongNumber) paramlist).getValue());
		return null;
	}
}
