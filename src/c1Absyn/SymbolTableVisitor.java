import c1Absyn.*;

public class SymbolTableVisitor implements AbsynVisitor {

  final static int SPACES = 4;
  Arraylist<Defined> definitions = new ArrayList<Defined>();
  HashMap <String> symTable = new HashMap<>();

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
    exp.lhs.accept( this, level );
    exp.rhs.accept( this, level );
  }

  public void visit( IfExp exp, int level ) {
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    if (exp.elsepart != null )
       exp.elsepart.accept( this, level );
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
  String ty = new String("");

  if (exp.typ.typ == NameTy.VOID)
      ty = new String("VOID");
  else if (exp.typ.typ == NameTy.INT)
      ty = new String("INT");

  if (exp.size != null)
  {
    //if (!exp.size.value.equals(null))
  }
  //else

}

//CallExp
public void visit(CallExp exp, int level ) {

  if (exp.args != null)
    exp.args.accept(this, level);
}

//CompoundExp
public void visit(CompoundExp exp, int level ) {
  indent(level);
  System.out.println("Entering a new block:");
  level++;
  if (exp.decs != null)
    exp.decs.accept(this, level);
  if (exp.exp != null)
    exp.exp.accept(this, level);

 level--;
 indent(level);
 System.out.println("Leaving a new block");
}

//FunctionDec
public void visit(FunctionDec exp, int level ) {
  if (exp.result.typ == NameTy.VOID)
  else if (exp.result.typ == NameTy.INT)

  level++;
  indent(level);
  System.out.println("Entering the scope of function " + exp.func + ":");
 

  if (exp.params != null)
    exp.params.accept(this, level);

  if (exp.body != null)
    exp.body.accept(this, level);

  level--;
  indent(level);
  System.out.println("Leaving the function scope");
}

//IndexVar
public void visit(IndexVar exp, int level ) {
  exp.index.accept(this, level);
}

//NilExp
public void visit(NilExp exp, int level ) {
}

//ReturnExp
public void visit(ReturnExp exp, int level ) {
  if (exp.exp != null)
    exp.exp.accept(this, level);
}

//SimpleDec
public void visit(SimpleDec exp, int level ) {
 // if (exp.typ.typ == NameTy.VOID)
  //else if (exp.typ.typ == NameTy.INT)
}

//SimpleVar
public void visit(SimpleVar exp, int level ) {
}

//WhileExp
public void visit(WhileExp exp, int level ) {
  if (exp.test != null)
    exp.test.accept(this, level);
  if (exp.body != null)
    exp.body.accept(this, level);
}

public void visit (NameTy exp, int level)
{

}
}
