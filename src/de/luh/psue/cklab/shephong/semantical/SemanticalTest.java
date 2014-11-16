package de.luh.psue.cklab.shephong.semantical;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import de.luh.psue.cklab.shephong.backend.destatic.RemoveStaticWalker;
import de.luh.psue.cklab.shephong.backend.desugar.DeSugarParam;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.cklab.shephong.syntactical.ShephongParser;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;
import de.luh.psue.compiler.input.InputBuffer;

public class SemanticalTest extends TestCase {
	private static void assertNotSemantical(String source, Reason reason) throws IOException {
		CompilerContext context = new CompilerContext();
		try {
			CompilerLogger logger = CompilerLogger.getInstance(context);
			ShephongParser parser = new ShephongParser(context, new InputBuffer(source));
			ProgramNode pn = parser.parse();
		
			
			// DeSugarStuff - begin
			new DeSugarParam().walk(pn, null);
			pn = (ProgramNode) new RemoveStaticWalker().walk(pn, false);
			// DeSugarStuff - end
			
			// SemanticalStuff - begin
			new InterferTypeWalker(context).walk(pn, null);
			new SemanticalErrorWalker(context).walk(pn, null);
			// SemanticalStuff - end

			if (logger.getErrorCount() != 0) {
				logger.criticalError(Reason.Syntactical, pn, "syntactical errors occured");
			}

		} catch (CompilerException e) {
			if (e.getReason() == reason) {
				return;
			}
		}
		de.luh.psue.compiler.error.Assert.assertError(context, 0, reason);
	}

	@Test
	public void testSECallChar() throws IOException {
		assertNotSemantical("(('o) `)", Reason.Semantical);
	}
	
	@Test
	public void testSECallNumber() throws IOException {
		assertNotSemantical("((#0) `)", Reason.Semantical);
	}
	
	// This test needs more semantical analysis to fail
	//@Test
	//public void testSECallJavaObject() throws IOException {
	//	assertNotSemantical("((\"\" \"java.lang.Integer\" \\) `)", Reason.Semantical);
	//}
	
	@Test
	public void testSYCallAssignment() throws IOException {
		// This is already caught by the parser
		assertNotSemantical("(((a a :)) `)", Reason.Syntactical);
	}

	@Test
	public void testSYCallMagic() throws IOException {
		// This is already caught by the parser
		assertNotSemantical("(((_ `)) `)", Reason.Syntactical);
	}

	@Test
	public void testSYCallModule() throws IOException {
		assertNotSemantical("((;include foo;) `)", Reason.Semantical);
	}

	@Test
	public void testSYOp() throws IOException {
		assertNotSemantical("(#° `)", Reason.Semantical);
	}
}
