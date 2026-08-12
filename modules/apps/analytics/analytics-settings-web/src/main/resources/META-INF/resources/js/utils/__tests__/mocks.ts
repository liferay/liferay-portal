/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TTableRequestParams} from '../../components/table/types';
import {serializeTableRequestParams} from '../../components/table/utils';
import request from '../request';

export const fetchPropertiesResponse = {
	actions: {},
	facets: [],
	items: [
		{
			channelId: '591043793166298694',
			dataSources: [
				{
					dataSourceId: '591043793057281573',
					siteIds: [],
				},
			],
			name: 'Liferay DXP',
		},
		{
			channelId: '507692450375472147',
			dataSources: [
				{
					dataSourceId: '507692450032087801',
					siteIds: [43811416, 82272606, 54804552, 10693199, 57390646],
				},
			],
			name: 'Beryl Commerce',
		},
	],
	lastPage: 1,
	page: 1,
	pageSize: 20,
	totalCount: 2,
};

export function fetchTableData(params: TTableRequestParams) {
	const queryString = serializeTableRequestParams(params);

	return request(`/table-data?${queryString}`, {method: 'GET'});
}

export function fetchTableDataResponse(items: any[]) {
	return {
		actions: {},
		facets: [],
		items,
		lastPage: 1,
		page: 1,
		pageSize: 20,
		totalCount: items.length,
	};
}
