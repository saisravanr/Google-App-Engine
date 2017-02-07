import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class filegenerater {

	public static void main(String[] args) {

		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		int size[] = { 1024,10240,102400, 1048576, 10485760, 104857600};
		String dirname[] = {"1KB","10KB","100KB","1MB","10MB","100MB"};
		String filenames[] = {"001_KB_","010_KB_","100_KB_","001_MB_","010_MB_","100_MB_"};
		int filecount[] = {100,100,100,100,10,1};

		try {
			for (int k = 0; k < 6; k++) {
				boolean success = (new File(dirname[k])).mkdirs();
				if (success) {
					for (int i = 1; i <= filecount[k]; i++) {
						FileWriter fstream = new FileWriter(dirname[k]+"/" + filenames[k] + String.format("%03d", i));
						BufferedWriter out = new BufferedWriter(fstream);
						int m =98;
						for (int j = 1; j <= size[k]; j++) {
							out.write(AB.charAt(rnd.nextInt(AB.length())));
							if (j % m == 0) {
								j = j + 2;
								m = m+100;
								out.newLine();
							}
						}
						out.close();
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
