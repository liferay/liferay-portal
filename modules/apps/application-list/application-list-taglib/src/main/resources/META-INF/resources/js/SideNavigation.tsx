/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {ClayVerticalNav} from '@clayui/nav';
import ClaySticker from '@clayui/sticker';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import SideNavigationColorSchemeButton from './SideNavigationColorSchemeButton';
import SideNavigationItemContent from './SideNavigationItemContent';
import SideNavigationResultsSkeleton from './SideNavigationResultsSkeleton';
import SideNavigationSearchInput from './SideNavigationSearchInput';
import SideNavigationSiteSelector from './SideNavigationSiteSelector';
import {SideNavigationItem} from './types/SideNavigation';
import {useSideNavigationFilter} from './useSideNavigationFilter';
import {useSideNavigationItems} from './useSideNavigationItems';

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

	const [initialExpandedKeys] = useState<Set<React.Key>>(
		() => new Set(externalExpandedKeys)
	);

	const [userExpandedKeys, setUserExpandedKeys] =
		useState<Set<React.Key>>(initialExpandedKeys);

	const [visible, setVisible] = useState(initialVisible);

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

	return (
		<SidePanel
			aria-label={sub(Liferay.Language.get('x-menu'), label)}
			closeOnEscape={false}
			containerRef={containerRef}
			data-canonical-name={canonicalName}
			data-qa-id="sideNavigation"
			data-testid="sideNavigation"
			defaultOpen={initialVisible}
			direction="left"
			id="com_liferay_application_list_taglib_side_navigation"
			onOpenChange={updateVisible}
			open={visible}
			panelWidth={320}
			position="fixed"
		>
			<SidePanel.Header
				className="c-mt-2 c-mx-1 c-px-2 side-navigation-header"
				closeButtonProps={
					Liferay.FeatureFlags['LPD-57922']
						? {
								className:
									'btn btn-monospaced btn-outline-borderless btn-outline-secondary btn-sm side-navigation-close-button',
							}
						: undefined
				}
				data-qa-id="sideNavigationHeader"
				messages={{
					backAriaLabel: Liferay.Language.get('go-back'),
					closeAriaLabel: Liferay.Language.get('close-product-menu'),
				}}
			>
				<SidePanel.Title className="align-items-center c-my-0 d-flex">
					<ClaySticker
						borderless
						className="c-mr-1"
						displayType="outline"
					>
						<img
							alt=""
							className="c-mx-1"
							data-qa-id="sideNavigationProductIcon"
							src={categoryImageUrl}
						/>
					</ClaySticker>

					<span
						className="c-ml-2 text-5"
						data-qa-id="sideNavigationLabel"
					>
						{label}
					</span>

					<SideNavigationSiteSelector
						eventName={siteAdministrationItemSelectedEventName}
						url={siteAdministrationItemSelectorUrl}
					/>

					{Liferay.FeatureFlags['LPD-57922'] && (
						<SideNavigationColorSchemeButton
							className="c-ml-2"
							colorScheme={colorScheme}
							colorSchemeSessionKey={colorSchemeSessionKey}
						/>
					)}
				</SidePanel.Title>
			</SidePanel.Header>

			<SidePanel.Body className="c-pt-2 c-px-0">
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
						{(item) => {
							if (typeof item === 'string') {
								return <span>{item}</span>;
							}

							return (
								<ClayVerticalNav.Item
									className={
										item.parentLabel
											? 'side-navigation-section-item'
											: undefined
									}
									data-canonical-name={item.canonicalName}
									href={item.href}
									items={item.items}
									key={item.id}
									textValue={item.label}
								>
									<SideNavigationItemContent
										item={item as SideNavigationItem}
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
			</SidePanel.Body>
		</SidePanel>
	);
}

export default SideNavigation;
