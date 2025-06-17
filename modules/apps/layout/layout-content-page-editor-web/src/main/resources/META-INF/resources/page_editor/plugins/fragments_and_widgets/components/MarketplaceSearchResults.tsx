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
	MarketplaceRest,
	Product,
	useMarketplaceConfiguration,
} from '@liferay/marketplace-js-components-web';
import React, {useEffect, useRef, useState} from 'react';

import {config} from '../../../app/config';
import {LIST_ITEM_TYPES} from '../../../app/config/constants/listItemTypes';
import {useSelector} from '../../../app/contexts/StoreContext';
import {useKeyboardNavigation} from '../../../app/js-index';
import MarketplaceTabItem from './MarketplaceTabItem';

export default function MarketplaceSearchResults({
	searchValue,
}: {
	searchValue: string;
}) {
	const baseResourceURL = MarketplaceRest.getBaseResourceURL(
		config.portletNamespace
	);

	const marketplaceConfiguration =
		useMarketplaceConfiguration(baseResourceURL);

	const [showResults, setShowResults] = useState(false);

	useEffect(() => {
		setShowResults(false);
	}, [searchValue, setShowResults]);

	return (
		<>
			{marketplaceConfiguration.authorized ? (
				<div className="page-editor__fragments-widgets__search-results-panel__marketplace-results">
					{showResults ? (
						<SearchResultsPanel searchValue={searchValue} />
					) : (
						<ClayButton
							className="p-3"
							displayType="link"
							onClick={() => {
								setShowResults(true);
							}}
							size="sm"
						>
							{Liferay.Language.get('see-marketplace-results')}
						</ClayButton>
					)}
				</div>
			) : null}
		</>
	);
}

function SearchResultsPanel({searchValue}: {searchValue: string}) {
	const baseResourceURL = MarketplaceRest.getBaseResourceURL(
		config.portletNamespace
	);

	const marketplaceConfiguration =
		useMarketplaceConfiguration(baseResourceURL);

	const [loading, setLoading] = useState(marketplaceConfiguration?.loading);
	const [page, setPage] = useState(1);
	const [results, setResults] = useState<APIResponse<Product>>();

	const searchValueRef = useRef(searchValue);

	const hasMoreResults = results?.lastPage && results.lastPage > page;

	useEffect(() => {
		const marketplaceRest = marketplaceConfiguration?.data
			? new MarketplaceRest(
					baseResourceURL,
					marketplaceConfiguration.data
				)
			: null;

		if (!marketplaceRest || !marketplaceConfiguration?.authorized) {
			return;
		}

		setLoading(true);

		const urlSearchParams = new URLSearchParams({
			'accountId': '-1',
			'attachments.accountId': '-1',
			'filter': marketplaceRest.settings?.references?.fragmentsFilter,
			'images.accountId': '-1',
			'nestedFields':
				'attachments,productSpecifications,skus,categories,images',
			'page': `${page}`,
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
	}, [
		baseResourceURL,
		marketplaceConfiguration?.authorized,
		marketplaceConfiguration?.data,
		page,
	]);

	return (
		<>
			<p className="pb-2 pl-3 pt-3 text-3 text-secondary">
				{Liferay.Language.get('showing-results-from-marketplace')}
			</p>

			<SearchResults loading={loading} results={results} />

			{hasMoreResults && (
				<ClayButton
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

function SearchResults({
	loading,
	results,
}: {
	loading: boolean | undefined;
	results?: APIResponse<Product>;
}) {
	const permissions = useSelector((state) => state.permissions);

	const listRef = useRef<HTMLUListElement | null>(null);

	useEffect(() => {
		if (
			listRef.current &&
			results &&
			results.page === 1 &&
			results.items.length
		) {
			const firstListItem = listRef.current.firstChild as HTMLLIElement;

			firstListItem?.focus();
		}
	}, [results, listRef]);

	return (
		<>
			{results?.items.length ? (
				<MarketplaceModal
					fragmentPortletNamespace={config.fragmentPortletNamespace}
					fragmentsImportURL={config.fragmentsImportURL}
					hideBackButton={true}
					permissions={{
						installFreeApps:
							permissions.INSTALL_FREE_BUNDLED_APPS_MARKETPLACE,
						manageFragmentsEntries:
							permissions.MANAGE_FRAGMENT_ENTRIES,
						purchaseAndInstallPaidApps:
							permissions.PURCHASE_AND_INSTALL_PAID_APPS_MARKETPLACE,
					}}
					portletNamespace={config.portletNamespace}
					trigger={null}
				>
					<ul
						aria-label={Liferay.Language.get(
							'marketplace-fragments'
						)}
						className="list-unstyled px-3"
						ref={listRef}
						role="menubar"
					>
						{results.items.map((item: Product) => (
							<MarketplaceSearchResultsList
								item={item}
								key={item.id}
							/>
						))}
					</ul>
				</MarketplaceModal>
			) : null}

			{!loading && !results?.items.length ? (
				<ClayEmptyState
					description={Liferay.Language.get(
						'try-again-with-a-different-search'
					)}
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
					small
					title={Liferay.Language.get('no-results-found')}
				/>
			) : null}

			{loading ? (
				<ClayLoadingIndicator className="mt-3" size="sm" />
			) : null}

			{!loading && results ? (
				<SearchResultsMessage numberOfResults={results.items.length} />
			) : null}
		</>
	);
}

function MarketplaceSearchResultsList({item}: {item: Product}) {
	const {isTarget, setElement} = useKeyboardNavigation({
		type: LIST_ITEM_TYPES.listItem,
	});

	const onClickRef = useRef<() => void | null>(null);

	return (
		<li
			className="card-interactive rounded"
			onClick={() => onClickRef.current?.()}
			onKeyDown={(event) => {
				if (['Enter', 'Space', ' '].includes(event.key)) {
					onClickRef.current?.();
				}
			}}
			ref={setElement}
			role="menuitem"
			tabIndex={isTarget ? 0 : -1}
		>
			<MarketplaceTabItem item={item} onClickRef={onClickRef} />
		</li>
	);
}
