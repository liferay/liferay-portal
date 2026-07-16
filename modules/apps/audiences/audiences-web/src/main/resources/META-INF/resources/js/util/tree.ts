/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {getOperators} from '../constants/operators';
import {
	AudiencesCriteria,
	CriteriaNode,
	Group,
	Rule,
	SerializedGroup,
	SerializedRule,
} from '../types';

export const MAX_DEPTH = 3;

export function isGroup(node: CriteriaNode): node is Group {
	return Array.isArray((node as Group).items);
}

export function createRule(audiencesCriteria: AudiencesCriteria): Rule {
	return {
		attribute: audiencesCriteria.key,
		id: `rule-${uuidv4()}`,
		operator:
			getOperators(
				audiencesCriteria.inputType,
				audiencesCriteria.type
			)[0] || '',
		value: audiencesCriteria.options[0]?.value || '',
	};
}

export function createGroup(
	conjunction = 'AND',
	items: CriteriaNode[] = []
): Group {
	return {conjunction, id: `group-${uuidv4()}`, items};
}

export function cloneNode(node: CriteriaNode): CriteriaNode {
	if (isGroup(node)) {
		return {
			...node,
			id: `group-${uuidv4()}`,
			items: node.items.map(cloneNode),
		};
	}

	return {...node, id: `rule-${uuidv4()}`};
}

export function parseGroup(serialized: SerializedGroup): Group {
	return {
		conjunction: serialized.conjunction || 'AND',
		id: `group-${uuidv4()}`,
		items: (serialized.rules ?? [])
			.filter(
				(node) => isSerializedGroup(node) || Boolean(node.attribute)
			)
			.map((node) =>
				isSerializedGroup(node)
					? parseGroup(node)
					: {
							attribute: node.attribute,
							id: `rule-${uuidv4()}`,
							operator: node.operator,
							value: node.value,
						}
			),
	};
}

export function parseRootGroup(serialized?: SerializedGroup): Group {
	return serialized ? parseGroup(serialized) : createGroup();
}

export function serializeGroup(group: Group): SerializedGroup {
	return {
		conjunction: group.conjunction,
		rules: group.items.map((node) =>
			isGroup(node)
				? serializeGroup(node)
				: {
						attribute: node.attribute,
						operator: node.operator,
						value: node.value,
					}
		),
	};
}

export function addRule(
	root: Group,
	groupPath: number[],
	audiencesCriteria: AudiencesCriteria,
	index?: number
): Group {
	return updateGroup(root, groupPath, (group) => ({
		...group,
		items: insertAt(
			group.items,
			index ?? group.items.length,
			createRule(audiencesCriteria)
		),
	}));
}

export function deleteRule(root: Group, path: number[]): Group {
	const [groupPath, index] = splitPath(path);

	return updateGroup(root, groupPath, (group) => ({
		...group,
		items: group.items.filter((_node, itemIndex) => itemIndex !== index),
	}));
}

export function duplicateRule(root: Group, path: number[]): Group {
	const [groupPath, index] = splitPath(path);

	return updateGroup(root, groupPath, (group) => ({
		...group,
		items: insertAt(group.items, index + 1, cloneNode(group.items[index])),
	}));
}

export function updateRule(root: Group, path: number[], rule: Rule): Group {
	const [groupPath, index] = splitPath(path);

	return updateGroup(root, groupPath, (group) => ({
		...group,
		items: group.items.map((node, itemIndex) =>
			itemIndex === index ? rule : node
		),
	}));
}

export function setConjunction(
	root: Group,
	groupPath: number[],
	conjunction: string
): Group {
	return updateGroup(root, groupPath, (group) => ({
		...group,
		conjunction,
	}));
}

export function reorderGroup(
	root: Group,
	groupPath: number[],
	items: CriteriaNode[]
): Group {
	return updateGroup(root, groupPath, (group) => ({...group, items}));
}

export function moveRule(
	root: Group,
	nodeId: string,
	targetGroupId: string,
	targetIndex: number
): Group {
	const node = findNode(root, nodeId);

	if (!node) {
		return root;
	}

	const targetGroup = findGroup(root, targetGroupId);

	const currentIndex = targetGroup
		? targetGroup.items.findIndex((item) => item.id === nodeId)
		: -1;

	const index =
		currentIndex !== -1 && currentIndex < targetIndex
			? targetIndex - 1
			: targetIndex;

	return unwrapRedundantGroups(
		deleteEmptyGroups(
			insertIntoGroup(
				detachNode(root, nodeId),
				targetGroupId,
				node,
				index
			)
		)
	);
}

