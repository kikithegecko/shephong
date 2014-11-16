package de.luh.psue.cklab.shephong.dl;

import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.il.NumberNode;
import de.luh.psue.compiler.error.CompilerException;

public class NumberDNode extends DataNode {

	private Integer intValue;
	
	public NumberDNode(NumberNode node, FunTab<DataNode> env) {
		this.intValue = node.getValue();
		this.setEnv (env);
	}
	public NumberDNode (int value) {
		this.intValue = value;
	}
	
	@Override
	public String toString () {
		return intValue.toString ();
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			DataNodeTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkNumber(this, argument);
	}

	public int getIntValue() {
		return this.intValue;
	}

}
