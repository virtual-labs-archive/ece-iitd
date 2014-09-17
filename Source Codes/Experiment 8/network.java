import java.util.LinkedList;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.Point;
import java.applet.*;
import java.awt.event.*;
import java.awt.event.ActionListener;


class Job{
	int stage;
	double time;
	double timeWait;
	double timeOr;
	
	Job(int i , double l , double ti){
	stage = i;
	time = l;
	timeWait = ti;
	timeOr = l;
	}
}

class jobQueue{
	
	protected LinkedList<Job> l = new LinkedList();
	
	public void push(Job a){
	l.addLast(a);
	}
	
	public Job pop(){
	return l.removeFirst();
	}
	
	public Job getJob(int n){
	return l.get(n);
	}
	
	public void addJob(Job a , int n){
	l.add(n,a);
	}
	
	public int size(){
	return l.size();
	}
}

public class network extends Applet implements ActionListener , Runnable{
	private double lambda , mu1 , mu2 , mu3 , mu4 , prob , prob2 , prob3 , prob4; //parameters to be entered by user
	private double MeanNoCustomersSys , MeanNoCustomersQue , MeanNoCustomersQue1 , MeanNoCustomersQue2 , MeanNoCustomersQue3 , MeanNoCustomersQue4 , MeanNoCustomersQueu1 , MeanNoCustomersQueu2 , MeanNoCustomersQueu3 , MeanNoCustomersQueu4 ;
	private double waitTime1 , waitTime2 , waitTime3 , waitTime4 ;
	private int waitCust1 , waitCust2 , waitCust3  , waitCust4;
	private int customersEntered , customersServed1 , customersServed2 , customersServed3  , customersServed4, customersInQ1 , customersInQ2 , customersInQ3  , customersInQ4; 
    private int etype , x , b , d , a = 100;     // for error
	private double time = 0.0;
	private double timeBusy1 , timeBusy2 , timeBusy3 , timeBusy4 , timeSystem , actualTime; 
    private static int SystemSize = 0;
    private Boolean playing = false, started = false;
    private Thread t;
	private String SteadyMeanNoCustomersSys , SteadyMeanNoCustomersSys1 , SteadyMeanNoCustomersSys2 , SteadyMeanNoCustomersSys3;
    private String SteadyMeanNoCustomersQue , SteadyMeanNoCustomersQue1 , SteadyMeanNoCustomersQue2 , SteadyMeanNoCustomersQue3;
    private String SteadyMeanWaitingTimeQue , SteadyMeanWaitingTimeQue2 , SteadyMeanWaitingTimeQue3 , SteadyMeanWaitingTimeQue1;
    private String SteadyMeanResponseTime , SteadyMeanResponseTime1 , SteadyMeanResponseTime2 , SteadyMeanResponseTime3;
    private String SteadyUtilisation , SteadyUtilisation1 , SteadyUtilisation2 , SteadyUtilisation3 ;
	public static double GenerateTime(double l) {
    double time = -Math.log(Math.random()) / l;
    return time;
    }
	
