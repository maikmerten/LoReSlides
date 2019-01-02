package de.maikmerten.loreslides;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maik
 */
public class Directory {
	
	List<DirEntry> entries = new ArrayList<>();
	
	public List<DirEntry> getEntries() {
		return entries;
	}
	
	public void addEntry(DirEntry entry) {
		entries.add(entry);
	}
	
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		baos.write(ByteUtil.writeInt(entries.size()));
		
		for(DirEntry entry : entries) {
			baos.write(entry.toBytes());
		}
		
		return baos.toByteArray();
	}
	
	
	public static Directory fromBytes(byte[] data, int off) {
		Directory dir = new Directory();
		
		int entries = ByteUtil.readInt(data, off);
		off += 4;
		
		System.out.println("Dir entries: " + entries);
		
		for(int i = 0; i < entries; ++i) {
			DirEntry entry = DirEntry.fromBytes(data, off);
			dir.entries.add(entry);
			off += DirEntry.entryLength();
			System.out.println(((entry.type == DirEntry.TYPE_FONT) ? "Font" : "Slide") + " at offset " + entry.off + " with id " + entry.id);
		}
		
		return dir;
	}
	
	public static int dirSize(int entries) {
		return (entries * DirEntry.entryLength()) + 4;
	}
	
}
