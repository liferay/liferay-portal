/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {defaultLanguageId} from '../constants';
import {removeNewLine, replaceTabSpaces} from '../util/utils';
import {DEFAULT_LANGUAGE} from './constants';
import {
	parseActions,
	parseAssignments,
	parseNotifications,
	parseTimers,
} from './utils';
import XMLDefinition from './xmlDefinition';

export default function DeserializeUtil(content) {
	const instance = this;

	instance.definition = new XMLDefinition({
		value: content,
	});
}

DeserializeUtil.prototype = {
	getElements() {
		const instance = this;

		const elements = [];

		const nodesNames = [];

		instance.definition.forEachField((_, fieldData) => {
			fieldData.results.forEach((node) => {
				if (node.name) {
					nodesNames.push(node.name);
				}
				else if (node.id) {
					nodesNames.push(node.id);
				}
			});
		});

		instance.definition.forEachField((tagName, fieldData) => {
			fieldData.results.forEach((node) => {
				if (node.actions?.length) {
					node.notifications = node.actions.filter((item) => {
						if (item.template) {
							return item;
						}
					});

					node.actions = node.actions.filter((item) => {
						if (!item.template) {
							return item;
						}
					});
				}

				const position = {};
				let type = tagName;

				if (node.initial) {
					type = 'start';
				}

				const metadata = node.metadata && JSON.parse(node.metadata);

				if (
					metadata?.terminal ||
					(type === 'state' && !node.transitions && !metadata)
				) {
					type = 'end';
				}

				position.x = metadata?.xy[0] || Math.floor(Math.random() * 800);
				position.y = metadata?.xy[1] || Math.floor(Math.random() * 500);

				let label = {};

				if (Array.isArray(node.labels)) {
					node.labels?.map((itemLabel) => {
						Object.entries(itemLabel).map(([key, value]) => {
							label[key] = replaceTabSpaces(removeNewLine(value));
						});
					});
				}
				else {
					label = {[defaultLanguageId]: node.name};
				}

				const data = {
					description: node.description,
					label,
					script: node.script,
				};

				if (type === 'condition') {
					data.scriptLanguage =
						node.scriptLanguage || DEFAULT_LANGUAGE;
				}

				if (type === 'task') {
					if (node.assignments) {
						data.assignments = parseAssignments(node);
					}
					if (node.taskTimers) {
						data.taskTimers = parseTimers(node);
					}

					data.scriptLanguage =
						node.scriptLanguage || DEFAULT_LANGUAGE;
				}

				data.actions = node.actions?.length && parseActions(node);

				data.notifications =
					node.notifications?.length && parseNotifications(node);

				let nodeName;

				if (node.id) {
					nodeName = node.id;
				}
				else if (node.name) {
					nodeName = node.name;
				}
				else {
					return;
				}

				elements.push({
					data,
					id: nodeName,
					position,
					type,
				});

				const transitions = node.transitions;

				if (transitions) {
					let hasDefaultEdge = transitions?.find(
						(transition) => transition?.default === 'true'
					);

					const transitionsNames = [];

					transitions.forEach((transition) => {
						let label = {};

						if (Array.isArray(transition.labels)) {
							transition.labels?.map((itemLabel) => {
								Object.entries(itemLabel).map(
									([key, value]) => {
										label[key] = replaceTabSpaces(
											removeNewLine(value)
										);
									}
								);
							});
						}
						else {
							label = {[defaultLanguageId]: transition.name};
						}

						let transitionName;

						if (transition.id) {
							transitionName = transition.id;
						}
						else if (transition.name) {
							transitionName = transition.name;
						}
						else {
							return;
						}

						if (transitionsNames.includes(transitionName)) {
							transitionName = `${nodeName}_${transitionName}_${transition.target}`;
						}
						else {
							transitionsNames.push(transitionName);
						}
						const defaultEdge =
							transition?.default === 'true' || !hasDefaultEdge
								? true
								: false;

						elements.push({
							arrowHeadType: 'arrowclosed',
							data: {
								defaultEdge,
								label,
								name: transitionName,
							},
							id: uuidv4(),
							source: nodeName,
							target: transition.target,
							type: 'transition',
						});

						if (defaultEdge && !hasDefaultEdge) {
							hasDefaultEdge = true;
						}
					});
				}
			});
		});

		return elements;
	},

	getMetadata() {
		const instance = this;

		return instance.definition.getDefinitionMetadata();
	},

	updateXMLDefinition(content) {
		const instance = this;

		instance.definition = new XMLDefinition({
			value: content,
		});
	},
};
