package de.luh.psue.cklab.shephong.il;

import de.luh.psue.cklab.shephong.il.ShephongTreeWalker;
import de.luh.psue.cklab.shephong.semantical.ShephongType;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.il.AbstractNode;
import de.luh.psue.compiler.input.ILocation;

/**
 * This is THE super class of all our Shephong nodes.
 * 
 * @author karo
 *
 */
public abstract class ShephongNode extends AbstractNode {
	
	protected boolean isStatic;
	protected boolean containsStatic;
	protected ShephongType type = ShephongType.UNKNOWN;

	public ShephongNode() {
		super();
		this.setStatic(false);
		this.setContainsStatic(false);
	}
	
	public ShephongNode(ILocation location) {
		super(location);
	}
	
	public void setStatic(boolean isStatic){
		this.isStatic = isStatic;
	}
	
	public boolean isStatic(){
		return this.isStatic;
	}
		
	public boolean getContainsStatic() {
		return this.containsStatic;
	}

	public void setContainsStatic(boolean containsStatic) {
		this.containsStatic = containsStatic;
	}

	protected String putInParentesis(String content) {
		String opening = "(";
		String closing = ")";
		
		if(this.isStatic()){
			opening = "[";
			closing = "]";
		}
		return opening + content + closing;
	}
	
	public abstract <ReturnType, ArgumentType>  ReturnType walk(ShephongTreeWalker<ReturnType, ArgumentType> walker, ArgumentType argument) throws CompilerException;

	/**
	 * 
	 * @return Interfered Type of this IL object
	 */
	public ShephongType getType() {
		return this.type;
	}

	/**
	 * 
	 * @param type Interfered Type to set for this IL object
	 */
	public void setType(ShephongType type) {
		this.type = type;
	}
}
