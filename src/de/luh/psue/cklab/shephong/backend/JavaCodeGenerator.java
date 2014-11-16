package de.luh.psue.cklab.shephong.backend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;

/**
 * This class generates Java code out of a Shephong program.
 * It is intended that you can generate the code and then
 * compile and run it, and it will do the same thing as
 * directly compiled shephong code.
 * 
 * @author shephongkrewe(karo, imp)
 *
 */
public class JavaCodeGenerator extends ShephongTreeWalker<Object, Boolean>{

	private StringBuilder builder;
	private FileWriter writer;
	private StringBuilder main; 

	public JavaCodeGenerator(CompilerContext context) {}

	/**
	 * This is the main method. It takes a PrgramNode and makes Java
	 * code out of it.
	 * 
	 * @param program the shephong program to be translated
	 * @return a file with the Java code.
	 * @throws CompilerException
	 * @throws IOException
	 */
	public File generate(ProgramNode program) throws CompilerException, IOException {
		
		File javaFile = new File("ShephongProgram.java");
		javaFile.setWritable(true);
		//javaFile.createNewFile();
		//CojenClassFile cojenClassFile = new CojenClassFile("de.luh.psue", "ShephongProgram");
		//classFile = cojenClassFile.getCojenClassFile();
		//classFile.addInterface(Runnable.class);
		builder = new StringBuilder();
		writer = new FileWriter(javaFile);
		main = new StringBuilder();
		
		
		walk(program, true);
		
		
		builder.append("\n }\n");
		
		writer.append(builder);
		writer.append(main);
		
		writer.close();

		return javaFile;
	}
	
	/* (non-Javadoc)
	 * @see de.luh.psue.cklab.shephong.il.ShephongTreeWalker#walkProgram(de.luh.psue.cklab.shephong.il.ProgramNode, java.lang.Object)
	 * 
	 * First, all assignments are translated into Java assignments.
	 * Afterwards, a main method is created and all (remaining) calls
	 * are put in there.
	 */
	@Override
	public Object walkProgram(ProgramNode node, Boolean argument)
			throws CompilerException {
		builder.append("import java.util.*;\n");
		builder.append("import de.luh.psue.cklab.shephong.backend.objects.*;\n");
		builder.append("public class ShephongProgram {\n");
		
		//TODO module stuff
		
		for(ExpressionNode e : node.getExpressions()){ 
			if(e instanceof AssignmentNode)
				e.walk(this, argument);
		}
		
		// add some newlines before the main starts:
		builder.append("\n\n\n");
		builder.append("public static void main(String[] args) {\n");
		
		for(ExpressionNode e : node.getExpressions()){
			if(e instanceof CallNode){
				builder.append("/*\n");
				ToStringWalker tsw = new ToStringWalker();
				builder.append(tsw.walk(e, null));
				builder.append("\n*/\n");
				builder.append("\tSystem.out.println(");
				e.walk(this, false);
				builder.append(");\n\n");
			}
		}
		
		main.append("\n}\n");
		
		return null;
	}

	@Override
	public Object walkIdent(IdentNode node, Boolean argument)
			throws CompilerException {
		builder.append("( new ShephongIdent(\"" + shepongIdent2javaIdent(node.getName()) + "\", ShephongProgram.class))"); // TODO ?
		return null;
	}

	@Override
	public Object walkCall(CallNode node, Boolean argument)
			throws CompilerException {
		
		ExpressionNode op = node.getOp();
		boolean hadCallNodeAsOp = false;
		
		if(op instanceof IdentNode){
			builder.append(shepongIdent2javaIdent(((IdentNode) op).getName()));
		}
		else if(op instanceof OpNode){
			walk(op, false);
		}
		else if(op instanceof CallNode){
			hadCallNodeAsOp = true;
			builder.append("((ShephongIdent) ");
			walk(op, false);
			builder.append(")");
			builder.append(".evaluate(");
		}
		else if(op instanceof ParameterNode){
			builder.append("paramlist"); //TODO ?
			System.err.println("using /paramlist/ as operator is not supported right now");
			// think it's easier to implement this by using ShephongIdent objects -- imp
		}
		else if(op instanceof ListNode){
			// TODO
			System.err.println("using lists as operator is not supported right now");
		}
		
		builder.append("(");
		
		// pass false as argument, so inner call nodes won't insert a ";" inside a call.
		walk(node.getParam(), false);
		
		builder.append(")");
		if(hadCallNodeAsOp){
			builder.append(")");
		}
		// only append the ";" on (nearly) top level
		if(argument){
			builder.append(";");
		}
		return null;
	}

