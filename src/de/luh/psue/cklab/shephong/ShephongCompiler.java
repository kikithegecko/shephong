package de.luh.psue.cklab.shephong;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;

import de.luh.psue.cklab.shephong.backend.ByteCodeGenerator;
import de.luh.psue.cklab.shephong.backend.Interpreter;
import de.luh.psue.cklab.shephong.backend.destatic.RemoveStaticWalker;
import de.luh.psue.cklab.shephong.backend.desugar.DeSugarParam;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.cklab.shephong.semantical.InterferTypeWalker;
import de.luh.psue.cklab.shephong.semantical.SemanticalErrorWalker;
import de.luh.psue.cklab.shephong.syntactical.ShephongParser;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.IGeneratingCompiler;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.error.CompilerLogger;
import de.luh.psue.compiler.error.Reason;
import de.luh.psue.compiler.input.IInputBuffer;
import de.luh.psue.compiler.input.ILocation;
import de.luh.psue.compiler.input.SourceManager;
import de.luh.psue.compiler.input.SourcePool;
import de.luh.psue.java.meta.JavaClassFile;
import de.luh.psue.meta.IClassFile;
import de.luh.psue.meta.MetaCompiler;
import de.luh.psue.meta.results.IResultLocation;

/**
 * A loader calss for compiling, interpreting and parsing
 * Shephong code.
 * 
 * @author shephongkrewe (karo)
 *
 */
public class ShephongCompiler implements IGeneratingCompiler {

	private CompilerContext context;
	private CompilerLogger logger;
	private SourcePool sourcePool;

	private IResultLocation outputFolder;
	private MetaCompiler metaCompiler;
	private IClassFile programClass;

	private ProgramNode program;

	private boolean parsed;
	private boolean checked;
	private boolean optimized;
	private boolean compiled;

	public ShephongCompiler(CompilerContext context) {
		this.context = context;
		this.logger = CompilerLogger.getInstance(context);
		this.sourcePool = new SourcePool(context);
		this.metaCompiler = MetaCompiler.getInstance(context);
		this.program = new ProgramNode();
	}
	
	@Override
	public CompilerContext getContext() {
		return this.context;
	}

	@Override
	public boolean addSource(Object source) {
		return this.sourcePool.enqueueSource(source);
	}

	@Override
	public Set<Object> getSources() {
		return this.sourcePool.getSources();
	}

	@Override
	public IResultLocation getOutputFolder() {
		return this.outputFolder;
	}

	@Override
	public void setOutputFolder(IResultLocation outputFolder) {
		this.outputFolder = outputFolder;
	}

	@Override
	public List<File> getClassPath() {
		return this.metaCompiler.getClassPath();
	}

	public ProgramNode getProgram() {
		return this.program;
	}

	@Override
	public int getErrorCount() {
		return this.logger.getErrorCount();
	}

	@Override
	public void parse() throws CompilerException {
		// TODO handle multiple files
		if(isParsed())
			return;
		
		while(!this.sourcePool.isEmpty()){
			Object source = this.sourcePool.dequeueSource();
			
			IInputBuffer buffer = SourceManager.getBuffer(context, source);
			if (buffer == null) 
				this.logger.criticalError(Reason.IO, (ILocation) null, "Cannot create buffer for source " + SourceManager.getName(source));
			ShephongParser parser = new ShephongParser(context, buffer);
	
			ProgramNode preProgram = parser.parse();
			// DeSugarStuff - begin
			DeSugarParam dsp = new DeSugarParam();
			dsp.walk(preProgram, null);
			
			RemoveStaticWalker rsw = new RemoveStaticWalker();
			
			ProgramNode partProgram = (ProgramNode) rsw.walk(preProgram, false);
			
			// last step: unpack the MagicNodes
			partProgram.unpackMagicNodes();
			// DeSugarStuff - end
			
			//TODO please implement ProgramNode.combine(), so we can uncomment the following to combine multiple source files:
			//program.combine(partProgram);
			program = partProgram;
		}
		
		if (this.logger.getErrorCount() != 0) {
			this.logger.criticalError(Reason.Syntactical, program, "syntactical errors occured");
		}
	
		this.parsed = true;
	}

