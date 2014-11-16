package de.luh.psue.cklab.shephong.backend.objects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author shephongkrewe (imp)
 *
 */
public class ShephongJavaObject extends ShephongIdent{
	private Object object;
	
	public ShephongJavaObject(Object object) {
		this.object = object;
	}
	
	public Object getObject(){
		return this.object;
	}

	@Override
	public ShephongObject evaluate(ShephongObject param) {
		if(param == null){
			// keep close to the original object types.
			// return a number if it's an int(eger)
			if(this.object instanceof Integer){
				Integer i = (Integer) this.object;
				return new ShephongNumber(i.intValue());
			}
			// return a ShephongChar if it's a char.
			else if(this.object instanceof Character){
				Character c = (Character) this.object;
				return new ShephongChar(c.charValue());
			}
			// return this.tostring as shephong string aka shephongList in all other cases
			return new ShephongList(this.object.toString());
		}
		else{
			// do some magic here:
			Class<?> clazz = this.object.getClass();
			// the first element form the parameterlist is the method name.
			ShephongList methodNameList = (ShephongList) ((ShephongList) param).getHead();
			// check if the first argument is really a list:
			if(!(methodNameList instanceof ShephongList)){
				System.err.println("Need a list here as method name, not an: " + methodNameList.getClass().getSimpleName());
				System.exit(-1);
			}
			
			String methodName = null;
			// if the methodNameList doesn't contain anything, we should call the constructor.
			if(methodNameList.size() == 0){
				System.err.println("Can not take an empty list (or a string without anything in it) as method name.");
				System.exit(-1);
			}
			else {
				methodName = methodNameList.toString();
			}
			
			// determine the parameter types we've got.
			ShephongList methodParams = ((ShephongList )param).getTail();
			// count them:
			int paramCount = methodParams.size();
			
			// create the arrays we need for later for the method lookup & call
			Class<?> paramTypes[] = new Class[paramCount];
			Object paramObjects[] = new Object[paramCount];
			for(int i = 0; i < paramCount; i++){
				// determine the type, and if it's an shephongobject, choose the right conversion.
				ShephongObject actualObject = methodParams.getIndex(i);
				while(((actualObject instanceof ShephongCall)
						|| (actualObject instanceof ShephongIdent)
						) && !(actualObject instanceof ShephongJavaObject)){
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
			try {
				m = clazz.getMethod(methodName, paramTypes);
			} catch (SecurityException e1) {
				e1.printStackTrace();
				System.err.println("Not allowed to access the method: " + methodName + " in: " + this.object.getClass().getSimpleName() + ".");
				System.exit(-1);
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
				System.err.println("The method: " + methodName + " is not known in: " + this.object.getClass().getSimpleName() + ".");
				System.exit(-1);
			} 
			// call the method and return the object as new ShephongJavaObject
			Object retObject = null;
			try {
				retObject =  m.invoke(this.object, paramObjects);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.err.println("Cannot pass: " + param.getClass().getSimpleName() + " to the method: " + methodName + " from: " + this.object.getClass().getSimpleName() + ".");
				System.exit(-1);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				System.err.println("Cannot access method: " + methodName + " inside: " + this.object.getClass().getSimpleName() + ".");
				System.exit(-1);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				System.err.println("Cannot invoke the method: " + methodName + " from: " + this.object.getClass().getSimpleName() + ".");
				System.exit(-1);
			}
			
			return new ShephongJavaObject(retObject);
		}
	}

	@Override
	public int compareTo(ShephongObject o) {
		// TODO do we really want to compare stuff?
		if(this.object.equals(o)){
			return 0;
		}
		return -1;
	}
	
	@Override
	public String toString(){
		return this.evaluate(null).toString();
	}
	
}
