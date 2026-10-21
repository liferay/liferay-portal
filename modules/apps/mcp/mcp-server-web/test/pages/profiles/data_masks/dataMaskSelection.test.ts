/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	buildDataMaskTree,
	filterDataMaskTree,
	getAvailableDataMasks,
	getNextExecutionOrder,
	getSelectedDataMaskExternalReferenceCodes,
} from '../../../../src/main/resources/META-INF/resources/js/pages/profiles/data_masks/dataMaskSelection';

import type {
	DataMask,
	DataMaskTypeKey,
} from '../../../../src/main/resources/META-INF/resources/js/types/DataMask';
import type {ProfileDataMaskRow} from '../../../../src/main/resources/META-INF/resources/js/types/ProfileDataMask';

function createRow(
	dataMaskExternalReferenceCode: string,
	executionOrder: number
): ProfileDataMaskRow {
	return {
		dataMaskExternalReferenceCode,
		description: '',
		executionOrder,
		externalReferenceCode: `ROW_${dataMaskExternalReferenceCode}`,
		id: executionOrder,
		name: dataMaskExternalReferenceCode,
		type: 'System',
	};
}

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
					{id: 'SYSTEM_1', maskCount: 1, name: 'Email Address'},
					{id: 'SYSTEM_2', maskCount: 1, name: 'Phone Number'},
				],
				id: 'maskType:system',
				maskCount: 2,
				name: 'System',
			},
			{
				children: [
					{id: 'CUSTOM_1', maskCount: 1, name: 'Project Codename'},
				],
				id: 'maskType:custom',
				maskCount: 1,
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
			{id: 'SYSTEM_1', maskCount: 1, name: 'Email Address'},
		]);
	});
});

describe('filterDataMaskTree', () => {
	const tree = buildDataMaskTree([
		createDataMask('system', 'Email Address', 'SYSTEM_1'),
		createDataMask('system', 'Phone Number', 'SYSTEM_2'),
		createDataMask('custom', 'Project Codename', 'CUSTOM_1'),
	]);

	it('returns every group expanded and nothing hidden when the query is empty', () => {
		expect(filterDataMaskTree(tree, '')).toEqual({
			expandedKeys: new Set(['maskType:system', 'maskType:custom']),
			matchCount: 0,
			visibleKeys: new Set(),
		});
	});

	it('keeps visible only the children matching the query, case-insensitively', () => {
		const {matchCount, visibleKeys} = filterDataMaskTree(tree, 'EMAIL');

		expect(visibleKeys).toEqual(new Set(['maskType:system', 'SYSTEM_1']));
		expect(matchCount).toBe(1);
	});

	it('expands only the groups with matches', () => {
		expect(filterDataMaskTree(tree, 'codename').expandedKeys).toEqual(
			new Set(['maskType:custom'])
		);
	});

	it('leaves nothing visible when nothing matches', () => {
		const {matchCount, visibleKeys} = filterDataMaskTree(tree, 'iban');

		expect(visibleKeys.size).toBe(0);
		expect(matchCount).toBe(0);
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

describe('getAvailableDataMasks', () => {
	const dataMasks = [
		createDataMask('system', 'Email Address', 'SYSTEM_1'),
		createDataMask('system', 'Phone Number', 'SYSTEM_2'),
		createDataMask('custom', 'Project Codename', 'CUSTOM_1'),
	];

	it('leaves out the masks the profile already uses', () => {
		expect(
			getAvailableDataMasks(dataMasks, [
				createRow('SYSTEM_2', 0),
				createRow('CUSTOM_1', 1),
			]).map((dataMask) => dataMask.externalReferenceCode)
		).toEqual(['SYSTEM_1']);
	});

	it('returns every mask when the profile uses none', () => {
		expect(getAvailableDataMasks(dataMasks, [])).toEqual(dataMasks);
	});
});

describe('getNextExecutionOrder', () => {
	it('follows the highest execution order in use', () => {
		expect(
			getNextExecutionOrder([
				createRow('SYSTEM_1', 3),
				createRow('CUSTOM_1', 1),
			])
		).toBe(4);
	});

	it('starts at one when the profile has no masks', () => {
		expect(getNextExecutionOrder([])).toBe(1);
	});
});
