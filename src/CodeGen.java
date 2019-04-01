import c1Absyn.*;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CodeGen {
	int IADDR_SIZE = 1024;
	int DADDR_SIZE = 1024;
	int NO_REGS = 8;
	int PC_REG = 7;

	void codeGen(Absyn tree) throws Exception{
		//Setting up output stream to file
		FileOutputStream f = new FileOutputStream("/output.tm");
		System.setOut(new PrintStream(f));
		int line = 2; //Variable to track line number in output file

		//Printing prelude
		
		
	}

}