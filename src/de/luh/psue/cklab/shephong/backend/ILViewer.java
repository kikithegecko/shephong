package de.luh.psue.cklab.shephong.backend;

import org.eclipse.swt.widgets.Display;
import de.luh.psue.cklab.shephong.il.*;
import de.luh.psue.compiler.ast.AstNode;
import de.luh.psue.compiler.ast.AstProduction;
import de.luh.psue.compiler.ast.AstToken;
import de.luh.psue.compiler.ast.AstTree;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.values.LocatedValue;
import de.luh.psue.eclipse.gef.ExtendableEditPartFactory;
import de.luh.psue.eclipse.gef.parts.ConnectionEditPart;
import de.luh.psue.eclipse.gef.parts.ObjectEditPart;
import de.luh.psue.eclipse.gef.parts.ast.AstProductionPart;
import de.luh.psue.eclipse.gef.parts.ast.AstTokenPart;
import de.luh.psue.eclipse.gef.parts.ast.AstTreePart;
import de.luh.psue.eclipse.viewer.ViewerDialog;
import de.luh.psue.util.graph.IConnection;

public class ILViewer extends ShephongTreeWalker<AstNode, Object> {

	static {
		ExtendableEditPartFactory.addBinding(Object.class, ObjectEditPart.class);
		ExtendableEditPartFactory.addBinding(IConnection.class, ConnectionEditPart.class);
		ExtendableEditPartFactory.addBinding(AstTree.class, AstTreePart.class);
		ExtendableEditPartFactory.addBinding(AstProduction.class, AstProductionPart.class);
		ExtendableEditPartFactory.addBinding(AstToken.class, AstTokenPart.class);
	}
	
	public void view(ProgramNode program) {
		Display.setAppName("Viewer");
		Display display = new Display();

		ViewerDialog dialog = new ViewerDialog();
		try {
			dialog.setContent(new AstTree(walk(program, null)));
		} catch (CompilerException e) {
			e.printStackTrace();
		}
		dialog.show();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	@Override
	public AstNode walkAssignment(AssignmentNode node, Object argument)
			throws CompilerException {
		AstProduction tree = new AstProduction("Assignment");
		tree.getSubNodes().add(walk(node.getIdent(), null));
		tree.getSubNodes().add(walk(node.getExpression(), null));
		return tree;
	}

	@Override
	public AstNode walkCall(CallNode node, Object argument)
			throws CompilerException {
		AstProduction tree = new AstProduction("Call");
		if (node.getParam() != null)
			tree.getSubNodes().add(walk(node.getParam(), null));
		tree.getSubNodes().add(walk(node.getOp(), null));
		return tree;
	}

	@Override
	public AstNode walkIdent(IdentNode node, Object argument)
			throws CompilerException {
		return new AstToken(new LocatedValue<String>(node.getLocation(), "Ident: " + node.getName()));
	}

	@Override
	public AstNode walkList(ListNode node, Object argument)
			throws CompilerException {
		AstProduction tree = new AstProduction("List");
		
		// show lists with chars only in our String representation
		boolean onlyChars = true;
		StringBuilder buffer = new StringBuilder();
		
		for (ExpressionNode e : node.getContent()) {
			if (!(e instanceof CharNode)) {
				onlyChars = false;
				break;
			}
			buffer.append(((CharNode)e).getValue());
		}
		
		if (onlyChars) {
			tree.getSubNodes().add(new AstToken(new LocatedValue<String>(
					node.getLocation(), "String: " + buffer.toString())));
		} else {
			for (ExpressionNode e : node.getContent())
				tree.getSubNodes().add(walk(e, null));
		}
		
		return tree;
	}

	@Override
	public AstNode walkMagic(MagicNode node, Object argument)
			throws CompilerException {
		AstProduction tree = new AstProduction("Magic");
		tree.getSubNodes().add(walk(node.getParam(), null));
		return tree;
	}

	@Override
	public AstNode walkModule(ModuleNode node, Object argument)
			throws CompilerException {
		return new AstToken(new LocatedValue<String>(node.getLocation(), "Module: " + node.getName()));
	}

	@Override
	public AstNode walkProgram(ProgramNode node, Object argument)
			throws CompilerException {
		AstProduction tree = new AstProduction("Program");
		
		/*
		 * Skip the first XX children. These are just our default definitions
		 * from our StdLib. It would be needless bloat to show them in the IL
		 * tree.
		 */
		int i = 1;
		int end = GenerateStdLib.getStdLib().size();
		
		for (ExpressionNode e : node.getExpressions()) {
			if (i <= end)
				i++;
			else
				tree.getSubNodes().add(walk(e, null));
		}
		return tree;
	}

	@Override
	public AstNode walkChar(CharNode node, Object argument)
			throws CompilerException {
		return new AstToken(new LocatedValue<String>(node.getLocation(), "Char: " + node.getValue()));
	}

	@Override
	public AstNode walkNumber(NumberNode node, Object argument)
			throws CompilerException {
		return new AstToken(new LocatedValue<String>(node.getLocation(), "Number: " + node.getValue()));
	}

	@Override
	public AstNode walkOp(OpNode node, Object argument)
			throws CompilerException {
		return new AstToken(new LocatedValue<String>(node.getLocation(), "Operator: " + node.getValue()));
	}

	@Override
	public AstNode walkParameter(ParameterNode node, Object argument)
			throws CompilerException {
		return new AstToken(new LocatedValue<String>(node.getLocation(), "$" + node.getParamNum()));
	}

}
