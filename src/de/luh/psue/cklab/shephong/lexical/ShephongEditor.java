package de.luh.psue.cklab.shephong.lexical;

import de.luh.psue.eclipse.editor.DefaultTextEditor;
import de.luh.psue.eclipse.language.builder.LanguageBuilder;

/**
 * Class for the editor with shephong syntax highlighting.
 * Copied and modified from the "Arithmetic" example project.
 * 
 * @author karo
 *
 */
public class ShephongEditor extends DefaultTextEditor{

	public ShephongEditor(){
		super(ShephongScanner.class, LanguageBuilder.BUILDER_ID);
	}		
}
