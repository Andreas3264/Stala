package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class FileCharacterIterator implements Iterator<Character> {
	
	private FileReader fr;
	private Character next = null;
	
	public FileCharacterIterator(File file) throws FileNotFoundException
	{
		fr = new FileReader(file);
	}

	@Override
	public boolean hasNext() {
		if(next == null)
		{
			try {
				int cr;
				cr = fr.read();
				if(cr != -1)
					next = (char) cr;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return next != null;
	}

	@Override
	public Character next() {
		char cr = next;
		next = null;
		return cr;
	}

}
