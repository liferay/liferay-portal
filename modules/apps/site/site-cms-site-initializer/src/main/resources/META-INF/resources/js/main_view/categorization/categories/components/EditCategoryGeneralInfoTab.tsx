/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {LanguagePicker, Provider} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayPanel from '@clayui/panel';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {IPermissionItem} from '../../../../common/components/forms/PermissionsTable';
import PermissionsFormGroup from '../../components/PermissionsFormGroup';

interface Props {
	category: TaxonomyCategory;
	defaultLanguageId: string;
	locales: any[];
	nameInputError: string;
	setCategory: Function;
	setCategoryPermissions: Function;
	setNameInputError: Function;
	showPermissions: boolean;
	spritemap: string;
}

const EditCategoryGeneralInfoTab = ({
	category,
	defaultLanguageId,
	locales,
	nameInputError,
	setCategory,
	setCategoryPermissions,
	setNameInputError,
	showPermissions,
	spritemap,
}: Props) => {
	const [languageId, setLanguageId] = useState<string>(defaultLanguageId);

	const getLanguageLabel = (languageId: string) => {
		return languageId.replace('_', '-');
	};

	const onChangeName = (newName: string) => {
		if (newName) {
			setNameInputError('');
		}
		else {
			setNameInputError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('name')
				)
			);
		}

		setCategory(() => ({
			...category,
			...(languageId === defaultLanguageId && {name: newName}),
			name_i18n: {
				...category.name_i18n,
				[getLanguageLabel(languageId)]: newName,
			},
		}));
	};

	const onChangeDescription = (newDescription: string) => {
		setCategory(() => ({
			...category,
			...(languageId === defaultLanguageId && {
				description: newDescription,
			}),
			description_i18n: {
				...category.description_i18n,
				[getLanguageLabel(languageId)]: newDescription,
			},
		}));
	};

	return (
		<div className="container-fluid container-fluid-max-md p-0 p-md-4">
			<ClayPanel
				aria-label="basic-info"
				className="mb-4"
				collapsable={false}
				displayType="secondary"
				role="group"
			>
				<ClayForm.Group className="c-gap-4 d-flex flex-column p-4">
					<ClayLayout.Row className="form-title" justify="between">
						<h2 className="mb-0 py-2 text-6 text-dark">
							{Liferay.Language.get('basic-info')}
						</h2>

						<div
							className="autofit-col"
							style={{width: 'fit-content'}}
						>
							<Provider spritemap={spritemap}>
								<LanguagePicker
									defaultLocaleId={defaultLanguageId}
									locales={locales}
									onSelectedLocaleChange={(
										localId: React.Key
									) => {
										setLanguageId(localId as string);
									}}
									selectedLocaleId={languageId}
									small
								/>
							</Provider>
						</div>
					</ClayLayout.Row>

					<div className={nameInputError ? 'has-error' : ''}>
						<label>
							{Liferay.Language.get('name')}

							<ClayIcon
								className="c-ml-1 reference-mark"
								focusable="false"
								role="presentation"
								symbol="asterisk"
							/>
						</label>

						<ClayInput
							aria-label={Liferay.Language.get('name')}
							data-testid="name-input"
							id="name"
							onChange={({target: {value}}) =>
								onChangeName(value)
							}
							required
							type="text"
							value={
								category.name_i18n[
									getLanguageLabel(languageId)
								] || ''
							}
						/>

						{nameInputError && (
							<ClayAlert displayType="danger" variant="feedback">
								{nameInputError}
							</ClayAlert>
						)}
					</div>

					<div>
						<label>{Liferay.Language.get('description')}</label>

						<ClayInput
							aria-label={Liferay.Language.get('description')}
							component="textarea"
							data-testid="description-input"
							id="description"
							onChange={({target: {value}}) =>
								onChangeDescription(value)
							}
							type="text"
							value={
								category.description_i18n
									? category.description_i18n[
											getLanguageLabel(languageId)
										] || ''
									: ''
							}
						/>
					</div>
				</ClayForm.Group>
			</ClayPanel>

			{showPermissions && (
				<PermissionsFormGroup
					onChange={(newPermissions: IPermissionItem[]) => {
						setCategoryPermissions(newPermissions);
					}}
				/>
			)}
		</div>
	);
};

export default EditCategoryGeneralInfoTab;
