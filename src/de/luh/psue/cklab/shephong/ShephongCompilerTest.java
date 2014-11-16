package de.luh.psue.cklab.shephong;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.junit.Test;

import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.meta.results.PathLocation;

public class ShephongCompilerTest extends TestCase{

	@Test
	public void test1() throws CompilerException, InstantiationException, IllegalAccessException {
		assertByteCode("examples/compilertest/test1.shephong", "42\n");
	}

	public static void assertByteCode (String fileName, String expected) throws CompilerException, InstantiationException, IllegalAccessException {
		CompilerContext context = new CompilerContext();
		ShephongCompiler compiler = new ShephongCompiler(context);

		/*
		 * This is the location for the compiled file. 
		 */
		File resultFolder = new File("result");
		if (!resultFolder.exists()) resultFolder.mkdir();
		compiler.addSource(new File(fileName));
		compiler.setOutputFolder(new PathLocation("result"));

		/*
		 * Compile the file
		 */
		compiler.check();
		compiler.compile();

		/*
		 * Catch stdout in output
		 */
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream stdoutSave = System.out;
		System.setOut(new PrintStream(output));

		// see DebugLoggerTest
		Class<? extends Runnable> metaClass = compiler.getProgramMetaClass();
		Runnable program = metaClass.newInstance();
		program.run();

		/*
		 * Compare the output, if it is the expected result
		 */
		assertEquals(expected, output.toString());

		/*
		 * restore stdout
		 */
		output.reset();
		System.setOut(stdoutSave);
	}

}
