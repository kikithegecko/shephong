package de.luh.psue.cklab.shephong.backend;

import org.cojen.classfile.ClassFile;
import org.cojen.classfile.CodeBuilder;
import org.cojen.classfile.Label;
import org.cojen.classfile.LocalVariable;
import org.cojen.classfile.MethodInfo;
import org.cojen.classfile.Modifiers;
import org.cojen.classfile.TypeDesc;

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
import de.luh.psue.cojen.meta.CojenClassFile;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;


public class ByteCodeGenerator extends ShephongTreeWalker<Object, Boolean> {

	private ClassFile classFile;
	private CodeBuilder codeBuilder;
	
	private TypeDesc shephongObject_t = TypeDesc.forClass("de/luh/psue/cklab/shephong/backend/objects/ShephongObject");
	private TypeDesc shephongIdent_t = TypeDesc.forClass("de/luh/psue/cklab/shephong/backend/objects/ShephongIdent");
	private TypeDesc shephongNumber_t = TypeDesc.forClass("de/luh/psue/cklab/shephong/backend/objects/ShephongNumber");
	private TypeDesc shephongChar_t = TypeDesc.forClass("de/luh/psue/cklab/shephong/backend/objects/ShephongChar");
	private TypeDesc shephongList_t = TypeDesc.forClass("de/luh/psue/cklab/shephong/backend/objects/ShephongList");
	private TypeDesc shephongStdLib_t = TypeDesc.forClass("de/luh/psue/cklab/shephong/backend/ShephongStdLib");
	private TypeDesc shephongCall_t = TypeDesc.forClass("de/luh/psue/cklab/shephong/backend/objects/ShephongCall");
	
	private LocalVariable param = null;

	public ByteCodeGenerator(CompilerContext context) {}

	public CojenClassFile generate(ProgramNode program)
			throws CompilerException {
		
		CojenClassFile cojenClassFile;
		
		if (program.getName() != null && (!program.getName().equals("")))
			cojenClassFile = new CojenClassFile("de.luh.psue", program.getName());
		cojenClassFile = new CojenClassFile("de.luh.psue", "ShephongProgram");
		
		classFile = cojenClassFile.getCojenClassFile();
		classFile.setTarget("1.6");
		classFile.addInterface(Runnable.class);

		walk(program, false);

		return cojenClassFile;
	}

	@Override
	public Object walkProgram(ProgramNode node, Boolean optimize)
			throws CompilerException {
		
		// create default constructor
		MethodInfo constructor = classFile.addConstructor(Modifiers.PUBLIC, null);
		codeBuilder = new CodeBuilder(constructor);
		codeBuilder.loadThis();
		codeBuilder.invokeSuperConstructor(null);
		codeBuilder.returnVoid();
		
		// first walk all top level assignments
		for (ExpressionNode expression : node.getExpressions()) {
			if (expression instanceof AssignmentNode)
				walk(expression, optimize);
		}
		
		// create run method for class
		MethodInfo runMethod = classFile.addMethod(Modifiers.PUBLIC, "run", TypeDesc.VOID, null);
		codeBuilder = new CodeBuilder(runMethod);
		codeBuilder.createLocalVariable("this", classFile.getType());
		
		// second walk all top level calls and put sysouts around them
		TypeDesc printStream_t = TypeDesc.forClass("java.io.PrintStream");
		for (ExpressionNode expression : node.getExpressions()) {
			
			if (!(expression instanceof AssignmentNode)) {
				
				codeBuilder.loadStaticField("java.lang.System", "out", printStream_t);
				
				walk(expression, optimize);
				
				codeBuilder.invokeVirtual(TypeDesc.OBJECT, "toString", TypeDesc.STRING, null);
				codeBuilder.invokeVirtual(printStream_t, "println", null,
						new TypeDesc[] { TypeDesc.STRING });
			}
		}
		
		// run method returns void
		codeBuilder.returnVoid();
		// run method is complete here, do not any more code into it
		codeBuilder = null;
		
		return null;
	}

