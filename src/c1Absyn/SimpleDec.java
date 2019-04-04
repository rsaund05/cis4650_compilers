package c1Absyn;

public class SimpleDec extends VarDec
{
    public NameTy typ;
    public String name;
    public int offset;

    public SimpleDec(int row, int col, NameTy typ, String name)
    {
        this.row = row;
        this.col = col;
        this.typ = typ;
        this.name = name;
        this.offset = -1;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
