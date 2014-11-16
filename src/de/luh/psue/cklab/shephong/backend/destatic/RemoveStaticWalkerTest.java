package de.luh.psue.cklab.shephong.backend.destatic;

import java.io.IOException;

import org.junit.Test;

import de.luh.psue.cklab.shephong.il.AssignmentNode;
import de.luh.psue.cklab.shephong.il.CallNode;
import de.luh.psue.cklab.shephong.il.IdentNode;
import de.luh.psue.cklab.shephong.il.ListNode;
import de.luh.psue.cklab.shephong.il.MagicNode;
import de.luh.psue.cklab.shephong.il.NumberNode;
import de.luh.psue.cklab.shephong.il.OpNode;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.cklab.shephong.il.ShephongNode;
import de.luh.psue.cklab.shephong.syntactical.ShephongParser;
import de.luh.psue.compiler.CompilerContext;
import de.luh.psue.compiler.error.CompilerException;
import de.luh.psue.compiler.input.InputBuffer;

import junit.framework.TestCase;

/**
 * 
 * @author shephongkrewe(imp)
 *
 */
public class RemoveStaticWalkerTest extends TestCase{
	@Test
	public void testSimpleNonStaticAssignment() throws CompilerException{
		// ( (#1 #+) abc :)
		IdentNode ident = new IdentNode(null, "abc");
		CallNode call = new CallNode(null, new OpNode(null, '+'), new NumberNode(null, 1));
		
		AssignmentNode assign = new AssignmentNode(null, null, null);
		assign.setIdent(ident);
		assign.setExpression(call);
				
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(assign, false);
		
		// check if we don't break the references:
		assertEquals("abc", ident.toString());
		
		// check if the overall assignment is what we are expecting:
		assertEquals("(abc = #+(#1))", retNode.toString());
	}
	
	@Test
	public void testSimpleStaticAssignment() throws CompilerException{
		// [(#1 #+) abc :]
		IdentNode ident = new IdentNode(null, "abc");
		CallNode call = new CallNode(null, new OpNode(null, '+'), new NumberNode(null, 1));
		
		AssignmentNode assign = new AssignmentNode(null, null, null);
		assign.setIdent(ident);
		assign.setExpression(call);
		
		assign.setStatic(true);
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(assign, false);
		
		// check if we don't break the references:
		assertEquals("abc", ident.toString());
		
		// check if the overall assignment is what we are expecting:
		assertEquals("(abc = #+(#1))", retNode.toString());
	}
	
	@Test
	public void testSimpleAssignmentWithStaticCallInside() throws CompilerException{
		// ( [smile #+]  abc :)
		IdentNode ident = new IdentNode(null, "abc");
		IdentNode innerIdent = new IdentNode(null, "smile");
		CallNode call = new CallNode(null, new OpNode(null, '+'), innerIdent);
		call.setStatic(true);
		
		AssignmentNode assign = new AssignmentNode(null, null, null);
		assign.setIdent(ident);
		assign.setExpression(call);
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(assign, false);
		
		// check if we don't break the references:
		assertEquals("abc", ident.toString());
		assertEquals("smile", innerIdent.toString());
		
		// check if the overall assignment is what we are expecting:
		assertEquals("(abc = #+(anon::run0::smile))", retNode.toString());
	}
	
