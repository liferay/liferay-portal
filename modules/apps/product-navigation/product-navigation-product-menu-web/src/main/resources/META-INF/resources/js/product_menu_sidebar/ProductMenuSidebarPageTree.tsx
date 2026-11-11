/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayDropDown from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {
	UnifiedSidebar,
	UnifiedSidebarItem,
	UnifiedSidebarSearchInput,
	openToast,
	useUnifiedSidebarStuck,
} from 'frontend-js-components-web';
import {fetch, navigate, objectToFormData, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import NavigationMenuItemsTree from '../NavigationMenuItemsTree';
import PagesAdministrationLink from '../PagesAdministrationLink';
import PagesTree from '../PagesTree';

const MAX_SEARCH_RESULTS = 10;

const PAGE_TREE_ITEMS: Array<UnifiedSidebarItem> = [];

// The sidebar mounts again on every navigation, so keep the last tree of each
// site to show it right away while it refreshes.

const pageTreeDataCache = new Map<number, PageTreeData>();

type PageTypeOptionGroup = {
	items: Array<{label: string; value: string}>;
	label: string;
};

type PageTreeData = {
	config: {
		addLayoutURL?: string;
		administrationPortletNamespace: string;
		administrationPortletURL: string;
		configureLayoutSetURL?: string;
		findLayoutsURL: string;
		isLayoutSetPrototype?: boolean;
		loadMoreItemsURL: string;
		maxPageSize: number;
		moveItemURL: string;
		stagingEnabled: boolean;
	};
	hasAdministrationPortletPermission: boolean;
	isPrivateLayoutsTree: boolean;
	isSiteNavigationMenu: boolean;
	items: Array<unknown>;
	pageTypeOptions: Array<PageTypeOptionGroup>;
	pageTypeSelectedOption: string;
	portletNamespace: string;
	selectedLayoutId: number;
	selectedLayoutPath: Array<number>;
	selectedSiteNavigationMenuItemId: number;
	showAddIcon: boolean;
	siteNavigationMenuItems: Array<unknown>;
};

type SearchResult = {
	name: string;
	path: Array<string>;
	target: string;
	url: string;
};

type SearchResults = {
	layouts: Array<SearchResult>;
	totalCount: number;
};

function openErrorToast() {
	openToast({
		message: Liferay.Language.get('an-unexpected-error-occurred'),
		title: Liferay.Language.get('error'),
		type: 'danger',
	});
}

function ProductMenuSidebarPageTree({url}: {url: string}) {
	const scopeGroupId = Liferay.ThemeDisplay.getScopeGroupId();

	const [data, setData] = useState<PageTreeData | null>(
		() => pageTreeDataCache.get(scopeGroupId) ?? null
	);
	const [keywords, setKeywords] = useState('');
	const [loading, setLoading] = useState(!data);
	const [searchResults, setSearchResults] = useState<SearchResults | null>(
		null
	);
	const [treeKey, setTreeKey] = useState(0);
	const [version, setVersion] = useState(0);

	const scrollRef = useRef<HTMLDivElement>(null);

	const stuck = useUnifiedSidebarStuck(scrollRef, PAGE_TREE_ITEMS);

	useEffect(() => {
		const abortController = new AbortController();

		if (!pageTreeDataCache.has(scopeGroupId)) {
			setLoading(true);
		}

		fetch(url, {signal: abortController.signal})
			.then((response) => {
				if (!response.ok) {
					throw new Error(response.statusText);
				}

				return response.json();
			})
			.then((pageTreeData: PageTreeData) => {
				pageTreeDataCache.set(scopeGroupId, pageTreeData);

				setData(pageTreeData);
				setLoading(false);

				// The trees only read their items on mount, so mount them
				// again with the refreshed items.

				setTreeKey((previousTreeKey) => previousTreeKey + 1);
			})
			.catch((error) => {
				if (abortController.signal.aborted) {
					return;
				}

				// eslint-disable-next-line no-console
				console.error(error);

				openErrorToast();

				setLoading(false);
			});

		return () => abortController.abort();
	}, [scopeGroupId, url, version]);

	const findLayoutsURL = data?.config.findLayoutsURL;
	const portletNamespace = data?.portletNamespace;

	useEffect(() => {
		if (!findLayoutsURL || !keywords) {
			setSearchResults(null);

			return;
		}

		const abortController = new AbortController();

		fetch(findLayoutsURL, {
			body: objectToFormData({
				[`${portletNamespace}keywords`]: keywords,
			}),
			method: 'POST',
			signal: abortController.signal,
		})
			.then((response) =>
				response.ok ? response.json() : {layouts: [], totalCount: 0}
			)
			.then(setSearchResults)
			.catch(() => {
				if (!abortController.signal.aborted) {
					openErrorToast();
				}
			});

		return () => abortController.abort();
	}, [findLayoutsURL, keywords, portletNamespace]);

	const updatePageType = useCallback(
		(pageType: React.Key) => {
			Liferay.Util.Session.set(
				`${portletNamespace}PAGE_TYPE_SELECTED_OPTION`,
				String(pageType)
			)
				.then(() => {
					pageTreeDataCache.delete(scopeGroupId);

					setVersion((previousVersion) => previousVersion + 1);
				})
				.catch(openErrorToast);
		},
		[portletNamespace, scopeGroupId]
	);

	return (
		<>
			<UnifiedSidebar.Top>
				<UnifiedSidebarSearchInput onChange={setKeywords} />

				{data && !keywords && (
					<div className="align-items-center c-mx-1 c-px-2 d-flex product-menu-sidebar-page-tree-toolbar">
						<Picker
							UNSAFE_menuClassName="cadmin"
							aria-label={Liferay.Language.get('pages-type')}
							className="form-control-sm w-auto"
							items={data.pageTypeOptions.filter(
								(pageTypeOptionGroup) =>
									pageTypeOptionGroup.items.length
							)}
							onSelectionChange={updatePageType}
							selectedKey={data.pageTypeSelectedOption}
						>
							{(pageTypeOptionGroup: PageTypeOptionGroup) => (
								<ClayDropDown.Group
									header={pageTypeOptionGroup.label}
									items={pageTypeOptionGroup.items}
									key={pageTypeOptionGroup.label}
								>
									{(pageTypeOption: {
										label: string;
										value: string;
									}) => (
										<Option key={pageTypeOption.value}>
											{pageTypeOption.label}
										</Option>
									)}
								</ClayDropDown.Group>
							)}
						</Picker>

						<div className="c-ml-auto d-flex">
							{data.showAddIcon && data.config.addLayoutURL && (
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'add-page'
									)}
									borderless
									displayType="secondary"
									onClick={() =>
										navigate(data.config.addLayoutURL!)
									}
									size="sm"
									symbol="plus"
									title={Liferay.Language.get('add-page')}
								/>
							)}

							{data.config.configureLayoutSetURL && (
								<ClayLink
									aria-label={Liferay.Language.get(
										'configure-pages'
									)}
									borderless
									button
									displayType="secondary"
									href={data.config.configureLayoutSetURL}
									monospaced
									small
									title={Liferay.Language.get(
										'configure-pages'
									)}
								>
									<ClayIcon symbol="cog" />
								</ClayLink>
							)}
						</div>
					</div>
				)}
			</UnifiedSidebar.Top>

			<div
				className={classNames(
					'product-menu-sidebar-page-tree unified-sidebar-scroll',
					{
						'unified-sidebar-scroll-stuck': stuck,
					}
				)}
				ref={scrollRef}
			>
				<UnifiedSidebar.Body className="c-px-0">
					{loading && <ClayLoadingIndicator className="c-mt-4" />}

					{data && keywords && (
						<ProductMenuSidebarPageTreeSearchResults
							administrationPortletURL={`${data.config.administrationPortletURL}&${data.config.administrationPortletNamespace}keywords=${encodeURIComponent(keywords)}`}
							searchResults={searchResults}
						/>
					)}

					{data &&
						!keywords &&
						!loading &&
						(data.isSiteNavigationMenu ? (
							<NavigationMenuItemsTree
								key={treeKey}
								portletNamespace={data.portletNamespace}
								selectedSiteNavigationMenuItemId={
									data.selectedSiteNavigationMenuItemId
								}
								siteNavigationMenuItems={
									data.siteNavigationMenuItems
								}
							/>
						) : (
							<PagesTree
								config={{
									isLayoutSetPrototype: Boolean(
										data.config.isLayoutSetPrototype
									),
									loadMoreItemsURL:
										data.config.loadMoreItemsURL,
									maxPageSize: data.config.maxPageSize,
									moveItemURL: data.config.moveItemURL,
									namespace: data.portletNamespace,
									stagingEnabled: data.config.stagingEnabled,
								}}
								isPrivateLayoutsTree={data.isPrivateLayoutsTree}
								items={data.items}
								key={treeKey}
								selectedLayoutId={data.selectedLayoutId}
								selectedLayoutPath={data.selectedLayoutPath}
							/>
						))}
				</UnifiedSidebar.Body>

				<div className="unified-sidebar-scroll-shadow" />
			</div>

			{data && (
				<PagesAdministrationLink
					administrationPortletURL={
						data.config.administrationPortletURL
					}
					hasAdministrationPortletPermission={
						data.hasAdministrationPortletPermission
					}
				/>
			)}
		</>
	);
}

