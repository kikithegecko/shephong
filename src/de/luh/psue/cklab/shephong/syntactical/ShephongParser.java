package de.luh.psue.cklab.shephong.syntactical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.luh.psue.cklab.shephong.backend.GenerateStdLib;
import de.luh.psue.cklab.shephong.helper.DebugLogger;
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
import de.luh.psue.cklab.shephong.lexical.ShephongScanner;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;
import de.luh.psue.compiler.input.IInputBuffer;
import de.luh.psue.compiler.input.ILocation;
import de.luh.psue.compiler.lexical.IScanner;
import de.luh.psue.compiler.lexical.TokenUtils;
import de.luh.psue.compiler.syntactical.IParser;

/**
 * 
 * @author shephongkrewe (imp)
 *
 */
public class ShephongParser implements IParser<ProgramNode>{
	
	private CompilerLogger logger;
	private ShephongScanner scanner;
	private boolean debuggingOn;

	public ShephongParser(CompilerContext context, IInputBuffer buffer) {
		this.logger = CompilerLogger.getInstance(context);
		this.scanner = new ShephongScanner(context, buffer);
		debuggingOn = false;
	}

	/**
	 * Parse the input
	 * 
	 * @param context a compiler context
	 * @param buffer  a buffer with the input
	 * @param debug   set true if debugging should be used
	 */
	public ShephongParser(CompilerContext context, IInputBuffer buffer, boolean debug) {
		this.logger = CompilerLogger.getInstance(context);
		this.scanner = new ShephongScanner(context, buffer);
		debuggingOn = debug;
	}

	@Override
	public IScanner getScanner() {
		return scanner;
	}
		
	protected void syntacticalError(String message) throws CompilerException {
		logger.criticalError(Reason.Syntactical, scanner.getLocation(), message + ", but was " + TokenUtils.getTokenDescription(scanner));
	}

	@Override
	public ProgramNode parse() throws CompilerException {
		this.scanner.startTokenize();
		DebugLogger.log("#######", debuggingOn);

		ProgramNode program = parseProgram();

		if (this.scanner.getCurrentToken() != ShephongScanner.EOF) {
			syntacticalError("EOF expected");
		}
		
		// remove all nodes which are not AssignmentNodes or MagicNodes on top level.
		ProgramNode retNode = new ProgramNode();
		retNode.setName(program.getName());
		
		for(ShephongNode sn : program.getExpressions()){
			if((sn instanceof AssignmentNode || sn instanceof MagicNode )){
				retNode.addChild(sn);
			}
		}
		return retNode; 
	}

	/*
	 * This method will parse all Tokens from the scanner and return them encapsulated in a ProgramNode.
	 * 
	 * @return The complete ProgramNode, containing as children all statements from the program.
	 * @throws CompilerException
	 */
	private ProgramNode parseProgram() throws CompilerException {
		ProgramNode program = new ProgramNode();
		
		/*
		 * add stdlib into the ProgramNode
		 */
		program.setExpressions(GenerateStdLib.getStdLib());

		while (this.scanner.getCurrentToken() != ShephongScanner.EOF) {
			program.addChild(parseProgramElement(program));
		}

		return program;
	}

	/*
	 * This method will take care for the right amount of parenthesis.
	 * Also it'll mark static Nodes as static.
	 * 
	 * @param program ProgramNode, only passed to assign a module name (if any given).
	 * @return A child node.
	 * @throws CompilerException If something goes wrong ;).
	 */
	private ShephongNode parseProgramElement(ProgramNode program) throws CompilerException{ // TODO maybe ExpressionNode is ok. 
		// TODO handle static / non-static in a different way 
		ShephongNode retNode =  null;
		DebugLogger.log("got: " + this.scanner.getCurrentToken(), debuggingOn);
		
		if(this.scanner.getCurrentToken() == ShephongScanner.DYNAMIC_OPEN){
			this.scanner.getNextToken(); // skip the parenthesis
			DebugLogger.log("(", debuggingOn);
			retNode = this.parseBlock(program);
			
			if(this.scanner.getCurrentToken() != ShephongScanner.DYNAMIC_CLOSE){
				syntacticalError(") expected");
			}
			
			DebugLogger.log(")", debuggingOn);
			this.scanner.getNextToken();
		}
		else if(this.scanner.getCurrentToken() == ShephongScanner.STATIC_OPEN){
			this.scanner.getNextToken();
			DebugLogger.log("[", debuggingOn);
			retNode = parseBlock(program);
			retNode.setStatic(true);
			if(this.scanner.getCurrentToken() != ShephongScanner.STATIC_CLOSE){
				syntacticalError("] expected");
			}
			DebugLogger.log("]", debuggingOn);
			this.scanner.getNextToken();
		}
		else{
			syntacticalError("expected block");
		}
		if(retNode == null){
			DebugLogger.log("return type of parseProgramElement is: " + null, debuggingOn);
		}
		else{
			DebugLogger.log("return type of parseProgramElement is: " + retNode.getClass().getSimpleName(), debuggingOn);
		}
		return retNode;
	}
	
