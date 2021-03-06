import c1Absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 4;

  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
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
    indent( level );
    System.out.println( "AssignExp:" );
    level++;
    exp.lhs.accept( this, level );
    exp.rhs.accept( this, level );
  }

  public void visit( IfExp exp, int level ) {
    indent( level );
    System.out.println( "IfExp:" );
    level++;
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    if (exp.elsepart != null )
       exp.elsepart.accept( this, level );
  }

  public void visit( IntExp exp, int level ) {
    indent( level );
    System.out.println( "IntExp: " + exp.value ); 
  }

  public void visit( OpExp exp, int level ) {
    indent( level );
    System.out.print( "OpExp:" ); 
    switch( exp.op ) {
      case OpExp.PLUS:
        System.out.println( " + " );
        break;
      case OpExp.MINUS:
        System.out.println( " - " );
        break;
      case OpExp.MUL:
        System.out.println( " * " );
        break;
      case OpExp.DIV:
        System.out.println( " / " );
        break;
      case OpExp.EQ:
        System.out.println( " == " );
        break;
      case OpExp.NE:
        System.out.println(" != ");
        break;
      case OpExp.LT:
        System.out.println( " < " );
        break;
      case OpExp.LE:
        System.out.println(" <= ");
        break;
      case OpExp.GT:
        System.out.println( " > " );
        break;
      case OpExp.GE:
        System.out.println(" >= ");
        break;
      default:
        System.out.println( "Unrecognized operator at line " + exp.row + " and column " + exp.col);
    }
    level++;
    exp.left.accept( this, level );
    exp.right.accept( this, level );
  }

  public void visit( VarExp exp, int level ) {
    indent( level );
    System.out.println( "VarExp: ");
    level++;

    if (exp.variable != null)
      exp.variable.accept(this, level);
  }

//still need to finish
//ArrayDec
public void visit(ArrayDec exp, int level ) {
  indent( level );
  String ty = new String("");

  if (exp.typ.typ == NameTy.VOID)
      ty = new String("VOID");
  else if (exp.typ.typ == NameTy.INT)
      ty = new String("INT");

  if (exp.size != null)
  {
    if (!exp.size.value.equals(null))
      System.out.println("ArrayDec: " + exp.name + "[" + exp.size.value + "]" + " - " + ty);
  }
  else
    System.out.println("ArrayDec: " + exp.name + "[]" + " - " + ty);

}

//CallExp
public void visit(CallExp exp, int level ) {
  indent( level );
  System.out.println("CallExp: " + exp.func);
  level++;
  if (exp.args != null)
    exp.args.accept(this, level);
}

//CompoundExp
public void visit(CompoundExp exp, int level ) {
  indent( level );
  System.out.println("CompoundExp: ");
  
  if (exp.decs != null && exp.exp != null)
    level++;

  if (exp.decs != null)
    exp.decs.accept(this, level);
  if (exp.exp != null)
    exp.exp.accept(this, level);
}

//FunctionDec
public void visit(FunctionDec exp, int level ) {
  indent( level );
  if (exp.result.typ == NameTy.VOID)
    System.out.println("FunctionDec: " + exp.func + " - VOID");
  else if (exp.result.typ == NameTy.INT)
  System.out.println("FunctionDec: " + exp.func + " - INT"); 

  level++;

  if (exp.params != null)
    exp.params.accept(this, level);

  if (exp.body != null)
    exp.body.accept(this, level);
}

//IndexVar
public void visit(IndexVar exp, int level ) {
  indent( level );
  System.out.println("IndexVar: " + exp.name);
  level++;
  exp.index.accept(this, level);
}

//NilExp
public void visit(NilExp exp, int level ) {
  indent( level );
  System.out.println("NilExp:");
}

//ReturnExp
public void visit(ReturnExp exp, int level ) {
  indent( level );
  System.out.println("ReturnExp: ");
  level++;

  if (exp.exp != null)
    exp.exp.accept(this, level);
}

//SimpleDec
public void visit(SimpleDec exp, int level ) {
  indent( level );
  if (exp.typ.typ == NameTy.VOID)
    System.out.println("SimpleDec: " + exp.name + " - VOID"); 
  else if (exp.typ.typ == NameTy.INT)
    System.out.println("SimpleDec: " + exp.name + " - INT");
}

//SimpleVar
public void visit(SimpleVar exp, int level ) {
  indent( level );
  System.out.println("SimpleVar: " + exp.name);
}

//WhileExp
public void visit(WhileExp exp, int level ) {
  indent( level );
  System.out.println("WhileExp: ");
  level++;
  if (exp.test != null)
    exp.test.accept(this, level);
  if (exp.body != null)
    exp.body.accept(this, level);
}

public void visit (NameTy exp, int level)
{

}
}
