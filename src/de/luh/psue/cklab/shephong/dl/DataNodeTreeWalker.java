package de.luh.psue.cklab.shephong.dl;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.il.INode;
import de.luh.psue.compiler.il.ITreeWalker;

public abstract class DataNodeTreeWalker<ReturnType, ArgumentType> implements ITreeWalker<ReturnType, ArgumentType> {
	@Override
	public ReturnType walk(INode node, ArgumentType argument) throws CompilerException {
		if (node instanceof DataNode) return walk((DataNode) node, argument);
		throw new IllegalArgumentException("walker can only process shephong nodes.");
	}

	public ReturnType walk(DataNode node, ArgumentType argument) throws CompilerException {
		return node.walk(this, argument);
	}
	
	public abstract ReturnType walkFunction(FunctionDNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkCall(CallDNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkList(ListDNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkIdent(IdentDNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkOp(OpDNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkChar(CharDNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkNumber(NumberDNode node, ArgumentType argument) throws CompilerException;
	
}
