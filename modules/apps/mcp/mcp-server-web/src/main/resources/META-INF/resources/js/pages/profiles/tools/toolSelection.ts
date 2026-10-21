/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ProfileTool} from '../../../types/ProfileTool';
import {ToolSet, ToolSummary} from '../../../types/Tool';

export type ToolTreeItem = {
	children?: ToolTreeItem[];
	id: string;
	name: string;
};

const TOOL_ID_SEPARATOR = '/';

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

export function isToolOfToolSet(
	key: string | number,
	toolSetName: string
): boolean {
	return String(key).startsWith(`${toolSetName}${TOOL_ID_SEPARATOR}`);
}

function fromToolId(id: string): {toolName: string; toolSetName: string} {
	const separatorIndex = id.indexOf(TOOL_ID_SEPARATOR);

	return {
		toolName: id.slice(separatorIndex + 1),
		toolSetName: id.slice(0, separatorIndex),
	};
}

function getAssignedToolIds(profileTools: ProfileTool[]): Set<string> {
	return new Set(
		profileTools.map((profileTool) =>
			toToolId(profileTool.toolSetName, profileTool.toolName)
		)
	);
}

function toToolId(toolSetName: string, toolName: string): string {
	return `${toolSetName}${TOOL_ID_SEPARATOR}${toolName}`;
}
