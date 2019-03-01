package c1Absyn;

public class NameTy extends Absyn
{
    public int pos;
    public int typ;
    final static int INT = 0;
    final static int VOID = 1;

    public NameTY (int pos, int typ)
    {
        this.pos = pos;
        this.typ = typ;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}