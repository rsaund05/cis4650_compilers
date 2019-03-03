package c1Absyn;

public class VarExp extends Exp {
  public String variable;
  
  public VarExp(int row, int col, Var variable ) {
    this.row = row;
    this.col = col;
    this.variable = variable;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}