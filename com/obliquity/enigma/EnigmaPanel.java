package com.obliquity.enigma;

import java.lang.Math;
import java.awt.*;

public class EnigmaPanel extends Component {
	static final int INACTIVE = 0, CAPTION = 1, FILLING = 2, EVALUATED = 3,
			MASTER = 4;

	Bead[] bead;
	int nWhitePegs, nBlackPegs;
	int nBeads;
	int nBeadSize, nPadding;
	int nNumBeads;
	int nStatus;
	int nWidth, nHeight;
	int nPegSize, nPegPadding;
	Color bgcolor;

	public EnigmaPanel(int numbeads, int beadsize, int padding, Color background) {
		nBeadSize = beadsize;
		nPadding = padding;
		nNumBeads = numbeads;

		nWhitePegs = nBlackPegs = 0;
		nStatus = INACTIVE;

		bead = new Bead[nNumBeads];

		for (int i = 0; i < nNumBeads; i++)
			bead[i] = null;

		nBeads = 0;

		int nBeadsWide = nNumBeads / 2 + (nNumBeads % 2);

		if ((nBeadSize - nPadding) % 2 == 0) {
			nPegSize = (nBeadSize - nPadding) / 2;
			nPegPadding = nPadding;
		} else {
			nPegSize = (nBeadSize - nPadding + 1) / 2;
			nPegPadding = nPadding - 1;
		}

		nHeight = nBeadSize + 2 * nPadding;
		nWidth = nNumBeads * (nBeadSize + nPadding) + nPadding + nBeadsWide
				* (nPegSize + nPadding);

		if (background == null)
			bgcolor = Color.lightGray;
		else
			bgcolor = background;

		setSize(nWidth, nHeight);
	}

	public Dimension getPreferredSize() {
		return new Dimension(nWidth, nHeight);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public int getStatus() {
		return nStatus;
	}

	public void setStatus(int status) {
		nStatus = status;
		repaint();
	}

	public int getNumBeads() {
		return nNumBeads;
	}

	public int getBeadCount() {
		return nBeads;
	}

	public boolean isFull() {
		return (nBeads == nNumBeads);
	}

	public boolean isEmpty() {
		return (nBeads == 0);
	}

	public void setPegs(int black, int white) {
		nBlackPegs = black;
		nWhitePegs = white;
		nStatus = EVALUATED;
	}

	public void addBead(Bead newbead) throws Exception {
		if (nStatus != FILLING)
			throw new Exception("Cannot add bead unless status is FILLING");

		if (nBeads < nNumBeads) {
			bead[nBeads] = newbead;
			nBeads++;
			repaint();
		} else
			throw new Exception("Cannot add bead to full EnigmaPanel");
	}

	public void deleteBead() throws Exception {
		if (nStatus != FILLING)
			throw new Exception("Cannot delete bead unless status is FILLING");

		if (nBeads > 0) {
			nBeads--;
			bead[nBeads] = null;
			repaint();
		} else
			throw new Exception("Cannot delete bead from empty EnigmaPanel");
	}

	public int getBead(int i) throws Exception {
		if (i < 0 || i >= nNumBeads)
			throw new Exception("Invalid index " + i
					+ " to EnigmaPanel of size " + nNumBeads);

		if (i >= nBeads)
			throw new Exception("Bead " + i + "has not been set");
		else
			return bead[i].getColour();
	}

	public boolean evaluate(EnigmaPanel other) throws Exception {
		if (nBeads < nNumBeads)
			throw new Exception("Cannot evaluate incomplete EnigmaPanel: only "
					+ nBeads + " set but " + nNumBeads + " required");

		if (nNumBeads != other.getNumBeads())
			throw new Exception("Cannot evaluate this EnigmaPanel ("
					+ nNumBeads + ") against one with " + other.getNumBeads());

		if (!other.isFull())
			throw new Exception(
					"Cannot evaluate against incomplete EnigmaPanel");

		nWhitePegs = nBlackPegs = 0;

		int[] answer = new int[nNumBeads];
		int[] guess = new int[nNumBeads];

		int i, j;

		for (i = 0; i < nNumBeads; i++) {
			answer[i] = bead[i].getColour();
			guess[i] = other.getBead(i);
			if (answer[i] == guess[i]) {
				nBlackPegs++;
				answer[i] = guess[i] = -1;
			}
		}

		for (i = 0; i < nNumBeads; i++) {
			if (guess[i] > -1) {
				for (j = 0; j < nNumBeads; j++) {
					if ((answer[j] > -1) && (guess[i] == answer[j])) {
						answer[j] = -1;
						nWhitePegs++;
						break;
					}
				}
			}
		}

		nStatus = EVALUATED;

		repaint();

		return (nBlackPegs == nNumBeads);
	}

	public void clear() {
		nBlackPegs = nWhitePegs = 0;
		for (int i = 0; i < nNumBeads; i++)
			bead[i] = null;
		nStatus = FILLING;
		nBeads = 0;
		repaint();
	}

	public void randomize(Bead[] choices) {
		int j = choices.length;

		for (int i = 0; i < nNumBeads; i++) {
			int k = Math.round((float) (Math.random() * 500));
			k = k % j;
			bead[i] = choices[k];
		}

		nStatus = MASTER;
		nBeads = nNumBeads;

		repaint();
	}

	public void paint(Graphics g) {
		int i;
		int x, y;

		if (nStatus == INACTIVE) {
			g.setColor(bgcolor);
			g.fillRect(0, 0, nWidth, nHeight);
			return;
		}

		if (nStatus == CAPTION) {
			return;
		}

		g.setColor(bgcolor);
		g.fill3DRect(0, 0, nWidth, nHeight, true);

		if (nStatus == MASTER)
			return;

		y = nPadding;

		for (i = 0; i < nBeads; i++) {
			if (bead[i] != null) {
				g.setColor(bead[i].getAWTColour());
				x = nPadding + i * (nPadding + nBeadSize);
				g.fillOval(x, y, nBeadSize, nBeadSize);
			}
		}

		if (nStatus != EVALUATED)
			return;

		int nPegs = 0;
		int x0 = nPadding + nNumBeads * (nPadding + nBeadSize);
		int y0 = nPadding;

		for (i = 0; i < nBlackPegs; i++) {
			x = x0 + (nPegs / 2) * (nPadding + nPegSize);
			y = y0 + (nPegs % 2) * (nPegPadding + nPegSize);
			g.setColor(Color.black);
			g.fillOval(x, y, nPegSize, nPegSize);
			nPegs++;
		}

		for (i = 0; i < nWhitePegs; i++) {
			x = x0 + (nPegs / 2) * (nPadding + nPegSize);
			y = y0 + (nPegs % 2) * (nPegPadding + nPegSize);
			g.setColor(Color.white);
			g.fillOval(x, y, nPegSize, nPegSize);
			nPegs++;
		}
	}
}
