package de.luh.psue.cklab.shephong.helper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.junit.Test;

/**
 *  
 * @author shephongkrewe (imp)
 *
 */
public class DebugLoggerTest extends TestCase {
	@Test
	public void testDebugLogger(){
		/*
		 * using a little bit of magic to get the stdoutput ;).
		 */
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        
        System.setOut(new PrintStream(outContent));
		
        DebugLogger.log("test");
		
        String output = outContent.toString();
        assertEquals("de.luh.psue.cklab.shephong.helper.DebugLoggerTest.testDebugLogger:26: test\n", output);
		
		outContent.reset();
        System.setOut(oldOut);
	}
}
