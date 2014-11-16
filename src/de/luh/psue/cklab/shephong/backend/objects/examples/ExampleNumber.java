package de.luh.psue.cklab.shephong.backend.objects.examples;

import de.luh.psue.cklab.shephong.backend.objects.ShephongNumber;
/**
 * 
 * @author shephongkrewe (imp)
 *
 */
public class ExampleNumber {
	public static void main(String argv[]){
		ShephongNumber a = new ShephongNumber(1);
		ShephongNumber b = new ShephongNumber(2);
		ShephongNumber c = new ShephongNumber(a.getValue() + b.getValue());
		System.out.println(c.getValue());
	}
}
