package edu.carleton.comp4601.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/sda")
public class SearchableDocumentArchive {

	//This method is called if TEXT_PLAIN is requested
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public String sayPlainTextHello() {
			return "Searchable Document Archive - Murad Berhanu and Mustapha Attah";
		}
		
		//This method is called if HTML is requested
		@GET
		@Produces(MediaType.TEXT_HTML)
		public String sayHTMLHello() {
			return "<html>" + "<title>" + "SDA" + "</title>" + 
			"<body><h1>" + "Searchable Document Archive - Murad Berhanu and Mustapha Attah" + "</body></h1>" + "</html>";
		}
}
