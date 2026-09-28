/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getObjectFields from '../../../src/main/resources/META-INF/resources/js/edit_field_mappings/getObjectFields';

const OBJECT_DEFINITIONS = [
	{
		className: 'com.liferay.object.model.ObjectDefinition#P4R4',
		label: 'PIM Base SKU',
		objectFields: [
			{label: 'Name', name: 'name'},
			{label: 'Code', name: 'code'},
		],
	},
	{
		className: 'com.liferay.object.model.ObjectDefinition#S1Z3',
		label: 'PIM Shirt',
		objectFields: [
			{label: 'Code', name: 'code'},
			{label: 'Size', name: 'size'},
		],
	},
];

describe('getObjectFields', () => {
	it('returns every structure field, deduplicated and sorted by label, when no source is selected', () => {
		expect(getObjectFields(OBJECT_DEFINITIONS, '')).toEqual([
			{label: 'Code', name: 'code'},
			{label: 'Name', name: 'name'},
			{label: 'Size', name: 'size'},
		]);
	});

	it('returns only the fields of the selected structure', () => {
		expect(
			getObjectFields(
				OBJECT_DEFINITIONS,
				'com.liferay.object.model.ObjectDefinition#S1Z3'
			)
		).toEqual([
			{label: 'Code', name: 'code'},
			{label: 'Size', name: 'size'},
		]);
	});

	it('returns nothing when the selected structure is gone', () => {
		expect(
			getObjectFields(
				OBJECT_DEFINITIONS,
				'com.liferay.object.model.ObjectDefinition#G0N3'
			)
		).toEqual([]);
	});
});
