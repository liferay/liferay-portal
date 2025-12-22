/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayModal from '@clayui/modal';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import TagService from '../../../common/services/TagService';
import {
	IBulkActionFDSData,
	IBulkActionTaskStarterDTO,
} from '../../../common/types/BulkActionTask';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../common/utils/constants';
import {displayErrorToast} from '../../../common/utils/toastUtil';
import AssetTags from '../../info_panel/components/AssetTags';
import {EntryCategorizationDTO} from '../../info_panel/services/ObjectEntryService';
import {triggerAssetBulkAction} from '../../props_transformer/actions/triggerAssetBulkAction';
import {
	getModalDescription,
	getScopeAttributes,
} from '../utils/BulkCategorizationModalUtil';

export default function EditAssetTagsModalContent({
	apiURL,
	closeModal,
	cmsGroupId,
	selectedData: selected,
}: {
	apiURL?: string;
	assetLibraries: any;
	closeModal: () => void;
	cmsGroupId: number;
	selectedData: IBulkActionFDSData;
}) {
	const selectedData = useMemo(
		() => ({
			...selected,
			items:
				selected?.items?.filter(
					({entryClassName}) =>
						entryClassName !== OBJECT_ENTRY_FOLDER_CLASS_NAME
				) || [],
		}),
		[selected]
	);

	const [categorizationDTO, setCategorizationDTO] =
		useState<EntryCategorizationDTO>({
			...getScopeAttributes(selectedData, false, true),
			keywords: [],
		} as unknown as EntryCategorizationDTO);
	const [commonRequested, setCommonRequested] = useState<boolean>(false);
	const [submitDisabled, setSubmitDisabled] = useState<boolean>(false);
	const [selectedOperation, setSelectedOperation] = useState<
		'add' | 'replace'
	>('add');

	const hasUpdatePermission = useMemo(
		() =>
			selectedData.items?.every(
				({actions}) => !!actions?.update?.href || false
			) || false,
		[selectedData]
	);

	const doBulkSubmit = useCallback(async () => {
		setSubmitDisabled(true);

		triggerAssetBulkAction({
			apiURL,
			keyValues: {
				append: selectedOperation === 'add',
				keywordsToAdd: categorizationDTO.keywordsToAdd,
				keywordsToRemove: categorizationDTO.keywordsToRemove,
			},
			onCreateError: ({error}) => {
				setSubmitDisabled(false);

				displayErrorToast(error as string);
			},
			onCreateSuccess: ({error = ''}) => {
				if (error) {
					setSubmitDisabled(false);

					displayErrorToast(error as string);

					return;
				}

				closeModal();
			},
			overrideDefaultErrorToast: true,
			selectedData,
			type: 'KeywordBulkAction',
		} as IBulkActionTaskStarterDTO<'KeywordBulkAction'>);
	}, [
		apiURL,
		categorizationDTO,
		closeModal,
		selectedData,
		selectedOperation,
		setSubmitDisabled,
	]);

	const getCommonEntries = useCallback(async () => {
		if (commonRequested) {
			return;
		}

		try {
			const {
				data: {items},
				error,
			} = await TagService.getCommonTags(selectedData);

			if (error) {
				throw new Error('Unable to set common categories.');
			}

			setCommonRequested(true);

			setCategorizationDTO((categorizationDTO) => ({
				...categorizationDTO,
				keywords: [...items.map(({name}: any) => name)],
			}));
		}
		catch (error) {
			displayErrorToast(error as any);

			console.error(error);
		}
	}, [commonRequested, selectedData]);

	useEffect(() => {
		getCommonEntries();
	}, [getCommonEntries]);

	const updateLocalObjectEntry = useCallback(
		({
			keywords,
			keywordsToAdd = [],
			keywordsToRemove = [],
		}: EntryCategorizationDTO): void => {
			setCategorizationDTO(
				({
					keywords: currentKeywords,
					keywordsToAdd: currentToAdd = [],
					keywordsToRemove: currentToRemove = [],
					...dto
				}) => {
					return {
						...dto,
						keywords: keywords || currentKeywords!,
						keywordsToAdd: [
							...new Set([...currentToAdd, ...keywordsToAdd]),
						],
						keywordsToRemove: [
							...new Set([
								...currentToRemove,
								...keywordsToRemove,
							]),
						],
					} as EntryCategorizationDTO;
				}
			);
		},
		[setCategorizationDTO]
	);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('edit-tags')}
			</ClayModal.Header>

			<ClayModal.Body>
				{selectedData.selectAll && (
					<ClayAlert title="">
						{Liferay.Language.get(
							'this-operation-will-not-be-applied-to-any-of-the-selected-folders'
						)}
					</ClayAlert>
				)}

				<p>{getModalDescription(selectedData)}</p>

				<ClayRadioGroup
					className="mb-4"
					name="add-replace"
					onChange={(value) =>
						setSelectedOperation(value as 'add' | 'replace')
					}
					value={selectedOperation}
				>
					<ClayRadio
						checked={true}
						label={Liferay.Language.get('edit')}
						value="add"
					>
						<div className="form-text">
							{Liferay.Language.get(
								'add-new-tags-or-remove-common-tags'
							)}
						</div>
					</ClayRadio>

					<ClayRadio
						label={Liferay.Language.get('replace')}
						value="replace"
					>
						<div className="form-text">
							{Liferay.Language.get(
								'these-tags-replace-all-existing-tags'
							)}
						</div>
					</ClayRadio>
				</ClayRadioGroup>

				<AssetTags
					cmsGroupId={cmsGroupId}
					collapsable={false}
					hasUpdatePermission={hasUpdatePermission}
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
									categorizationDTO.keywordsToAdd?.length ||
									categorizationDTO.keywordsToRemove?.length
								) || submitDisabled
							}
							displayType="primary"
							onClick={doBulkSubmit}
							type="button"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
