package com.obliquity.enigma;

import java.awt.*;
import java.awt.event.*;

class BeadButton extends Component {
	boolean pressed = false;
	ActionListener actionListener;
	String actionCommand;
	Color mycolour;
	Bead mybead;

	BeadButton(int size, Bead bead) {
		setSize(size, size);
		mybead = bead;
		mycolour = bead.getAWTColour();
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	public Bead getBead() {
		return mybead;
	}

	public void paint(Graphics g) {
		int width = getSize().width, height = getSize().height;

		int hoffset = width / 10;
		if (hoffset < 2)
			hoffset = 2;

		int voffset = height / 10;
		if (voffset < 2)
			voffset = 2;

		g.setColor(getBackground());
		g.fill3DRect(0, 0, width, height, !pressed);

		g.setColor(pressed ? mycolour.darker() : mycolour);
		g.fillOval(hoffset, voffset, width - 2 * hoffset, height - 2 * voffset);
	}

	public Dimension getPreferredSize() {
		return getSize();
	}

	public void processEvent(AWTEvent e) {
		if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			pressed = true;
			repaint();
		} else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
			pressed = false;
			repaint();
			fireEvent();
		}
		super.processEvent(e);
	}

	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	public void addActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

	public void removeActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

	private void fireEvent() {
		if (actionListener != null) {
			ActionEvent event = new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, actionCommand);
			actionListener.actionPerformed(event);
		}
	}
}
