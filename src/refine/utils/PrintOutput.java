package refine.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class PrintOutput {

	Map<String, PrintStream> outputsMap;

	public PrintOutput() {
		this.outputsMap = new HashMap<String, PrintStream>();
	}



	public void write(String text, String address) {
		PrintStream outuput = outputsMap.get(address);

		try {
			if (outuput == null) {
				outputsMap.put(address, new PrintStream(new File(address)));
			}
			 outuput = outputsMap.get(address);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		
		if (outuput != null) {
			outuput.print(text);
		}
	}



	public void finish(String adress) {
		PrintStream outuput = outputsMap.get(adress);

		if (outuput != null) {
			outuput.close();
		}

		outputsMap.remove(adress);
	}
}
