package de.ganix.heizung;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class TemperatureChart
 */
@WebServlet("/temperaturechart")
public class TemperatureChart extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	private static byte[] imageBytes = new byte[0];

	private static Logger logger = LoggerFactory.getLogger(TemperatureChart.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TemperatureChart() {
        super();
    }

    public static synchronized void updateChart() {
		TimeSeriesCollection dataset = TemperatureHistory.getInstance().getDataSet();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            null,  // chart title
            "Zeit",
            "Temperatur °C",
            dataset,         // data
            true,            // include legend
            true,            // tooltips
            true             // urls
        );
        
		BufferedImage image = new BufferedImage(310 , 200, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		
		chart.draw(g2, new Rectangle2D.Double(0, 0, 310, 200), null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] theseImageBytes;
		try {
			ImageIO.write(image, "png", baos);
			theseImageBytes = baos.toByteArray();
		} catch (IOException e) {
			logger.error("cant create image", e);
			theseImageBytes = null;
		}
		
		synchronized(imageBytes) {
			imageBytes = theseImageBytes;
		}
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		byte[] theseImageBytes;
		synchronized(imageBytes) {
			theseImageBytes = imageBytes;
		}
		if(theseImageBytes != null) {
			response.getOutputStream().write(theseImageBytes);
		} else {
			logger.error("no image to serve!");
		}

		response.setHeader("Cache-Control", "public");
		response.setHeader("Pragma", "min-fresh = " + Config.getInstance().getInterval());
		response.setDateHeader("Expires", System.currentTimeMillis() + 1000 * (Config.getInstance().getInterval()));
		response.flushBuffer();
	}

}
