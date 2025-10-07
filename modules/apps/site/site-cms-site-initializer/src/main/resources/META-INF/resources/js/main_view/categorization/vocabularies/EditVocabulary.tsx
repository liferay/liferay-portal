/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {useModal} from '@clayui/modal';
import {ClayVerticalNav} from '@clayui/nav';
import {navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import Toolbar from '../../../common/components/Toolbar';
import {IPermissionItem} from '../../../common/components/forms/PermissionsTable';
import CategorizationPermissionService from '../../../common/services/CategorizationPermissionService';
import VocabularyService from '../../../common/services/VocabularyService';
import {IVocabulary} from '../../../common/types/IVocabulary';
import {
	displayNameInUseErrorToast,
	displaySystemErrorToast,
} from '../../../common/utils/toastUtil';
import {DEFAULT_PERMISSIONS} from '../utils/CategorizationPermissionsUtil';
import ConfirmChangesModal from './ConfirmChangesModal';
import EditAssociatedAssetTypes from './EditAssociatedAssetTypes';
import EditGeneralInfo from './EditGeneralInfo';

const NAVIGATION_TABS = {
	ASSET_TYPES: 'assetTypes',
	GENERAL: 'general',
};

export default function EditVocabulary({
	availableAssetTypes,
	backURL,
	cmsGroupId,
	defaultLanguageId,
	locales,
	spritemap,
	vocabularyId,
	vocabularyPermissionsAPIURL,
}: {
	availableAssetTypes: AssetType[];
	backURL: string;
	cmsGroupId: number;
	defaultLanguageId: string;
	locales: any[];
	spritemap: string;
	vocabularyId: number;
	vocabularyPermissionsAPIURL: string;
}) {
	const [activeVerticalNavKey, setActiveVerticalNavKey] = useState(
		NAVIGATION_TABS.GENERAL
	);
	const [assetLibraries, setAssetLibraries] = useState<AssetLibraryType[]>(
		[]
	);
	const [assetTypes, setAssetTypes] = useState<AssetType[]>([]);
	const [assetTypeChange, setAssetTypeChange] = useState(false);
	const [assetTypeInputError, setAssetTypeInputError] = useState<string>('');
	const [nameInputError, setNameInputError] = useState<string>('');
	const {observer, onOpenChange, open} = useModal();
	const [spaceChange, setSpaceChange] = useState(false);
	const [spaceInputError, setSpaceInputError] = useState('');
	const [title, setTitle] = useState<string>('');
	const [vocabulary, setVocabulary] = useState<IVocabulary>({
		assetLibraries: [
			{
				id: -1,
				name: 'All Spaces',
			},
		],
		assetTypes: [
			{
				required: false,
				type: 'AllAssetTypes',
				typeId: 0,
			},
		],
		description: '',
		description_i18n: {
			[defaultLanguageId]: '',
		},
		multiValued: true,
		name: '',
		name_i18n: {
			[defaultLanguageId.replace('_', '-')]: '',
		},
		visibilityType: 'PUBLIC',
	});
	const [vocabularyPermissions, setVocabularyPermissions] =
		useState<IPermissionItem[]>(DEFAULT_PERMISSIONS);

	const isNew = Number(vocabularyId) === 0;

	useEffect(() => {
		const fetchData = async () => {
			if (isNew) {
				return;
			}
			else {
				const {data, error} =
					await VocabularyService.fetchVocabulary(vocabularyId);

				if (data) {
					setAssetLibraries(data.assetLibraries);
					setAssetTypes(data.assetTypes);
					setTitle(data.name);
					setVocabulary(data);
				}
				else if (error) {
					console.error(error);
					navigate(backURL);
				}
			}
		};

		fetchData();
	}, [backURL, isNew, vocabularyId]);

	const _handleValidateInputs = () => {
		if (nameInputError || vocabulary.name === '') {
			setNameInputError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('name')
				)
			);

			setActiveVerticalNavKey('general');

			return false;
		}

		if (spaceInputError) {
			setActiveVerticalNavKey('general');

			return false;
		}

		if (assetTypeInputError) {
			setActiveVerticalNavKey('assetTypes');

			return false;
		}

		return true;
	};

	const _handleSave = async () => {
		if (!_handleValidateInputs()) {
			return;
		}

		if (isNew) {
			const {data, error, status} =
				await VocabularyService.createVocabulary(
					cmsGroupId,
					vocabulary
				);

			if (error) {
				if (status === 'CONFLICT') {
					setNameInputError(
						Liferay.Language.get(
							'please-enter-a-unique-name.-this-one-is-already-in-use'
						)
					);

					displayNameInUseErrorToast();
				}
				else {
					displaySystemErrorToast();
				}

				throw new Error(error);
			}
			else {
				const vocabularyId: number = data?.id || 0;

				const {error: putPermissionsError} =
					await CategorizationPermissionService.putPermissions(
						vocabularyPermissionsAPIURL.replace(
							'{taxonomyVocabularyId}',
							String(vocabularyId)
						),
						vocabularyPermissions
					);

				if (putPermissionsError) {
					displaySystemErrorToast();

					throw new Error(
						`PUT request failed to update permissions at ${vocabularyPermissionsAPIURL} using the following provided data: ${JSON.stringify(vocabularyPermissions)}`
					);
				}
			}
		}
		else {
			const {error} =
				await VocabularyService.updateVocabulary(vocabulary);

			if (error) {
				displaySystemErrorToast();

				throw new Error(error);
			}
		}

		await navigate(backURL);

		if (isNew) {
			Liferay.Util.openToast({
				message: Liferay.Util.sub(
					Liferay.Language.get('x-was-published-successfully'),
					vocabulary.name
				),
				type: 'success',
			});
		}
		else {
			Liferay.Util.openToast({
				message: Liferay.Util.sub(
					Liferay.Language.get('x-was-updated-successfully'),
					vocabulary.name
				),
				type: 'success',
			});
		}
	};

	const _handleVerticalNavChange = (verticalNav: string) => {
		setActiveVerticalNavKey(verticalNav);
	};

	return (
		<div className="categorization-section">
			<div className="edit-page">
				<Toolbar
					backURL={backURL}
					title={
						title
							? sub(Liferay.Language.get('edit-x'), title)
							: Liferay.Language.get('new-vocabulary')
					}
				>
					<Toolbar.Item>
						<ClayButton
							aria-label={Liferay.Language.get('back')}
							borderless
							displayType="secondary"
							onClick={() => navigate(backURL)}
							outline
							size="sm"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="inline-item-after"
							displayType="primary"
							onClick={() => {
								if (assetTypeChange || spaceChange) {
									onOpenChange(true);
								}
								else {
									_handleSave();
								}
							}}
							size="sm"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</Toolbar.Item>
				</Toolbar>

				<ClayLayout.ContainerFluid size={false}>
					<ClayLayout.Row className="min-vh-100">
						<ClayLayout.Col
							className="cms-sidebar-nav sidebar-layout"
							md="auto"
							sm={12}
						>
							<div className="px-md-2 py-3 py-md-4">
								<ClayVerticalNav
									items={[
										{
											active:
												activeVerticalNavKey ===
												NAVIGATION_TABS.GENERAL,
											label: Liferay.Language.get(
												'general'
											),
											onClick: () =>
												_handleVerticalNavChange(
													NAVIGATION_TABS.GENERAL
												),
										},
										{
											active:
												activeVerticalNavKey ===
												NAVIGATION_TABS.ASSET_TYPES,
											label: Liferay.Language.get(
												'associated-asset-types'
											),
											onClick: () =>
												_handleVerticalNavChange(
													NAVIGATION_TABS.ASSET_TYPES
												),
										},
									]}
								/>
							</div>
						</ClayLayout.Col>

						<ClayLayout.Col className="col-md" sm={12}>
							{activeVerticalNavKey === 'general' && (
								<EditGeneralInfo
									assetLibraries={assetLibraries}
									defaultLanguageId={defaultLanguageId}
									isNew={isNew}
									locales={locales}
									nameInputError={nameInputError}
									onChangeVocabulary={setVocabulary}
									setNameInputError={setNameInputError}
									setSpaceChange={setSpaceChange}
									setSpaceInputError={setSpaceInputError}
									setVocabularyPermissions={
										setVocabularyPermissions
									}
									showPermissions={isNew}
									spaceInputError={spaceInputError}
									spritemap={spritemap}
									vocabulary={vocabulary}
								/>
							)}

							{activeVerticalNavKey === 'assetTypes' && (
								<EditAssociatedAssetTypes
									assetTypeInputError={assetTypeInputError}
									availableAssetTypes={availableAssetTypes}
									initialAssetTypes={assetTypes}
									onChangeVocabulary={setVocabulary}
									setAssetTypeChange={setAssetTypeChange}
									setAssetTypeInputError={
										setAssetTypeInputError
									}
									vocabulary={vocabulary}
								/>
							)}
						</ClayLayout.Col>
					</ClayLayout.Row>
				</ClayLayout.ContainerFluid>

				<ConfirmChangesModal
					assetTypeChange={assetTypeChange}
					observer={observer}
					onOpenChange={onOpenChange}
					onSave={_handleSave}
					open={open}
					spaceChange={spaceChange}
				/>
			</div>
		</div>
	);
}
