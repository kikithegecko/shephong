package de.luh.psue.cklab.shephong.il;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * TestCase for the il nodes.
 * @author shephongkrewe(imp)
 *
 */
public class IlNodeTest extends TestCase {
	@Test
	public void testStaticAssignmentNode(){	
		AssignmentNode asn = new AssignmentNode(null, null, null);
		
		// check defaults:
		assertEquals(false, asn.isStatic());
		assertEquals(false, asn.getContainsStatic());
		
		asn.setStatic(true);
		assertEquals(true, asn.isStatic());
		
		asn.setStatic(false);
		assertEquals(false, asn.isStatic());
		
		asn.setContainsStatic(true);
		assertEquals(true, asn.getContainsStatic());
		
		asn.setContainsStatic(false);
		assertEquals(false, asn.getContainsStatic());
	}
	
	@Test
	public void testStaticInsideAssignmentNode(){
		AssignmentNode asn = new AssignmentNode(null, null, null);
		
		CallNode staticCall = new CallNode(null, null, null);
		staticCall.setStatic(true);
		
		asn.setExpression(staticCall);
		
		assertEquals(true, asn.getContainsStatic());
		assertEquals(false, asn.isStatic());
		
		// check the constructor:
		asn = new AssignmentNode(null, null, staticCall);
		
		assertEquals(true, asn.getContainsStatic());
		assertEquals(false, asn.isStatic());
	}
	
	@Test
	public void testStaticCallNode(){
		CallNode call = new CallNode(null, null, null);
		
		assertEquals(false, call.getContainsStatic());
		assertEquals(false, call.isStatic());
		
		call.setStatic(true);
		
		assertEquals(false, call.getContainsStatic());
		assertEquals(true, call.isStatic());
		
		call.setStatic(false);
		call.setContainsStatic(true);
		
		assertEquals(true, call.getContainsStatic());
		assertEquals(false, call.isStatic());
		
		call.setContainsStatic(false);
		
		assertEquals(false, call.getContainsStatic());
		assertEquals(false, call.isStatic());
	}	

	@Test
	public void testStaticOpInsideCallNode(){
		CallNode call = new CallNode(null, null, null);
		
		CallNode staticInnerCall = new CallNode(null, null, null);
		staticInnerCall.setStatic(true);
		
		assertEquals(true, staticInnerCall.isStatic());
		
		call.setOp(staticInnerCall);
		
		assertEquals(true, call.getContainsStatic());
	}

	@Test
	public void testStaticParamInsideCallNode(){
		CallNode call = new CallNode(null, null, null);
		
		CallNode staticInnerCall = new CallNode(null, null, null);
		staticInnerCall.setStatic(true);
		
		assertEquals(true, staticInnerCall.isStatic());
		
		call.setParam(staticInnerCall);
		
		assertEquals(true, call.getContainsStatic());
	}
	
	@Test
	public void testStaticListNode(){
		ListNode list = new ListNode(null);
		// check constructor
		assertEquals(false, list.isStatic());
		assertEquals(false, list.getContainsStatic());
		
		list.setStatic(true);
		
		assertEquals(true, list.isStatic());
		assertEquals(false, list.getContainsStatic());
		
		list.setStatic(false);
		
		assertEquals(false, list.isStatic());
		assertEquals(false, list.getContainsStatic());
	}
	
	@Test
	public void testStaticInsideListNode(){
		ListNode list = new ListNode(null);
		
		CallNode staticCall = new CallNode(null, null, null);
		staticCall.setStatic(true);
		
		CallNode call = new CallNode(null, null, null);
		
		list.addContent(call);
		
		assertEquals(false, list.getContainsStatic());
		assertEquals(false, list.isStatic());
		
		list.addContent(staticCall);
		
		assertEquals(true, list.getContainsStatic());
		assertEquals(false, list.isStatic());
		
		ListNode tail = list.getTail();
		
		assertEquals(true, tail.getContainsStatic());
		assertEquals(false, tail.isStatic());
		
		ShephongNode head = list.getHead();
		
		assertEquals(false, head.getContainsStatic());
		assertEquals(false, head.isStatic());
	}
	
	@Test
	public void testStaticMagicNode(){
		MagicNode magic = new MagicNode(null, null);
		
		assertEquals(false, magic.getContainsStatic());
		assertEquals(false, magic.isStatic());
		
		magic.setStatic(true);
		
		assertEquals(false, magic.getContainsStatic());
		assertEquals(true, magic.isStatic());
		
		magic.setStatic(false);
		magic.setContainsStatic(true);
		
		assertEquals(true, magic.getContainsStatic());
		assertEquals(false, magic.isStatic());
		
		magic.setContainsStatic(false);
		
		assertEquals(false, magic.getContainsStatic());
		assertEquals(false, magic.isStatic());
	}
	
	@Test
	public void testStaticInsideMagicNode(){
		MagicNode magic = new MagicNode(null, null);
		
		CallNode staticCall = new CallNode(null, null, null);
		staticCall.setContainsStatic(true);
		
		magic.setParam(staticCall);
		
		assertEquals(true, magic.getContainsStatic());
		assertEquals(false, magic.isStatic());
		
		// check constructor:
		magic = new MagicNode(null, staticCall);
		
		assertEquals(true, magic.getContainsStatic());
		assertEquals(false, magic.isStatic());
	}
	
//	// TODO for later use.
//	@Test
//	public void testModuleNode(){
//		fail("not implemented.");
//	}
	
}
