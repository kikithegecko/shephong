package de.luh.psue.cklab.shephong.il;

import java.util.ArrayList;

import de.luh.psue.compiler.error.CompilerException;

public final class ProgramNode extends ShephongNode {
	
	private ArrayList<ExpressionNode> expressions; //TODO make nice (rename etc.)
	private String name;
	
	/**
	 * Constructor. Initializes new Lists for modules and 
	 * functions.
	 * 
	 */
	public ProgramNode() {
		this.expressions = new ArrayList<ExpressionNode>();
	}
	
	public ArrayList<ModuleNode> getModules(){
		ArrayList<ModuleNode> modules = new ArrayList<ModuleNode>();
		for(ExpressionNode e : expressions){
			if(e instanceof ModuleNode){
				modules.add((ModuleNode) e);
			}
		}
		return modules;
	}
	
	public ArrayList<ExpressionNode> getExpressions(){
		return expressions;
	}
	
	public void setExpressions (ArrayList<ExpressionNode> expressions) {
		this.expressions = expressions;
	}
	
	public void addChild(ShephongNode node){
		if(node == null){
			return;
		}
//		System.out.println(">adding expression node: " + node.getClass().getSimpleName());
		this.addExpressionNode( (ExpressionNode) node);
	}
	
	private void addExpressionNode(ExpressionNode node){
		this.expressions.add(node);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		
		StringBuilder buffer = new StringBuilder();
		ArrayList<ModuleNode> modules = this.getModules();
		if(!modules.isEmpty()){
			// TODO maybe we should check those modules too for included modules?
			buffer.append("modules used by this program:\n");
			
			for(ModuleNode m : this.getModules()){
				buffer.append(m.toString() + ", ");
			}
			buffer.delete(buffer.length()-2, buffer.length()); // make shiny :).
			buffer.append("\n");
		}
		
		if(!this.expressions.isEmpty()){
			buffer.append("program:\n");
			
			for(ExpressionNode e : this.expressions){
				buffer.append(e.toString());
				buffer.append("\n");
			}
		}
		
		return buffer.toString();
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			ShephongTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkProgram(this, argument);
	}
	
	/**
	 * Checks if there's already an assignment to override, preferred method to add assignments after initial generation
	 * of a ProgramNode from the parser, which should only use addChild!
	 * @param param
	 */
	public void replaceOrInsertAssignmentNode(AssignmentNode param){
		for(ShephongNode sn : this.expressions){
			if(sn instanceof AssignmentNode && ((AssignmentNode) sn).getIdent().getName().equals(param.getIdent().getName())){
				this.expressions.set(this.expressions.indexOf(sn), param);
				return;
			}
		}
		this.expressions.add(param);
	}
	
	/**
	 * "Unpacks" the MagicNodes, like (a `) -> a.
	 * This method should only be called as last action on the ProgramNode before handing it over to the ByteCodeGenerator.
	 */
	public void unpackMagicNodes(){
		for(ShephongNode sn : this.expressions){
			if(sn instanceof MagicNode){
				this.expressions.set(this.expressions.indexOf(sn), ((MagicNode) sn).getParam());
			}
		}
	}
}