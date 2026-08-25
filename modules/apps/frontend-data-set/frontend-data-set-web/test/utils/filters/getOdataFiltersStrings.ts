/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EEntityFieldType} from '../../../src/main/resources/META-INF/resources/management_bar/controls/filters/utils/types';
import {getOdataFiltersStrings} from '../../../src/main/resources/META-INF/resources/utils/filters/getOdataFiltersStrings';
import {IConnectedFDSState} from '../../../src/main/resources/META-INF/resources/utils/filters/types';
import {
	IBaseFilterState,
	IFDSState,
} from '../../../src/main/resources/META-INF/resources/utils/types';

const selectionFilter = (
	id: string,
	selectedItems: Array<{value: string}>,
	active = true
) =>
	({
		active,
		enabled: true,
		entityFieldType: EEntityFieldType.STRING,
		id,
		label: id,
		multiple: false,
		preloadedData: {},
		selectedData: {exclude: false, selectedItems},
		selectedItemsLabel: '',
		type: 'selection',
	}) as unknown as IBaseFilterState;

describe('getOdataFiltersStrings', () => {
	it('returns one expression per active configured filter', () => {
		const fdsState: IFDSState = {
			filters: [
				selectionFilter('status', [{value: 'approved'}]),
				selectionFilter('author', [{value: 'joe'}], false),
			],
			search: {query: ''},
		};

		expect(getOdataFiltersStrings(fdsState)).toEqual([
			"status eq 'approved'",
		]);
	});

	it('replaces the configured filters with the ones a connection owns', () => {
		const fdsState: IConnectedFDSState = {
			connectionFilters: [
				{id: 'status', odataFilterString: "status eq 'draft'"},
				{id: 'author', odataFilterString: "author eq 'joe'"},
			],
			filters: [selectionFilter('status', [{value: 'approved'}])],
			search: {query: ''},
		};

		expect(getOdataFiltersStrings(fdsState)).toEqual([
			"status eq 'draft'",
			"author eq 'joe'",
		]);
	});

	it('filters out empty expressions', () => {
		const fdsState: IConnectedFDSState = {
			connectionFilters: [
				{id: 'status', odataFilterString: ''},
				{id: 'author', odataFilterString: "author eq 'joe'"},
			],
			filters: [],
			search: {query: ''},
		};

		expect(getOdataFiltersStrings(fdsState)).toEqual(["author eq 'joe'"]);
	});

	it('sends no filter when a connection owns an empty set', () => {
		const fdsState: IConnectedFDSState = {
			connectionFilters: [],
			filters: [selectionFilter('status', [{value: 'approved'}])],
			search: {query: ''},
		};

		expect(getOdataFiltersStrings(fdsState)).toEqual([]);
	});

	it('reports a type error when the data set writes the connection slice', () => {
		const fdsState: IFDSState = {

			// @ts-expect-error TS2353: 'connectionFilters' does not exist in type 'IFDSState'

			connectionFilters: [{id: 'status', odataFilterString: ''}],
			filters: [],
			search: {query: ''},
		};

		expect(getOdataFiltersStrings(fdsState)).toEqual([]);
	});
});