	/**
	 * Generates a ShephongIdent object in JBC from an IdentNode.
	 * 
	 * It creates a reference to a ShephongIdent object and invokes the
	 * constructor of it. It leaves one reference to the created object
	 * on top the stack.
	 */
	@Override
	public Object walkIdent(IdentNode node, Boolean optimize)
			throws CompilerException {
		
		codeBuilder.newObject(shephongIdent_t);
		codeBuilder.dup();
		codeBuilder.loadConstant(this.identName((node)));
		codeBuilder.loadConstant(classFile.getType());
		codeBuilder.invokeConstructor(shephongIdent_t,
				new TypeDesc[] {TypeDesc.STRING, TypeDesc.forClass("java.lang.Class")});
		
		return null;
	}

	@Override
	public Object walkCall(CallNode node, Boolean optimize)
			throws CompilerException {
		
		if (optimize) {
			
			/* * * * * * * * * * * * * * * * * * * * * * * * * *
			 * We are at a position in the shephong IL tree where
			 * we can produce optimized JBC so let's do that :-)
			 * For this we directly call everything instead of
			 * encapsulating in with objects
			 * * * * * * * * * * * * * * * * * * * * * * * * * */
			
			
			// this is the simplest case: (PARAM #BASIC)
			if (node.getOp() instanceof OpNode) {
				
				// let's put the PARAM onto the stack
				walk(node.getParam(), false); // TODO maybe also be optimized here?
				
				// invoke our basic op's method
				this.invokeBasic((OpNode) node.getOp());
			}
			// also a simple case: (PARAM IDENT)
			else if (node.getOp() instanceof IdentNode) {
				
				// let's put the PARAM onto the stack
				walk(node.getParam(), false); // TODO maybe also be optimized here?
				
				// directly invoke the method encapsulated by the ident
				codeBuilder.invokeStatic(classFile.getType(), this.identName((IdentNode) node.getOp()),
						shephongObject_t, new TypeDesc[] {shephongObject_t});
			}
			// this is the following case: (PARAM (...))
			else if (node.getOp() instanceof CallNode) {
				
				// generate the object representation of the param
				if (node.getParam() != null)
					walk(node.getParam(), false); // TODO maybe also be optimized here?
				// ..or push null if there is no param
				else
					codeBuilder.loadNull();
				
				walk(node.getOp(), optimize);
				
				Label testForList = codeBuilder.createLabel();
				Label after = codeBuilder.createLabel();
				
				codeBuilder.instanceOf(shephongIdent_t);
				codeBuilder.ifZeroComparisonBranch(testForList, "==");
				codeBuilder.swap();
				codeBuilder.invokeVirtual(shephongIdent_t, "evaluate",
						shephongObject_t, new TypeDesc[] {shephongObject_t});
				codeBuilder.branch(after);
				
				testForList.setLocation();
				codeBuilder.instanceOf(shephongList_t);
				codeBuilder.ifZeroComparisonBranch(after, "==");
				codeBuilder.swap();
				codeBuilder.invokeVirtual(shephongList_t, "evaluate",
						shephongObject_t, new TypeDesc[] {shephongObject_t});
				
				after.setLocation();
				
				/*
				 *  TODO this is *NOT* really optimized
				 *  And maybe it is not good overall...
				 */
				
				// create a reference to a shephong call object 
				//codeBuilder.newObject(shephongCall_t);
				//codeBuilder.dup();
				
				// generate the object representation of the op
				//walk(node.getOp(), false);
				
				// generate the object representation of the param
				//if (node.getParam() != null)
					//walk(node.getParam(), false);
				// ..or push null if there is no param
				//else
					//codeBuilder.loadNull();
				
				// invoke constructor of the shephong call
				//codeBuilder.invokeConstructor(shephongCall_t,
						//new TypeDesc[] {shephongObject_t, shephongObject_t});
				
				// we are in the optimized case so we can evaluate this directly
				// without any argument to evaluate (it has been given to the
				// constructor some lines above)
				//codeBuilder.loadNull();
				//codeBuilder.invokeVirtual(shephongCall_t, "evaluate",
						//shephongObject_t, new TypeDesc[] {shephongObject_t});
			}
			// this is our mapply case: (PARAM (...~))
			else if (node.getOp() instanceof ListNode) {
				throw new RuntimeException("ByteCodeGenerator (optimized case):\n" + 
						" Encountered a list as an OP in a call:\n" +
						"  Not yet implemented");
			} else if (node.getOp() instanceof ParameterNode) {
				throw new RuntimeException("ByteCodeGenerator (optimized case):\n" + 
						" Encountered a param ($) as an OP in a call:\n" +
						"  Not yet implemented");
			}
			// this can't be
			else {
				throw new RuntimeException("ByteCodeGenerator (optimized case):\n" +
						" Encountered an unhandled case:\n" +
						"  You are facing an implementation error.");
			}
		
		} // END optimized

		else {
			
			/* * * * * * * * * * * * * * * * * * * * * * * * * *
			 * We are at a position in the shephong IL tree where
			 * we cannot produce optimized JBC so we encapsulate
			 * it with producing some objects :-(
			 * * * * * * * * * * * * * * * * * * * * * * * * * */
			
			
			// this is the simplest case: (PARAM #BASIC)
			if (node.getOp() instanceof OpNode
			||
			// also a simple case: (PARAM IDENT)
			node.getOp() instanceof IdentNode
			||
			// this is the following case: (PARAM (...))
			node.getOp() instanceof CallNode
			||
			node.getOp() instanceof ParameterNode
			||
			node.getOp() instanceof ListNode) {
				
				// create a reference to a shephong call object 
				codeBuilder.newObject(shephongCall_t);
				codeBuilder.dup();
				
				// generate the ShephongIdent object (the op)
				walk(node.getOp(), false);
				
				// generate the object representation of the param..
				if (node.getParam() != null)
					walk(node.getParam(), false);
				// ..or push null if there is no param
				else
					codeBuilder.loadNull();
				
				// invoke constructor of the shephong call
				codeBuilder.invokeConstructor(shephongCall_t,
						new TypeDesc[] {shephongObject_t, shephongObject_t});
			}
			// this can't be
			else {
				throw new RuntimeException("ByteCodeGenerator (NOT optimized case):\n" +
						" Encountered an unhandled case:\n" +
						"  You are facing an implementation error.");
			}
		}
		
		return null;
	}

