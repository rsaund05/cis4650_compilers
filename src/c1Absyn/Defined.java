package c1Absyn;

public class Defined 
{
    public Dec declaration; 
    public int level;

    public Defined (Dec declaration, int level)
    {
        this.declaration = declaration;
        this.level = level;
    }
}