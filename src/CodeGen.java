import c1Absyn.*;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CodeGen {
	int IADDR_SIZE = 1024;
	int DADDR_SIZE = 1024;
	int NO_REGS = 8;
	int PC_REG = 7;
	public static int line = 3; //Variable to track line number in output file, starts at line after prelude

	public static void emitRM(String opCode, int r1, int offset, int r2, String comment) {
		System.out.println(line + ": " + opCode + " " + r1 + ", " + offset + "(" + r2 + ") \t" + comment);
		line++;
	}

	public static void emitRO(String opCode, int r1, int r2, int r3){
		System.out.println(line + ": " + opCode + " " + r1 + ", " + r2 + ", " + r3 + " \t" + comment);
		line++;
	}

	public static void codeGen() throws Exception{
		//Setting up output stream to file
		PrintStream console = System.out;
		System.out.println("GENERATING CODE");
		FileOutputStream f = new FileOutputStream("./output.tm");
		System.setOut(new PrintStream(f));
		

		//Printing prelude
		System.out.println("0: LD 6, 0(0)"); //load gp with maxaddr
		System.out.println("1: LDA 5, 0(6)"); //Copy gp to fp
		System.out.println("2: ST 0, 0(0)"); //Clear content at loc

		//Rest of the stuff lol

		//Printing finale
		
		if(f != null) f.close();
		System.setOut(console); //Reset output to terminal
	}

}