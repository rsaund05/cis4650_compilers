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
   
import c1Absyn.*;
import java.lang.*;
import java.io.*;
import java.util.*;
   
class CM {
  public static boolean SHOW_TREE = false;
  public static boolean SHOW_SCOPE = false;
  public static boolean COMPILE = false;
  public static boolean PARSE_ERROR = false;
  public static boolean SEMANTIC_ERROR = false;
  public static String fileNameTM = "";
  static public void main(String argv[]) throws Exception{    
    /* Start the parser */

    //Checking for valid file given in command line
    File test = new File(argv[0]);
    PrintStream console = System.out; //Saving the outputstream 
    if(!(test.exists() && !test.isDirectory())){
      System.out.println("Error: Invalid file given. Exiting.");
      System.exit(0);
    }
    fileNameTM = argv[0].substring(0, argv[0].indexOf(".cm")) + ".tm";

    System.out.println("FILE: " + fileNameTM);
    

    FileOutputStream f = new FileOutputStream("./output.txt");
    
    //Check for output flags 
    for(int i = 0; i < argv.length; i++){
      if(argv[i].equals("-a")) SHOW_TREE = true;
      if(argv[i].equals("-s")) SHOW_SCOPE = true;
      if(argv[i].equals("-c")) COMPILE = true;
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

       
      if(PARSE_ERROR == true) {
        System.out.println("\n\nErrors are present in " + argv[0] + ", cannot compile");
        System.exit(0);
      }
      SymbolTableVisitor visitor = new SymbolTableVisitor();
      if(SHOW_SCOPE == true){
        visitor.SHOW_SCOPE = true; 
        
        System.setOut(new PrintStream(f));
        System.out.println("Entering the Global Scope:");
      } 
      //ArrayList<Defined> outputList = new ArrayList<Defined>();

      //Creating and adding the 'input' function to the hashmap
      //SimpleDec tempIn = new SimpleDec(0,0, new NameTy(0,0, NameTy.VOID), null);
      //VarDecList inputList = new VarDecList(tempIn, null);
      FunctionDec inputFunc = new FunctionDec(0,0, new NameTy(0,0, NameTy.INT), "input", null, null);
      Defined inputDef = new Defined(inputFunc, -1);
      ArrayList<Defined> inputAr = new ArrayList<Defined>();
      inputAr.add(inputDef);

      SimpleDec tempOut = new SimpleDec(0, 0, new NameTy(0, 0, NameTy.INT), "x");
      VarDecList outputList = new VarDecList(tempOut, null);
      FunctionDec outputFunc = new FunctionDec(0, 0, new NameTy(0, 0, NameTy.VOID), "output", outputList, null);
      Defined outputDef = new Defined(outputFunc, -1);
      ArrayList<Defined> outputAr = new ArrayList<Defined>();
      outputAr.add(outputDef);

      visitor.symTable.put("input", inputAr);
      visitor.symTable.put("output", outputAr);

      result.accept(visitor, 0);
      visitor.print(0);
      visitor.delete(0);
      if(visitor.SEMANTIC_ERROR == true) SEMANTIC_ERROR = true;

      if(SHOW_SCOPE == true) {
        System.out.println("Leaving global scope"); 
        System.setOut(console);
        if(f != null) f.close();
      }
      if(SEMANTIC_ERROR == true) {
        System.out.println("\n\nErrors are present in " + argv[0] + ", cannot compile");
        System.exit(0);
      }
      if((COMPILE == true) && (SEMANTIC_ERROR != true)){
        //Setting up output stream to file
        //PrintStream console = System.out;
        //System.out.println("GENERATING CODE");
        FileOutputStream fTM = new FileOutputStream("./" + fileNameTM);
        System.setOut(new PrintStream(fTM));

        //Prelude
        CodeGen.prelude(fileNameTM);
        //Other stuff
        CodeGen visitorTM = new CodeGen();
        result.accept(visitorTM, 0);
        //Finale
        CodeGen.finale(console);
      }

    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
      }
  }
}