export function moveGroup(
	root: Group,
	nodeId: string,
	targetId: string,
	conjunction = 'AND'
): Group {
	const node = findNode(root, nodeId);

	if (!node || nodeId === targetId) {
		return root;
	}

	const detached = detachNode(root, nodeId);
	const parent = findParent(detached, targetId);

	if (parent && parent.items.length === 1) {
		return unwrapRedundantGroups(
			deleteEmptyGroups(insertIntoGroup(detached, parent.id, node, 1))
		);
	}

	return unwrapRedundantGroups(
		deleteEmptyGroups(wrapNode(detached, targetId, node, conjunction))
	);
}

export function addGroup(
	root: Group,
	targetId: string,
	audiencesCriteria: AudiencesCriteria,
	conjunction = 'AND'
): Group {
	const rule = createRule(audiencesCriteria);

	const parent = findParent(root, targetId);

	if (parent && parent.items.length === 1) {
		return insertIntoGroup(root, parent.id, rule, 1);
	}

	return unwrapRedundantGroups(wrapNode(root, targetId, rule, conjunction));
}

export function deleteEmptyGroups(group: Group): Group {
	return {
		...group,
		items: group.items
			.map((node) => (isGroup(node) ? deleteEmptyGroups(node) : node))
			.filter((node) => !isGroup(node) || !!node.items.length),
	};
}

export function unwrapRedundantGroups(group: Group): Group {
	const items = group.items.map((node) =>
		isGroup(node) ? unwrapGroup(node) : node
	);

	const [onlyNode] = items;

	if (items.length === 1 && isGroup(onlyNode)) {
		return {
			...group,
			conjunction: onlyNode.conjunction,
			items: onlyNode.items,
		};
	}

	return {...group, items};
}

function unwrapGroup(group: Group): CriteriaNode {
	const items = group.items.map((node) =>
		isGroup(node) ? unwrapGroup(node) : node
	);

	if (items.length === 1) {
		return items[0];
	}

	return {...group, items};
}

export function canGroupNode(path: number[]): boolean {
	return path.length < MAX_DEPTH;
}

function findNode(group: Group, id: string): CriteriaNode | undefined {
	for (const node of group.items) {
		if (node.id === id) {
			return node;
		}

		if (isGroup(node)) {
			const found = findNode(node, id);

			if (found) {
				return found;
			}
		}
	}

	return undefined;
}

function findGroup(root: Group, groupId: string): Group | undefined {
	if (root.id === groupId) {
		return root;
	}

	const node = findNode(root, groupId);

	return node && isGroup(node) ? node : undefined;
}

function findParent(group: Group, id: string): Group | undefined {
	for (const node of group.items) {
		if (node.id === id) {
			return group;
		}

		if (isGroup(node)) {
			const found = findParent(node, id);

			if (found) {
				return found;
			}
		}
	}

	return undefined;
}

function insertAt<T>(items: T[], index: number, item: T): T[] {
	const next = [...items];

	next.splice(index, 0, item);

	return next;
}

function insertIntoGroup(
	group: Group,
	groupId: string,
	node: CriteriaNode,
	index: number
): Group {
	if (group.id === groupId) {
		return {...group, items: insertAt(group.items, index, node)};
	}

	return {
		...group,
		items: group.items.map((child) =>
			isGroup(child)
				? insertIntoGroup(child, groupId, node, index)
				: child
		),
	};
}

function wrapNode(
	group: Group,
	targetId: string,
	node: CriteriaNode,
	conjunction: string
): Group {
	return {
		...group,
		items: group.items.map((child) => {
			if (child.id === targetId) {
				return createGroup(conjunction, [child, node]);
			}

			return isGroup(child)
				? wrapNode(child, targetId, node, conjunction)
				: child;
		}),
	};
}

export function isSerializedGroup(
	node: SerializedGroup | SerializedRule
): node is SerializedGroup {
	return Array.isArray((node as SerializedGroup).rules);
}

function detachNode(group: Group, id: string): Group {
	return {
		...group,
		items: group.items
			.filter((node) => node.id !== id)
			.map((node) => (isGroup(node) ? detachNode(node, id) : node)),
	};
}

function splitPath(path: number[]): [number[], number] {
	if (!path.length) {
		throw new Error('Path cannot be empty');
	}

	return [path.slice(0, -1), path[path.length - 1]];
}

function updateGroup(
	root: Group,
	groupPath: number[],
	updater: (group: Group) => Group
): Group {
	if (!groupPath.length) {
		return updater(root);
	}

	const [head, ...rest] = groupPath;

	return {
		...root,
		items: root.items.map((node, index) => {
			if (index !== head) {
				return node;
			}

			if (!isGroup(node)) {
				throw new Error(
					`The path segment ${head} does not point to a group`
				);
			}

			return updateGroup(node, rest, updater);
		}),
	};
}
