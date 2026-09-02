/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import Label from '@clayui/label';
import ClayPanel from '@clayui/panel';
import {AIAssistantTriggerButton} from '@liferay/ai-hub-cell-js-components-web';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import React, {useCallback, useId, useMemo, useState} from 'react';

import ErrorFeedback from '../../../common/components/forms/ErrorFeedback';
import {
	IAssetObjectEntry,
	IGroupedTaxonomies,
	ITaxonomyCategoryFacade,
} from '../../../common/types/AssetType';
import {AI_ASSISTANT_TOOLBAR_TRIGGER_ID} from '../../../common/utils/constants';
import {CategorizationInputSize} from './AssetCategorization';
import {
	AUTO_CATEGORIZE_AGENT,
	CATEGORIZE_EVENT,
} from './categorizationAgentEvents';

import type {EntryCategorizationDTO} from '../services/ObjectEntryService';

/**
 * Renders an asset's categories in one of two modes:
 *
 * - Generic mode (no vocabularyId): shows categories from every vocabulary,
 *   grouped under vocabulary-name headers. Pass systemVocabularyIds to hide
 *   the vocabularies that have their own dedicated panel (Personas, Funnel
 *   Stage) so they are not shown twice.
 *
 * - Scoped mode (vocabularyId set): shows the categories of that single
 *   vocabulary only, with no header, and ignores systemVocabularyIds.
 */
const AssetCategories = ({
	cmsGroupId,
	collapsable = true,
	errorMessage,
	getContent,
	hasUpdatePermission,
	inputSize,
	objectEntry,
	placeholder,
	systemVocabularyIds,
	title,
	updateObjectEntry,
	vocabularyId,
}: {
	cmsGroupId: number | string;
	collapsable?: boolean;
	errorMessage?: string;
	getContent?: (
		objectDefinitionExternalReferenceCode?: string
	) => Promise<string>;
	hasUpdatePermission?: boolean;
	inputSize?: CategorizationInputSize;
	objectEntry: IAssetObjectEntry | EntryCategorizationDTO;
	placeholder?: string;
	systemVocabularyIds?: number[];
	title?: string;
	updateObjectEntry: (object: EntryCategorizationDTO) => void | Promise<void>;
	vocabularyId?: number;
}) => {
	const [value, setValue] = useState('');

	const feedbackId = useId();

	const apiURL = useMemo(() => {
		if (vocabularyId) {
			return `${Liferay.ThemeDisplay.getPortalURL()}/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${vocabularyId}/taxonomy-categories`;
		}

		const {
			scopeId,
			systemProperties: {objectDefinitionBrief: {classNameId = -1} = {}},
		} = objectEntry;

		if (!Number.isInteger(scopeId)) {
			return '';
		}

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
	}, [cmsGroupId, objectEntry, vocabularyId]);

	const isVisibleVocabulary = useCallback(
		(currentVocabularyId: number) => {
			if (vocabularyId) {
				return currentVocabularyId === vocabularyId;
			}

			return !systemVocabularyIds?.includes(currentVocabularyId);
		},
		[systemVocabularyIds, vocabularyId]
	);

	// Hide categories from the system vocabularies (Personas, Funnel Stage)
	// in the generic dropdown, since they have their own dedicated panels.

	const filterDropdownItem = useCallback(
		(item: any) =>
			!systemVocabularyIds?.includes(
				Number(item?.parentTaxonomyVocabulary?.id)
			),
		[systemVocabularyIds]
	);

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

	const selectedCategories = useMemo(
		() =>
			Object.entries(groupedTaxonomies.taxonomyVocabularies)
				.filter(([currentVocabularyId]) =>
					isVisibleVocabulary(Number(currentVocabularyId))
				)
				.flatMap(([, vocabularyCategories]) => vocabularyCategories),
		[groupedTaxonomies.taxonomyVocabularies, isVisibleVocabulary]
	);

	const handleGenerateCategories = useCallback(async () => {
		const {
			scopeId,
			systemProperties: {
				objectDefinitionBrief: {
					classNameId = -1,
					externalReferenceCode,
				} = {},
			},
		} = objectEntry;

		Liferay.fire(CATEGORIZE_EVENT, {
			agent: AUTO_CATEGORIZE_AGENT,
			classNameId,
			cmsGroupId,
			content:
				(await getContent?.(externalReferenceCode)) ||
				(objectEntry as IAssetObjectEntry).contentRawText ||
				'',
			currentCategoryIds: (objectEntry.taxonomyCategoryBriefs ?? []).map(
				(brief) => brief.taxonomyCategoryId
			),
			scopeId,
		});
	}, [cmsGroupId, getContent, objectEntry]);

	return (
		<ClayPanel
			collapsable={collapsable}
			collapseHeaderClassNames="text-secondary"
			defaultExpanded={true}
			displayTitle={title ?? Liferay.Language.get('categories')}
			displayType="unstyled"
			showCollapseIcon={collapsable}
		>
			<ClayPanel.Body>
				<div className="align-items-end d-flex">
					<div
						className={
							errorMessage
								? 'flex-grow-1 form-group has-error'
								: 'flex-grow-1'
						}
					>
						{apiURL ? (
							<ItemSelector<any>
								apiURL={apiURL}
								aria-describedby={
									errorMessage ? feedbackId : undefined
								}
								disabled={!hasUpdatePermission}
								estimateSize={49}
								items={selectedCategories}
								itemsFilter={
									vocabularyId
										? undefined
										: filterDropdownItem
								}
								locator={{
									id: 'id',
									label: 'name',
									value: 'id',
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
								placeholder={
									placeholder ??
									Liferay.Language.get('add-category')
								}
								refetchOnActive
								sizing={inputSize}
								value={value}
							>
								{(item) => (
									<ItemSelector.Item
										key={item.id}
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
						) : null}
					</div>

					{!vocabularyId &&
					Liferay.FeatureFlags?.['LPD-62272'] &&
					hasUpdatePermission &&
					(getContent ||
						(objectEntry as IAssetObjectEntry).contentRawText) ? (
						<AIAssistantTriggerButton
							anchorId={AI_ASSISTANT_TOOLBAR_TRIGGER_ID}
							className="ai-assistant-chat__trigger--categorization ml-2"
							hideLabel
							instructionDefinitionScope="cms"
							label={Liferay.Language.get(
								'add-categories-with-ai'
							)}
							onOpen={handleGenerateCategories}
							presentation="dropdown"
						/>
					) : null}
				</div>

				{errorMessage && (
					<ClayForm.FeedbackGroup id={feedbackId} role="alert">
						<ErrorFeedback message={errorMessage} />
					</ClayForm.FeedbackGroup>
				)}

				{groupedTaxonomies.taxonomyVocabularies &&
					Object.entries(groupedTaxonomies?.taxonomyVocabularies).map(
						(
							[currentVocabularyId, vocabularyCategories],
							index
						) => {
							if (
								!isVisibleVocabulary(
									Number(currentVocabularyId)
								)
							) {
								return null;
							}

							const vocabularyName =
								vocabularyCategories[0].parentTaxonomyVocabulary
									.name;

							return vocabularyCategories.length ? (
								<div className="pt-3" key={index}>
									{!vocabularyId && (
										<p className="font-weight-semi-bold vocabulary-name">
											{vocabularyName}
										</p>
									)}

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
												inverse
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