	/*
	 * parseBlock will parse the /inner/ content of a block (therefore excluding the parenthesis.
	 * It returns the the expression as an Node (and doesn't do any marking for static/non-static expressions).
	 * @param program ProgramNode, only to the the module Name if any given.
	 * @return The parsed expression.
	 * @throws CompilerException
	 */
	private ExpressionNode parseBlock(ProgramNode program) throws CompilerException{
		// Thinking in progress....
		
		ExpressionNode node = null;
		
		List<ExpressionNode> tokenStack = new ArrayList<ExpressionNode>();
		
		// parse the inner statement
		while(this.scanner.getCurrentToken() == ShephongScanner.IDENTIFIER
				|| this.scanner.getCurrentToken() == ShephongScanner.CHAR_CONST
				|| this.scanner.getCurrentToken() == ShephongScanner.STRING
				|| this.scanner.getCurrentToken() == ShephongScanner.CONSTANT
				|| this.scanner.getCurrentToken() == ShephongScanner.ASSIGNOP
				|| this.scanner.getCurrentToken() == ShephongScanner.DYNAMIC_OPEN
				|| this.scanner.getCurrentToken() == ShephongScanner.STATIC_OPEN
				|| this.scanner.getCurrentToken() == ShephongScanner.PARAM
				|| this.scanner.getCurrentToken() == ShephongScanner.PARAMLIST
				|| this.scanner.getCurrentToken() == ShephongScanner.NUMBER){
	
			// Handle blocks in blocks and call parseProgramElement to deal with it.
			switch(this.scanner.getCurrentToken()){
			
				case ShephongScanner.DYNAMIC_OPEN:
				case ShephongScanner.STATIC_OPEN:
					node = (ExpressionNode) this.parseProgramElement(program);
					if(node == null){ // in case we've found an empty call node or such.
						node = new CallNode(this.scanner.getLocation(), null, null); // dirty hack.
					}
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
				
				case ShephongScanner.CHAR_CONST:
					node = new CharNode(this.scanner.getLocation(), (Character) this.scanner.getTokenValue());
					this.scanner.getNextToken();
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
					
				case ShephongScanner.CONSTANT:
					if(this.scanner.getTokenValue() instanceof Character){
						node = new OpNode(this.scanner.getLocation(), (Character) this.scanner.getTokenValue());
					}
					else if(this.scanner.getTokenValue() instanceof Integer){
						node = new NumberNode(this.scanner.getLocation(), (Integer) this.scanner.getTokenValue());
					}
					this.scanner.getNextToken();
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
					
				case ShephongScanner.IDENTIFIER:
					node = new IdentNode(this.scanner.getLocation(), (String) this.scanner.getTokenValue());
					this.scanner.getNextToken();
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
				
				case ShephongScanner.ASSIGNOP:
					node = new AssignmentNode(this.scanner.getLocation(), null, null);
					this.scanner.getNextToken();
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
					
				case ShephongScanner.STRING:
					node = makeListNodeFromString(this.scanner.getTokenValue());
					this.scanner.getNextToken();
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
					
				case ShephongScanner.PARAM:
					// Don't know yet which function this parameter belongs to, so leave it blank. 
					node = new ParameterNode (this.scanner.getLocation (), (Integer)this.scanner.getTokenValue ());
					this.scanner.getNextToken();
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
					
				case ShephongScanner.PARAMLIST:
					// Don't know yet which function this paramlist belongs to, so leave it blank. 
					node = new ParameterNode (this.scanner.getLocation (), 0);
					this.scanner.getNextToken();
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
				case ShephongScanner.NUMBER:
					node = makeCallNodeFromNumber((String)this.scanner.getTokenValue());
					this.scanner.getNextToken();
					DebugLogger.log("\tadding a: " + node.getClass().getSimpleName() + " to the tokenStack", debuggingOn);
					tokenStack.add(node);
					break;
				/*	
				case ShephongScanner.MODULESTATEMENT:
					if(tokenStack.isEmpty())
						return parseModuleStatement(program);
					else
						syntacticalError("incorrect placement of a module statement (used as operator with paramenters)");
					break; */
				
			}
		}
		
		// Handle ;name ... ; and ; include ... ; statements
		if(this.scanner.getCurrentToken() == ShephongScanner.MODULESTATEMENT && tokenStack.isEmpty()){
			return parseModuleStatement(program);
		}
		else if(this.scanner.getCurrentToken() == ShephongScanner.MODULESTATEMENT){
			syntacticalError("incorrect placement of a module statement (used as operator with paramenters)");
		}
		
		
		// If LISTOP -> create list node, else, just apply the parameter and operands.
		if(this.scanner.getCurrentToken() == ShephongScanner.LISTOP){
			node = new ListNode(this.scanner.getLocation());
			Collections.reverse(tokenStack);
			for(ExpressionNode e : tokenStack){
				((ListNode) node).addContent(e);
			}
			DebugLogger.log("\tlist", debuggingOn);
			this.scanner.getNextToken();
			return node;
		}
		
		if(this.scanner.getCurrentToken() == ShephongScanner.EVAL){
			if(tokenStack.size() > 1){
				syntacticalError("eval ('`') can only have one argument");
			}
			this.scanner.getNextToken();
			return new MagicNode(this.scanner.getLocation(), (ExpressionNode) tokenStack.get(0));
		}
		
		// Don't add anything behind this, it'll cause problems (a closing parenthesis is the _last_ thing we can find!).
		if(this.scanner.getCurrentToken() == ShephongScanner.DYNAMIC_CLOSE
				|| this.scanner.getCurrentToken() == ShephongScanner.STATIC_CLOSE){
			// TODO pop the stack and create a fitting node from it and return it.
			node = null;
			/* Important Rules:
			 * 1. If there's only one argument, just add it.
			 * 2. If there are 2 or more, add them as list to the Callnode.
			 */
			if(tokenStack.size() > 0 && tokenStack.get(tokenStack.size()-1) instanceof AssignmentNode){
				// 3 for sugared, 2 for salted.
				DebugLogger.log("\tmaking final touches on AssignmentNode", debuggingOn);
				DebugLogger.log("tokenStack.size() = " + tokenStack.size(), debuggingOn);
				if(tokenStack.size() == 3){
					node = tokenStack.remove(tokenStack.size()-1);
					
					ShephongNode ident = tokenStack.remove(tokenStack.size()-1);
					
					// check if it's really an ident:
					if(!(ident instanceof IdentNode)){
						if(ident instanceof CallNode){
							// check it if it's a (hopefully) automagically generated numbercallnode
							CallNode identCall = (CallNode) ident;
							if(identCall.getParam() instanceof NumberNode && ((NumberNode) identCall.getParam()).getValue() == 0){
								ident = identCall.getOp();
							}
						}
						else{
							syntacticalError("expected ident and got: " + tokenStack.get(tokenStack.size()-1).getClass().getSimpleName() );							
						}
					}
					
					((AssignmentNode)node).setIdent((IdentNode) ident);
					((AssignmentNode)node).setExpression(tokenStack.remove(tokenStack.size()-1));
				}
				else if(tokenStack.size() == 2){
					node = tokenStack.remove(tokenStack.size()-1);
					ExpressionNode token = tokenStack.get(0);
					if (!(token instanceof ListNode)) {
						syntacticalError("an assignment with one argument needs a list and not a " + token.getClass().getSimpleName());
					}
					ShephongNode ident = ((ListNode)token).getHead();
					if(!(ident instanceof IdentNode)){
						if(ident instanceof CallNode){
							// check it if it's a (hopefully) automagically generated numbercallnode
							CallNode identCall = (CallNode) ident;
							if(identCall.getParam() instanceof NumberNode && ((NumberNode) identCall.getParam()).getValue() == 0){
								ident = identCall.getOp();
							}
						}
						else{
							syntacticalError("first element of list must be a identifier (and not a: " + ident.getClass().getSimpleName());
						}
					}
					((AssignmentNode)node).setIdent((IdentNode) ident);
					((AssignmentNode)node).setExpression((ExpressionNode) ((ListNode)tokenStack.get(0)).getTail());
				}
				else{
					syntacticalError("not the right number of parameters for assignment op (should be 1 or 2 and not: " + 
							(tokenStack.size()-1)+ ")");
				}
			}
			else if(tokenStack.size() > 0 && tokenStack.get(tokenStack.size()-1) instanceof MagicNode){
				// TODO do we really need this here?
				syntacticalError("not implemented yet");
			}
			else if(tokenStack.size()>=1 && 
					(tokenStack.get(tokenStack.size()-1) instanceof ListNode 
							|| tokenStack.get(tokenStack.size()-1) instanceof IdentNode
							|| tokenStack.get(tokenStack.size()-1) instanceof OpNode
							|| tokenStack.get(tokenStack.size()-1) instanceof CallNode
							|| tokenStack.get(tokenStack.size()-1) instanceof ParameterNode
							// the following two checks are only to give the semantically checker some work :)
							|| tokenStack.get(tokenStack.size()-1) instanceof NumberNode
							|| tokenStack.get(tokenStack.size()-1) instanceof CharNode
							|| false )){ // TODO add some more here?)
				if(tokenStack.size() == 1){
					DebugLogger.log("making callnode with 0 arguments.", debuggingOn);
					node = new CallNode(this.scanner.getLocation(), tokenStack.get(0), null);
				}
				// Rule 1:
				if(tokenStack.size() == 2){ // one argument + one op = 2.
					DebugLogger.log("making callnode with 1 argument.", debuggingOn);
					node = new CallNode(this.scanner.getLocation(),
							tokenStack.get(tokenStack.size()-1), 
							tokenStack.get(0));
				}
				// Rule 2:
				else if(tokenStack.size() > 2){
					DebugLogger.log("making callnode with >=2 arguments.", debuggingOn);
					ListNode paramList = new ListNode(this.scanner.getLocation());
					for(int i = tokenStack.size()-2; i >= 0;  i--){ // don't add the op into the list -> -1.
						paramList.addContent(tokenStack.get(i));
					}
					node = new CallNode(this.scanner.getLocation(),
							tokenStack.get(tokenStack.size()-1), 
							paramList);
				}
			}
			else {
				DebugLogger.log("maybe something went wrong (like an empty statement)", debuggingOn);
			}
			return node;
		}

		syntacticalError("missing closing block (')' or '}'");
		return node; // We'll never reach it - but it makes eclipse happy :).
	}
	
