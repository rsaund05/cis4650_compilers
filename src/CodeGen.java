import c1Absyn.*;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CodeGen implements AbsynVisitor {
	int IADDR_SIZE = 1024;
	int DADDR_SIZE = 1024;
	int NO_REGS = 8;
  	int PC_REG = 7;

  	//public static int offset = 1;

  	public static ArrayList<Defined> definitions = new ArrayList<Defined>();
    public HashMap <String, ArrayList<Defined>> symTable = new HashMap<>();

  public static boolean inComp = false;
	//predifined registers
	public static int pc = 7;
	public static int gp = 6;
	public static int fp = 5;
	public static int ac = 0;
	public static int ac1 = 1;

	//public static int line = 0; //Variable to track line number in output file, starts at line after prelude
	public static int entry = 0;
	public static int globalOffset = 1;

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

	public static void prelude(String fileNameTM){
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
		emitRM("ST", 0, -1, 5, "store return");
		emitRM("ST", 0,-1,5, "load output value");
		emitRO("OUT", 0, 0, 0, "output");
		emitRM("LD", 7, -1, 5, "return to caller");
		emitBackup(savedLoc);
		emitRM("LDA", 7, 7, 7, "jump around i/o code");
		emitRestore();
		emitComment("End of standard prelude");
	}

	public static void finale(PrintStream console){
		//Printing finale
		emitRM("ST", fp, globalOffset+ofpFO, fp, "push ofp");
		emitRM("LDA", fp, globalOffset, fp, "push frame");
		emitRM_Abs("LDA", pc, entry, "jump to main loc");
    	emitRM("LD", fp, ofpFO, fp, "pop frame");
    	emitComment("end of execution.");
		emitRO("HALT", 0, 0, 0, "");
		//reset to stdout
		System.setOut(console); //Reset output to terminal
	}

	//Hashmap Functions
	public void delete( int level ) {
  ArrayList<String> toRemove = new ArrayList<String>();

  for (String key: symTable.keySet())
  {
    definitions = symTable.get(key);
    Dec temp = definitions.get(0).declaration;
    if (definitions.get(0).level == level)
    {
      if (temp instanceof SimpleDec)
      {
        SimpleDec remove = (SimpleDec)temp;
        toRemove.add(remove.name);
      }
      else if (temp instanceof ArrayDec)
      {
        ArrayDec remove = (ArrayDec)temp;
        toRemove.add(remove.name);     
      }
      else if (temp instanceof FunctionDec)
      {
        FunctionDec remove = (FunctionDec)temp;
        toRemove.add(remove.func);     
      }
    }
  }

  for (int i = 0; i < toRemove.size(); i++)
  {
    definitions = symTable.get(toRemove.get(i));
    Dec temp = definitions.get(0).declaration;
    definitions.remove(0);

    if (definitions.size() > 0)
    {
      if (temp instanceof SimpleDec)
      {
        SimpleDec toAdd = (SimpleDec)temp;
        symTable.put(toAdd.name, definitions);
      }
      else if (temp instanceof ArrayDec)
      {
        ArrayDec toAdd = (ArrayDec)temp;
        symTable.put(toAdd.name, definitions);
      }
      else
      {
        FunctionDec toAdd = (FunctionDec) temp;
        symTable.put(toAdd.func, definitions);
      }
    }
    else
    {
      if (temp instanceof SimpleDec)
      {
        SimpleDec toRem = (SimpleDec)temp;
        symTable.remove(toRem.name);
      }
      else if (temp instanceof ArrayDec)
      {
        ArrayDec toRem = (ArrayDec)temp;
        symTable.remove(toRem.name);
      }
      else
      {
        FunctionDec toRem = (FunctionDec)temp;
        symTable.remove(toRem.func);
      }
    }
    }

}
public int getType(Exp toCheck)
{
  int toReturn = -1;
  SimpleDec tempSDec;
  SimpleVar tempSVar;
  ArrayDec tempIDec;
  IndexVar tempIVar;
  FunctionDec tempFunc;
  Dec tempDec;

  if (toCheck instanceof VarExp)
  {
    VarExp tempExp = (VarExp) toCheck;

    if (tempExp.variable instanceof SimpleVar)
    {
      tempSVar = (SimpleVar)tempExp.variable;
      if (symTable.get(tempSVar.name) != null)
      {
        definitions = symTable.get(tempSVar.name);
        if (definitions.get(0).declaration instanceof SimpleDec)
        {
          tempSDec = (SimpleDec)definitions.get(0).declaration;
          toReturn = tempSDec.typ.typ;
        }
        else
        {
          tempIDec = (ArrayDec)definitions.get(0).declaration;
          toReturn = tempIDec.typ.typ;
        }
      }
      else{
          return -1;
      }
    }
    else if (tempExp.variable  instanceof IndexVar)
    {
      tempIVar = (IndexVar)tempExp.variable;
      if (symTable.get(tempIVar.name) != null)
      {
        definitions = symTable.get(tempIVar.name);
        tempIDec = (ArrayDec)definitions.get(0).declaration;
        toReturn = tempIDec.typ.typ;
      }
      else{
        return -1;
      }
    }
  }
  else if (toCheck instanceof IntExp)
  {
    toReturn = 0;
  }
  else if (toCheck instanceof CallExp)
  {
    CallExp tempCall = (CallExp)toCheck;
    definitions = symTable.get(tempCall.func);
    if (definitions != null)
    {
      tempFunc = (FunctionDec)definitions.get(0).declaration;
      toReturn = tempFunc.result.typ;
    }
    else
    {
      return -1;
    }
  }

  return toReturn;
}
public int typeCheck(Exp left, Exp right)
{
  int leftType = -1;
  int rightType = -1;

  leftType = getType(left);
  rightType = getType(right);

  if (leftType == -1 || rightType == -1)
    return -1;
  else if (leftType == rightType)
    return 1;
  else if (leftType != rightType)
    return 0;

    return -1;
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
    emitComment("-> op");
    // System.out.println( "AssignExp:" );
    level++;

    exp.lhs.accept( this, level );

    if (exp.lhs instanceof SimpleVar)
    {
    	SimpleVar temp = (SimpleVar)exp.lhs;
    	emitComment("-> id");
  		emitComment("looking up id: " + temp.name);
  		emitRM("LDA", 0, globalOffset, gp, "load id address");
  		emitComment("<- id");
  		emitRM("ST", ac, globalOffset, gp, "op: Push left");
    }
    else if (exp.lhs instanceof IndexVar)
    {
		IndexVar temp = (IndexVar)exp.lhs;
    	emitComment("-> id");
  		emitComment("looking up id: " + temp.name);
  		emitRM("LDA", 0, globalOffset, gp, "load id address");
  		emitComment("<- id");
  		emitRM("ST", ac, globalOffset, gp, "op: Push left");
    } 

	exp.rhs.accept( this, level );
    if (exp.rhs instanceof VarExp)
    {
    	VarExp tempV = (VarExp)exp.rhs;
    	Var rhs = (Var)tempV.variable;

    	 if (rhs instanceof SimpleVar) {
    		SimpleVar temp = (SimpleVar)rhs;
    		emitComment("-> id");
  			emitComment("looking up id: " + temp.name);
  			emitRM("LD", 0, globalOffset, gp, "load id value");
  			emitComment("<- id");
    	}
    	else if (rhs instanceof IndexVar){
    		IndexVar temp = (IndexVar)rhs;
    		emitComment("-> id");
  			emitComment("looking up id: " + temp.name);
  			emitRM("LD", 0, globalOffset, gp, "load id value");
  			emitComment("<- id");
    	}
    }

   
    if (exp.rhs instanceof IntExp) {
    	IntExp temp = (IntExp)exp.rhs;
    	emitComment("-> constant");
  		try {
  			int tempVal = Integer.parseInt(temp.value);
  			emitRM("LDC", ac, tempVal, 0, "load const");
  			emitComment("<- constant");
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		
    }
    emitRM("LD", ac, globalOffset, gp, "op: load left");
    emitRM("ST", ac, globalOffset, gp, "assign: Store value");
    emitComment("<- op");
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
	exp.setOffset(globalOffset);
	//globalOffset++;
  if (level == 0)
  {
    emitComment("allocating global var: " + exp.name);
    emitComment("<- vardec");
  }
  else if (inComp == true)
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
  inComp = true;
  if (exp.decs != null && exp.exp != null)
    level++;

  if (exp.decs != null)
    exp.decs.accept(this, level);
  if (exp.exp != null)
    exp.exp.accept(this, level);
  emitComment("<- compound statement");
  inComp = false;
}

//FunctionDec
public void visit(FunctionDec exp, int level ) {
  //if (exp.result.typ == NameTy.VOID)
    //System.out.println("FunctionDec: " + exp.func + " - VOID");
 // else if (exp.result.typ == NameTy.INT)
  //System.out.println("FunctionDec: " + exp.func + " - INT"); 
	exp.setOffset(globalOffset);
	//globalOffset++;
  emitComment("Processing function: " + exp.func);
  emitComment("jump around function body here");
  emitRM("ST", 0, -1, fp, "store return");
  level++;
  if (exp.params != null)
    exp.params.accept(this, level);

  if (exp.body != null)
    exp.body.accept(this, level);
	
	emitRM("LD", pc, -1, fp, "return to caller");
	emitRM_Abs("LDA", pc, 0, "Jump around function body");
  emitComment("<- fundecl");
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
	exp.setOffset(globalOffset);
	//globalOffset++;
    if (level == 0)
    {
      emitComment("allocating global var: " + exp.name);
      emitComment("<- vardec");
    }
    else if (inComp == true)
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
  //System.out.println("SimpleVar: " + exp.name);
}

//WhileExp
public void visit(WhileExp exp, int level ) {
  //System.out.println("WhileExp: ");
  emitComment("-> while");
  emitComment("while: jump after body comes back here");
  level++;
  if (exp.test != null){
  	exp.test.accept(this, level);
  	emitComment("while: jump to end belongs here");
  }
    
  if (exp.body != null)
    exp.body.accept(this, level);
	emitComment("<- while");
  
}

public void visit (NameTy exp, int level)
{

}

}