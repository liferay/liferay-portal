/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import {ClayVerticalNav} from '@clayui/nav';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {
	UnifiedSidebar,
	UnifiedSidebarItem,
	UnifiedSidebarItemContent,
	UnifiedSidebarResultsSkeleton,
	UnifiedSidebarSearchInput,
	UnifiedSidebarSiteSelector,
	countUnifiedSidebarItems,
	useUnifiedSidebarExpandedKeys,
	useUnifiedSidebarFilter,
	useUnifiedSidebarItems,
	useUnifiedSidebarStuck,
} from 'frontend-js-components-web';
import React, {useMemo, useRef} from 'react';

import {useProductMenuSidebarVisibility} from './useProductMenuSidebarVisibility';

interface Props {
	canonicalName: string;
	expandedKeys: Array<React.Key>;
	expandedKeysSessionKey: string;
	imageURL: string;
	items: Array<UnifiedSidebarItem>;
	label: string;
	navigationItemsURL: string;
	selectedPortletId: string;
	siteItemSelectedEventName: string;
	siteItemSelectorURL: string;
	visible: boolean;
	visibleSessionKey: string;
}

function ProductMenuSidebar({
	canonicalName,
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	imageURL,
	items: externalItems,
	label,
	navigationItemsURL,
	selectedPortletId,
	siteItemSelectedEventName,
	siteItemSelectorURL,
	visible: initialVisible,
	visibleSessionKey,
}: Props) {
	const {overlay, updateVisible, visible} = useProductMenuSidebarVisibility(
		initialVisible,
		visibleSessionKey
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

	const numberOfResults = useMemo(
		() =>
			isFilterActive ? numberOfMatches : countUnifiedSidebarItems(items),
		[isFilterActive, items, numberOfMatches]
	);

	// Keep already matched items on screen while the filter-only items are
	// still loading, so a slow request cannot blank a complete result set.

	const showResultsSkeleton = isFilterActive && loading && !numberOfResults;

	const scrollRef = useRef<HTMLDivElement>(null);

	const stuck = useUnifiedSidebarStuck(scrollRef, items);

	return (
		<UnifiedSidebar
			backdrop={overlay}
			backdropContainerProps={{className: 'cadmin'}}
			canonicalName={canonicalName}
			categoryImageUrl={imageURL}
			headerActions={
				<UnifiedSidebarSiteSelector
					eventName={siteItemSelectedEventName}
					url={siteItemSelectorURL}
				/>
			}
			label={label}
			navigationId="com_liferay_product_navigation_product_menu_web_sidebar"
			onOpenChange={updateVisible}
			open={visible}
			testId="sideNavigation"
		>
			<UnifiedSidebar.Top>
				<UnifiedSidebarSearchInput
					onChange={setQuery}
					onFocus={prefetchFilterOnlyItems}
				/>

				<SearchResultsMessage
					numberOfResults={
						showResultsSkeleton ? null : numberOfResults
					}
					resultType={Liferay.Language.get('navigation-items')}
				/>
			</UnifiedSidebar.Top>

			<div
				className={classNames('unified-sidebar-scroll', {
					'unified-sidebar-scroll-stuck': stuck,
				})}
				ref={scrollRef}
			>
				<UnifiedSidebar.Body className="c-px-0">
					{showResultsSkeleton ? (
						<UnifiedSidebarResultsSkeleton />
					) : numberOfResults ? (
						<ClayVerticalNav
							active={selectedPortletId}
							defaultExpandedKeys={initialExpandedKeys}
							displayType="primary"
							expandedKeys={expandedKeys}
							itemAriaCurrent={true}
							items={items}
							onExpandedChange={updateExpandedKeys}
							stacked={true}
						>
							{(collectionItem) => {
								if (typeof collectionItem === 'string') {
									return <span>{collectionItem}</span>;
								}

								const item =
									collectionItem as UnifiedSidebarItem;

								return (
									<ClayVerticalNav.Item
										className={classNames({
											'unified-sidebar-section-item':
												item.parentLabel,
										})}
										data-canonical-name={item.canonicalName}
										href={item.href}
										items={item.items}
										key={item.id}
										textValue={item.label}
									>
										<UnifiedSidebarItemContent
											item={item}
										/>
									</ClayVerticalNav.Item>
								);
							}}
						</ClayVerticalNav>
					) : (
						<ClayEmptyState
							className="c-mt-n2 c-px-4 text-center"
							description={Liferay.Language.get(
								'adjust-or-clear-the-search-to-view-all-navigation-items'
							)}
							small
							title={Liferay.Language.get('no-matching-items')}
						/>
					)}
				</UnifiedSidebar.Body>

				<div className="unified-sidebar-scroll-shadow" />
			</div>
		</UnifiedSidebar>
	);
}

export default ProductMenuSidebar;