	/**
	 * Generates a ListNode from a given String.
	 * TODO wrong order right now.
	 * @param tokenValue
	 * @return
	 * @throws CompilerException
	 */
	private ExpressionNode makeListNodeFromString(Object tokenValue) throws CompilerException {
		if(!(tokenValue instanceof String)){
			syntacticalError("Expected a String Object and not a: " + tokenValue.getClass().getSimpleName());
		}
		DebugLogger.log("generating list from: \"" + (String)tokenValue + "\":", debuggingOn);
		ListNode retList = new ListNode(this.scanner.getLocation());
		char[] chars = ((String)tokenValue).toCharArray();
		for(char c : chars) {
			// Note: the locations fits only for the start of the String.
			DebugLogger.log("\tadding char: " + c, debuggingOn);
			retList.addContent(new CharNode(this.scanner.getLocation(), c));
		}
		DebugLogger.log("string2list done.", debuggingOn);
		return retList;
	}

	/**
	 * 
	 * @param program We need this to set the program name.
	 * @return ModuleNode in case of an include statement, null if it's a name statement.
	 * @throws CompilerException
	 */
	private ExpressionNode parseModuleStatement(ProgramNode program) throws CompilerException{
		ExpressionNode ret = null;
		this.scanner.getNextToken();
		
		if(!(this.scanner.getCurrentToken() == ShephongScanner.IDENTIFIER)){ 
			syntacticalError("expecting ident");
		}
		if(this.scanner.getTokenValue().equals("name")){
			this.scanner.getNextToken();
			if(!(this.scanner.getCurrentToken() == ShephongScanner.IDENTIFIER
					|| (this.scanner.getCurrentToken() == ShephongScanner.NUMBER && ((String)this.scanner.getTokenValue()).length() >= 1))){
				syntacticalError("expecting ident");
			}
			program.setName((String) this.scanner.getTokenValue());
			DebugLogger.log("\tmodulename: " + (String) this.scanner.getTokenValue(), debuggingOn);
		}
		else if(this.scanner.getTokenValue().equals("include")){
				this.scanner.getNextToken();
				// accept also NUMBER Tokens, if the String length is greater equal 1 (/casting/ to IdentNode).
				if(!((this.scanner.getCurrentToken() == ShephongScanner.IDENTIFIER)
						|| (this.scanner.getCurrentToken() == ShephongScanner.NUMBER && ((String)this.scanner.getTokenValue()).length() >= 1))){
					syntacticalError("expected ident");
				}
				ret = new ModuleNode(this.scanner.getLocation(), (String)this.scanner.getTokenValue());
				DebugLogger.log("\tinclude: " + (String) this.scanner.getTokenValue(), debuggingOn);
		}
		else{
			syntacticalError("expecting \"name\" or \"include\"");
		}
		this.scanner.getNextToken();
		
		
		if(!(this.scanner.getCurrentToken() == ShephongScanner.MODULESTATEMENT)){
			syntacticalError("expected ';'");
		}
		this.scanner.getNextToken();
		return ret;
	}
	
