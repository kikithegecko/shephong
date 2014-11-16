package de.luh.psue.cklab.shephong.backend.objects.examples;

import de.luh.psue.cklab.shephong.backend.objects.ShephongChar;
/**
 * 
 * @author shephongkrewe (imp)
 *
 */
public class ExampleChar {
	public static void main(String argv[]){
		ShephongChar sc = new ShephongChar('a');
		System.out.println(sc.getChar());
	}
}
