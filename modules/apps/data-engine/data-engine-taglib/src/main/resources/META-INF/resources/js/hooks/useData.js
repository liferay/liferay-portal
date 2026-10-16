/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useResource} from '@clayui/data-provider';
import {fetch} from 'frontend-js-web';

const omit = (object, props) => {
	const result = {...object};

	props.forEach((prop) => {
		delete result[prop];
	});

	return result;
};

/**
 * The implementation of parse data tries to omit some data that we already received
 * via props at application startup that are comparable with the data via request,
 * some data is assembled from the props via data representation for the
 * Data Engine structure.
 */
const parseDataLayout = (dataLayout) => {
	const {paginationMode, ...dataLayouts} = omit(dataLayout, [
		'dataRules',
		'dataLayoutPages',
		'description',
		'name',
	]);

	return {
		dataLayout: dataLayouts,
		paginationMode,
	};
};

const parseDataDefinition = (originalDataDefinition) => {
	const {availableLanguageIds, description, name, ...dataDefinition} = omit(
		originalDataDefinition,
		['defaultLanguageId', 'defaultDataLayout']
	);

	return {
		availableLanguageIds,
		dataDefinition,
		description,
		name,
	};
};

const DEFAULT_ID = '0';
const FETCH_TIMEOUT = 60000;
const NOT_FOUND = 404;

const customFetch =
	({defaultData, id, parse}) =>
	async (url, init) => {
		if (id === DEFAULT_ID) {
			return defaultData;
		}

		const response = await fetch(url, init);

		if (response.ok) {
			return parse(await response.json());
		}

		if (response.status === NOT_FOUND) {
			return defaultData;
		}

		throw response;
	};

const DEFAULT_DATA_DEFINITION = {
	dataDefinition: {
		dataDefinitionFields: [],
	},
};

const DEFAULT_DATA_LAYOUT = {
	dataLayoutFields: {},
	dataLayoutPages: [],
	dataRules: [],
	paginationMode: 'single-page',
};

export function useData({dataDefinitionId, dataLayoutId}) {
	let pathContext = themeDisplay.getPathContext();

	if (!pathContext || pathContext === '/') {
		pathContext = '';
	}

	const {resource: dataDefinition} = useResource({
		fetch: customFetch({
			defaultData: DEFAULT_DATA_DEFINITION,
			id: dataDefinitionId,
			parse: parseDataDefinition,
		}),
		fetchPolicy: 'cache-first',
		fetchRetry: {
			attempts: 0,
		},
		fetchTimeout: FETCH_TIMEOUT,
		link: `${window.location.origin}${pathContext}/o/data-engine/v2.0/data-definitions/${dataDefinitionId}`,
	});

	const {resource: dataLayout} = useResource({
		fetch: customFetch({
			defaultData: DEFAULT_DATA_LAYOUT,
			id: dataLayoutId,
			parse: parseDataLayout,
		}),
		fetchPolicy: 'cache-first',
		fetchRetry: {
			attempts: 0,
		},
		fetchTimeout: FETCH_TIMEOUT,
		link: `${window.location.origin}${pathContext}/o/data-engine/v2.0/data-layouts/${dataLayoutId}`,
	});

	return {dataDefinition, dataLayout};
}
