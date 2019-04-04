package c1Absyn;

public class FunctionDec extends Dec
{
    public NameTy result;
    public String func;
    public VarDecList params;
    public CompoundExp body;
    public int offset = -1;
    public int funcAddr;

    public FunctionDec (int row, int col, NameTy result, String func, VarDecList params, CompoundExp body)
    {
        this.row = row;
        this.col = col;
        this.result = result;
        this.func = func;
        this.params = params;
        this.body = body;
    }
    
    public void setOffset (int offset)
    {
        this.offset = offset;
    }
    
    public void setFuncAddr(int addr)
    {
        this.funcAddr = addr;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}