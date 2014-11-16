package de.luh.psue.cklab.shephong.backend.objects.examples;

import org.junit.Test;

import de.luh.psue.cklab.shephong.backend.ShephongStdLib;
import de.luh.psue.cklab.shephong.backend.objects.ShephongCall;
import de.luh.psue.cklab.shephong.backend.objects.ShephongIdent;
import de.luh.psue.cklab.shephong.backend.objects.ShephongJavaObject;
import de.luh.psue.cklab.shephong.backend.objects.ShephongList;
import de.luh.psue.cklab.shephong.backend.objects.ShephongNumber;

import junit.framework.TestCase;

/**
 * 
 * @author shephongkrewe(imp)
 *
 */
public class ShephongReflectTest extends TestCase{
	@Test
	public void testReflection(){
		// ((#23 "" "java.lang.Integer" #\) "toString" "java.lang.Integer" #\)
		ShephongList innerParamList = new ShephongList();
		
		innerParamList.add(new ShephongList("java.lang.Integer")); // "java.lang.Integer"
		innerParamList.add(new ShephongList()); // ""
		innerParamList.add(new ShephongNumber(23)); // #23
		
		ShephongCall innerCall = new ShephongCall(
				new ShephongIdent("std_basic_reflect", ShephongStdLib.class), 
				innerParamList);
		
		ShephongList outerParamList = new ShephongList();
		
		outerParamList.add(new ShephongList("java.lang.Integer")); // "java.lang.Integer"		
		outerParamList.add(new ShephongList("toString")); // "toString"
		outerParamList.add(innerCall);
		
		ShephongCall outerCall = new ShephongCall(
				new ShephongIdent("std_basic_reflect", ShephongStdLib.class), 
				outerParamList);
		
		assertEquals("23", outerCall.evaluate(null).toString());
		assertEquals(ShephongJavaObject.class, outerCall.evaluate(null).getClass());
		assertEquals(23, new Integer((Integer) ((ShephongJavaObject) innerCall.evaluate(null)).getObject()).intValue());
	}
}
