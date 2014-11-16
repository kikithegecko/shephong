package de.luh.psue.cklab.shephong.backend.desugar;

import java.util.ArrayList;

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
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * Change ParamNodes to only use the ParamList in the code, not
 * the individual parameters, by changing eg. $3 to ((($ _) _) ^)
 * 
 * @author shephongkrewe (salz, imp)
 *
 */
public class DeSugarParam extends ShephongTreeWalker<ShephongNode, Object> {

	@Override
	public ShephongNode walkParameter(ParameterNode node, Object argument)
			throws CompilerException {
		int paramNum = node.getParamNum ();
		if (paramNum == 0) {
			// nothing to do for the parameter list
			return node;
		}
		ILocation location = node.getLocation ();
		// desugar the paramnodes. $1 is ($ ^)
		ExpressionNode newNode = node;
		// for every n >= 2 in $n, add n-1 calls to _
		while (--paramNum > 0) {
			newNode = new CallNode (
					location,
					new OpNode (location, '_'),
					newNode);
		}
		newNode = new CallNode (
				location,
				new OpNode (location, '^'),
				newNode);
		// change the innermost node type to paramNode
		node.setParamNum (0);
		return newNode;
	}

	@Override
	public ShephongNode walkAssignment(AssignmentNode node, Object argument)
			throws CompilerException {
		if (node.getIdent () != null)
			node.setIdent ((IdentNode) walk (node.getIdent (), argument));
		if (node.getExpression () != null)
			node.setExpression ((ExpressionNode) walk (node.getExpression (), argument));
		return node;
	}

	@Override
	public ShephongNode walkCall(CallNode node, Object argument)
			throws CompilerException {
		if (node.getOp () != null)
			node.setOp ((ExpressionNode) walk (node.getOp (), argument));
		if (node.getParam () != null)
			node.setParam ((ExpressionNode) walk (node.getParam (), argument));
		return node;
	}

	@Override
	public ShephongNode walkChar(CharNode node, Object argument)
			throws CompilerException {
		// nothing to do
		return node;
	}

	@Override
	public ShephongNode walkIdent(IdentNode node, Object argument)
			throws CompilerException {
		// nothing to do
		return node;
	}

	@Override
	public ShephongNode walkList(ListNode node, Object argument)
			throws CompilerException {
		ArrayList<ExpressionNode> content = new ArrayList<ExpressionNode> ();
		for (ShephongNode element : node.getContent ())
			content.add ((ExpressionNode) walk (element, argument));
		node.setContent (content);
		return node;
	}

	@Override
	public ShephongNode walkMagic(MagicNode node, Object argument)
			throws CompilerException {
		if (node.getParam () != null)
			node.setParam ((ExpressionNode) walk (node.getParam (), argument));
		return node;
	}

	@Override
	public ShephongNode walkModule(ModuleNode node, Object argument)
			throws CompilerException {
		// TODO nothing here, move along
		return node;
	}

	@Override
	public ShephongNode walkNumber(NumberNode node, Object argument)
			throws CompilerException {
		// nothing to do
		return node;
	}

	@Override
	public ShephongNode walkOp(OpNode node, Object argument)
			throws CompilerException {
		// nothing to do
		return node;
	}

	@Override
	public ShephongNode walkProgram(ProgramNode node, Object argument)
			throws CompilerException {
		// TODO maybe we should change the ExpressionNode stuff inside the
		// ProramNode.
		ArrayList<ExpressionNode> content = new ArrayList<ExpressionNode> ();
		for (ShephongNode element : node.getExpressions ()){
			content.add((ExpressionNode)walk (element, argument)); // TODO see comment above
		}
		node.setExpressions (content);
		return node;
	}

}
