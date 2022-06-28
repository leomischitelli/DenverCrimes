package it.polito.tdp.crimes.model;

import it.polito.tdp.crimes.db.EventsDao;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Model {
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private List<String> best; //cammino migliore
	
	public Model() {
		dao = new EventsDao();
		
	}
	
	public void creaGrafo(String categoria, int mese){
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo vertici
		Graphs.addAllVertices(this.grafo, dao.getVertici(categoria, mese));
		//aggiungo archi
		for(Adiacenza a : dao.getArchi(categoria, mese)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
		}
		
		System.out.println("Grafo creato!");
		System.out.println("Numero vertici: " + this.grafo.vertexSet().size());
		System.out.println("Numero archi: " + this.grafo.edgeSet().size());

	}

	public List<Adiacenza> getArchiMaggioriPesoMedio(){
		//scorro gli archi e calcolo il peso medio
		double pesoTot = 0.0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pesoTot += this.grafo.getEdgeWeight(e);
		}
		double avg = pesoTot / this.grafo.edgeSet().size();
		//scorro di nuovo e prendo i maggiori di avg
		List<Adiacenza> result = new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) > avg) {
				result.add(new Adiacenza(this.grafo.getEdgeSource(e), 
						this.grafo.getEdgeTarget(e), 
						(int) this.grafo.getEdgeWeight(e)));
			}
		}
		return result;
		
	}
	
	public List<String> calcolaPercorso(String sorgente ,String destinazione){
		best = new ArrayList<>();
		List<String> parziale = new ArrayList<>();
		parziale.add(sorgente); //parto necessariamente dalla sorgente
		cerca(parziale, destinazione); //livello 1
		return best;
	}

	private void cerca(List<String> parziale, String destinazione) {
		//condizione di terminazione
		if(parziale.get(parziale.size() - 1).equals(destinazione)) {
			//soluzione migliore?
			if(parziale.size() > best.size()) {
				best = new ArrayList<>(parziale);
			}
			return; //esco dalla ricorsione perche sono arrivato a destinazione
		}
		
		//scorro i vicini dell'ultimo inserito, esploro i cammini
		for(String v : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size() - 1))){
			if(!parziale.contains(v)) {
				parziale.add(v);
				cerca(parziale, destinazione);
				parziale.remove(parziale.size() - 1);
			}
		}
		
	}
	
	

}
