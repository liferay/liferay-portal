/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.tree.util;

import com.liferay.oauth2.provider.web.internal.tree.Tree;
import com.liferay.petra.string.StringBundler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedAcyclicGraph;

/**
 * @author Carlos Sierra Andrés
 */
public class TreeUtil {

	public static <T> Tree.Node<T> getTreeNode(
		Set<T> set, T rootValue, BiPredicate<T, T> biPredicate) {

		DirectedAcyclicGraph<T, String> directedAcyclicGraph =
			new DirectedAcyclicGraph<>(String.class);

		for (T vertex1 : set) {
			directedAcyclicGraph.addVertex(vertex1);

			for (T vertex2 : set) {
				if (Objects.equals(vertex1, vertex2) ||
					!biPredicate.test(vertex1, vertex2)) {

					continue;
				}

				directedAcyclicGraph.addVertex(vertex2);

				directedAcyclicGraph.addEdge(
					vertex1, vertex2,
					StringBundler.concat(vertex1, "#", vertex2));
			}
		}

		Set<T> endingVertices = new HashSet<>();
		Set<T> initialVertices = new HashSet<>();

		for (T vertex : set) {
			if (directedAcyclicGraph.outDegreeOf(vertex) == 0) {
				endingVertices.add(vertex);
			}

			if (directedAcyclicGraph.inDegreeOf(vertex) != 0) {
				continue;
			}

			initialVertices.add(vertex);
		}

		_filterRedundantPaths(
			directedAcyclicGraph, initialVertices, endingVertices);

		return _createTreeNode(
			directedAcyclicGraph, rootValue, initialVertices);
	}

	private static <T, E> Tree<T> _createTree(Graph<T, E> graph, T value) {
		if (graph.outDegreeOf(value) == 0) {
			return new Tree.Leaf<>(value);
		}

		Set<T> set = new HashSet<>();

		for (E edge : graph.outgoingEdgesOf(value)) {
			set.add(graph.getEdgeTarget(edge));
		}

		return _createTreeNode(graph, value, set);
	}

	private static <T> Tree.Node<T> _createTreeNode(
		Graph<T, ?> graph, T value, Set<T> set) {

		List<Tree<T>> trees = new ArrayList<>();

		for (T childValue : set) {
			trees.add(_createTree(graph, childValue));
		}

		return new Tree.Node<>(value, trees);
	}

	private static <T> void _filterRedundantPaths(
		DirectedAcyclicGraph<T, String> directedAcyclicGraph,
		Set<T> initialVertices, Set<T> endingVertices) {

		AllDirectedPaths<T, String> allDirectedPaths = new AllDirectedPaths<>(
			directedAcyclicGraph);

		List<GraphPath<T, String>> allGraphPaths = allDirectedPaths.getAllPaths(
			initialVertices, endingVertices, true, null);

		Comparator<GraphPath<?, ?>> comparator = Comparator.comparingInt(
			GraphPath::getLength);

		allGraphPaths.sort(comparator.reversed());

		HashMap<String, Set<String>> visitedEdgesMap = new HashMap<>();
		HashMap<String, Set<T>> visitedVerticesMap = new HashMap<>();

		for (GraphPath<T, String> graphPath : allGraphPaths) {
			String pathKey = StringBundler.concat(
				graphPath.getStartVertex(), "#", graphPath.getEndVertex());

			Set<String> visitedEdgesSet = visitedEdgesMap.computeIfAbsent(
				pathKey, key -> new HashSet<>());
			Set<T> visitedVerticesSet = visitedVerticesMap.computeIfAbsent(
				pathKey, key -> new HashSet<>());

			if (visitedVerticesSet.containsAll(graphPath.getVertexList())) {
				for (String edge : graphPath.getEdgeList()) {
					if (visitedEdgesSet.contains(edge)) {
						continue;
					}

					directedAcyclicGraph.removeEdge(edge);
				}

				continue;
			}

			visitedEdgesSet.addAll(graphPath.getEdgeList());
			visitedVerticesSet.addAll(graphPath.getVertexList());
		}
	}

}