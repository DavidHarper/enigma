package com.obliquity.enigma;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Enigma extends Applet {
  static final int PLAYING = 0, FINISHED = 1;

  EnigmaPanel [] tries;
  EnigmaPanel master;
  Bead [] beads;
  int currentpanel;
  Button btnEvaluate, btnDelete;
  int nStatus;
  int nBeads;
  int nTries;

  public Enigma() {
  }

  public static void main(String args[]) {
    Frame frame = new Frame("Enigma");

    frame.setLayout(new BorderLayout());

    Enigma enigma = new Enigma();

    enigma.init();
    frame.add(enigma, BorderLayout.CENTER);

    enigma.start();

    frame.setSize(enigma.getMinimumSize());

    frame.show();
  }

  public int getIntegerParameter(String name, int defaultValue) {
    String str;
    int value;

    try {
      str = getParameter(name);
    }
    catch (NullPointerException npe) {
      str = null;
    }

    if (str == null)
      str = System.getProperty(name);

    try {
      value = Integer.parseInt(str);
      return value;
    }
    catch (Exception e) {
      return defaultValue;
    }
  }

  public void init() {
    nBeads = getIntegerParameter("beads", 4);

    nTries = nBeads * 3;
    if ((nTries%2) != 0)
      nTries += 1;

    setLayout(new BorderLayout());

    Panel mainpanel = new Panel();
    add(mainpanel, "Center");

    mainpanel.setLayout(new StackLayout(10));

    tries = new EnigmaPanel[nTries];

    beads = new Bead[8];

    beads[0] = new Bead(Bead.WHITE);

    beads[1] = new Bead(Bead.RED);
    beads[2] = new Bead(Bead.GREEN);
    beads[3] = new Bead(Bead.BLUE);

    beads[4] = new Bead(Bead.CYAN);
    beads[5] = new Bead(Bead.MAGENTA);
    beads[6] = new Bead(Bead.YELLOW);

    beads[7] = new Bead(Bead.BLACK);

    master = new EnigmaPanel(nBeads, 20, 4, Color.lightGray);
    master.setStatus(EnigmaPanel.MASTER);

    Panel subpanel = new Panel();
    subpanel.setLayout(new FlowLayout());

    subpanel.add(master);

    mainpanel.add(subpanel);

    subpanel = new Panel();
    subpanel.setLayout(new FlowLayout());

    BeadButton bb;

    for (int i = 0; i < beads.length; i++) {
      bb = new BeadButton(30, beads[i]);
      bb.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (nStatus != PLAYING)
	    return;
	  
	  BeadButton btn = (BeadButton)e.getSource();
	  Bead b = btn.getBead();
	  try {
	    tries[currentpanel].addBead(b);
	  }
	  catch (Exception ex) {
	    System.err.println("BeadButton actionPerformed: " + ex);
	  }
	  checkButtons();
	}
      });
		    
      subpanel.add(bb);
    }

    mainpanel.add(subpanel);

    subpanel = new Panel();
    subpanel.setLayout(new FlowLayout());

    Button b;

    btnEvaluate = new Button("Evaluate");
    subpanel.add(btnEvaluate);

    btnEvaluate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (nStatus != PLAYING)
	  return;

	try {
	  boolean done = tries[currentpanel].evaluate(master);

	  if (done) {
	    reveal();
	  } else {
	    currentpanel++;
	    checkButtons();
	  }
	}
	catch (Exception ex) {
	  System.err.println("Evaluate button actionPerformed: " + ex);
	}
      }
    });

    btnDelete = new Button("Delete");
    subpanel.add(btnDelete);

    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (nStatus != PLAYING)
	  return;
	
	try {
	  tries[currentpanel].deleteBead();
	}
	catch (Exception ex) {			
	  System.err.println("Delete button actionPerformed: " + ex);
	}

	checkButtons();
      }
    });

    b = new Button("New Game");
    subpanel.add(b);

    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	newGame();
      }
    });

    b = new Button("Reveal");
    subpanel.add(b);

    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	reveal();
      }
    });

    mainpanel.add(subpanel);
    
    subpanel = new Panel();
    subpanel.setLayout(new FlowLayout());

    Panel playpanel1 = new Panel();
    playpanel1.setLayout(new StackLayout(5));
    Panel playpanel2 = new Panel();
    playpanel2.setLayout(new StackLayout(5));

    for (int i = nTries - 1; i > -1; i--) {
      tries[i] = new EnigmaPanel(nBeads, 20, 4, Color.lightGray);
      tries[i].setStatus(EnigmaPanel.FILLING);
	    
      if (i < nTries/2)
	playpanel1.add(tries[i]);
      else
	playpanel2.add(tries[i]);
    }

    subpanel.add(playpanel1);
    subpanel.add(playpanel2);

    mainpanel.add(subpanel);
    
    newGame();
  }

  private void reveal() {
    master.setStatus(EnigmaPanel.EVALUATED);
    nStatus = FINISHED;
    checkButtons();
  }

  private void newGame() {
    for (int i = 0; i < 12; i++)
      tries[i].clear();

    master.randomize(beads);

    currentpanel = 0;
    nStatus = PLAYING;

    checkButtons();
  }

  private void checkButtons() {
    if (nStatus == PLAYING) {
      btnDelete.setEnabled(! tries[currentpanel].isEmpty());
      btnEvaluate.setEnabled(tries[currentpanel].isFull());
    } else {
      btnDelete.setEnabled(false);
      btnEvaluate.setEnabled(false);
    }
  }

  public String getAppletInfo() {
    return "Enigma\nCopyright \u00a9 2001 David Harper at Obliquity Consulting\n" +
      "www.obliquity.com";
  }
}
