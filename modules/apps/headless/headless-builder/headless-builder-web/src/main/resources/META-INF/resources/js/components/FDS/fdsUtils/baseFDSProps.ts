/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFrontendDataSetProps} from '@liferay/frontend-data-set-web';

export const baseFDSProps: Partial<IFrontendDataSetProps> = {
	currentURL: window.location.pathname + window.location.search,
	filters: [
		{
			id: 'dateModified',
			label: Liferay.Language.get('last-updated'),
			type: 'dateRange',
		},
	],
	pagination: {
		initialDelta: 20,
		initialPageNumber: 0,
	},
	showManagementBar: true,
	showPagination: true,
	showSearch: true,
	sidePanelId: 'none',
	style: 'fluid',
};

export function getEmptyState(
	title: string
): IFrontendDataSetProps['emptyState'] {
	return {
		description: '',
		filtered: {
			filters: {symbol: 'search-state'},
			search: {symbol: 'search-state'},
			searchAndFilters: {symbol: 'search-state'},
		},
		symbol: 'empty-state',
		title,
	};
}
