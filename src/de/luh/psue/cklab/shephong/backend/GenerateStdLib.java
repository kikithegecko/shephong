package de.luh.psue.cklab.shephong.backend;

import java.util.ArrayList;

import de.luh.psue.cklab.shephong.il.AssignmentNode;
import de.luh.psue.cklab.shephong.il.CallNode;
import de.luh.psue.cklab.shephong.il.ExpressionNode;
import de.luh.psue.cklab.shephong.il.IdentNode;
import de.luh.psue.cklab.shephong.il.ListNode;
import de.luh.psue.cklab.shephong.il.NumberNode;
import de.luh.psue.cklab.shephong.il.OpNode;
import de.luh.psue.cklab.shephong.il.ParameterNode;
import de.luh.psue.cklab.shephong.il.ProgramNode;
import de.luh.psue.compiler.input.ILocation;

public class GenerateStdLib {
	/**
	 * This is our stdlib generated as ShephongNodes
	 * 
	 * @return stdlib as List of AssignmentNodes
	 */
	public static ArrayList<ExpressionNode> getStdLib() {
		ArrayList<ExpressionNode> stdLib = new ArrayList<ExpressionNode>();
		ILocation stdLoc = null;
		
		// { --------------------------------------------- }
		// { -- THIS IS THE SHEPHONG STANDARD LIBRARY ---- }
		// { --------------------------------------------- }
		// { -- DO _NOT_ CHANGE ANYTHING IN HERE UNLESS -- }
		// { -- YOU ARE REALLY KNOWING WHAT YOU DO ------- }
		// { --------------------------------------------- }
		// 
		// 
		// { PART 1: ARITHMETIC }
		// 
		// (($ #+) + :) { add }
		stdLib.add(justAssign(stdLoc, '+'));
		
		// (($ #-) - :) { subtract }
		stdLib.add(justAssign(stdLoc, '-'));
		
		// (($ #*) * :) { multiply }
		stdLib.add(justAssign(stdLoc, '*'));
		
		// (($ #/) / :) { divide }
		stdLib.add(justAssign(stdLoc, '/'));
		
		// (($ #%) % :) { modulo }
		stdLib.add(justAssign(stdLoc, '%'));
		
		// 
		// { PART 2: LOGIC }
		// 
		// (($ #&) & :) { and }
		stdLib.add(justAssign(stdLoc, '&'));
		
		// (($ #|) | :) { or }
		stdLib.add(justAssign(stdLoc, '|'));
		
		// (($ #!) ! :) { not }
		stdLib.add(justAssign(stdLoc, '!'));
		
		// (($ #=) = :) { equal }
		stdLib.add(justAssign(stdLoc, '='));
		
		// (($ #<) < :) { less than }
		stdLib.add(justAssign(stdLoc, '<'));
		
		// (($ #>) > :) { greater than }
		stdLib.add(justAssign(stdLoc, '>'));
		
		// 
		// { PART 3: LISTS }
		// 
		// (($ #^) ^ :) { head }
		stdLib.add(justAssign(stdLoc, '^'));
		
		// (($ #_) _ :) { tail }
		stdLib.add(justAssign(stdLoc, '_'));
		
		// (($ #@) @ :) { append }
		stdLib.add(justAssign(stdLoc, '@'));
		
		// (($ #?) ? :) { isList }
		stdLib.add(justAssign(stdLoc, '?'));
		
		// 
		// { PART 4: NUMBERS }
		// 
		// ( ($ #10 #*) 0 :)
		stdLib.add(number0Assign(stdLoc));
		
		// ((($ #10 #*) #1 #+) 1 :)
		stdLib.add(numberAssign(stdLoc, 1));
		
		// ((($ #10 #*) #2 #+) 2 :)
		stdLib.add(numberAssign(stdLoc, 2));
		
		// ((($ #10 #*) #3 #+) 3 :)
		stdLib.add(numberAssign(stdLoc, 3));
		
		// ((($ #10 #*) #4 #+) 4 :)
		stdLib.add(numberAssign(stdLoc, 4));
		
		// ((($ #10 #*) #5 #+) 5 :)
		stdLib.add(numberAssign(stdLoc, 5));
		
		// ((($ #10 #*) #6 #+) 6 :)
		stdLib.add(numberAssign(stdLoc, 6));
		
		// ((($ #10 #*) #7 #+) 7 :)
		stdLib.add(numberAssign(stdLoc, 7));
		
		// ((($ #10 #*) #8 #+) 8 :)
		stdLib.add(numberAssign(stdLoc, 8));
		
		// ((($ #10 #*) #9 #+) 9 :)
		stdLib.add(numberAssign(stdLoc, 9));
		
		// 
		// { PART 5: MAGIC }
		// 
		// (($ #\) \ :) { reflection api }
		stdLib.add(justAssign(stdLoc, '\\'));

		//
		// { PART 6: CHARACTER <=> NUMBER CONVERSION }
		//
		// (($ #c) c2n :)
		stdLib.add(new AssignmentNode(stdLoc,
				new IdentNode(stdLoc, "c2n"),
				new CallNode(stdLoc,
						new OpNode(stdLoc, 'c'),
						new ParameterNode(stdLoc, 0))));
        // (($ #n) n2c :)
		stdLib.add(new AssignmentNode(stdLoc,
				new IdentNode(stdLoc, "n2c"),
				new CallNode(stdLoc,
						new OpNode(stdLoc, 'n'),
						new ParameterNode(stdLoc, 0))));

		return stdLib;
	}
	
	/**
	 * For Unit-Tests generate from our stdlib a String which must be
	 * prepended to the expected output.
	 * 
	 * @return .toString() of the stdlib in a ProgramNode
	 */
	public static String getStdLibString() {
		ProgramNode stdLib = new ProgramNode();
		stdLib.setExpressions(getStdLib());
		return stdLib.toString();
	}
	
	private static AssignmentNode justAssign (ILocation loc, Character op) {
		return new AssignmentNode(loc,
				new IdentNode(loc, op.toString()),
				new CallNode(loc,
						new OpNode(loc, op),
						new ParameterNode(loc, 0)));
	}

	private static AssignmentNode number0Assign (ILocation loc) {
		return new AssignmentNode (loc,
				new IdentNode(loc, "0"),
				new CallNode(loc,
						new OpNode(loc, '*'),
						new ListNode(loc)
							.addContent(new NumberNode(loc, 10))
							.addContent(new ParameterNode(loc, 0))));
	}

	private static AssignmentNode numberAssign (ILocation loc, Integer num) {
		return new AssignmentNode (loc,
				new IdentNode(loc, num.toString()),
				new CallNode(loc,
						new OpNode(loc, '+'), 
						new ListNode(loc)
							.addContent(new NumberNode(loc, num))
							.addContent(new CallNode(loc,
									new OpNode(loc, '*'),
									new ListNode(loc)
										.addContent(new NumberNode(loc, 10))
										.addContent(new ParameterNode(loc, 0))))));
	}

}
