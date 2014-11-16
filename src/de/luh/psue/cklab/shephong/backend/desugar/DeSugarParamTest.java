package de.luh.psue.cklab.shephong.backend.desugar;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import de.luh.psue.cklab.shephong.backend.GenerateStdLib;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.cklab.shephong.syntactical.ShephongParser;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.InputBuffer;


public class DeSugarParamTest extends TestCase{
	private static String stdLib = GenerateStdLib.getStdLibString();
	
	@Test
	public void testGreaterEquals3() throws IOException, CompilerException{
		CompilerContext c = new CompilerContext();
		ShephongParser parser = new ShephongParser(c, new InputBuffer("(($3 $)`)"));
		ProgramNode program = parser.parse();
		program.unpackMagicNodes();
		
		DeSugarParam dsp = new DeSugarParam();
		dsp.walk(program, null);

		assertEquals(stdLib + "$(#^(#_(#_($))))\n", program.toString());
	}
	
	@Test
	public void testEquals2() throws IOException, CompilerException{
		CompilerContext c = new CompilerContext();
		ShephongParser parser = new ShephongParser(c, new InputBuffer("(($2 $)`)"));
		ProgramNode program = parser.parse();
		program.unpackMagicNodes();
		
		DeSugarParam dsp = new DeSugarParam();
		dsp.walk(program, null);

		assertEquals(stdLib + "$(#^(#_($)))\n", program.toString());
	}
	
	@Test
	public void testEquals1() throws IOException, CompilerException{
		CompilerContext c = new CompilerContext();
		ShephongParser parser = new ShephongParser(c, new InputBuffer("(($1 $)`)"));
		ProgramNode program = parser.parse();
		program.unpackMagicNodes();
		DeSugarParam dsp = new DeSugarParam();
		dsp.walk(program, null);

		assertEquals(stdLib + "$(#^($))\n", program.toString());
	}
	
	@Test
	public void testZero() throws IOException, CompilerException{
		CompilerContext c = new CompilerContext();
		ShephongParser parser = new ShephongParser(c, new InputBuffer("(($ $)`)"));
		ProgramNode program = parser.parse();
		program.unpackMagicNodes();
		
		DeSugarParam dsp = new DeSugarParam();
		dsp.walk(program, null);

		assertEquals(stdLib + "$($)\n", program.toString());
	}
}
