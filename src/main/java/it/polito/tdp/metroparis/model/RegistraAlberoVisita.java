package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class RegistraAlberoVisita implements TraversalListener<Fermata, DefaultEdge> {

	private Graph<Fermata, DefaultEdge> grafo;
	private Map<Fermata, Fermata> alberoInverso;
	
	
	public RegistraAlberoVisita(Map<Fermata, Fermata> alberoInverso, Graph<Fermata, DefaultEdge> grafo) {
		super();
		this.alberoInverso = alberoInverso;
		this.grafo=grafo;
	}

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
 /**
  * Attravresando un arco inserisco nella mappa alberoInverso il nuovo vertice raggiunto
  */
	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {   //e è l'evento non l'arco
		System.out.println(e.getEdge());
		
		Fermata source=this.grafo.getEdgeSource(e.getEdge());
		Fermata target=this.grafo.getEdgeTarget(e.getEdge());
		
		
        //Se source e target esistono già entrambi nella mappa non li devo aggiungere --> non faccio nulla
	
		//Se non contiene il target l'ho scoperto dal source
		if(!alberoInverso.containsKey(target)) {
			alberoInverso.put(target, source);
//			System.out.println(target+" si raaggiunge da "+source);
		} else {
			//Se non contiene il source l'ho scoperto dal target
			if(!alberoInverso.containsKey(source)) {
				alberoInverso.put(source, target);
//				System.out.println(source+" si raggiunge da "+target);
			}
		}
		
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> e) {
		// TODO Auto-generated method stub
		
	}

}
