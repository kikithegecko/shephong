package de.luh.psue.cklab.shephong.backend.objects;

public class ShephongNumber extends ShephongObject {
	private int value;
	
	public ShephongNumber(int value) {
		this.value = value;
	}
	
	public ShephongNumber(ShephongNumber number){
		if(number instanceof ShephongNumber){
			this.value = number.getValue();
		}
	}
	
	public int getValue(){
		return this.value;
	}

	@Override
	public ShephongObject evaluate(ShephongObject param) {
		System.err.println("Can't evaluate a ShephongNumber.");
		System.exit(-1);
		return null;
	}

	@Override
	public int compareTo(ShephongObject o) {
		if(o instanceof ShephongNumber){
			if(this.value == ((ShephongNumber) o).getValue()){
				return 0;
			}
			else if(this.value < ((ShephongNumber) o).getValue()){
				return 1;
			}
			else{
				return -1;
			}
		}
		System.err.println("You can only compare ShephongNumbers with numbers, not with: " + o.getClass().getSimpleName());
		System.exit(-1);
		return 0;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}

}
