package de.luh.psue.cklab.shephong.il;

import java.util.ArrayList;

import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.ILocation;

/**
 * A node representation of a list. Uses an ArrayList
 * for saving the content.
 * 
 * @author karo of the shephongkrewe
 *
 */
public final class ListNode extends ExpressionNode{

	private ArrayList<ExpressionNode> content;
	
	/**
	 * Constructor, chains to the super class.
	 * 
	 * @param location the node's location
	 * @param content the list content
	 */
	public ListNode(ILocation location) {
		super(location);
		this.content = new ArrayList<ExpressionNode>();
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(ExpressionNode n : this.content){
			buffer.append(n.toString() + ", ");
		}
		if(this.content.size() != 0){
			buffer.delete(buffer.length()-2, buffer.length()); // remove the last ", " from the output. i.e. cleanup.
		}
		return "list" + putInParentesis(buffer.toString()); 
	}
	
	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			ShephongTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkList(this, argument);
	}
	
	public ArrayList<ExpressionNode> getContent() {
		return content;
	}

	public void setContent (ArrayList<ExpressionNode> content) {
		this.content = content;
	}
	
	public ListNode addContent(ExpressionNode node) {
		this.content.add(node);
		this.setContainsStatic(this.getContainsStatic() || node.getContainsStatic() || node.isStatic());
		return this;
	}

	public ExpressionNode getHead() {
		if (this.getContent().size() == 0)
			return null;
		return this.getContent().get(0);
	}
	
	/**
	 * Grabs all ExpressionNodes from the ListNode's content
	 * throws the first one away and puts the rest in a new
	 * ListNode.
	 * 
	 * @return a new ListNode with the tail
	 */
	public ListNode getTail(){
		
		if (this.content.size() == 0)
			return null;
		
		ListNode foo = new ListNode(this.location);
		for(int i = 1; i < this.content.size(); i++){
			ExpressionNode node = this.content.get(i);
			foo.addContent(node);
		}
		return foo;
	}
}
