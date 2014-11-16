package de.luh.psue.cklab.shephong.backend.objects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author shephongkrewe (imp)
 *
 */

public class ShephongIdent extends ShephongObject {
	private Class<?> clazz;
	private String meth;
	
	protected ShephongIdent(){
		// only for internal use..
	}
	
	public ShephongIdent(String meth, Class<?> c) {
		this.clazz = c;
		this.meth = meth;
	}
	
	public ShephongObject evaluate(ShephongObject param) {
		try {
			Method method = this.clazz.getMethod(this.meth, new Class[] {ShephongObject.class});
			return (ShephongObject) method.invoke(null, param);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.err.println("Cannot pass: " + param.getClass().getSimpleName() + " to the method: " + this.meth + " from: " + this.clazz + ".");
			System.exit(-1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.err.println("Cannot access method: " + this.meth + " inside: " + this.clazz + ".");
			System.exit(-1);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.err.println("Cannot invoke the method: " + this.meth + " from: " + this.clazz + ".");
			System.exit(-1);
		} catch (SecurityException e) {
			e.printStackTrace();
			System.err.println("Not allowed to access the method: " + this.meth + " in: " + this.clazz + ".");
			System.exit(-1);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.err.println("The method: " + this.meth + " is not known in: " + this.clazz.getSimpleName() + ".");
			System.exit(-1);
		}
		// in case something else explodes ;)
		System.err.println("Something got terribly wrong... See above.");
		System.exit(-1);
		return null; // make the compiler happy....
	}
	
	/**
	 * This function is needed for the compare. 
	 * @return classname+methodname
	 */
	public String getCompleteIdent(){
		return this.clazz + "." + this.meth;
	}

	@Override
	public int compareTo(ShephongObject o) {
		if(o instanceof ShephongIdent){
			if(this.getCompleteIdent().equals(((ShephongIdent) o).getCompleteIdent())){
				return 0;
			}
			else{
				return -1;
			}
		}
		System.err.println("can't compare ShephongIdent with: " + o.getClass().getSimpleName());
		System.exit(-1);
		return -1;
	}
	
	public String toString(){
		ShephongObject retObj = this.evaluate(null);
		while(retObj instanceof ShephongIdent){
			retObj = retObj.evaluate(null);
		}
		return retObj.toString();
	}
}
