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
	UnifiedSidebarItemContent,
	UnifiedSidebarResultsSkeleton,
	UnifiedSidebarSearchInput,
	UnifiedSidebarSiteSelector,
	countUnifiedSidebarItems,
	useId,
	useUnifiedSidebarExpandedKeys,
	useUnifiedSidebarFilter,
	useUnifiedSidebarItems,
	useUnifiedSidebarStuck,
	useUnifiedSidebarVisibility,
} from 'frontend-js-components-web';
import React, {useMemo, useRef} from 'react';

import SideNavigationScopeItem from './SideNavigationScopeItem';
import {
	filterSideNavigationItems,
	isScopeMarker,
} from './filterSideNavigationItems';
import {SideNavigationItem} from './types/SideNavigation';

interface Props {
	canonicalName: string;
	categoryImageUrl: string;
	colorScheme: 'dark' | 'light';
	colorSchemeSessionKey: string;
	expandedKeys: Array<React.Key>;
	expandedKeysSessionKey: string;
	items: Array<SideNavigationItem>;
	label: string;
	navigationItemsURL: string;
	selectedPortletId: string;
	siteAdministrationItemSelectedEventName: string;
	siteAdministrationItemSelectorUrl: string;
	visible: boolean;
	visibleSessionKey: string;
}

function SideNavigation({
	canonicalName,
	categoryImageUrl,
	colorScheme,
	colorSchemeSessionKey,
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	items: externalItems,
	label,
	navigationItemsURL,
	selectedPortletId,
	siteAdministrationItemSelectedEventName,
	siteAdministrationItemSelectorUrl,
	visible: initialVisible,
	visibleSessionKey,
}: Props) {
	const containerRef = useRef<HTMLElement | null>(
		document.getElementById(
			'com_liferay_application_list_taglib_side_navigation_container'
		)
	);

	const baseId = useId();

	const scrollRef = useRef<HTMLDivElement>(null);

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
	} = useUnifiedSidebarFilter(navigationItems, filterSideNavigationItems);

	const {expandedKeys, initialExpandedKeys, updateExpandedKeys} =
		useUnifiedSidebarExpandedKeys({
			expandedKeys: externalExpandedKeys,
			expandedKeysSessionKey,
			filterExpandedKeys,
			isFilterActive,
		});

	const {updateVisible, visible} = useUnifiedSidebarVisibility({
		hiddenSessionValue: 'hidden',
		initialVisible,
		sessionKey: visibleSessionKey,
		stateChangedEventName: 'sideNavigationStateChanged',
		stateRequestedEventName: 'sideNavigationStateRequested',
		visibleSessionValue: 'visible',
	});

	const numberOfResults = useMemo(
		() =>
			isFilterActive ? numberOfMatches : countUnifiedSidebarItems(items),
		[isFilterActive, items, numberOfMatches]
	);

	// Keep already matched items on screen while the filter-only items are
	// still loading, so a slow request cannot blank a complete result set.

	const showResultsSkeleton = isFilterActive && loading && !numberOfResults;

	const stuck = useUnifiedSidebarStuck(
		scrollRef,
		items,
		'.side-navigation-scope-item'
	);

	return (
		<UnifiedSidebar
			canonicalName={canonicalName}
			categoryImageUrl={categoryImageUrl}
			colorScheme={colorScheme}
			colorSchemeSessionKey={colorSchemeSessionKey}
			containerRef={containerRef}
			headerActions={
				siteAdministrationItemSelectedEventName &&
				siteAdministrationItemSelectorUrl ? (
					<UnifiedSidebarSiteSelector
						eventName={siteAdministrationItemSelectedEventName}
						url={siteAdministrationItemSelectorUrl}
					/>
				) : undefined
			}
			label={label}
			navigationId="com_liferay_application_list_taglib_side_navigation"
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
									collectionItem as SideNavigationItem;
								const scopeItemId = `${baseId}-${item.scope}`;

								if (isScopeMarker(item)) {
									return (
										<SideNavigationScopeItem
											id={scopeItemId}
											key={item.id}
											label={item.label}
											scope={item.scope}
										/>
									);
								}

								return (
									<ClayVerticalNav.Item
										aria-describedby={
											item.scope ? scopeItemId : undefined
										}
										className={classNames({
											'side-navigation-scope-zone':
												item.scope,
											[`side-navigation-scope-zone-${item.scope}`]:
												item.scope,
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

export default SideNavigation;
