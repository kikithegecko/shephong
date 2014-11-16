package de.luh.psue.cklab.shephong.backend.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author shephongkrewe (imp)
 * 
 */

public class ShephongList extends ShephongObject implements
		Iterable<ShephongObject> {

	private List<ShephongObject> list;

	public ShephongList() {
		this.list = new ArrayList<ShephongObject>();
	}
	
	public ShephongList(int capacity) {
		this.list = new ArrayList<ShephongObject>(capacity);
	}
	
	public ShephongList(String string){
		this.list = new ArrayList<ShephongObject>();
		char[] chars = string.toCharArray();
		for(char c: chars){
			this.list.add(new ShephongChar(c));
		}
	}

	public ShephongList add(ShephongObject object) {
		this.list.add(object);

		// returning the list itself, allows us to do add(foo).add(bar)... :)
		return this;
	}

	public ShephongObject getHead() {
		if (this.list.size() == 0) {
			System.err.println("you can't get the head of an empty list!");
			System.exit(-1);
			return null;
		}
		return this.list.get(0);
	}

	public ShephongList getTail() {
		// TODO FIXME i'm slow :P
		ShephongList retList = new ShephongList();
		if (this.list.size() == 1) {
			return new ShephongList();
		} else if (this.list.size() == 0) {
			System.out
					.println("can't return tail from a list with one element!");
			System.exit(-1);
			return null;
		}
		for (int index = 1; index < this.list.size(); index++) {
			retList.add(this.list.get(index));
		}
		return retList;
	}

	public ShephongObject getIndex(int i) {
		// Just pass getI() to .get() of our list
		return this.list.get(i);
	}

	@Override
	public Iterator<ShephongObject> iterator() {
		return this.list.iterator();
	}

	/**
	 * THIS is MAPPLY!
	 */
	@Override
	public ShephongObject evaluate(ShephongObject param) {
		if(param == null){
			return this;
		}
		else if (param instanceof ShephongChar || param instanceof ShephongNumber) {
			System.err.println("Can't apply: "
					+ param.getClass().getSimpleName() + "on a list.");
			System.exit(-1);
			return null;
		} else if (param instanceof ShephongCall) {
			return this.evaluate(param.evaluate(null));
		} else if (param instanceof ShephongList) {
			ShephongList tempList = new ShephongList();

			for (ShephongObject listItem : this.list) {
				ShephongObject tempListItem = listItem;
				for (ShephongObject paramItem : ((ShephongList) param)) {
					tempListItem = paramItem.evaluate(tempListItem);
				}
			}

			return tempList;
		} else {
			ShephongList tempList = new ShephongList();

			for (ShephongObject listItem : this.list) {
				tempList.add(new ShephongCall(param, listItem));
			}

			return tempList;
		}
	}

	public int size() {
		return this.list.size();
	}

	@Override
	public int compareTo(ShephongObject o) {
		if (o instanceof ShephongList) {
			if (this.list.size() == 0 && ((ShephongList) o).size() == 0) {
				return 0;
			} else {
				for (int i = 0; i < this.list.size(); i++) {
					if (((ShephongList) o).size() <= i) {
						return 1; // this list is bigger than o
					}
					/* 
					 * get and evaluate the ShephongObjects in both lists
					 */
					ShephongObject o1 = this.list.get(i);
					while(!(o1 instanceof ShephongChar
							|| o1 instanceof ShephongNumber
							|| o1 instanceof ShephongList)){
						o1 = o1.evaluate(null);
					}

					ShephongObject o2 = ((ShephongList) o).getIndex(i);
					while(!(o2 instanceof ShephongChar
							|| o2 instanceof ShephongNumber
							|| o2 instanceof ShephongList)){
						o2 = o2.evaluate(null);
					}

					/*
					 * compare both evaluated objects and abort the comparision on
					 * the first difference.
					 */
					int c = o1.compareTo(o2);
					if (c != 0) {
						return c; // some elements are not the same
					}
				}
				if (this.list.size() != ((ShephongList) o).size()) {
					return -1; // this list is smaller than o
				}
				return 0;
			}
		}
		System.err.println("You can't compare ShephongLists with everything.");
		System.exit(-1);
		return 0;
	}

	@Override
	public String toString() {
		if (list.size() == 0) {
			/*
			 * Quick return if list is empty
			 */
			return "(~)";
		}
		StringBuffer buffer = new StringBuffer(" ~)");
		StringBuffer string = new StringBuffer();

		boolean firstElement = true;
		boolean charsOnly = true;
		for (ShephongObject node : list) {
			while (node instanceof ShephongCall
					|| node instanceof ShephongIdent) {
				node = node.evaluate(null);
			}
			if (charsOnly) {
				if (node instanceof ShephongChar) {
					string.append(((ShephongChar) node).getChar());
				} else {
					charsOnly = false;
				}
			}
			if (!firstElement) {
				buffer.insert(0, " ");
			}
			buffer.insert(0, node);
			firstElement = false;
		}
		if (charsOnly && string.length() > 0) {
			return string.toString();
		} else {
			buffer.insert(0, "(");
			return buffer.toString();
		}
	}

}
