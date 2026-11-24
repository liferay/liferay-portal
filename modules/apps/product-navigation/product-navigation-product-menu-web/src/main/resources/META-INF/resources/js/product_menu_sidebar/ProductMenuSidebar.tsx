/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	UnifiedSidebar,
	UnifiedSidebarSiteSelector,
	useUnifiedSidebarExpandedKeys,
	useUnifiedSidebarFilter,
	useUnifiedSidebarItems,
} from 'frontend-js-components-web';
import React, {useCallback, useState} from 'react';

import ProductMenuSidebarNavigation, {
	ProductMenuSidebarItem,
} from './ProductMenuSidebarNavigation';
import ProductMenuSidebarPageTree from './ProductMenuSidebarPageTree';
import {ProductMenuSidebarStagingProps} from './ProductMenuSidebarStaging';
import {useProductMenuSidebarVisibility} from './useProductMenuSidebarVisibility';

interface Props {
	canonicalName: string;
	expandedKeys: Array<React.Key>;
	expandedKeysSessionKey: string;
	imageURL: string;
	items: Array<ProductMenuSidebarItem>;
	label: string;
	navigationItemsURL: string;
	pageTree?: {
		stateSessionKey: string;
		url: string;
		visible: boolean;
	};
	selectedPortletId: string;
	sidebarStateSessionKey: string;
	siteItemSelectedEventName: string;
	siteItemSelectorURL: string;
	staging?: ProductMenuSidebarStagingProps;
	visible: boolean;
}

function ProductMenuSidebar({
	canonicalName,
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	imageURL,
	items: externalItems,
	label,
	navigationItemsURL,
	pageTree,
	selectedPortletId,
	sidebarStateSessionKey,
	siteItemSelectedEventName,
	siteItemSelectorURL,
	staging,
	visible: initialVisible,
}: Props) {
	const {overlay, updateVisible, visible} = useProductMenuSidebarVisibility(
		initialVisible,
		sidebarStateSessionKey
	);

	const {
		items: navigationItems,
		loading,
		prefetchFilterOnlyItems,
	} = useUnifiedSidebarItems(externalItems, navigationItemsURL);

	const {
		expandedKeys: filterExpandedKeys,
		isFilterActive,
		items,
		numberOfMatches,
		setQuery,
	} = useUnifiedSidebarFilter(navigationItems);

	const {expandedKeys, initialExpandedKeys, updateExpandedKeys} =
		useUnifiedSidebarExpandedKeys({
			expandedKeys: externalExpandedKeys,
			expandedKeysSessionKey,
			filterExpandedKeys,
			isFilterActive,
		});

	const onExpandedChange = useCallback(
		(updatedExpandedKeys: Set<React.Key>) => {

			// Clay expands every clicked item, but the Page Tree item opens a
			// view instead of a group.

			const pageTreeItem = externalItems.find((item) => item.pageTree);

			if (pageTreeItem && updatedExpandedKeys.has(pageTreeItem.id)) {
				return;
			}

			updateExpandedKeys(updatedExpandedKeys);
		},
		[externalItems, updateExpandedKeys]
	);

	const [pageTreeVisible, setPageTreeVisible] = useState(
		Boolean(pageTree?.visible)
	);

	const updatePageTreeVisible = useCallback(
		(pageTreeVisible: boolean) => {
			if (!pageTree) {
				return;
			}

			setPageTreeVisible(pageTreeVisible);
			setQuery('');

			Liferay.Util.Session.set(
				pageTree.stateSessionKey,
				pageTreeVisible
					? String(Liferay.ThemeDisplay.getScopeGroupId())
					: ''
			).catch((error: Error) => {

				// eslint-disable-next-line no-console
				console.error(error);
			});
		},
		[pageTree, setQuery]
	);

	const showPageTree = pageTree && pageTreeVisible;

	return (
		<UnifiedSidebar
			backdrop={overlay}
			backdropContainerProps={{className: 'cadmin'}}
			canonicalName={canonicalName}
			categoryImageUrl={imageURL}
			headerActions={
				showPageTree ? undefined : (
					<UnifiedSidebarSiteSelector
						eventName={siteItemSelectedEventName}
						url={siteItemSelectorURL}
					/>
				)
			}
			label={showPageTree ? Liferay.Language.get('page-tree') : label}
			navigationId="com_liferay_product_navigation_product_menu_web_sidebar"
			onBack={
				showPageTree ? () => updatePageTreeVisible(false) : undefined
			}
			onOpenChange={updateVisible}
			open={visible}
			testId="sideNavigation"
		>
			{showPageTree ? (
				<ProductMenuSidebarPageTree url={pageTree.url} />
			) : (
				<ProductMenuSidebarNavigation
					expandedKeys={expandedKeys}
					initialExpandedKeys={initialExpandedKeys}
					isFilterActive={isFilterActive}
					items={items}
					label={label}
					loading={loading}
					numberOfMatches={numberOfMatches}
					onExpandedChange={onExpandedChange}
					onPageTreeOpen={() => updatePageTreeVisible(true)}
					onQueryChange={setQuery}
					onSearchFocus={prefetchFilterOnlyItems}
					selectedPortletId={selectedPortletId}
					staging={staging}
				/>
			)}
		</UnifiedSidebar>
	);
}

export default ProductMenuSidebar;
