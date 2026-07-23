/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EEntityFieldType, IGroupedFilterConfig, TFilterConfig} from './types';

/**
 * Returns the full set of filters for CMS Item Selector data sets.
 * These filters align with the CMS "Contents" data set filters.
 */
export function getCMSItemSelectorFilters(
	groupId: number | string
): TFilterConfig[] {
	return [
		{
			apiURL: "/o/headless-asset-library/v1.0/asset-libraries?filter=type eq 'Space'",
			entityFieldType: EEntityFieldType.COLLECTION_INTEGER,
			id: 'groupIds',
			itemKey: 'siteId',
			itemLabel: 'name',
			label: Liferay.Language.get('space'),
			multiple: true,
			type: 'selection',
		},
		{
			apiURL: "/o/object-admin/v1.0/object-definitions?filter=objectFolderExternalReferenceCode eq 'L_CMS_FILE_TYPES'",
			entityFieldType: EEntityFieldType.STRING,
			id: 'objectDefinitionExternalReferenceCode',
			itemKey: 'externalReferenceCode',
			itemLabel: 'label.LANG',
			label: Liferay.Language.get('type'),
			multiple: true,
			type: 'selection',
		},
		{
			apiURL: `/o/headless-admin-taxonomy/v1.0/sites/${groupId}/taxonomy-categories`,
			entityFieldType: EEntityFieldType.COLLECTION_INTEGER,
			id: 'taxonomyCategoryIds',
			itemKey: 'id',
			itemLabel: 'name',
			label: Liferay.Language.get('category'),
			multiple: true,
			type: 'selection',
		},
		{
			apiURL: `/o/headless-admin-taxonomy/v1.0/sites/${groupId}/keywords`,
			entityFieldType: EEntityFieldType.COLLECTION_STRING,
			id: 'keywords',
			itemKey: 'name',
			itemLabel: 'name',
			label: Liferay.Language.get('tags'),
			multiple: true,
			type: 'selection',
		},
		{
			apiURL: `/o/headless-admin-user/v1.0/user-accounts`,
			autocompleteEnabled: true,
			entityFieldType: EEntityFieldType.INTEGER,
			id: 'creatorId',
			itemKey: 'id',
			itemLabel: 'name',
			label: Liferay.Language.get('author'),
			multiple: true,
			type: 'selection',
		},
		{
			entityFieldType: EEntityFieldType.INTEGER,
			id: 'status',
			items: [
				{
					label: Liferay.Language.get('approved'),
					value: '0',
				},
				{
					label: Liferay.Language.get('pending'),
					value: '1',
				},
				{
					label: Liferay.Language.get('draft'),
					value: '2',
				},
				{
					label: Liferay.Language.get('expired'),
					value: '3',
				},
				{
					label: Liferay.Language.get('scheduled'),
					value: '8',
				},
			],
			label: Liferay.Language.get('status'),
			multiple: true,
			type: 'selection',
		},
		{
			entityFieldType: EEntityFieldType.DATE_TIME,
			id: 'dateCreated',
			label: Liferay.Language.get('create-date'),
			type: 'dateRange',
		},
		{
			entityFieldType: EEntityFieldType.DATE_TIME,
			id: 'dateDisplay',
			label: Liferay.Language.get('display-date'),
			type: 'dateRange',
		},
		{
			entityFieldType: EEntityFieldType.DATE_TIME,
			id: 'dateExpiration',
			label: Liferay.Language.get('expiration-date'),
			type: 'dateRange',
		},
		{
			entityFieldType: EEntityFieldType.DATE_TIME,
			id: 'dateModified',
			label: Liferay.Language.get('modified-date'),
			type: 'dateRange',
		},
		{
			entityFieldType: EEntityFieldType.DATE_TIME,
			id: 'dateReview',
			label: Liferay.Language.get('review-date'),
			type: 'dateRange',
		},
	];
}

/**
 * Returns the grouped filters configuration for CMS Item Selector data sets.
 *
 * The space/group filter ID can be customized via spaceFilterId so the same
 * groupings can be reused by data sets that register the space filter under a
 * different ID (e.g. the CMS "Contents" FDS uses "scopeGroupId").
 */
export function getCMSItemSelectorGroupedFilters(
	spaceFilterId: 'groupIds' | 'scopeGroupId' = 'groupIds'
): IGroupedFilterConfig[] {
	return [
		{
			filters: [
				spaceFilterId,
				'objectDefinitionExternalReferenceCode',
				'taxonomyCategoryIds',
				'keywords',
				'cmpProjectObjectEntryIds',
				'extension',
				'creatorId',
				'status',
			],
			label: Liferay.Language.get('filter-by'),
		},
		{
			filters: [
				'dateCreated',
				'dateDisplay',
				'dateExpiration',
				'dateModified',
				'dateReview',
			],
			label: Liferay.Language.get('filter-by-date'),
		},
	];
}
