package de.luh.psue.cklab.shephong.syntactical;

import static de.luh.psue.compiler.syntactical.Assert.*;
import junit.framework.TestCase;
import de.luh.psue.cklab.shephong.backend.GenerateStdLib;
import de.luh.psue.cklab.shephong.lexical.ShephongScannerTest;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.Reason;

import org.junit.Test;

/**
 * JUnit tests for the shephong parser.
 * 
 * @author shephongkrewe (kiki)
 *
 */
public class ShephongParserTest extends TestCase{
	private static String stdLib = GenerateStdLib.getStdLibString();
	/**
	 * Simple tests for the beginning
	 * 
	 * @throws CompilerException in case of wrong parsing.
	 */
	@Test
	public void testSimple() throws CompilerException{
		assertParsable(ShephongParser.class, "()", stdLib);
		assertParsable(ShephongParser.class, "", stdLib);
//		assertParsable(ShephongParser.class, "(2 3)", stdLib + "3(2(#0))\n"); // TODO
		assertParsable(ShephongParser.class, "((ds 5 +)`)", stdLib + "`(+(list(5(#0), ds)))\n");
		assertParsable(ShephongParser.class, "(['b 4 +] f :)", stdLib + "(f = +[list(4(#0), b)])\n");
		assertParsable(ShephongParser.class, "((2 3 b ?)`)", stdLib + "`(?(list(b, 3(#0), 2(#0))))\n");
		assertParsable(ShephongParser.class, "((3 >)`)", stdLib + "`(>(3(#0)))\n");
		assertParsable(ShephongParser.class, "((($ ^) baz :) foo :)", stdLib + "(foo = (baz = ^($)))\n");
	}
	
	@Test
	public void testAssignments() throws CompilerException{
		assertParsable(ShephongParser.class, "(#1 1 :)", stdLib + "(1 = #1)\n");
	}
	/**
	 * Some tests for the list syntax.
	 * 
	 * @throws CompilerException in case of wrong parsing.
	 */
	@Test
	public void testLists() throws CompilerException{
		assertParsable(ShephongParser.class, "((1 2 3 ~)`)", stdLib + "`(list(3(#0), 2(#0), 1(#0)))\n");
		assertParsable(ShephongParser.class, "([a b c ~]`)", stdLib + "`(list[c, b, a])\n");
		assertParsable(ShephongParser.class, "((a (b c ~) 3 ~)`)", stdLib + "`(list(3(#0), list(c, b), a))\n");
		assertParsable(ShephongParser.class, "((('s h u ~) [8 4 +] 6 ~)`)", stdLib + "`(list(6(#0), +[list(4(#0), 8(#0))], list(u, h, s)))\n");
		assertParsable(ShephongParser.class, "(#1 2 #+) ((1 #2 +)`)", stdLib + "`(+(list(#2, 1(#0))))\n");
	}
	
	/**
	 * Tests for the comment syntax. I know, this was already tested 
	 * in {@link ShephongScannerTest} but we want to make assurance
	 * double sure!
	 * 
	 * @throws CompilerException in case of wrong parsing.
	 */
	@Test
	public void testComments() throws CompilerException{
		assertParsable(ShephongParser.class, "{ comment! }", stdLib);
		assertParsable(ShephongParser.class, "{ (b a :) }", stdLib);
		assertParsable(ShephongParser.class, "(3) {<-three, four ->} (3 4 :)", stdLib + "(4 = 3(#0))\n");
		assertParsable(ShephongParser.class, "{ \"}\" }", stdLib);
		assertParsable(ShephongParser.class, "{ foo { bar } baz }", stdLib);
	}
	
	/**
	 * Tests for the string syntax. Same as with {@link ShephongParserTest#commentTests()}:
	 * double assurance!
	 * 
	 * @throws CompilerException in case of wrong parsing.
	 */
	@Test
	public void testStrings() throws CompilerException{
		assertParsable(ShephongParser.class, "((123 \"foo\" ~)`)", stdLib + "`(list(list(f, o, o), 3(2(1(#0)))))\n");
		assertParsable(ShephongParser.class, "((asd \")\" f)`)", stdLib + "`(f(list(list()), asd)))\n");
		assertParsable(ShephongParser.class, "{ comment w \"} string\" ?}", stdLib);
	}
	
