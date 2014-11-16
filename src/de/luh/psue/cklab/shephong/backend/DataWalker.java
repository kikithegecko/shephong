package de.luh.psue.cklab.shephong.backend;

import de.luh.psue.cklab.shephong.dl.*;
import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.helper.DebugLogger;
import de.luh.psue.cklab.shephong.il.AssignmentNode;
import de.luh.psue.cklab.shephong.il.CallNode;
import de.luh.psue.cklab.shephong.il.CharNode;
import de.luh.psue.cklab.shephong.il.IdentNode;
import de.luh.psue.cklab.shephong.il.ListNode;
import de.luh.psue.cklab.shephong.il.MagicNode;
import de.luh.psue.cklab.shephong.il.ModuleNode;
import de.luh.psue.cklab.shephong.il.NumberNode;
import de.luh.psue.cklab.shephong.il.OpNode;
import de.luh.psue.cklab.shephong.il.ParameterNode;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.cklab.shephong.il.ShephongTreeWalker;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;

/**
 * This walker makes dl code out of il code.
 * @author Shephongkrewe (killroy)
 *
 */
public class DataWalker extends ShephongTreeWalker<DataNode, FunTab<DataNode>> {

	private CompilerContext context;
	private CompilerLogger logger;
	
	public DataWalker (CompilerContext context) {
		this.context = context;
		this.logger = CompilerLogger.getInstance(this.context);
	}
	
	@Override
	public DataNode walkAssignment(AssignmentNode node, FunTab<DataNode> env)
			throws CompilerException {

		/*
		 * TODO implement it
		 */
		
		DebugLogger.log (node.toString());
		this.logger.criticalError (Reason.Runtime,
				"walkAssignment in DataWalker: " +
				"Not yet implemented");
		// TODO wrong?
		// just make a dl tree of the assignment body
		//return walk(node.getExpression(), null);
		
		// not reached
		return null;
	}

	@Override
	public DataNode walkCall(CallNode node, FunTab<DataNode> env)
			throws CompilerException {
		
		// TODO don't add null environment
		CallDNode callDNode = new CallDNode(node, env, context);
		return callDNode;
	}

	@Override
	public DataNode walkChar(CharNode node, FunTab<DataNode> env)
			throws CompilerException {
		
		// just build a basic char data node
		return new CharDNode(node, env);
	}

	@Override
	public DataNode walkIdent(IdentNode node, FunTab<DataNode> env)
			throws CompilerException {

		// just build a simple ident data node
		return new IdentDNode(node, env);
	}

	@Override
	public DataNode walkList(ListNode node, FunTab<DataNode> env)
			throws CompilerException {
		
		/*
		 * just build a list data node as a container of
		 * other data nodes
		 */
		return new ListDNode(node, env, context);
	}

	@Override
	public DataNode walkMagic(MagicNode node, FunTab<DataNode> env)
			throws CompilerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataNode walkModule(ModuleNode node, FunTab<DataNode> env)
			throws CompilerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataNode walkNumber(NumberNode node, FunTab<DataNode> env)
			throws CompilerException {
		
		// just build a simple number data node
		return new NumberDNode(node, env);
	}

	@Override
	public DataNode walkOp(OpNode node, FunTab<DataNode> env) throws CompilerException {
		
		// just build a data representation of an op node
		return new OpDNode(node, env);
	}

	@Override
	public DataNode walkParameter(ParameterNode node, FunTab<DataNode> env)
			throws CompilerException {
		// TODO Fix this in the Parser - remove ParameterNode

		// just build a simple ident data node
		return new IdentDNode(node, env);
	}

	@Override
	public DataNode walkProgram(ProgramNode node, FunTab<DataNode> env)
			throws CompilerException {
		// TODO Auto-generated method stub
		return null;
	}
}