package de.luh.psue.cklab.shephong.il;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.il.INode;
import de.luh.psue.compiler.il.ITreeWalker;

public abstract class ShephongTreeWalker<ReturnType, ArgumentType> implements ITreeWalker<ReturnType, ArgumentType> {
	@Override
	public ReturnType walk(INode node, ArgumentType argument) throws CompilerException {
		if (node instanceof ShephongNode) return walk((ShephongNode) node, argument);
		throw new IllegalArgumentException("walker can only process shephong nodes.");
	}

	public ReturnType walk(ShephongNode node, ArgumentType argument) throws CompilerException {
		return node.walk(this, argument);
	}
	
	public abstract ReturnType walkProgram(ProgramNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkIdent(IdentNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkCall(CallNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkList(ListNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkMagic(MagicNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkAssignment(AssignmentNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkModule(ModuleNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkChar(CharNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkNumber(NumberNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkOp(OpNode node, ArgumentType argument) throws CompilerException;
	public abstract ReturnType walkParameter(ParameterNode node, ArgumentType argument) throws CompilerException;
	
}