	/**
	 * Generates a ShephongList object in JBC from an ListNode.
	 * 
	 * It creates a reference to a ShephongList object and invokes the
	 * constructor of it. It adds all the items from the ListNode into
	 * the ShephongList object. At the end it leaves one reference to the
	 * created shephong list object on top of the stack.
	 */
	@Override
	public Object walkList(ListNode node, Boolean optimize)
			throws CompilerException {
		
		// create new shephong list object
		codeBuilder.newObject(shephongList_t);
		codeBuilder.dup();
		codeBuilder.invokeConstructor(shephongList_t, null);
		
		// generate JBC for the original content and put all these items into our list
		for (ShephongNode n : node.getContent()) {
			// generate byte code for item
			// leave a reference on top of the stack
			walk(n, false);
			
			// we assume a reference of the generated object is on top of the stack.
			// we add it into our list
			// we leave a reference to the list on top of the stack.
			codeBuilder.invokeVirtual(shephongList_t, "add", shephongList_t,
					new TypeDesc[] {shephongObject_t});
		}
		
		return null;
	}

	/**
	 * If we find a magic node in here we just throw a RuntimeException.
	 * This should never happen.
	 */
	@Override
	public Object walkMagic(MagicNode node, Boolean optimize)
			throws CompilerException {
		throw new RuntimeException("ByteCodeGenerator:\n" +
				" We encountered a magic node in the bcg:\n" +
				"  This is an implementation error");
	}

	@Override
	public Object walkAssignment(AssignmentNode node, Boolean optimize)
		throws CompilerException {
		
		// create according method name
		MethodInfo functionMethod = classFile.addMethod(Modifiers.PUBLIC_STATIC,
				this.identName(node.getIdent()), shephongObject_t, new TypeDesc[] {shephongObject_t});
		
		// create method body
		codeBuilder = new CodeBuilder(functionMethod);
		
		// create a local variable for $ to able to load it later in this assignment
		this.param = codeBuilder.createLocalVariable("PARAM", shephongObject_t);
		
		// create JBC for the stuff our assignment encapsulates
		walk(node.getExpression(), optimize);
		
		// return a shephongObject in all of our methods
		codeBuilder.returnValue(shephongObject_t);
		
		// method is complete, do not add more code into it
		codeBuilder = null;
		// parameter can only be used inside assignments and we are done here
		this.param = null;
		
		return null;
	}

