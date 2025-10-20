/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {openModal} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import Toolbar from '../../../common/components/Toolbar';
import VerticalNavLayout from '../../../common/components/VerticalNavLayout';
import {IPermissionItem} from '../../../common/components/forms/PermissionsTable';
import CategorizationPermissionService from '../../../common/services/CategorizationPermissionService';
import CategoryService from '../../../common/services/CategoryService';
import {
	displayCreateSuccessToast,
	displayEditSuccessToast,
	displayNameInUseErrorToast,
	displaySystemErrorToast,
} from '../../../common/utils/toastUtil';
import {DEFAULT_PERMISSIONS} from '../utils/CategorizationPermissionsUtil';
import EditCategoryGeneralInfoTab from './components/EditCategoryGeneralInfoTab';
import EditCategoryPropertiesTab from './components/EditCategoryPropertiesTab';

interface Props {
	backURL: string | URL;
	categoryByCategoryIdAPIURL: string;
	categoryByParentCategoryIdAPIURL: string;
	categoryByVocabularyIdAPIURL: string;
	categoryId: number;
	categoryPermissionsAPIURL: string;
	defaultLanguageId: string;
	isCreateNew: boolean;
	locales: any[];
	parentCategoryId: number;
	spritemap: string;
	vocabularyId: number;
}

