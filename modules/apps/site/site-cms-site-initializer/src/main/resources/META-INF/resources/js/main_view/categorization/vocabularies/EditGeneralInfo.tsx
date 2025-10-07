/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {LanguagePicker, Provider} from '@clayui/core';
import ClayForm, {
	ClayInput,
	ClaySelectWithOption,
	ClayToggle,
} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayPanel from '@clayui/panel';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {IPermissionItem} from '../../../common/components/forms/PermissionsTable';
import {IVocabulary} from '../../../common/types/IVocabulary';
import CategorizationSpaces from '../components/CategorizationSpaces';
import PermissionsFormGroup from '../components/PermissionsFormGroup';

const VISIBILITY_OPTIONS = [
	{
		label: Liferay.Language.get('public'),
		value: 'PUBLIC',
	},
	{
		label: Liferay.Language.get('private'),
		value: 'INTERNAL',
	},
];

export default function EditGeneralInfo({
	assetLibraries,
	defaultLanguageId,
	isNew,
	locales,
	nameInputError,
	onChangeVocabulary,
	setNameInputError,
	setSpaceChange,
	setSpaceInputError,
	setVocabularyPermissions,
	showPermissions,
	spaceInputError,
	spritemap,
	vocabulary,
}: {
	assetLibraries: AssetLibraryType[];
	defaultLanguageId: string;
	isNew: boolean;
	locales: any[];
	nameInputError: string;
	onChangeVocabulary: Function;
	setNameInputError: Function;
	setSpaceChange: (value: boolean) => void;
	setSpaceInputError: (value: string) => void;
	setVocabularyPermissions: Function;
	showPermissions: boolean;
	spaceInputError: string;
	spritemap: string;
	vocabulary: IVocabulary;
}) {
	const [languageId, setLanguageId] = useState<string>(defaultLanguageId);

	const getLanguageLabel = (languageId: string) => {
		return languageId.replace('_', '-');
	};

	const onChangeDescription = (newDescription: string) => {
		onChangeVocabulary(() => ({
			...vocabulary,
			...(languageId === defaultLanguageId && {
				description: newDescription,
			}),
			description_i18n: {
				...vocabulary.description_i18n,
				[getLanguageLabel(languageId)]: newDescription,
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

		onChangeVocabulary(() => ({
			...vocabulary,
			...(languageId === defaultLanguageId && {
				name: newName,
			}),
			name_i18n: {
				...vocabulary.name_i18n,
				[getLanguageLabel(languageId)]: newName,
			},
		}));
	};

	const onChangeSelectedSpaces = (newSelectedSpaces: number[]) => {
		onChangeVocabulary(() => ({
			...vocabulary,
			assetLibraries: newSelectedSpaces.length
				? newSelectedSpaces.map((number) => ({
						id: number,
					}))
				: [],
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
							onChange={({target: {value}}) =>
								onChangeName(value)
							}
							required
							type="text"
							value={
								vocabulary.name_i18n[
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
							onChange={({target: {value}}) =>
								onChangeDescription(value)
							}
							type="text"
							value={
								vocabulary.description_i18n
									? vocabulary.description_i18n[
											getLanguageLabel(languageId)
										] || ''
									: ''
							}
						/>
					</div>

					<label className="toggle-switch">
						<ClayToggle
							aria-label="Multi Value"
							onToggle={(checked) => {
								onChangeVocabulary(() => ({
									...vocabulary,
									multiValued: checked,
								}));
							}}
							toggled={vocabulary.multiValued}
						/>

						{Liferay.Language.get('allow-multiple-categories')}

						<ClayTooltipProvider>
							<span
								className="help-text-icon ml-2"
								title={Liferay.Language.get(
									'multi-valued-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<div>
						<label>
							{Liferay.Language.get('visibility')}

							<ClayTooltipProvider>
								<span
									className="help-text-icon ml-2"
									title={Liferay.Language.get(
										'visibility-help'
									)}
								>
									<ClayIcon symbol="question-circle-full" />
								</span>
							</ClayTooltipProvider>
						</label>

						<ClaySelectWithOption
							aria-label={Liferay.Language.get('visibility')}
							className={isNew ? undefined : 'bg-white'}
							disabled={!isNew}
							onChange={(event) =>
								onChangeVocabulary(() => ({
									...vocabulary,
									visibilityType: event.target.value,
								}))
							}
							options={VISIBILITY_OPTIONS}
							value={vocabulary.visibilityType}
						/>
					</div>
				</ClayForm.Group>
			</ClayPanel>

			<ClayPanel
				aria-label="space"
				className="mb-4"
				collapsable={false}
				displayType="secondary"
				role="group"
			>
				<ClayForm.Group className="c-gap-4 d-flex flex-column p-4">
					<h2 className="mb-0 py-2 text-6 text-dark">
						{Liferay.Language.get('space')}
					</h2>

					<CategorizationSpaces
						assetLibraries={assetLibraries}
						checkboxText="vocabulary"
						selectedSpaces={
							vocabulary.assetLibraries
								? vocabulary.assetLibraries.map(
										(item: AssetLibraryType) => item.id
									)
								: []
						}
						setSelectedSpaces={onChangeSelectedSpaces}
						setSpaceChange={setSpaceChange}
						setSpaceInputError={setSpaceInputError}
						spaceInputError={spaceInputError}
					/>
				</ClayForm.Group>
			</ClayPanel>

			{showPermissions && (
				<PermissionsFormGroup
					onChange={(newPermissions: IPermissionItem[]) => {
						setVocabularyPermissions(newPermissions);
					}}
				/>
			)}
		</div>
	);
}
