package de.luh.psue.cklab.shephong.dl;

import java.util.ArrayList;

import de.luh.psue.cklab.shephong.backend.DataWalker;
import de.luh.psue.cklab.shephong.funtab.FunTab;
import de.luh.psue.cklab.shephong.il.ExpressionNode;
import de.luh.psue.cklab.shephong.il.ListNode;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;

public class ListDNode extends DataNode {
	
	private ArrayList<DataNode> content;

	// This constructor is called from DataWalker
	public ListDNode(ListNode node, FunTab<DataNode> env, CompilerContext context) throws CompilerException {
		
		this.content = new ArrayList<DataNode>();
		this.setEnv(env);
	
		for (ExpressionNode n : node.getContent()) {
			DataWalker dw = new DataWalker(context);
			content.add(dw.walk(n, env));
		}
	}
	
	// This constructor is called from CallDNode to build a ListDNode from a basic DataNode
	public ListDNode (DataNode node, FunTab<DataNode> env) throws CompilerException {
		this.content = new ArrayList<DataNode> ();
		this.content.add (node);
		this.setEnv(env);
	}

	// Also used in CallDNode
	public ListDNode (ArrayList<DataNode> content, FunTab<DataNode> env) {
		this.content = content;
		this.env = env;
	}

	@Override
	public String toString () {
		if (content.size() == 0) {
			/*
			 * Quick return if list is empty
			 */
			return "(~)";
		}
		StringBuffer buffer = new StringBuffer (" ~)");
		StringBuffer string = new StringBuffer ("\"");
		
		boolean firstElement = true;
		boolean charsOnly = true;
		for (DataNode node : content) {
			if (charsOnly) {
				if (node instanceof CharDNode) {
					string.append(((CharDNode) node).getCharValue ());
				} else {
					charsOnly = false;
				}
			}
			if (! firstElement) {
				buffer.insert (0, " ");
			}
			buffer.insert (0, node);
			firstElement = false;
		}
		if (charsOnly && string.length () > 0) {
			string.insert (0, "\"");
			return string.toString ();
		} else {
			buffer.insert (0, "(");
			return buffer.toString ();
		}
	}

	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			DataNodeTreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType argument) throws CompilerException {
		return walker.walkList(this, argument);
	}
	
	/*
	 * OpDNodes need to process the List, so let it be read.
	 */
	public ArrayList<DataNode> getContent () {
		return this.content;
	}
	
	public void setContent (ArrayList<DataNode> content) {
		this.content = content;
	}
}
