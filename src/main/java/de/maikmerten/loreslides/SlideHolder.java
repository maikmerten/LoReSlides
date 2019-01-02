package de.maikmerten.loreslides;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author maik
 */
public class SlideHolder implements ListModel<Slide>{
	
	private List<Font> fonts = new ArrayList<>();
	private List<Slide> slides = new ArrayList<>();
	int position = 0;
	
	// ListModel stuff
	List<ListDataListener> listeners = new ArrayList<>();
	
	
	public SlideHolder() {
		fonts.add(new Font());
		addSlide();
	}
	
	public Font getFontById(int fontId) {
		for(Font f : fonts) {
			if(f.getId() == fontId) {
				return f;
			}
		}
		return null;
	}
	
	public List<Font> getFonts() {
		return fonts;
	}
	
	public List<Slide> getSlides() {
		notifyListeners();
		return slides;
	}
	
	private void checkPosition() {
		position = position < 0 ? 0 : position;
		position = position > slides.size() - 1 ? slides.size() - 1 : position;
		notifyListeners();
	}
	
	public void addSlide(Slide s) {
		slides.add(s);
		position = slides.size() - 1;
		checkPosition();
	}
	
	public void addSlide() {
		Slide s = new Slide(this, fonts.get(0).getId(), 40, 25);
		addSlide(s);
	}
	
	public void removeSlide() {
		slides.remove(position);
		if(slides.size() < 1) {
			addSlide();
		}
		checkPosition();
	}
	
	public Slide getNextSlide() {
		position++;
		checkPosition();
		return slides.get(position);
	}
	
	public Slide getPrevSlide() {
		position--;
		checkPosition();
		return slides.get(position);
	}
	
	public Slide getCurrentSlide() {
		return slides.get(position);
	}
	


	public Slide setCurrentPosition(int position) {
		this.position = position;
		checkPosition();
		return getCurrentSlide();
	}
	
	public int getCurrentPosition() {
		return position;
	}
	
	public void moveUp() {
		if(position == 0) {
			return;
		}
		Slide previous = slides.get(position - 1);
		Slide current = getCurrentSlide();
		
		slides.set(position - 1, current);
		slides.set(position, previous);
		position -= 1;
		checkPosition();
	}
	
	public void moveDown() {
		if(position >= slides.size() - 1) {
			return;
		}
		Slide next = slides.get(position + 1);
		Slide current = getCurrentSlide();
		
		slides.set(position + 1, current);
		slides.set(position, next);
		position += 1;
		checkPosition();
	}
	
	
	public byte[] toBytes() throws IOException {
		
		Directory dir = new Directory();
		
		int numentries = fonts.size() + slides.size();
		int dirsize = Directory.dirSize(numentries);
		
		int offset = dirsize;
		List<byte[]> entrydata = new ArrayList<>();
		
		for(Font f : fonts) {
			DirEntry dentry = new DirEntry(DirEntry.TYPE_FONT, f.getId(), offset);
			dir.addEntry(dentry);
			byte[] dat = f.toBytes();
			entrydata.add(dat);
			offset += dat.length;
		}
		
		for(Slide s : slides) {
			DirEntry dentry = new DirEntry(DirEntry.TYPE_SLIDE, 0, offset);
			dir.addEntry(dentry);
			byte[] dat = s.toBytes();
			entrydata.add(dat);
			offset += dat.length;
		}
		
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		baos.write(dir.toBytes());
		for(byte[] dat : entrydata) {
			baos.write(dat);
		}
		
		return baos.toByteArray();
	}
	
	public void saveToFile(File f) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(toBytes());
		fos.close();
	}
	
	
	public void fromBytes(byte[] data, int off) {
		fonts.clear();
		slides.clear();
		position = 0;
		
		Directory dir = Directory.fromBytes(data, off);
		
		for(DirEntry entry : dir.getEntries()) {
			if(entry.type == DirEntry.TYPE_FONT) {
				Font f = Font.fromBytes(data, entry.off);
				fonts.add(f);
			} else if(entry.type == DirEntry.TYPE_SLIDE) {
				Slide s = Slide.fromBytes(data, entry.off, this);
				slides.add(s);
			}
		}
		notifyListeners();
	}
	
	public void loadFromFile(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] buf = new byte[2048];
		int read = fis.read(buf);
		while(read > 0) {
			baos.write(buf, 0, read);
			read = fis.read(buf);
		}
		
		fromBytes(baos.toByteArray(), 0);
	}
	
	private void notifyListeners() {
		for(ListDataListener l : listeners) {
			l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, (slides.size() - 1)));
		}
	}

	@Override
	public int getSize() {
		return slides.size();
	}

	@Override
	public Slide getElementAt(int index) {
		return slides.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		if(!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
}