const EditCategoryPage = ({
	backURL,
	categoryByCategoryIdAPIURL,
	categoryByParentCategoryIdAPIURL,
	categoryByVocabularyIdAPIURL,
	categoryPermissionsAPIURL,
	defaultLanguageId,
	isCreateNew,
	locales,
	parentCategoryId,
	spritemap,
}: Props) => {
	const [category, setCategory] = useState<TaxonomyCategory>({
		name: '',
		name_i18n: {
			[defaultLanguageId.replace('_', '-')]: '',
		},
	});
	const [categoryPermissions, setCategoryPermissions] =
		useState<IPermissionItem[]>(DEFAULT_PERMISSIONS);
	const [nameInputError, setNameInputError] = useState<string>('');
	const [title, setTitle] = useState<string>('');

	useEffect(() => {
		const fetchInitialData = async () => {
			if (isCreateNew) {
				return;
			}
			else {
				const {data, error} = await CategoryService.getCategory(
					categoryByCategoryIdAPIURL
				);

				if (data) {
					setTitle(data.name);
					setCategory(data);
				}
				else if (error) {
					console.error(error);
					navigate(backURL);
				}
			}
		};

		void fetchInitialData();

		return () => {
			resetForm();
		};

		// eslint-disable-next-line react-compiler/react-compiler
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	function resetForm() {
		setCategory({
			name: '',
			name_i18n: {
				[defaultLanguageId]: '',
			},
		});
		setNameInputError('');
		setTitle('');
	}

	function validateForm() {
		if (category.name.trim() === '') {
			setNameInputError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('name')
				)
			);
		}
		else {
			setNameInputError('');
		}
	}

	function getFormattedCategoryProperties(category: TaxonomyCategory) {
		return category.taxonomyCategoryProperties?.filter((row) => {
			return row.key.trim() !== '' && row.value.trim() !== '';
		});
	}

	async function handleSave() {
		validateForm();

		if (nameInputError !== '') {
			return;
		}

		if (isCreateNew) {
			const {data, error, status} = Number(parentCategoryId)
				? await CategoryService.createCategory(
						categoryByParentCategoryIdAPIURL,
						{
							...category,
							taxonomyCategoryProperties:
								getFormattedCategoryProperties(category),
						}
					)
				: await CategoryService.createCategory(
						categoryByVocabularyIdAPIURL,
						{
							...category,
							taxonomyCategoryProperties:
								getFormattedCategoryProperties(category),
						}
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

				throw new Error(
					`POST request failed to create a new Category under 'vocabularyId = ${category.taxonomyVocabularyId}' using the following provided data: ${JSON.stringify(category)}`
				);
			}
			else {
				const {error: putPermissionsError} =
					await CategorizationPermissionService.putPermissions(
						categoryPermissionsAPIURL.replace(
							'{taxonomyCategoryId}',
							String(data?.id)
						),
						categoryPermissions
					);

				if (putPermissionsError) {
					displaySystemErrorToast();

					throw new Error(
						`PUT request failed to update permissions at ${categoryPermissionsAPIURL} using the following provided data: ${JSON.stringify(categoryPermissions)}`
					);
				}
			}

			navigate(backURL);
			displayCreateSuccessToast(category.name);
		}
		else {
			openModal({
				bodyHTML: Liferay.Language.get('edit-category-confirmation'),
				buttons: [
					{
						autoFocus: true,
						displayType: 'secondary',
						label: Liferay.Language.get('cancel'),
						type: 'cancel',
					},
					{
						displayType: 'primary',
						label: Liferay.Language.get('save'),
						onClick: async ({processClose}) => {
							processClose();

							const {error} =
								await CategoryService.updateCategory(
									categoryByCategoryIdAPIURL,
									{
										...category,
										taxonomyCategoryProperties:
											getFormattedCategoryProperties(
												category
											),
									}
								);

							if (error) {
								console.error(error);

								displaySystemErrorToast();

								throw new Error(error);
							}
							else {
								navigate(backURL);
								displayEditSuccessToast(category.name);
							}
						},
					},
				],
				status: 'warning',
				title: sub(
					Liferay.Language.get('edit-x'),
					'"' + category.name + '"'
				),
			});
		}
	}

	async function handleSaveAndAddAnother() {
		validateForm();

		if (nameInputError !== '') {
			return;
		}

		const {data, error, status} = Number(parentCategoryId)
			? await CategoryService.createCategory(
					categoryByParentCategoryIdAPIURL,
					category
				)
			: await CategoryService.createCategory(
					categoryByVocabularyIdAPIURL,
					category
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

			throw new Error(
				`POST request failed to create a new Category under 'vocabularyId = ${category.taxonomyVocabularyId}' using the following provided data: ${JSON.stringify(category)}`
			);
		}
		else {
			const {error: putPermissionsError} =
				await CategorizationPermissionService.putPermissions(
					categoryPermissionsAPIURL.replace(
						'{taxonomyCategoryId}',
						String(data?.id)
					),
					categoryPermissions
				);

			if (putPermissionsError) {
				displaySystemErrorToast();

				throw new Error(
					`PUT request failed to update permissions at ${categoryPermissionsAPIURL} using the following provided data: ${JSON.stringify(categoryPermissions)}`
				);
			}
		}

		window.location.reload();

		displayCreateSuccessToast(category.name);
	}

	const verticalNavItems = [
		{
			component: (
				<EditCategoryGeneralInfoTab
					category={category}
					defaultLanguageId={defaultLanguageId}
					locales={locales}
					nameInputError={nameInputError}
					setCategory={setCategory}
					setCategoryPermissions={setCategoryPermissions}
					setNameInputError={setNameInputError}
					showPermissions={isCreateNew}
					spritemap={spritemap}
				/>
			),
			id: 'general',
			label: Liferay.Language.get('general'),
		},
		{
			component: <div>Images Tab Placeholder Content</div>,
			id: 'images',
			label: Liferay.Language.get('images'),
		},
		{
			component: (
				<EditCategoryPropertiesTab
					category={category}
					setCategory={setCategory}
					spritemap={spritemap}
				/>
			),
			id: 'properties',
			label: Liferay.Language.get('properties'),
		},
	];

	const getTitle = () => {
		if (isCreateNew) {
			if (Number(parentCategoryId) === 0) {
				return Liferay.Language.get('new-category');
			}
			else {
				return Liferay.Language.get('new-subcategory');
			}
		}
		else {
			return sub(Liferay.Language.get('edit-x'), title);
		}
	};

	return (
		<div className="categorization-section">
			<div className="edit-page">
				<Toolbar backURL={backURL.toString()} title={getTitle()}>
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

						{isCreateNew && (
							<ClayButton
								data-testid="save-and-add-another-button"
								onClick={handleSaveAndAddAnother}
								outline={true}
								size="sm"
							>
								{Liferay.Language.get('save-and-add-another')}
							</ClayButton>
						)}

						<ClayButton
							className="inline-item-after"
							data-testid="save-button"
							displayType="primary"
							onClick={handleSave}
							size="sm"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</Toolbar.Item>
				</Toolbar>

				<VerticalNavLayout items={verticalNavItems} />
			</div>
		</div>
	);
};
export default EditCategoryPage;
