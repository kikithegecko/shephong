package de.luh.psue.cklab.shephong.semantical;

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
import de.luh.psue.cklab.shephong.il.ShephongNode;
import de.luh.psue.cklab.shephong.il.ShephongTreeWalker;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;

/**
 * Uses the interfered type information to try to detect some semantical errors.
 * Also catches all errors that the ByteCodeGenerator would have to throw otherwise.
 * 
 * @author shephongkrewe (salz)
 *
 */
public class SemanticalErrorWalker extends
		ShephongTreeWalker<Object, Object> {
	
	private CompilerContext context;
	private CompilerLogger logger;
	
	public SemanticalErrorWalker(CompilerContext context) {
		this.context = context;
		this.logger = CompilerLogger.getInstance(this.context);
	}

	@Override
	public Object walkAssignment(AssignmentNode node, Object argument)
			throws CompilerException {
		walk(node.getIdent(), argument);
		walk(node.getExpression(), argument);

		return null;
	}

	@Override
	public Object walkCall(CallNode node, Object argument)
			throws CompilerException {
		walk(node.getOp(), argument);
		if (node.getParam() != null) {
			walk(node.getParam(), argument);
		}

		/*
		 * OK:  Op == OpNode | IdentNode | CallNode
		 * N/I: Op == ListNode
		 * ERR: Op == AssignmentNode | CharNode | MagicNode |
		 *            ModuleNode | NumberNode | ParameterNode |
		 *            ProgramNode
		 */
		ShephongNode op = node.getOp();
		ShephongType opType = op.getType();

		// Semantical Analysis
		if (opType == ShephongType.LIST) {
			// this is our mapply case: (PARAM (...~))
			// FIXME: killroy: Not yet implemented in optimized == true
			//this.logger.criticalError(Reason.Semantical, node.getLocation(),
			//		"mapply not yet implemented");
		} else if (opType == ShephongType.CHAR) {
			this.logger.criticalError(Reason.Semantical, node.getLocation(),
					"Can't call a Char");
		} else if (opType == ShephongType.NUMBER) {
			this.logger.criticalError(Reason.Semantical, node.getLocation(),
					"Can't call a Number");
		} else if (opType == ShephongType.JOBJECT) {
			this.logger.criticalError(Reason.Semantical, node.getLocation(),
					"Can't call a JavaObject");
			
		// Syntactical Analysis fallback
		} else if (op instanceof OpNode
				|| op instanceof IdentNode
				|| op instanceof CallNode
				|| op instanceof ListNode) {
			// These are processed by the ByteCodeGenerator
		} else if (op instanceof ParameterNode) {
			// FIXME: killroy: This node is not handeled in the ByteCodeGenerater if optimize == true
		} else {
			this.logger.criticalError(Reason.Semantical, node.getLocation(),
					"Can't call a " + op.getClass().getSimpleName());
		}
		return null;
	}

	@Override
	public Object walkChar(CharNode node, Object argument)
			throws CompilerException {
		return null;
	}

	@Override
	public Object walkIdent(IdentNode node, Object argument)
			throws CompilerException {
		return null;
	}

	@Override
	public Object walkList(ListNode node, Object argument)
			throws CompilerException {
		for (ExpressionNode sibling : node.getContent()) {
			walk(sibling, argument);
		}

		return null;
	}

	@Override
	public Object walkMagic(MagicNode node, Object argument)
			throws CompilerException {
		walk(node.getParam(), argument);

		// These should only be on toplevel and disappear completely after desugar
		return null;
	}

	@Override
	public Object walkModule(ModuleNode node, Object argument)
			throws CompilerException {
		this.logger.criticalError(Reason.Semantical, node.getLocation(),
				"Module nodes are not yet implemented.");
		return null;
	}

	@Override
	public Object walkNumber(NumberNode node, Object argument)
			throws CompilerException {
		return null;
	}

	@Override
	public Object walkOp(OpNode node, Object argument) throws CompilerException {
		// Test the OpString for the ops in the StdLib
		String operator = node.getValue().toString();
		if (! "+-*/%&|!=<>^_\"@?\\cn".contains(operator)) {
			this.logger.criticalError(Reason.Semantical, node.getLocation(),
					"The op " + operator + " does not implement a basic operation.");
		}
		return null;
	}

	@Override
	public Object walkParameter(ParameterNode node, Object argument)
			throws CompilerException {
		return null;
	}

	@Override
	public Object walkProgram(ProgramNode node, Object argument)
			throws CompilerException {
		for (ExpressionNode sibling : node.getExpressions()) {
			walk(sibling, argument);
		}

		return null;
	}

}
