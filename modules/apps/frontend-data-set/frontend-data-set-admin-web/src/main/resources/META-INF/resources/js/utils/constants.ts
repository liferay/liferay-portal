/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IBaseVisualizationMode} from './types';

const DEFAULT_FETCH_HEADERS = {
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
};

const FUZZY_OPTIONS = {
	post: '</strong>',
	pre: '<strong>',
};

const OBJECT_RELATIONSHIP_PREFIX = 'dataSetTo';

const OBJECT_RELATIONSHIP = {
	DATA_SET_ACTIONS: `${OBJECT_RELATIONSHIP_PREFIX}DataSetActions`,
	DATA_SET_CARDS_SECTIONS: `${OBJECT_RELATIONSHIP_PREFIX}DataSetCardsSections`,
	DATA_SET_CLIENT_EXTENSION_FILTERS: `${OBJECT_RELATIONSHIP_PREFIX}DataSetClientExtensionFilters`,
	DATA_SET_DATE_FILTERS: `${OBJECT_RELATIONSHIP_PREFIX}DataSetDateFilters`,
	DATA_SET_LIST_SECTIONS: `${OBJECT_RELATIONSHIP_PREFIX}DataSetListSections`,
	DATA_SET_SELECTION_FILTERS: `${OBJECT_RELATIONSHIP_PREFIX}DataSetSelectionFilters`,
	DATA_SET_SORTS: `${OBJECT_RELATIONSHIP_PREFIX}DataSetSorts`,
	DATA_SET_TABLE_SECTIONS: `${OBJECT_RELATIONSHIP_PREFIX}DataSetTableSections`,
} as const;

const FDS_DEFAULT_PROPS = {
	pagination: {
		deltas: [{label: 4}, {label: 8}, {label: 20}, {label: 40}, {label: 60}],
		initialDelta: 8,
	},
};

const DEFAULT_VISUALIZATION_MODES: Array<IBaseVisualizationMode<any>> = [
	{
		label: Liferay.Language.get('cards'),
		mode: 'cards',
		thumbnail: 'cards2',
		visualizationModeId: 'defaultCards',
	},
	{
		label: Liferay.Language.get('list'),
		mode: 'list',
		thumbnail: 'list',
		visualizationModeId: 'defaultList',
	},
	{
		label: Liferay.Language.get('table'),
		mode: 'table',
		thumbnail: 'table',
		visualizationModeId: 'defaultTable',
	},
];

const ALLOWED_ENDPOINTS_PARAMETERS = ['scopeKey', 'siteId', 'userId'];

const PAGE_SIZE = '100';

export {
	DEFAULT_VISUALIZATION_MODES,
	FDS_DEFAULT_PROPS,
	FUZZY_OPTIONS,
	DEFAULT_FETCH_HEADERS,
	OBJECT_RELATIONSHIP,
	ALLOWED_ENDPOINTS_PARAMETERS,
	PAGE_SIZE,
};
