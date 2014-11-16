package de.luh.psue.cklab.shephong.backend.objects;
/**
 * 
 * @author shephongkrewe(imp)
 *
 */
public class ShephongCall extends ShephongObject {
	protected ShephongObject op;
	private ShephongObject param;
	
	private ShephongObject value = null;
	
	public ShephongCall(ShephongObject op, ShephongObject param) {
		this.op = op;
		this.param = param;
	}
	
	public ShephongObject getOp(){
		return this.op;
	}
	
	public ShephongObject getParam(){
		return this.param;
	}
	
	public ShephongObject evaluate(ShephongObject callparam){
		// in case we don't get an paramlist, we need to evaluate this call itself (we should have parameters, maybe at least).
		// there's no paramlist given on toplevel, see ExampleCallEvalute for more details.
		ShephongObject paramlist;
		if(callparam == null){
			// just set the already given param as paramlist and start working.
			paramlist = this.param;
			// are we already evaluated? then just return our value
			if (this.value != null) {
				return this.value;
			}
		} else {
			paramlist = callparam;
		}
		if(this.op instanceof ShephongCall){
			this.op = ((ShephongCall) this.op).evaluate(((ShephongCall) this.op).getParam());
			while(this.op instanceof ShephongCall){
				// the callnode contains it's own paramterlist, it doesn't need ours.
				ShephongObject tempParam = ((ShephongCall) this.op).getParam();
				this.op = this.op.evaluate(tempParam);
			}
		}
		// let's hope it's an ShephongIdent (or ShephongList) to evaluate.
		ShephongObject ret = this.op.evaluate(paramlist);
		// make sure we don't return another callnode:
		while(ret instanceof ShephongCall){
			ret = ret.evaluate(null);
		}
		// were we called without params? if so we can store our value since it should not change in subsequent calls
		if (callparam == null) {
			this.value = ret;
		}
		return ret;
	}

	@Override
	public int compareTo(ShephongObject o) {
		// do we really want to compare Calls? -> NO.
		System.err.println("unimplemented (comparsion of calls)");
		System.exit(-1);
		return 0;
	}
	
	public String toString(){
		ShephongObject retObj = this.evaluate(null); 
		return retObj.toString();
	}
}
