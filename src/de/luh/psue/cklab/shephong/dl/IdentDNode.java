package de.luh.psue.cklab.shephong.dl;

import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.il.IdentNode;
import de.luh.psue.cklab.shephong.il.ParameterNode;
import de.luh.psue.compiler.error.CompilerException;

public class IdentDNode extends DataNode {
	
	private String name;
	
	public IdentDNode(IdentNode node, FunTab<DataNode> env) {
		this.name = node.getName();
		this.setEnv(env);
	}
	
	/*
	 * Overloaded Constructor for ParameterNode
	 */
	public IdentDNode(ParameterNode node, FunTab<DataNode> env) {
		this.name = "$";
		this.setEnv(env);
	}

	/*
	 * generic constructor, walker use
	 */
	public IdentDNode (String name, FunTab<DataNode> env) {
		this.name = name;
		this.env = env;
	}

	@Override
	public String toString () {
		return "IdentDNode:" + name;
	}

	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			DataNodeTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkIdent(this, argument);
	}

	public String getName() {
		return this.name;
	}

}
