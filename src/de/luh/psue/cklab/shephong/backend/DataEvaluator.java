package de.luh.psue.cklab.shephong.backend;

import java.util.ArrayList;

import de.luh.psue.cklab.shephong.dl.CallDNode;
import de.luh.psue.cklab.shephong.dl.CharDNode;
import de.luh.psue.cklab.shephong.dl.DataNode;
import de.luh.psue.cklab.shephong.dl.DataNodeTreeWalker;
import de.luh.psue.cklab.shephong.dl.FunctionDNode;
import de.luh.psue.cklab.shephong.dl.IdentDNode;
import de.luh.psue.cklab.shephong.dl.ListDNode;
import de.luh.psue.cklab.shephong.dl.NumberDNode;
import de.luh.psue.cklab.shephong.dl.OpDNode;
import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;

/**
 * This is the walker for evaluating the nodes in the interpreter.
 * 
 * @author ingo
 *
 */
public class DataEvaluator extends DataNodeTreeWalker<DataNode, Object> {

	private CompilerContext context;
	private CompilerLogger logger;

	public DataEvaluator(CompilerContext context) {
		this.context = context;
		this.logger = CompilerLogger.getInstance(this.context);
	}

	@Override
	public DataNode walkCall(CallDNode node, Object argument)
			throws CompilerException {
		while (true) {
			/*
			 * Get these from the current CallDNode
			 */
			DataNode op = node.getOp();
			DataNode param = node.getParam();
			FunTab<DataNode> env = node.getEnv();

			/*
			 * Lazy functional return an anonymous function if this CallDNode
			 * has no parameters.
			 */
			if (param == null) {
				//System.out.println("found " + node + " has no params");
				return new CallDNode(walk(op, null), new IdentDNode("$", env),
						env);
			}
			/*
			 * We need to add our parameters to a new Environment we give out.
			 * If param is null, don't add a value.
			 */
			FunTab<DataNode> evalEnv = new FunTab<DataNode>(env);
			evalEnv.addFun("$", param);

			op = node.evaluate(op, evalEnv, context);
			node.setOp(op);

			if ((op instanceof CharDNode) || (op instanceof NumberDNode)) {
				/*
				 * Basic data received, return
				 */
				return op;

			} else if (op instanceof OpDNode) {
				/*
				 * Evaluate the OpDNode here, since it can't be evaluated by
				 * super.evaluate ()
				 */
				op.setEnv(evalEnv);
				return walk(op, null);

			} else if ((op instanceof IdentDNode) || (op instanceof CallDNode)) {
				/*
				 * An IdentDNode should also be evaluated again, like OpNode
				 * Also should (to be done) anonymous functions
				 * This is simply done on the next iteration loop, so nothing to do here.
				 */

			} else if (op instanceof FunctionDNode) {
				// bail out
				logger.criticalError(Reason.Runtime,
						"evaluate() in CallDNode: " + op.toString()
								+ " can't be called");

			} else if (op instanceof ListDNode) {
				/*
				 * mapply. if a list is the operator of a callnode, call every
				 * element in the list with the parameters of this callnode.
				 */

				/*
				 * don't mapply a list of list
				 */
				if (param instanceof ListDNode) {
					return op;
				}
				
				// build a list of CallDNodes
				ArrayList<CallDNode> mapplyList = new ArrayList<CallDNode>();
				for (DataNode n : ((ListDNode) op).getContent()) {
					mapplyList.add(new CallDNode(param, n, env));
				}
				// Call them not, we are lazy
				ArrayList<DataNode> mapplyResult = new ArrayList<DataNode>();
				for (CallDNode n : mapplyList) {
					// TODO: don't call but fix output
					mapplyResult.add(walk(n, null));
				}

				return new ListDNode(mapplyResult, env);

			} else {
				logger.criticalError(Reason.Runtime,
						"evaluate() in CallDNode: illegal instance of op");
			}
		}
	}

	@Override
	public DataNode walkChar(CharDNode node, Object argument)
			throws CompilerException {
		return node;
	}

	@Override
	public DataNode walkFunction(FunctionDNode node, Object argument)
			throws CompilerException {
		/*
		 * This returns any data node *but* a FunctionDNode. That means it will
		 * work if the public evaluate() will get called and it will not be an
		 * endless recursion.
		 * 
		 * It also builds a *new* data node (tree) everytime a lookup of an
		 * ident is made. I guess this is exactly the point why we need this!
		 */
		DataWalker dw = new DataWalker(context);
		return dw.walk(node.getBody(), node.getEnv());
	}

	@Override
	public DataNode walkIdent(IdentDNode node, Object argument)
			throws CompilerException {
		return node;
	}

	@Override
	public DataNode walkList(ListDNode node, Object argument)
			throws CompilerException {
		// a list is always itself
		return node;
	}

	@Override
	public DataNode walkNumber(NumberDNode node, Object argument)
			throws CompilerException {
		return node;
	}

