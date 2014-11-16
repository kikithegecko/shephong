package de.luh.psue.cklab.shephong.dl;

import de.luh.psue.cklab.shephong.backend.DataWalker;
import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.il.CallNode;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;

/**
 * Representation of a function call in our
 * data language.
 * 
 * @author shephongkrewe (karo)
 *
 */
public class CallDNode extends DataNode {
	
	private DataNode op;
	private DataNode param;
	
	public CallDNode(CallNode node, FunTab<DataNode> env, CompilerContext context)
		throws CompilerException{
		
		DataWalker dw = new DataWalker(context);
		//DebugLogger.log ("\n===== new CallDNode:\n" + node + "\n=====\n");
		op = dw.walk(node.getOp(), env);
		/*
		 * Not every CallDNode has got params
		 */
		if (node.getParam () != null) {
			param = dw.walk(node.getParam(), env);
		} else {
			param = null;
		}
	
		this.setEnv (env);
	}
	/*
	 * generic constructor if all params are known
	 */
	public CallDNode(DataNode op, DataNode param, FunTab<DataNode> env) {
		this.op = op;
		this.param = param;
		this.env = env;
	}

	@Override
	public String toString () {
		return "CallDNode:"+this.op+"("+this.param+")";
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			DataNodeTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkCall(this, argument);
	}
	
	/**
	 * @return the op
	 */
	public DataNode getOp() {
		return op;
	}
	/**
	 * @param op the op to set
	 */
	public void setOp(DataNode op) {
		this.op = op;
	}
	/**
	 * @return the param
	 */
	public DataNode getParam() {
		return param;
	}
	/**
	 * @param param the param to set
	 */
	public void setParam(DataNode param) {
		this.param = param;
	}

}
