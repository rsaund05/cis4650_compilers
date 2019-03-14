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
   
class Main {
  public boolean SHOW_TREE = false;
  public boolean SHOW_SCOPE = false;
  static public void main(String argv[]) {    
    /* Start the parser */

    //Checking for valid file given in command line
    File test = new File(argv[0]);
    if(!(test.exists() && !test.isDirectory())){
      System.out.println("Error: Invalid file given. Exiting.");
      System.exit(0);
    }

    //Check for '-a' command, to know whether to print the AST 
    for(i = 0; i < argv.length; i++){
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
       if(SHOW_SCOPE) {
        System.out.println("Entering the Global Scope:");
        SymbolTableVisitor visitor = new SymbolTableVisitor();
        result.accept(visitor, 0);
        System.out.println("Leaving global scope"); 
       }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
      }
  }
}


