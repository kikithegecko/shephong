package de.luh.psue.cklab.shephong.lexical;

import java.io.IOException;

import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;
import de.luh.psue.compiler.input.IInputBuffer;
import de.luh.psue.compiler.lexical.AbstractScanner;
import de.luh.psue.compiler.lexical.TokenDescription;
import de.luh.psue.compiler.lexical.TokenFormat;
import de.luh.psue.compiler.lexical.TokenFormats;
import de.luh.psue.compiler.lexical.TokenType;
import de.luh.psue.compiler.lexical.TokenValueType;

/**
 * An implementation of a lexical scanner for the programming language
 * Shephong.
 * 
 * Includes definitions for token classes which will be used by the
 * eclipse build-in editor.
 * 
 * Every value assigned to a token should be > 255 (because everything below is reserved 
 * for the ascii representation of the chars).
 * 
 * @author shephongkrewe (killroy)
 *
 */

@TokenFormats({
	@TokenFormat(type = "BRACKET", foreground = { 255, 0, 0 }),
	@TokenFormat(type = "BRACE", foreground = { 0, 0, 255 }),
	@TokenFormat(type = "COMMENT", foreground = { 100, 100, 255 }),
	@TokenFormat(type = "STRING", foreground = { 0, 50, 200 }),
	@TokenFormat(type = "CONSTANT", foreground = { 0, 255, 0 }),
	@TokenFormat(type = "CHAR_CONST", foreground = { 125, 125, 0 }),
	@TokenFormat(type = "LISTOP", foreground = { 100, 100, 100 }),
	@TokenFormat(type = "IDENTIFIER", foreground = { 0, 0, 0 }),
	@TokenFormat(type = "ASSIGNOP", foreground = { 127, 0, 85 }, style = { "BOLD" }),
	@TokenFormat(type = "PARAMLIST", foreground = { 255, 0, 255 }, style = { "BOLD" }),
	@TokenFormat(type = "PARAM", foreground = { 255, 0, 255 }),
	@TokenFormat(type = "EVAL", foreground = { 0, 0, 0 }),
	@TokenFormat(type = "NUMBER", foreground = { 0, 0, 0 }),
	//TODO change colors
})

public class ShephongScanner extends AbstractScanner{

	// TODO understand what is @TokenValueType(char.class)
	@TokenType("BRACKET")
	@TokenDescription(description = "bracket_open", needValue = false)
	@TokenValueType(char.class)
	public static final int DYNAMIC_OPEN = '(';
	
	@TokenType("BRACKET")
	@TokenDescription(description = "bracket_close", needValue = false)
	@TokenValueType(char.class)
	public static final int DYNAMIC_CLOSE = ')';
	
	@TokenType("BRACE")
	@TokenDescription(description = "brace_open", needValue = false)
	@TokenValueType(char.class)
	public static final int STATIC_OPEN = '[';
	
	@TokenType("BRACE")
	@TokenDescription(description = "brace_close", needValue = false)
	@TokenValueType(char.class)
	public static final int STATIC_CLOSE = ']';

	@TokenType("COMMENT")
	@TokenDescription(description = "comment", needValue = true)
	@TokenValueType(String.class)
	public static final int COMMENT = 280;

	@TokenType("STRING")
	@TokenDescription(description = "string", needValue = true)
	@TokenValueType(String.class)
	public static final int STRING = 290;

	@TokenType("CONSTANT") //char combination where the first char is a '#'
	@TokenDescription(description = "constant", needValue = true)
	@TokenValueType(String.class)
	public static final int CONSTANT = 300;

	@TokenType("CHAR_CONST") //two-char combination where the first char is a ''' ;)
	@TokenDescription(description = "char constant", needValue = true)
	@TokenValueType(String.class)
	public static final int CHAR_CONST = 310;

	@TokenType("LISTOP")
	@TokenDescription(description = "list operator", needValue = false)
	@TokenValueType(char.class)
	public static final int LISTOP = '~';

