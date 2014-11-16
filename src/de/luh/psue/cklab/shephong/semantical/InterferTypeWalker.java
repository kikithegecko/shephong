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
import de.luh.psue.cklab.shephong.il.ShephongTreeWalker;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;

// TODO: better semantical analysis

/**
 * This Walker tries to interfer the return types of the IL nodes it walks.
 * 
 * @author shephongkrewe (salz)
 * 
 */
public class InterferTypeWalker extends
		ShephongTreeWalker<ShephongType, Object> {

	private CompilerContext context;
	private CompilerLogger logger;
	private ProgramNode program = null;
	private SearchIdentHelper idents = null;
	
	public InterferTypeWalker(CompilerContext context) {
		this.context = context;
		this.logger = CompilerLogger.getInstance(this.context);
	}
	
	@Override
	public ShephongType walkAssignment(AssignmentNode node, Object argument)
			throws CompilerException {
		if (node.getIdent() == null) {
			logger.criticalError(Reason.Unknown, node.getLocation(),
					"Assignment without identifier is impossible");
		} else if (node.getExpression() == null) {
			logger.criticalError(Reason.Semantical, node.getLocation(),
					"Assignment must have an expression");
		}
		walk(node.getIdent(), argument);
		walk(node.getExpression(), argument);

		node.setType(ShephongType.TOPLEVEL);
		return node.getType();
	}

	@Override
	public ShephongType walkCall(CallNode node, Object argument)
			throws CompilerException {
		if (node.getOp() == null) {
			logger.criticalError(Reason.Unknown, node.getLocation(),
					"Call with an empty operand is impossible");
		}
		walk(node.getOp(), argument);
		if (node.getParam() != null) {
			walk(node.getParam(), argument);
		}

		node.setType(ShephongType.UNKNOWN);
		return node.getType();
	}

	@Override
	public ShephongType walkChar(CharNode node, Object argument)
			throws CompilerException {

		node.setType(ShephongType.CHAR);
		return node.getType();
	}

	@Override
	public ShephongType walkIdent(IdentNode node, Object argument)
			throws CompilerException {
		if (this.idents != null // if we were called from a subtree, ident checking is not possible
				&& this.idents.find(node.getName()) == null) {
			logger.criticalError(Reason.Semantical, node.getLocation(),
					"Identifier " + node.getName() + " never defined in the program.");
		}
		node.setType(ShephongType.UNKNOWN);
		return node.getType();
	}

	@Override
	public ShephongType walkList(ListNode node, Object argument)
			throws CompilerException {
		for (ExpressionNode sibling : node.getContent()) {
			if (sibling == null) {
				logger.criticalError(Reason.Unknown, node.getLocation(),
						"A list with an empty slot is impossible");
			}
			walk(sibling, argument);
		}

		node.setType(ShephongType.LIST);
		return node.getType();
	}

	@Override
	public ShephongType walkMagic(MagicNode node, Object argument)
			throws CompilerException {
		if (node.getParam() == null) {
			logger.criticalError(Reason.Semantical, node.getLocation(),
					"A magic node needs an argument");
		}
		walk(node.getParam(), argument);

		node.setType(ShephongType.TOPLEVEL);
		return node.getType();
	}

	@Override
	public ShephongType walkModule(ModuleNode node, Object argument)
			throws CompilerException {

		node.setType(ShephongType.TOPLEVEL);
		return node.getType();
	}

	@Override
	public ShephongType walkNumber(NumberNode node, Object argument)
			throws CompilerException {

		node.setType(ShephongType.NUMBER);
		return node.getType();
	}

	@Override
	public ShephongType walkOp(OpNode node, Object argument)
			throws CompilerException {

		node.setType(ShephongType.UNKNOWN);
		return node.getType();
	}

	@Override
	public ShephongType walkParameter(ParameterNode node, Object argument)
			throws CompilerException {

		node.setType(ShephongType.UNKNOWN);
		return node.getType();
	}

	@Override
	public ShephongType walkProgram(ProgramNode node, Object argument)
			throws CompilerException {
		/*
		 * Save this ProgramNode in the Walker. This breaks if the IL tree
		 * contains a second ProgramNode, and that should be considered an error.
		 */
		if (this.program == null) {
			this.program = node;
		    this.idents = new SearchIdentHelper (this.program);
		} else {
			/*
			 * Is this correct if we implement modules? If yes, change accordingly.
			 */
			logger.criticalError(Reason.Semantical, node.getLocation(),
					"A program node should only be once in the IL tree");
		}
		
		for (ExpressionNode sibling : node.getExpressions()) {
			if (sibling == null) {
				logger.criticalError(Reason.Unknown, node.getLocation(),
						"A list with an empty slot is impossible");
			}
			walk(sibling, argument);
		}

		node.setType(ShephongType.TOPLEVEL);
		return node.getType();
	}

}
