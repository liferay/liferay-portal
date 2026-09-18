/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	buildDataMaskTree,
	buildToolChildren,
	buildToolWaves,
	filterDataMaskTree,
	getAssignedToolIds,
	getAvailableToolSets,
	getSelectedDataMaskExternalReferenceCodes,
	getSelectedTools,
	isSystemMask,
	required,
	toIdentifier,
	toODataStringLiteral,
	toToolId,
} from '../src/main/resources/META-INF/resources/js/utils';

import type {
	DataMask,
	DataMaskTypeKey,
	ProfileTool,
	ToolSet,
} from '../src/main/resources/META-INF/resources/js/types';

function createDataMask(
	key: DataMaskTypeKey,
	name: string,
	externalReferenceCode?: string
): DataMask {
	return {
		detectionRegex: '\\d+',
		externalReferenceCode,
		maskType: {key, name: key === 'system' ? 'System' : 'Custom'},
		name,
		replacementValue: '[X]',
	};
}

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

describe('buildDataMaskTree', () => {
	it('groups the masks by type with system first', () => {
		const tree = buildDataMaskTree([
			createDataMask('custom', 'Project Codename', 'CUSTOM_1'),
			createDataMask('system', 'Email Address', 'SYSTEM_1'),
			createDataMask('system', 'Phone Number', 'SYSTEM_2'),
		]);

		expect(tree).toEqual([
			{
				children: [
					{id: 'SYSTEM_1', name: 'Email Address'},
					{id: 'SYSTEM_2', name: 'Phone Number'},
				],
				id: 'maskType:system',
				name: 'System',
			},
			{
				children: [{id: 'CUSTOM_1', name: 'Project Codename'}],
				id: 'maskType:custom',
				name: 'Custom',
			},
		]);
	});

	it('omits a group without masks', () => {
		const tree = buildDataMaskTree([
			createDataMask('system', 'Email Address', 'SYSTEM_1'),
		]);

		expect(tree).toHaveLength(1);
		expect(tree[0].id).toBe('maskType:system');
	});

	it('omits masks without an external reference code', () => {
		const tree = buildDataMaskTree([
			createDataMask('system', 'Email Address', 'SYSTEM_1'),
			createDataMask('system', 'Phone Number'),
		]);

		expect(tree[0].children).toEqual([
			{id: 'SYSTEM_1', name: 'Email Address'},
		]);
	});
});

describe('filterDataMaskTree', () => {
	const tree = buildDataMaskTree([
		createDataMask('system', 'Email Address', 'SYSTEM_1'),
		createDataMask('system', 'Phone Number', 'SYSTEM_2'),
		createDataMask('custom', 'Project Codename', 'CUSTOM_1'),
	]);

	it('returns every group expanded when the query is empty', () => {
		expect(filterDataMaskTree(tree, '')).toEqual({
			expandedKeys: ['maskType:system', 'maskType:custom'],
			items: tree,
		});
	});

	it('keeps only the children matching the query, case-insensitively', () => {
		const {items} = filterDataMaskTree(tree, 'EMAIL');

		expect(items).toEqual([
			{
				children: [{id: 'SYSTEM_1', name: 'Email Address'}],
				id: 'maskType:system',
				name: 'System',
			},
		]);
	});

	it('expands only the groups with matches', () => {
		expect(filterDataMaskTree(tree, 'codename').expandedKeys).toEqual([
			'maskType:custom',
		]);
	});

	it('returns no items when nothing matches', () => {
		expect(filterDataMaskTree(tree, 'iban').items).toEqual([]);
	});
});

