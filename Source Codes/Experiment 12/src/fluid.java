import java.util.StringTokenizer; 
import java.util.LinkedList;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.Point;
import java.applet.*;
import java.awt.event.*;
import java.awt.event.ActionListener;

public class fluid extends Applet implements ActionListener , Runnable{
	private String ratesTemp,ratesTemp1;
	private double lambda, mu, capacityBuffer, fluidInBuffer, time=0.0, actualTime=0.0, timeInc = 0.0, tempTime1, tempTime2, tempCapacity,tempTime3;
	private double meanBufferContent , timeEmpty, timeFull,avgInputRate,throughput,fluidOut;
	private int numServers, capacity,state,nextState;
	private int etype , x , b , d , a = 100;     // for error
	private Boolean playing = false, started = false;
    private Thread t;
	private Double[] rates,inputRates,outputRates;
	private StringTokenizer st,st1;

	public static double GenerateTime(double l) {
		double timeq = -Math.log(Math.random()) / l;
		return timeq;
    }
	
	protected int canvasWidth = 700,  canvasHeight = 1200; 	//awt components
    protected Image buffImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB); 
    protected SimulationCanvas canvass;  //awt components
    Graphics g = buffImage.getGraphics(); //awt components

	Button startButton , resetButton;
	TextField lambda1, mu1, numServers1, capacity1, rates1, capacityBuffer1,rates2;
    Label label1,label2,label3,label4,label5,label6,label7,label8,label9,label10;
	Color GridColor = Color.BLACK;
    Color BackgroundColor = Color.WHITE;
    Color PathColor1 = Color.BLACK;
	Color PathColor2 = new Color(0,126,250);

    Font font1 = new Font("Arial", Font.PLAIN, 12);
    Font font2 = new Font("Arial", Font.BOLD, 12);
    Font font3 = new Font("Times New Roman", Font.PLAIN, 20);
    Font font4 = new Font("Arial", Font.BOLD, 20);
    Font font5 = new Font("CASTELLER", Font.BOLD, 28);
    Font font6 = new Font("Arial", Font.ITALIC, 14);

	int MaxTime, sizeInterval, MaxSize;
    double timeInterval;
    double scale;
    double yscale,yscale1;

    public void PaintInitialGrid(Graphics g) {
        g.setColor(BackgroundColor);
        g.fillRect(1, 1, 700, 439);
        g.setColor(GridColor);
        g.drawLine(50, 400, 650, 400);
        g.drawLine(50, 400, 50, 230);
        g.setColor(Color.RED);
        g.drawLine(50, 230, 650, 230);
		
		g.setColor(GridColor);
        g.drawLine(50, 200, 650, 200);
        g.drawLine(50, 200, 50, 30);
        g.setColor(Color.RED);
        g.drawLine(50, 30, 650, 30);

        g.setColor(GridColor);
        g.setFont(font1);
        double maxRepresentableTime = (time < MaxTime) ? MaxTime : time;
        scale = time < MaxTime ? 1 : MaxTime / time;
        timeInterval = (time < MaxTime ? 2 : 2 * (time / 20));
        for (int i = 0; i <= (int) maxRepresentableTime; i += timeInterval) {
            int xPos = (int) (i * 600 / maxRepresentableTime) + 50;
            g.drawLine(xPos, 400, xPos, 408);
            g.drawString(String.valueOf(i), xPos - 7, 420);
        }
		for (int i = 0; i <= (int) maxRepresentableTime; i += timeInterval) {
            int xPos = (int) (i * 600 / maxRepresentableTime) + 50;
            g.drawLine(xPos, 200, xPos, 208);
            g.drawString(String.valueOf(i), xPos - 7, 220);
        }
        g.setFont(font2);
        g.drawString("Time [units]", 330, 435);
        g.drawString("Buffer ", 10, 240);
		g.drawString("Size", 10, 253);
		g.drawString("State ", 10, 50);
		// g.drawString("Size", 10, 253);

        if (fluidInBuffer > MaxSize) {

            MaxSize = 10* (int)(fluidInBuffer/10) + 10;
            yscale = (double) 10 / (double) MaxSize;
        }

		yscale1 = (double) (10/(capacity/10 + 10));
        double maxRepresentableSize = MaxSize;
        sizeInterval = (int) MaxSize / 5;
        for (int i = 0; i <= (int) maxRepresentableSize; i += sizeInterval) {
            int yPos = 400 - (int) (i * 170 / maxRepresentableSize);
            g.drawLine(42, yPos, 50, yPos);
            g.drawString(String.valueOf(i), 30, yPos);
        }
		for (int i = 0; i <= (capacity/10 + 10); i += (capacity/10+2)) {
            int yPos = 200 - (int) (i * 170 / (capacity/10 + 10));
            g.drawLine(42, yPos, 50, yPos);
            g.drawString(String.valueOf(i), 30, yPos);
        }

		 
		
	   g.setColor(Color.WHITE);
        g.fillRect(412, 443, 108, 35);
        g.setColor(Color.BLACK);
        g.setFont(font6);
        g.drawString("(till t=" + (int) (time + 1) + ")", 440, 474);
        g.drawString("(Theoretical)", 537, 474);
        g.setColor(Color.DARK_GRAY);
        g.setFont(font3);
        g.drawString("Run Time", 430, 460);
        g.drawString("Steady State", 535, 460);
		g.drawString("State of background process = " + state, 60, 20);
		g.drawString("Fluid in Buffer = " +truncate(fluidInBuffer), 360,20);
		g.setColor(Color.WHITE);
        g.fillRect(415, 481, 100, 170);
		
        // g.fillRect(60, 381, 340, 150);
        g.setColor(Color.GRAY);
        g.setFont(font3);
		g.drawString("" +truncate(meanBufferContent), 450,500);
		g.drawString("" +truncate(timeFull/time) , 450, 530);
		g.drawString("" +truncate(timeEmpty/time), 450, 560);
		g.drawString("" +truncate(fluidOut/time),450,590 );
		g.drawString("" +truncate(1-(timeEmpty/time)), 450, 620);
		g.drawString("" + truncate(meanBufferContent*(capacity+1)/avgInputRate), 450, 650);
		canvass.repaint();

    }

    public void Join(Graphics g, Point p1, Point p2) {
        g.setColor(PathColor2);
		 int[] tempx = {(int) ((p1.x - 50) * scale + 50),(int) ((p2.x - 50) * scale + 50),(int) ((p2.x - 50) * scale + 50),(int) ((p1.x - 50) * scale + 50)};
		 int[] tempy = {(int) (400 - (270 - p1.y) * yscale), (int) (400 - (270 - p2.y) * yscale), 400,400};
		 g.fillPolygon(tempx , tempy , 4);
		}

    public void Join1(Graphics g, Point p1, Point p2) {
          g.setColor(PathColor2);

        g.drawLine((int) ((p1.x - 50) * scale + 50), (int) (200 - (270 - p1.y) * yscale1), (int) ((p2.x - 50) * scale + 50), (int) (200 - (270 - p2.y) * yscale1));
  	}

    public void DrawPath(Graphics g, LinkedList<Point> l) {
        for (int i = 0; i < l.size() - 1; i++) {
            Join(g, l.get(i), l.get(i + 1));
        }
    }
	public void DrawPath1(Graphics g, LinkedList<Point> l) {
        for (int i = 0; i < l.size() - 1; i++) {
            Join1(g, l.get(i), l.get(i + 1));
        }
    }

	public void HandleError(int e) {
		g.setColor(Color.RED);
        Font ErrorFont = new Font("Arial", Font.BOLD, 22);
        g.setFont(ErrorFont);

		if (e == 1) {
            g.drawString("*Error in input in Lambda", a, b + d * x);
            x++;
        }
		if(e ==2){
			g.drawString("*Lambda should be more than 0", a, b + d * x);
            x++;
        }
		if(e==3){
			g.drawString("*Error in input in Mu", a, b + d * x);
			x++;
        }
		if(e==4){
			g.drawString("*Mu should be more than 0", a, b + d * x);
			x++;
        }
		if(e==5){
			g.drawString("*Error in input in Capacity of backgound process", a, b + d * x);
            x++;
        }
		if(e==6){
			g.drawString("*Capacity of background process should be more than 1", a, b + d * x);
            x++;
		}
		if(e==7){
			g.drawString("*Error in Buffer Capacity", a, b + d * x);
            x++;
        }
		if(e==8){
			g.drawString("*Buffer capacity should be more than 0", a, b + d * x);
            x++;
        }
		if(e==9){
			g.drawString("*Error in inflow fluid Rates. The rates should be positive", a, b + d * x);
            x++;
        }
		if(e==10){
			g.drawString("*The number of inflow rates is more than capacity+1", a, b + d * x);
            x++;
        }
		if(e==11){
			g.drawString("*The number of inflow rates is less than capacity+1", a, b + d * x);
            x++;
        }
		if(e==12){
			g.drawString("*Error in outflow Fluid Rates. The rates should be positive", a, b + d * x);
            x++;
        }
		if(e==13){
			g.drawString("*The number of outflow rates is more than capacity+1", a, b + d * x);
            x++;
        }
		if(e==14){
			g.drawString("*The number of outflow rates is less than capacity+1", a, b + d * x);
            x++;
        }
	}

    public void init() {
        this.setLayout(new BorderLayout(5, 5));
        this.setBackground(Color.WHITE);

        canvass = new SimulationCanvas(buffImage);
        canvass.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        canvass.setBackground(Color.DARK_GRAY);
        this.add("Center", canvass);
		
		label1 = new Label("Parameters of background process");
		label2 = new Label("Arrival Rate (lambda)");
		label3 = new Label("Departure Rate (mu)");
		label4 = new Label("Number of Servers");
		label5 = new Label("Number of states");
		label6 = new Label("Buffer capacity");
		label7 = new Label("Inflow rates of fluid buffer(e.g. 2,5,....,4,5)");
		label8 = new Label("(Enter the inflow and outflow ");
		label10 = new Label("rates separated by a comma)");
		label9 = new Label("Outflow rates of fluid buffer(e.g. 3,2,....,5,1)");  
		startButton = new Button("Start");
        startButton.addActionListener(this);
        resetButton = new Button("Reset");
        resetButton.addActionListener(this);
        resetButton.enable(false);
		lambda1 = new TextField("", 4);
		mu1 = new TextField("",4);
		numServers1 = new TextField("1",4);
		numServers1.enable(false);
		capacity1 = new TextField("",4);
		rates1 = new TextField("", 8);
		rates2 = new TextField("",8);
		capacityBuffer1 = new TextField("",4);
		
		Panel p1 = new Panel();
        p1.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;c.gridy = 0;
		p1.add(startButton,c);
		c.gridx = 1;c.gridy = 0;
		p1.add(resetButton,c);
		c.gridx = 0; c.gridy = 4;
		p1.add(label1,c);
		c.gridx = 0; c.gridy = 5;
		p1.add(label2,c);
		c.gridx = 1; c.gridy = 5;
		p1.add(lambda1,c);
		c.gridx = 0;c.gridy = 6;
		p1.add(label3,c);
		c.gridx = 1;c.gridy = 6;
		p1.add(mu1,c);
		c.gridx = 0;c.gridy = 7;
		p1.add(label4,c);
		c.gridx = 1;c.gridy = 7;
		p1.add(numServers1,c);
		c.gridx = 0;c.gridy = 8;
		p1.add(label5,c);
		c.gridx = 1;c.gridy = 8;
		p1.add(capacity1,c);
		c.gridx = 0;c.gridy = 3;
		p1.add(label6,c);
		c.gridx = 1;c.gridy = 3;
		p1.add(capacityBuffer1,c);
		c.gridx = 0;c.gridy = 1;
		p1.add(label7,c);
		c.gridx = 1;c.gridy = 1;
		p1.add(rates1,c);
		c.gridx = 0;c.gridy = 2;
		p1.add(label9,c);
		c.gridx = 1;c.gridy = 2;
		p1.add(rates2,c);
		// c.gridx = 0;c.gridy = 9;
		// p1.add(label8,c);
		// c.gridx = 0;c.gridy = 10;
		// p1.add(label10,c);
		
	
		this.add("East", p1);
        canvass.repaint();
   }
	
	 private static double truncate(double x) {
        long y = (long) (x * 1000);
        return (double) y / 1000;
    }
	
	public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            if (startButton.getLabel().equals("Start")) {
                if (started == false) {
                    x = 0;
                    b = 350;
                    d = 25;
					try{
						lambda = Double.parseDouble(lambda1.getText());
					}catch(NumberFormatException ee){
						etype = 1;
                        HandleError(etype);
                        lambda = 1;
					}
                    if (lambda <= 0) {
                        etype = 2;
                        HandleError(etype);
                    }
					try{
						mu = Double.parseDouble(mu1.getText());
					}catch(NumberFormatException ee){
						etype = 3;
                        HandleError(etype);
                        mu = 1;
                    }
					if (mu <= 0) {
                        etype = 4;
                        HandleError(etype);
                    }
					try{
						capacity = Integer.parseInt(capacity1.getText())-1;
					}catch(NumberFormatException ee){
						etype = 5;
                        HandleError(etype);
                        capacity = 1;
                    }
					if (capacity <= 0) {
                        etype = 6;
                        HandleError(etype);
                    }
					try{
						capacityBuffer = Double.parseDouble(capacityBuffer1.getText());
					}catch(NumberFormatException ee){
						etype = 7;
                        HandleError(etype);
                        capacityBuffer = 1;
                    }
					if (capacityBuffer <= 0) {
                        etype = 8;
                        HandleError(etype);
                    }
					st = new StringTokenizer(rates1.getText(), ","); 
					st1 = new StringTokenizer(rates2.getText(), ","); 
					// System.out.println(rates1.getText());
					avgInputRate = 0.0;
					rates = new Double[capacity+1];
					inputRates = new Double[capacity+1];
					outputRates = new Double[capacity+1];
					
					for(int rateNum = 0 ; rateNum < capacity + 1 ; rateNum++ ){
						if(st.hasMoreTokens()){
							try{
								ratesTemp = st.nextToken();
								inputRates[rateNum] = Double.parseDouble(ratesTemp);
								avgInputRate = avgInputRate + inputRates[rateNum];
							}catch(NumberFormatException ee){
								
								if(etype!=9 ) {
								HandleError(9);
								etype = 9;
								}
							}
						}
						else  {
							
							etype = 11;
							HandleError(etype);
						}
					}if(st.hasMoreTokens()){
						etype = 10;
						HandleError(10);
					}
					for(int rateNum = 0 ; rateNum < capacity + 1 ; rateNum++ ){
						if(st1.hasMoreTokens()){
							try{
								ratesTemp = st1.nextToken();
								outputRates[rateNum] = Double.parseDouble(ratesTemp);
								// System.out.println(ratesTemp + " " + rates[rateNum]);
							}catch(NumberFormatException ee){
								
								if(etype!=12 ) {
								HandleError(12);
								etype = 12;
								}
							}
						}
						else  {
							
							etype = 14;
							HandleError(etype);
						}
					}
					if(st1.hasMoreTokens()){
						etype = 13;
						HandleError(13);
					}
					for(int rateNum = 0 ; rateNum < capacity + 1 ; rateNum++ ){
						rates[rateNum] = inputRates[rateNum] - outputRates[rateNum];
					}
					lambda1.enable(false);
					mu1.enable(false);
					capacity1.enable(false);
					capacityBuffer1.enable(false);
					rates1.enable(false);
					rates2.enable(false);
					resetButton.enable(true);
                    canvass.repaint();
					if (!(etype == 0)) {
                        g.drawString("CLICK ON RESET BUTTON AND CORRECT INPUTS", 10, b + d * x);
                    } else {
                        t = new Thread(this);
                        started = true;
                        t.start();
                        startButton.setLabel("Pause");
                        playing = true;
                        canvass.repaint();
                    }
                }
                playing = true;
                startButton.setLabel("Pause");

                canvass.repaint();
            } else if (startButton.getLabel().equals("Pause")) {
                System.out.println("This is a pause");
				playing = false;
					
                startButton.setLabel("Start");
                canvass.repaint();
            }
        }
        if (e.getSource() == resetButton) {
            if (t != null) {
                t.stop();
                t = null;
            }
			lambda1.enable(true);
            mu1.enable(true);
            capacity1.enable(true);
			capacityBuffer1.enable(true);
			rates1.enable(true);
            rates2.enable(true);
            started = false;
            playing = false;
            resetButton.enable(false);
            time = 0.0;
            etype = 0;
            startButton.setLabel("Start");
            g.setColor(Color.DARK_GRAY);
            g.fillRect(1, 1, 10000, 1200);
            canvass.repaint();
        }
    }

    public void CalculateSteadyStatemeasures() {
	}
	
	int factorial(int a) {
        int fact = 1;
        for (int i = 1; i <= a; i++) {
            fact = fact * i;

        }
        return fact;
    }
            
	public void run() {
		
		state = 0;
		fluidInBuffer = 0;
		LinkedList<Point> points = new LinkedList();
		LinkedList<Point> points1 = new LinkedList();
		time = 0;
		timeEmpty = 0;
		timeFull = 0;
		fluidOut = 0.0;
		timeInc = GenerateTime(lambda);
		nextState = 1;
		CalculateSteadyStatemeasures();
        int prevX = 50, nextX = 50,prevY = 270, nextY = 270 ;
        int prevX1 = 50, nextX1 = 50,prevY1 = 270, nextY1 = 270 ;
        g.setColor(Color.WHITE);
        g.fillRect(1, 310, 700, 500);

        g.setColor(Color.BLACK);
        g.setFont(font4);
        g.drawRoundRect(50, 440, 600, 225, 10, 10);
        g.drawRoundRect(51, 441, 598, 223, 10, 10);
        g.drawLine(50, 480, 650, 480);
        g.drawLine(50, 478, 650, 478);
        g.drawLine(50, 479, 650, 479);
        g.drawLine(410, 440, 410,665);
        g.drawLine(411, 440, 411, 665);
        g.drawLine(412, 440, 412, 665);
        g.drawLine(520, 440, 520, 665);
        g.drawLine(521, 440, 521, 665);
        g.drawLine(522, 440, 522, 665);
		
        g.setColor(Color.DARK_GRAY);
        g.drawString("Performance Measures", 100, 470);
		g.setColor(Color.GRAY);
        g.setFont(font3);
		g.drawString("Average Buffer Content", 60, 500);
		g.drawString("Probability of overflow", 60, 530);
		g.drawString("Probability of buffer being empty", 60, 560);
		g.drawString("ThroughPut",60,590);
		g.drawString("Utilization", 60,620);
		g.drawString("Mean Delay", 60,650);
		g.drawString("N/A", 560, 500);
		g.drawString("N/A", 560, 530);
		g.drawString("N/A", 560, 560);
		g.drawString("N/A", 560, 590);
		g.drawString("N/A", 560, 620);
		g.drawString("N/A", 560, 650);
	
		g.setFont(font3);
        MaxTime = 20;
        sizeInterval = 4;
        MaxSize = 10;

        yscale = 1;
		
		while(true){
			
			nextY1   = 270 - (int) (170 * nextState / 10);
			
			
			
			tempCapacity = (timeInc * rates[state]);
			
			if(tempCapacity > (capacityBuffer - fluidInBuffer)){
				fluidOut = fluidOut + outputRates[state] * timeInc;
				tempTime3 =  (capacityBuffer - fluidInBuffer)/rates[state];
				if(fluidInBuffer != capacityBuffer)
				{
					nextX   = (int) (600 * (time+tempTime3) / (MaxTime)) + 50;
					nextY   = 270 - (int) (170 * capacityBuffer / 10);
					points.addLast(new Point(prevX, prevY));
					points.addLast(new Point(nextX, nextY));
					prevX = nextX;
					prevY = nextY;
					}
			
				timeFull = (timeFull + timeInc -  tempTime3);
				meanBufferContent = (meanBufferContent * time + fluidInBuffer * timeInc + 0.5 * (capacityBuffer - fluidInBuffer) * timeInc )/(time + timeInc); 
			}
			else if(tempCapacity <  (-1 * fluidInBuffer)){
				tempTime3 =  (-1*fluidInBuffer)/rates[state];
				if(fluidInBuffer != 0){
					fluidOut = fluidOut + fluidInBuffer;
					nextX   = (int) (600 * (time+tempTime3) / (MaxTime)) + 50;
					nextY   = 270;
			
					points.addLast(new Point(prevX, prevY));
					points.addLast(new Point(nextX, nextY));
			
					prevX = nextX;
					prevY = nextY;
				}
				timeEmpty = timeEmpty + timeInc - tempTime3;
				meanBufferContent = (meanBufferContent * time + fluidInBuffer * timeInc + 0.5 * (-1 * fluidInBuffer) * timeInc )/(time + timeInc); 
			}
						
			// if(fluidInBuffer == capacityBuffer && tempCapacity > 0){
				// meanBufferContent = (meanBufferContent * time + fluidInBuffer * timeInc )/(time + timeInc); 
			// }else if(fluidInBuffer == 0 && tempCapacity < 0){
				// meanBufferContent = (meanBufferContent * time)/(time + timeInc); 
			// }
			else{
				fluidOut = fluidOut + outputRates[state] * timeInc;
				meanBufferContent = (meanBufferContent * time + fluidInBuffer * timeInc + 0.5 * tempCapacity * timeInc )/(time + timeInc); 
			}
			
			
			// System.out.println(meanBufferContent);
			fluidInBuffer = fluidInBuffer + tempCapacity;
			if(fluidInBuffer > capacityBuffer) fluidInBuffer = capacityBuffer;
			if(fluidInBuffer < 0) fluidInBuffer = 0; 
			time = time + timeInc;
			nextX1   = (int) (600 * (time) / (MaxTime)) + 50;
        
			state = nextState;
			
			nextX   = (int) (600 * time / (MaxTime)) + 50;
            nextY   = 270 - (int) (170 * fluidInBuffer / 10);
			
            points.addLast(new Point(prevX, prevY));
            points.addLast(new Point(nextX, nextY));
			
			points1.addLast(new Point(prevX1, prevY1));
            points1.addLast(new Point(nextX1, prevY1));
			
			prevX = nextX;
			prevY = nextY;
			prevY1 = nextY1;
			prevX1 = nextX1;
			
			//Code for pausing the simulation
			if (!playing) {
                try {
                    t.sleep(100);
                } catch (InterruptedException ie) {
                }
                continue;
            }
			
			//dealing with the states keeping in mind first and last state
			if(state != 0 && state != capacity){
				tempTime1 = GenerateTime(lambda);
				tempTime2 = GenerateTime(mu);
				if(tempTime1 < tempTime2) {
					nextState = state + 1;
					timeInc = tempTime1;
				}else{
					nextState = state - 1;
					timeInc = tempTime2;
				}
			
			}else if(state == 0){
				nextState = 1;
				timeInc = GenerateTime(lambda);
			}else if(state == capacity){
				nextState = state - 1;
				timeInc = GenerateTime(lambda);
			}
			
			 PaintInitialGrid(g);
			DrawPath(g, points );
			DrawPath1(g, points1 );
			
			//Giving the simulation a right time to proceed 
			try {
                t.sleep((int)(timeInc*1000));
            } catch (InterruptedException z) {
            }
		}
		
	}
	
    public void stop() {
        t.stop();
    }
}