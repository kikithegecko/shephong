package de.luh.psue.cklab.shephong.backend;

import de.luh.psue.cklab.shephong.dl.CallDNode;
import de.luh.psue.cklab.shephong.dl.DataNode;
import de.luh.psue.cklab.shephong.dl.FunctionDNode;
import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.il.AssignmentNode;
import de.luh.psue.cklab.shephong.il.CallNode;
import de.luh.psue.cklab.shephong.il.CharNode;
import de.luh.psue.cklab.shephong.il.ExpressionNode;
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

public class Interpreter extends ShephongTreeWalker<Object, Object>{
	
	private CompilerContext context;
	private CompilerLogger logger;
	private FunTab<DataNode> env;
	

	/**
	 * @param context
	 * @param logger
	 * @param env
	 * @param magicTab
	 */
	public Interpreter(CompilerContext context) {
		this.context = context;
		this.logger = CompilerLogger.getInstance(this.context);
		this.env = new FunTab<DataNode>(this.context);
	}
	
	/**
	 * The init method to call from the outside to make the
	 * Interpreter interpret a program.
	 * @param il The program (ProgramNode) to interpret
	 * @throws CompilerException
	 */
	public void interpret(ProgramNode il) throws CompilerException {

		// TODO: better output - atm it only prints the return of expressions
		
		// simply walk all top level nodes from the program
		for (ExpressionNode node : il.getExpressions()) {
			DataNode result = (DataNode) walk (node, null);
			
			// don't print out assignments
			if (!(node instanceof AssignmentNode)) {
				System.out.println (result);
			}
		}
		
		// show FunTab
		// System.out.println(this.env);
	}

	@Override
	public Object walkAssignment(AssignmentNode node, Object argument)
			throws CompilerException {
		
		// create new FunctionDNode and put it into our top level FunTab
		// The FunctionDNode only contains the expression of the assignment
		FunctionDNode functionDNode = new FunctionDNode(node.getExpression (), this.env);
		this.env.addFun(node.getIdent().getName(), functionDNode);
		
		return functionDNode; // just to print it in interpret()
	}

	@Override
	public Object walkCall(CallNode node, Object argument)
			throws CompilerException {

		// build a new call data node and give our top level FunTab to it
		CallDNode callDNode = new CallDNode(node, this.env, context);
		
		// evaluate this call because we want a basic result!
		return new DataEvaluator(context).walk (callDNode, null); // return it just because to print it
	}

	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * The following lines are not important.
	 * They are just here because a subclass must implement *all*
	 * methods from its parent.
	 * They just throw an error if they get called because they
	 * never should get called :)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	
	
	
	@Override
	public Object walkChar(CharNode node, Object argument)
			throws CompilerException {
		
		// chars on top level can't be 
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
			"Found CharNode on top level. This can't be!");
		return null;
	}

	@Override
	public Object walkIdent(IdentNode node, Object argument)
			throws CompilerException {
		
		// idents on top level can't be 
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
			"Found IdentNode on top level. This can't be!");
		return null;
	}

	@Override
	public Object walkList(ListNode node, Object argument)
			throws CompilerException {
		
		// lists on top level can't be 
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
			"Found ListNode on top level. This can't be!");
		return null;
	}

	@Override
	public Object walkMagic(MagicNode node, Object argument)
			throws CompilerException {
		
		// MagicNodes on top level while interpreting can't be 
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
			"Found MagicNode while interpreting. This can't be!");
		return null;
	}

	@Override
	public Object walkModule(ModuleNode node, Object argument)
			throws CompilerException {
		

		// we do not handle modules yet 
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
			"Interpreting ModuleNodes is not yet implemented");
		return null;
	}

	@Override
	public Object walkNumber(NumberNode node, Object argument)
			throws CompilerException {
		
		// basic numbers on top level can't be 
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
			"Found NumberNode on top level. This can't be!");
		return null;
	}

	@Override
	public Object walkOp(OpNode node, Object argument) throws CompilerException {
		
		// basic operators on top level can't be 
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
			"Found OpNode on top level. This can't be!");
		return null;
	}

	@Override
	public Object walkParameter(ParameterNode node, Object argument)
			throws CompilerException {
		
		// parameter on top level can't be 
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
			"Found ParameterNode on top level. This can't be!");
		return null;
	}
	
	@Override
	public Object walkProgram(ProgramNode node, Object argument)
			throws CompilerException {
		
		// we do not interpret ProgramNodes in ProgramNodes yet
		this.logger.criticalError(Reason.Runtime, node.getLocation(),
				"Found ProgramNode while interpreting another one");
		return null;
	}
}