import c1Absyn.*;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CodeGen {
	int IADDR_SIZE = 1024;
	int DADDR_SIZE = 1024;
	int NO_REGS = 8;
	int PC_REG = 7;

	public static void codeGen() throws Exception{
		//Setting up output stream to file
		PrintStream console = System.out;
		System.out.println("GENERATING CODE");
		FileOutputStream f = new FileOutputStream("./output.tm");
		System.setOut(new PrintStream(f));
		int line = 3; //Variable to track line number in output file, starts at line after prelude

		//Printing prelude
		System.out.println("0: LD 6, 0(0)"); //load gp with maxaddr
		System.out.println("1: LDA 5, 0(6)"); //Copy gp to fp
		System.out.println("2: ST 0, 0(0)"); //Clear content at loc

		//Rest of the stuff lol

		//Printing finale
		System.out.println(line + ": ST 5, -1(5)");
		line++;
		System.out.println(line + ": LDA 5, -1(5)");
		line++;
		System.out.println(line + ": LDA 0, 1(7)");
		line++;
		System.out.println(line + ": LDA 7, -35(7)");
		line++;
		System.out.println(line + ": LD 5, 0(5)");
		line++;
		System.out.println(line + ": HALT 0, 0, 0");
		
		if(f != null) f.close();
		System.setOut(console); //Reset output to terminal
	}

}