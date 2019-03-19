package c1Absyn;
import java.util.*;

public class SymbolTableVisitor implements AbsynVisitor {
  public static boolean SHOW_SCOPE = false;

  final static int SPACES = 4;
   ArrayList<Defined> definitions = new ArrayList<Defined>();
   HashMap <String, ArrayList<Defined>> symTable = new HashMap<>();

  private void indent( int level ) {
    if(SHOW_SCOPE == true) for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
  }

public void SysPrint(String toPrint){
  if(SHOW_SCOPE == true){
    System.out.println(toPrint);
  }
}
//Print and delete functions
//*****************************************************************************************************
public  void print( int level ) {

  for (String key: symTable.keySet())
       {
           definitions = symTable.get(key);
           Dec temp = definitions.get(0).declaration;
           if (definitions.get(0).level == level)
           {
              indent(level);
              if (temp instanceof SimpleDec)
              {
                String type = new String();
                SimpleDec tempS = (SimpleDec)temp;

                if (tempS.typ.typ == NameTy.INT)
                  type = "INT";
                else if (tempS.typ.typ == NameTy.VOID)
                  type = "VOID";

                SysPrint(tempS.name + ": " + type);
              }
              else if (temp instanceof ArrayDec)
              {
                String type = new String();
                ArrayDec tempS = (ArrayDec)temp;
                if (tempS.typ.typ == NameTy.VOID)
                  type = "VOID[]";
                else
                  type = "INT[]";
                SysPrint(tempS.name + ": " + type);
              }
              else if (temp instanceof FunctionDec)
              {
                FunctionDec tempS = (FunctionDec)temp;
                String funcString = tempS.func + ": (";
                VarDecList tempList= tempS.params;

                while(tempList.tail != null) {
                  if(tempList.head instanceof ArrayDec) {
                    ArrayDec tempDec = (ArrayDec)tempList.head;
                  
                  if(tempDec.typ.typ == NameTy.VOID) funcString+= "VOID[]";
                  else if(tempDec.typ.typ == NameTy.INT) funcString+= "INT[]";
                  
                  if(tempList.tail != null) funcString += ", ";
                } 
                else
                {
                  SimpleDec tempDec = (SimpleDec)tempList.head;
                  
                  if(tempDec.typ.typ == NameTy.VOID) funcString+= "VOID";
                  else if(tempDec.typ.typ == NameTy.INT) funcString+= "INT";
                  
                  if(tempList.tail != null) funcString += ", ";
                }
                
                tempList = tempList.tail;
              } 

              if (tempList.head != null)
              {
                if (tempList.head instanceof SimpleDec)
                {
                  SimpleDec tempDec = (SimpleDec)tempList.head;
                  
                  if(tempDec.typ.typ == NameTy.VOID) funcString+= "VOID";
                  else if(tempDec.typ.typ == NameTy.INT) funcString+= "INT";
                }
                else
                {
                  ArrayDec tempDec = (ArrayDec)tempList.head;
                  
                  if(tempDec.typ.typ == NameTy.VOID) funcString+= "VOID[]";
                  else if(tempDec.typ.typ == NameTy.INT) funcString+= "INT[]";
                }
              }

                funcString += ") -> ";
                if(tempS.result.typ == NameTy.VOID) funcString += "VOID";
                else funcString += "INT";
                SysPrint(funcString); 
              }
           }
       }
}

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
        tempSDec = (SimpleDec)definitions.get(0).declaration;
        toReturn = tempSDec.typ.typ;
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
//*****************************************************************************************************


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
    exp.lhs.accept( this, level );
    exp.rhs.accept( this, level );

    int type1 = -1;
    int type2 = -1;

    if (exp.lhs instanceof SimpleVar)
    {
      SimpleVar tempS = (SimpleVar)exp.lhs;
      SimpleDec tempSDec;
      if (symTable.get(tempS.name) != null)
      {
        definitions = symTable.get(tempS.name);
        tempSDec = (SimpleDec)definitions.get(0).declaration;
        type1 = tempSDec.typ.typ;
      }
    }
    else{
      IndexVar tempI = (IndexVar)exp.lhs;
      ArrayDec tempADec;
      if (symTable.get(tempI.name) != null)
      {
        definitions = symTable.get(tempI.name);
        tempADec = (ArrayDec)definitions.get(0).declaration;
        type1 = tempADec.typ.typ;

      }
    }

    if (exp.rhs instanceof OpExp)
    {
      OpExp tempOP = (OpExp)exp.rhs;
      type2 = checkOPExp(tempOP.left, tempOP.right);
    }
    else
    {
      Exp tempE = (Exp)exp.rhs;
      type2 = getType(tempE);
    }

