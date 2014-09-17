import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;

public class SimulationCanvas extends Canvas                 // ye to shayad standars hai
{
	private Image image;
	private int width;
	private int height;

	public SimulationCanvas(Image image)
	{
		super();
		this.image = image;
		this.width = this.image.getWidth(null);
		this.height = this.image.getHeight(null);
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	public void paint(Graphics g)
	{
		g.drawImage(image, 0, 0, width, height, null);
	}
}
