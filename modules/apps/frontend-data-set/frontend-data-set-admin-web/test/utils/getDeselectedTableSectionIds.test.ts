/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getDeselectedTableSectionIds from '../../src/main/resources/META-INF/resources/js/utils/getDeselectedTableSectionIds';
import {
	IDataSetTableSection,
	IField,
	IFieldTreeItem,
} from '../../src/main/resources/META-INF/resources/js/utils/types';

const fieldTreeItems = [
	{label: 'Description', name: 'description'},
	{
		children: [{label: 'Name', name: 'creator.name'}],
		label: 'Creator',
		name: 'creator',
	},
	{label: 'Title', name: 'title'},
] as IFieldTreeItem[];

const tableSections = [
	{fieldName: 'creator.name', id: 1},
	{fieldName: 'customField', id: 2},
	{fieldName: 'description', id: 3},
	{fieldName: 'title', id: 4},
] as IDataSetTableSection[];

describe('getDeselectedTableSectionIds', () => {
	it('returns no IDs when every table section is still selected', () => {
		expect(
			getDeselectedTableSectionIds({
				fieldTreeItems,
				selectedFields: [
					{name: 'creator.name'},
					{name: 'description'},
					{name: 'title'},
				] as IField[],
				tableSections,
			})
		).toEqual([]);
	});

	it('returns the IDs of the deselected table sections, including nested fields', () => {
		expect(
			getDeselectedTableSectionIds({
				fieldTreeItems,
				selectedFields: [{name: 'title'}] as IField[],
				tableSections,
			})
		).toEqual(['1', '3']);
	});

	it('keeps table sections whose field is not in the data source', () => {
		expect(
			getDeselectedTableSectionIds({
				fieldTreeItems,
				selectedFields: [],
				tableSections,
			})
		).not.toContain('2');
	});
});
