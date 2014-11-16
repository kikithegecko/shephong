package de.luh.psue.cklab.shephong.dl;

import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.il.CharNode;
import de.luh.psue.compiler.error.CompilerException;

public class CharDNode extends DataNode {
	
	private char charValue;
	
	public CharDNode(CharNode node, FunTab<DataNode> env) {
		this.charValue = node.getValue();
		this.setEnv (env);
	}

	@Override
	public String toString () {
		return "CharDNode:'" + this.charValue;
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			DataNodeTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkChar(this, argument);
	}

	public char getCharValue() {
		return this.charValue;
	}

}
