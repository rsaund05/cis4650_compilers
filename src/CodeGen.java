import c1Absyn.*;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CodeGen implements AbsynVisitor {
	int IADDR_SIZE = 1024;
	int DADDR_SIZE = 1024;
	int NO_REGS = 8;
  int PC_REG = 7;
  
  public static varNum = 1;

	//predifined registers
	public static int pc = 7;
	public static int gp = 6;
	public static int fp = 5;
	public static int ac = 0;
	public static int ac1 = 1;

	//public static int line = 0; //Variable to track line number in output file, starts at line after prelude
	public static int entry = 0;
	public static int globalOffset = 0;

	public static int ofpFO = 0;
	public static int retFO = -1;
	public static int initFO = -2;

	public static int emitLoc = 0;
	public static int highEmitLoc = 0;

	public static void emitRM(String opCode, int r1, int offset, int r2, String comment) {
		System.out.println(emitLoc + ": " + opCode + " " + r1 + ", " + offset + "(" + r2 + ") \t" + comment);
		emitLoc++;

		if (highEmitLoc < emitLoc)
			highEmitLoc = emitLoc;
	}

	public static void emitRO(String opCode, int r1, int r2, int r3, String comment){
		System.out.println(emitLoc + ": " + opCode + " " + r1 + ", " + r2 + ", " + r3 + " \t" + comment);
		emitLoc++;

		if (highEmitLoc < emitLoc)
			highEmitLoc = emitLoc;
	}

	public static void emitRM_Abs(String opCode, int r, int a, String comment){
		System.out.println(emitLoc + ": " + opCode + " " + r + " " + (a - (emitLoc + 1)) + "(" + pc + ") \t" + comment);
		emitLoc++;

		if (highEmitLoc < emitLoc)
			highEmitLoc = emitLoc;
	}

	public static void emitComment(String comment)
	{
		System.out.println("* " + comment);
	}

	public static int emitSkip(int distance)
	{
		int i = emitLoc;
		if (highEmitLoc < emitLoc)
			highEmitLoc = emitLoc;

		emitLoc += distance;
		return i;
	}

	public static void emitRestore()
	{
		emitLoc = highEmitLoc;
	}

	public static void emitBackup(int loc)
	{
		if (loc > highEmitLoc)
			emitComment("BUG in emitBackup");
		emitLoc = loc;
	}

	public static void codeGen(String fileNameTM) throws Exception{
		//Setting up output stream to file
		PrintStream console = System.out;
		//System.out.println("GENERATING CODE");
		FileOutputStream f = new FileOutputStream("./" + fileNameTM);
		System.setOut(new PrintStream(f));
		

		//Printing prelude
		emitComment("C-Minus Compilation to TM Code");
		emitComment("File: " + fileNameTM);
		emitComment("Standard prelude:");
		emitRM("LD", 6, 0, 0, "load gp with maxaddr");
		emitRM("LDA", 5, 0, 6, "Copy gp to fp");
		emitRM("ST", 0, 0, 0, "Clear content at loc");
		int savedLoc = emitSkip(1);
		emitComment("Jump around i/o routines here");
		emitComment("code for input routine");
		emitRM("ST", 0, -1, 5, "store return");
		emitRO("IN", 0, 0, 0, "input");
		emitRM("LD", 7, -1, 5, "return to caller");
		emitComment("code for output routine");
		emitRM("ST", 0,-1,5, "load output value");
		emitRO("OUT", 0, 0, 0, "output");
		emitRM("LD", 7, -1, 5, "return to caller");
		emitBackup(savedLoc);
		emitRM("LDA", 7, 7, 7, "jump around i/o code");
		emitRestore();

		//Rest of the stuff lol

		//Printing finale
		emitRM("ST", fp, globalOffset+ofpFO, fp, "push ofp");
		emitRM("LDA", fp, globalOffset, fp, "push frame");
		emitRM_Abs("LDA", pc, entry, "jump to main loc");
		emitRM("LD", fp, ofpFO, fp, "pop frame");
		emitRO("HALT", 0, 0, 0, "");

		if(f != null) f.close();
		System.setOut(console); //Reset output to terminal
	}

	  public void visit( ExpList expList, int level ) {
    while( expList != null ) {
      if (expList.head != null)
        expList.head.accept( this, level );
      expList = expList.tail;
    } 
  }

  public void visit (VarDecList varDecList, int level)
  {
    while(varDecList != null)
    {
      if (varDecList.head != null)
        varDecList.head.accept(this, level);

      varDecList = varDecList.tail;
    }
  }

  public void visit (DecList decList, int level)
  {
    while(decList != null)
    {
      if (decList.head != null)
        decList.head.accept(this, level);

      decList = decList.tail;
    }
  }

  public void visit( AssignExp exp, int level ) {
 
    // System.out.println( "AssignExp:" );
    level++;
    exp.lhs.accept( this, level );
    exp.rhs.accept( this, level );
  }

  public void visit( IfExp exp, int level ) {
    emitComment("-> if");
    //System.out.println( "IfExp:" );
    level++;
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    if (exp.elsepart != null )
    {
      emitComment("if: jump to else belongs here");
       exp.elsepart.accept( this, level );
    }
    emitComment("<- if");
  }

  public void visit( IntExp exp, int level ) {
    emitComment("-> constant");
    emitComment("<- constant");
    //System.out.println( "IntExp: " + exp.value ); 
  }

  public void visit( OpExp exp, int level ) {
    emitComment("-> op");
    //System.out.print( "OpExp:" ); 
    switch( exp.op ) {
      case OpExp.PLUS:
        //System.out.println( " + " );
        break;
      case OpExp.MINUS:
        //System.out.println( " - " );
        break;
      case OpExp.MUL:
        //System.out.println( " * " );
        break;
      case OpExp.DIV:
        //System.out.println( " / " );
        break;
      case OpExp.EQ:
        //System.out.println( " == " );
        break;
      case OpExp.NE:
        //System.out.println(" != ");
        break;
      case OpExp.LT:
        //System.out.println( " < " );
        break;
      case OpExp.LE:
        //System.out.println(" <= ");
        break;
      case OpExp.GT:
        //System.out.println( " > " );
        break;
      case OpExp.GE:
        //System.out.println(" >= ");
        break;
      default:
        //System.out.println( "Unrecognized operator at line " + exp.row + " and column " + exp.col);
    }
    level++;
    exp.left.accept( this, level );
    exp.right.accept( this, level );
    emitComment("<- op");
  }

  public void visit( VarExp exp, int level ) {
 
    //System.out.println( "VarExp: ");
    level++;

    if (exp.variable != null)
      exp.variable.accept(this, level);
  }

//still need to finish
//ArrayDec
public void visit(ArrayDec exp, int level ) {
  if (level == 0)
  {
    emitComment("allocating global var: " + exp.name);
    emitComment("<- vardec" +  varnum;)
    varNum++;
  }
  else
  {
    emitComment("Processing local var: " + exp.name);
  }
  // String ty = new String("");

  // if (exp.typ.typ == NameTy.VOID)
  //    ty = new String("VOID");
  // else if (exp.typ.typ == NameTy.INT)
  //    ty = new String("INT");

  // if (exp.size != null)
  // {
  //  if (!exp.size.value.equals(null))
  //     //System.out.println("ArrayDec: " + exp.name + "[" + exp.size.value + "]" + " - " + ty);
  // }
  // else
    //System.out.println("ArrayDec: " + exp.name + "[]" + " - " + ty);

}

//CallExp
public void visit(CallExp exp, int level ) {
  //System.out.println("CallExp: " + exp.func);
  emitComment("-> call of function: " + exp.func);
  level++;
  if (exp.args != null)
    exp.args.accept(this, level);
  emitComment("<- call");
}

//CompoundExp
public void visit(CompoundExp exp, int level ) {
  //System.out.println("CompoundExp: ");
  emitComment("-> compound statement");
  if (exp.decs != null && exp.exp != null)
    level++;

  if (exp.decs != null)
    exp.decs.accept(this, level);
  if (exp.exp != null)
    exp.exp.accept(this, level);
    emitComment("<- compound statement");
}

//FunctionDec
public void visit(FunctionDec exp, int level ) {
  //if (exp.result.typ == NameTy.VOID)
    //System.out.println("FunctionDec: " + exp.func + " - VOID");
 // else if (exp.result.typ == NameTy.INT)
  //System.out.println("FunctionDec: " + exp.func + " - INT"); 

  emitComment("Processing function: " + exp.func);
  emitComment("jump around function body here");
  level++;
  if (exp.params != null)
    exp.params.accept(this, level);

  if (exp.body != null)
    exp.body.accept(this, level);
}

//IndexVar
public void visit(IndexVar exp, int level ) {
  emitComment("-> subs");
  //System.out.println("IndexVar: " + exp.name);
  level++;
  exp.index.accept(this, level);
  emitComment("<- subs");
}

//NilExp
public void visit(NilExp exp, int level ) {
  //System.out.println("NilExp:");
}

//ReturnExp
public void visit(ReturnExp exp, int level ) {
  //System.out.println("ReturnExp: ");
  level++;
  emitComment("-> return");
  if (exp.exp != null)
    exp.exp.accept(this, level);
  emitComment("<- return");
}

//SimpleDec
public void visit(SimpleDec exp, int level ) {
    if (level == 0)
    {
      emitComment("allocating global var: " + exp.name);
      emitComment("<- vardec" +  varnum;)
      varNum++;
    }
    else
    {
      emitComment("Processing local var: " + exp.name);
    }
      
  //if (exp.typ.typ == NameTy.VOID)
    //System.out.println("SimpleDec: " + exp.name + " - VOID"); 
  //else if (exp.typ.typ == NameTy.INT)
    //System.out.println("SimpleDec: " + exp.name + " - INT");
}

//SimpleVar
public void visit(SimpleVar exp, int level ) {
  emitComment("-> id");
  emitComment("looking up id: " + exp.name);
  emitComment("<- id");
  //System.out.println("SimpleVar: " + exp.name);
}

//WhileExp
public void visit(WhileExp exp, int level ) {
  //System.out.println("WhileExp: ");
  emitComment("-> while");
  emitComment("while: jump after body comes back here");
  level++;
  if (exp.test != null)
    exp.test.accept(this, level);
  if (exp.body != null)
    exp.body.accept(this, level);
  emitComment("while: jump to end belongs here");
}

public void visit (NameTy exp, int level)
{

}

}