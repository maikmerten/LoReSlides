package de.maikmerten.loreslides;

import de.maikmerten.loreslides.gui.CharPanel;
import de.maikmerten.loreslides.gui.SlideEditor;
import de.maikmerten.loreslides.gui.SlideNavigator;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author maik
 */
public class LoReSlides {
	
	static File lastChosenFile = null;
	
	public static void main(String[] args) throws Exception {
		
		JFrame frame = new JFrame("LoReSlides");
		SlideHolder sh = new SlideHolder();
		SlideEditor se = new SlideEditor();
		CharPanel cp = new CharPanel(se, sh);
		SlideNavigator sn = new SlideNavigator(se, sh);
		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		
		
		JMenuItem saveItem = new JMenuItem("Save to file");
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(lastChosenFile == null) {
						lastChosenFile = new File(System.getProperty("user.home"));
					}
					
					JFileChooser fc = new JFileChooser(lastChosenFile);
					int retval = fc.showSaveDialog(frame);
					System.out.println("save 1");
					if(retval == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						System.out.println("save 2");
						sh.saveToFile(f);
						lastChosenFile = f;
						System.out.println("save 3");
					}
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		fileMenu.add(saveItem);
		
		JMenuItem loadItem = new JMenuItem("Load from file");
		loadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(lastChosenFile == null) {
						lastChosenFile = new File(System.getProperty("user.home"));
					}
					
					JFileChooser fc = new JFileChooser(lastChosenFile);
					int retval = fc.showOpenDialog(frame);
					if(retval == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						if(f.isFile()) {
							sh.loadFromFile(f);
							se.setSlide(sh.getCurrentSlide());
						}
					}
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		fileMenu.add(loadItem);
		
		
		JMenuItem drawFrameItem = new JMenuItem("Draw slide frame");
		drawFrameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				se.drawFrame();
			}
		});
		editMenu.add(drawFrameItem);
		
		mb.add(fileMenu);
		mb.add(editMenu);

		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 640);

		
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(mb, BorderLayout.NORTH);
		frame.getContentPane().add(se, BorderLayout.CENTER);
		frame.getContentPane().add(cp, BorderLayout.EAST);
		frame.getContentPane().add(sn, BorderLayout.WEST);

		frame.pack();
		frame.setVisible(true);

		se.setSlide(sh.getCurrentSlide());
		se.requestFocus();
		
	}
	
}
