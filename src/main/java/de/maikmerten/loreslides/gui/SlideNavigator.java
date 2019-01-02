package de.maikmerten.loreslides.gui;

import de.maikmerten.loreslides.Slide;
import de.maikmerten.loreslides.SlideHolder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author maik
 */
public class SlideNavigator extends JPanel implements ListDataListener {
	
	private final SlideHolder sh;
	private final SlideEditor se;
	private final JList<Slide> slideList;
	private final JButton buttonUp;
	private final JButton buttonDown;
	private final JButton buttonNew;
	private final JButton buttonDuplicate;
	private final JButton buttonDelete;
	
	public SlideNavigator(SlideEditor se, SlideHolder sh) {
		super();
		this.sh = sh;
		this.se = se;
		
		sh.addListDataListener(this);
		
		slideList = new JList<>();
		slideList.setModel(sh);
		slideList.setFocusable(false);
		
		
		slideList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				sh.setCurrentPosition(slideList.getSelectedIndex());
				se.setSlide(sh.getCurrentSlide());
			}
		});
		JScrollPane listScroll = new JScrollPane(slideList);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(5, 1));
		buttonUp = new JButton("move up");
		buttonDown = new JButton("move down");
		buttonDuplicate = new JButton("duplicate slide");
		buttonNew = new JButton("new slide");
		buttonDelete = new JButton("delete slide");

		buttonUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sh.moveUp();
			}
		});
		
		buttonDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sh.moveDown();
			}
		});
		

		buttonNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sh.addSlide();
				se.setSlide(sh.getCurrentSlide());
			}
		});
		
		
		buttonDuplicate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Slide template = sh.getCurrentSlide();
					Slide newslide = Slide.fromBytes(template.toBytes(), 0, sh);
					sh.addSlide(newslide);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		
		buttonDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sh.removeSlide();
				se.setSlide(sh.getCurrentSlide());
			}
		});
		

		buttonPanel.add(buttonUp);
		buttonPanel.add(buttonDown);
		buttonPanel.add(buttonNew);
		buttonPanel.add(buttonDuplicate);
		buttonPanel.add(buttonDelete);

		
		setLayout(new BorderLayout());
		add(listScroll, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		this.setPreferredSize(new Dimension(200, 500));
		
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		processListDataEvent(e);
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		processListDataEvent(e);
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		processListDataEvent(e);
	}
	
	private void processListDataEvent(ListDataEvent e) {
		slideList.setSelectedIndex(sh.getCurrentPosition());
	}
	
	
	
}
