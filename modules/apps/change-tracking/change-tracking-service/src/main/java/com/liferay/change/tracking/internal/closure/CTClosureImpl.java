/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.closure;

import com.liferay.change.tracking.closure.CTClosure;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Preston Crary
 */
public class CTClosureImpl implements CTClosure {

	public CTClosureImpl(
		long ctCollectionId, Map<Node, Collection<Node>> closureMap) {

		_ctCollectionId = ctCollectionId;

		for (Map.Entry<Node, Collection<Node>> entry : closureMap.entrySet()) {
			Node node = entry.getKey();

			_nodeMap.putIfAbsent(node, node);

			for (Node childNode : entry.getValue()) {
				_nodeMap.putIfAbsent(childNode, childNode);
			}
		}

		int index = 0;

		for (Node node : _nodeMap.values()) {
			node.setIndex(index++);
		}

		int nodeCount = _nodeMap.size();

		ArrayDeque<Node> queue = new ArrayDeque<>(nodeCount);
		BitSet bitSet = new BitSet(nodeCount);

		for (Map.Entry<Node, Collection<Node>> entry : closureMap.entrySet()) {
			Node node = _nodeMap.get(entry.getKey());

			if (node.equals(Node.ROOT_NODE)) {
				for (Node childNode : entry.getValue()) {
					_rootNodes.add(_nodeMap.get(childNode));
				}

				continue;
			}

			Collection<Node> childNodes = entry.getValue();

			bitSet.clear();

			queue.addAll(childNodes);

			while (!queue.isEmpty()) {
				Collection<Node> grandchildNodes = closureMap.get(queue.poll());

				if (grandchildNodes != null) {
					for (Node grandchildNode : grandchildNodes) {
						grandchildNode = _nodeMap.get(grandchildNode);

						int grandchildIndex = grandchildNode.getIndex();

						if (!bitSet.get(grandchildIndex)) {
							bitSet.set(grandchildIndex);

							queue.add(grandchildNode);
						}
					}
				}
			}

			for (Node childNode : childNodes) {
				childNode = _nodeMap.get(childNode);

				if (bitSet.get(childNode.getIndex())) {
					continue;
				}

				childNode.addParentNode(node);

				node.addChildNode(childNode);
			}
		}

		_nodeMap.remove(Node.ROOT_NODE);
	}

	@Override
	public long getCTCollectionId() {
		return _ctCollectionId;
	}

	@Override
	public Map<Long, List<Long>> getChildPKsMap(
		long classNameId, long classPK) {

		Node node = _nodeMap.get(new Node(classNameId, classPK));

		if (node == null) {
			return Collections.emptyMap();
		}

		List<Node> childNodes = node.getChildNodes();

		if (childNodes.isEmpty()) {
			return Collections.emptyMap();
		}

		return _toMap(childNodes);
	}

	@Override
	public Map<Long, List<Long>> getParentPKsMap(
		long classNameId, long classPK) {

		Node node = _nodeMap.get(new Node(classNameId, classPK));

		if (node == null) {
			return Collections.emptyMap();
		}

		List<Node> parentNodes = node.getParentNodes();

		if (parentNodes.isEmpty()) {
			return Collections.emptyMap();
		}

		return _toMap(parentNodes);
	}

	@Override
	public Map<Long, List<Long>> getRootPKsMap() {
		if (_rootNodes.isEmpty()) {
			return Collections.emptyMap();
		}

		return _toMap(_rootNodes);
	}

	@Override
	public String toString() {
		StringBundler sb1 = new StringBundler();

		sb1.append("{\n");

		Map<Long, List<Long>> pksMap = getRootPKsMap();

		Deque<Map.Entry<Map.Entry<Long, ? extends Collection<Long>>, Integer>>
			queue = new LinkedList<>();

		for (Map.Entry<Long, ? extends Collection<Long>> entry :
				pksMap.entrySet()) {

			queue.add(new AbstractMap.SimpleImmutableEntry<>(entry, 1));
		}

		Map.Entry<Map.Entry<Long, ? extends Collection<Long>>, Integer>
			indentEntry = null;

		while ((indentEntry = queue.poll()) != null) {
			Map.Entry<Long, ? extends Collection<Long>> entry =
				indentEntry.getKey();

			long classNameId = entry.getKey();

			int indent = indentEntry.getValue();

			StringBundler sb2 = new StringBundler(indent);

			for (int i = 0; i < indent; i++) {
				sb2.append(CharPool.TAB);
			}

			String indentString = sb2.toString();

			for (long classPK : entry.getValue()) {
				sb1.append(indentString);
				sb1.append("(classNameId=");
				sb1.append(classNameId);
				sb1.append(", classPK=");
				sb1.append(classPK);
				sb1.append(")\n");

				Map<Long, ? extends Collection<Long>> childPKsMap =
					getChildPKsMap(classNameId, classPK);

				for (Map.Entry<Long, ? extends Collection<Long>> childEntry :
						childPKsMap.entrySet()) {

					queue.addFirst(
						new AbstractMap.SimpleImmutableEntry<>(
							childEntry, indent + 1));
				}
			}
		}

		sb1.append("}");

		return sb1.toString();
	}

	private Map<Long, List<Long>> _toMap(List<Node> nodes) {
		Map<Long, List<Long>> map = new HashMap<>();

		for (Node node : nodes) {
			List<Long> primaryKeys = map.computeIfAbsent(
				node.getClassNameId(), key -> new ArrayList<>());

			primaryKeys.add(node.getPrimaryKey());
		}

		return map;
	}

	private final long _ctCollectionId;
	private final Map<Node, Node> _nodeMap = new HashMap<>();
	private final List<Node> _rootNodes = new ArrayList<>();

}