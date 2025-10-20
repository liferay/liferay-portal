/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CollectionItemLayoutDataItem} from '../../types/layout_data/CollectionItemLayoutDataItem';
import {FormLayoutDataItem} from '../../types/layout_data/FormLayoutDataItem';
import {LayoutData, LayoutDataItem} from '../../types/layout_data/LayoutData';
import {
	FragmentEntryLink,
	FragmentEntryLinkMap,
} from '../actions/addFragmentEntryLinks';
import {LayoutDataItemType} from '../config/constants/layoutDataItemTypes';
import {config} from '../config/index';
import {PageContent} from '../utils/usePageContents';
import draftServiceFetch, {OnNetworkStatus} from './draftServiceFetch';
import serviceFetch from './serviceFetch';

export interface StyleBookTokenValue {
	cssVariable: string;
	editorType: string;
	label: string;
	name: string;
	tokenCategoryLabel: string;
	tokenSetLabel: string;
	value: string;
}

export type StyleBookTokenValueMap = Record<string, StyleBookTokenValue>;

export interface Layout {
	groupId: string;
	layoutId: string;
	layoutUuid: string;
	privateLayout: boolean;
	title: string;
}

export default {
	addItem({
		itemType,
		onNetworkStatus,
		parentItemId,
		position,
		segmentsExperienceId,
	}: {
		itemType: LayoutDataItemType;
		onNetworkStatus: OnNetworkStatus;
		parentItemId: string;
		position: number;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			addedItemId: string;
			layoutData: LayoutData;
		}>(
			config.addItemURL,
			{
				body: {
					itemType,
					parentItemId,
					position,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	changeMasterLayout({
		masterLayoutPlid,
		onNetworkStatus,
	}: {
		masterLayoutPlid: string;
		onNetworkStatus: OnNetworkStatus;
	}) {
		return draftServiceFetch<
			| {
					fragmentEntryLinks: FragmentEntryLinkMap;
					masterLayoutData: LayoutData;
			  }
			| undefined
		>(
			config.changeMasterLayoutURL,
			{body: {masterLayoutPlid}},
			onNetworkStatus
		);
	},

	changeStyleBookEntry({
		onNetworkStatus,
		styleBookEntryERC,
	}: {
		onNetworkStatus: OnNetworkStatus;
		styleBookEntryERC: string;
	}) {
		return draftServiceFetch<{tokenValues: StyleBookTokenValueMap}>(
			config.changeStyleBookEntryURL,
			{body: {styleBookEntryERC}},
			onNetworkStatus
		);
	},

	getLayoutFriendlyURL(layout: Layout) {
		return serviceFetch<{friendlyURL: string}>(
			config.getLayoutFriendlyURL,
			{body: layout}
		);
	},

	markItemForDeletion({
		itemIds,
		onNetworkStatus,
		portletIds = [],
		segmentsExperienceId,
	}: {
		itemIds: string[];
		onNetworkStatus: OnNetworkStatus;
		portletIds?: string[];
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			layoutData: LayoutData;
			pageContents: PageContent[];
		}>(
			config.markItemForDeletionURL,
			{
				body: {
					itemIds,
					portletIds,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	moveItems({
		itemIds,
		onNetworkStatus,
		parentItemIds,
		positions,
		segmentsExperienceId,
	}: {
		itemIds: string[];
		onNetworkStatus: OnNetworkStatus;
		parentItemIds: string[];
		positions: number[];
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<LayoutData>(
			config.moveItemsURL,
			{
				body: {
					itemIds,
					parentItemIds,
					positions,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	moveStepper({
		fragmentEntryLinkId,
		itemId,
		numberOfSteps,
		onNetworkStatus,
		parentItemId,
		position,
		segmentsExperienceId,
	}: {
		fragmentEntryLinkId: FragmentEntryLink['fragmentEntryLinkId'];
		itemId: string;
		numberOfSteps: number;
		onNetworkStatus: OnNetworkStatus;
		parentItemId: string;
		position: number;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<LayoutData>(
			config.moveStepperFragmentEntryLinkURL,
			{
				body: {
					fragmentEntryLinkId,
					itemId,
					numberOfSteps,
					parentItemId,
					position,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	restoreCollectionDisplayConfig({
		filterFragmentEntryLinks,
		itemConfig,
		itemId,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		filterFragmentEntryLinks: Array<{
			editableValues: FragmentEntryLink['editableValues'];
			fragmentEntryLinkId: string;
		}>;
		itemConfig: CollectionItemLayoutDataItem['config'];
		itemId: string;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<void>(
			config.restoreCollectionDisplayConfigURL,
			{
				body: {
					filterFragmentEntryLinks: JSON.stringify(
						filterFragmentEntryLinks
					),
					itemConfig: JSON.stringify(itemConfig),
					itemId,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	undoUpdateFormConfig({
		addedItemIds,
		itemConfig,
		itemId,
		movedItemIds,
		onNetworkStatus,
		removedItemIds,
		segmentsExperienceId,
		stepperFragmentEntryLinkId,
	}: {
		addedItemIds: string[];
		itemConfig: FormLayoutDataItem['config'];
		itemId: string;
		movedItemIds: {itemId: string; parentId: string}[];
		onNetworkStatus: OnNetworkStatus;
		removedItemIds: string[];
		segmentsExperienceId: string;
		stepperFragmentEntryLinkId?: FragmentEntryLink['fragmentEntryLinkId'];
	}) {
		return draftServiceFetch<{
			fragmentEntryLinks: FragmentEntryLinkMap;
			layoutData: LayoutData;
		}>(
			config.undoUpdateFormConfigURL,
			{
				body: {
					addedItemIds,
					config: JSON.stringify(itemConfig),
					itemId,
					movedItemIds: JSON.stringify(movedItemIds),
					removedItemIds,
					segmentsExperienceId,
					stepperFragmentEntryLinkId,
				},
			},
			onNetworkStatus
		);
	},

	unmarkItemsForDeletion({
		itemIds,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		itemIds: string[];
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			layoutData: LayoutData;
			pageContents: PageContent[];
		}>(
			config.unmarkItemsForDeletionURL,
			{
				body: {
					itemIds: JSON.stringify(itemIds),
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	updateCollectionDisplayConfig({
		itemConfig,
		itemId,
		languageId,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		itemConfig: CollectionItemLayoutDataItem['config'];
		itemId: string;
		languageId: Liferay.Language.Locale;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			fragmentEntryLinks: FragmentEntryLink[];
			layoutData: LayoutData;
			pageContents: PageContent[];
		}>(
			config.updateCollectionDisplayConfigURL,
			{
				body: {
					itemConfig: JSON.stringify(itemConfig),
					itemId,
					languageId,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	updateItemConfig({
		itemConfig,
		itemIds,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		itemConfig: LayoutDataItem['config'];
		itemIds: string[];
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			layoutData: LayoutData;
			pageContents: PageContent[];
		}>(
			config.updateItemConfigURL,
			{
				body: {
					itemConfig: JSON.stringify(itemConfig),
					itemIds,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	updateLayoutData({
		layoutData,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		layoutData: LayoutData;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<void>(
			config.updateLayoutPageTemplateDataURL,
			{
				body: {
					data: JSON.stringify(layoutData),
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	updateRowColumns({
		itemId,
		numberOfColumns,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		itemId: string;
		numberOfColumns: number;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			layoutData: LayoutData;
			pageContents: PageContent[];
		}>(
			config.updateRowColumnsURL,
			{
				body: {
					itemId,
					numberOfColumns,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},
};
