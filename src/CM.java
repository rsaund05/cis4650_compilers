/*
  Created by: Robert Saunders, Curtis Collins
  File Name: Main.java
  To Build: 
  After the scanner, tiny.flex, and the parser, tiny.cup, have been created.
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.tiny

  where gcd.tiny is an test input file for the tiny language.
*/
   
import java.io.*;
import c1Absyn.*;
import java.lang.*;
import java.io.*;
   
class Main {
  public static boolean SHOW_TREE = false;
  public static boolean SHOW_SCOPE = false;
  static public void main(String argv[]) throws Exception{    
    /* Start the parser */

    //Checking for valid file given in command line
    File test = new File(argv[0]);
    if(!(test.exists() && !test.isDirectory())){
      System.out.println("Error: Invalid file given. Exiting.");
      System.exit(0);
    }
    FileOutputStream f = new FileOutputStream("./output.txt");
    
    //Check for output flags 
    for(int i = 0; i < argv.length; i++){
      if(argv[i].equals("-a")) SHOW_TREE = true;
      if(argv[i].equals("-s")) SHOW_SCOPE = true;
    } 

    //Begin parsing
    try {
     parser p = new parser(new Lexer(new FileReader(argv[0])));
     Absyn result = (Absyn)(p.parse().value);      
       if (SHOW_TREE) { //Print out Abstract Syntax Tree
         System.out.println("The abstract syntax tree is:");
         ShowTreeVisitor visitor = new ShowTreeVisitor();
         result.accept(visitor, 0); 
       }
       
      
      SymbolTableVisitor visitor = new SymbolTableVisitor();
      if(SHOW_SCOPE == true){
        visitor.SHOW_SCOPE = true; 
        
        System.setOut(new PrintStream(f));
        System.out.println("Entering the Global Scope:");
      } 
      result.accept(visitor, 0);
      visitor.print(0);
      visitor.delete(0);
      if(SHOW_SCOPE == true) {
        System.out.println("Leaving global scope"); 
        if(f != null) f.close();
      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
      }
  }
}


