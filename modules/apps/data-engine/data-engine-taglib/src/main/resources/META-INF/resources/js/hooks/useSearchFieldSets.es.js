/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useConfig} from 'data-engine-js-components-web';
import {useCallback, useEffect, useRef, useState} from 'react';

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

	if (!results.length) {
		return {fieldSets: [], truncated: false};
	}

	const rejectedResults = results.filter(({status}) => status === 'rejected');

	if (rejectedResults.length === results.length) {
		throw rejectedResults[0].reason;
	}

	const items = [];

	let totalCount = 0;

	results.forEach((result) => {
		if (result.status === 'fulfilled') {
			items.push(...result.value.items);

			totalCount += result.value.totalCount;
		}
	});

	return {
		fieldSets: items.filter(
			({id}) => id !== parseInt(dataDefinitionId, 10)
		),
		truncated: totalCount > items.length,
	};
}

export default function useSearchFieldSets(searchTerm) {
	const {contentType, dataDefinitionId, groupId} = useConfig();

	const [searchState, setSearchState] = useState(null);

	const hasErrorRef = useRef(false);
	const removedFieldSetIdsRef = useRef(new Set());

	useEffect(() => {
		if (!contentType || !searchTerm) {
			setSearchState(null);

			return;
		}

		const abortController = new AbortController();

		const {signal} = abortController;

		const timeoutId = setTimeout(() => {
			fetchFieldSets({
				contentType,
				dataDefinitionId,
				groupId,
				keywords: searchTerm,
				signal,
			})
				.then(({fieldSets, truncated}) => {
					if (!signal.aborted) {
						hasErrorRef.current = false;

						setSearchState({
							hasError: false,
							items: fieldSets.filter(
								({id}) => !removedFieldSetIdsRef.current.has(id)
							),
							term: searchTerm,
							truncated,
						});
					}
				})
				.catch(() => {
					if (!signal.aborted) {
						if (!hasErrorRef.current) {
							errorToast();
						}

						hasErrorRef.current = true;

						setSearchState({
							hasError: true,
							items: [],
							term: searchTerm,
							truncated: false,
						});
					}
				});
		}, SEARCH_DELAY);

		return () => {
			abortController.abort();

			clearTimeout(timeoutId);
		};
	}, [contentType, dataDefinitionId, groupId, searchTerm]);

	const removeSearchResult = useCallback((fieldSetId) => {
		removedFieldSetIdsRef.current.add(fieldSetId);

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
		isTruncated: hasCurrentResults && Boolean(searchState.truncated),
		removeSearchResult,
		searchResults: hasCurrentResults ? searchState.items : null,
	};
}
