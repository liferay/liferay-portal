/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {
	MarketplaceModal,
	SearchResultsMessage,
} from '@liferay/layout-js-components-web';
import {
	APIResponse,
	MarketplaceConfiguration,
	MarketplaceRest,
	Product,
} from '@liferay/marketplace-js-components-web';
import React, {useEffect, useMemo, useRef, useState} from 'react';

import {LIST_ITEM_TYPES} from '../../../app/config/constants/listItemTypes';
import {useKeyboardNavigation} from '../../../app/js-index';
import MarketplaceTabItem from './MarketplaceTabItem';

interface MarketplaceSearchResultsProps {
	baseResourceURL: string;
	marketplaceConfiguration: {
		authorized: boolean;
		data: MarketplaceConfiguration;
		loading: boolean;
	};
	searchValue: string;
}

export default function MarketplaceSearchResults({
	baseResourceURL,
	marketplaceConfiguration,
	searchValue,
}: MarketplaceSearchResultsProps) {
	const [loading, setLoading] = useState(marketplaceConfiguration.loading);
	const [page, setPage] = useState(1);
	const [results, setResults] = useState<APIResponse<Product>>();

	const searchValueRef = useRef(searchValue);

	const marketplaceRest = useMemo(() => {
		return new MarketplaceRest(
			baseResourceURL,
			marketplaceConfiguration.data
		);
	}, [baseResourceURL, marketplaceConfiguration.data]);

	const showMoreResults = results?.lastPage && results.lastPage > page;

	useEffect(() => {
		if (!marketplaceConfiguration.authorized) {
			return;
		}

		setLoading(true);

		const urlSearchParams = new URLSearchParams({
			'accountId': '-1',
			'attachments.accountId': '-1',
			'filter': "(categoryNames/any(x:(x eq 'Fragments')))",
			'images.accountId': '-1',
			'nestedFields': 'productSpecifications,skus,categories,images',
			'page': page.toString(),
			'pageSize': '20',
			'search': searchValueRef.current,
			'skus.accountId': '-1',
			'sort': 'name:asc',
		});

		marketplaceRest
			.getProducts(urlSearchParams)
			.then((nextResults) => {
				setResults((prevResults) => {
					if (prevResults?.items) {
						nextResults.items = prevResults.items.concat(
							nextResults.items
						);
					}

					return nextResults;
				});
				setLoading(false);
			})
			.catch((error: Error) =>
				console.error('Failed to fetch products:', error)
			)
			.finally(() => setLoading(false));
	}, [marketplaceConfiguration.authorized, marketplaceRest, page]);

	return (
		<>
			{results && !loading && (
				<SearchResultsMessage numberOfResults={results.items.length} />
			)}

			<p className="pb-2 pl-3 pt-3 text-3 text-secondary">
				{Liferay.Language.get('showing-results-from-marketplace')}
			</p>

			{results?.items.length ? (
				<ul
					aria-label={Liferay.Language.get('marketplace-fragments')}
					className="list-unstyled px-3"
					role="menubar"
				>
					{results.items.map((item: Product) => (
						<MarketplaceSearchResultsList
							item={item}
							key={item.id}
						/>
					))}
				</ul>
			) : (
				!loading && (
					<ClayEmptyState
						description={Liferay.Language.get(
							'try-again-with-a-different-search'
						)}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
						small
						title={Liferay.Language.get('no-results-found')}
					/>
				)
			)}

			{loading && <ClayLoadingIndicator className="mt-3" size="sm" />}

			{showMoreResults && (
				<ClayButton
					aria-label={Liferay.Language.get('load-more-results')}
					className="p-3 text-secondary"
					displayType="link"
					onClick={() => {
						setPage((prevPage) => prevPage + 1);
					}}
					size="sm"
				>
					{Liferay.Language.get('load-more-results')}
				</ClayButton>
			)}
		</>
	);
}

function MarketplaceSearchResultsList({item}: {item: Product}) {
	const {isTarget, setElement} = useKeyboardNavigation({
		type: LIST_ITEM_TYPES.listItem,
	});

	return (
		<li
			className="card-interactive rounded"
			ref={setElement}
			role="menuitem"
			tabIndex={isTarget ? 0 : -1}
		>
			<MarketplaceModal trigger={<MarketplaceTabItem item={item} />} />
		</li>
	);
}
