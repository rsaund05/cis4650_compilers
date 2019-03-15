package c1Absyn;
import java.util.*;

public class SymbolTableVisitor implements AbsynVisitor {

  final static int SPACES = 4;
   ArrayList<Defined> definitions = new ArrayList<Defined>();
   HashMap <String, ArrayList<Defined>> symTable = new HashMap<>();

  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
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
                if (tempS.type.typ == NameTy.INT)
                  type = "INT";
                else if (tempS.type.typ == NameTy.VOID)
                  type = "VOID";
                  
                System.out.println(tempS.name + ": " + type);
              }
              else if (temp instanceof ArrayDec)
              {
                ArrayDec tempS = (ArrayDec)temp;
                System.out.println(tempS.name);
              }
              else if (temp instanceof FunctionDec)
              {
                FunctionDec tempS = (FunctionDec)temp;
                System.out.println(tempS.func); 
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
    SimpleDec temp = (SimpleDec)definitions.get(0).declaration;
    definitions.remove(0);

    if (definitions.size() > 0)
      symTable.put(temp.name, definitions);
    else
      symTable.remove(temp.name);
    }

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
  }

  public void visit( IfExp exp, int level ) {
    indent(level);
    System.out.println("Entering a new block:");
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    if (exp.elsepart != null )
       exp.elsepart.accept( this, level );

       print(level);

       delete(level);

    indent(level);
    level--;
    System.out.println("Leaving a new block");
  }

  public void visit( IntExp exp, int level ) { 
  }

  public void visit( OpExp exp, int level ) {
    switch( exp.op ) {
      case OpExp.PLUS:
        break;
      case OpExp.MINUS:
        break;
      case OpExp.MUL:
        break;
      case OpExp.DIV:
        break;
      case OpExp.EQ:
        break;
      case OpExp.NE:
        break;
      case OpExp.LT:
        break;
      case OpExp.LE:
        break;
      case OpExp.GT:
        break;
      case OpExp.GE:
        break;
      default:
        break;
    }
    exp.left.accept( this, level );
    exp.right.accept( this, level );
  }

  public void visit( VarExp exp, int level ) {

    if (exp.variable != null)
      exp.variable.accept(this, level);
  }

//still need to finish
//ArrayDec
public void visit(ArrayDec exp, int level ) {
  if (symTable.get(exp.name) != null)
  {
        definitions = symTable.get(exp.name);
        definitions.add(0, new Defined(exp, level));
        symTable.put(exp.name, definitions);
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
        definitions.add(0, new Defined(exp, level));
        symTable.put(exp.func, definitions);
  }
  else
  {
      definitions = new ArrayList<Defined>();
      definitions.add(0, new Defined(exp, level));
      symTable.put(exp.func, definitions);

  }
  
  level++;
  indent(level);
  System.out.println("Entering the scope of function " + exp.func + ":");

  if (exp.params != null)
    exp.params.accept(this, level);

  if (exp.body != null)
    exp.body.accept(this, level);

    print(level);
    delete(level);

  indent(level);
  level--;
  System.out.println("Leaving the function scope");
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
        definitions.add(0, new Defined(exp, level));
        symTable.put(exp.name, definitions);
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
  level++;
  indent(level);
  System.out.println("Entering a new block");
   
  if (exp.test != null)
    exp.test.accept(this, level);
  if (exp.body != null)
    exp.body.accept(this, level);

    print(level);
    delete(level);

  indent(level);
  level--;
  System.out.println("Leaving a new block");
}

public void visit (NameTy exp, int level)
{

}
}
