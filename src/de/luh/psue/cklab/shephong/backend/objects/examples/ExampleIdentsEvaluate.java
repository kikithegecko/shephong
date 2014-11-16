package de.luh.psue.cklab.shephong.backend.objects.examples;

import java.util.ArrayList;
import java.util.List;

import de.luh.psue.cklab.shephong.backend.objects.ShephongIdent;
import de.luh.psue.cklab.shephong.backend.objects.ShephongNumber;
import de.luh.psue.cklab.shephong.backend.objects.ShephongObject;
/**
 * 
 * @author shephongkrewe (imp)
 *
 */
public class ExampleIdentsEvaluate {
	public static void main(String argv[]) {
		List<ShephongIdent> mList = new ArrayList<ShephongIdent>();
		
		mList.add(new ShephongIdent("foo", ExampleIdentsEvaluate.class));
		mList.add(new ShephongIdent("bar", ExampleIdentsEvaluate.class));
		mList.add(new ShephongIdent("blup", ExampleIdentsEvaluate.class));
		
		mList.get(2).evaluate(new ShephongNumber(1));
	}
	
	public static void foo(ShephongObject param){
		System.out.println("foo");
	}
	
	public static void bar(ShephongObject param){
		System.out.println("bar");
	}
	
	public static void blup(ShephongObject param){
		System.out.println("blup");
	}
}