function ProductMenuSidebarPageTreeSearchResults({
	administrationPortletURL,
	searchResults,
}: {
	administrationPortletURL: string;
	searchResults: SearchResults | null;
}) {
	if (!searchResults) {
		return <ClayLoadingIndicator className="c-mt-4" />;
	}

	if (!searchResults.totalCount) {
		return (
			<ClayEmptyState
				className="c-mt-n2 c-px-4 text-center"
				small
				title={Liferay.Language.get('page-not-found')}
			/>
		);
	}

	return (
		<>
			<ul className="nav nav-stacked product-menu-sidebar-page-tree-results">
				{searchResults.layouts
					.slice(0, MAX_SEARCH_RESULTS)
					.map((searchResult) => (
						<li className="nav-item" key={searchResult.url}>
							<a
								className="nav-link"
								href={searchResult.url}
								target={searchResult.target}
							>
								{!!searchResult.path?.length && (
									<span className="d-block product-menu-sidebar-page-tree-result-path text-secondary text-truncate">
										{searchResult.path.join(' / ')}
									</span>
								)}

								<span className="d-block text-truncate">
									{searchResult.name}
								</span>
							</a>
						</li>
					))}
			</ul>

			{searchResults.totalCount > MAX_SEARCH_RESULTS && (
				<div className="c-px-4 c-py-3 text-center">
					<p>
						{sub(
							Liferay.Language.get(
								'there-are-x-more-results-narrow-your-searc-to-get-more-precise-results'
							),
							searchResults.totalCount - MAX_SEARCH_RESULTS
						)}
					</p>

					<ClayLink href={administrationPortletURL}>
						{Liferay.Language.get('view-in-page-administration')}
					</ClayLink>
				</div>
			)}
		</>
	);
}

export default ProductMenuSidebarPageTree;
