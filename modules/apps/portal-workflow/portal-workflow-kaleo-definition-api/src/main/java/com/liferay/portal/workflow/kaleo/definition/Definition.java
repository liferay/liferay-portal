/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition;

import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class Definition {

	public Definition(
		String name, String description, String content, int version) {

		_name = name;
		_description = description;
		_content = content;
		_version = version;
	}

	public void addNode(Node node) throws KaleoDefinitionValidationException {
		if (_nodesMap.containsKey(node.getName())) {
			throw new KaleoDefinitionValidationException.DuplicateNode(
				node.getDefaultLabel());
		}

		_nodesMap.put(node.getName(), node);

		if (node instanceof State) {
			State state = (State)node;

			if (state.isInitial()) {
				_initialState = state;
			}
		}
		else if (node instanceof Fork) {
			_forks.add((Fork)node);
		}
		else if (node instanceof Join) {
			_joins.add((Join)node);
		}
	}

	public String getContent() {
		return _content;
	}

	public String getDescription() {
		return _description;
	}

	public List<Fork> getForks() {
		return Collections.unmodifiableList(_forks);
	}

	public int getForksCount() {
		return _forks.size();
	}

	public State getInitialState() {
		return _initialState;
	}

	public List<Join> getJoins() {
		return Collections.unmodifiableList(_joins);
	}

	public int getJoinsCount() {
		return _joins.size();
	}

	public String getName() {
		return _name;
	}

	public Node getNode(String name) {
		return _nodesMap.get(name);
	}

	public Collection<Node> getNodes() {
		return Collections.unmodifiableCollection(_nodesMap.values());
	}

	public List<State> getTerminalStates() {
		if (_terminalStates == null) {
			_terminalStates = new ArrayList<>();

			for (Node node : _nodesMap.values()) {
				if (!(node instanceof State)) {
					continue;
				}

				State state = (State)node;

				if (!state.isTerminal()) {
					continue;
				}

				_terminalStates.add(state);
			}
		}

		return Collections.unmodifiableList(_terminalStates);
	}

	public int getVersion() {
		return _version;
	}

	public boolean hasNode(String name) {
		return _nodesMap.containsKey(name);
	}

	private final String _content;
	private final String _description;
	private final List<Fork> _forks = new ArrayList<>();
	private State _initialState;
	private final List<Join> _joins = new ArrayList<>();
	private final String _name;
	private final Map<String, Node> _nodesMap = new HashMap<>();
	private List<State> _terminalStates;
	private final int _version;

}