package c1Absyn;

public class CompoundExp extends Exp
{
    public int pos;
    public VarDecList decs;
    public ExpList exps;

    public CompoundExp (int pos, VarDecList decs, ExpList exp)
    {
        this.pos = pos;
        this.decs = decs;
        this.exp = exp;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}