describe('getSelectedDataMaskExternalReferenceCodes', () => {
	const tree = buildDataMaskTree([
		createDataMask('system', 'Email Address', 'SYSTEM_1'),
		createDataMask('system', 'Phone Number', 'SYSTEM_2'),
		createDataMask('custom', 'Project Codename', 'CUSTOM_1'),
	]);

	it('returns the selected masks in tree order', () => {
		expect(
			getSelectedDataMaskExternalReferenceCodes(
				tree,
				new Set(['CUSTOM_1', 'SYSTEM_2'])
			)
		).toEqual(['SYSTEM_2', 'CUSTOM_1']);
	});

	it('ignores group keys added by a recursive parent selection', () => {
		expect(
			getSelectedDataMaskExternalReferenceCodes(
				tree,
				new Set(['maskType:system', 'SYSTEM_1', 'SYSTEM_2'])
			)
		).toEqual(['SYSTEM_1', 'SYSTEM_2']);
	});

	it('returns an empty list when nothing is selected', () => {
		expect(
			getSelectedDataMaskExternalReferenceCodes(tree, new Set())
		).toEqual([]);
	});
});

describe('required', () => {
	it('returns an error message for an empty value', () => {
		expect(required('')).toBe('this-field-is-required');
	});

	it('returns an error message for a whitespace-only value', () => {
		expect(required('   ')).toBe('this-field-is-required');
	});

	it('returns undefined for a non-empty value', () => {
		expect(required('summarize-page')).toBeUndefined();
	});
});

describe('toIdentifier', () => {
	it('lowercases the name', () => {
		expect(toIdentifier('Summarize')).toBe('summarize');
	});

	it('replaces spaces and symbols with single hyphens', () => {
		expect(toIdentifier('Summarize Page & Comments')).toBe(
			'summarize-page-comments'
		);
	});

	it('collapses consecutive separators', () => {
		expect(toIdentifier('summarize -- page')).toBe('summarize-page');
	});

	it('trims leading and trailing separators', () => {
		expect(toIdentifier('  Summarize Page!  ')).toBe('summarize-page');
	});

	it('keeps digits', () => {
		expect(toIdentifier('Top 10 Results')).toBe('top-10-results');
	});

	it('returns an empty string when nothing remains', () => {
		expect(toIdentifier('!!!')).toBe('');
	});
});

describe('toODataStringLiteral', () => {
	it('wraps the value in single quotes', () => {
		expect(toODataStringLiteral('custom')).toBe("'custom'");
	});

	it('escapes embedded single quotes by doubling them', () => {
		expect(toODataStringLiteral("O'Brien's")).toBe("'O''Brien''s'");
	});
});

describe('isSystemMask', () => {
	const dataMask = (key: string): DataMask => ({
		detectionRegex: '\\d+',
		maskType: {key: key as DataMask['maskType']['key'], name: key},
		name: 'mask',
		replacementValue: '[X]',
	});

	it('returns true for a system mask', () => {
		expect(isSystemMask(dataMask('system'))).toBe(true);
	});

	it('returns false for a custom mask', () => {
		expect(isSystemMask(dataMask('custom'))).toBe(false);
	});

	it('returns false when there is no mask', () => {
		expect(isSystemMask(null)).toBe(false);
	});
});

describe('buildToolChildren', () => {
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

describe('getAssignedToolIds', () => {
	it('collects the ids of the tools the profile carries', () => {
		expect(
			getAssignedToolIds([
				createProfileTool('user-management', 'getUserAccount'),
				createProfileTool('organizations', 'getOrganization'),
			])
		).toEqual(
			new Set([
				'user-management/getUserAccount',
				'organizations/getOrganization',
			])
		);
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

	it('keeps a tool set whose tools were not read', () => {
		expect(getAvailableToolSets(toolSets, [], new Map())).toEqual(toolSets);
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
			getSelectedTools(
				new Set([toToolId('mcp-server-v1.0', 'tool/with/slashes')])
			)
		).toEqual([
			{toolName: 'tool/with/slashes', toolSetName: 'mcp-server-v1.0'},
		]);
	});
});

describe('toToolId', () => {
	it('joins the tool set and the tool name', () => {
		expect(toToolId('user-management', 'getUserAccount')).toBe(
			'user-management/getUserAccount'
		);
	});
});