	@Override
	public DataNode walkOp(OpDNode node, Object argument)
			throws CompilerException {
		// TODO: some magic parsing of the ops
		// The $ in the environment should contain parameters
		if (node.getOp() == '+') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			int number1 = getNumber(paramList.get(0));
			int number2 = getNumber(paramList.get(1));
			return new NumberDNode(number1 + number2);

		} else if (node.getOp() == '-') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			int number1 = getNumber(paramList.get(0));
			int number2 = getNumber(paramList.get(1));
			return new NumberDNode(number1 - number2);

		} else if (node.getOp() == '*') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			int number1 = getNumber(paramList.get(0));
			int number2 = getNumber(paramList.get(1));
			return new NumberDNode(number1 * number2);

		} else if (node.getOp() == '/') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			int number1 = getNumber(paramList.get(0));
			int number2 = getNumber(paramList.get(1));
			return new NumberDNode(number1 / number2);

		} else if (node.getOp() == '<') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			int number1 = getNumber(paramList.get(0));
			int number2 = getNumber(paramList.get(1));
			// DebugLogger.log(number1+"<"+number2);
			if (number1 < number2) {
				return new OpDNode('^', node.getEnv());
			} else {
				// works even if you define ^_ as head+tail function
				// return new IdentDNode ("^_", this.env);
				return new OpDNode('"', node.getEnv());
			}

		} else if (node.getOp() == '=') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			int number1 = getNumber(paramList.get(0));
			int number2 = getNumber(paramList.get(1));
			// DebugLogger.log(number1+"=="+number2);
			if (number1 == number2) {
				return new OpDNode('^', node.getEnv());
			} else {
				// works even if you define ^_ as head+tail function
				// return new IdentDNode ("^_", this.env);
				return new OpDNode('"', node.getEnv());
			}

		} else if (node.getOp() == '>') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			int number1 = getNumber(paramList.get(0));
			int number2 = getNumber(paramList.get(1));
			// DebugLogger.log(number1+">"+number2);
			if (number1 > number2) {
				return new OpDNode('^', node.getEnv());
			} else {
				// works even if you define ^_ as head+tail function
				// return new IdentDNode ("^_", this.env);
				return new OpDNode('"', node.getEnv());
			}

		} else if (node.getOp() == '^') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			return paramList.get(0);

		} else if (node.getOp() == '"') {
			// This is not tail but returns the 2nd element of a list. internal
			// use only.
			// TODO: This is only list for if, so it simply returns the second
			// entry
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			ArrayList<DataNode> paramList = param.getContent();
			return paramList.get(1);

		} else if (node.getOp() == '_') {
			ListDNode param = getParamList(node);
			// Note, bounds checking missing
			@SuppressWarnings("unchecked")
			ArrayList<DataNode> paramList = (ArrayList<DataNode>) param
					.getContent().clone();
			paramList.remove(0);
			return new ListDNode(paramList, param.getEnv());
		}

		return null;
	}

	private int getNumber(DataNode node) throws CompilerException {
		while (true) {
			DataNode newNode = node.evaluate(node, node.getEnv(), context);
			if (newNode instanceof NumberDNode) {
				return ((NumberDNode) newNode).getIntValue();
			} else if (newNode instanceof ListDNode) {
				// TODO: call mappy

			} else if ((newNode instanceof CharDNode)
					|| (newNode instanceof OpDNode)
					|| (newNode instanceof FunctionDNode)) {
				logger.criticalError(Reason.Runtime,
						"OpDNode.getNumber (): could not reduce "
								+ newNode.getClass().getSimpleName() + " "
								+ newNode + " to a number");
			} else if ((newNode instanceof CallDNode)
					|| (newNode instanceof IdentDNode)) {
				// rinse, repeat
			} else {
				logger.criticalError(Reason.Runtime, "OpDNode.getNumber (): "
						+ node + " is a strange node");
			}
			node = newNode;
		}
	}

	/**
	 * Tries to evaluate the parameter until a list is returned or a simple
	 * primitive that is no longer to be expanded.
	 * 
	 * The ParamList is initially passed in the environment.
	 * 
	 * @return A ListNode containing all parameters, optionally created if only
	 *         one parameter is present. null if no parameter exists.
	 * @throws CompilerException
	 */
	private ListDNode getParamList(DataNode node) throws CompilerException {
		DataNode param = node.getEnv().lookupFunEntry("$");

		param = node.evaluate(param, param.getEnv(), context);

		if (param instanceof ListDNode) {
			return (ListDNode) param;

		} else if ((param instanceof CharDNode)
				|| (param instanceof NumberDNode) || (param instanceof OpDNode)
				|| (param instanceof IdentDNode)
				|| (param instanceof CallDNode)) {
			/*
			 * All these nodes can be arguments to a function, so put them in a
			 * list and give them back.
			 */
			return new ListDNode(param, param.getEnv());

		} else if (param instanceof FunctionDNode) {
			logger.criticalError(Reason.Runtime, "OpDNode.getParamList (): "
					+ param + " not applicable in this context");
		} else {
			logger.criticalError(Reason.Runtime, "OpDNode.getParamList (): "
					+ param + " is a strange node");
		}
		// never reached, but compiler does not know that
		return null;
	}

}
