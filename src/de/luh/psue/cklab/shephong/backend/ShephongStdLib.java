package de.luh.psue.cklab.shephong.backend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.luh.psue.cklab.shephong.backend.objects.ShephongCall;
import de.luh.psue.cklab.shephong.backend.objects.ShephongChar;
import de.luh.psue.cklab.shephong.backend.objects.ShephongIdent;
import de.luh.psue.cklab.shephong.backend.objects.ShephongJavaObject;
import de.luh.psue.cklab.shephong.backend.objects.ShephongList;
import de.luh.psue.cklab.shephong.backend.objects.ShephongNumber;
import de.luh.psue.cklab.shephong.backend.objects.ShephongObject;

/**
 * This is the shephong standard library. It contains essential #-Methods
 * written in Java.
 * 
 * @author shephongkrewe (imp, kiki)
 *
 */
public class ShephongStdLib {
	
	/**
	 * Sums up all numbers that encounter in the parameter.
	 * Works with numbers, lists an even function calls
	 * (that return numbers)
	 * 
	 * @param param any applicable ShephongObject (number, ident, call, list)
	 * @return the result, encapsulated in a ShephongNumber object.
	 */
	public static ShephongNumber std_basic_plus(ShephongObject param) {
		if(param instanceof ShephongIdent
				|| param instanceof ShephongCall){
			/* Do an recursive call of std_basic_plus with the evaluated param.
			 * Example a:
			 * 		(#1 a :)
			 * 		(a #+)
			 * Example b:
			 * 		(((#1 #1 ~) #+)
			 * In both cases, we need to evaluate the Ident / Call.
			 */ 
			return ShephongStdLib.std_basic_plus(param.evaluate(null));
		}
		else if(param instanceof ShephongNumber) {
			// just return the given number itself.
			return (ShephongNumber) param;
			
		}
		else if (param instanceof ShephongList) {
			if(((ShephongList) param).size() == 0){
				return new ShephongNumber(0);
			}
			
			/* Get the first element, since the std_basic_plus always returns a ShephongNumber,
			 * call it do deal with any kind of Objects we can encounter inside a list.
			 */
			if(((ShephongList) param).size() == 1){
				return new ShephongNumber(ShephongStdLib.std_basic_plus(((ShephongList)param).getHead()).getValue());
			}
			ShephongNumber number = ShephongStdLib.std_basic_plus(((ShephongList) param).getHead());
			return new ShephongNumber(number.getValue() + ShephongStdLib.std_basic_plus(((ShephongList)param).getTail()).getValue());
		}
		else{
			System.err.println("Can't use #+ on: " + param.getClass().getSimpleName());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Does a subtraction with all numbers in the parameter. The very first
	 * value is the minuend, all following values are treated as subtrahends.
	 * 
	 * Example a: ((1 2 3 20 ~) #-) -> 20-3-2-1 -> 14
	 * Example b: (((3 2 1 ~) (5 5 ~)) = 10 - 6
	 * 
	 * 
	 * @param param any applicable ShephongObject (number, ident, call, list)
	 * @return the result, encapsulated in a ShephongNumber object.
	 */
	public static ShephongNumber std_basic_minus(ShephongObject param) {
		if(param instanceof ShephongIdent
				|| param instanceof ShephongCall){
			// Do an recursive call of std_basic_minus with the evaluated param. 
			return ShephongStdLib.std_basic_minus(param.evaluate(null));
		}
		else if(param instanceof ShephongNumber) {
			// just return the given number itself.
			return new ShephongNumber(((ShephongNumber) param).getValue()*(-1));
			
		}
		else if (param instanceof ShephongList) {
			if(((ShephongList) param).size() == 0){
				return new ShephongNumber(0);
			}
			/* Get the first element, since the std_basic_minus always returns a ShephongNumber,
			 * call it do deal with any kind of Objects we can encounter inside a list.
			 */
			if(((ShephongList) param).size() == 1){
				return new ShephongNumber(ShephongStdLib.std_basic_minus(((ShephongList)param).getHead()).getValue());
			}
			ShephongNumber number = ShephongStdLib.std_basic_minus(ShephongStdLib.std_basic_plus(((ShephongList) param).getHead()));
			return new ShephongNumber(number.getValue()*(-1) - ShephongStdLib.std_basic_plus(((ShephongList)param).getTail()).getValue());
		}
		else{
			System.err.println("Can't use #- on: " + param.getClass().getSimpleName());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Multiplies all numbers that encounter in the parameter.
	 * Works with numbers, lists an even function calls
	 * (that return numbers)
	 * 
	 * @param param any applicable ShephongObject (number, ident, call, list)
	 * @return the result, encapsulated in a ShephongNumber object.
	 */
	public static ShephongNumber std_basic_mul(ShephongObject param) {
		if(param instanceof ShephongIdent
				|| param instanceof ShephongCall){
			// Do an recursive call of std_basic_mul with the evaluated param.
			return ShephongStdLib.std_basic_mul(param.evaluate(null));
		}
		else if(param instanceof ShephongNumber) {
			// just return the given number itself.
			return (ShephongNumber) param;
			
		}
		else if (param instanceof ShephongList) {
			if(((ShephongList) param).size() == 0){
				return new ShephongNumber(1);
			}
			/* Get the first element, since the std_basic_mul always returns a ShephongNumber,
			 * call it do deal with any kind of Objects we can encounter inside a list.
			 */
			if(((ShephongList) param).size() == 1){
				return new ShephongNumber(ShephongStdLib.std_basic_mul(((ShephongList)param).getHead()).getValue());
			}
			ShephongNumber number = ShephongStdLib.std_basic_mul(((ShephongList) param).getHead());
			return new ShephongNumber(number.getValue() * ShephongStdLib.std_basic_mul(((ShephongList)param).getTail()).getValue());
		}
		else{
			System.err.println("Can't use #* on: " + param.getClass().getSimpleName());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Calculates a whole-number division with all given values. The very first value is 
	 * treated as the dividend, all following values are divisors.
	 * 
	 * Examples: 
	 * ((4 6 ~) #/) -> 6 div 4 -> 1
	 * ((10 20 30 ~) #/) -> ((30 div 20) div 10) -> 1 div 10 -> 0
	 * 
	 * @param param any applicable ShephongObject (number, ident, call, list)
	 * @return the result, encapsulated in a ShephongNumber object.
	 */
	public static ShephongNumber std_basic_quot(ShephongObject param) {
		if(param instanceof ShephongIdent
				|| param instanceof ShephongCall){
			// Do an recursive call of std_basic_quot with the evaluated param.
			return ShephongStdLib.std_basic_quot(param.evaluate(null));
		}
		else if(param instanceof ShephongNumber) {
			// just return the given number itself.
			return (ShephongNumber) param;
			
		}
		else if (param instanceof ShephongList) {
			if(((ShephongList) param).size() == 0){
				return new ShephongNumber(1);
			}
			/* Get the first element, since the std_basic_quot always returns a ShephongNumber,
			 * call it do deal with any kind of Objects we can encounter inside a list.
			 */
			if(((ShephongList) param).size() == 1){
				return new ShephongNumber(ShephongStdLib.std_basic_quot(((ShephongList)param).getHead()).getValue());
			}
			ShephongNumber number = ShephongStdLib.std_basic_quot(((ShephongList) param).getHead());
			return new ShephongNumber(number.getValue() / ShephongStdLib.std_basic_mul(((ShephongList)param).getTail()).getValue());
		}
		else{
			System.err.println("Can't use #/ on: " + param.getClass().getSimpleName());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Calculates the remainder of the two given values.
	 * 
	 * Example: 
	 * ((4 6 ~) #%) -> 6 remainder 4 -> 2
	 * 
	 * @param param any applicable ShephongObject (number, ident, call, list)
	 *   must eval to a list with 2 params or to one number
	 * @return the result, encapsulated in a ShephongNumber object.
	 */
	public static ShephongNumber std_basic_rem(ShephongObject param) {
		if(param instanceof ShephongIdent
				|| param instanceof ShephongCall){
			// Do an recursive call of std_basic_quot with the evaluated param.
			return ShephongStdLib.std_basic_quot(param.evaluate(null));
		}
		else if(param instanceof ShephongNumber
				|| ! (param instanceof ShephongList)
				|| ((ShephongList) param).size() != 2) {
			System.err.println("Need a list with exactly 2 entries as parameters for remainder");
			System.exit(-1);
			return null;
			
		}
		/*
		 * call std_basic_mul to evaluate the parameters. It does this if it is called with only one parameter.
		 */
		ShephongNumber divisor = ShephongStdLib.std_basic_mul(((ShephongList) param).getHead());
		ShephongNumber divident = ShephongStdLib.std_basic_mul(((ShephongList) param).getTail().getHead());
		return new ShephongNumber(divisor.getValue() % divident.getValue());
	}

	/*
	 * List ops:
	 */
	public static ShephongObject std_basic_head(ShephongObject param){
		if (param == null) {
			System.err.println("#^ needs an argument (a list)");
			System.exit(-1);
		}
		if(param instanceof ShephongIdent || param instanceof ShephongCall){
			return std_basic_head(param.evaluate(null));
		}
		if(param instanceof ShephongList){
			return ((ShephongList) param).getHead();
		}
		System.err.println("can't get head from an: " + param.getClass().getSimpleName());
		System.exit(-1);
		return null;
	}
	
	public static ShephongList std_basic_tail(ShephongObject param){
		if(param instanceof ShephongIdent || param instanceof ShephongCall){
			ShephongObject temp = std_basic_tail(param.evaluate(null));
			if(temp instanceof ShephongList){
				return (ShephongList) temp;
			}
			// create a new list:
			ShephongList tempList = new ShephongList();
			return tempList.add(temp);
		}
		if(param instanceof ShephongList){
			return ((ShephongList) param).getTail();
		}
		System.err.println("can't get tail from an: " + param.getClass().getSimpleName());
		System.exit(-1);
		return null;
	}
	
	public static ShephongObject std_basic_headOfTail(ShephongObject param){
		return ShephongStdLib.std_basic_head(ShephongStdLib.std_basic_tail(param));
	}
	
	/**
	 * appends a list to a list. Some examples:
	 * 
	 * ((3 2 ~) (1 ~) #@)             { -> (3 2 1 ~) }
	 * ((4 7 ~) (3 5 ~) #@      	  { -> (4 7 5 3 ~) }
	 * ((6 5 4 ~) ((3 2 1 ~) ~) #@)   { -> ((6 5 4 ~) (3 2 1~) ~) }
	 * 
	 * @param param a list, consisting of two lists e.g. ((3 2 ~) (1 ~) ~).
	 * @return a ShephongList where the first param is appended to the second param.
	 */
	public static ShephongObject std_basic_append(ShephongObject param) {
		
		while (param instanceof ShephongCall || param instanceof ShephongIdent)
			param = param.evaluate(null);
		
		if(!(param instanceof ShephongList)){
			System.err.println("std_basic_append: two parameters required!");
			System.exit(-1);
			return null;
		}
		
		if(((ShephongList)param).size() != 2) {
			System.err.println("std_basic_append: two parameters required!");
			System.exit(-1);
			return null;
		}
		
		ShephongObject head = ((ShephongList) param).getHead();
		
		while((head instanceof ShephongCall) || (head instanceof ShephongIdent)){
			head = head.evaluate(null);
		}
		
		if(!(head instanceof ShephongList)){
			System.err.println("std_basic_append: first parameter MUST BE a list object!");
			System.exit(-1);
			return null;
		}
		
		ShephongObject headOfTail = (((ShephongList) param).getTail()).getHead();
		
		while((headOfTail instanceof ShephongCall) || (headOfTail instanceof ShephongIdent)){
			headOfTail = headOfTail.evaluate(null);
		}
		
		if(!(headOfTail instanceof ShephongList)) {
			System.err.println("std_basic_append: second parameter MUST BE a list object!");
			System.exit(-1);
			return null;
		}
		
		ShephongList tmp = new ShephongList(((ShephongList)head).size() +
				((ShephongList)headOfTail).size());
		for (ShephongObject so : (ShephongList)head) {
			tmp.add(so);
		}
		for (ShephongObject so : (ShephongList)headOfTail) {
			tmp.add(so);
		}
		
		return tmp;
	}
	
	/*
	 * comparison ops:
	 */
	
	public static ShephongIdent std_basic_lt(ShephongObject param){
		if(param instanceof ShephongNumber
				|| param instanceof ShephongChar){
			System.err.println("#< needs an list as argument an not: " + param.getClass().getSimpleName());
			System.exit(-1);
			return null;
		}
		else if(param instanceof ShephongList && ((ShephongList) param).size() < 2){
			System.err.println("#< needs at least two arguments inside it's parameterlist.");
			System.exit(-1);
			return null;
		}
		ShephongObject before = ((ShephongList) param).getHead();
		for(ShephongObject so : ((ShephongList) param).getTail()){
			ShephongObject actual = so;
			// handle lists in lists -> throw error.
			if(before instanceof ShephongList || actual instanceof ShephongList){
				System.err.println("Can't compare a list with Shephong Objects like Number, Char, Ident, Call (in short: you can't have list in paramlists for comparsion operations)");
				System.exit(-1);
				return null;
			}
			if(!(actual instanceof ShephongChar
					|| actual instanceof ShephongNumber)){
				actual = actual.evaluate(null);
			}
			if(!(before instanceof ShephongChar
					|| before instanceof ShephongNumber)){
				before = before.evaluate(null);
			}
			
			if(before.compareTo(actual) != 1){
				return new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class);
			}
			before = actual;
		}

		return new ShephongIdent("std_basic_head", ShephongStdLib.class);
	}
	
	// FIXME eq and gt are both copies of lt, only the error output and the value for the comparsion with the result are different.
	public static ShephongIdent std_basic_eq(ShephongObject param){
		param = std_internal_evalParam(param);
		if(! (param instanceof ShephongList)){
			System.err.println("#= needs an list as argument an not: " + param.getClass().getSimpleName());
			System.exit(-1);
			return null;
		}
		else if(param instanceof ShephongList && ((ShephongList) param).size() < 2){
			System.err.println("#= needs at least two arguments inside it's parameterlist.");
			System.exit(-1);
			return null;
		}
		ShephongObject before = ((ShephongList) param).getHead();
		before = std_internal_evalParam(before);
		for(ShephongObject so : ((ShephongList) param).getTail()){
			ShephongObject actual = so;
			// lists are handled by .compareTo() of ShephongList, and are not eval()ed
			actual = std_internal_evalParam(actual);

			if(before.compareTo(actual) != 0){
				return new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class);
			}
			before = actual;
		}

		return new ShephongIdent("std_basic_head", ShephongStdLib.class);
	}
	
	public static ShephongIdent std_basic_gt(ShephongObject param){
		if(param instanceof ShephongNumber
				|| param instanceof ShephongChar){
			System.err.println("#> needs an list as argument an not: " + param.getClass().getSimpleName());
			System.exit(-1);
			return null;
		}
		else if(param instanceof ShephongList && ((ShephongList) param).size() < 2){
			System.err.println("#> needs at least two arguments inside it's parameterlist.");
			System.exit(-1);
			return null;
		}
		ShephongObject before = ((ShephongList) param).getHead();
		for(ShephongObject so : ((ShephongList) param).getTail()){
			ShephongObject actual = so;
			// handle lists in lists -> throw error.
			if(before instanceof ShephongList || actual instanceof ShephongList){
				System.err.println("Can't compare a list with Shephong Objects like Number, Char, Ident, Call (in short: you can't have list in paramlists for comparsion operations)");
				System.exit(-1);
				return null;
			}
			if(!(actual instanceof ShephongChar
					|| actual instanceof ShephongNumber)){
				actual = actual.evaluate(null);
			}
			if(!(before instanceof ShephongChar
					|| before instanceof ShephongNumber)){
				before = before.evaluate(null);
			}
			
			if(before.compareTo(actual) != -1){
				return new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class);
			}
			before = actual;
		}

		return new ShephongIdent("std_basic_head", ShephongStdLib.class);
	}

	/*
	 * Logic ops AND, OR, NOT
	 * 
	 * These look for #^ or #" in the parameter list and treat them as boolean values.
	 */
	public static ShephongIdent std_basic_and (ShephongObject param) {
		if (!(param instanceof ShephongList)) {
			System.err.println ("Need at least two params to compare");
			System.exit(-1);
			return null;
		}
		for (ShephongObject bool : ((ShephongList) param)) {
			bool = bool.evaluate(null);
			if (!(bool instanceof ShephongIdent)) {
				System.err.println ("Can only AND comparisions");
				System.exit(-1);
				return null;
			} else if (((ShephongIdent) bool).getCompleteIdent().equals("class de.luh.psue.cklab.shephong.backend.ShephongStdLib.std_basic_headOfTail")) {
				return (ShephongIdent) bool;
			} else if (!((ShephongIdent) bool).getCompleteIdent().equals("class de.luh.psue.cklab.shephong.backend.ShephongStdLib.std_basic_head")) {
				System.err.println ("Can only AND comparision idents");
				System.exit(-1);
				return null;
			}
		}
		return new ShephongIdent("std_basic_head", ShephongStdLib.class);
	}

	public static ShephongIdent std_basic_or (ShephongObject param) {
		if (!(param instanceof ShephongList)) {
			System.err.println ("Need at least two params to compare");
			System.exit(-1);
			return null;
		}
		for (ShephongObject bool : ((ShephongList) param)) {
			bool = bool.evaluate(null);
			if (!(bool instanceof ShephongIdent)) {
				System.err.println ("Can only OR comparisions");
				System.exit(-1);
				return null;
			} else if (((ShephongIdent) bool).getCompleteIdent().equals("class de.luh.psue.cklab.shephong.backend.ShephongStdLib.std_basic_head")) {
				return (ShephongIdent) bool;
			} else if (!((ShephongIdent) bool).getCompleteIdent().equals("class de.luh.psue.cklab.shephong.backend.ShephongStdLib.std_basic_headOfTail")) {
				System.err.println ("Can only OR comparision idents");
				System.exit(-1);
				return null;
			}
		}
		return new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class);
	}

	public static ShephongIdent std_basic_not (ShephongObject param) {
		param = param.evaluate(null);
		if (!(param instanceof ShephongIdent)) {
			System.err.println ("Can only NOT comparisions");
			System.exit(-1);
			return null;
		} else if (((ShephongIdent) param).getCompleteIdent().equals("class de.luh.psue.cklab.shephong.backend.ShephongStdLib.std_basic_head")) {
			return new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class);
		} else if (!((ShephongIdent) param).getCompleteIdent().equals("class de.luh.psue.cklab.shephong.backend.ShephongStdLib.std_basic_headOfTail")) {
			System.err.println ("Can only OR comparision idents");
			System.exit(-1);
			return null;
		}
		return new ShephongIdent("std_basic_head", ShephongStdLib.class);
	}
	
	public static ShephongIdent std_basic_isList(ShephongObject param){
		if(param instanceof ShephongList){
			return new ShephongIdent("std_basic_head", ShephongStdLib.class);
		}
		else{
			return new ShephongIdent("std_basic_headOfTail", ShephongStdLib.class);
		}
	}
	
	public static ShephongJavaObject std_basic_reflect(ShephongObject param){
		// expected input: (<parameter for funktion call, maybe a list> "methodname, empty if we want to call the constructor" "complete classname" #\)
		
		// get classname:
		ShephongList classNameList = (ShephongList) std_basic_head(param);
		
		// get method name / check if we need to call the constructor
		ShephongList methodNameList = (ShephongList) std_basic_headOfTail(param);
		if(!(methodNameList instanceof ShephongList 
				&& classNameList instanceof ShephongList )){
			System.err.println("Need an list at this place: (param \"methodname or empty list\" \"classname\" #\\) not an: " + methodNameList.getClass().getSimpleName());
			System.exit(-1);
		}
		String className = classNameList.toString();
		
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String methodName = methodNameList.toString();
		boolean callConstructor = false;
		if(methodNameList.size() == 0){
			callConstructor = true;
		}
		
		// determine the parameter types we've got.
		ShephongList methodParams = std_basic_tail(std_basic_tail(param));
		// count them:
		int paramCount = methodParams.size();
		
		// create the arrays we need for later for the method lookup & call
		Class<?> paramTypes[] = new Class[paramCount];
		Object paramObjects[] = new Object[paramCount];
		for(int i = 0; i < paramCount; i++){
			// determine the type, and if it's an shephongobject, choose the right conversion.
			ShephongObject actualObject = methodParams.getIndex(i);
			while((actualObject instanceof ShephongCall)
					|| (actualObject instanceof ShephongIdent)){
				actualObject = actualObject.evaluate(null);
			}
			if(actualObject instanceof ShephongList){
				paramTypes[i] = String.class;
				paramObjects[i] = actualObject.toString();
			}
			else if(actualObject instanceof ShephongNumber){
				paramTypes[i] = int.class;
				paramObjects[i] = ((ShephongNumber) actualObject).getValue();
			}
			else if(actualObject instanceof ShephongChar){
				paramTypes[i] = char.class;
				paramObjects[i] = ((ShephongChar) actualObject).getChar();
			}
			else if(actualObject instanceof ShephongJavaObject){
				paramTypes[i] = ((ShephongJavaObject) actualObject).getObject().getClass();
				paramObjects[i] = ((ShephongJavaObject) actualObject).getObject();
			}
		}
		// find the right method
		Method m = null;
		Constructor<?> c = null;
		try {
			if(!callConstructor){
				m = clazz.getMethod(methodName, paramTypes);
				if(!Modifier.isStatic(m.getModifiers())){
					System.err.println("Can't call a non static method without creating an instance of this class (" + clazz.getSimpleName() +") first.");
					System.exit(-1);
				}
			}
			else{
				c = clazz.getConstructor(paramTypes);
			}
			
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			System.err.println("Tried to find ");
			e1.printStackTrace();
		} 
		// call the method and return the object as new ShephongJavaObject
		Object retObject = null;
		try {
			if(!callConstructor){
				// assuming it's an static method
				retObject =  m.invoke(null, paramObjects);
			}
			else{
				retObject = c.newInstance(paramObjects);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // .callParam1(param-rest)
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ShephongJavaObject(retObject);
	}

	public static ShephongNumber std_basic_char2number(ShephongObject param) {
		param = std_internal_evalParam(param);
		if (!(param instanceof ShephongChar)) {
			System.out.println("Can't convert a "
					+ param.getClass().getSimpleName()
					+ " to a number");
			System.exit(-1);
			return null;
		}
		return new ShephongNumber((int) ((ShephongChar) param).getChar());
	}

	public static ShephongChar std_basic_number2char(ShephongObject param) {
		param = std_internal_evalParam(param);
		if (!(param instanceof ShephongNumber)) {
			System.out.println("Can't convert a "
					+ param.getClass().getSimpleName()
					+ " to a char");
			System.exit(-1);
			return null;
		}
		return new ShephongChar((char) ((ShephongNumber) param).getValue());
	}

	/**
	 * Breaks down an ShephongObject into something basic, which the stdlib functions
	 * could operate on.
	 * 
	 * This is done by repeatedly calling evaluate() while the returned object is
	 * a Call, Ident or JavaObject.
	 */
	private static ShephongObject std_internal_evalParam(ShephongObject param){
		while(param instanceof ShephongCall
				|| param instanceof ShephongIdent
				|| param instanceof ShephongJavaObject) {
			param=param.evaluate(null);
		}
		return param;
	}

}
