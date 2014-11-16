package de.luh.psue.cklab.shephong.dl;

import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.il.OpNode;
import de.luh.psue.compiler.error.CompilerException;

public class OpDNode extends DataNode {
	
	private char op;

	public OpDNode(OpNode node, FunTab<DataNode> env) {
		this.op = node.getValue();
		this.setEnv (env);
	}

	public OpDNode (char op, FunTab<DataNode> env) {
		this.op = op;
		this.env = env;
	}
	
	@Override
	public String toString () {
		return "OpNode:#"+op;
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			DataNodeTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkOp(this, argument);
	}
	
	public char getOp () {
		return this.op;
	}
	
	public void setOp (char op) {
		this.op = op;
	}

}
