/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LayoutData} from '../../types/layout_data/LayoutData';
import {FragmentComposition} from '../actions/addFragmentComposition';
import {FragmentEntryLinkComment} from '../actions/addFragmentEntryLinkComment';
import {
	FragmentEntryLink,
	FragmentEntryLinkMap,
} from '../actions/addFragmentEntryLinks';
import {FragmentEntry} from '../actions/updateFragments';
import {config} from '../config/index';
import {PageContent} from '../utils/usePageContents';
import draftServiceFetch, {OnNetworkStatus} from './draftServiceFetch';
import serviceFetch from './serviceFetch';

export default {
	addComment({
		body,
		fragmentEntryLinkId,
		onNetworkStatus,
		parentCommentId,
	}: {
		body: string;
		fragmentEntryLinkId: string;
		onNetworkStatus: OnNetworkStatus;
		parentCommentId?: string;
	}) {
		const fetchBody: {
			body: string;
			fragmentEntryLinkId: string;
			parentCommentId?: string;
		} = {
			body,
			fragmentEntryLinkId,
		};

		if (parentCommentId) {
			fetchBody.parentCommentId = parentCommentId;
		}

		return draftServiceFetch<FragmentEntryLinkComment>(
			config.addFragmentEntryLinkCommentURL,
			{body: fetchBody},
			onNetworkStatus
		);
	},

	addFragmentComposition({
		description,
		fileEntryId,
		fragmentCollectionId,
		itemId,
		name,
		onNetworkStatus,
		saveInlineContent,
		saveMappingConfiguration,
		segmentsExperienceId,
	}: {
		description?: string;
		fileEntryId?: number | string;
		fragmentCollectionId?: string;
		itemId: string;
		name: string;
		onNetworkStatus: OnNetworkStatus;
		saveInlineContent?: boolean;
		saveMappingConfiguration?: boolean;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			fragmentComposition: FragmentComposition | null;
			url: string;
			valid: boolean;
		}>(
			config.addFragmentCompositionURL,
			{
				body: {
					description,
					fileEntryId,
					fragmentCollectionId,
					itemId,
					name,
					saveInlineContent,
					saveMappingConfiguration,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	addFragmentEntryLink({
		fragmentEntryKey,
		groupId,
		onNetworkStatus,
		parentItemId,
		position,
		segmentsExperienceId,
	}: {
		fragmentEntryKey: string;
		groupId: string;
		onNetworkStatus: OnNetworkStatus;
		parentItemId: string;
		position: number;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			addedItemId: string;
			fragmentEntryLink: FragmentEntryLink;
			layoutData: LayoutData;
		}>(
			config.addFragmentEntryLinkURL,
			{
				body: {
					fragmentEntryKey,
					groupId,
					parentItemId,
					position,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	addFragmentEntryLinks({
		fragmentEntryKey,
		groupId,
		onNetworkStatus,
		parentItemId,
		position,
		segmentsExperienceId,
	}: {
		fragmentEntryKey: string;
		groupId: string;
		onNetworkStatus: OnNetworkStatus;
		parentItemId: string;
		position: number;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			addedItemId: string;
			fragmentEntryLinks: FragmentEntryLinkMap;
			layoutData: LayoutData;
		}>(
			config.addFragmentEntryLinksURL,
			{
				body: {
					fragmentEntryKey,
					groupId,
					parentItemId,
					position,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	addStepperFragmentEntryLink({
		fragmentEntryKey,
		groupId,
		numberOfSteps,
		onNetworkStatus,
		parentItemId,
		position,
		segmentsExperienceId,
	}: {
		fragmentEntryKey: string;
		groupId: string;
		numberOfSteps: number;
		onNetworkStatus: OnNetworkStatus;
		parentItemId: string;
		position: number;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			addedItemId: string;
			fragmentEntryLink: FragmentEntryLink;
			layoutData: LayoutData;
		}>(
			config.addStepperFragmentEntryLinkURL,
			{
				body: {
					fragmentEntryKey,
					groupId,
					numberOfSteps,
					parentItemId,
					position,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	deleteComment({
		commentId,
		onNetworkStatus,
	}: {
		commentId: string;
		onNetworkStatus: OnNetworkStatus;
	}) {
		return draftServiceFetch<void>(
			config.deleteFragmentEntryLinkCommentURL,
			{body: {commentId}},
			onNetworkStatus
		);
	},

	duplicateItem({
		itemIds,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		itemIds: string[];
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			duplicatedFragmentEntryLinks: FragmentEntryLink[];
			duplicatedItemIds: string[];
			layoutData: LayoutData;
			restrictedItemIds: string[];
		}>(
			config.duplicateItemURL,
			{
				body: {
					itemIds,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	editComment({
		body,
		commentId,
		onNetworkStatus,
		resolved,
	}: {
		body: string;
		commentId: string;
		onNetworkStatus: OnNetworkStatus;
		resolved: boolean;
	}) {
		return draftServiceFetch<FragmentEntryLinkComment>(
			config.editFragmentEntryLinkCommentURL,
			{
				body: {
					body,
					commentId,
					resolved,
				},
			},
			onNetworkStatus
		);
	},

	pasteItems({
		itemIds,
		onNetworkStatus,
		parentItemId,
		segmentsExperienceId,
	}: {
		itemIds: string[];
		onNetworkStatus: OnNetworkStatus;
		parentItemId: string;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			copiedFragmentEntryLinks: FragmentEntryLink[];
			copiedItemIds: string[];
			layoutData: LayoutData;
			restrictedItemIds: string[];
		}>(
			config.copyItemsURL,
			{
				body: {
					itemIds,
					parentItemId,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	renderFragmentEntryLinksContent({
		data,
		languageId,
		segmentsExperienceId,
	}: {
		data: Array<{
			fragmentEntryLinkId: string;
			itemClassName?: string | null;
			itemClassPK?: string | null;
			itemExternalReferenceCode?: string | null;
		}>;
		languageId: Liferay.Language.Locale;
		segmentsExperienceId: string;
	}) {
		const body: {
			data: string;
			languageId: Liferay.Language.Locale;
			segmentsExperienceId: string;
		} = {
			data: JSON.stringify(data),
			languageId,
			segmentsExperienceId,
		};

		return serviceFetch<
			Array<
				Pick<
					FragmentEntryLink,
					'content' | 'editableTypes' | 'fragmentEntryLinkId'
				>
			>
		>(config.renderFragmentEntriesURL, {
			body,
		});
	},

	swapFragment({
		editableValues,
		fragmentEntryKey,
		fragmentEntryLinkId,
		groupId,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		editableValues: string;
		fragmentEntryKey: FragmentEntry['fragmentEntryKey'];
		fragmentEntryLinkId: FragmentEntryLink['fragmentEntryLinkId'];
		groupId?: string;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		const body: {
			editableValues: string;
			fragmentEntryKey: FragmentEntry['fragmentEntryKey'];
			fragmentEntryLinkId: FragmentEntryLink['fragmentEntryLinkId'];
			groupId?: string;
			segmentsExperienceId: string;
		} = {
			editableValues,
			fragmentEntryKey,
			fragmentEntryLinkId,
			segmentsExperienceId,
		};

		if (groupId) {
			body.groupId = groupId;
		}

		return draftServiceFetch<{
			fragmentEntryLink: FragmentEntryLink;
			layoutData: LayoutData;
		}>(
			config.swapFragmentEntryLinkURL,
			{
				body,
			},
			onNetworkStatus
		);
	},

	toggleFragmentHighlighted({
		fragmentEntryKey,
		groupId = '0',
		highlighted,
		onNetworkStatus,
	}: {
		fragmentEntryKey: string;
		groupId?: string;
		highlighted: boolean;
		onNetworkStatus: OnNetworkStatus;
	}) {
		return draftServiceFetch<{highlightedFragments: FragmentEntry[]}>(
			config.updateFragmentsHighlightedConfigurationURL,
			{
				body: {
					fragmentEntryKey,
					groupId,
					highlighted,
				},
			},
			onNetworkStatus
		);
	},

	updateConfigurationValues({
		editableValues,
		fragmentEntryLinkId,
		languageId,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		editableValues: FragmentEntryLink['editableValues'];
		fragmentEntryLinkId: string;
		languageId: Liferay.Language.Locale;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			fragmentEntryLink: FragmentEntryLink;
			layoutData: LayoutData;
			pageContents: PageContent[];
		}>(
			config.updateConfigurationValuesURL,
			{
				body: {
					editableValues: JSON.stringify(editableValues),
					fragmentEntryLinkId,
					languageId,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	updateEditableValues({
		editableValues,
		fragmentEntryLinkId,
		languageId,
		onNetworkStatus,
		segmentsExperienceId,
	}: {
		editableValues: FragmentEntryLink['editableValues'];
		fragmentEntryLinkId: string;
		languageId: Liferay.Language.Locale;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
	}) {
		return draftServiceFetch<{
			fragmentEntryLink: FragmentEntryLink;
			pageContents: PageContent[];
		}>(
			config.editFragmentEntryLinkURL,
			{
				body: {
					editableValues: JSON.stringify(editableValues),
					fragmentEntryLinkId,
					languageId,
					segmentsExperienceId,
				},
			},
			onNetworkStatus
		);
	},

	updateSetsOrder({
		fragmentCollectionKeys,
		onNetworkStatus,
		portletCategoryKeys,
	}: {
		fragmentCollectionKeys: string[] | null;
		onNetworkStatus: OnNetworkStatus;
		portletCategoryKeys: string[] | null;
	}) {
		return draftServiceFetch(
			config.updateFragmentPortletSetsSortURL,
			{
				body: {
					fragmentCollectionKeys:
						JSON.stringify(fragmentCollectionKeys) || null,
					portletCategoryKeys:
						JSON.stringify(portletCategoryKeys) || null,
				},
			},
			onNetworkStatus
		);
	},

	validateFragmentComposition({
		itemId,
		segmentsExperienceId,
	}: {
		itemId: string;
		segmentsExperienceId: string;
	}) {
		return serviceFetch<{
			valid: boolean;
		}>(config.validateFragmentCompositionURL, {
			body: {
				itemId,
				segmentsExperienceId,
			},
		});
	},
};
