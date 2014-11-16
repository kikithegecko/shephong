package de.luh.psue.cklab.shephong.backend.destatic;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import de.luh.psue.cklab.shephong.backend.ToStringWalker;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.cklab.shephong.syntactical.ShephongParser;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.InputBuffer;

/**
 * 
 * @author shephongkrewe(imp)
 *
 */
public class RenameWalkerTest extends TestCase{
	@Test
	public void testSimpleRenameKeepStatic() throws CompilerException, IOException{
		CompilerContext c = new CompilerContext();
		ShephongParser parser = new ShephongParser(c, new InputBuffer("[#3 3 :] (3 2 :) (2 1 :) [(1) `] ((1) `)"));
		ProgramNode program = parser.parse();
		
		// now call the CopyRenameWalker:
		RenameWalker rw = new RenameWalker(1);
		ProgramNode renamedProgram = (ProgramNode) rw.walk(program, false);
		
		System.out.println(renamedProgram);
		// use the ToStringWalker to cut of the stdlib:
		ToStringWalker tsw = new ToStringWalker();
		String programAsShephongCode = tsw.walk(renamedProgram, null);
		
		assertEquals("[#3\n" +
				" anon::run1::3 :]\n" +
				"((#0\n" +
				"  anon::run1::3)\n" +
				" anon::run1::2 :)\n" +
				"((#0\n" +
				"  anon::run1::2)\n" +
				" anon::run1::1 :)\n" +
				"[((#0\n" +
				"   anon::run1::1)) `]\n" +
				"(((#0\n" +
				"   anon::run1::1)) `)\n"
				, programAsShephongCode);
	}
	
	@Test
	public void testSimpleRenameRunStripStatic() throws CompilerException, IOException{
		CompilerContext c = new CompilerContext();
		ShephongParser parser = new ShephongParser(c, new InputBuffer("[#3 3 :] (3 2 :) (2 1 :) [(1) `] ((1) `)"));
		ProgramNode program = parser.parse();
		
		// now call the CopyRenameWalker:
		RenameWalker rw = new RenameWalker(1);
		ProgramNode renamedProgram = (ProgramNode) rw.walk(program, true);
		
		System.out.println(renamedProgram);
		// use the ToStringWalker to cut of the stdlib:
		ToStringWalker tsw = new ToStringWalker();
		String programAsShephongCode = tsw.walk(renamedProgram, null);
		
		assertEquals("(#3\n" +
				" anon::run1::3 :)\n" +
				"((#0\n" +
				"  anon::run1::3)\n" +
				" anon::run1::2 :)\n" +
				"((#0\n" +
				"  anon::run1::2)\n" +
				" anon::run1::1 :)\n" +
				"(((#0\n" +
				"   anon::run1::1)) `)\n" +
				"(((#0\n" +
				"   anon::run1::1)) `)\n"
				, programAsShephongCode);
	}
}
