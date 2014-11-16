package de.luh.psue.cklab.shephong.dl;

import de.luh.psue.cklab.shephong.backend.DataEvaluator;
import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;

/**
 * This is an abstract superclas for the data language 
 * needed for lazy evaluation in Shephong. 
 * 
 * @author shephongkrewe (karo)
 *
 */
public abstract class DataNode {

	protected FunTab<DataNode> env;

	/**
	 * Evaluates a DataNode by looking up IdentDNodes and CallDNodes
	 * until we fail to look it up in the environment or a ListDNode
	 * is reached.
	 * 
	 * Called as super.evaluate from the nodes :)
	 * 
	 * @return Something Lazy.
	 * @throws CompilerException
	 */
	public DataNode evaluate(DataNode node, FunTab<DataNode> evalEnv, CompilerContext context) throws CompilerException
	{
		while (true) {
			//DebugLogger.log ("\n"+node.toString () + "\n" + evalEnv.toString ());
				
			if        (node instanceof CharDNode) {
				/*
				 * A CharDNode has no parameters. If any are present just ignore them.
				 */
				return node;
				
			} else if (node instanceof NumberDNode) {
				/*
				 * A NumberDNode has no parameters, too.
				 * 123 is ((1 2) 3) and no NumberDNode, #123 is the NumberDNode 123.
				 */
				return node;
				
			} else if (node instanceof OpDNode) {
				/*
				 * The OpNode should do magic evaluations of the parameters,
				 * since it contains a basic operator.
				 * But this must be done in the CallDNode.
				 */
//				node.setEnv (evalEnv);
//				node = ((OpDNode) node).evaluate();
				return node;
				
			} else if (node instanceof FunctionDNode) {
				/*
				 * The evaluation of a FunctionDNode gives the corresponding
				 * DataNode tree from the FunTab.
				 */
				node.setEnv (evalEnv);
				node = new DataEvaluator(context).walk(node, null);
				
			} else if (node instanceof IdentDNode) {
				/*
				 * Lookup the IdentDNode in the environment and
				 * use as new operator. Bail if not found.
				 * 
				 * $ must be looked up in its own environment.
				 */
				DataNode newNode;
				
				if (((IdentDNode) node).getName () == "$") {
					newNode = node.env.lookupFunEntry (((IdentDNode) node).getName ());
				} else {
					newNode = this.env.lookupFunEntry(((IdentDNode) node).getName());
				}
				if (newNode == null) {
					// just lazy return the IdentDNode if not found, caller has to handle it
					return node;
				}
				
				/*
				 * Don't know what we got from the FunTab, it might be a FunctionDNode
				 * This will get processed in the next iteration.
				 */
				node = newNode;
				
			} else if (node instanceof CallDNode) {
				/*
				 * Finally we got a CallDNode.
				 * So eval it using our evalEnv.
				 * 
				 * This means we first change its env an then we eval it.
				 */
				node.setEnv (evalEnv);

				/*
				 * The result of the evaluation is the result to pass up.
				 * TODO: We could do tail recursion here.
				 * TODO: This is not lazy. evaluate alap!
				 */
				return new DataEvaluator(context).walk(node, null);

			} else if (node instanceof ListDNode) {
				/*
				 * ListDNodes are lazy, so return.
				 * Their true meaning are decided by the caller,
				 * e.g. mapply as op of a CallDNode.
				 */
				return node;
				
			} else {
				CompilerLogger.getInstance(context).criticalError (Reason.Runtime,
						"evaluate() in CallDNode: illegal instance of op");
			}
		}
	}
	
	public abstract <ReturnType, ArgumentType>  ReturnType walk(DataNodeTreeWalker<ReturnType, ArgumentType> walker, ArgumentType argument) throws CompilerException;

	public FunTab<DataNode> getEnv() {
		return env;
	}

	public void setEnv (FunTab<DataNode> env) {
		this.env = env;
	}
	
}
