/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useDebounce} from '@clayui/shared';
import {useConfig} from 'data-engine-js-components-web';
import {useCallback, useEffect, useState} from 'react';

import {getItems} from '../utils/client.es';
import {errorToast} from '../utils/toast.es';

const SEARCH_DELAY = 500;

export async function fetchFieldSets({
	contentType,
	dataDefinitionId,
	groupId,
	keywords,
	signal,
}) {
	const requests = [];

	if (groupId) {
		requests.push(
			getItems(
				`/o/data-engine/v2.0/sites/${groupId}/data-definitions/by-content-type/${contentType}`,
				keywords,
				{signal}
			)
		);
	}

	if (groupId !== themeDisplay.getCompanyGroupId()) {
		requests.push(
			getItems(
				`/o/data-engine/v2.0/data-definitions/by-content-type/${contentType}`,
				keywords,
				{signal}
			)
		);
	}

	const results = await Promise.allSettled(requests);

	const rejectedResults = results.filter(({status}) => status === 'rejected');

	if (rejectedResults.length === results.length) {
		throw rejectedResults[0].reason;
	}

	const items = [];

	results.forEach((result) => {
		if (result.status === 'fulfilled') {
			items.push(...result.value);
		}
	});

	return items.filter(({id}) => id !== parseInt(dataDefinitionId, 10));
}

export default function useSearchFieldSets(searchTerm) {
	const {contentType, dataDefinitionId, groupId} = useConfig();

	const [searchState, setSearchState] = useState(null);

	const debouncedSearchTerm = useDebounce(searchTerm, SEARCH_DELAY);

	useEffect(() => {
		if (!contentType || !debouncedSearchTerm) {
			setSearchState(null);

			return;
		}

		const abortController = new AbortController();

		const {signal} = abortController;

		fetchFieldSets({
			contentType,
			dataDefinitionId,
			groupId,
			keywords: debouncedSearchTerm,
			signal,
		})
			.then((items) => {
				if (!signal.aborted) {
					setSearchState({
						hasError: false,
						items,
						term: debouncedSearchTerm,
					});
				}
			})
			.catch(() => {
				if (!signal.aborted) {
					errorToast();

					setSearchState({
						hasError: true,
						items: [],
						term: debouncedSearchTerm,
					});
				}
			});

		return () => abortController.abort();
	}, [contentType, dataDefinitionId, debouncedSearchTerm, groupId]);

	const removeSearchResult = useCallback((fieldSetId) => {
		setSearchState(
			(searchState) =>
				searchState && {
					...searchState,
					items: searchState.items.filter(
						({id}) => id !== fieldSetId
					),
				}
		);
	}, []);

	const hasCurrentResults = Boolean(
		searchState && searchState.term === searchTerm
	);

	return {
		hasError: hasCurrentResults && Boolean(searchState.hasError),
		isLoading:
			Boolean(contentType) && Boolean(searchTerm) && !hasCurrentResults,
		removeSearchResult,
		searchResults: hasCurrentResults ? searchState.items : null,
	};
}
