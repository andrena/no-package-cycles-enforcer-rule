package de.andrena.tools.nopackagecycles;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jdepend.framework.JavaPackage;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class PackageCycleCollector {

	public List<Set<JavaPackage>> collectCycles(List<JavaPackage> packages) {
		DirectedGraph<JavaPackage, DefaultEdge> graph = new DefaultDirectedGraph<JavaPackage, DefaultEdge>(
				DefaultEdge.class);
		addVerticesToGraph(packages, graph);
		addEdgesToGraph(packages, graph);
		return collectCycles(graph);
	}

	private List<Set<JavaPackage>> collectCycles(DirectedGraph<JavaPackage, DefaultEdge> graph) {
		List<Set<JavaPackage>> stronglyConnectedSets = new StrongConnectivityInspector<JavaPackage, DefaultEdge>(graph)
				.stronglyConnectedSets();
		removeSingletonSets(stronglyConnectedSets);
		return stronglyConnectedSets;
	}

	private void removeSingletonSets(List<Set<JavaPackage>> stronglyConnectedSets) {
		Iterator<Set<JavaPackage>> iterator = stronglyConnectedSets.iterator();
		while (iterator.hasNext()) {
			Set<JavaPackage> stronglyConnectedSet = iterator.next();
			if (stronglyConnectedSet.size() == 1) {
				iterator.remove();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addEdgesToGraph(List<JavaPackage> packages, DirectedGraph<JavaPackage, DefaultEdge> graph) {
		for (JavaPackage javaPackage : packages) {
			for (JavaPackage efferent : (Collection<JavaPackage>) javaPackage.getEfferents()) {
				graph.addEdge(javaPackage, efferent);
			}
		}
	}

	private void addVerticesToGraph(List<JavaPackage> packages, DirectedGraph<JavaPackage, DefaultEdge> graph) {
		for (JavaPackage javaPackage : packages) {
			graph.addVertex(javaPackage);
		}
	}

}
