/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.tree;

import com.liferay.object.tree.constants.TreeConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class Tree {

	public Tree(Node rootNode) {
		this.rootNode = rootNode;
	}

	public Node fetchNode(long primaryKey) {
		Iterator<Node> iterator = iterator();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			if (node.getPrimaryKey() == primaryKey) {
				return node;
			}
		}

		return null;
	}

	public List<Edge> getAncestorEdges(long primaryKey) {
		Node node = getNode(primaryKey);

		if (node.isRoot()) {
			return Collections.emptyList();
		}

		List<Edge> edges = new ArrayList<>();

		Node parentNode = node.getParentNode();

		edges.add(node.getEdge());

		while (!parentNode.isRoot()) {
			edges.add(parentNode.getEdge());

			parentNode = parentNode.getParentNode();
		}

		return edges;
	}

	public int getHeight(Node node) {
		if (node == null) {
			return -1;
		}

		int maxHeight = -1;

		for (Node childNode : node.getChildNodes()) {
			maxHeight = Math.max(maxHeight, getHeight(childNode));
		}

		return maxHeight + 1;
	}

	public Node getNode(long primaryKey) {
		Node node = null;

		Iterator<Node> iterator = iterator();

		while (iterator.hasNext()) {
			node = iterator.next();

			if (node.getPrimaryKey() == primaryKey) {
				break;
			}
		}

		return node;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public Iterator<Node> iterator() {
		return iterator(TreeConstants.ITERATOR_TYPE_BREADTH_FIRST);
	}

	public Iterator<Node> iterator(long primaryKey) {
		return new BreadthFirstIterator(getNode(primaryKey));
	}

	public Iterator<Node> iterator(String iteratorType) {
		if (Objects.equals(
				iteratorType, TreeConstants.ITERATOR_TYPE_BREADTH_FIRST)) {

			return new BreadthFirstIterator(rootNode);
		}
		else if (Objects.equals(
					iteratorType, TreeConstants.ITERATOR_TYPE_POST_ORDER)) {

			return new PostOrderIterator(rootNode);
		}

		return null;
	}

	protected final Node rootNode;

}