package de.luh.psue.cklab.shephong.backend;

import junit.framework.TestCase;

import org.junit.Test;

import de.luh.psue.cklab.shephong.backend.objects.ShephongCall;
import de.luh.psue.cklab.shephong.backend.objects.ShephongIdent;
import de.luh.psue.cklab.shephong.backend.objects.ShephongJavaObject;
import de.luh.psue.cklab.shephong.backend.objects.ShephongList;
import de.luh.psue.cklab.shephong.backend.objects.ShephongNumber;
import de.luh.psue.cklab.shephong.backend.objects.ShephongObject;

/**
 * Various tests for the shephong standard library.
 * 
 * @author shephongkrewe(imp, kiki)
 *
 */
public class ShephongStdLibTest extends TestCase{
	
	/** 
	 * Test case for numbers, lists, calls and idents inside one list 
	 * which is parameter to std_basic_plus.
	 * 
	 * ((get_number_10 (5 4 #+) 8 (7 6 5 ~)4 3 2 1 ~) #+)
	 */
	@Test
	public void testStd_basic_plus() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(1));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(3));
		list.add(new ShephongNumber(4));
		
		ShephongList innerList = new ShephongList();
		
		innerList.add(new ShephongNumber(5));
		innerList.add(new ShephongNumber(6));
		innerList.add(new ShephongNumber(7));
		
		list.add(innerList);
		
		list.add(new ShephongNumber(8));
		
		ShephongList paramList = new ShephongList();
		paramList.add(new ShephongNumber(4));
		paramList.add(new ShephongNumber(5));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_plus", ShephongStdLib.class), paramList);
		list.add(call);
		
		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
		
		ShephongCall finalCall = new ShephongCall(new ShephongIdent("std_basic_plus", ShephongStdLib.class), list);
		
		assertEquals(55, ((ShephongNumber) finalCall.evaluate(null)).getValue());
	}
	
	@Test
	public void testStd_basic_minusBasic() {
		
		ShephongList simpleList = new ShephongList();
		simpleList.add(new ShephongNumber(5));
		simpleList.add(new ShephongNumber(2));
		
		ShephongCall simpleCall = new ShephongCall(new ShephongIdent("std_basic_minus", ShephongStdLib.class), simpleList);
		assertEquals(3, ((ShephongNumber) simpleCall.evaluate(null)).getValue());
	}
	
	@Test
	public void testStd_basic_minusSimple() {
		
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(20));
		list.add(new ShephongNumber(1));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(3));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_minus", ShephongStdLib.class), list);
		
		assertEquals(14, ((ShephongNumber) call.evaluate(null)).getValue());
	}
	
	/**
	 * Test case for numbers, lists, calls and idents inside one list 
	 * which is parameter to std_basic_plus.
	 * 
	 * ((get_number_10 (4 5 #-) 8 (7 6 5 ~)4 3 2 100 ~) #-)
	 */
	@Test
	public void testStd_basic_minusComplex() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(100));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(3));
		list.add(new ShephongNumber(4));
		
		ShephongList innerList = new ShephongList();
		
		innerList.add(new ShephongNumber(5));
		innerList.add(new ShephongNumber(6));
		innerList.add(new ShephongNumber(7));
		
		list.add(innerList);
		
		list.add(new ShephongNumber(8));
		
		ShephongList paramList = new ShephongList();
		paramList.add(new ShephongNumber(5));
		paramList.add(new ShephongNumber(4));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_minus", ShephongStdLib.class), paramList);
		list.add(call);
		
		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
		
		ShephongCall finalCall = new ShephongCall(new ShephongIdent("std_basic_minus", ShephongStdLib.class), list);
		
		assertEquals(54, ((ShephongNumber) finalCall.evaluate(null)).getValue());
	}
	
	/**
	 * Testcase for list minus list, like:
	 * (((1 2 3 4 ~) (5 6 7 8 ~) #-)
	 */
	@Test
	public void testListMinusListStd_basic_minusComplex() {
		ShephongList first = new ShephongList();
		first.add(new ShephongNumber(8));
		first.add(new ShephongNumber(7));
		first.add(new ShephongNumber(6));
		first.add(new ShephongNumber(5));
		
		ShephongList second = new ShephongList();
		second.add(new ShephongNumber(4));
		second.add(new ShephongNumber(3));
		second.add(new ShephongNumber(2));
		second.add(new ShephongNumber(1));
		
		ShephongList list = new ShephongList();
//		list.add(new ShephongNumber(0));
		list.add(first);
		list.add(second);
		
		ShephongCall finalCall = new ShephongCall(new ShephongIdent("std_basic_minus", ShephongStdLib.class), list);

		
		assertEquals(16, ((ShephongNumber) finalCall.evaluate(null)).getValue());
	}
	/**
	 * Test case for numbers, lists, calls and idents inside one list 
	 * which is parameter to std_basic_plus.
	 * 
	 * ((get_number_10 (5 4 #*) 8 (7 6 5 ~)4 3 2 1 ~) #*)
	 */
	@Test
	public void testStd_basic_mul() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(1));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(3));
		list.add(new ShephongNumber(4));
		
		ShephongList innerList = new ShephongList();
		
		innerList.add(new ShephongNumber(5));
		innerList.add(new ShephongNumber(6));
		innerList.add(new ShephongNumber(7));
		
		list.add(innerList);
		
		list.add(new ShephongNumber(8));
		
		ShephongList paramList = new ShephongList();
		paramList.add(new ShephongNumber(4));
		paramList.add(new ShephongNumber(5));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_mul", ShephongStdLib.class), paramList);
		list.add(call);
		
		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
		
		ShephongCall finalCall = new ShephongCall(new ShephongIdent("std_basic_mul", ShephongStdLib.class), list);
		
		assertEquals(8064000, ((ShephongNumber) finalCall.evaluate(null)).getValue());
	}
	
	/**
	 * Tests if our division is a really a whole-number division.
	 */
	@Test
	public void testStd_basic_quotSimple() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(6));
		list.add(new ShephongNumber(4));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_quot", ShephongStdLib.class), list);
		assertEquals(1, ((ShephongNumber) call.evaluate(null)).getValue());
	}
	
	/**
	 * Tests if chaining divisions work.
	 */
	@Test
	public void testStd_basic_quotComplex() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(60));
		list.add(new ShephongNumber(3));
		list.add(new ShephongNumber(5));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_quot", ShephongStdLib.class), list);
		assertEquals(4, ((ShephongNumber) call.evaluate(null)).getValue());
	}
	
	/**
	 * Very basic test of the "less than" operation.
	 */
	@Test
	public void testStd_basic_ltBasic() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(5));
		list.add(new ShephongNumber(4));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_lt", ShephongStdLib.class), list);

		assertTrue(0 == new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class).compareTo(call.evaluate(null)));
	}
	
	/**
	 * Tests if the chaining of "less than" works correctly. 
	 */
	@Test
	public void testStd_basic_ltSimple() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(1));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(5));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_lt", ShephongStdLib.class), list);
		
		assertTrue(0 == new ShephongIdent("std_basic_head", ShephongStdLib.class).compareTo(call.evaluate(null)));
	}
	/**
	 * Tests if the chaining of "less than" works correctly and evaluation of idents.
	 */
	@Test
	public void testStd_basic_ltALittleBitMoreComplex() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(1));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(5));
		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_lt", ShephongStdLib.class), list);
		
		assertTrue(0 == new ShephongIdent("std_basic_head", ShephongStdLib.class).compareTo(call.evaluate(null)));
	}
	/**
	 * Tests if the chaining of "less than" works correctly and evaluation of idents.
	 */
	@Test
	public void testStd_basic_ltAnotherLittleBitMoreComplex() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
		list.add(new ShephongNumber(5));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(1));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_lt", ShephongStdLib.class), list);
		
		assertTrue(0 == new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class).compareTo(call.evaluate(null)));
	}
	
	/**
	 * Very basic test of the "greater than" operation.
	 */
	@Test
	public void testStd_basic_gtBasic() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(5));
		list.add(new ShephongNumber(4));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_gt", ShephongStdLib.class), list);
		
		assertTrue(0 == new ShephongIdent("std_basic_head", ShephongStdLib.class).compareTo(call.evaluate(null)));
	}
	
	/**
	 * Tests if the chaining of "greater than" works correctly and evaluation of idents.
	 */
	@Test
	public void testStd_basic_gtAnotherLittleBitMoreComplex() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
		list.add(new ShephongNumber(5));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(1));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_gt", ShephongStdLib.class), list);
		
		assertTrue(0 == new ShephongIdent("std_basic_head", ShephongStdLib.class).compareTo(call.evaluate(null)));
	}
	
	/**
	 * Tests if the chaining of "equals" works correctly and evaluation of idents.
	 */
	@Test
	public void testStd_basic_eqAnotherLittleBitMoreComplex() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
		list.add(new ShephongNumber(10));
		list.add(new ShephongNumber(10));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_eq", ShephongStdLib.class), list);
		
		assertTrue(0 == new ShephongIdent("std_basic_head", ShephongStdLib.class).compareTo(call.evaluate(null)));
	}
	
	/**
	 * Tests if the chaining of "equals" works correctly and evaluation of idents.
	 */
	@Test
	public void testStd_basic_eqFalseAnotherLittleBitMoreComplex() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
		list.add(new ShephongNumber(5));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(1));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_eq", ShephongStdLib.class), list);
		
		assertTrue(0 == new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class).compareTo(call.evaluate(null)));
	}
	
	/**
	 * Tests the correct implementation of "append"
	 * 
	 * ((1 2 3 ~) (4 ~) #@) -> (1 2 3 4 ~)
	 */
	@Test
	public void testFirstAppend() {
		ShephongList list = new ShephongList();
		
		list.add(new ShephongNumber(3));
		list.add(new ShephongNumber(2));
		list.add(new ShephongNumber(1));
		
		ShephongList elem = new ShephongList().add(new ShephongNumber(4));
		
		ShephongList paramlist = new ShephongList();
		paramlist.add(elem);
		paramlist.add(list);
		
		ShephongList assertList = new ShephongList();
		assertList.add(new ShephongNumber(4));
		assertList.add(new ShephongNumber(3));
		assertList.add(new ShephongNumber(2));
		assertList.add(new ShephongNumber(1));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_append", ShephongStdLib.class), paramlist);
		assertEquals(assertList.toString(), call.evaluate(null).toString());
	}
	
	/**
	 * Tests the correct implementation of "append"
	 * 
	 * ((4 7 ~) (3 5 ~) #@)      { -> (4 7 3 5 ~) }
	 */
	@Test
	public void testSecondAppend() {
		
		ShephongList list1 = new ShephongList();
		list1.add(new ShephongNumber(5));
		list1.add(new ShephongNumber(3));
		
		ShephongList list2 = new ShephongList();
		list2.add(new ShephongNumber(7));
		list2.add(new ShephongNumber(4));
		
		ShephongList paramlist = new ShephongList().add(list1).add(list2);
		
		ShephongList assertList = new ShephongList();
		assertList.add(new ShephongNumber(5));
		assertList.add(new ShephongNumber(3));
		assertList.add(new ShephongNumber(7));
		assertList.add(new ShephongNumber(4));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_append", ShephongStdLib.class), paramlist);
		assertEquals(assertList.toString(), call.evaluate(null).toString());
	}
	
	/**
	 * Tests the correct implementation of "append"
	 * 
	 * ((6 5 4 ~) ((3 2 1 ~) ~) #@) -> (6 5 4 (3 2 1~) ~)
	 */
	@Test
	public void testThirdAppend() {
		
		ShephongList list = new ShephongList();
		list.add(new ShephongNumber(4));
		list.add(new ShephongNumber(5));
		list.add(new ShephongNumber(6));
		
		ShephongList outer = new ShephongList();
		ShephongList inner = new ShephongList();
		inner.add(new ShephongNumber(1));
		inner.add(new ShephongNumber(2));
		inner.add(new ShephongNumber(3));
		outer.add(inner);
		
		ShephongList paramlist = new ShephongList().add(outer).add(list);
		
		ShephongList assertList = new ShephongList();
		assertList.add(new ShephongList().add(new ShephongNumber(1)).add(new ShephongNumber(2)).add(new ShephongNumber(3)));
		assertList.add(new ShephongNumber(4));
		assertList.add(new ShephongNumber(5));
		assertList.add(new ShephongNumber(6));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_append", ShephongStdLib.class), paramlist);
		assertEquals(assertList.toString(), call.evaluate(null).toString());
		
	}
	
	/**
	 * Tests the correct handling of nestend calls in the append method.
	 * 
	 * ((((1 3 ~) #-) ~)((4 3 ~) #+) ~) #@) -> (2 7 ~)
	 */
	@Test
	public void testAppendWithCalls() {
		ShephongList list1 = new ShephongList();
		list1.add(new ShephongCall(new ShephongIdent("std_basic_plus", ShephongStdLib.class), 
				new ShephongList().add(new ShephongNumber(3)).add(new ShephongNumber(4))));
		
		ShephongList list2 = new ShephongList();
		list2.add(new ShephongCall(new ShephongIdent("std_basic_minus", ShephongStdLib.class),
				new ShephongList().add(new ShephongNumber(3)).add(new ShephongNumber(1))));
		
		ShephongList param = new ShephongList().add(list1).add(list2);
		
		ShephongList assertList = new ShephongList();
		assertList.add(new ShephongNumber(7));
		assertList.add(new ShephongNumber(2));
		
		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_append", ShephongStdLib.class), param);
		assertEquals(assertList.toString(), call.evaluate(null).toString());
	}
	
	public void testBasicReflectUse(){
		/*
		 * we want to create a new String object by using std_basic_reflect.
		 * aim: do the same like:  */
		String expected = new String("I am a string.");
		
		
		ShephongIdent op = new ShephongIdent("std_basic_reflect", ShephongStdLib.class);
		ShephongList param = new ShephongList();
		
		// classname
		param.add(new ShephongList("java.lang.String"));
		// methodname, in this case empty because we want to call the constructor
		param.add(new ShephongList());
		// parameter for constructor:
		param.add(new ShephongList("I am a string."));
		
		ShephongCall call = new ShephongCall(op, param);		
		
		ShephongJavaObject sjo = (ShephongJavaObject) call.evaluate(null);
		
		assertEquals(expected, sjo.getObject());
		
		/* 
		 * since we've got an string object now, let's try to evaluate it an call a method from the object.
		 * let us try to do something like:   */
		char[] expectedAsCharArray = expected.toCharArray();
		
		// create a new parameter list:
		ShephongList paramsForSecondCall = new ShephongList();
		paramsForSecondCall.add(new ShephongList("toCharArray"));
		
		ShephongCall secondCall = new ShephongCall(sjo, paramsForSecondCall);
		ShephongJavaObject retSecondCall = (ShephongJavaObject) secondCall.evaluate(null);
		
		char[] retSecondCallPrimitiv = (char[]) retSecondCall.getObject();
		
		for(int i = 0; i < expectedAsCharArray.length; i++){
			assertEquals(expectedAsCharArray[i], retSecondCallPrimitiv[i]);
		}
		
	}
	
	
//	NOTE: The following test can only fail, we won't support lists in lists.
//	/**
//	 * More complex test for the "less than" method.
//	 * 
//	 * ((get_number_10 (7 6 ~) 4 ~) #<)
//	 */
//	@Test
//	public void complexTestStd_basic_lt() {
//		ShephongList list = new ShephongList();
//		
//		list.add(new ShephongNumber(4));
//		
//		ShephongList innerList = new ShephongList();
//		
//		innerList.add(new ShephongNumber(6));
//		innerList.add(new ShephongNumber(7));
//		
//		list.add(innerList);
//		list.add(new ShephongIdent("get_number_10", ShephongStdLibTest.class));
//		
//		ShephongCall call = new ShephongCall(new ShephongIdent("std_basic_lt", ShephongStdLib.class), list);
//		assertEquals(new ShephongIdent("std_basic_head", ShephongStdLib.class), call.evaluate(null));
//	}
	
	public static ShephongObject get_number_10(ShephongObject paramlist){
		return new ShephongNumber(10);
	}
}
