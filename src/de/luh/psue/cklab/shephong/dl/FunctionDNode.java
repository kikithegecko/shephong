package de.luh.psue.cklab.shephong.dl;

import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.il.ShephongNode;
import de.luh.psue.compiler.error.CompilerException;

/**
 * This encapsulates the IL-body of an assignment and gets placed
 * in the FunTab.
 * @author Shephongkrewe (killroy)
 *
 */
public class FunctionDNode extends DataNode {
	
	private ShephongNode body;
	
	public FunctionDNode(ShephongNode body, FunTab<DataNode> env){
		this.body = body;
		this.setEnv (env);
	}

	@Override
	public String toString() {
		// just show String representation of the IL body
		return "FunctionDNode:" + this.body.toString();
	}

	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			DataNodeTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkFunction(this, argument);
	}
	
	public ShephongNode getBody() {
		return this.body;
	}

}
