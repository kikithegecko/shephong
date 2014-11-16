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
 * The passed argument (if true) will cause statification like the passed node was already static.
 * 
 * @author shephongkrewe(imp)
 *
 */
public class RemoveStaticWalker  extends ShephongTreeWalker<ShephongNode, Boolean>{
	private ProgramNode programAlreadystatic;
	private ProgramNode nodesFromStatifing;
	private int run;
	
	public RemoveStaticWalker() {
		this.programAlreadystatic = new ProgramNode();
		this.nodesFromStatifing = new ProgramNode();
		this.run = 0;
	}
	
	@Override
	public ShephongNode walkAssignment(AssignmentNode node, Boolean argument)
			throws CompilerException {
		if(node.isStatic() || node.getContainsStatic() || argument){
			copyTree();
			
			walk(node.getExpression(), true);
			
			this.run++;
			node.setContainsStatic(false);
			node.setStatic(false);
		}
		return node;
	}

	@Override
	public ShephongNode walkCall(CallNode node, Boolean argument)
			throws CompilerException {
		if(node.isStatic() || argument){
			copyTree();
			
			// handle op
			node.setOp((ExpressionNode) walk(node.getOp(), true));
			node.getOp().setContainsStatic(false);
			node.getOp().setStatic(false);
			
			// handle param (if there's one)
			if(node.getParam() != null){
				node.setParam((ExpressionNode) walk(node.getParam(), true));
				node.getParam().setContainsStatic(false);
				node.getParam().setStatic(false);
			}

		}
		else if(node.getOp().isStatic() || node.getOp().getContainsStatic()){
			node.setOp((ExpressionNode) walk(node.getOp(), true));
			node.getOp().setContainsStatic(false);
			node.getOp().setStatic(false);
		}
		else if(node.getParam() != null){
			if(node.getParam().isStatic() || node.getParam().getContainsStatic()){
				node.setParam((ExpressionNode) walk(node.getParam(), true));
				node.getParam().setContainsStatic(false);
				node.getParam().setStatic(false);
			}
		}
		this.run++;
		node.setStatic(false);
		node.setContainsStatic(false);
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
		if(argument){
			ShephongNode temp = new CopyWalker().walk(node, null);
			renameContentOfNode(temp);
			this.run++;
			return temp;
		}
		return node;
	}

	@Override
	public ShephongNode walkList(ListNode node, Boolean argument)
			throws CompilerException {
		if(node.isStatic() || argument){
			copyTree();
			
			CopyWalker cw = new CopyWalker();
			node = (ListNode) cw.walk(node, null);
			
			renameContentOfNode(node);
		}
		else if(node.getContainsStatic()){
			ListNode tempList = new ListNode(node.getLocation());
			
			// copy the tree first:
			copyTree();
			
			// rename only the nodes we need to rename
			for(ShephongNode sn : node.getContent()){
				if(sn.isStatic() || sn.getContainsStatic()){
					System.out.println("adding copy to list (" + sn.getClass().getSimpleName() + ")");
					CopyWalker cw = new CopyWalker();
					ExpressionNode temp = (ExpressionNode) cw.walk(sn, null);
					renameContentOfNode(temp);
					tempList.addContent(temp);
				}
				else{
					System.out.println("adding orginal node to list (" + sn.getClass().getSimpleName() + ")");
					tempList.addContent((ExpressionNode) sn);
				}
			}
			node.setContent(tempList.getContent());
		}

		node.setContainsStatic(false);
		node.setStatic(false);
		this.run++;
		return node;
	}

	@Override
	public ShephongNode walkMagic(MagicNode node, Boolean argument)
			throws CompilerException {
		if(node.isStatic() || argument){
			copyTree();
			
			CopyWalker cw = new CopyWalker();
			node = (MagicNode) cw.walk(node, null);
			
			// rename the guts of this node:
			renameContentOfNode(node);
			
			this.run++;
			node.setStatic(false);
			node.setContainsStatic(false);
		}
		else if(node.getContainsStatic()){
			walk(node.getParam(), true);
			node.setContainsStatic(false);
		}
		return node;
	}

	@Override
	public ShephongNode walkModule(ModuleNode node, Boolean argument)
			throws CompilerException {
		// TODO
		System.err.println("not yet implemented");
		System.exit(-1);
		return null;
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
		// TODO unsure
		return node;
	}

	@Override
	public ShephongNode walkProgram(ProgramNode node, Boolean argument)
			throws CompilerException {
		// loop over all nodes inside the given ProgramNode
		for(ShephongNode sn : node.getExpressions()){
			ShephongNode tempNode = walk(sn, argument);
			// check if there's a new assignment of a node - which we will replace for further usage
			if(tempNode instanceof AssignmentNode){
				this.programAlreadystatic.replaceOrInsertAssignmentNode((AssignmentNode) tempNode);
			}
			else {
				this.programAlreadystatic.addChild(walk(sn, argument));
			}
		}
		 
		for(ShephongNode sn : nodesFromStatifing.getExpressions()){
			this.programAlreadystatic.addChild(sn);
		}
		
		return this.programAlreadystatic;
	}

	
	/**
	 * Copies the tree and renames every identnode.
	 * @throws CompilerException 
	 */
	private void copyTree() throws CompilerException{
		RenameWalker rw = new RenameWalker(this.run);
		// we need to copy the whole known tree first:
		CopyWalker cw = new CopyWalker();
		ProgramNode progKnownSoFar = (ProgramNode) cw.walk(programAlreadystatic, null);
		
		// rename the nodes inside:	
		progKnownSoFar = (ProgramNode) rw.walk(progKnownSoFar, true);
		
		// now we've got the static nodes, append them to nodesFromStatifing:
		for(ShephongNode sn : progKnownSoFar.getExpressions()){
			if(sn instanceof AssignmentNode){
				this.nodesFromStatifing.replaceOrInsertAssignmentNode((AssignmentNode) sn);
			}
			else{
				this.nodesFromStatifing.addChild(sn);
			}
		}
	}
	
	/**
	 * Renames all subnodes of the given node.
	 * @param node to be renamed
	 * @throws CompilerException
	 */
	private void renameContentOfNode(ShephongNode node) throws CompilerException{
		if(node == null){
			return;
		}
		else{
			new RenameWalker(this.run).walk(node, false);
		}
	}
	
}
