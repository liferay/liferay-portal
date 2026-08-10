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
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import HelpTooltipIcon from '../../../common/components/forms/HelpTooltipIcon';
import {IPermissionItem} from '../../../common/components/forms/PermissionsTable';
import {IVocabulary} from '../../../common/types/IVocabulary';
import CategorizationProjects from '../components/CategorizationProjects';
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
	cmpEnabled,
	defaultLanguageId,
	externalReferenceCodeInputError,
	externalReferenceCodeMaxLength,
	isNew,
	locales,
	nameInputError,
	onChangeVocabulary,
	projects,
	setExternalReferenceCodeInputError,
	setNameInputError,
	setProjectChange,
	setProjectInputError,
	setSpaceChange,
	setSpaceInputError,
	setVocabularyPermissions,
	showPermissions,
	spritemap,
	vocabulary,
}: {
	assetLibraries: AssetLibraryType[];
	cmpEnabled?: boolean;
	defaultLanguageId: string;
	externalReferenceCodeInputError: string;
	externalReferenceCodeMaxLength: number;
	isNew: boolean;
	locales: any[];
	nameInputError: string;
	onChangeVocabulary: Function;
	projects: AssetLibraryType[];
	setExternalReferenceCodeInputError: (value: string) => void;
	setNameInputError: Function;
	setProjectChange: (value: boolean) => void;
	setProjectInputError: (value: string) => void;
	setSpaceChange: (value: boolean) => void;
	setSpaceInputError: (value: string) => void;
	setVocabularyPermissions: Function;
	showPermissions: boolean;
	spritemap: string;
	vocabulary: IVocabulary;
}) {
	const [languageId, setLanguageId] = useState<string>(defaultLanguageId);

	const getLanguageLabel = (languageId: string) => {
		return languageId.replace('_', '-');
	};

	const handleNameBlur = () => {
		const name = vocabulary.name_i18n[getLanguageLabel(languageId)];

		if (!name.trim()) {
			setNameInputError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('name')
				)
			);
		}
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

	const onChangeSelectedProjects = (newSelectedProjects: string[]) => {
		onChangeVocabulary(() => ({
			...vocabulary,
			projects: newSelectedProjects.length
				? newSelectedProjects.map((projectScopeKey) => ({
						scopeKey: projectScopeKey,
					}))
				: [],
		}));
	};

	const onChangeSelectedSpaces = (newSelectedSpaces: string[]) => {
		onChangeVocabulary(() => ({
			...vocabulary,
			assetLibraries: newSelectedSpaces.length
				? newSelectedSpaces.map((string) => ({
						scopeKey: string,
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
							disabled={vocabulary.system}
							onBlur={handleNameBlur}
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

					<div
						className={
							externalReferenceCodeInputError ? 'has-error' : ''
						}
					>
						<label>
							{Liferay.Language.get('external-reference-code')}
						</label>

						<ClayInput
							aria-label={Liferay.Language.get(
								'external-reference-code'
							)}
							disabled={vocabulary.system}
							onChange={({target: {value}}) => {
								if (
									value.length >
									externalReferenceCodeMaxLength
								) {
									setExternalReferenceCodeInputError(
										sub(
											Liferay.Language.get(
												'external-reference-code-cannot-exceed-x-characters'
											),
											String(
												externalReferenceCodeMaxLength
											)
										)
									);
								}
								else if (externalReferenceCodeInputError) {
									setExternalReferenceCodeInputError('');
								}

								onChangeVocabulary(
									(prevVocabulary: IVocabulary) => ({
										...prevVocabulary,
										externalReferenceCode: value,
									})
								);
							}}
							type="text"
							value={vocabulary.externalReferenceCode || ''}
						/>

						{externalReferenceCodeInputError && (
							<ClayAlert displayType="danger" variant="feedback">
								{externalReferenceCodeInputError}
							</ClayAlert>
						)}
					</div>

					<div>
						<label>{Liferay.Language.get('description')}</label>

						<ClayInput
							aria-label={Liferay.Language.get('description')}
							component="textarea"
							disabled={vocabulary.system}
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

						<HelpTooltipIcon
							className="ml-2"
							message={Liferay.Language.get('multi-valued-help')}
						/>
					</label>

					<div>
						<label>
							{Liferay.Language.get('visibility')}

							<HelpTooltipIcon
								className="ml-2"
								message={Liferay.Language.get(
									'visibility-help'
								)}
							/>
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
				aria-labelledby="categorization-scope-title"
				className="mb-4"
				collapsable={false}
				displayType="secondary"
				role="group"
			>
				<ClayForm.Group className="c-gap-4 d-flex flex-column p-4">
					<h2
						className="mb-0 py-2 text-6 text-dark"
						id="categorization-scope-title"
					>
						{Liferay.Language.get('scope')}
					</h2>

					<CategorizationSpaces
						assetLibraries={assetLibraries}
						checkboxText="vocabulary"
						disabled={vocabulary.system}
						setSelectedSpaces={onChangeSelectedSpaces}
						setSpaceChange={setSpaceChange}
						setSpaceInputError={setSpaceInputError}
					/>

					{cmpEnabled && (
						<CategorizationProjects
							checkboxText="vocabulary"
							disabled={vocabulary.system}
							projects={projects}
							setProjectChange={setProjectChange}
							setProjectInputError={setProjectInputError}
							setSelectedProjects={onChangeSelectedProjects}
						/>
					)}
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
