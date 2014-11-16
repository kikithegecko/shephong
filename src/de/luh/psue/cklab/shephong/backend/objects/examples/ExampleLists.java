package de.luh.psue.cklab.shephong.backend.objects.examples;

import de.luh.psue.cklab.shephong.backend.objects.ShephongList;
import de.luh.psue.cklab.shephong.backend.objects.ShephongNumber;
/**
 * 
 * @author shephongkrewe (imp)
 *
 */
public class ExampleLists {
	public static void main(String argv[]){
		// create simple list:
		ShephongList list = new ShephongList();
		list.add(new ShephongNumber(1)).add(new ShephongNumber(2));
		
		/* and now, use the list to add the two numbers.
		 * note: we don't check the elemements here!
		 */
		ShephongNumber sn = new ShephongNumber(((ShephongNumber) list.getHead()).getValue() + ((ShephongNumber) ((ShephongList) list.getTail()).getHead()).getValue());
		System.out.println(sn.getValue());
	}
}