	@Override
	public Object walkList(ListNode node, Boolean argument)
			throws CompilerException {
		builder.append("(new ShephongList())");
		
		for(ShephongNode element : node.getContent()){
			builder.append(".add(");
			walk(element, argument);
			builder.append(")");
		}
		return null;
	}

	@Override
	public Object walkMagic(MagicNode node, Boolean argument)
			throws CompilerException {
		// magic nodes are gone at this stage.
		return null;
	}

	/* (non-Javadoc)
	 * @see de.luh.psue.cklab.shephong.il.ShephongTreeWalker#walkAssignment(de.luh.psue.cklab.shephong.il.AssignmentNode, java.lang.Object)
	 * 
	 * Translates a shephong assignment into a java definition.
	 */
	@Override
	public Object walkAssignment(AssignmentNode node, Boolean argument)
			throws CompilerException {
		builder.append("public static ShephongObject "); 
		builder.append(shepongIdent2javaIdent(((IdentNode)node.getIdent()).getName())); 
		builder.append("(ShephongObject paramlist) {\n"); 
		
		// add the shephongcode as comment
		builder.append("/*\n");
		ToStringWalker tsw = new ToStringWalker();
		builder.append(tsw.walk(node, null));
		builder.append("\n*/\n");
		
		builder.append("\treturn ");
		
		// TODO stuff like (#^ ^ :) fails. maybe it should be written like: (($ #^) ^:)
		walk(node.getExpression(), false);
		
		if(node.getExpression() instanceof CallNode
				|| node.getExpression() instanceof OpNode
				|| node.getExpression() instanceof NumberNode
				|| node.getExpression() instanceof ListNode){
			builder.append(";\n");
		}
		else{ // the ";" is already there
			builder.append("\n");
		}
		builder.append("}\n\n");
		return null;
	}

	@Override
	public Object walkModule(ModuleNode node, Boolean argument)
			throws CompilerException {
		return null;
	}

	@Override
	public Object walkChar(CharNode node, Boolean argument)
			throws CompilerException {
		builder.append("( new ShephongChar('" + node.getValue()+ "'))");
		return null;
	}

	@Override
	public Object walkNumber(NumberNode node, Boolean argument)
			throws CompilerException {
		builder.append("( new ShephongNumber(" + node.getValue() +"))");
		return null;
	}

	@Override
	public Object walkOp(OpNode node, Boolean argument)
			throws CompilerException {
		// handle it like an normal ident atm:
		builder.append("new ShephongIdent(\"");
		builder.append(shepongIdent2javaIdent(node.toString()) + "\", ShephongProgram.class");
		builder.append(")");
		return null;
	}

	@Override
	public Object walkParameter(ParameterNode node, Boolean argument)
			throws CompilerException {
		builder.append("paramlist");
		return null;
	}
	
	private String shepongIdent2javaIdent(String ident){
		// TODO we should keep out java identifier too...
		if(isJavaIdentifier(ident)) {
			return ident;
		}
		if(ident.substring(0, 1).matches("\\d")){
			ident = "number_" + ident;
		}
		ident = ident.replaceAll("#", "hash_");
		ident = ident.replaceAll("\\+", "std_plus");
		ident = ident.replaceAll("-", "std_minus");
		ident = ident.replaceAll("\\*", "std_mult");
		ident = ident.replaceAll("/", "std_div");
		ident = ident.replaceAll("\\|", "std_or");
		ident = ident.replaceAll("&", "std_and");
		ident = ident.replaceAll("=", "std_eq");
		ident = ident.replaceAll("<", "std_lt");
		ident = ident.replaceAll(">", "std_gt");
		ident = ident.replaceAll("%", "std_mod");
		ident = ident.replaceAll("\\^", "caret");
		return ident;
	}
	
	private boolean isJavaIdentifier(String s) {
		int n = s.length();
	    if (n==0) return false;
	    if (!Character.isJavaIdentifierStart(s.charAt(0))){
	    	return false;
	    }
	    for (int i = 1; i < n; i++){
	    	if (!Character.isJavaIdentifierPart(s.charAt(i))){
	    		return false;
	    	}
	    }
	    return true;
	}
}
