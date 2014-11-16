package de.luh.psue.cklab.shephong.backend.destatic;

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
import de.luh.psue.cklab.shephong.il.ShephongNode;
import de.luh.psue.cklab.shephong.il.ShephongTreeWalker;
import de.luh.psue.compiler.error.CompilerException;

/**
 * This walker removes any static attributes from the given nodes an renames ALL idents it finds.
 * This walker _DOES_ manipulate the given nodes.
 * If the passed boolean is true, it'll set static on the node to false, if you pass false, it won't touch it. 
 * 
 * @author shephongkrewe(imp)
 *
 */
public class RenameWalker  extends ShephongTreeWalker<ShephongNode, Boolean> {
	
	String runNumberString;
	
	public RenameWalker(int runNumber) {
		this.runNumberString = "anon::run" + String.valueOf(runNumber) + "::";
	}

	@Override
	public ShephongNode walkAssignment(AssignmentNode node, Boolean argument)
			throws CompilerException {
		if(argument){
			node.setStatic(false);
		}
		walk(node.getIdent(), argument);
		walk(node.getExpression(), argument);
		return node;
	}

	@Override
	public ShephongNode walkCall(CallNode node, Boolean argument)
			throws CompilerException {
		if(argument){
			node.setStatic(false);
		}
		walk(node.getOp(), argument);
		if(node.getParam() != null){
			walk(node.getParam(), argument);
		}
		return node;
	}

	@Override
	public ShephongNode walkChar(CharNode node, Boolean argument)
			throws CompilerException {
		return node;
	}

	@Override
	public ShephongNode walkIdent(IdentNode node, Boolean argument)
			throws CompilerException {
		node.setName(runNumberString + node.getName());
		return node;
	}

	@Override
	public ShephongNode walkList(ListNode node, Boolean argument)
			throws CompilerException {
		if(argument){
			node.setStatic(false);
		}
		for(ShephongNode sn : node.getContent()){
			walk(sn, argument);
		}
		return node;
	}

	@Override
	public ShephongNode walkMagic(MagicNode node, Boolean argument)
			throws CompilerException {
		if(argument){
			node.setStatic(false);
		}
		walk(node.getParam(), argument);
		return node;
	}

	@Override
	public ShephongNode walkModule(ModuleNode node, Boolean argument)
			throws CompilerException {
		// TODO maybe not the thing we really intend? think about later some day.
		return node;
	}

	@Override
	public ShephongNode walkNumber(NumberNode node, Boolean argument)
			throws CompilerException {
		return node;
	}

	@Override
	public ShephongNode walkOp(OpNode node, Boolean argument)
			throws CompilerException {
		return node;
	}

	@Override
	public ShephongNode walkParameter(ParameterNode node, Boolean argument)
			throws CompilerException {
		return node;
	}

	@Override
	public ShephongNode walkProgram(ProgramNode node, Boolean argument)
			throws CompilerException {
		for(ShephongNode sn : node.getExpressions()){
			walk(sn, argument);
		}
		return node;
	}
	
}
