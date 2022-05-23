package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;


public class Model {

	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate;
	private Map<Integer, Fermata> fermateIdMap;
	
	
	/**
	 * serve per popolare le comboBox dell'interfaccia grafica
	 * @return
	 */
	public List<Fermata> getFermate(){
		
		//se contiene già un dato non serve fare la query al database, se no si
		if(this.fermate==null) {
			MetroDAO dao=new MetroDAO();
			this.fermate=dao.getAllFermate();
			
			this.fermateIdMap=new HashMap<Integer, Fermata>();
			for(Fermata f: this.fermate) {
				fermateIdMap.put(f.getIdFermata(), f);
			}
		}
		
		return this.fermate;
	}
	
	/**
	 * metodo che serve al controller, dà le stazioni e cerca il percorso, cioè un elenco di fermate da passare per arrivare al punto di arrivo
	 * @param partenza
	 * @param arrivo
	 * @return
	 */
	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo){
		creaGrafo();  //crea e popola il grafo
		Map<Fermata, Fermata> alberoInverso=visitaGrafo(partenza);
		
		
		//Ricostruisco il percorso a partire dall'albero inverso 
		Fermata corrente=arrivo;
		List<Fermata> percorso=new ArrayList<>();    
		//finchè la fermata corrente non è arrivata alla partenza (che ho assegnato a null in visitaGrafo)
		while(corrente != null) { 
			percorso.add(0, corrente); //aggiungo  in testa per avere il percorso nel verso giusto
			corrente = alberoInverso.get(corrente);
		}
			
		
		return null;
	}
	
	
	private void creaGrafo() {
		//legge le informazioni dal database e popola il grafo
		
		//creo il grafico direttamente all'interno del metodo così se cambiano i dati riparte in automatico da zero
		this.grafo=new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		
		MetroDAO dao=new MetroDAO();
		
//		parte tolta e spostata nel metodo getFermate
//		List<Fermata> fermate=dao.getAllFermate();
//		Map<Integer, Fermata> fermateIdMap=new HashMap<Integer, Fermata>();
//		for(Fermata f: fermate) {
//			fermateIdMap.put(f.getIdFermata(), f);
//		}
		
		
//		Graphs.addAllVertices(this.grafo, fermate);  fermate è popolato solo se qualcuno ha chiamato il metodo getFermate prima
		Graphs.addAllVertices(this.grafo, getFermate());
	
		
        //MTETODO 1: ITERO SU OGNI COPPIA DI VERTICI (LENTO SE SONO TANTI)
		for(Fermata partenza: fermate) {
			for(Fermata arrivo: fermate) {
			//se esiste almeno una connessione tra partenza e arrivo aggiungo l'arco
				if(dao.isFermateConnesse(partenza, arrivo)) {   
					this.grafo.addEdge(partenza, arrivo);
				}
			}
		}

        //METODO 2 : DATO CIASCUN VERTICE, TROVA QUELLI A CUI è COLLEGATO 
		//Variante 2a: il DAO restituisce un elenco di ID numerici
		
		//Nota: posso iterare su 'fermate' oppure su 'this.grafo.vertexSet()'
		for(Fermata partenza:fermate) {
			List<Integer> idConnesse=dao.getIdFermateConnesse(partenza);
			for(Integer id: idConnesse) {
				//arrivo è la fermata che possiede questo id
				Fermata arrivo = null ;  
				for(Fermata f:fermate) {     //fermate=this.grafo.vertexSet()
					if(f.getIdFermata()==id) {
						arrivo=f;
					    break;
					}
				}
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		
		//METODO 2 : DATO CIASCUN VERTICE, TROVA QUELLI A CUI è COLLEGATO 
		//Variante 2b: il DAO restituisce un elenco di oggetti fermata
		for(Fermata partenza: fermate) {
			List<Fermata> arrivi =dao.getFermateConnesse(partenza);
			for(Fermata arrivo: arrivi) {
				this.grafo.addEdge(partenza, arrivo); //passo due oggetti di tipo fermata di cui il primo c'è già nel grafo, il secondo non ancora ma è uguale a un oggetto che già esiste
			}
		}
		
		
		//METODO 2 : DATO CIASCUN VERTICE, TROVA QUELLI A CUI è COLLEGATO 
		//Variante 2c: il DAO restituisce un elenco di ID numerici, che converto in oggetti
		//tramite una Map<Integer, Fermata> - "Identity Map"
		for(Fermata partenza: fermate) {
			List<Integer> idFermateConnesse = dao.getIdFermateConnesse(partenza);
			for(int id: idFermateConnesse) {
				Fermata arrivo=fermateIdMap.get(id);
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		
		//METODO 3 : FACCIO UNA SOLA QUERY CHE RESTITUISCE LE COPPIE DI FERMATE DA COLLEGARE
		//Varianti a, b, c (variante preferita è la 3c: usare Identity Map)
		List<CoppiaId> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaId coppia: fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()), fermateIdMap.get(coppia.getIdArrivo()));
		}
	
		
		System.out.println (this.grafo);
		System.out.println("Vertici: "+this.grafo.vertexSet().size());  //per stampare il numero di vertici
		System.out.println("Archi: "+this.grafo.edgeSet().size());
		
		visitaGrafo(fermate.get(0));
		
	}
	
	
	/**
	 * stampa l'elenco dei vertici raggiunti
	 * @param partenza
	 */
	public Map<Fermata, Fermata> visitaGrafo(Fermata partenza) {
		GraphIterator<Fermata, DefaultEdge> visita=new BreadthFirstIterator<>(this.grafo, partenza);  //visita in ampiezza per avere il numero minimo di archi
		
		Map<Fermata, Fermata> alberoInverso=new HashMap<Fermata, Fermata>();  //albero inverso perchè è l'albero di visita ma con le frecce all'insù (quindi non è più un albero)
		alberoInverso.put(partenza, null);  //definisco la radice dell'albero a null
		
		//salvo le informazioni importanti durante l'iterazione
		visita.addTraversalListener(new RegistraAlberoVisita(alberoInverso, this.grafo));
		while(visita.hasNext()) {
			Fermata f=visita.next();
//			System.out.println(f);
		}
		
		return alberoInverso;
			
	}
	
}