    if (type1 != type2 && type2 != -1 && type2 != -2)
      System.err.println("Error: trying to assign " + type2  + " to variable of type " + type1);
    
  }

  public void visit( IfExp exp, int level ) {
    indent(level);
    SysPrint("Entering a new block:");
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    if (exp.elsepart != null )
       exp.elsepart.accept( this, level );

       print(level);
       delete(level);

    indent(level);
    level--;
    SysPrint("Leaving a new block");
  }

  public void visit( IntExp exp, int level ) { 
  }

  public int checkOPExp(Exp left, Exp right)
  {
      int type1 = 100;
      int type2 = 100;
      SimpleVar sTemp;
      OpExp temp;
      VarExp tempV;
      Var tempVar;

      if (left instanceof OpExp)
      {
        temp = (OpExp)left;
        type1 = checkOPExp(temp.left, temp.right);
      }
      else
      {
        type1 = getType(left);
      }

      if (right instanceof OpExp)
      {
        temp = (OpExp)right;
        type2 = checkOPExp(temp.left, temp.right);
      }
      else
      {
        type2 = getType(right);
      }

      if (type1 == -1 || type2 == -1)
        return -2;
        
      if (type1 != type2)
      {
        return -1;
      }

      return type1;
  }

  public void visit( OpExp exp, int level ) {
    if (checkOPExp(exp.left, exp.right) == -1)
      System.err.println("Error: Line: " + exp.row + " Colums: " + exp.col +  " mismatched types ");
    exp.right.accept( this, level );
  }

  public void visit( VarExp exp, int level ) {
    if (exp.variable instanceof SimpleVar)
    {
      SimpleVar temp = (SimpleVar) exp.variable;
        if (symTable.get(temp.name) == null)
          System.err.println("Error: variable " + temp.name + " undefined");
    }
    else
    {
      IndexVar temp = (IndexVar) exp.variable;
      if (symTable.get(temp.name) == null)
          System.err.println("Error: variable " + temp.name + " undefined");
    }

    if (exp.variable != null)
      exp.variable.accept(this, level);
  }

//still need to finish
//ArrayDec
public void visit(ArrayDec exp, int level ) {
  if (symTable.get(exp.name) != null)
  {
    definitions = symTable.get(exp.name);
    Defined temp = definitions.get(0);

    if (temp.level == level)
    {
      System.err.println("Error: variable " + exp.name + " has already been declared");
    }
    else
    {
        definitions = symTable.get(exp.name);
        definitions.add(0, new Defined(exp, level));
        symTable.put(exp.name, definitions);
    }
  }
  else
  {
      definitions = new ArrayList<Defined>();
      definitions.add(0, new Defined(exp, level));
      symTable.put(exp.name, definitions);

  }
}

//CallExp
public void visit(CallExp exp, int level ) {
  int type1 = -1;
  int type2 = -1;
  if (symTable.get(exp.func) == null)
    System.err.println("Error: undefined function " + exp.func);
  else
  {
    definitions = symTable.get(exp.func);
    ExpList callList = exp.args;
    Dec tempDec = definitions.get(0).declaration;
    FunctionDec tempFunc = (FunctionDec)tempDec;
    VarDecList params = tempFunc.params;

    while(params != null)
    {
      if (params.head == null)
        break;

      if (callList == null)
      {
        System.err.println("Error: too few arguments");
        break;
      }
        type1 = getType(callList.head);

        if (params.head instanceof SimpleDec)
        {
          SimpleDec tempSDec = (SimpleDec)params.head;
          type2 = tempSDec.typ.typ;
        }

        if (type1 != type2)
        {
          System.err.println("Error: conflicting types: " + type1 + " : " + type2);
        }

        params = params.tail;
        callList = callList.tail;
    }

    if (params == null && callList != null)
      System.err.println("Error: too many arguments!");
    else if (params.head == null && callList != null)
      System.err.println("Error: too many arguments!");
  }

  if (exp.args != null)
    exp.args.accept(this, level);
}

//CompoundExp
public void visit(CompoundExp exp, int level ) {
  if (exp.decs != null)
    exp.decs.accept(this, level);

  level++;
  if (exp.exp != null)
    exp.exp.accept(this, level);
}

//FunctionDec
public void visit(FunctionDec exp, int level ) {
  if (symTable.get(exp.func) != null)
  {
        definitions = symTable.get(exp.func);
        Defined temp = definitions.get(0);

        if (temp.level == level)
        {
          System.err.println("Error: function " + exp.func + " has already been declared");
        }
        else
        {
          definitions = symTable.get(exp.func);
          definitions.add(0, new Defined(exp, level));
          symTable.put(exp.func, definitions);
        }
  }
  else
  {
      definitions = new ArrayList<Defined>();
      definitions.add(0, new Defined(exp, level));
      symTable.put(exp.func, definitions);

  }
  
  level++;
  indent(level);
  SysPrint("Entering the scope of function " + exp.func + ":");

  if (exp.params != null)
    exp.params.accept(this, level);

  if (exp.body != null)
    exp.body.accept(this, level);

    print(level);
    delete(level);

  indent(level);
  level--;
  SysPrint("Leaving the function scope");
}

//IndexVar
public void visit(IndexVar exp, int level ) {
  exp.index.accept(this, level);
}

//NilExp
public void visit(NilExp exp, int level ) {
  exp.accept(this, level);
}

//ReturnExp
public void visit(ReturnExp exp, int level ) {
  if (exp.exp != null)
    exp.exp.accept(this, level);
}

//SimpleDec
public void visit(SimpleDec exp, int level ) {
  if (symTable.get(exp.name) != null)
  {
    definitions = symTable.get(exp.name);
    Defined temp = definitions.get(0);

    if (temp.level == level)
    {
      System.err.println("Error: variable " + exp.name + " has already been declared");
    }
    else
    {
        definitions = symTable.get(exp.name);
        definitions.add(0, new Defined(exp, level));
        symTable.put(exp.name, definitions);
    }
  }
  else
  {
      definitions = new ArrayList<Defined>();
      definitions.add(0, new Defined(exp, level));
      symTable.put(exp.name, definitions);

  }
}

//SimpleVar
public void visit(SimpleVar exp, int level ) {
}

//WhileExp
public void visit(WhileExp exp, int level ) {
  indent(level);
  SysPrint("Entering a while block");
   
  if (exp.test != null)
    exp.test.accept(this, level);
  if (exp.body != null)
    exp.body.accept(this, level);

    print(level);
    delete(level);

  indent(level);
  level--;
  SysPrint("Leaving a while block");
}

public void visit (NameTy exp, int level)
{

}
}
