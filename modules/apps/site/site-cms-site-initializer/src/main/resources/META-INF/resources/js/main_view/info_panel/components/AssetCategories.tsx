/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import ClayPanel from '@clayui/panel';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import React, {useCallback, useMemo, useState} from 'react';

import {
	IAssetObjectEntry,
	IGroupedTaxonomies,
	ITaxonomyCategoryFacade,
} from '../../../common/types/AssetType';
import {CategorizationInputSize} from './AssetCategorization';

import type {EntryCategorizationDTO} from '../services/ObjectEntryService';

const AssetCategories = ({
	cmsGroupId,
	collapsable = true,
	hasUpdatePermission,
	inputSize,
	objectEntry,
	updateObjectEntry,
}: {
	cmsGroupId: number | string;
	collapsable?: boolean;
	hasUpdatePermission?: boolean;
	inputSize?: CategorizationInputSize;
	objectEntry: IAssetObjectEntry | EntryCategorizationDTO;
	updateObjectEntry: (object: EntryCategorizationDTO) => void | Promise<void>;
}) => {
	const [value, setValue] = useState('');

	const apiURL = useMemo(() => {
		const {
			scopeId,
			systemProperties: {objectDefinitionBrief: {classNameId = -1} = {}},
		} = objectEntry;

		const assetTypes = ["'0'"];

		if (classNameId >= 0) {
			assetTypes.push("'" + classNameId + "'");
		}

		const filterStrings: string[] = [
			`assetTypes in (${assetTypes.join(', ')})`,
		];

		let endpoint = `asset-libraries/${scopeId}`;

		if (scopeId < 0) {
			endpoint = `sites/${cmsGroupId}`;

			filterStrings.push(`assetLibraries in ('${scopeId}')`);
		}

		const filterString = `?filter=${filterStrings.join(' and ')}`;

		return `${Liferay.ThemeDisplay.getPortalURL()}/o/headless-admin-taxonomy/v1.0/${endpoint}/taxonomy-categories${filterString}`;
	}, [cmsGroupId, objectEntry]);

	const groupedTaxonomies: IGroupedTaxonomies = useMemo(
		() =>
			(objectEntry.taxonomyCategoryBriefs || []).reduce(
				(
					groupedTaxonomies,
					{embeddedTaxonomyCategory: categoryBrief}
				): IGroupedTaxonomies => {
					const {id, taxonomyVocabularyId} = categoryBrief;

					const taxonomyCategories =
						groupedTaxonomies.taxonomyVocabularies[
							taxonomyVocabularyId
						] || [];

					taxonomyCategories.push(categoryBrief);

					return {
						taxonomyCategoryIds: [
							...groupedTaxonomies.taxonomyCategoryIds,
							parseInt(id as string, 10),
						],
						taxonomyVocabularies: {
							...groupedTaxonomies.taxonomyVocabularies,
							[taxonomyVocabularyId]: taxonomyCategories,
						},
					};
				},
				{
					taxonomyCategoryIds: [],
					taxonomyVocabularies: {},
				} as IGroupedTaxonomies
			),
		[objectEntry]
	);

	const addCategory = useCallback(
		async (category: any) => {
			const taxonomyCategoryId = parseInt(category.id, 10);

			if (
				groupedTaxonomies.taxonomyCategoryIds?.includes(
					taxonomyCategoryId
				)
			) {
				return;
			}

			const updated = [
				...groupedTaxonomies.taxonomyCategoryIds,
				taxonomyCategoryId,
			];

			await updateObjectEntry({
				lastAddedBrief: {embeddedTaxonomyCategory: category},
				taxonomyCategoryIds: updated,
				taxonomyCategoryIdsToAdd: updated,
			} as unknown as EntryCategorizationDTO);
		},
		[groupedTaxonomies.taxonomyCategoryIds, updateObjectEntry]
	);

	const removeCategory = useCallback(
		async (category: ITaxonomyCategoryFacade) => {
			const {taxonomyCategoryIds} = groupedTaxonomies;

			const index = taxonomyCategoryIds.findIndex(
				(id) => id === parseInt(category.id as string, 10)
			);

			if (index === -1) {
				return;
			}

			const taxonomyCategoryIdsToRemove = [];

			taxonomyCategoryIdsToRemove.push(taxonomyCategoryIds[index]);

			taxonomyCategoryIds.splice(index, 1);

			await updateObjectEntry({
				lastRemovedBrief: {embeddedTaxonomyCategory: category},
				taxonomyCategoryIds,
				taxonomyCategoryIdsToAdd: taxonomyCategoryIds,
				taxonomyCategoryIdsToRemove,
			} as EntryCategorizationDTO);
		},
		[groupedTaxonomies, updateObjectEntry]
	);

	return (
		<ClayPanel
			collapsable={collapsable}
			defaultExpanded={true}
			displayTitle={
				<ClayPanel.Title className="panel-title text-secondary">
					{Liferay.Language.get('categories')}
				</ClayPanel.Title>
			}
			displayType="unstyled"
			showCollapseIcon={collapsable}
		>
			<ClayPanel.Body>
				<ItemSelector<any>
					apiURL={apiURL}
					disabled={!hasUpdatePermission}
					estimateSize={49}
					locator={{
						id: 'id',
						label: 'name',
						value: 'externalReferenceCode',
					}}
					onChange={setValue}
					onItemsChange={(newItems: any) => {
						if (newItems[0]) {
							addCategory(newItems[0]);

							// The reason for this timeout is because of react's
							// batch rendering. Clay internals set the value of
							// the input, but we need to wait for the next 'tick' to set the value.

							setTimeout(() => setValue(''));
						}
					}}
					placeholder={Liferay.Language.get('add-category')}
					refetchOnActive
					sizing={inputSize}
					value={value}
				>
					{(item) => (
						<ItemSelector.Item
							key={item.name}
							textValue={item.name}
						>
							<div>
								<span className="font-weight-bold text-truncate">
									{item?.name}
								</span>

								<span
									className="text-1 text-secondary text-truncate text-uppercase"
									title={item?.path}
								>
									{item?.path}
								</span>
							</div>
						</ItemSelector.Item>
					)}
				</ItemSelector>

				{groupedTaxonomies.taxonomyVocabularies &&
					Object.entries(groupedTaxonomies?.taxonomyVocabularies).map(
						([, vocabularyCategories], index) => {
							const vocabularyName =
								vocabularyCategories[0].parentTaxonomyVocabulary
									.name;

							return vocabularyCategories.length ? (
								<div className="pt-3" key={index}>
									<p className="font-weight-semi-bold vocabulary-name">
										{vocabularyName}
									</p>

									{vocabularyCategories.map(
										(category: ITaxonomyCategoryFacade) => (
											<Label
												closeButtonProps={{
													'aria-label':
														Liferay.Language.get(
															'close'
														),
													'disabled':
														!hasUpdatePermission,
													'onClick': async (
														event
													) => {
														event.preventDefault();

														await removeCategory(
															category
														);
													},
													'title':
														Liferay.Language.get(
															'close'
														),
												}}
												displayType="secondary"
												key={`${category.taxonomyVocabularyId}_${category.id}`}
											>
												{category.name}
											</Label>
										)
									)}
								</div>
							) : null;
						}
					)}
			</ClayPanel.Body>
		</ClayPanel>
	);
};

export default AssetCategories;
