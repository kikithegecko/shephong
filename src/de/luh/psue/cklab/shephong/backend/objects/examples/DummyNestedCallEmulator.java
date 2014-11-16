package de.luh.psue.cklab.shephong.backend.objects.examples;

import de.luh.psue.cklab.shephong.backend.objects.ShephongCall;
import de.luh.psue.cklab.shephong.backend.objects.ShephongIdent;
import de.luh.psue.cklab.shephong.backend.objects.ShephongObject;
/**
 * This class returns an useable ShephongIdent (note: it's hardcoded for use with ExampleCallEvaluate).
 * @author Shephongkrewe(imp)
 *
 */
public class DummyNestedCallEmulator extends ShephongCall {
	public DummyNestedCallEmulator(ShephongObject op, ShephongObject param) {
		super(op, param);
	}
	
	@Override
	public ShephongObject evaluate(ShephongObject param){
		System.out.println("eval dummy, returning an ShephongIdent");
		return new ShephongIdent("print", ExampleCallEvaluate.class);
	}
}
