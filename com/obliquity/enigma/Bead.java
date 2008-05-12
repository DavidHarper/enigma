package com.obliquity.enigma;

import java.awt.Color;

public class Bead {
	static final int WHITE = 0, RED = 1, GREEN = 2, BLUE = 3;
	static final int CYAN = 4, MAGENTA = 5, YELLOW = 6, BLACK = 7;

	private int colour;

	public Bead(int mycolour) {
		colour = mycolour;
	}

	public int getColour() {
		return colour;
	}

	public Color getAWTColour() {
		switch (colour) {
		case WHITE:
			return Color.white;
		case RED:
			return Color.red;
		case GREEN:
			return Color.green;
		case BLUE:
			return Color.blue;
		case CYAN:
			return Color.cyan;
		case MAGENTA:
			return Color.magenta;
		case YELLOW:
			return Color.yellow;
		case BLACK:
			return Color.black;
		default:
			return Color.gray;
		}
	}

	public boolean equals(Bead other) {
		return (colour == other.getColour());
	}
}