	private ExpressionNode makeCallNodeFromNumber(String number) throws CompilerException{
		if((!(number instanceof String)) && number.length() > 0){
			syntacticalError("expected a String Object and got: " + number.getClass().getSimpleName());
		}
		return makeCallNodeFromNumber(number, null);
	}
	
	private ExpressionNode makeCallNodeFromNumber(String number, CallNode callNode){
		DebugLogger.log("number is: " + number, debuggingOn);
		if(callNode == null){
			callNode = new CallNode(this.scanner.getLocation(), null, null);
			// make a CallNode from the first two idents.
			if(number.length() >= 1){
				/* start the first callnode like: (#0 1)
				 * it has the benefit we wont't need to catch any empty arguments
				 * inside the definition of our numbers like: 
				 * 		( (((#10 $ #*) #1 ~) #+) 1 :).
				 * (Multiplying the 10 with 0 doesn't really hurt besides some cpu time ;).
				 */
				callNode.setParam(new NumberNode(this.scanner.getLocation(), 0));
				callNode.setOp(new IdentNode(this.scanner.getLocation(), number.substring(0, 1)));
				return makeCallNodeFromNumber(number.substring(1), callNode);
			}
		}
		// if we've got a call node:
		else {
			if(number.length() > 1){
				CallNode tempCallNode = new CallNode(this.scanner.getLocation(), 
									new IdentNode(this.scanner.getLocation(), number.substring(0, 1)),
									callNode);
				return makeCallNodeFromNumber(number.substring(1), tempCallNode);
			}
			else if(number.length() == 1 && callNode != null){
				return new CallNode(this.scanner.getLocation(),
									new IdentNode(this.scanner.getLocation(), number),
									callNode);
			}
		}
		return callNode; // make eclipse happy ;)
	}
	
	@Override
	public ILocation getLocation() {
		return this.scanner.getLocation();
	}

	@Override
	public CompilerContext getContext() {
		return this.scanner.getContext();
	}

}
