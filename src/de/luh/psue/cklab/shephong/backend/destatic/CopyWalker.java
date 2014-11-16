package de.luh.psue.cklab.shephong.backend.destatic;

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

/**
 * This walker doesn't manipulates the given nodes, it returns a (changed) copy.
 * 
 * @author shephongkrewe(imp)
 *
 */
public class CopyWalker  extends ShephongTreeWalker<ShephongNode, String> {

	@Override
	public ShephongNode walkAssignment(AssignmentNode node, String argument)
			throws CompilerException {
		return new AssignmentNode(node.getLocation(), (IdentNode) walk(node.getIdent(), null), (ExpressionNode) walk(node.getExpression(), null));
	}

	@Override
	public ShephongNode walkCall(CallNode node, String argument)
			throws CompilerException {
		ExpressionNode op = null;
		if(node.getOp() != null){
			op = (ExpressionNode) walk(node.getOp(), null);
		}
		ExpressionNode param = null;
		if(node.getParam() != null){
			param = (ExpressionNode) walk(node.getParam(), null);
		}
		return new CallNode(node.getLocation(), op, param);
	}

	@Override
	public ShephongNode walkChar(CharNode node, String argument)
			throws CompilerException {
		return new CharNode(node.getLocation(), node.getValue());
	}

	@Override
	public ShephongNode walkIdent(IdentNode node, String argument)
			throws CompilerException {
		return new IdentNode(node.getLocation(), node.getName());
	}

	@Override
	public ShephongNode walkList(ListNode node, String argument)
			throws CompilerException {
		ListNode list = new ListNode(node.getLocation());
		for(ShephongNode sn : node.getContent()){
			list.addContent((ExpressionNode) walk(sn, null));
		}
		return list;
	}

	@Override
	public ShephongNode walkMagic(MagicNode node, String argument)
			throws CompilerException {
		return new MagicNode(node.getLocation(), (ExpressionNode) walk(node.getParam(), null));
	}

	@Override
	public ShephongNode walkModule(ModuleNode node, String argument)
			throws CompilerException {
		return new ModuleNode(node.getLocation(), node.getName());
	}

	@Override
	public ShephongNode walkNumber(NumberNode node, String argument)
			throws CompilerException {
		return new NumberNode(node.getLocation(), node.getValue());
	}

	@Override
	public ShephongNode walkOp(OpNode node, String argument)
			throws CompilerException {
		return new OpNode(node.getLocation(), node.getValue());
	}

	@Override
	public ShephongNode walkParameter(ParameterNode node, String argument)
			throws CompilerException {
		return new ParameterNode(node.getLocation(), node.getParamNum());
	}

	@Override
	public ShephongNode walkProgram(ProgramNode node, String argument)
			throws CompilerException {
		ProgramNode pn = new ProgramNode();
		pn.setName(node.getName());
		
		for(ShephongNode sn : node.getExpressions()){
			pn.addChild(walk(sn, null));
		}
		return pn;
	}
	
}
