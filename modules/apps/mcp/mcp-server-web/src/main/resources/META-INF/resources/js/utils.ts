/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';

import {
	DataMask,
	DataMaskTreeItem,
	DataMaskTypeKey,
	ProfileTool,
	ToolSet,
	ToolSummary,
	ToolTreeItem,
	TreeFilter,
	TreeItem,
} from './types';

type ToastMessageOptions = {
	dangerouslySetMessageHTML?: boolean;
};

const DATA_MASK_GROUP_ID_PREFIX = 'maskType:';

const DATA_MASK_GROUP_KEYS: DataMaskTypeKey[] = ['system', 'custom'];

const TOOL_ID_SEPARATOR = '/';

export function buildDataMaskTree(dataMasks: DataMask[]): DataMaskTreeItem[] {
	return DATA_MASK_GROUP_KEYS.flatMap((groupKey) => {
		const groupDataMasks = dataMasks.filter(
			(dataMask) =>
				dataMask.maskType?.key === groupKey &&
				dataMask.externalReferenceCode
		);

		if (!groupDataMasks.length) {
			return [];
		}

		const children = groupDataMasks.map((dataMask) => ({
			id: dataMask.externalReferenceCode as string,
			maskCount: 1,
			name: dataMask.name,
		}));

		return [
			{
				children,
				id: `${DATA_MASK_GROUP_ID_PREFIX}${groupKey}`,
				maskCount: children.length,
				name: groupDataMasks[0].maskType.name,
			},
		];
	});
}

export function buildToolChildren(
	toolSetName: string,
	tools: ToolSummary[],
	profileTools: ProfileTool[]
): ToolTreeItem[] {
	const assignedToolIds = getAssignedToolIds(profileTools);

	return tools
		.map((tool) => ({
			id: toToolId(toolSetName, tool.name),
			name: tool.name,
		}))
		.filter((toolTreeItem) => !assignedToolIds.has(toolTreeItem.id));
}

export function buildToolWaves<T extends {toolName: string}>(
	tools: T[]
): T[][] {
	const waves: T[][] = [];

	let remaining = tools;

	while (remaining.length) {
		const namesInWave = new Set<string>();
		const skipped: T[] = [];
		const wave: T[] = [];

		remaining.forEach((tool) => {
			if (namesInWave.has(tool.toolName)) {
				skipped.push(tool);
			}
			else {
				namesInWave.add(tool.toolName);
				wave.push(tool);
			}
		});

		waves.push(wave);

		remaining = skipped;
	}

	return waves;
}

export function filterDataMaskTree(
	tree: DataMaskTreeItem[],
	query: string
): TreeFilter {
	const loweredQuery = query.trim().toLowerCase();

	if (!loweredQuery) {
		return {
			expandedKeys: new Set(tree.map((group) => group.id)),
			matchCount: 0,
			visibleKeys: new Set<string>(),
		};
	}

	return filterTree(
		tree,
		(item) =>
			!item.children && item.name.toLowerCase().includes(loweredQuery)
	);
}

export function filterTree<T extends TreeItem<T>>(
	tree: T[],
	matches: (item: T) => boolean
): TreeFilter {
	const expandedKeys = new Set<string>();
	const visibleKeys = new Set<string>();

	const matchCount = collectVisibleKeys(
		tree,
		matches,
		expandedKeys,
		visibleKeys
	);

	return {expandedKeys, matchCount, visibleKeys};
}

export function getAssignedToolIds(profileTools: ProfileTool[]): Set<string> {
	return new Set(
		profileTools.map((profileTool) =>
			toToolId(profileTool.toolSetName, profileTool.toolName)
		)
	);
}

export function getAvailableToolSets(
	toolSets: ToolSet[],
	profileTools: ProfileTool[],
	toolsByToolSetName: Map<string, ToolSummary[] | null>
): ToolSet[] {
	return toolSets.filter((toolSet) => {
		if (!toolsByToolSetName.has(toolSet.name)) {
			return true;
		}

		const tools = toolsByToolSetName.get(toolSet.name);

		if (!tools) {
			return false;
		}

		return !!buildToolChildren(toolSet.name, tools, profileTools).length;
	});
}

export function getSelectedDataMaskExternalReferenceCodes(
	tree: DataMaskTreeItem[],
	selectedKeys: Set<string | number>
): string[] {
	return tree.flatMap((group) =>
		(group.children ?? [])
			.filter((child) => selectedKeys.has(child.id))
			.map((child) => child.id)
	);
}

export function getSelectedTools(
	selectedKeys: Set<string | number>
): Array<{toolName: string; toolSetName: string}> {
	return [...selectedKeys]
		.filter(
			(key): key is string =>
				typeof key === 'string' && key.includes(TOOL_ID_SEPARATOR)
		)
		.map(fromToolId);
}

export function isSystemMask(dataMask: DataMask | null): boolean {
	return dataMask?.maskType?.key === 'system';
}

export function isToolOfToolSet(
	key: string | number,
	toolSetName: string
): boolean {
	return String(key).startsWith(`${toolSetName}${TOOL_ID_SEPARATOR}`);
}

export function openErrorToast(
	message: string,
	options?: ToastMessageOptions
): void {
	openToast({
		message: toToastMessage(message, options),
		type: 'danger',
	});
}

export function openSuccessToast(
	message: string,
	options?: ToastMessageOptions
): void {
	openToast({
		message: toToastMessage(message, options),
		type: 'success',
	});
}

export function required(value: string): string | undefined {
	return value?.trim()
		? undefined
		: Liferay.Language.get('this-field-is-required');
}

export function toIdentifier(name: string): string {
	return name
		.toLowerCase()
		.replace(/[^a-z0-9]+/g, '-')
		.replace(/^-+|-+$/g, '');
}

export function toODataStringLiteral(value: string): string {
	return `'${value.replace(/'/g, "''")}'`;
}

export function toToolId(toolSetName: string, toolName: string): string {
	return `${toolSetName}${TOOL_ID_SEPARATOR}${toolName}`;
}

function collectVisibleKeys<T extends TreeItem<T>>(
	tree: T[],
	matches: (item: T) => boolean,
	expandedKeys: Set<string>,
	visibleKeys: Set<string>
): number {
	let matchCount = 0;

	for (const item of tree) {
		const childMatchCount = collectVisibleKeys(
			item.children ?? [],
			matches,
			expandedKeys,
			visibleKeys
		);

		if (childMatchCount) {
			expandedKeys.add(item.id);
		}

		const itemMatches = matches(item);

		if (itemMatches || childMatchCount) {
			visibleKeys.add(item.id);
		}

		matchCount += childMatchCount + Number(itemMatches);
	}

	return matchCount;
}

function fromToolId(id: string): {toolName: string; toolSetName: string} {
	const separatorIndex = id.indexOf(TOOL_ID_SEPARATOR);

	return {
		toolName: id.slice(separatorIndex + 1),
		toolSetName: id.slice(0, separatorIndex),
	};
}

function toToastMessage(
	message: string,
	options?: ToastMessageOptions
): string {
	if (options?.dangerouslySetMessageHTML) {
		return message;
	}

	return Liferay.Util.escapeHTML(message);
}
