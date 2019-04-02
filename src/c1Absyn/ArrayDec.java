package c1Absyn;

public class ArrayDec extends VarDec
{
    public NameTy typ;
    public String name;
    public IntExp size;
    public int offset = -1;

    public ArrayDec(int row, int col, NameTy typ, String name, IntExp size)
    {
        this.row = row;
        this.col = col;
        this.typ = typ;
        this.name = name;
        this.size = size;
    }

    public setOffset (int offset)
    {
        this.offset = offset;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}