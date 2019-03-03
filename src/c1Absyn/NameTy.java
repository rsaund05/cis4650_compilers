package c1Absyn;

public class NameTy extends Absyn
{
    public int typ;
    final static int INT = 0;
    final static int VOID = 1;

    public NameTy (int row, int col, int typ)
    {
        this.row = row;
        this.col = col;
        this.typ = typ;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}