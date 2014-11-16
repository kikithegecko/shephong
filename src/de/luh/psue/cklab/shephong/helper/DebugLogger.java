package de.luh.psue.cklab.shephong.helper;

/**
 * This class is intended to give you more logging abilities then System.out.print{,ln} might give you.
 * Therefore it'll print the source of it's call (full classpath and linenumber).
 * @author shephongkrewe (imp)
 *
 */
public class DebugLogger {
	public static void log(String message, int stackTraceElementNumber){
		// get stack elements
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		
		// get calling Element (head-stackTraceElementNumber)
		StackTraceElement caller = stackTraceElements[stackTraceElementNumber];
		
		// compose output
		String output = caller.getClassName() +  
			"." + caller.getMethodName() + 
			":" + caller.getLineNumber() +
			": " + message;
		
		System.out.println(output);
	}
	
	/**
	 * Simply prints the given message with the prefix (classname and linenumber).
	 * @param message
	 */
	public static void log(String message){
		log(message, 3);
	}
	
	/**
	 * Will print the line only if print is true.
	 * @param message
	 * @param print
	 */
	public static void log(String message, boolean print){
		if(print){
			log(message, 3);
		}
	}
}
