import java.util.*;
import de.luh.psue.cklab.shephong.backend.objects.*;
public class ShephongProgram {
public static ShephongObject number_1(ShephongObject paramlist) {
/*
(#1
 1 :)
*/
	return ( new ShephongNumber(1));
}

public static ShephongObject number_2(ShephongObject paramlist) {
/*
(#2
 2 :)
*/
	return ( new ShephongNumber(2));
}

public static ShephongObject number_3(ShephongObject paramlist) {
/*
(#3
 3 :)
*/
	return ( new ShephongNumber(3));
}

public static ShephongObject number_4(ShephongObject paramlist) {
/*
(#4
 4 :)
*/
	return ( new ShephongNumber(4));
}

public static ShephongObject number_5(ShephongObject paramlist) {
/*
(#5
 5 :)
*/
	return ( new ShephongNumber(5));
}

public static ShephongObject number_6(ShephongObject paramlist) {
/*
(#6
 6 :)
*/
	return ( new ShephongNumber(6));
}

public static ShephongObject number_7(ShephongObject paramlist) {
/*
(#7
 7 :)
*/
	return ( new ShephongNumber(7));
}

public static ShephongObject number_8(ShephongObject paramlist) {
/*
(#8
 8 :)
*/
	return ( new ShephongNumber(8));
}

public static ShephongObject number_9(ShephongObject paramlist) {
/*
(#9
 9 :)
*/
	return ( new ShephongNumber(9));
}

public static ShephongObject number_0(ShephongObject paramlist) {
/*
(#0
 0 :)
*/
	return ( new ShephongNumber(0));
}

public static ShephongObject caret(ShephongObject paramlist) {
/*
(#^
 ^ :)
*/
	return hash_caret;
}

public static ShephongObject _(ShephongObject paramlist) {
/*
(#_
 _ :)
*/
	return hash__;
}

public static ShephongObject caret_(ShephongObject paramlist) {
/*
((($
   _)
  ^)
 ^_ :)
*/
	return caret(_(paramlist));
}

public static ShephongObject apply(ShephongObject paramlist) {
/*
(((#2
   #1 ~)
  $)
 apply :)
*/
	return paramlist((new ShephongList()).add(( new ShephongNumber(1))).add(( new ShephongNumber(2))));
}

public static ShephongObject std_plus(ShephongObject paramlist) {
/*
(($
  #+)
 + :)
*/
	return hash_std_plus(paramlist);
}

public static ShephongObject double(ShephongObject paramlist) {
/*
((($
   $ ~)
  #+)
 double :)
*/
	return hash_std_plus((new ShephongList()).add(paramlist).add(paramlist));
}

public static ShephongObject primes(ShephongObject paramlist) {
/*
((#7
  #5
  #3
  #2 ~)
 primes :)
*/
	return (new ShephongList()).add(( new ShephongNumber(2))).add(( new ShephongNumber(3))).add(( new ShephongNumber(5))).add(( new ShephongNumber(7)));
}

public static ShephongObject fortify(ShephongObject paramlist) {
/*
((($
   #42 ~)
  ((#23
    $ ~)
   #<))
 fortify :)
*/
	return ((new ShephongList()).add(( new ShephongNumber(42))).add(paramlist));
}

public static ShephongObject sumstd_minusof(ShephongObject paramlist) {
/*
((((((((#1
        $ ~)
       #-)
      sum-of)
     $ ~)
    #+)
   #0 ~)
  ((#1
    $ ~)
   #<))
 sum-of :)
*/
	return ((new ShephongList()).add(( new ShephongNumber(0))).add(hash_std_plus((new ShephongList()).add(paramlist).add(sumstd_minusof(hash_std_minus((new ShephongList()).add(paramlist).add(( new ShephongNumber(1)))))))));
}

public static ShephongObject fak(ShephongObject paramlist) {
/*
((((((((#1
        $ ~)
       #-)
      fak)
     $ ~)
    #*)
   #1 ~)
  ((#1
    $ ~)
   #<))
 fak :)
*/
	return ((new ShephongList()).add(( new ShephongNumber(1))).add(hash_std_mult((new ShephongList()).add(paramlist).add(fak(hash_std_minus((new ShephongList()).add(paramlist).add(( new ShephongNumber(1)))))))));
}

public static ShephongObject fib(ShephongObject paramlist) {
/*
((((((((#2
        $ ~)
       #-)
      fib)
     (((#1
        $ ~)
       #-)
      fib) ~)
    #+)
   $ ~)
  ((#2
    $ ~)
   #<))
 fib :)
*/
	return ((new ShephongList()).add(paramlist).add(hash_std_plus((new ShephongList()).add(fib(hash_std_minus((new ShephongList()).add(paramlist).add(( new ShephongNumber(1)))))).add(fib(hash_std_minus((new ShephongList()).add(paramlist).add(( new ShephongNumber(2)))))))));
}

public static ShephongObject square(ShephongObject paramlist) {
/*
((($
   $ ~)
  #*)
 square :)
*/
	return hash_std_mult((new ShephongList()).add(paramlist).add(paramlist));
}

public static ShephongObject compose(ShephongObject paramlist) {
/*
(((($
    #_)
   #^)
  ($
   #^))
 compose :)
*/
	return (hash_caret(hash__(paramlist)));
}

public static ShephongObject sqfib(ShephongObject paramlist) {
/*
(((fib
   square ~)
  compose)
 sqfib :)
*/
	return compose((new ShephongList()).add(( new ShephongIdent(square))).add(( new ShephongIdent(fib))));
}

public static ShephongObject print(ShephongObject paramlist) {
/*
($
 print :)
*/
	return paramlist
}




public static void main(String[] args) {
/*
((#7
  #8
  #9 ~)
 ^_)
*/
	System.out.println(caret_((new ShephongList()).add(( new ShephongNumber(9))).add(( new ShephongNumber(8))).add(( new ShephongNumber(7)))));

/*
(((#11
   (#22
    #33
    #44 ~)
   #55 ~)
  ^_)
 ^_)
*/
	System.out.println(caret_(caret_((new ShephongList()).add(( new ShephongNumber(55))).add((new ShephongList()).add(( new ShephongNumber(44))).add(( new ShephongNumber(33))).add(( new ShephongNumber(22)))).add(( new ShephongNumber(11))))));

/*
((#1
  #2 ~)
 #+)
*/
	System.out.println(hash_std_plus((new ShephongList()).add(( new ShephongNumber(2))).add(( new ShephongNumber(1)))));

/*
(+
 apply)
*/
	System.out.println(apply(( new ShephongIdent(std_plus))));

/*
(((#2
   #3 ~)
  #+)
 double)
*/
	System.out.println(double(hash_std_plus((new ShephongList()).add(( new ShephongNumber(3))).add(( new ShephongNumber(2))))));

/*
(primes
 #^)
*/
	System.out.println(hash_caret(( new ShephongIdent(primes))));

/*
(primes
 #_)
*/
	System.out.println(hash__(( new ShephongIdent(primes))));

/*
((#6
  #2 ~)
 (#+
  #-
  #*
  #/ ~))
*/
	System.out.println(((new ShephongList()).add(( new ShephongNumber(2))).add(( new ShephongNumber(6)))));

/*
(#17
 fortify)
*/
	System.out.println(fortify(( new ShephongNumber(17))));

/*
(#27
 fortify)
*/
	System.out.println(fortify(( new ShephongNumber(27))));

/*
(#10
 sum-of)
*/
	System.out.println(sumstd_minusof(( new ShephongNumber(10))));

/*
(#10
 fak)
*/
	System.out.println(fak(( new ShephongNumber(10))));

/*
(#21
 fib)
*/
	System.out.println(fib(( new ShephongNumber(21))));

/*
(#10
 sqfib)
*/
	System.out.println(sqfib(( new ShephongNumber(10))));

/*
(('H
  'e
  'l
  'l
  'o
  ',
  ' 
  'W
  'o
  'r
  'l
  'd
  '! ~)
 print)
*/
	System.out.println(print((new ShephongList()).add(( new ShephongChar('!'))).add(( new ShephongChar('d'))).add(( new ShephongChar('l'))).add(( new ShephongChar('r'))).add(( new ShephongChar('o'))).add(( new ShephongChar('W'))).add(( new ShephongChar(' '))).add(( new ShephongChar(','))).add(( new ShephongChar('o'))).add(( new ShephongChar('l'))).add(( new ShephongChar('l'))).add(( new ShephongChar('e'))).add(( new ShephongChar('H')))));


 }

}