	protected int canvasWidth = 700,  canvasHeight = 1000; 	//awt components
    protected Image buffImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB); 
    protected SimulationCanvas canvass;  //awt components
    Graphics g = buffImage.getGraphics(); //awt components

	Button startButton , resetButton;
	TextField lambda1 , mu11 , mu21 , mu31  , mu41 , prob1 , prob21  , prob31 , prob41;
    Label label1,label2,label3,label4,label5,label6 , label7 ,label8  , label9 , label10 , label11;
	Checkbox queueGraphTotal , queueGraph1 , queueGraph2 , queueGraph3  , queueGraph4; 
	CheckboxGroup outputPara;
	Checkbox server , parameter;
	Choice serverNum , parameterReqd;
	
    Color GridColor = Color.BLACK;
    Color BackgroundColor = Color.WHITE;
    Color PathColor1 = Color.BLACK;
	Color PathColor2 = Color.BLUE;
	Color PathColor3 = Color.RED;
	Color PathColor4 = Color.GREEN;
	
    Font font1 = new Font("Arial", Font.PLAIN, 12);
    Font font2 = new Font("Arial", Font.BOLD, 12);
    Font font3 = new Font("Times New Roman", Font.PLAIN, 20);
    Font font4 = new Font("Arial", Font.BOLD, 20);
    Font font5 = new Font("CASTELLER", Font.BOLD, 28);
    Font font6 = new Font("Arial", Font.ITALIC, 14);
    
	int MaxTime, sizeInterval, MaxSize;
    double timeInterval;
    double scale;
    double yscale;
	
	public void PaintInitialGrid(Graphics g){
	    g.setColor(BackgroundColor);
        g.fillRect(1, 1, 700, 309);
        g.fillRect(1, 300, 700, 20);
        g.setColor(GridColor);
        g.drawLine(50, 300, 650, 300);
        g.drawLine(50, 300, 50, 130);
        g.setColor(Color.RED);
        g.drawLine(50, 130, 650, 130);

        g.setColor(GridColor);
        g.setFont(font1);
        double maxRepresentableTime = (time < MaxTime) ? MaxTime : time;
        scale = time < MaxTime ? 1 : MaxTime / time;
        timeInterval = (time < MaxTime ? 2 : 2 * (time / 20));
        for (int i = 0; i <= (int) maxRepresentableTime; i += timeInterval) {
            int xPos = (int) (i * 600 / maxRepresentableTime) + 50;
            g.drawLine(xPos, 300, xPos, 308);
            g.drawString(String.valueOf(i), xPos - 7, 320);
        }
        g.setFont(font2);
        g.drawString("Time [units]", 330, 335);
        g.drawString("System Size", 20, 100);

        if (SystemSize > MaxSize) {

            MaxSize = MaxSize + 10;
            yscale = (double) 10 / (double) MaxSize;
        }
        System.out.println("Scale   " + yscale);
        double maxRepresentableSize = MaxSize;
        sizeInterval = (int) MaxSize / 5;
        for (int i = 0; i <= (int) maxRepresentableSize; i += sizeInterval) {
            int yPos = 300 - (int) (i * 170 / maxRepresentableSize);
            g.drawLine(42, yPos, 50, yPos);
            g.drawString(String.valueOf(i), 30, yPos);
        }
        g.setColor(Color.WHITE);
        g.fillRect(412, 343, 108, 35);
        g.setColor(Color.BLACK);
        g.setFont(font6);
        g.drawString("(till t=" + (int) (time + 1) + ")", 440, 374);
        g.drawString("(Theoretical)", 537, 374);
        g.setColor(Color.DARK_GRAY);
        g.setFont(font3);
        g.drawString("Run Time", 430, 360);
        g.drawString("Steady state", 535, 360);
        g.setColor(Color.WHITE);
        g.fillRect(412, 381, 107, 150);
        g.fillRect(60, 381, 340, 150);
        g.fillRect(522, 381, 107, 150);
		g.fillRect(652, 381, 107, 150);
		
        g.setColor(Color.GRAY);
        g.setFont(font3);
       
		if(server.getState()){
			// g.drawString("N/A", 560, 400);
			// g.drawString("N/A", 560, 430);
			// g.drawString("N/A", 560, 460);
			// g.drawString("N/A", 560, 490);
			// g.drawString("N/A", 560, 520);
        
			if(serverNum.getSelectedIndex() == 0){
				// static String SteadyMeanNoCustomersSys , SteadyMeanNoCustomersSys1 , SteadyMeanNoCustomersSys2 , SteadyMeanNoCustomersSys3;
				// static String SteadyMeanNoCustomersQue , SteadyMeanNoCustomersQue1 , SteadyMeanNoCustomersQue2 , SteadyMeanNoCustomersQue3;
				// static String SteadyMeanWaitingTimeQue , SteadyMeanWaitingTimeQue2 , SteadyMeanWaitingTimeQue3 , SteadyMeanWaitingTimeQue1;
				// static String SteadyMeanResponseTime , SteadyMeanResponseTime1 , SteadyMeanResponseTime2 , SteadyMeanResponseTime3;
				// static String SteadyUtilisation , SteadyUtilisation1 , SteadyUtilisation2 , SteadyUtilisation3 ;
				g.drawString(SteadyMeanNoCustomersSys1, 560, 400);
				g.drawString(SteadyMeanNoCustomersQue1, 560, 430);
				g.drawString(SteadyMeanWaitingTimeQue1, 560, 460);
				g.drawString(SteadyMeanResponseTime1, 560, 490);
				g.drawString(SteadyUtilisation1, 560, 520);
				
				g.drawString("Mean No. of Customers served by node 1", 60, 400);
				g.drawString("" + truncate(MeanNoCustomersQue1), 450, 400);
				g.drawString("Mean number of customers in Queue 1", 60, 430);
				g.drawString("" + truncate(MeanNoCustomersQueu1), 450, 430);
				g.drawString("Mean Waiting Time in Queue 1  ", 60, 460);
				g.drawString("" + truncate(waitTime1/waitCust1), 450, 460);
				g.drawString("Mean Sojourn Time with node 1", 60, 490);
				if(customersServed1 == 0) g.drawString("N/A", 450, 490 );
				else g.drawString("" + (truncate(waitTime1/waitCust1 + timeBusy1 / customersServed1 )) , 450, 490 );
				g.drawString("Utilisation of node 1 ", 60, 520);
				g.drawString("" + truncate(timeBusy1/time), 450, 520);
			}else if(serverNum.getSelectedIndex() == 1){
				g.drawString(SteadyMeanNoCustomersSys2, 560, 400);
				g.drawString(SteadyMeanNoCustomersQue2, 560, 430);
				g.drawString(SteadyMeanWaitingTimeQue2, 560, 460);
				g.drawString(SteadyMeanResponseTime2, 560, 490);
				g.drawString(SteadyUtilisation2, 560, 520);
				g.drawString("Utilisation of node 2 ", 60, 520);
				g.drawString("Mean Sojourn Time with node 2", 60, 490);
				g.drawString("Mean Waiting Time in Queue 2  ", 60, 460);
				g.drawString("Mean number of customers in Queue 2", 60, 430);
				g.drawString("Mean No. of Customers served by node 2", 60, 400);
				g.drawString("" + truncate(MeanNoCustomersQue2), 450, 400);
				g.drawString("" + truncate(MeanNoCustomersQueu2), 450, 430);
				g.drawString("" + truncate(waitTime2/waitCust2), 450, 460);
				if(customersServed2 == 0) g.drawString("N/A", 450, 490 );
				else g.drawString("" + (truncate(waitTime2/waitCust2 + timeBusy2 / customersServed2 )) , 450, 490 );
				g.drawString("" + truncate(timeBusy2/time), 450, 520);
			}else {
				g.drawString(SteadyMeanNoCustomersSys3, 560, 400);
				g.drawString(SteadyMeanNoCustomersQue3, 560, 430);
				g.drawString(SteadyMeanWaitingTimeQue3, 560, 460);
				g.drawString(SteadyMeanResponseTime3, 560, 490);
				g.drawString(SteadyUtilisation3, 560, 520);
			
				g.drawString("Utilisation of node 3 ", 60, 520);
				g.drawString("Mean Sojourn Time with node 3", 60, 490);
				g.drawString("Mean Waiting Time in Queue 3  ", 60, 460);
				g.drawString("Mean number of customers in Queue 3", 60, 430);
				g.drawString("Mean No. of Customers served by node 3", 60, 400);
				g.drawString("" + truncate(MeanNoCustomersQue3), 450, 400);
				g.drawString("" + truncate(MeanNoCustomersQueu3), 450, 430);
				g.drawString("" + truncate(waitTime3/waitCust3), 450, 460);
				if(customersServed3 == 0) g.drawString("N/A", 450, 490 );
				else g.drawString("" + (truncate(waitTime3/waitCust3 + timeBusy3 / customersServed3 )) , 450, 490 );
				g.drawString("" + truncate(timeBusy3/time), 450, 520);
			}
		}else{
			
			if(parameterReqd.getSelectedIndex() == 0){
				g.drawString(SteadyMeanNoCustomersSys, 560, 400);
				g.drawString(SteadyMeanNoCustomersSys1, 560, 437);
				g.drawString(SteadyMeanNoCustomersSys2, 560, 474);
				g.drawString(SteadyMeanNoCustomersSys3, 560, 511);
				g.drawString("Mean No. of Customers in the System ", 60, 400);
				g.drawString("" + truncate(truncate(MeanNoCustomersQue1) + truncate(MeanNoCustomersQue2) + truncate(MeanNoCustomersQue3)), 450, 400);
				g.drawString("Mean No. of Customers served by node 1", 60, 437);
				g.drawString("" + truncate(MeanNoCustomersQue1), 450, 437);
				g.drawString("Mean No. of Customers served by node 2", 60, 474);
				g.drawString("" + truncate(MeanNoCustomersQue2), 450, 474);
				g.drawString("Mean No. of Customers served by node 3", 60, 511);
				g.drawString("" + truncate(MeanNoCustomersQue3), 450, 511);
				
			}else if(parameterReqd.getSelectedIndex() == 1){
				// g.drawString(SteadyMeanNoCustomersQue3, 560, 430);
				g.drawString(SteadyMeanNoCustomersQue, 560, 400);
				g.drawString(SteadyMeanNoCustomersQue1, 560, 437);
				g.drawString(SteadyMeanNoCustomersQue2, 560, 474);
				g.drawString(SteadyMeanNoCustomersQue3, 560, 511);
				g.drawString("Mean number of customers waiting ", 60, 400);
				g.drawString("" + ( truncate(MeanNoCustomersQueu1 + MeanNoCustomersQueu2 + MeanNoCustomersQueu3 ) ), 450, 400);
				g.drawString("Mean number of customers in Queue 1", 60, 437);
				g.drawString("" + truncate(MeanNoCustomersQueu1), 450, 437);
				g.drawString("Mean number of customers in Queue 2", 60, 474);
				g.drawString("" + truncate(MeanNoCustomersQueu2), 450, 474);
				g.drawString("Mean number of customers in Queue 3", 60, 511);
				g.drawString("" + truncate(MeanNoCustomersQueu3), 450, 511);
				
			}else if(parameterReqd.getSelectedIndex() == 2){
				g.drawString(SteadyMeanWaitingTimeQue, 560, 400);
				g.drawString(SteadyMeanWaitingTimeQue1, 560, 437);
				g.drawString(SteadyMeanWaitingTimeQue2, 560, 474);
				g.drawString(SteadyMeanWaitingTimeQue3, 560, 511);
				double qaz = 0 , qax = 0 , qac = 0 ;
				if(waitCust1 != 0) qaz = waitTime1 / waitCust1;
				if(waitCust2 != 0) qax = waitTime2 / waitCust2;
				if(waitCust3 != 0) qac = waitTime3 / waitCust3;
				
				g.drawString("Mean Waiting Time in system  ", 60, 400);
				g.drawString("" +  truncate(qaz +qax + qac) , 450, 400);
				g.drawString("Mean Waiting Time in Queue 1  ", 60, 437);
				g.drawString("" + truncate(qaz), 450, 437);
				g.drawString("Mean Waiting Time in Queue 2  ", 60, 474);
				g.drawString("" + truncate(qax), 450, 474);
				g.drawString("Mean Waiting Time in Queue 3  ", 60, 511);
				g.drawString("" + truncate(qac), 450, 511);
			}else if(parameterReqd.getSelectedIndex() == 3){
				g.drawString(SteadyMeanResponseTime, 560, 400);
				g.drawString(SteadyMeanResponseTime1, 560, 437);
				g.drawString(SteadyMeanResponseTime2, 560, 474);
				g.drawString(SteadyMeanResponseTime3, 560, 511);
			double qaz = 0 , qax = 0 , qac = 0 ;
				if(customersServed1 != 0) qaz = waitTime1/waitCust1 + timeBusy1 / customersServed1;
				if(customersServed2 != 0) qax = waitTime2/waitCust2 + timeBusy2 / customersServed2;
				if(customersServed3 != 0) qac = waitTime3/waitCust3 + timeBusy3 / customersServed3;
								
				g.drawString("Mean Sojourn Time in System ", 60, 400);
				g.drawString("" + truncate(qaz + qax + qac ) , 450, 400 );
				g.drawString("Mean Sojourn Time with node 1", 60, 437);
				g.drawString("" + truncate(qaz ) , 450, 437 );
				g.drawString("Mean Sojourn Time with node 2", 60, 474);
				g.drawString("" + truncate(qax) , 450, 474 );
				g.drawString("Mean Sojourn Time with node 3", 60, 511);
				g.drawString("" + truncate(qac ) , 450, 511 );
			}else{
				// g.drawString("Mean Utilisation of servers  ", 60, 400);
				// g.drawString("" + truncate(timeSystem/time), 450, 400);
				g.drawString(SteadyUtilisation1, 560, 400);
				g.drawString(SteadyUtilisation2, 560, 440);
				g.drawString(SteadyUtilisation3, 560, 480);
				
				g.drawString("Utilisation of node 1 ", 60, 400);
				g.drawString("" + truncate(timeBusy1/time), 450, 400);
				g.drawString("Utilisation of node 2 ", 60, 440);
				g.drawString("" + truncate(timeBusy2/time), 450, 440);
				g.drawString("Utilisation of node 3 ", 60, 480);
				g.drawString("" + truncate(timeBusy3/time), 450, 480);
			
			}
		}
		
		g.drawString("Customers Entered = " + customersEntered, 120, 20);
        g.drawString("Customers Served by 1 = " + customersServed1, 120, 42);
        g.drawString("Customers Served by 2 = " + customersServed2, 120, 64);
        g.drawString("Customers Served by 3 = " + customersServed3, 120, 86);
        g.drawString("Customers in queue 1 = " + customersInQ1 , 400, 42);
        g.drawString("Customers in queue 2 = " + customersInQ2, 400, 64);
        g.drawString("Customers in queue 3 = " + customersInQ3, 400, 86);

        g.drawString("No. Of Customers In System = " + SystemSize, 400, 20);

		canvass.repaint();
    }
 
	
    public void Join(Graphics g, Point p1, Point p2 , Color PathColor) {
        g.setColor(PathColor);

        g.drawLine((int) ((p1.x - 50) * scale + 50), (int) (300 - (270 - p1.y) * yscale), (int) ((p2.x - 50) * scale + 50), (int) (300 - (270 - p2.y) * yscale));
    }

    public void DrawPath(Graphics g, LinkedList<Point> l , Color PathColor) {
        for (int i = 0; i < l.size() - 1; i++) {
            Join(g, l.get(i), l.get(i + 1) , PathColor);
        }
    }
    public void HandleError(int e) {


        g.setColor(Color.RED);
        Font ErrorFont = new Font("Arial", Font.BOLD, 22);
        g.setFont(ErrorFont);

        if (e == 11) {
            g.drawString("*Error In Input In ? (Lambda)", a, b + d * x);
            x++;
        }
        if (e == 12) {
            g.drawString("*? (Lambda) Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 21) {
            g.drawString("*Error in Input In µ1 (Mu1)", a, b + d * x);
            x++;
        }
        if (e == 22) {
            g.drawString("*µ1 (Mu1) Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 31) {
            g.drawString("*Error in Input In µ2 (Mu2)", a, b + d * x);
            x++;
        }
        if (e == 32) {
            g.drawString("*µ2 (Mu2) Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 41) {
            g.drawString("*Error in Input In µ3 (Mu3)", a, b + d * x);
            x++;
        }
        if (e == 42) {
            g.drawString("*µ3 (Mu3) Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 91) {
            g.drawString("*Error in Input In µ4 (Mu4)", a, b + d * x);
            x++;
        }
        if (e == 92) {
            g.drawString("*µ4 (Mu4) Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 51) {
            g.drawString("*Error in Input In p1 ", a, b + d * x);
            x++;
        }
        if (e == 52) {
            g.drawString("p1 Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 53) {
            g.drawString("p1 Should Be less than 1", a, b + d * x);
            x++;
        }
        if (e == 61) {
            g.drawString("*Error in Input In p2 ", a, b + d * x);
            x++;
        }
        if (e == 62) {
            g.drawString("p2 Should Be Positive ", a, b + d * x);
            x++;
        }
        if (e == 63) {
            g.drawString("p2 Should Be less than 1", a, b + d * x);
            x++;
        }
		if (e == 71) {
            g.drawString("*Error in Input In p 3 ", a, b + d * x);
            x++;
        }
        if (e == 72) {
            g.drawString("p 3 Should Be Positive ", a, b + d * x);
            x++;
        }
        if (e == 73) {
            g.drawString("p3 Should Be less than 1", a, b + d * x);
            x++;
        }
		if (e == 81) {
            g.drawString("*Error in Input In p 4 ", a, b + d * x);
            x++;
        }
        if (e == 82) {
            g.drawString("p 4 Should Be Positive ", a, b + d * x);
            x++;
        }
        if (e == 83) {
            g.drawString("p 4 Should Be less than 1", a, b + d * x);
            x++;
        }
        canvass.repaint();
	}
	
    public void init() {
        this.setLayout(new BorderLayout(5, 5));
        this.setBackground(Color.WHITE);

        canvass = new SimulationCanvas(buffImage);
        canvass.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        canvass.setBackground(Color.DARK_GRAY);
        this.add("Center", canvass);


        startButton = new Button("Start");
		startButton.addActionListener(this);
        resetButton = new Button("Reset");
		resetButton.addActionListener(this);
        resetButton.enable(false);
		queueGraphTotal  = new Checkbox("System" , null  , true);
		queueGraph1  = new Checkbox("     1      " , null  , false);
		queueGraph2  = new Checkbox("     2      " , null  , false);
		queueGraph3  = new Checkbox("     3      " , null  , false);
		queueGraph4  = new Checkbox("     4      " , null  , false);
		outputPara = new CheckboxGroup();
		server  = new Checkbox("Node          " , outputPara , false);
		parameter = new Checkbox ("Measures" , outputPara , true);
		server.enable(false);
		parameter.enable(false);
		
		serverNum  = new Choice();
		parameterReqd = new Choice();
		serverNum.addItem("1     ");
		serverNum.addItem("2     ");
		serverNum.addItem("3     ");
		// serverNum.addItem("4     ");
		serverNum.enable(false);
		parameterReqd.addItem("Customers in system");
		parameterReqd.addItem("Customers in queue");
		parameterReqd.addItem("Mean Waiting Time");
		parameterReqd.addItem("Mean Sojourn Time");
		parameterReqd.addItem("Utilisation");
		parameterReqd.enable(false);
		
        lambda1 = new TextField("", 5);
        mu11 = new TextField("", 5);
        mu21 = new TextField("", 5);
        mu31 = new TextField("", 5);
        mu41 = new TextField("1", 5);
		prob1 = new TextField("0" , 5);
		prob21 = new TextField("0" , 5);
		prob31 = new TextField("1" , 5);
		prob41 = new TextField("0" , 5);
		
        label1=new Label();
        label1.setText("Arrival Rate (lambda) :");
        label2=new Label();
        label2.setText("Service Rate 1 (µ1) :");
        label3=new Label();
        label3.setText("Service Rate 2 (µ2) :");
        label4=new Label();
        label4.setText("Service Rate 3 (µ3) :");
        label9=new Label();
        label9.setText("Service Rate 4 (µ4) :");
        label5 = new Label();
        label5.setText("Probability of feedback 1:");
        label7 = new Label();
        label7.setText("Probability of feedback 2:");
        label8 = new Label();
        label8.setText("p3 :");
        label10 = new Label();
        label10.setText("p4 :");
        label6 = new Label();
		label6.setText("Select Output Sample Path :");
        Panel p1 = new Panel();
        p1.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        p1.add(startButton, c);
        c.gridx = 1;
        p1.add(resetButton, c);
        c.gridx = 0;
		c.gridy = 1;
		p1.add(new Label("Input Parameters"), c);
        c.gridx = 0;
		c.gridy = 6;
		p1.add(new Label("Output Parameters"), c);
        
		c.gridx = 0;
        c.gridy = 2;
        p1.add(label1, c);
        c.gridx=1;
        p1.add(lambda1,c);
        c.gridx = 0;
        c.gridy = 3;
        p1.add(label2, c);
        c.gridx=1;
        p1.add(mu11,c);
        c.gridx = 0;
        c.gridy = 4;
        p1.add(label3, c);
        c.gridx=1;
        p1.add(mu21,c);
        c.gridx = 0;
        c.gridy = 5;
        p1.add(label4, c);
        c.gridx=1;
        p1.add(mu31,c);
        c.gridx = 0;
        c.gridy = 9;
        // p1.add(label9, c);
        // c.gridx=1;
        // p1.add(mu41,c);
        // c.gridx = 0;
        // c.gridy = 6;
        // p1.add(label5,c);
        // c.gridx=1;
		// p1.add(prob1 , c);
        // c.gridx = 0;
        // c.gridy = 6;
        // p1.add(label7,c);
        // c.gridx=1;
		// p1.add(prob21 , c);
        // c.gridx = 0;
        // c.gridy = 8;
        // p1.add(label8,c);
        // c.gridx=1;
		// p1.add(prob31 , c);
		// c.gridx = 0;
        // c.gridy = 9;
        // p1.add(label10,c);
        // c.gridx=1;
		// p1.add(prob41 , c);
		// c.gridx = 0;
		// c.gridy = 7;
		p1.add(label6 , c);
		c.gridy = 9;
		c.gridx = 1;
		p1.add(queueGraphTotal , c);
		c.gridy = 10;
		// c.gridx = 0 ;
		p1.add(queueGraph1 , c);
		c.gridy = 11;
		// c.gridx = 1;
		
		p1.add(queueGraph2 , c);
		c.gridy = 13;
		// c.gridx = 2;
		p1.add(queueGraph3 , c);
		// c.gridy = 14;
		// p1.add(queueGraph4 , c);
		c.gridx = 0;
		c.gridy = 7;
		p1.add( server, c);
		c.gridx = 1;
		p1.add (serverNum , c);
		c.gridx = 0;
		c.gridy = 8;
		p1.add( parameter, c);
		c.gridx = 1;
		p1.add ( parameterReqd , c);
		
		this.add("East", p1);
        canvass.repaint();
		//System.out.println("hey i am in 274");
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
                    try {
                        lambda = Double.parseDouble(lambda1.getText());

                    } catch (NumberFormatException ee) {
                        etype = 11;
                        HandleError(etype);
                        lambda = 1;
                        System.out.println("erroe\r   " + lambda);
                    }
                    if (lambda <= 0) {
                        etype = 12;
                        HandleError(etype);
                    }
                    try {
                        mu1 = Double.parseDouble(mu11.getText());
                    } catch (NumberFormatException ee) {
                        etype = 21;
                        HandleError(etype);
                        mu1 = 1;
                    }
                    if (mu1 <= 0) {
                        etype = 22;
                        HandleError(etype);
                    }

                    try {
                        mu2 = Double.parseDouble(mu21.getText());
                    } catch (NumberFormatException ee) {
                        etype = 31;
                        HandleError(etype);
                        mu2 = 1;
                    }
                    if (mu2 <= 0) {
                        etype = 32;
                        HandleError(etype);
                    }
                    try {
                        mu3 = Double.parseDouble(mu31.getText());
                    } catch (NumberFormatException ee) {
                        etype = 41;
                        HandleError(etype);
                        mu3 = 1;
                    }
                    if (mu3 <= 0) {
                        etype = 42;
                        HandleError(etype);
                    }
                    try {
                        mu4 = Double.parseDouble(mu41.getText());
                    } catch (NumberFormatException ee) {
                        etype = 91;
                        HandleError(etype);
                        mu4 = 1;
                    }
                    if (mu4 <= 0) {
                        etype = 92;
                        HandleError(etype);
                    }
                    try {
                        prob = Double.parseDouble(prob1.getText());
                    } catch (NumberFormatException ee) {
                        etype = 51;
                        HandleError(etype);
                        prob = 1;
                    }
                    if (prob < 0) {
                        etype = 52;
                        HandleError(etype);
                    }
                    if (prob > 1) {
                        etype = 53;
                        HandleError(etype);
                    }
					try {
                        prob2 = Double.parseDouble(prob21.getText());
                    } catch (NumberFormatException ee) {
                        etype = 61;
                        HandleError(etype);
                        prob2 = 1;
                    }
                    if (prob2 < 0) {
                        etype = 62;
                        HandleError(etype);
                    }
                    if (prob2 > 1) {
                        etype = 63;
                        HandleError(etype);
                    }
					try {
                        prob3 = Double.parseDouble(prob31.getText());
                    } catch (NumberFormatException ee) {
                        etype = 71;
                        HandleError(etype);
                        prob3 = 1;
                    }
                    if (prob3 < 0) {
                        etype = 72;
                        HandleError(etype);
                    }
                    if (prob3 > 1) {
                        etype = 73;
                        HandleError(etype);
                    }
					try {
                        prob4 = Double.parseDouble(prob41.getText());
                    } catch (NumberFormatException ee) {
                        etype = 81;
                        HandleError(etype);
                        prob4 = 1;
                    }
                    if (prob4 < 0) {
                        etype = 82;
                        HandleError(etype);
                    }
                    if (prob4 > 1) {
                        etype = 83;
                        HandleError(etype);
                    }
                    lambda1.enable(false);
                    mu11.enable(false);
                    mu21.enable(false);
                    mu31.enable(false);
					mu41.enable(false);
                    prob1.enable(false);
                    prob21.enable(false);
                    prob31.enable(false);
					prob41.enable(false);
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
				// run();
            } else if (startButton.getLabel().equals("Pause")) {
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
            mu11.enable(true);
            mu21.enable(true);
            mu31.enable(true);
			mu41.enable(true);
            prob1.enable(true);
            prob21.enable(true);
			prob31.enable(true);
			prob41.enable(true);
            started = false;
            playing = false;
            resetButton.enable(false);
			

            time = 0.0;
            SystemSize = 0;
            etype = 0;
            startButton.setLabel("Start");
            g.setColor(Color.DARK_GRAY);
            g.fillRect(1, 1, 1000, 1000);
            canvass.repaint();
        }
    }
    public void CalculateSteadystagemeasures() {
		
		double ro1 = lambda/mu1;
		double ro2 = mu1/mu2;
		double ro3 = mu2/mu3;
		SteadyMeanNoCustomersSys = "N/A";
		SteadyMeanNoCustomersSys1 = "N/A" ;
		SteadyMeanNoCustomersSys2 = "N/A";
		SteadyMeanNoCustomersSys3 = "N/A";
		SteadyMeanNoCustomersQue  = "N/A";
		SteadyMeanNoCustomersQue1  = "N/A";
		SteadyMeanNoCustomersQue2  = "N/A";
		SteadyMeanNoCustomersQue3  = "N/A";
		SteadyMeanWaitingTimeQue  = "N/A";
		SteadyMeanWaitingTimeQue1  = "N/A";
		SteadyMeanWaitingTimeQue2  = "N/A";
		SteadyMeanWaitingTimeQue3  = "N/A";
		SteadyMeanResponseTime = "N/A";
		SteadyMeanResponseTime1 = "N/A";
		SteadyMeanResponseTime2 = "N/A";
		SteadyMeanResponseTime3 = "N/A";
		SteadyUtilisation = "N/A";
		SteadyUtilisation1 = "1";
		SteadyUtilisation2 = "1";
		SteadyUtilisation3 = "1";
		if(ro1 < 1 ){
			SteadyMeanNoCustomersSys1 = Double.toString(truncate(ro1/(1 - ro1)));
			SteadyMeanNoCustomersQue1  = Double.toString(truncate(ro1 * ro1/(1 - ro1)));
			SteadyMeanWaitingTimeQue1  = Double.toString(truncate(ro1/(mu1 - lambda)));
			SteadyMeanResponseTime1 = Double.toString(truncate(1/(mu1 - lambda)));
			SteadyUtilisation1 = Double.toString(truncate(ro1));
		}
		if(ro2 < 1 ){
			SteadyMeanNoCustomersSys2 = Double.toString(truncate(ro2/(1 - ro2)));
			SteadyMeanNoCustomersQue2  = Double.toString(truncate(ro2 * ro2/(1 - ro2)));
			SteadyMeanWaitingTimeQue2  = Double.toString(truncate(ro2/(mu2 - mu1)));
			SteadyMeanResponseTime2 = Double.toString(truncate(1/(mu2 - mu1)));
			SteadyUtilisation2 = Double.toString(truncate(ro2));
		}
		if(ro3 < 1 ){
			SteadyMeanNoCustomersSys3 = Double.toString(truncate(ro3/(1 - ro3)));
			SteadyMeanNoCustomersQue3  = Double.toString(truncate(ro3 * ro3/(1 - ro3)));
			SteadyMeanWaitingTimeQue3  = Double.toString(truncate(ro3/(mu3 - mu2)));
			SteadyMeanResponseTime3 = Double.toString(truncate(1/(mu3 - mu2)));
			SteadyUtilisation3 = Double.toString(truncate(ro3));
		}
		if(ro1 < 1 && ro2 < 1 && ro3 < 1){
			SteadyMeanNoCustomersSys = Double.toString(truncate((ro3/(1 - ro3)) + (ro2/(1 - ro2)) + (ro1/(1 - ro1))));
			SteadyMeanNoCustomersQue  = Double.toString(truncate((ro3 * ro3/(1 - ro3)) + (ro2 * ro2/(1 - ro2)) + (ro1 * ro1/(1 - ro1))));
			SteadyMeanWaitingTimeQue  = Double.toString(truncate((ro3/(mu3 - mu2)) + (ro2/(mu2 - mu1)) + (ro1/(mu1 - lambda))));
			SteadyMeanResponseTime = Double.toString(truncate((1/(mu3 - mu2)) + (1/(mu2 - mu1)) + (1/(mu1 - lambda))));
			SteadyUtilisation = Double.toString(truncate(ro3 + ro2 + ro1));
		}
	}
	
	public void run() {
		server.enable(true);
		parameter.enable(true);
		parameterReqd.enable(true);
		
		int loopTimes = 0;
        System.out.println("this is in run");
		jobQueue mainQueue = new jobQueue();
		jobQueue queue0 = new jobQueue();
		jobQueue queue1 = new jobQueue();
		jobQueue queue2 = new jobQueue();
		jobQueue queue3 = new jobQueue();
		jobQueue queue4 = new jobQueue();
		LinkedList<Point> points = new LinkedList();
		LinkedList<Point> points1 = new LinkedList();
		LinkedList<Point> points2 = new LinkedList();
		LinkedList<Point> points3 = new LinkedList();
		LinkedList<Point> points4 = new LinkedList();
		customersEntered  = 0;
		customersServed1 = 0;
		customersServed2 = 0;
		customersServed3 = 0;
		customersServed4 = 0;
		waitTime1  = 0;
		waitTime2  = 0;
		waitTime3  = 0;
		waitTime4  = 0;
		waitCust1  = 0;
		waitCust2  = 0;
		waitCust3  = 0;
		waitCust4  = 0;
		timeBusy1 = 0;
		timeBusy2 = 0;
		timeBusy3 = 0;
		timeBusy4 = 0;
		timeSystem = 0;
		actualTime = 0;

		customersInQ1 = 0;
		customersInQ2 = 0;
		customersInQ3 = 0 ;
		customersInQ4 = 0 ;

        Job Test = new Job( 0 , GenerateTime(lambda) , 0);
		Job nextJob;
		

        CalculateSteadystagemeasures();
        mainQueue.push(Test);
		queue0.push(Test);
		int prevX = 50, nextX = 50, nextY = 270 , nextY1 = 270 , nextY2 = 270 , nextY3 = 270 , nextY4 = 270;
        //printing initial grid
        g.setColor(Color.WHITE);
        g.fillRect(1, 310, 700, 350);

        g.setColor(Color.BLACK);
        g.setFont(font4);
        g.drawRoundRect(50, 340, 600, 200, 10, 10);
        g.drawRoundRect(51, 341, 598, 198, 10, 10);
        g.drawLine(50, 380, 650, 380);
        g.drawLine(50, 378, 650, 378);
        g.drawLine(50, 379, 650, 379);
        g.drawLine(410, 350, 410, 540);
        g.drawLine(411, 340, 411, 540);
        g.drawLine(412, 340, 412, 540);
        g.drawLine(520, 340, 520, 540);
        g.drawLine(521, 340, 521, 540);
        g.drawLine(522, 340, 522, 540);

        g.setColor(Color.DARK_GRAY);
//        g.setFont(font5);
        g.drawString("Performance Measures", 120, 370);
		g.setFont(font3);
        // g.drawString("Mean number of customers with sever 4 ", 60, 510);
        // g.drawString("Mean number of customers with sever 3 ", 60, 480);
        // g.drawString("Mean number of customers with sever 2  ", 60, 450);
        // g.drawString("Mean number of customers with sever 1 ", 60, 420);
        // g.drawString("Mean No. of Customers in the System  ", 60, 390);
		// g.drawString("Average wait time in queue 1  ", 60, 540);
        // g.drawString("Average wait time in queue 2  ", 60, 570);
        // g.drawString("Average wait time in queue 3  ", 60, 600);
		// g.drawString("Average wait time in queue 4  ", 60, 630);
		// g.drawString("N/A", 560, 400);
        // g.drawString("N/A", 560, 430);
        // g.drawString("N/A", 560, 460);
        // g.drawString("N/A", 560, 490);
        // g.drawString("N/A", 560, 520);
        // g.drawString("N/A", 560, 540);
        // g.drawString("N/A", 560, 570);
        // g.drawString("N/A", 560, 600);
        // g.drawString("N/A", 560, 630);
        

        MaxTime = 20;
        sizeInterval = 4;
        MaxSize = 10;

        yscale = 1;
		
        MeanNoCustomersSys = 0;
        MeanNoCustomersQue = 0;
        MeanNoCustomersQue1 = 0;
        MeanNoCustomersQue2 = 0;
        MeanNoCustomersQue3 = 0;
        MeanNoCustomersQue4 = 0;
		MeanNoCustomersQueu1 = 0;
        MeanNoCustomersQueu2 = 0;
        MeanNoCustomersQueu3 = 0;
        MeanNoCustomersQueu4 = 0;

		customersEntered = 0;
		customersServed1 = 0;
		customersServed2 = 0;
		customersServed3 = 0;
		customersServed4 = 0;
        customersEntered = 0;
		int k;
		
        while (true) {
			
			if(server.getState()){
				serverNum.enable(true);
				parameterReqd.enable(false);
			}else{
				serverNum.enable(false);
				parameterReqd.enable(true);
			}
			// int netSize = queue0.size() + queue1.size + queue2.size() + queue3.size();
			// System.out.println("the system size is " + mainQueue.size() + " and time is " +time);
			// System.out.println("the sum of queues  is " + queue0.size() + " " +queue1.size()+ " " +queue2.size()+ " " +queue3.size());
        	
			loopTimes++;
			MeanNoCustomersSys = (SystemSize + MeanNoCustomersSys * (loopTimes - 1)) / loopTimes;
			MeanNoCustomersQue = (queue0.size() + MeanNoCustomersQue * (loopTimes - 1)) / loopTimes;
			MeanNoCustomersQue1 = (queue1.size() + MeanNoCustomersQue1 * (loopTimes - 1)) / loopTimes;
			MeanNoCustomersQue2 = (queue2.size() + MeanNoCustomersQue2 * (loopTimes - 1)) / loopTimes;
			MeanNoCustomersQue3 = (queue3.size() + MeanNoCustomersQue3 * (loopTimes - 1)) / loopTimes;
			MeanNoCustomersQue4 = (queue4.size() + MeanNoCustomersQue3 * (loopTimes - 1)) / loopTimes;
			if(queue1.size() > 0 ) MeanNoCustomersQueu1 = ((queue1.size() - 1) + MeanNoCustomersQueu1 * (loopTimes - 1)) / loopTimes;
			if(queue2.size() > 0 ) MeanNoCustomersQueu2 = ((queue2.size() - 1)+ MeanNoCustomersQueu2 * (loopTimes - 1)) / loopTimes;
			if(queue3.size() > 0 ) MeanNoCustomersQueu3 = ((queue3.size() - 1) + MeanNoCustomersQueu3 * (loopTimes - 1)) / loopTimes;
			if(queue4.size() > 0 ) MeanNoCustomersQueu4 = ((queue4.size() - 1) + MeanNoCustomersQueu4 * (loopTimes - 1)) / loopTimes;
			if (!playing) {
                try {
                    t.sleep(100);
                } catch (InterruptedException ie) {
                }
                continue;
            }

			Job e = mainQueue.pop();
			time = actualTime;
            actualTime = actualTime + e.time;
			timeSystem = timeSystem + e.timeOr;	
				
			if(queue1.size() > 0)customersInQ1  = queue1.size() - 1;
			else customersInQ1 = 0;
			if(queue2.size() > 0)customersInQ2  = queue2.size() - 1;
			else customersInQ2 = 0;
			if(queue3.size() > 0)customersInQ3  = queue3.size() - 1;
			else customersInQ3 = 0;
			if(queue4.size() > 0)customersInQ4  = queue4.size() - 1;
			else customersInQ4 = 0;
			SystemSize = queue1.size() + queue2.size() + queue3.size() + queue4.size();
			
            nextX   = (int) (600 * time / (MaxTime)) + 50;
            nextY   = 270 - (int) (170 * SystemSize / 10);
			nextY1  = 270 - (int) (170 * queue1.size() / 10);
			nextY2  = 270 - (int) (170 * queue2.size() / 10);
			nextY3  = 270 - (int) (170 * queue3.size() / 10);
			nextY4  = 270 - (int) (170 * queue4.size() / 10);
			System.out.println(queue4.size()+ "----------------------------------");
			
            points.addLast(new Point(prevX, nextY));
            points.addLast(new Point(nextX, nextY));
			points1.addLast(new Point(prevX, nextY1));
            points1.addLast(new Point(nextX, nextY1));
			points2.addLast(new Point(prevX, nextY2));
            points2.addLast(new Point(nextX, nextY2));
			points3.addLast(new Point(prevX, nextY3));
            points3.addLast(new Point(nextX, nextY3));
			points4.addLast(new Point(prevX, nextY4));
            points4.addLast(new Point(nextX, nextY4));
			
            prevX = nextX;


			//System.out.println("this is before grid");
            PaintInitialGrid(g);
			//System.out.println("this is after grid");
            
			if(queueGraph1.getState())DrawPath(g, points1 , Color.RED);
            if(queueGraph2.getState())DrawPath(g, points2 , Color.BLUE);
            if(queueGraph3.getState())DrawPath(g, points3 , Color.GREEN);
            if(queueGraph4.getState())DrawPath(g, points4 , new Color(255, 0, 255));
			if(queueGraphTotal.getState()) DrawPath(g, points , Color.BLACK);

		//update the time of other events in Queue

		for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
			Job temp = mainQueue.pop();
			double temptime = temp.time;
			temp.time = temptime - e.time;
			mainQueue.push(temp);
		}
		
		//take out the last event and make next event
		if(e.stage == 0){
			customersEntered++;
			queue0.pop();
			e = new Job(1 , GenerateTime(mu1) , time);
			nextJob = new Job(0 , GenerateTime(lambda), time);
			queue0.push(nextJob);
			if(queue1.size() == 0){
			k = 0;
				for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
					Job temp = mainQueue.getJob(qw);
					if(temp.time < e.time ) k = qw+1;
				}
			mainQueue.addJob(e , k);
			}
			k = 0;
			for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
				// System.out.println("--------------- " + qw+ " ---- " + mainQueue.size());
				Job temp = mainQueue.getJob(qw);
				if(temp.time < nextJob.time ) k = qw+1;
			}
			mainQueue.addJob(nextJob , k);
			queue1.push(e);
		}else if(e.stage == 1){
			timeBusy1 = timeBusy1 + e.timeOr;
			customersServed1++;
			queue1.pop();
			if(queue1.size() > 0) waitTime1 =  waitTime1 + (time - e.timeOr - e.timeWait);
			System.out.println(" this is 1" +waitTime1);
			waitCust1++;
			if(queue1.size() != 0){
				nextJob = queue1.getJob(0);
				k = 0;
				for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
					Job temp = mainQueue.getJob(qw);
					if(temp.time < nextJob.time ) k = qw+1;
				}
				mainQueue.addJob(nextJob , k);
			}
			double q = Math.random();
			if(q < prob3){
				e = new Job(2 , GenerateTime(mu2), time);
				if(queue2.size() == 0){
					k = 0;
					for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
						Job temp = mainQueue.getJob(qw);
						if(temp.time < e.time ) k = qw+1;
					}
					mainQueue.addJob(e , k);
				}
				queue2.push(e);
			}else{
				e = new Job(4 , GenerateTime(mu4), time);
				if(queue4.size() == 0){
					k = 0;
					for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
						Job temp = mainQueue.getJob(qw);
						if(temp.time < e.time ) k = qw+1;
					}
					mainQueue.addJob(e , k);
				}
				queue4.push(e);
			}
		}else if(e.stage == 2){
			timeBusy2 = timeBusy2 + e.timeOr;customersServed2++;
			queue2.pop();
			if(queue2.size() > 0) waitTime2 =  waitTime2 + time - e.timeOr - e.timeWait;
			waitCust2++;
			
			double q = Math.random();
			if(q < 1 - prob2){
				e = new Job(3 , GenerateTime(mu3), time);
				if(queue3.size() == 0){
					k = 0;
					for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
						Job temp = mainQueue.getJob(qw);
						if(temp.time < e.time ) k = qw+1;
					}
					mainQueue.addJob(e , k);
				}
				queue3.push(e);
			}else{
				e = new Job(2 , GenerateTime(mu2), time);
				if(queue2.size() == 0){
					k = 0;
					for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
						Job temp = mainQueue.getJob(qw);
						if(temp.time < e.time ) k = qw+1;
					}
					mainQueue.addJob(e , k);
				}
				queue2.push(e);
			}
			if(queue2.size() != 0){
				nextJob = queue2.getJob(0);
				k = 0;
				for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
					Job temp = mainQueue.getJob(qw);
					if(temp.time < nextJob.time ) k = qw+1;
				}
				mainQueue.addJob(nextJob , k);
			}
			
		}else if(e.stage == 3){
			timeBusy3 = timeBusy3 + e.timeOr;
			customersServed3++;
			queue3.pop();
			if(queue3.size() > 0) waitTime3 =  waitTime3 + time - e.timeOr - e.timeWait;
			waitCust3++;
			if(queue3.size() != 0){
				nextJob = queue3.getJob(0);
				k = 0;
				for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
					Job temp = mainQueue.getJob(qw);
					if(temp.time < nextJob.time ) k = qw+1;
				}
				mainQueue.addJob(nextJob , k);
			}
			double q = Math.random();
			if(q < prob){
				double pq = Math.random();
				if(pq < prob3){
					e = new Job(1 , GenerateTime(mu1), time);
					if(queue2.size() == 0){
						k = 0;
						for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
							Job temp = mainQueue.getJob(qw);
							if(temp.time < e.time ) k = qw+1;
						}
						mainQueue.addJob(e , k);
					}
					queue1.push(e);
				}else{
					e = new Job(4 , GenerateTime(mu4), time);
					if(queue4.size() == 0){
						k = 0;
						for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
							Job temp = mainQueue.getJob(qw);
							if(temp.time < e.time ) k = qw+1;
						}
						mainQueue.addJob(e , k);
					}
					queue4.push(e);
				}
			}
			
		}else if(e.stage == 4){
			timeBusy4 = timeBusy4 + e.timeOr;
			customersServed4++;
			queue4.pop();
			if(queue4.size() > 0) waitTime4 =  waitTime4 + time - e.timeOr - e.timeWait;
			waitCust4++;
			if(queue4.size() != 0){
				nextJob = queue4.getJob(0);
				k = 0;
				for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
					Job temp = mainQueue.getJob(qw);
					if(temp.time < nextJob.time ) k = qw+1;
				}
				mainQueue.addJob(nextJob , k);
			}
			double q = Math.random();
			if(q < prob4){
				e = new Job(3 , GenerateTime(mu3), time);
				if(queue3.size() == 0){
					k = 0;
					for(int qw = 0 ; qw < mainQueue.size() ; qw++ ){
						Job temp = mainQueue.getJob(qw);
						if(temp.time < e.time ) k = qw+1;
					}
					mainQueue.addJob(e , k);
				}queue3.push(e);
			}
		}


            try {
			if (lambda >1)
                t.sleep((int)(800/lambda));
            else t.sleep((int)(800));
            
			} catch (InterruptedException z) {
            }
        }

    }

    public void stop() {
        t.stop();
    }
}