	@TokenType("IDENTIFIER")
	@TokenDescription(description = "identifier", needValue = true)
	@TokenValueType(String.class)
	public static final int IDENTIFIER = 400;
	
	@TokenType("ASSIGNOP")
	@TokenDescription(description = "assign operator", needValue = true)
	@TokenValueType(String.class)
	public static final int ASSIGNOP = ':';

	@TokenType("PARAMLIST")
	@TokenDescription(description = "parameter list", needValue = false)
	@TokenValueType(char.class)
	public static final int PARAMLIST = '$';

	@TokenType("PARAM")
	@TokenDescription(description = "parameter element", needValue = true)
	@TokenValueType(char.class)
	public static final int PARAM = 500;
	
	@TokenType("EVAL")
	@TokenDescription(description = "evaluation force", needValue = false)
	@TokenValueType(char.class)
	public static final int EVAL = '`';

	@TokenType("MODULESTATEMENT")
	@TokenDescription(description = "module statement", needValue = false)
	@TokenValueType(char.class)
	public static final int MODULESTATEMENT = ';';
	
	@TokenType("NUMBER")
	@TokenDescription(description = "number", needValue = true)
	@TokenValueType(char.class)
	public static final int NUMBER = 600;

	private CompilerLogger logger;

	public ShephongScanner(CompilerContext context, IInputBuffer buffer) {
		super(context, buffer);
		this.logger = CompilerLogger.getInstance(context);
	}

	@Override
	public boolean canScanWhitespaces() {
		return true;
	}
	
	@Override
	protected boolean nextToken() throws IOException, CompilerException {
		/*
		 * just read whitespaces and do nothing with them
		 */
		if (Character.isWhitespace(currentChar)) {
			currentToken = WHITESPACE;
			this.readWhitespace();
			return !scanWhitespaces; // dunno why (if) this is needed, it's magic
		}
		/*
		 * scan whole comments
		 * (even strings in comments in strings in comments and such)
		 */
		else if (currentChar == '{') {
			this.readComment();
			return !scanWhitespaces; // dunno why (if) this is needed, it's magic
		}
		/*
		 * scan strings
		 */
		else if (currentChar == '"') {
			this.readString();
			return false;
		}
		/*
		 * scan all kinds of CONSTANT
		 */
		else if (currentChar == '#') {
			currentToken = CONSTANT;
			
			nextChar();
			if (Character.isDigit(currentChar)) {
				currentValue = this.readNumber();
			}
			else {
				currentValue = (char)currentChar;
				nextChar();
			}
			return false;
		}
		/*
		 * scan any CONST_CHAR
		 */
		else if (currentChar == '\'') {
			currentToken = CHAR_CONST;
			nextChar();
			currentValue = (char)currentChar;
			nextChar();
			return false;
		}
		/*
		 * scan for PARAM[LIST]
		 */
		else if (currentChar == '$') {
			currentToken = PARAMLIST;
			nextChar();
			
			if (Character.isDigit(currentChar))
				currentToken = PARAM;
			else
				return false;
			
			StringBuilder buffer = new StringBuilder();
			while (Character.isDigit(currentChar)) {
				buffer.append((char) currentChar);
				nextChar();
			}
			currentValue = Integer.parseInt(buffer.toString());
			return false;
		}
		/*
		 * scan for MODULESTATEMENTs
		 */
		else if (currentChar == ';') {
			currentToken = MODULESTATEMENT;
			nextChar();
			return false;
		}
		/*
		 * scan for EVALs
		 */
		else if (currentChar == '`') {
			currentToken = EVAL;
			nextChar();
			return false;
		}
		/*
		 * scan for special number token :)
		 */
		else if (Character.isDigit(currentChar)) {
			currentToken = NUMBER;
			currentValue = this.readIdentifier();
			return false;
		}
		/*
		 * scan for listop ~
		 */
		else if (currentChar == '~') {
			currentToken = LISTOP;
			nextChar ();
			return false;
		}
		/*
		 * scan for IDENTIFIER (that is nearly everything!)
		 * Maybe this should be the second last test here!
		 */
		else if ((this.isShephongIdentifierPart(currentChar))
				// the following tokens must never be identifiers!!!
				&& currentChar != '$'
				&& currentChar != ':'
				&& currentChar != '~'
				&& currentChar != '`'
				&& !Character.isDigit(currentChar))
		{
			currentToken = IDENTIFIER;
			currentValue = this.readIdentifier();
			return false;
		}
		/*
		 * if nothing else fits call super man!
		 * let this be last!
		 */
		else {
			return super.nextToken();
		}
	}
	
