package de.luh.psue.cklab.shephong.backend.objects;

public abstract class ShephongObject implements Comparable<ShephongObject>{
	public abstract ShephongObject evaluate(ShephongObject param);
}

