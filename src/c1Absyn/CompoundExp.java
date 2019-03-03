package c1Absyn;

public class CompoundExp extends Exp
{
    public VarDecList decs;
    public ExpList exp;

    public CompoundExp (int row, int col, VarDecList decs, ExpList exp)
    {
        this.row = row;
        this.col = col;
        this.decs = decs;
        this.exp = exp;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}