	/**
	 * This method reads the rest chars in an IDENTIFIER.
	 * @return the IDENTIFIER (*not* including the startsymbol)
	 * @throws IOException
	 */
	private String readIdentifier() throws IOException {
		StringBuilder buffer = new StringBuilder();
		do {
			buffer.append((char) currentChar);
			nextChar();
		} while (this.isShephongIdentifierPart(currentChar));
		return buffer.toString();
	}
	
	private int readNumber() throws IOException, CompilerException {
		StringBuilder buffer = new StringBuilder();
		while (Character.isDigit(currentChar)) {
			buffer.append((char) currentChar);
			nextChar();
		}
		try {
			return Integer.parseInt(buffer.toString());
		} catch (NumberFormatException e) {
			logger.uncriticalError(Reason.Lexial, this.getLocation(), "number out of range");
			return -1;
		}
	}

	private void readString() throws IOException {
		currentToken = STRING;
		currentValue = "";
		
		do {
			nextChar();
			
			// do not scan EOF as a string
			if (currentChar < 0)
				return;
			
			if (currentChar != '"')
				currentValue = currentValue.toString() + Character.toString((char) currentChar);
		} while (currentChar != '"');
		
		nextChar(); // CALL IT, EVERY TIME, CALL IT!! (cost me 30min.)
	}

	private void readComment() throws IOException {
		
		currentToken = COMMENT;
		currentValue = ""; // use String as Object and save the content
		
		/*
		 * keep track on comments in comments
		 */
		short curlyBracketCount = 1; // we've already read one '{'
		short doubleQuoteCount = 0;
		
		do {
			nextChar();
			
			// do not scan EOF as a comment
			if (currentChar < 0)
				return;
			
			// do not count { and } in strings
			if (currentChar == '"')
				doubleQuoteCount++;
			if (!(doubleQuoteCount % 2 == 0)) {
				currentValue = currentValue.toString() + currentChar;
				continue;
			}
			
			// do not count { and } after a '
			if (currentChar == '\'') {
				currentValue = currentValue.toString() + currentChar;
				nextChar();
				currentValue = currentValue.toString() + currentChar;
				continue;
			}
			
			// count nested comments
			if (currentChar == '}')
				curlyBracketCount--;
			else if (currentChar == '{')
				curlyBracketCount++;
			
			if (curlyBracketCount > 0)
				currentValue = currentValue.toString() + currentChar;
		} while (curlyBracketCount > 0);
		
		nextChar();
	}

	private void readWhitespace() throws IOException {
		do {
			nextChar();
		} while (Character.isWhitespace(currentChar));
	}
	
	private boolean isShephongIdentifierPart(int currentChar) {
		if (Character.isJavaIdentifierPart(currentChar) ||
				currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' ||
				currentChar == '^' || currentChar == '?' || currentChar == '|' || currentChar == '&' ||
				currentChar == '!' || currentChar == ':' || currentChar == '%' || currentChar == '=' ||
				currentChar == '~' || currentChar == '@' || currentChar == '<' || currentChar == '>' ||
				currentChar == '\\')
			return true;
		return false;
	}

}
