package de.luh.psue.cklab.shephong.funtab;

import java.util.ArrayList;
import java.util.Iterator;

import de.luh.psue.cklab.shephong.il.CallNode;
import de.luh.psue.cklab.shephong.il.IdentNode;
import de.luh.psue.cklab.shephong.il.ProgramNode;

/**
 * Stores MagicNodes
 * 
 * @author shephongkrewe (salz)
 *
 */
public final class MagicTab implements Iterable<String> {
	private ArrayList<String> table;
	
	public MagicTab(){
		this.table = new ArrayList<String>();
	}
	
	public void add(String string){
		this.table.add(string);
	}
	
	public void appendContentToProgramNode(ProgramNode program){
		for(String entry : this.table){
			program.addChild(new CallNode(null, 
					new IdentNode(null, entry),
					null)); // FIXME we should have a real location
			// or at least a dummylocation which tells everybody it was auto generated.
		}
	}
	
	public boolean contains (String name) {
		return this.table.contains(name);
	}
	
	public boolean delete(String name) {
		return this.table.remove (name);
	}
	
	public String get (int index) {
		return this.table.get(index);
	}
	
	public int indexOf (String name) {
		return this.table.indexOf(name);
	}
	
	@Override
	public Iterator<String> iterator() {
		return this.table.iterator ();
	}

	@Override
	public String toString () {
		return this.table.toString ();
	}

	public ArrayList<String> getTable() {
		return table;
	}

	public void setTable(ArrayList<String> table) {
		this.table = table;
	}
}
