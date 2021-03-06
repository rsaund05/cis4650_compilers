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
  
  //offsets
  public static int globalOffset = 0;
  public static int frameOffset = 0;

	public static int ofpFO = 0;
	public static int retFO = -1;
	public static int initFO = -2;

	public static int emitLoc = 0;
	public static int highEmitLoc = 0;

	public static void emitRM(String opCode, int r1, int offset, int r2, String comment) {
		System.out.println(emitLoc + ": " + opCode + " " + r1 + ", " + offset + "(" + r2 + ") \t" + comment);
    emitLoc++;
    highEmitLoc++;

		if (highEmitLoc < emitLoc)
			highEmitLoc = emitLoc;
	}

	public static void emitRO(String opCode, int r1, int r2, int r3, String comment){
		System.out.println(emitLoc + ": " + opCode + " " + r1 + ", " + r2 + ", " + r3 + " \t" + comment);
    emitLoc++;
    highEmitLoc++;

		if (highEmitLoc < emitLoc)
			highEmitLoc = emitLoc;
	}

	public static void emitRM_Abs(String opCode, int r, int a, String comment){
		System.out.println(emitLoc + ": " + opCode + " " + r + ", " + (a - (emitLoc + 1)) + "(" + pc + ") \t" + comment);
    emitLoc++;
    highEmitLoc++;

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

  public static void prelude(String fileNameTM)
  {
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

  public static void finale(PrintStream console)
  {
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
  public void delete( int level ) 
  {
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

  public void visit( AssignExp exp, int level ){
    emitComment("-> op");
    int offset;
    level++;

    exp.lhs.accept( this, level );

    if (exp.lhs instanceof SimpleVar)
    {
      SimpleVar temp = (SimpleVar)exp.lhs;
    	emitComment("-> id");
      emitComment("looking up id: " + temp.name);

      definitions = symTable.get(temp.name);
      Defined tempD = definitions.get(0);
      offset = tempD.getOffSet();
  		emitRM("LDA", 0, offset, fp, "load id address");
  		emitComment("<- id");
  		emitRM("ST", ac, offset, fp, "op: Push left");
    }
    else if (exp.lhs instanceof IndexVar)
    {
		  IndexVar temp = (IndexVar)exp.lhs;
    	emitComment("-> id");
  		emitComment("looking up id: " + temp.name);
  		emitRM("LDA", 0, globalOffset, fp, "load id address");
  		emitComment("<- id");
  		emitRM("ST", ac, globalOffset, fp, "op: Push left");
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
        definitions = symTable.get(temp.name);
        Defined tempD = definitions.get(0);
        offset = tempD.getOffSet();
  			emitRM("LD", 0, offset, fp, "load id value");
  			emitComment("<- id");
    	}
    	else if (rhs instanceof IndexVar){
    		IndexVar temp = (IndexVar)rhs;
    		emitComment("-> id");
  			emitComment("looking up id: " + temp.name);
  			emitRM("LD", 0, globalOffset, fp, "load id value");
  			emitComment("<- id");
    	}
    }
    else if (exp.rhs instanceof IntExp) {
    	exp.rhs.accept(this, level);
    }
    else if (exp.rhs instanceof OpExp)
    {
      OpExp tempOp = (OpExp)exp.rhs;
      procOp (tempOp, tempOp.op, level);
    }
   

    emitRM("LD", ac, 0, fp, "op: load left");
    emitRM("ST", ac, 0, fp, "assign: Store value");
    emitComment("<- op");
  }

  public void visit( IfExp exp, int level ) {
    emitComment("-> if");
    level++;
    exp.test.accept( this, level );
    int savedLoc = emitSkip(1);
    exp.thenpart.accept( this, level );
    int savedLoc2 = emitSkip(0);
    emitBackup(savedLoc);
    emitRM_Abs("JEQ", 0, savedLoc2, "if: jump to else part");
    emitRestore();
    if (exp.elsepart != null )
    {
      emitComment("if: jump to else belongs here");
       exp.elsepart.accept( this, level );
    }
    emitComment("<- if");

    delete(level);
    level--;
  }

  public void visit( IntExp exp, int level ) {
    emitComment("-> constant");
    try{
      emitRM("LDC", ac, Integer.parseInt(exp.value), 0, "load const");
    }catch (Exception e)
    {

    }
    emitComment("<- constant");
  }

  public void visit( OpExp exp, int level ) {
    emitComment("-> op");
    procOp(exp, exp.op, level);
    
    level++;
    exp.left.accept( this, level );
    exp.right.accept( this, level );
    emitComment("<- op");
  }

  public void procOp (OpExp ex, int op, int level)
  {
    OpExp tempOp;
    VarExp tempV;
    Var tempVar;
    int offset;

    if (ex.left instanceof OpExp)
    {
      tempOp = (OpExp)ex.left;
      procOp(tempOp, tempOp.op, level);
    }
    else if (ex.left instanceof VarExp)
    {
      tempV = (VarExp)ex.left;
      tempVar = (Var)tempV.variable;

      if (tempVar instanceof SimpleVar)
      {
        SimpleVar temp = (SimpleVar)tempVar;
        emitComment("-> id");
        emitComment("looking up id: " + temp.name);
        definitions = symTable.get(temp.name);
        Defined tempD = definitions.get(0);
        offset = tempD.getOffSet();
  			emitRM("LD", 0, offset, fp, "load id value");
        emitComment("<- id");
        emitRM("ST", ac, 0, fp, "op: push left");
      }
      else if (tempVar instanceof IndexVar)
      {
        emitRM("ST", ac, 0, fp, "op: push left");
      }
    }
    else if (ex.left instanceof IntExp)
    {
        ex.left.accept(this, level);
        emitRM("ST", ac, 0, fp, "op: push left");
    }
    else if(ex.left instanceof CallExp)
    {
      emitRM("ST", ac, 0, fp, "op: push left");
    }

    if (ex.right instanceof OpExp)
    {
      tempOp = (OpExp)ex.right;
      procOp(tempOp, tempOp.op, level);
    }
    else if (ex.right instanceof VarExp)
    {
        tempV = (VarExp)ex.left;
        tempVar = (Var)tempV.variable;

        if (tempVar instanceof SimpleVar)
        {
          SimpleVar temp = (SimpleVar)tempVar;
          emitComment("-> id");
          emitComment("looking up id: " + temp.name);
          definitions = symTable.get(temp.name);
          Defined tempD = definitions.get(0);
          offset = tempD.getOffSet();
          emitRM("LD", 0, offset, fp, "load id value");
          emitComment("<- id");
        }
        else if (tempVar instanceof IndexVar)
        {

        }
    }
    else if (ex.right instanceof IntExp)
    {
      ex.right.accept(this, level);
      emitRM("LD", ac, 0, fp, "op: load left");
    }
    else if(ex.right instanceof CallExp)
    {
      emitRM("LD", ac, 0, fp, "op: load left");
    }

    switch(op)
    {
      case OpExp.PLUS:
        emitRO("ADD", ac, 1, ac, "ADD OP");
        break;
      case OpExp.MINUS:
        emitRO("SUB", ac, 1, ac, "SUB OP");
        break;
      case OpExp.MUL:
        emitRO("MUL", ac, 1, ac, "MUL OP");
        break;  
      case OpExp.DIV:
        emitRO("DIV", ac, 1, ac, "DIV OP");
        break;
      case OpExp.EQ:
        emitRO("SUB", ac, 1, ac, "EQUAL OP");
        emitRM("JEQ", ac, 2, pc, "");
        emitRM("LDC", ac, 0, 0, "FALSE CASE");
        emitRM("LDA", pc, 1, pc, "UNCONDITIONAL JUMP");
        emitRM("LDC", ac, 1, 0, "TRUE CASE");
        break;
      case OpExp.NE:
        emitRO("SUB", ac, 1, ac, "OP <");
        emitRM("JNE", ac, 2, pc, "");
        emitRM("LDC", ac, 0, 0, "FALSE CASE");
        emitRM("LDA", pc, 1, pc, "UNCONDITIONAL JUMP");
        emitRM("LDC", ac, 1, 0, "TRUE CASE");
        break;
      case OpExp.LT:
        emitRO("SUB", ac, 1, ac, "OP <");
        emitRM("JLT", ac, 2, pc, "");
        emitRM("LDC", ac, 0, 0, "FALSE CASE");
        emitRM("LDA", pc, 1, pc, "UNCONDITIONAL JUMP");
        emitRM("LDC", ac, 1, 0, "TRUE CASE");
        break;
      case OpExp.LE:
        emitRO("SUB", ac, 1, ac, "OP <");
        emitRM("JLE", ac, 2, pc, "");
        emitRM("LDC", ac, 0, 0, "FALSE CASE");
        emitRM("LDA", pc, 1, pc, "UNCONDITIONAL JUMP");
        emitRM("LDC", ac, 1, 0, "TRUE CASE");
        break;
      case OpExp.GT:
        emitRO("SUB", ac, 1, ac, "OP <");
        emitRM("JGT", ac, 2, pc, "");
        emitRM("LDC", ac, 0, 0, "FALSE CASE");
        emitRM("LDA", pc, 1, pc, "UNCONDITIONAL JUMP");
        emitRM("LDC", ac, 1, 0, "TRUE CASE");
        break;
      case OpExp.GE:
        emitRO("SUB", ac, 1, ac, "OP <");
        emitRM("JLE", ac, 2, pc, "");
        emitRM("LDC", ac, 0, 0, "FALSE CASE");
        emitRM("LDA", pc, 1, pc, "UNCONDITIONAL JUMP");
        emitRM("LDC", ac, 1, 0, "TRUE CASE");
        break;
    }
    return;
  }

  public void visit( VarExp exp, int level ) {
    level++;

    if (exp.variable != null)
      exp.variable.accept(this, level);
  }

//still need to finish
//ArrayDec
public void visit(ArrayDec exp, int level ) {
  ArrayDec tempA;
  Defined tempDef;
  if (level == 0)
  {
    emitComment("allocating global var: " + exp.name);
    emitComment("<- vardec");
    if (symTable.get(exp.name) != null)
    {
      definitions = symTable.get(exp.name);
      Defined temp = definitions.get(0);
      tempDef = new Defined(exp, level);
      tempA = (ArrayDec)temp.declaration;
      try{
      tempDef.setOffSet(globalOffset - Integer.parseInt(tempA.size.value));
      }catch(Exception e)
      {

      }
      definitions.add(0, tempDef);
      symTable.put(exp.name, definitions);
    }
    else
    {
        definitions = new ArrayList<Defined>();
        tempDef = new Defined(exp, level);
        tempA = (ArrayDec)exp;
        try{
        tempDef.setOffSet(globalOffset - Integer.parseInt(tempA.size.value));
        }catch(Exception e)
        {

        }
        definitions.add(0, tempDef);
        symTable.put(exp.name, definitions);
    }
  
    globalOffset = globalOffset - Integer.parseInt(tempA.size.value);
  }
  else if (inComp == true)
  {
    emitComment("Processing local var: " + exp.name);
    if (symTable.get(exp.name) != null)
    {
      definitions = symTable.get(exp.name);
      Defined temp = definitions.get(0);
      definitions = symTable.get(exp.name);
      tempDef = new Defined(exp, level);
      tempA = (ArrayDec)temp.declaration;
      try{
      tempDef.setOffSet(frameOffset - Integer.parseInt(tempA.size.value));
      }catch(Exception e)
      {

      }
      definitions.add(0, tempDef);
      symTable.put(exp.name, definitions);
    }
    else
    {
        definitions = new ArrayList<Defined>();
        tempDef = new Defined(exp, level);
        tempA = (ArrayDec)exp;
        try{
        tempDef.setOffSet(frameOffset - Integer.parseInt(tempA.size.value));
        }catch(Exception e)
        {
  
        }
        definitions.add(0, tempDef);
        symTable.put(exp.name, definitions);
    }
  
    frameOffset = frameOffset - Integer.parseInt(tempA.size.value);
  }
  else
  {
    if (symTable.get(exp.name) != null)
    {
      definitions = symTable.get(exp.name);
      Defined temp = definitions.get(0);
      definitions = symTable.get(exp.name);
      tempDef = new Defined(exp, level);
      tempA = (ArrayDec)temp.declaration;
      try{
      tempDef.setOffSet(frameOffset - Integer.parseInt(tempA.size.value));
      }catch(Exception e)
      {

      }
      definitions.add(0, tempDef);
      symTable.put(exp.name, definitions);
    }
    else
    {
        definitions = new ArrayList<Defined>();
        tempDef = new Defined(exp, level);
        tempA = (ArrayDec)exp;
        try{
        tempDef.setOffSet(frameOffset - Integer.parseInt(tempA.size.value));
        }catch(Exception e)
        {
  
        }
        definitions.add(0, tempDef);
        symTable.put(exp.name, definitions);
    }
  }
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
public void visit(CompoundExp exp, int level ) 
{
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
public void visit(FunctionDec exp, int level ) 
{
  if (exp.func.equals("main"))
    entry = emitLoc;

  frameOffset = 0;
  emitComment("Processing function: " + exp.func);
  emitComment("jump around function body here");

  frameOffset--;
  emitRM("ST", 0, frameOffset, fp, "store return");
  frameOffset--;
  
  int savedLoc = emitSkip(1);

  if (symTable.get(exp.func) != null)
  {
    definitions = symTable.get(exp.func);
    Defined temp = definitions.get(0);
    definitions = symTable.get(exp.func);
    Defined tempDef = new Defined(exp, level);
    tempDef.setOffSet(emitLoc);
    definitions.add(0, tempDef);
    symTable.put(exp.func, definitions);
  }
  else
  {
      definitions = new ArrayList<Defined>();
      Defined tempDef = new Defined(exp, level);
      tempDef.setOffSet(emitLoc);
      definitions.add(0, tempDef);
      symTable.put(exp.func, definitions);
  }

  level++;
  if (exp.params != null)
    exp.params.accept(this, level);

  if (exp.body != null)
    exp.body.accept(this, level);

  emitRM("LD", pc, -1, fp, "return to caller");
  emitBackup(savedLoc);
  emitRM_Abs("LDA", pc, highEmitLoc, "Jump around function body");
  emitComment("<- fundecl");
  emitRestore();

  delete(level);
  level--;
}

//IndexVar
public void visit(IndexVar exp, int level ) 
{
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
public void visit(ReturnExp exp, int level ) 
{
  //System.out.println("ReturnExp: ");
  level++;
  emitComment("-> return");
  if (exp.exp != null)
    exp.exp.accept(this, level);
  emitComment("<- return");
}

//SimpleDec
public void visit(SimpleDec exp, int level ) 
{
  if (level == 0)
  {
    emitComment("allocating global var: " + exp.name);
    emitComment("<- vardec");
    if (symTable.get(exp.name) != null)
    {
      definitions = symTable.get(exp.name);
      Defined temp = definitions.get(0);
      definitions = symTable.get(exp.name);
      Defined tempDef = new Defined(exp, level);
      tempDef.setOffSet(globalOffset);
      definitions.add(0, tempDef);
      symTable.put(exp.name, definitions);
    }
    else
    {
        definitions = new ArrayList<Defined>();
        Defined tempDef = new Defined(exp, level);
        tempDef.setOffSet(globalOffset);
        definitions.add(0, tempDef);
        symTable.put(exp.name, definitions);
    }
  
    globalOffset--;
  }
  else if (inComp == true)
  {
    emitComment("Processing local var: " + exp.name);
    if (symTable.get(exp.name) != null)
    {
      definitions = symTable.get(exp.name);
      Defined temp = definitions.get(0);
      definitions = symTable.get(exp.name);
      Defined tempDef = new Defined(exp, level);
      tempDef.setOffSet(frameOffset);
      definitions.add(0, tempDef);
      symTable.put(exp.name, definitions);
    }
    else
    {
        definitions = new ArrayList<Defined>();
        Defined tempDef = new Defined(exp, level);
        tempDef.setOffSet(frameOffset);
        definitions.add(0, tempDef);
        symTable.put(exp.name, definitions);
    }
  
    frameOffset--;
  }
  else
  {
    if (symTable.get(exp.name) != null)
    {
      definitions = symTable.get(exp.name);
      Defined temp = definitions.get(0);
      definitions = symTable.get(exp.name);
      Defined tempDef = new Defined(exp, level);
      tempDef.setOffSet(frameOffset);
      definitions.add(0, tempDef);
      symTable.put(exp.name, definitions);
    }
    else
    {
        definitions = new ArrayList<Defined>();
        Defined tempDef = new Defined(exp, level);
        tempDef.setOffSet(frameOffset);
        definitions.add(0, tempDef);
        symTable.put(exp.name, definitions);
    }
  }
}

//SimpleVar
public void visit(SimpleVar exp, int level ) {
  //System.out.println("SimpleVar: " + exp.name);
}

//WhileExp
public void visit(WhileExp exp, int level ) 
{
  //System.out.println("WhileExp: ");
  emitComment("-> while");
  emitComment("while: jump after body comes back here");
  int savedLoc = emitSkip(0);
  level++;
  if (exp.test != null){
  	exp.test.accept(this, level);
  	emitComment("while: jump to end belongs here");
  }
  int savedLoc2 = emitSkip(1);
  if (exp.body != null)
    exp.body.accept(this, level);
  emitRM_Abs("LDA", pc, savedLoc, "while: absolute jump to test");
  int savedLoc3 = emitSkip(0);
  emitBackup(savedLoc2);
  emitRM_Abs("JEQ", 0, savedLoc3, "while: jump to end");
  emitRestore();
  emitComment("<- while");
  
  delete(level);
  level--;
}

public void visit (NameTy exp, int level)
{

}

}