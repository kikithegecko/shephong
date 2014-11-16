package de.luh.psue.cklab.shephong.backend.objects;

/**
 * 
 * @author shephongkrewe (imp)
 *
 */
public class ShephongChar extends ShephongObject {
	private char content;
	
	public ShephongChar(char content) {
		this.content = content;
	}

	public char getChar(){
		return this.content;
	}

	@Override
	public ShephongObject evaluate(ShephongObject param) {
		System.err.println("Unable to evaluate ShephongChar.");
		System.exit(-1);
		return null;
	}

	@Override
	public int compareTo(ShephongObject o) {
		if(o instanceof ShephongChar){
			if(this.content == ((ShephongChar) o).getChar()){
				return 0;
			}
			else if(this.content < ((ShephongChar) o).getChar()){
				return 1;
			}
			else{
				return -1;
			}
		}
		System.err.println("Can't compare ShephongChar with: " + o.getClass().getSimpleName());
		System.exit(-1);
		return 0;
	}
	
	public String toString(){
		return Character.toString(this.content);
	}
}
