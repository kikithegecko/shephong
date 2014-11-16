package de.luh.psue.cklab.shephong.backend.objects.examples;

import junit.framework.TestCase;
import de.luh.psue.cklab.shephong.backend.ShephongStdLib;
import de.luh.psue.cklab.shephong.backend.objects.ShephongCall;
import de.luh.psue.cklab.shephong.backend.objects.ShephongIdent;
import de.luh.psue.cklab.shephong.backend.objects.ShephongList;
import de.luh.psue.cklab.shephong.backend.objects.ShephongNumber;
import de.luh.psue.cklab.shephong.backend.objects.ShephongObject;

/**
 * This class is intended to test a (human) translation of the fak-function written in shephong
 * in combination of ShephongObjects.
 * 
 * Shephong code:
 * 
(
	(
	((($ ((#1 $ #-) fak) ~) #*) #1 ~) {param}
	((#1 $ ~) #=) {op}
	)
fak :)

((#2 fak) `)
 * @author shephongkrewe(imp)
 *
 */
public class FakTest extends TestCase{
	public static void main(String argv[]){
		System.out.println((new ShephongCall(new ShephongIdent("fak", FakTest.class), new ShephongNumber(12))).evaluate(null));
	}
	
	public void testFak(){
		assertEquals("479001600", (new ShephongCall(new ShephongIdent("fak", FakTest.class), new ShephongNumber(12))).evaluate(null).toString());
	}
	
	public static ShephongObject fak(ShephongObject param){
		return new ShephongCall(
				// {op}
				new ShephongCall(new ShephongIdent("std_basic_lt", ShephongStdLib.class), new ShephongList().add(param).add(new ShephongNumber(1))),
				// {param}
				new ShephongList()
							.add(new ShephongNumber(1))
							.add(new ShephongCall(
									new ShephongIdent("std_basic_mul", ShephongStdLib.class),
									new ShephongList()
									.add(new ShephongCall(
											new ShephongIdent("fak", FakTest.class),
											new ShephongCall(
													new ShephongIdent("std_basic_minus", ShephongStdLib.class), 
													new ShephongList()
														.add(param)
														.add(new ShephongNumber(1)))))
									.add(param))));
	}
}
