package c1Absyn;

public class SimpleDec extends VarDec
{
    public int pos;
    public NameTy typ;
    public String name;

    public SimpleDec(int pos, NameTy typ, String name)
    {
        this.pos = pos;
        this.typ = pos;
        this.name = name;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
