import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import de.luh.psue.cklab.shephong.backend.ILViewer;
import de.luh.psue.cklab.shephong.ShephongCompiler;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.meta.results.PathLocation;

/**
 * Just testing stuff without not testable with junit.
 * @author shephongkrewe
 *
 */
@SuppressWarnings("unused") // the need of imports depends on our personal Main
public class Main {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		ArrayList<String> examples = new ArrayList<String>();
		examples.add("jbc");
		examples.add("kikistests");
		examples.add("lazy");
//		examples.add("eulersalz");
		examples.add("utilities");
		examples.add("mergesort");
//		examples.add("tutorial");
		examples.add("hello_world_oli");
		
		System.out.println(">>>>>>>>>BEGIN RUN OF MAIN<<<<<<<<<");
		
		for(String ex : examples) {
			System.out.println("   >>>>>>>>>"+ex+"<<<<<<<<<   ");
			try {
				CompilerContext context = new CompilerContext();
				ShephongCompiler compiler = new ShephongCompiler(context);
				
				File resultFolder = new File("result");
				if (!resultFolder.exists()) resultFolder.mkdir();
				
				//compiler.addSource(new File("examples/jbc.shephong"));			
				compiler.addSource(new File("examples/"+ex+".shephong"));			
				compiler.setOutputFolder(new PathLocation("result"));
				
				compiler.check();
				//(new ILViewer()).view(compiler.getProgram());
	
				//compiler.interpret();
				compiler.compile();
				Class<? extends Runnable> metaClass = compiler.getProgramMetaClass();
				Runnable program = metaClass.newInstance();
				
				program.run();
	
				
			} catch (CompilerException e) {
			}
		}
		
		System.out.println(">>>>>>>>>END RUN OF MAIN<<<<<<<<<");
	}
}
