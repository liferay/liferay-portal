/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';
import ReactFlow, {Controls, ReactFlowProvider} from 'react-flow-renderer';

import '../../css/main.scss';

import {createResourceURL} from 'frontend-js-web';

import EventObserver from '../util/EventObserver';
import {
	edgeTypes,
	getLayoutedElements,
	getNodeType,
	isCurrent,
	isVisited,
	nodeTypes,
} from '../util/util';
import CurrentNodes from './CurrentNodes';
import ErrorFeedback from './ErrorFeedback';

const eventObserver = new EventObserver();

let ReactFlowDefault = ReactFlow;

// `react-flow-renderer` provides both a commonjs and ESM version.
// We need this logic here so that both work. Unit tests rely on commonjs and
// our DXP runtime uses ESM.

if (ReactFlowDefault.default) {
	ReactFlowDefault = ReactFlowDefault.default;
}

export default function WorkflowInstanceTracker({
	baseResourceURL,
	workflowInstanceId,
}) {
	const [currentNodes, setCurrentNodes] = useState([]);
	const [definitionElements, setDefinitionElements] = useState({});
	const [filteredCurrentNodes, setFilteredCurrentNodes] = useState([]);
	const [nodes, setNodes] = useState([]);
	const [transitions, setTransitions] = useState([]);
	const [visitedNodes, setVisitedNodes] = useState([]);

	const languageId = themeDisplay.getLanguageId().replaceAll('_', '-');

	useEffect(() => {
		fetch(
			`/o/headless-admin-workflow/v1.0/workflow-instances/${workflowInstanceId}`,
			{method: 'GET'}
		)
			.then((response) => response.json())
			.then((data) => {
				setCurrentNodes(data.currentNodeNames);

				fetch(
					createResourceURL(baseResourceURL, {
						p_p_resource_id:
							'/workflow_metrics/get_workflow_definition_info',
						workflowDefinitionName: data.workflowDefinitionName,
						workflowDefinitionVersion:
							data.workflowDefinitionVersion,
					}),
					{
						headers: {
							'Accept-Language': languageId,
						},
					}
				)
					.then((response) => response.json())
					.then((data) => {
						setDefinitionElements({
							nodes: data.nodes,
							transitions: data.transitions,
						});
					});
			});

		fetch(
			`/o/headless-admin-workflow/v1.0/workflow-instances/${workflowInstanceId}/workflow-logs?types=NodeEntry`,
			{method: 'GET'}
		)
			.then((response) => response.json())
			.then((data) => {
				const visitedNodes = data.items.map((item) => item.state);

				setVisitedNodes(visitedNodes);
			});

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		if (definitionElements && visitedNodes) {
			const position = {x: 0, y: 0};
			const {nodes: nodeElements, transitions: transitionElements} =
				definitionElements;

			if (nodeElements?.length && transitionElements?.length) {
				const nodes = nodeElements.map((node) => {
					return {
						data: {
							current: isCurrent(currentNodes, node),
							done: isVisited(
								visitedNodes,
								transitionElements,
								node
							),
							initial: node.type === 'INITIAL_STATE',
							label: node.label,
							notifyVisibilityChange: (visible) => () => {
								eventObserver.notify(node.name, () => visible);
							},
						},
						id: node.name,
						position,
						type: getNodeType(node.type),
					};
				});

				setNodes(nodes);

				const transitions = transitionElements.map((transition) => {
					return {
						arrowHeadType: 'arrowclosed',
						data: {
							eventObserver,
							text: transition.label,
						},
						id: transition.name,
						source: transition.sourceNodeName,
						target: transition.targetNodeName,
						type: 'transition',
					};
				});

				setTransitions(transitions);
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [definitionElements, visitedNodes]);

	useEffect(() => {
		const filteredCurrentNodes = [];

		nodes.map((node) => {
			if (node.data.current) {
				filteredCurrentNodes.push(node.id);
			}
		});

		setFilteredCurrentNodes(filteredCurrentNodes);
	}, [nodes]);

	const elements = nodes.concat(transitions);

	const layoutedElements = getLayoutedElements(elements);

	const onLoad = (reactFlowInstance) => {
		reactFlowInstance.fitView();
	};

	if (!layoutedElements.length) {
		return <ErrorFeedback />;
	}

	return (
		<div className="workflow-instance-tracker">
			{!!layoutedElements.length && (
				<ReactFlowProvider>
					<ReactFlowDefault
						edgeTypes={edgeTypes}
						elements={layoutedElements}
						minZoom="0.1"
						nodeTypes={nodeTypes}
						onLoad={onLoad}
					/>

					<Controls showInteractive={false} />

					<CurrentNodes nodesNames={filteredCurrentNodes} />
				</ReactFlowProvider>
			)}
		</div>
	);
}