	@Override
	public boolean isParsed() {
		return this.parsed;
	}

	@Override
	public void check() throws CompilerException {
		if (checked) return;
		parse();
	
		new InterferTypeWalker(context).walk(program, null);
		new SemanticalErrorWalker(context).walk(program, null);
		
		if (logger.getErrorCount() != 0) {
			logger.criticalError(Reason.Semantical, program, "semantical errors occured");
		}
		checked = true;
		
	}

	@Override
	public boolean isChecked() {
		return this.checked;
	}

	@Override
	public void optimize() throws CompilerException {
		check();
		if (optimized) return;
	
		//TODO: do optimzation
		optimized = true;
	}

	@Override
	public boolean isOptimized() {
		return this.optimized;
	}

	public void interpret() throws CompilerException {
		optimize();
		Interpreter interpreter = new Interpreter(context);
		interpreter.interpret(program);
		if (logger.getErrorCount() != 0) {
			logger.criticalError(Reason.Runtime, program, "runtime errors occured");
		}
	}

	@Override
	public void compile() throws CompilerException {
		optimize();
		if (compiled) return;
	
		compileGenerated();
		//compileInterpreter();
		compiled = true;
	}

	public void compileInterpreter() throws CompilerException {
		JavaClassFile classFile = new JavaClassFile("de.luh.psue", "Interpreter");
	
		classFile.printPackage();
		classFile.printClassDeclaration();
		classFile.printImplements(Runnable.class);
		classFile.printClassBegin();
	
		classFile.printMethodDeclaration(Modifier.STATIC | Modifier.PUBLIC, void.class, "main");
		classFile.printArgument(String[].class, "args");
		classFile.printMethodBegin();
		classFile.print("(new ");
		classFile.printClassName();
		classFile.println("()).run();");
		classFile.printMethodEnd();
	
		classFile.printMethodDeclaration(void.class, "run");
		classFile.printMethodBegin();
		classFile.printTypeName(CompilerContext.class);
		classFile.print(" context = new ");
		classFile.printTypeName(CompilerContext.class);
		classFile.println("();");
		classFile.printTypeName(this.getClass());
		classFile.print(" compiler = new ");
		classFile.printTypeName(this.getClass());
		classFile.println("(context);");
	
		for (Object source : sourcePool.getSources()) {
			if (source instanceof IFile) {
				IFile file = (IFile) source;
				classFile.print("compiler.addSource(new java.io.File(\"");
				classFile.print(file.getLocationURI().getPath());
				classFile.println("\"));");
			}
		}
		classFile.println("try {");
		classFile.indent++;
		classFile.println("compiler.interpret();");
		classFile.indent--;
		classFile.println("} catch (Throwable e) { e.printStackTrace(); }");
		classFile.printMethodEnd();
	
		classFile.printClassEnd();
	
		metaCompiler.addClass(classFile);
		if (!metaCompiler.compile()) {
			logger.criticalError(Reason.Unknown, "error during generate interpreter class");
		} else if (outputFolder != null) {
			outputFolder.clean();
			metaCompiler.writeTo(outputFolder);
		}
	
		programClass = classFile;
	}

	public void compileGenerated() throws CompilerException {
		optimize();
		if (compiled) return;
	
		ByteCodeGenerator generator = new ByteCodeGenerator(context);
		programClass = generator.generate(program);
		if (logger.getErrorCount() != 0) {
			logger.criticalError(Reason.Unknown, program, "generator errors occured");
		}
	
		metaCompiler.addClass(programClass);
		if (!metaCompiler.compile()) {
			logger.criticalError(Reason.Unknown, "error during generate cojen classes");
		} else if (outputFolder != null) {
			outputFolder.clean();
			metaCompiler.writeTo(outputFolder);
		}
	}

	@Override
	public boolean isCompiled() {
		return this.compiled;
	}

	@Override
	public boolean dependsOnExtension(String extension) {
		return false;
	}

	@Override
	public boolean supportsExtension(String extension) {
		return extension.equals("shephong");
	}

	@SuppressWarnings("unchecked")
	public Class<? extends Runnable> getProgramMetaClass() {
		return (Class<? extends Runnable>) programClass.getMetaClass();
	}

}
