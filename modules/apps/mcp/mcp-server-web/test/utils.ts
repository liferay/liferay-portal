/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	buildDataMaskTree,
	filterDataMaskTree,
	getSelectedDataMaskExternalReferenceCodes,
	isSystemMask,
	required,
	toIdentifier,
	toODataStringLiteral,
} from '../src/main/resources/META-INF/resources/js/utils';

import type {
	DataMask,
	DataMaskTypeKey,
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
