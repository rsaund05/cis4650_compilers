import c1Absyn.*;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CodeGen {
	int IADDR_SIZE = 1024;
	int DADDR_SIZE = 1024;
	int NO_REGS = 8;
	int PC_REG = 7;

	public static int pc = 7;
	public static int gp = 6;
	public static int fp = 5;
	public static int ac = 0;
	public static int ac1 = 1;

	public static int line = 0; //Variable to track line number in output file, starts at line after prelude
	public static int entry = 0;
	public static int globalOffset = 0;

	public static int ofpFO = 0;
	public static int retFO = -1;
	public static int initFO = -2;



	public static void emitRM(String opCode, int r1, int offset, int r2, String comment) {
		System.out.println(line + ": " + opCode + " " + r1 + ", " + offset + "(" + r2 + ") \t" + comment);
		line++;
	}

	public static void emitRO(String opCode, int r1, int r2, int r3, String comment){
		System.out.println(line + ": " + opCode + " " + r1 + ", " + r2 + ", " + r3 + " \t" + comment);
		line++;
	}

	public static void emitRM_Abs(String opCode, int r, int a, String comment){
		System.out.println(line + ": " + opCode + " " + r + " " + (a - (line + 1)) + "(" + pc + ") \t" + comment);
		line++;
	}

	public static void codeGen() throws Exception{
		//Setting up output stream to file
		PrintStream console = System.out;
		System.out.println("GENERATING CODE");
		FileOutputStream f = new FileOutputStream("./output.tm");
		System.setOut(new PrintStream(f));
		

		//Printing prelude
		emitRM("LD", 6, 0, 0, "load gp with maxaddr");
		emitRM("LDA", 5, 0, 6, "Copy gp to fp");
		emitRM("ST", 0, 0, 0, "Clear content at loc");
		// System.out.println("0: LD 6, 0(0) \tload gp with maxaddr"); 
		// System.out.println("1: LDA 5, 0(6) \tCopy gp to fp"); 
		// System.out.println("2: ST 0, 0(0) \tClear content at loc"); 

		//Rest of the stuff lol

		//Printing finale
		emitRM("ST", fp, globalOffset+ofpFO, fp, "push ofp");
		emitRM("LDA", fp, globalOffset, fp, "push frame");
		emitRM_Abs("LDA", pc, entry, "jump to main loc");
		emitRM("LD", fp, ofpFO, fp, "pop frame");
		emitRO("HALT", 0, 0, 0, "");

		if(f != null) f.close();
		System.setOut(console); //Reset output to terminal
	}

}