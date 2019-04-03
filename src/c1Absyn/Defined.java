package c1Absyn;

public class Defined 
{
    public Dec declaration; 
    public int level;
    public int offSet;

    public Defined (Dec declaration, int level)
    {
        this.declaration = declaration;
        this.level = level;
        this.offSet = -1;
    }

    public void setOffSet (int toSet)
    {
        this.offSet = toSet;
    }

    public int getOffSet ()
    {
        return this.offSet;
    }
}