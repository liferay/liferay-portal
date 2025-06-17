/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import {
	MarketplaceButton,
	SearchForm,
	SearchResultsMessage,
	isNullOrUndefined,
} from '@liferay/layout-js-components-web';
import {useSessionState} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useMemo, useState} from 'react';

import {FRAGMENTS_DISPLAY_STYLES} from '../../../app/config/constants/fragmentsDisplayStyles';
import {HIGHLIGHTED_COLLECTION_ID} from '../../../app/config/constants/highlightedCollectionId';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../app/config/constants/layoutDataItemTypes';
import {config} from '../../../app/config/index';
import {useSelector} from '../../../app/contexts/StoreContext';
import {useLoadWidgets} from '../../../app/contexts/WidgetsContext';
import SidebarPanelHeader from '../../../common/components/SidebarPanelHeader';
import {TABS_IDS} from '../config/constants/tabsIds';
import SearchResultsPanel from './SearchResultsPanel';
import TabsPanel from './TabsPanel';
import {ReorderSetsModal} from './reorder_sets_modal/ReorderSetsModal';

export const COLLECTION_IDS = {
	fragments: 0,
	widgets: 1,
};

const collectionFilter = (collections, searchValue) => {
	const searchValueLowerCase = searchValue.toLowerCase();

	const itemFilter = (item) =>
		item.label.toLowerCase().indexOf(searchValueLowerCase) !== -1;

	const hasChildren = (collection) => {
		if (collection.children?.length) {
			return true;
		}

		return collection.collections?.some(hasChildren) ?? false;
	};

	return collections
		.reduce((acc, collection) => {
			if (collection.collectionId === HIGHLIGHTED_COLLECTION_ID) {
				return acc;
			}

			if (itemFilter(collection)) {
				return [...acc, collection];
			}
			else {
				const updateCollection = {
					...collection,
					children: collection.children?.filter(
						(item) =>
							itemFilter(item) ||
							item.portletItems?.some(itemFilter)
					),
					...(collection.collections?.length && {
						collections: collectionFilter(
							collection.collections,
							searchValueLowerCase
						),
					}),
				};

				return [...acc, updateCollection];
			}
		}, [])
		.filter(hasChildren);
};

export function normalizeWidget(widget) {
	return {
		data: {
			portletId: widget.portletId,
			portletItemId: widget.portletItemId || null,
		},
		deprecated: widget.deprecated,
		disabled: !widget.instanceable && (widget.used || widget.embedded),
		highlighted: widget.highlighted,
		icon: widget.instanceable ? 'square-hole-multi' : 'square-hole',
		itemId: widget.portletId,
		label: widget.title,
		portletItems: widget.portletItems?.length
			? widget.portletItems.map(normalizeWidget)
			: null,
		preview: '',
		type: LAYOUT_DATA_ITEM_TYPES.fragment,
	};
}

const normalizeCollection = (collection) => {
	const normalizedElement = {
		children: collection.portlets.map(normalizeWidget),
		collectionId: collection.path,
		label: collection.title,
	};

	if (collection.categories?.length) {
		normalizedElement.collections =
			collection.categories.map(normalizeCollection);
	}

	return normalizedElement;
};

const normalizeFragmentEntry = (fragmentEntry) => ({
	data: {
		fieldTypes: fragmentEntry.fieldTypes,
		fragmentEntryKey: fragmentEntry.fragmentEntryKey,
		groupId: fragmentEntry.groupId,
		type: fragmentEntry.type,
	},
	highlighted: fragmentEntry.highlighted,
	icon: fragmentEntry.icon,
	itemId: fragmentEntry.fragmentEntryKey,
	label: fragmentEntry.name,
	preview: fragmentEntry.imagePreviewURL,
	type: fragmentEntry.itemType || LAYOUT_DATA_ITEM_TYPES.fragment,
});

