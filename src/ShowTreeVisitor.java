import c1Absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 4;

  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
  }

  public void visit( ExpList expList, int level ) {
    while( expList != null ) {
      expList.head.accept( this, level );
      expList = expList.tail;
    } 
  }

  public void visit (VarDecList varDecList, int level)
  {
    while(varDecList != null)
    {
      varDecList.head.accept(this, level);
      varDecList = varDecList.tail;
    }
  }

  public void visit (DecList decList, int level)
  {
    while(decList != null)
    {
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
        System.out.println( " = " );
        break;
      case OpExp.NE:
        System.out.prinln(" != ");
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
    exp.variable.accept(this, level);
  }

//still need to finish
//ArrayDec
public void visit(ArrayDec exp, int level ) {
  indent( level );

  if (exp.size > 0)
    System.out.println("ArrayDec: " + exp.name + "[" + exp.size + "]" + " - " + exp.typ);
  else
    System.out.println("ArrayDec: " + exp.name + "[]" + " - " + exp.typ);
}

//CallExp
public void visit(CallExp exp, int level ) {
  indent( level );
  System.out.println("CallExp: " + exp.func);
  level++;
  exp.args.accept(this, level);
}

//CompoundExp
public void visit(CompoundExp exp, int level ) {
  indent( level );
  System.out.println("CompoundExp: ");
  level++;
  exp.decs.accept(this, level);
  exp.exp.accept(this, level);
}

//FunctionDec
public void visit(FunctionDec exp, int level ) {
  indent( level );
  System.out.println("FunctionDec: " + exp.func + " - " + exp.result);
  level++;
  exp.params.accept(this, level);
  exp.body.accept(this, level);
}

//IndexVar
public void visit(IndexVar exp, int level ) {
  indent( level );
  System.out.println("IndexVar: " + exp.name);
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
  exp.accept(this, level);
}

//SimpleDec
public void visit(SimpleDec exp, int level ) {
  indent( level );
  System.out.println("SimpleDec: " + exp.name + " - " + exp.typ);
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

//tiny stuff
 /* public void visit( ReadExp exp, int level ) {
    indent( level );
    System.out.println( "ReadExp:" );
    exp.input.accept( this, ++level );
  }

  public void visit( RepeatExp exp, int level ) {
    indent( level );
    System.out.println( "RepeatExp:" );
    level++;
    exp.exps.accept( this, level );
    exp.test.accept( this, level ); 
  }

  public void visit( WriteExp exp, int level ) {
    indent( level );
    System.out.println( "WriteExp:" );
    exp.output.accept( this, ++level );
  }*/

}
