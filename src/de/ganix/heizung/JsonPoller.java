package de.ganix.heizung;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

/**
 * Servlet implementation class JsonPoller
 */
@WebServlet("/jsonpoller")
public class JsonPoller extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JsonPoller() {
        super();
    }

    public class State {
    	public String indoor;
    	public String outdoor;
    	public String switch1;
    	public String date;
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Temperature temp = new Temperature();
		Ausgang ausg = new Ausgang();
		
		State state = new State();
		state.date = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.GERMANY).format(new Date());
		state.indoor = formatTemperature(temp.getInnen());
		state.outdoor = formatTemperature(temp.getAussen());
		state.switch1 = ausg.getAusgang1();
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer();
		writer.writeValue(response.getOutputStream(), state);
	}

	private String formatTemperature(Float innen) {
		NumberFormat fmt = NumberFormat.getInstance(Locale.GERMANY);
		fmt.setMaximumFractionDigits(2);
		fmt.setMinimumFractionDigits(2);
		if(innen != null ) {
			return fmt.format(innen) + "°C";
		}
		return "n/a";
	}

}
