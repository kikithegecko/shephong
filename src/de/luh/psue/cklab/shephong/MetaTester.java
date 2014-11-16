package de.luh.psue.cklab.shephong;

import java.util.Enumeration;

import org.junit.Test;

import de.luh.psue.cklab.shephong.backend.ShephongStdLibTest;
import de.luh.psue.cklab.shephong.backend.destatic.CopyWalkerTest;
import de.luh.psue.cklab.shephong.backend.destatic.RemoveStaticWalkerTest;
import de.luh.psue.cklab.shephong.backend.destatic.RenameWalkerTest;
import de.luh.psue.cklab.shephong.backend.desugar.DeSugarParamTest;
import de.luh.psue.cklab.shephong.backend.objects.examples.FakTest;
import de.luh.psue.cklab.shephong.backend.objects.examples.ShephongReflectTest;
import de.luh.psue.cklab.shephong.helper.DebugLoggerTest;
import de.luh.psue.cklab.shephong.il.IlNodeTest;
import de.luh.psue.cklab.shephong.lexical.ShephongScannerTest;
import de.luh.psue.cklab.shephong.syntactical.ShephongParserTest;
import de.luh.psue.cklab.shephong.syntactical.ShephongToStringWalkerTest;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Meta testcase for ALL testcases.
 * Allows quick overview if everything is fine or not.
 * @author shephongkrewe (imp)
 *
 */
public class MetaTester extends TestCase {
	@Test
	public void testMeta(){
		Class<?>[] testClasses = {
				// scanner test:
				ShephongScannerTest.class,
				
				// il nodes test:
				IlNodeTest.class,
				
				// parser test:
				ShephongParserTest.class,
				
				// toStringWalker tests:
				ShephongToStringWalkerTest.class,
				
				// helper test:
				DebugLoggerTest.class,

				// desugar tests:
				DeSugarParamTest.class,
				
				// destatic tests: 
				CopyWalkerTest.class,
				RenameWalkerTest.class,
				RemoveStaticWalkerTest.class,
				
				// shephongobject test:
				FakTest.class,
				ShephongReflectTest.class,
				
				// stdlib test:
				ShephongStdLibTest.class,
				
				// compiler test:
				ShephongCompilerTest.class,
			};
		TestSuite ts = new TestSuite(testClasses);
		
		TestResult tr = new TestResult();
		ts.run(tr);
		System.err.println("Ran " + ts.countTestCases() + " tests, " + (ts.countTestCases() - (tr.errorCount() + tr.failureCount())) + " were successfull" );
		if(!tr.wasSuccessful()){
			if(tr.errorCount() > 0){
				System.err.println("Errors: " + tr.errorCount());
				Enumeration<TestFailure> errors = tr.errors();
				while(errors.hasMoreElements()){
					System.err.println("\t" + errors.nextElement().failedTest());
				}
			}
			
			if(tr.failureCount() > 0){
				System.err.println("Failures: " + tr.failureCount());
				Enumeration<TestFailure> failures = tr.failures();
				while(failures.hasMoreElements()){
					System.err.println("\t" + failures.nextElement().failedTest());
				}
			}
			
			fail("" + (tr.errorCount() + tr.failureCount()) + " of " + ts.countTestCases() + " tests failed!");
		}
	}
}