	@Test
	public void testSimpleStaticCall() throws CompilerException{
		// [smile #+]
		IdentNode ident = new IdentNode(null, "smile");
		CallNode call = new CallNode(null, new OpNode(null, '+'), ident);
		call.setStatic(true);
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(call, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		
		// check if the overall assignment is what we are expecting:
		assertEquals("#+(anon::run0::smile)", retNode.toString());
	}
	
	@Test
	public void testCallWithStaticCallAsOp() throws CompilerException{
		// (#+ [smile])
		IdentNode ident = new IdentNode(null, "smile");
		CallNode innerCall = new CallNode(null, ident, null);
		innerCall.setStatic(true);

		CallNode call = new CallNode(null, innerCall, new OpNode(null, '+'));
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(call, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		
		// check if the overall assignment is what we are expecting:
		assertEquals("anon::run0::smile()(#+)", retNode.toString());
	}
	
	@Test
	public void testCallWithStaticParam() throws CompilerException{
		// ([smile] #+)
		IdentNode ident = new IdentNode(null, "smile");
		CallNode innerCall = new CallNode(null, ident, null);
		innerCall.setStatic(true);

		CallNode call = new CallNode(null, new OpNode(null, '+'), innerCall);
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(call, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		
		// check if the overall assignment is what we are expecting:
		assertEquals("#+(anon::run0::smile())", retNode.toString());
	}
	
	@Test
	public void testCallWithStaticListAsParam() throws CompilerException{
		// ([smile ~] #+)
		IdentNode ident = new IdentNode(null, "smile");
		ListNode list = new ListNode(null);
		list.addContent(ident);
		list.setStatic(true);
		
		CallNode call = new CallNode(null, new OpNode(null, '+'), list);
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(call, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		
		// check the output
		assertEquals("#+(list(anon::run0::smile))", retNode.toString());
	}
	
	@Test
	public void testCallWithStaticParamAndOp() throws CompilerException{
		// ([smile ~] [grin ~])
		IdentNode ident = new IdentNode(null, "smile");
		ListNode list = new ListNode(null);
		list.addContent(ident);
		list.setStatic(true);
		
		IdentNode ident2 = new IdentNode(null, "grin");
		ListNode list2 = new ListNode(null);
		list2.addContent(ident2);
		list2.setStatic(true);
		
		CallNode call = new CallNode(null, list2, list);
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(call, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		assertEquals("grin", ident2.toString());
		
		// check the output
		assertEquals("list(anon::run0::grin)(list(anon::run1::smile))", retNode.toString());
	}
	
	
	
	@Test
	public void testStaticList() throws CompilerException{
		// [smile ~]
		IdentNode ident = new IdentNode(null, "smile");
		ListNode list = new ListNode(null);
		list.addContent(ident);
		list.setStatic(true);
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(list, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		
		// check if the overall assignment is what we are expecting:
		assertEquals("list(anon::run0::smile)", retNode.toString());
	}
	
	@Test
	public void testListContainingStatic() throws CompilerException{
		// ( [smile] smile ~)
		IdentNode ident = new IdentNode(null, "smile");
		CallNode call = new CallNode(null, ident, null);
		call.setStatic(true);
		ListNode list = new ListNode(null);
		list.addContent(ident);
		list.addContent(call);
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		ShephongNode retNode = rsw.walk(list, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		
		// check if the overall assignment is what we are expecting:
		System.out.println(retNode.toString());
		assertEquals("list(smile, anon::run0::smile())", retNode.toString());
	}
	
	@Test
	public void testProgramWithMixedElements() throws IOException, CompilerException {
		CompilerContext c = new CompilerContext();
		
		ShephongParser parser = new ShephongParser(c, new InputBuffer("([123]`)"));
		
		ProgramNode prog = parser.parse();
		prog.unpackMagicNodes();
		
		RemoveStaticWalker rsw = new RemoveStaticWalker();
		ProgramNode destatified = (ProgramNode) rsw.walk(prog, false);
		
		assertEquals("program:\n" +
				"(+ = #+($))\n" +
				"(- = #-($))\n" +
				"(* = #*($))\n" +
				"(/ = #/($))\n" +
				"(% = #%($))\n" +
				"(& = #&($))\n" +
				"(| = #|($))\n" +
				"(! = #!($))\n" +
				"(= = #=($))\n" +
				"(< = #<($))\n" +
				"(> = #>($))\n" +
				"(^ = #^($))\n" +
				"(_ = #_($))\n" +
				"(@ = #@($))\n" +
				"(? = #?($))\n" +
				"(0 = #*(list(#10, $)))\n" +
				"(1 = #+(list(#1, #*(list(#10, $)))))\n" +
				"(2 = #+(list(#2, #*(list(#10, $)))))\n" +
				"(3 = #+(list(#3, #*(list(#10, $)))))\n" +
				"(4 = #+(list(#4, #*(list(#10, $)))))\n" +
				"(5 = #+(list(#5, #*(list(#10, $)))))\n" +
				"(6 = #+(list(#6, #*(list(#10, $)))))\n" +
				"(7 = #+(list(#7, #*(list(#10, $)))))\n" +
				"(8 = #+(list(#8, #*(list(#10, $)))))\n" +
				"(9 = #+(list(#9, #*(list(#10, $)))))\n" +
				"(\\ = #\\($))\n" +
				"(c2n = #c($))\n" +
				"(n2c = #n($))\n" +
				"anon::run0::3(anon::run1::2(anon::run2::1(#0)))()\n" +
				"(anon::run0::+ = #+($))\n" +
				"(anon::run0::- = #-($))\n" +
				"(anon::run0::* = #*($))\n" +
				"(anon::run0::/ = #/($))\n" +
				"(anon::run0::% = #%($))\n" +
				"(anon::run0::& = #&($))\n" +
				"(anon::run0::| = #|($))\n" +
				"(anon::run0::! = #!($))\n" +
				"(anon::run0::= = #=($))\n" +
				"(anon::run0::< = #<($))\n" +
				"(anon::run0::> = #>($))\n" +
				"(anon::run0::^ = #^($))\n" +
				"(anon::run0::_ = #_($))\n" +
				"(anon::run0::@ = #@($))\n" +
				"(anon::run0::? = #?($))\n" +
				"(anon::run0::0 = #*(list(#10, $)))\n" +
				"(anon::run0::1 = #+(list(#1, #*(list(#10, $)))))\n" +
				"(anon::run0::2 = #+(list(#2, #*(list(#10, $)))))\n" +
				"(anon::run0::3 = #+(list(#3, #*(list(#10, $)))))\n" +
				"(anon::run0::4 = #+(list(#4, #*(list(#10, $)))))\n" +
				"(anon::run0::5 = #+(list(#5, #*(list(#10, $)))))\n" +
				"(anon::run0::6 = #+(list(#6, #*(list(#10, $)))))\n" +
				"(anon::run0::7 = #+(list(#7, #*(list(#10, $)))))\n" +
				"(anon::run0::8 = #+(list(#8, #*(list(#10, $)))))\n" +
				"(anon::run0::9 = #+(list(#9, #*(list(#10, $)))))\n" +
				"(anon::run0::\\ = #\\($))\n" +
				"(anon::run0::c2n = #c($))\n" +
				"(anon::run0::n2c = #n($))\n" +
				"(anon::run1::+ = #+($))\n" +
				"(anon::run1::- = #-($))\n" +
				"(anon::run1::* = #*($))\n" +
				"(anon::run1::/ = #/($))\n" +
				"(anon::run1::% = #%($))\n" +
				"(anon::run1::& = #&($))\n" +
				"(anon::run1::| = #|($))\n" +
				"(anon::run1::! = #!($))\n" +
				"(anon::run1::= = #=($))\n" +
				"(anon::run1::< = #<($))\n" +
				"(anon::run1::> = #>($))\n" +
				"(anon::run1::^ = #^($))\n" +
				"(anon::run1::_ = #_($))\n" +
				"(anon::run1::@ = #@($))\n" +
				"(anon::run1::? = #?($))\n" +
				"(anon::run1::0 = #*(list(#10, $)))\n" +
				"(anon::run1::1 = #+(list(#1, #*(list(#10, $)))))\n" +
				"(anon::run1::2 = #+(list(#2, #*(list(#10, $)))))\n" +
				"(anon::run1::3 = #+(list(#3, #*(list(#10, $)))))\n" +
				"(anon::run1::4 = #+(list(#4, #*(list(#10, $)))))\n" +
				"(anon::run1::5 = #+(list(#5, #*(list(#10, $)))))\n" +
				"(anon::run1::6 = #+(list(#6, #*(list(#10, $)))))\n" +
				"(anon::run1::7 = #+(list(#7, #*(list(#10, $)))))\n" +
				"(anon::run1::8 = #+(list(#8, #*(list(#10, $)))))\n" +
				"(anon::run1::9 = #+(list(#9, #*(list(#10, $)))))\n" +
				"(anon::run1::\\ = #\\($))\n" +
				"(anon::run1::c2n = #c($))\n" +
				"(anon::run1::n2c = #n($))\n" +
				"(anon::run2::+ = #+($))\n" +
				"(anon::run2::- = #-($))\n" +
				"(anon::run2::* = #*($))\n" +
				"(anon::run2::/ = #/($))\n" +
				"(anon::run2::% = #%($))\n" +
				"(anon::run2::& = #&($))\n" +
				"(anon::run2::| = #|($))\n" +
				"(anon::run2::! = #!($))\n" +
				"(anon::run2::= = #=($))\n" +
				"(anon::run2::< = #<($))\n" +
				"(anon::run2::> = #>($))\n" +
				"(anon::run2::^ = #^($))\n" +
				"(anon::run2::_ = #_($))\n" +
				"(anon::run2::@ = #@($))\n" +
				"(anon::run2::? = #?($))\n" +
				"(anon::run2::0 = #*(list(#10, $)))\n" +
				"(anon::run2::1 = #+(list(#1, #*(list(#10, $)))))\n" +
				"(anon::run2::2 = #+(list(#2, #*(list(#10, $)))))\n" +
				"(anon::run2::3 = #+(list(#3, #*(list(#10, $)))))\n" +
				"(anon::run2::4 = #+(list(#4, #*(list(#10, $)))))\n" +
				"(anon::run2::5 = #+(list(#5, #*(list(#10, $)))))\n" +
				"(anon::run2::6 = #+(list(#6, #*(list(#10, $)))))\n" +
				"(anon::run2::7 = #+(list(#7, #*(list(#10, $)))))\n" +
				"(anon::run2::8 = #+(list(#8, #*(list(#10, $)))))\n" +
				"(anon::run2::9 = #+(list(#9, #*(list(#10, $)))))\n" +
				"(anon::run2::\\ = #\\($))\n" +
				"(anon::run2::c2n = #c($))\n" +
				"(anon::run2::n2c = #n($))\n"
				, destatified.toString());
			}
	
	@Test
	public void testStaticMagic() throws CompilerException{
		// [smile `]
		IdentNode ident = new IdentNode(null, "smile");
		MagicNode magic = new MagicNode(null, ident);
		magic.setStatic(true);

		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		MagicNode retNode = (MagicNode) rsw.walk(magic, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		
		// check the output
		assertEquals("`(anon::run0::smile)", retNode.toString());
	}
	
	@Test
	public void testMagicWithStaticContent() throws CompilerException{
		// ([smile] `)
		IdentNode ident = new IdentNode(null, "smile");
		CallNode call = new CallNode(null, ident, null);
		call.setStatic(true);
		MagicNode magic = new MagicNode(null, call);

		RemoveStaticWalker rsw = new RemoveStaticWalker();
		
		MagicNode retNode = (MagicNode) rsw.walk(magic, false);
		
		// check if we don't break the references:
		assertEquals("smile", ident.toString());
		
		// check the output
		assertEquals("`(anon::run0::smile())", retNode.toString());
	}
}
