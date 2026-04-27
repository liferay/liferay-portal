/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getCMSItemSelectorFilters,
	getCMSItemSelectorGroupedFilters,
} from '../src/main/resources/META-INF/resources/item_selector/getCMSItemSelectorFilters';
import {
	EEntityFieldType,
	ISelectionFilterConfig,
} from '../src/main/resources/META-INF/resources/item_selector/types';

const Liferay = {
	Language: {
		get: (key: string) => key,
	},
};

(global as any).Liferay = Liferay;

describe('getCMSItemSelectorFilters', () => {
	it('returns the correct filter configurations', () => {
		const filters = getCMSItemSelectorFilters(12345);

		expect(filters.length).toBe(12);

		expect(filters.map((f) => f.id)).toEqual([
			'groupIds',
			'objectDefinitionExternalReferenceCode',
			'taxonomyCategoryIds',
			'keywords',
			'creatorId',
			'status',
			'dateCreated',
			'dateDisplay',
			'dateExpiration',
			'dateModified',
			'datePublish',
			'dateReview',
		]);

		const typeFilter = filters.find(
			(f) => f.id === 'objectDefinitionExternalReferenceCode'
		) as ISelectionFilterConfig;

		expect(typeFilter?.apiURL).toContain(
			"objectFolderExternalReferenceCode eq 'L_CMS_FILE_TYPES'"
		);
		expect(typeFilter?.entityFieldType).toBe(EEntityFieldType.STRING);
		expect(typeFilter?.itemLabel).toBe('label.LANG');

		const categoryFilter = filters.find(
			(f) => f.id === 'taxonomyCategoryIds'
		) as ISelectionFilterConfig;
		expect(categoryFilter?.entityFieldType).toBe(
			EEntityFieldType.COLLECTION
		);

		const authorFilter = filters.find(
			(f) => f.id === 'creatorId'
		) as ISelectionFilterConfig;
		expect(authorFilter?.autocompleteEnabled).toBe(true);

		const statusFilter = filters.find(
			(f) => f.id === 'status'
		) as ISelectionFilterConfig;

		expect(statusFilter.items).toBeDefined();
		expect(statusFilter.items?.length).toBe(5);
	});

	it('returns the correct grouped filter configurations', () => {
		const groupedFilters = getCMSItemSelectorGroupedFilters();

		expect(groupedFilters.length).toBe(2);
		expect(groupedFilters[0].filters.length).toBe(6);
		expect(groupedFilters[0].filters[0]).toBe('groupIds');
		expect(groupedFilters[1].filters.length).toBe(6);
	});

	it('lets the caller override the space filter id', () => {
		const groupedFilters = getCMSItemSelectorGroupedFilters('scopeGroupId');

		expect(groupedFilters[0].filters[0]).toBe('scopeGroupId');
	});
});
