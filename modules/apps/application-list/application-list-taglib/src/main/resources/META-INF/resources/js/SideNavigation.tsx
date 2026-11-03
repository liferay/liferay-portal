/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import {ClayVerticalNav} from '@clayui/nav';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {UnifiedSidebar, useId} from 'frontend-js-components-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import SideNavigationItemContent from './SideNavigationItemContent';
import SideNavigationResultsSkeleton from './SideNavigationResultsSkeleton';
import SideNavigationScopeItem from './SideNavigationScopeItem';
import SideNavigationSearchInput from './SideNavigationSearchInput';
import {SideNavigationItem} from './types/SideNavigation';
import {useSideNavigationFilter} from './useSideNavigationFilter';
import {useSideNavigationItems} from './useSideNavigationItems';
import {useSideNavigationStuck} from './useSideNavigationStuck';

interface Props {
	canonicalName: string;
	categoryImageUrl: string;
	expandedKeys: Array<React.Key>;
	expandedKeysSessionKey: string;
	items: Array<SideNavigationItem>;
	label: string;
	navigationItemsURL: string;
	selectedPortletId: string;
	visible: boolean;
	visibleSessionKey: string;
}

function countNavigableItems(
	navigationItems: Array<SideNavigationItem>
): number {
	return navigationItems.reduce(
		(count, navigationItem) =>
			count +
			(navigationItem.href ? 1 : 0) +
			(navigationItem.items
				? countNavigableItems(navigationItem.items)
				: 0),
		0
	);
}

function SideNavigation({
	canonicalName,
	categoryImageUrl,
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	items: externalItems,
	label,
	navigationItemsURL,
	selectedPortletId,
	visible: initialVisible,
	visibleSessionKey,
}: Props) {
	const containerRef = useRef<HTMLElement | null>(
		document.getElementById(
			'com_liferay_application_list_taglib_side_navigation_container'
		)
	);

	const [initialExpandedKeys] = useState<Set<React.Key>>(
		() => new Set(externalExpandedKeys)
	);

	const [userExpandedKeys, setUserExpandedKeys] =
		useState<Set<React.Key>>(initialExpandedKeys);

	const [visible, setVisible] = useState(initialVisible);

	const baseId = useId();

	const scrollRef = useRef<HTMLDivElement>(null);

	const {
		items: navigationItems,
		loading,
		prefetchFilterOnlyItems,
	} = useSideNavigationItems(externalItems, navigationItemsURL);

	const {expandedKeys, isFilterActive, items, numberOfMatches, setQuery} =
		useSideNavigationFilter(navigationItems);

	const [filterCollapse, setFilterCollapse] = useState<{
		appliesTo: Set<React.Key>;
		expandedKeys: Set<React.Key>;
	}>();

	const effectiveExpandedKeys =
		filterCollapse && filterCollapse.appliesTo === expandedKeys
			? filterCollapse.expandedKeys
			: expandedKeys ?? userExpandedKeys;

	const updateExpandedKeys = useCallback(
		async (updatedExpandedKeys: Set<React.Key>) => {
			if (isFilterActive && expandedKeys) {
				setFilterCollapse({
					appliesTo: expandedKeys,
					expandedKeys: updatedExpandedKeys,
				});

				return;
			}

			await Liferay.Util.Session.set(
				expandedKeysSessionKey,
				Array.from(updatedExpandedKeys).join(',')
			);

			setUserExpandedKeys(updatedExpandedKeys);
		},
		[expandedKeys, expandedKeysSessionKey, isFilterActive]
	);

	const updateVisible = useCallback(
		async (visible: boolean) => {
			await Liferay.Util.Session.set(
				visibleSessionKey,
				visible ? 'visible' : 'hidden'
			);

			setVisible(visible);

			Liferay.fire('sideNavigationStateChanged', {visible});
		},
		[visibleSessionKey]
	);

	useEffect(
		function setupVisibilityRequestHandler() {
			async function handleStateRequest({visible}: {visible: boolean}) {
				await updateVisible(visible);
			}

			Liferay.on('sideNavigationStateRequested', handleStateRequest);

			return () =>
				Liferay.detach(
					'sideNavigationStateRequested',
					handleStateRequest
				);
		},
		[updateVisible]
	);

	const numberOfResults = useMemo(
		() => (isFilterActive ? numberOfMatches : countNavigableItems(items)),
		[isFilterActive, items, numberOfMatches]
	);

	// Keep already matched items on screen while the filter-only items are
	// still loading, so a slow request cannot blank a complete result set.

	const showResultsSkeleton = isFilterActive && loading && !numberOfResults;

	const stuck = useSideNavigationStuck(scrollRef, items);

	return (
		<UnifiedSidebar
			canonicalName={canonicalName}
			categoryImageUrl={categoryImageUrl}
			containerRef={containerRef}
			navigationId="com_liferay_application_list_taglib_side_navigation"
			label={label}
			onOpenChange={updateVisible}
			open={visible}
			testId="sideNavigation"
		>
			<UnifiedSidebar.Top>
				<SideNavigationSearchInput
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

			<UnifiedSidebar.Body className="c-px-0">
				<div
					className={classNames('side-navigation-scroll', {
						'side-navigation-scroll-stuck': stuck,
					})}
					ref={scrollRef}
				>
					{showResultsSkeleton ? (
						<SideNavigationResultsSkeleton />
					) : numberOfResults ? (
						<ClayVerticalNav
							active={selectedPortletId}
							defaultExpandedKeys={initialExpandedKeys}
							displayType="primary"
							expandedKeys={effectiveExpandedKeys}
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

								if (item.scopeMarker && item.scope) {
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
											'side-navigation-section-item':
												item.parentLabel,
										})}
										data-canonical-name={item.canonicalName}
										href={item.href}
										items={item.items}
										key={item.id}
										textValue={item.label}
									>
										<SideNavigationItemContent
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

					<div className="side-navigation-scroll-shadow" />
				</div>
			</UnifiedSidebar.Body>
		</UnifiedSidebar>
	);
}

export default SideNavigation;
