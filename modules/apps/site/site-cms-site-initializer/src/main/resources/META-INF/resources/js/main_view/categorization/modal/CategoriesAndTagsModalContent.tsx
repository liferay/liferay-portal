/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useCallback, useState} from 'react';

import {IBulkActionTaskStarterDTO} from '../../../common/types/BulkActionTask';
import {displayErrorToast} from '../../../common/utils/toastUtil';
import AssetCategories from '../../info_panel/components/AssetCategories';
import AssetTags from '../../info_panel/components/AssetTags';
import {EntryCategorizationDTO} from '../../info_panel/services/ObjectEntryService';
import {triggerAssetBulkAction} from '../../props_transformer/actions/triggerAssetBulkAction';

export default function CategoriesAndTagsModalContent({
	apiURL,
	closeModal,
	cmsGroupId,
	selectedData,
}: {
	apiURL?: string;
	closeModal: () => void;
	cmsGroupId: number;
	selectedData: any;
}) {
	const [categorizationDTO, setCategorizationDTO] =
		useState<EntryCategorizationDTO>({
			keywords: [],
			taxonomyCategoryBriefs: [],
			taxonomyCategoryIds: [],
		});
	const [submitDisabled, setSubmitDisabled] = useState<boolean>(false);

	const doBulkSubmit = useCallback(async () => {
		setSubmitDisabled(true);

		const tasks: Promise<any>[] = [];

		const tasksTemplate: Partial<IBulkActionTaskStarterDTO<any>> = {
			apiURL,
			overrideDefaultErrorToast: true,
			overrideDefaultSuccessToast: true,
			selectedData,
		};

		if (categorizationDTO?.taxonomyCategoryIds?.length) {
			tasks.push(
				new Promise((resolve, reject) => {
					triggerAssetBulkAction({
						...tasksTemplate,
						keyValues: {
							taxonomyCategoryIds:
								categorizationDTO.taxonomyCategoryIds,
						},
						onCreateError: ({error}) => reject(error),
						onCreateSuccess: (response) =>
							response.error
								? reject(response.error)
								: resolve(response),
						type: 'TaxonomyCategoryBulkAction',
					} as IBulkActionTaskStarterDTO<'TaxonomyCategoryBulkAction'>);
				})
			);
		}

		if (categorizationDTO?.keywords?.length) {
			tasks.push(
				new Promise((resolve, reject) => {
					triggerAssetBulkAction({
						...tasksTemplate,
						keyValues: {keywords: categorizationDTO.keywords},
						onCreateError: ({error}) => reject(error),
						onCreateSuccess: (response) =>
							response.error
								? reject(response.error)
								: resolve(response),
						type: 'KeywordBulkAction',
					} as IBulkActionTaskStarterDTO<'KeywordBulkAction'>);
				})
			);
		}

		try {
			await Promise.all(tasks);

			closeModal();
		}
		catch (error) {
			setSubmitDisabled(false);

			displayErrorToast(error as string);
		}
	}, [
		apiURL,
		categorizationDTO,
		closeModal,
		selectedData,
		setSubmitDisabled,
	]);

	const updateLocalObjectEntry = useCallback(
		({
			keywords,
			lastAddedBrief,
			taxonomyCategoryIds,
		}: EntryCategorizationDTO): void => {
			setCategorizationDTO(
				({
					keywords: currentKeywords,
					taxonomyCategoryBriefs = [],
					taxonomyCategoryIds: currentTaxonomyCategoryIds,
				}) => ({
					keywords: keywords || currentKeywords!,
					taxonomyCategoryBriefs: [
						...taxonomyCategoryBriefs,
						...(lastAddedBrief
							? [
									{
										embeddedTaxonomyCategory:
											lastAddedBrief,
									},
								]
							: []),
					],
					taxonomyCategoryIds:
						taxonomyCategoryIds || currentTaxonomyCategoryIds,
				})
			);
		},
		[setCategorizationDTO]
	);

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('add-categories-and-tags')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayAlert displayType="info" hideCloseIcon={true}>
					{Liferay.Language.get(
						'adding-categories-and-tags-will-preserve-the-ones-already-applied-to-the-currently-selected-assets'
					)}
				</ClayAlert>

				<AssetCategories
					cmsGroupId={cmsGroupId}
					objectEntry={categorizationDTO}
					updateObjectEntry={updateLocalObjectEntry}
				/>

				<AssetTags
					cmsGroupId={cmsGroupId}
					objectEntry={categorizationDTO}
					updateObjectEntry={updateLocalObjectEntry}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={
								!(
									categorizationDTO.keywords?.length ||
									categorizationDTO.taxonomyCategoryIds
										?.length
								) || submitDisabled
							}
							displayType="primary"
							onClick={doBulkSubmit}
							type="button"
						>
							{selectedData.selectAll
								? Liferay.Language.get('add-to-all-assets')
								: selectedData?.items.length === 1
									? Liferay.Language.get('add-to-1-asset')
									: sub(
											Liferay.Language.get(
												'add-to-x-assets'
											),
											selectedData?.items.length
										)}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
