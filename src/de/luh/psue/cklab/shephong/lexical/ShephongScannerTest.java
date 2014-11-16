package de.luh.psue.cklab.shephong.lexical;

import static de.luh.psue.compiler.lexical.Assert.*;

import static de.luh.psue.cklab.shephong.lexical.ShephongScanner.*;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import de.luh.psue.compiler.error.CompilerException;

/**
 * The tests contained in this class will put our scanner
 * to the acid test!
 * @author shephongkrewe
 *
 */
public class ShephongScannerTest extends TestCase{
	
	/**
	 * This method is used to test if all known tokens can be read.
	 * Therefore the tokens are just ordered by define occurrence
	 * in ShephongScanner.
	 * @throws IOException In case of... hey it's a lexer!
	 * It's fairly obvious, really
	 * @throws CompilerException This comes from the magic framework keep attention!
	 */
	@Test
	public void testTokenize() throws IOException, CompilerException {
		/*
		 * Put all tokens into the input String here one time to test
		 * if all tokens will be recognized correctly
		 * 
		 * TODO let us see if the following is wise:
		 * [..] CURLEY_BRACKET_OPEN, COMMENT, CURLEY_BRACKET_CLOSE [..]
		 * and
		 * [..] DOUBLE_QUOTE, STRING, DOUBLE_QUOTE [..]
		 * 
		 * Adopt changes into whitespaces(), too
		 */
		ShephongScanner scanner = createScanner(ShephongScanner.class,
				"() [ ] {b \"a\" } \"pan{ t}ies \"#1 #+ #1337 'c ~ 2 :");
		assertNextTokens(scanner, DYNAMIC_OPEN, DYNAMIC_CLOSE, STATIC_OPEN, STATIC_CLOSE,
				STRING, CONSTANT, CONSTANT, CONSTANT, CHAR_CONST, LISTOP, NUMBER, ASSIGNOP, EOF);
	}
	
	/**
	 * @see ShephongScannerTest#tokenize()
	 * But with whitespaces
	 * @throws IOException In case of... hey it's a lexer!
	 * It's fairly obvious, really
	 * @throws CompilerException This comes from the magic framework keep attention!
	 */
	@Test
	public void testWhitespaces() throws IOException, CompilerException {
		/*
		 * Do the same tests here but also scan for whitespaces
		 * (put WHITESPACE at each place where to find such character)
		 */
		ShephongScanner scanner = createScanner(ShephongScanner.class,
				"() [ ] {b \"a\" } \"pan{ t}ies \"#1 #+ #1337 'c ~ 2 :");
		scanner.setScanWhitespaces(true);
		assertNextTokens(scanner, DYNAMIC_OPEN, DYNAMIC_CLOSE, WHITESPACE, STATIC_OPEN, WHITESPACE,
				STATIC_CLOSE, WHITESPACE, COMMENT, WHITESPACE, STRING, CONSTANT, WHITESPACE, CONSTANT,
				WHITESPACE, CONSTANT, WHITESPACE, CHAR_CONST, WHITESPACE, LISTOP, WHITESPACE,
				NUMBER, WHITESPACE, ASSIGNOP, EOF);
	}
	
	/**
	 * This method is primary used to test if the scanner can handle
	 * a fairly short and correct written piece of code which only
	 * uses base grammar. If the lexer is unable to scan this something
	 * very basic and needed is broken.
	 * @throws CompilerException This comes from the magic framework keep attention!
	 */
	@Test
	public void testFriendlyCode() throws CompilerException {
		ShephongScanner scanner = createScanner(ShephongScanner.class, "((1 2 ~) f :)");
		assertNextTokens(scanner, DYNAMIC_OPEN, DYNAMIC_OPEN, NUMBER, NUMBER, LISTOP,
				DYNAMIC_CLOSE, IDENTIFIER, ASSIGNOP, DYNAMIC_CLOSE, EOF);		
	}
	
	/**
	 * @see ShephongScannerTest#friendlyCode()
	 * But with whitespaces
	 * @throws CompilerException This comes from the magic framework keep attention!
	 */
	@Test
	public void testFriendlyCodeWhitespaces() throws CompilerException {
		ShephongScanner scanner = createScanner(ShephongScanner.class, "((1 2 ~) f :)");
		scanner.setScanWhitespaces(true);
		assertNextTokens(scanner, DYNAMIC_OPEN, DYNAMIC_OPEN, NUMBER, WHITESPACE, NUMBER,
				WHITESPACE, LISTOP, DYNAMIC_CLOSE, WHITESPACE, IDENTIFIER, WHITESPACE, ASSIGNOP,
				DYNAMIC_CLOSE, EOF);
	}
	
	@Test
	public void testComments() throws CompilerException {
		ShephongScanner scanner = createScanner(ShephongScanner.class,
				" {co\"mm}\"ent} {b {\"a{} }\" } } ");
		assertNextTokens(scanner, EOF);
	}
	
