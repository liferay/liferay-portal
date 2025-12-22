/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {
	IAssetTaxonomyVocabulary,
	ITaxonomyCategoryBrief,
} from '../../../common/types/AssetType';
import {IBulkActionFDSData} from '../../../common/types/BulkActionTask';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../common/utils/constants';
import {EntryCategorizationDTO} from '../../info_panel/services/ObjectEntryService';

export function getModalDescription(
	selectedData: IBulkActionFDSData,
	type: string = 'TaxonomyCategoryBulkAction'
) {
	if (selectedData.selectAll) {
		return type === 'TaxonomyCategoryBulkAction'
			? Liferay.Language.get(
					'you-are-editing-the-common-categories-for-all-the-items.-select-edit-or-replace-current-categories'
				)
			: Liferay.Language.get(
					'you-are-editing-the-common-tags-for-all-the-items.-select-edit-or-replace-current-tags'
				);
	}

	const size = selectedData?.items?.length || 0;

	if (size === 1) {
		return type === 'TaxonomyCategoryBulkAction'
			? Liferay.Language.get(
					'you-are-editing-the-tags-for-the-selected-item'
				)
			: Liferay.Language.get(
					'you-are-editing-the-tags-for-the-selected-item'
				);
	}

	return sub(
		type === 'TaxonomyCategoryBulkAction'
			? Liferay.Language.get(
					'you-are-editing-the-common-categories-for-x-items.-select-edit-or-replace-current-categories'
				)
			: Liferay.Language.get(
					'you-are-editing-the-common-tags-for-x-items.-select-edit-or-replace-current-tags'
				),
		size
	);
}

export function getScopeAttributes(
	{items = []}: IBulkActionFDSData,
	filterFolders = false,
	scopeOnly = false
): Pick<EntryCategorizationDTO, 'scopeId' | 'systemProperties'> {
	const scopeAttributes = {
		scopeId: -1,
	} as Pick<EntryCategorizationDTO, 'scopeId' | 'systemProperties'>;

	let selectedItems = [...items];

	if (filterFolders) {
		selectedItems = selectedItems.filter(
			({entryClassName}) =>
				entryClassName !== OBJECT_ENTRY_FOLDER_CLASS_NAME
		);
	}

	const scopeIds = new Set([
		...selectedItems.map(({embedded: {scopeId}}) => scopeId),
	]);

	if (scopeIds.size === 1) {
		scopeAttributes.scopeId = [...scopeIds][0];
	}

	if (!scopeOnly) {
		const classNameIds = new Set([
			...selectedItems.map(
				({
					embedded: {
						systemProperties: {objectDefinitionBrief = {}},
					},
				}) => objectDefinitionBrief?.classNameId || 0
			),
		]);

		scopeAttributes.systemProperties = {
			objectDefinitionBrief: {
				classNameId:
					classNameIds.size === 1 ? [...classNameIds][0] : -1,
			},
		} as any;
	}

	return scopeAttributes as Pick<
		EntryCategorizationDTO,
		'scopeId' | 'systemProperties'
	>;
}

export function toTaxonomyCategoryDTO(
	vocabularies: IAssetTaxonomyVocabulary[] = []
): Pick<
	EntryCategorizationDTO,
	'taxonomyCategoryBriefs' | 'taxonomyCategoryIds'
> {
	return vocabularies.reduce(
		(dto, vocabulary) => {
			const {name, taxonomyCategories, taxonomyVocabularyId} = vocabulary;

			if (taxonomyCategories.length) {
				taxonomyCategories.forEach(
					({taxonomyCategoryId, taxonomyCategoryName}) => {
						dto.taxonomyCategoryBriefs.push({
							embeddedTaxonomyCategory: {
								id: taxonomyCategoryId,
								name: taxonomyCategoryName,
								parentTaxonomyVocabulary: {
									id: taxonomyVocabularyId,
									name,
								},
								taxonomyVocabularyId,
							},
							taxonomyCategoryId,
							taxonomyCategoryName,
						} as ITaxonomyCategoryBrief);

						dto.taxonomyCategoryIds.push(taxonomyCategoryId);
					}
				);
			}

			return dto;
		},
		{
			taxonomyCategoryBriefs: [],
			taxonomyCategoryIds: [],
		} as Pick<
			EntryCategorizationDTO,
			'taxonomyCategoryBriefs' | 'taxonomyCategoryIds'
		>
	);
}