export default function FragmentsSidebar() {
	const fragments = useSelector((state) => state.fragments);
	const widgets = useSelector((state) => state.widgets);
	const permissions = useSelector((state) => state.permissions);

	const loadWidgets = useLoadWidgets();

	const [loadingWidgets, setLoadingWidgets] = useState(false);

	const [activeTabId, setActiveTabId] = useSessionState(
		`${config.portletNamespace}_fragments-sidebar_active-tab-id`,
		0
	);

	const [displayStyle, setDisplayStyle] = useSessionState(
		'FRAGMENTS_DISPLAY_STYLE_KEY',
		FRAGMENTS_DISPLAY_STYLES.LIST
	);

	const [searchValue, setSearchValue] = useState(null);
	const [showReorderModal, setShowReorderModal] = useState(false);

	const tabs = useMemo(
		() => [
			{
				collections: fragments.map((collection) => ({
					children: collection.fragmentEntries.map((fragmentEntry) =>
						normalizeFragmentEntry(fragmentEntry)
					),
					collectionId: collection.fragmentCollectionId,
					deprecated: collection.deprecated,
					label: collection.name,
				})),
				id: COLLECTION_IDS.fragments,
				label: Liferay.Language.get('fragments'),
			},
			{
				collections: widgets
					? widgets.map((collection) =>
							normalizeCollection(collection)
						)
					: [],
				id: COLLECTION_IDS.widgets,
				label: Liferay.Language.get('widgets'),
			},
		],
		[fragments, widgets]
	);

	const filteredTabs = useMemo(
		() =>
			searchValue
				? tabs
						.map((tab) => ({
							...tab,
							collections: collectionFilter(
								tab.collections,
								searchValue
							),
						}))
						.filter((item) => item.collections.length)
				: tabs,
		[tabs, searchValue]
	);

	const displayStyleButtonDisabled =
		searchValue || activeTabId === COLLECTION_IDS.widgets;

	const numberOfResults = useMemo(
		() =>
			isNullOrUndefined(searchValue)
				? null
				: filteredTabs.flatMap((tab) =>
						tab.collections.flatMap(
							(collection) => collection.children
						)
					).length,
		[filteredTabs, searchValue]
	);

	useEffect(() => {
		if (widgets) {
			setLoadingWidgets(false);
		}
		else if (searchValue || activeTabId === TABS_IDS.widgets) {
			setLoadingWidgets(true);

			loadWidgets();
		}
	}, [activeTabId, loadWidgets, searchValue, widgets]);

	const viewButtonLabel = sub(
		Liferay.Language.get('switch-to-x-view'),
		displayStyle === FRAGMENTS_DISPLAY_STYLES.LIST
			? Liferay.Language.get('card')
			: Liferay.Language.get('list[noun]')
	);

	return (
		<>
			<SidebarPanelHeader>
				{Liferay.Language.get('components')}
			</SidebarPanelHeader>

			<SearchResultsMessage numberOfResults={numberOfResults} />

			<div className="d-flex flex-column page-editor__sidebar__fragments-widgets-panel">
				<div className="align-items-center d-flex flex-shrink-0 justify-content-between mb-3 px-3">
					<SearchForm
						className="flex-grow-1 mb-0"
						label={Liferay.Language.get(
							'search-fragments-and-widgets'
						)}
						onChange={setSearchValue}
					/>

					<ClayDropDownWithItems
						className="flex-shrink-0"
						items={[
							{
								label: Liferay.Language.get('reorder-sets'),
								onClick: () => {
									setShowReorderModal(true);
								},
								symbolLeft: 'order-arrow',
							},
							{
								disabled: displayStyleButtonDisabled,
								label: viewButtonLabel,
								onClick: () => {
									setDisplayStyle(
										displayStyle ===
											FRAGMENTS_DISPLAY_STYLES.LIST
											? FRAGMENTS_DISPLAY_STYLES.CARDS
											: FRAGMENTS_DISPLAY_STYLES.LIST
									);
								},
								symbolLeft:
									displayStyleButtonDisabled ||
									displayStyle ===
										FRAGMENTS_DISPLAY_STYLES.LIST
										? 'cards2'
										: 'list',
							},
						]}
						renderMenuOnClick
						trigger={
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'components-options'
								)}
								className="components-options ml-2"
								data-tooltip-align="top"
								displayType="unstyled"
								size="sm"
								symbol="ellipsis-v"
								title={Liferay.Language.get(
									'components-options'
								)}
							/>
						}
					/>

					{Liferay.FeatureFlags['LPD-34938'] &&
					permissions.VIEW_MARKETPLACE ? (
						<MarketplaceButton
							body={Liferay.Language.get(
								'we-are-excited-to-share-that-marketplace-is-now-part-of-page-builder'
							)}
							className="ml-1"
							fragmentPortletNamespace={
								config.fragmentPortletNamespace
							}
							fragmentsImportURL={config.fragmentsImportURL}
							heading={Liferay.Language.get(
								'marketplace-is-now-in-page-builder'
							)}
							permissions={{
								installFreeApps:
									permissions.INSTALL_FREE_BUNDLED_APPS_MARKETPLACE,
								manageFragmentsEntries:
									permissions.MANAGE_FRAGMENT_ENTRIES,
								purchaseAndInstallPaidApps:
									permissions.PURCHASE_AND_INSTALL_PAID_APPS_MARKETPLACE,
							}}
							portletNamespace={config.portletNamespace}
						/>
					) : null}
				</div>

				{searchValue ? (
					<SearchResultsPanel
						filteredTabs={filteredTabs}
						loading={loadingWidgets}
						searchValue={searchValue}
					/>
				) : (
					<TabsPanel
						activeTabId={activeTabId}
						displayStyle={displayStyle}
						setActiveTabId={setActiveTabId}
						tabs={tabs}
					/>
				)}
			</div>

			{showReorderModal && (
				<ReorderSetsModal
					onCloseModal={() => setShowReorderModal(false)}
				/>
			)}
		</>
	);
}