	/**
	 * If we encounter a module node we just throw a RuntimeException.
	 * This is not and maybe will never be implemented.
	 */
	@Override
	public Object walkModule(ModuleNode node, Boolean optimize)
			throws CompilerException {
		throw new RuntimeException("ByteCodeGenerator:\n" +
				" Encountered a module node:\n" +
				"  This is an implementation error");
	}

	/**
	 * Generates a ShephongChar object in JBC from an CharNode.
	 * 
	 * It creates a reference to a ShephongChar object and invokes the
	 * constructor of it. It leaves one reference to the created object
	 * on top of the stack.
	 */
	@Override
	public Object walkChar(CharNode node, Boolean optimize)
			throws CompilerException {
		
		codeBuilder.newObject(shephongChar_t);
		codeBuilder.dup();
		codeBuilder.loadConstant(node.getValue());
		codeBuilder.invokeConstructor(shephongChar_t, new TypeDesc[] {TypeDesc.CHAR});
		
		return null;
	}

	/**
	 * Generates a ShephongNumber object in JBC from an NumberNode.
	 * 
	 * It creates a reference to a ShephongNumber object and invokes the
	 * constructor of it. It leaves one reference to the created object
	 * on the stack.
	 */
	@Override
	public Object walkNumber(NumberNode node, Boolean optimize)
			throws CompilerException {
		
		codeBuilder.newObject(shephongNumber_t);
		codeBuilder.dup();
		codeBuilder.loadConstant(node.getValue());
		codeBuilder.invokeConstructor(shephongNumber_t, new TypeDesc[] {TypeDesc.INT});
		
		return null;
	}
	
	@Override
	public Object walkOp(OpNode node, Boolean optimize) {
		
		/*
		 * Use and handle OpNode exactly the same way as an IdentNode.
		 * In compiler language it is the same thing (of course in IL it is
		 * not but that does not matter).
		 * That means we just have to create a ShephongIdent here as we do
		 * this in walkIdent.
		 */
		
		codeBuilder.newObject(shephongIdent_t);
		codeBuilder.dup();
		codeBuilder.loadConstant(this.opName(node));
		
		// the difference to ident is just that an OP's method lies in our stdlib.
		codeBuilder.loadConstant(shephongStdLib_t);
		
		codeBuilder.invokeConstructor(shephongIdent_t,
				new TypeDesc[] {TypeDesc.STRING, TypeDesc.forClass("java.lang.Class")});
		
		return null;
	}

	@Override
	public Object walkParameter(ParameterNode node, Boolean optimize)
			throws CompilerException {
		
		codeBuilder.loadLocal(this.param);
		
		return null;
	}
	
	/**
	 * Replaces some 'bad' identifiers if you ask the JVM
	 * with simple text. 
	 * @param node The IdentNode from which you need a 'good' name.
	 * @return The name of the identifier with no bad characters.
	 */
	private String identName(IdentNode node) {
		// TODO find out what is 'bad' for the JVM and replace it :)
		String name = node.getName();
		if(name.contains("/") ||
			name.contains("<") ||
			name.contains(">"))
		{
			name = "sane::" + name.replace("/", ":slash:")
								  .replace("<", ":less:")
								  .replace(">", ":greater:");
		}
		return name;
	}
	
