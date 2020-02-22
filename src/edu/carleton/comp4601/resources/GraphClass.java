package edu.carleton.comp4601.resources;

import java.io.Serializable;
import java.net.URL;

import javax.xml.bind.annotation.XmlRootElement;

import edu.uci.ics.crawler4j.url.WebURL;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.Multigraph;

@XmlRootElement
public class GraphClass implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //private Multigraph<Vertex, DefaultEdge> graph;
    private DirectedMultigraph<String, DefaultEdge> graph;

    public GraphClass() {
        //this.graph = new Multigraph<Vertex, DefaultEdge>(DefaultEdge.class);
        this.graph = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
    }

    public DirectedMultigraph<String, DefaultEdge> getGraph() {
        return graph;
    }
}