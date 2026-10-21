/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	buildToolChildren,
	buildToolWaves,
	getAvailableToolSets,
	getSelectedTools,
	isToolOfToolSet,
} from '../../../../src/main/resources/META-INF/resources/js/pages/profiles/tools/toolSelection';

import type {ProfileTool} from '../../../../src/main/resources/META-INF/resources/js/types/ProfileTool';
import type {ToolSet} from '../../../../src/main/resources/META-INF/resources/js/types/Tool';

function createProfileTool(toolSetName: string, toolName: string): ProfileTool {
	return {
		externalReferenceCode: `${toolSetName}.${toolName}`,
		toolName,
		toolSetName,
	};
}

function createToolSets(toolSetNames: string[]): ToolSet[] {
	return toolSetNames.map((toolSetName) => ({name: toolSetName}));
}

function createTools(toolNames: string[]) {
	return toolNames.map((toolName) => ({name: toolName}));
}

describe('buildToolChildren', () => {
	it('identifies each tool by its tool set and its name', () => {
		expect(
			buildToolChildren(
				'user-management',
				createTools(['getUserAccount']),
				[]
			)
		).toEqual([
			{id: 'user-management/getUserAccount', name: 'getUserAccount'},
		]);
	});

	it('omits the tools the profile already carries', () => {
		expect(
			buildToolChildren(
				'user-management',
				createTools(['getUserAccount', 'createUserAccount']),
				[createProfileTool('user-management', 'getUserAccount')]
			)
		).toEqual([
			{
				id: 'user-management/createUserAccount',
				name: 'createUserAccount',
			},
		]);
	});

	it('keeps a tool the profile carries under another tool set', () => {
		expect(
			buildToolChildren(
				'user-management',
				createTools(['getUserAccount']),
				[createProfileTool('organizations', 'getUserAccount')]
			)
		).toEqual([
			{id: 'user-management/getUserAccount', name: 'getUserAccount'},
		]);
	});

	it('returns nothing when the profile carries every tool', () => {
		expect(
			buildToolChildren(
				'user-management',
				createTools(['getUserAccount']),
				[createProfileTool('user-management', 'getUserAccount')]
			)
		).toEqual([]);
	});
});

describe('buildToolWaves', () => {
	it('returns a single wave when every tool name is unique', () => {
		expect(
			buildToolWaves([
				{toolName: 'getOpenAPI', toolSetName: 'audit-v1.0'},
				{toolName: 'getSitesPage', toolSetName: 'headless-admin-v1.0'},
			])
		).toEqual([
			[
				{toolName: 'getOpenAPI', toolSetName: 'audit-v1.0'},
				{toolName: 'getSitesPage', toolSetName: 'headless-admin-v1.0'},
			],
		]);
	});

	it('spreads same-named tools across waves keeping one per wave', () => {
		expect(
			buildToolWaves([
				{toolName: 'getOpenAPI', toolSetName: 'audit-v1.0'},
				{toolName: 'getSitesPage', toolSetName: 'headless-admin-v1.0'},
				{toolName: 'getOpenAPI', toolSetName: 'admin-server-v1.0'},
				{toolName: 'getOpenAPI', toolSetName: 'openapi'},
			])
		).toEqual([
			[
				{toolName: 'getOpenAPI', toolSetName: 'audit-v1.0'},
				{toolName: 'getSitesPage', toolSetName: 'headless-admin-v1.0'},
			],
			[{toolName: 'getOpenAPI', toolSetName: 'admin-server-v1.0'}],
			[{toolName: 'getOpenAPI', toolSetName: 'openapi'}],
		]);
	});

	it('returns no waves for an empty selection', () => {
		expect(buildToolWaves([])).toEqual([]);
	});
});

describe('getAvailableToolSets', () => {
	const toolSets = createToolSets(['user-management', 'organizations']);

	it('omits a tool set whose every tool the profile carries', () => {
		expect(
			getAvailableToolSets(
				toolSets,
				[createProfileTool('user-management', 'getUserAccount')],
				new Map([['user-management', createTools(['getUserAccount'])]])
			)
		).toEqual(createToolSets(['organizations']));
	});

	it('keeps a tool set with a tool left to add', () => {
		expect(
			getAvailableToolSets(
				toolSets,
				[createProfileTool('user-management', 'getUserAccount')],
				new Map([
					[
						'user-management',
						createTools(['getUserAccount', 'createUserAccount']),
					],
				])
			)
		).toEqual(toolSets);
	});

	it('keeps a tool set the prefetch skipped', () => {
		expect(getAvailableToolSets(toolSets, [], new Map())).toEqual(toolSets);
	});

	it('omits a tool set whose prefetch failed', () => {
		expect(
			getAvailableToolSets(
				toolSets,
				[createProfileTool('user-management', 'getUserAccount')],
				new Map([['user-management', null]])
			)
		).toEqual(createToolSets(['organizations']));
	});
});

describe('getSelectedTools', () => {
	it('resolves the checked leaves back into tool set and tool names', () => {
		expect(
			getSelectedTools(
				new Set([
					'user-management/createUserAccount',
					'organizations/getOrganization',
				])
			)
		).toEqual([
			{toolName: 'createUserAccount', toolSetName: 'user-management'},
			{toolName: 'getOrganization', toolSetName: 'organizations'},
		]);
	});

	it('ignores the tool set keys the tree view also reports as selected', () => {
		expect(
			getSelectedTools(
				new Set(['organizations', 'organizations/getOrganization'])
			)
		).toEqual([
			{toolName: 'getOrganization', toolSetName: 'organizations'},
		]);
	});

	it('returns nothing when nothing is checked', () => {
		expect(getSelectedTools(new Set())).toEqual([]);
	});

	it('keeps a tool name containing the separator addressable', () => {
		expect(
			getSelectedTools(new Set(['mcp-server-v1.0/tool/with/slashes']))
		).toEqual([
			{toolName: 'tool/with/slashes', toolSetName: 'mcp-server-v1.0'},
		]);
	});
});

describe('isToolOfToolSet', () => {
	it('recognizes a tool key of the tool set', () => {
		expect(
			isToolOfToolSet('user-management/getUserAccount', 'user-management')
		).toBe(true);
	});

	it('rejects the tool set key itself and the keys of other tool sets', () => {
		expect(isToolOfToolSet('user-management', 'user-management')).toBe(
			false
		);
		expect(
			isToolOfToolSet('organizations/getOrganization', 'user-management')
		).toBe(false);
	});
});