	/**
	 * Replaces some 'bad' (if you ask the JVM) names of basic operands
	 * with simple text. 
	 * @param node The OpNode from which you need a 'good' name.
	 * @return The name of the identifier with no bad characters.
	 */
	private String opName(OpNode node) {
		
		// Math Ops
		if (node.getValue() == '+') {
			return "std_basic_plus";
		} else if (node.getValue() == '-') {
			return "std_basic_minus";
		} else if (node.getValue() == '*') {
			return "std_basic_mul";
		} else if (node.getValue() == '/') {
			return "std_basic_quot";
		} else if (node.getValue() == '%') {
			return "std_basic_rem";
		// Logic Ops
		} else if (node.getValue() == '&') {
			return "std_basic_and";
		} else if (node.getValue() == '|') {
			return "std_basic_or";
		} else if (node.getValue() == '!') {
			return "std_basic_not";
		} else if (node.getValue() == '=') {
			return "std_basic_eq";
		} else if (node.getValue() == '<') {
			return "std_basic_lt";
		} else if (node.getValue() == '>') {
			return "std_basic_gt";
		// Lists
		} else if (node.getValue() == '^') {
			return "std_basic_head";
		} else if (node.getValue() == '_') {
			return "std_basic_tail";
		} else if (node.getValue() == '\"') {
			return "std_basic_headOfTail";
		} else if (node.getValue() == '@') {
			return "std_basic_append";
		} else if (node.getValue() == '?') {
			return "std_basic_isList";
		// Magic
		} else if (node.getValue() == '\\') {
			return "std_basic_reflect";
		
		// Char <-> Number conversions
		} else if (node.getValue() == 'c') {
			return "std_basic_char2number";
		} else if (node.getValue() == 'n') {
			return "std_basic_number2char";
		}
		
		throw new RuntimeException("ByteCodeGenerator:\n" +
				" The following OP does not represent a correct and implemented" +
				" basic operation: " + node.getValue());
	}
	
	/**
	 * It invokes the according basic method from the stdlib.
	 * Beware that it assumes the PARAM to be on top of the stack!
	 * It leaves the calculated result on top of the stack.
	 */
	private void invokeBasic(OpNode node)
			throws CompilerException {
		
		// Math Ops
		if (node.getValue() == '+') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_plus",
					shephongNumber_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == '-') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_minus",
					shephongNumber_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == '*') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_mul",
					shephongNumber_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == '/') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_quot",
					shephongNumber_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == '%') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_rem",
					shephongNumber_t, new TypeDesc[] {shephongObject_t});
		// Logic Ops
		} else if (node.getValue() == '&') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_and",
					shephongIdent_t, new TypeDesc[] {shephongObject_t});			
		} else if (node.getValue() == '|') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_or",
					shephongIdent_t, new TypeDesc[] {shephongObject_t});			
		} else if (node.getValue() == '!') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_not",
					shephongIdent_t, new TypeDesc[] {shephongObject_t});			
		} else if (node.getValue() == '=') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_eq",
					shephongIdent_t, new TypeDesc[] {shephongObject_t});			
		} else if (node.getValue() == '<') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_lt",
					shephongIdent_t, new TypeDesc[] {shephongObject_t});			
		} else if (node.getValue() == '>') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_gt",
					shephongIdent_t, new TypeDesc[] {shephongObject_t});
		// Lists
		} else if (node.getValue() == '^') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_head",
					shephongObject_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == '_') {			
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_tail",
					shephongList_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == '\"') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_headOfTail",
					shephongObject_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == '@') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_append",
					shephongObject_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == '?') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_isList",
					shephongObject_t, new TypeDesc[] {shephongObject_t});
		// Magic
		} else if (node.getValue() == '\\') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_reflect",
					shephongObject_t, new TypeDesc[] {shephongObject_t});

		// Char <-> Number conversions
		} else if (node.getValue() == 'c') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_char2number",
					shephongObject_t, new TypeDesc[] {shephongObject_t});
		} else if (node.getValue() == 'n') {
			codeBuilder.invokeStatic(shephongStdLib_t, "std_basic_number2char",
					shephongObject_t, new TypeDesc[] {shephongObject_t});

		} else
			throw new RuntimeException("ByteCodeGenerator:\n" +
					" The following OP does not represent a correct and implemented" +
					" basic operation: " + node.getValue());
	}
	
	
	/*
	 * TODO This is a notice for killroy. We need a loop around our calls.
	 * That is because we can have a call as an op in a call as an op in a
	 * call as an op in a call....
	 * 
	 * Simple example: (PARAM ((Y (X foo))
	 * 
	 * This could also be dynamic: (PARAM ((1 2 (callme) ~) ^))
	 * There is a call as an op in a call but we will not know it before
	 * we did not have evaluated the first call (the ^)!  
	 */
}