	@Test
	public void testCommentsWhitespaces() throws CompilerException {
		ShephongScanner scanner = createScanner(ShephongScanner.class,
				" {co\"mm}\"ent} {b {\"a{} }\" } } ");
		scanner.setScanWhitespaces(true);
		assertNextTokens(scanner, WHITESPACE, COMMENT, WHITESPACE, COMMENT, WHITESPACE, EOF);
	}
	
	@Test
	public void testQuoteCharTest () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"{ '} \"}\" }");
		scanner.setScanWhitespaces(true);
		assertNextTokens (scanner, COMMENT, EOF);
	}
	
	/*
	 * This tests might not be for the final code because it should never
	 * succeed with a working scanner. The comment is just not closed.
	 */
	@Test
	public void testFailCommentEOF () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"{this is finnish");
		scanner.setScanWhitespaces(true);
		assertNextTokens (scanner, COMMENT, EOF);
	}
	
	@Test
	public void testFailStringEOF () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"\"but not the end");
		assertNextTokens (scanner, STRING, EOF);
	}
	
	/*
	 * The scanner must return additional closing braces of a comment.
	 * This must be handeled in the parser.
	 */
	@Test
	public void testCommentAdditionalClose () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"{a{comment}isnt}}{badly'nested}");
		assertNextTokens (scanner, '}', EOF);
	}
	
	/*
	 * This test is from the parser suite.
	 */
	@Test
	public void testDynamicStaticMixed () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"(['b 4 +] f :)");
		assertNextTokens (scanner, DYNAMIC_OPEN, STATIC_OPEN, CHAR_CONST,
				NUMBER, IDENTIFIER, STATIC_CLOSE, IDENTIFIER, ASSIGNOP,
				DYNAMIC_CLOSE, EOF);
	}
	
	@Test
	public void testHashFunWithoutSpaces () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"#111z #zzz #1#z #z ##z");
		assertNextTokens (scanner,
				CONSTANT, IDENTIFIER,
				CONSTANT, IDENTIFIER,
				CONSTANT, CONSTANT,
				CONSTANT,
				CONSTANT, IDENTIFIER,
				EOF);
	}

	@Test
	public void testMoneyFunWithoutSpaces () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"$111z $zzz $1$z $z $$z");
		assertNextTokens (scanner,
				PARAM, IDENTIFIER,
				PARAMLIST, IDENTIFIER,
				PARAM, PARAMLIST, IDENTIFIER,
				PARAMLIST, IDENTIFIER,
				PARAMLIST, PARAMLIST, IDENTIFIER,
				EOF);
	}
	
	@Test
	public void testMagicEval () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"`'`\"`'`");
		assertNextTokens (scanner,
				EVAL, CHAR_CONST, STRING, EOF);
	}
	
	@Test
	public void testIncludeMe () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				";name ICH  ; ;include DU; ");
		assertNextTokens (scanner,
				MODULESTATEMENT, IDENTIFIER, IDENTIFIER, MODULESTATEMENT,
				MODULESTATEMENT, IDENTIFIER, IDENTIFIER, MODULESTATEMENT,
				EOF);
	}
	
	@Test
	public void testMoreStrangeChars () throws CompilerException {
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"(5 >) <3 d@d @ '<3 Micro$oft An$1cht");
		assertNextTokens (scanner,
				DYNAMIC_OPEN, NUMBER, IDENTIFIER, DYNAMIC_CLOSE,
				IDENTIFIER, IDENTIFIER, IDENTIFIER, CHAR_CONST, NUMBER,
				IDENTIFIER, IDENTIFIER,
				EOF);
	}
	
	@Test
	public void testBigIntegers () throws CompilerException {
		ShephongScanner scanner = createScanner(ShephongScanner.class,
				"#123 #1234567890 #123456789012345678901234567890 #42");
		assertNextTokens (scanner,
				CONSTANT, CONSTANT, CONSTANT, CONSTANT, EOF);
	}
	
	@Test
	public void testBasicSymbols () throws CompilerException {
		// isList? should be ~? but is not implemented
		ShephongScanner scanner = createScanner (ShephongScanner.class,
				"+ - * / % & | ! = < > ~ ^ _ @ ~? : ? $ $25 *crp");
		assertNextTokens (scanner,
				// Matheops
				IDENTIFIER, IDENTIFIER, IDENTIFIER, IDENTIFIER,IDENTIFIER, 
				// Logikops
				IDENTIFIER, IDENTIFIER, IDENTIFIER, IDENTIFIER, IDENTIFIER, IDENTIFIER,
				// Listen
				LISTOP, IDENTIFIER, IDENTIFIER, IDENTIFIER, LISTOP, IDENTIFIER,
				// Zuweisung
				ASSIGNOP,
				// If
				IDENTIFIER,
				// Other
				PARAMLIST, PARAM, IDENTIFIER,
				EOF
				);
	}
}	
