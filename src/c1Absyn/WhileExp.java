package c1Absyn;

public class WhileExp extends Exp
{
    public int pos;
    public Exp test;
    public Exp body;

    public WhileExp (int pos, Exp test, Exp body)
    {
        this.pos = pos;
        this.test = test;
        this.body = body;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}