	/**
	 * Tests for more complex statements including nested statements
	 * and various combinations of dynamic statements, static statements,
	 * comments and strings.
	 * 
	 * @throws CompilerException in case of wrong parsing.
	 */
	@Test
	public void testComplexs() throws CompilerException{
		assertParsable(ShephongParser.class, 
				"([(4 7 8 ~) {<-list} ($2 $1 (0 2 =) ?) (\"foo!\" \"bar\" =) ?]`)",
				stdLib + "`(?[list(=(list(list(b, a, r), list(f, o, o, !))), ?(list(=(list(2(#0), 0(#0))), $1, $2)), list(8(#0), 7(#0), 4(#0)))])\n");
		assertParsable(ShephongParser.class,  
				"((((($1 2 -) (($1 1 -) fib) +) {else} 1 {then} (1 $1 =) {Bedingung} ?) {if, else-Teil vom ersten if} 0 {then} (0 $1 =) {Bedingung} ?) {if} fib :)",
				stdLib + "(fib = ?(list(=(list($1, 0(#0))), 0(#0), ?(list(=(list($1, 1(#0))), 1(#0), +(list(fib(-(list(1(#0), $1))), -(list(2(#0), $1)))))))))\n");
		assertParsable(ShephongParser.class, 
				"((((($ _) sum) ($^) +) 0 (($^) *crp* =) ?) sum :)", 
				stdLib + "(sum = ?(list(=(list(*crp*, ^($))), 0(#0), +(list(^($), sum(_($)))))))\n");
		assertParsable(ShephongParser.class,  
				"([( \"abc\" (($^) 1 -) 2 /) (17 #3 *) ~] abc :)", 
				stdLib + "(abc = list[*(list(#3, 7(1(#0)))), /(list(2(#0), -(list(1(#0), ^($))), list(a, b, c)))])\n");
		assertParsable(ShephongParser.class,  
				"((fs 'a)`) ([z (1 2 3 ~)]`) {crazy! \":}\"} (($_) a :)", 
				stdLib + "`(a(fs))\n`(list(3(#0), 2(#0), 1(#0))[z])\n" +
				"(a = _($))\n");
	}

	@Test
	public void testIncludes () throws CompilerException {
		// TODO if we want modules later, uncomment the lines below and patch the parser!
		assertParsable (ShephongParser.class,
				"((4 foo) bar :) (;include ESSEN;) [;include TRINKEN ; ]", 
//				"modules used by this program:\n" +
//				"module(ESSEN), module(TRINKEN)\n" +
				stdLib +
				"(bar = foo(4(#0)))\n"); //+
//				"module(ESSEN)\n" +
//				"module(TRINKEN)\n");
		assertParsable (ShephongParser.class,
				"((4 (; include 1   ;) )[ ;include include ;] )", stdLib);
	}
	
	@Test
	public void testEvals() throws CompilerException {
		assertParsable(ShephongParser.class,
				"((17 fak) `)",
				stdLib + "`(fak(7(1(#0))))\n");
	}
	
	@Test
	public void testNewMagicNodes() throws CompilerException {
		/*
		 * NOTE: the parenthesis are from the magicnode itself, they are _NOT_ from a callnode.
		 */
		// number
		assertParsable(ShephongParser.class, "(#1 `)", stdLib + "`(#1)\n");
		
		// char
		assertParsable(ShephongParser.class, "('a `)", stdLib + "`(a)\n");
		
		// ident
		assertParsable(ShephongParser.class, "(a `)", stdLib + "`(a)\n");
		
		// list
		assertParsable(ShephongParser.class, "((#1 #2 #3 ~) `)", stdLib + "`(list(#3, #2, #1))\n");
		
		// call, 1 will be converted to (#0 1)
		assertParsable(ShephongParser.class, "(1 `)", stdLib + "`(1(#0))\n");
		
		// basic op ????
		assertParsable(ShephongParser.class, "(#+ `)", stdLib + "`(#+)\n");
		
		
		// test later, maybe
//		// module node -> not parsable
//		assertNotParsable(ShephongParser.class, "", stdLib + "`");
//		
//		// magicnode -> not parsable
//		assertParsable(ShephongParser.class, "", stdLib + "`");
	}

	@Test
	public void testFailures() throws CompilerException{
		// TODO uncomment in case of further implemenation of the rest...
//		assertNotParsable(ShephongParser.class, "\"string\"", null);
		assertNotParsable(ShephongParser.class, "(a) b)", 4, Reason.Syntactical);
		assertNotParsable(ShephongParser.class, "(ab ()", 6, Reason.Syntactical);
	}
}
