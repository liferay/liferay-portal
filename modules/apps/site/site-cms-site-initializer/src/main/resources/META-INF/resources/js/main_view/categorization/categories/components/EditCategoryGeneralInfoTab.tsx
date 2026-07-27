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
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import FieldWrapper from '../../../../common/components/forms/FieldWrapper';
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

	const id = useId();

	const getLanguageLabel = (languageId: string) => {
		return languageId.replace('_', '-');
	};

	const handleNameBlur = () => {
		const name = category.name_i18n[getLanguageLabel(languageId)];

		if (!name.trim()) {
			setNameInputError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('name')
				)
			);
		}
	};

	const onChangeLocalizedField = (
		field: 'description' | 'friendlyUrlPath' | 'name',
		value: string
	) => {
		setCategory(() => ({
			...category,
			...(languageId === defaultLanguageId && {[field]: value}),
			[`${field}_i18n`]: {
				...category[`${field}_i18n`],
				[getLanguageLabel(languageId)]: value,
			},
		}));
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

		onChangeLocalizedField('name', newName);
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
					<ClayLayout.Row className="mx-0" justify="between">
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
							disabled={category.system}
							id="name"
							onBlur={handleNameBlur}
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
							disabled={category.system}
							id="description"
							onChange={({target: {value}}) =>
								onChangeLocalizedField('description', value)
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

					<FieldWrapper
						className="mb-0"
						disabled={category.system}
						fieldId={`${id}slug`}
						helpIcon={sub(
							Liferay.Language.get(
								"the-unique-path-for-this-x.-it-is-appended-to-the-channel's-friendly-url"
							),
							Liferay.Language.get('category')
						)}
						label={Liferay.Language.get('slug')}
					>
						<ClayInput
							disabled={category.system}
							id={`${id}slug`}
							onChange={({target: {value}}) =>
								onChangeLocalizedField('friendlyUrlPath', value)
							}
							type="text"
							value={
								category.friendlyUrlPath_i18n?.[
									getLanguageLabel(languageId)
								] ?? ''
							}
						/>
					</FieldWrapper>
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
