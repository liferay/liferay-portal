/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {
	ClayCheckbox,
	ClayInput,
	ClayRadio,
	ClayRadioGroup,
	ClaySelect,
} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayList from '@clayui/list';
import {addParams} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

const EXPORT_DEFAULT = 0;

const EXPORT_ALL = -1;

const Experiences = ({
	experiences,
	onChangeExperience,
	selectedExperiencesIds,
}) => {
	if (experiences?.length > 1) {
		return (
			<>
				<label className="mb-2">
					{Liferay.Language.get('select-experiences')}
				</label>
				<ClayList className="translation-experiences-wrapper">
					{experiences.map(({label, segment, value}) => {
						const checked =
							selectedExperiencesIds.indexOf(value) !== -1;
						const inputId = `experience_${value}`;

						return (
							<ClayList.Item flex key={value}>
								<ClayList.ItemField>
									<ClayCheckbox
										checked={checked}
										id={inputId}
										onChange={() => {
											onChangeExperience(!checked, value);
										}}
										value={value}
									/>
								</ClayList.ItemField>

								<ClayList.ItemField expand>
									<ClayLayout.ContentRow
										className="list-group-label"
										containerElement="label"
										htmlFor={inputId}
									>
										<ClayLayout.ContentCol className="translation-experience-name">
											<div
												className="text-truncate"
												title={label}
											>
												{label}
											</div>
										</ClayLayout.ContentCol>

										<ClayLayout.ContentCol
											className="text-right"
											expand
										>
											<span className="small text-secondary text-truncate">
												{segment}
											</span>
										</ClayLayout.ContentCol>
									</ClayLayout.ContentRow>
								</ClayList.ItemField>
							</ClayList.Item>
						);
					})}
				</ClayList>
			</>
		);
	}

	return null;
};

const ExportFileFormats = ({
	availableExportFileFormats,
	exportMimeType,
	portletNamespace,
	setExportMimeType,
}) => {
	if (availableExportFileFormats.length === 1) {
		return (
			<ClayInput
				readOnly
				value={availableExportFileFormats[0].displayName}
			/>
		);
	}
	else {
		return (
			<ClaySelect
				id={`${portletNamespace}exportMimeType`}
				name={`${portletNamespace}exportMimeType`}
				onChange={(event) => {
					setExportMimeType(event.currentTarget.value);
				}}
				value={exportMimeType}
			>
				{availableExportFileFormats.map((exportFileFormat) => (
					<ClaySelect.Option
						key={exportFileFormat.mimeType}
						label={exportFileFormat.displayName}
						value={exportFileFormat.mimeType}
					/>
				))}
			</ClaySelect>
		);
	}
};

const MultiplePagesExperiences = ({
	multipleExperiences,
	onChangeExperience,
	portletNamespace,
	selectedExperienceValue,
}) => {
	if (multipleExperiences) {
		return (
			<div className="mb-5">
				<label className="mb-2">
					{Liferay.Language.get('export-experiences')}
				</label>

				<ClayRadioGroup
					name={`${portletNamespace}exportExperience`}
					onChange={onChangeExperience}
					value={selectedExperienceValue}
				>
					<ClayRadio
						label={Liferay.Language.get('default-experience')}
						value={EXPORT_DEFAULT}
					>
						<div className="form-text">
							{Liferay.Language.get(
								'export-default-experience-help-message'
							)}
						</div>
					</ClayRadio>

					<ClayRadio
						label={Liferay.Language.get('all-experiences')}
						value={EXPORT_ALL}
					>
						<div className="form-text">
							{Liferay.Language.get(
								'export-all-experiences-help-message'
							)}
						</div>
					</ClayRadio>
				</ClayRadioGroup>
			</div>
		);
	}

	return null;
};

const SourceLocales = ({
	availableSourceLocales,
	portletNamespace,
	setSourceLanguageId,
	sourceLanguageId,
}) => {
	if (availableSourceLocales.length === 1) {
		return (
			<ClayInput readOnly value={availableSourceLocales[0].displayName} />
		);
	}
	else {
		return (
			<ClaySelect
				id={`${portletNamespace}sourceLanguageId`}
				name={`${portletNamespace}sourceLanguageId`}
				onChange={(event) => {
					setSourceLanguageId(event.currentTarget.value);
				}}
				value={sourceLanguageId}
			>
				{availableSourceLocales.map((locale) => (
					<ClaySelect.Option
						key={locale.languageId}
						label={locale.displayName}
						value={locale.languageId}
					/>
				))}
			</ClaySelect>
		);
	}
};

const TargetLocale = ({
	locale,
	onChangeTargetLanguage,
	selectedTargetLanguageIds,
	sourceLanguageId,
}) => {
	const languageId = locale.languageId;
	const checked = selectedTargetLanguageIds.indexOf(languageId) !== -1;

	return (
		<ClayLayout.Col className="py-2" md={4}>
			<ClayCheckbox
				checked={checked}
				disabled={languageId === sourceLanguageId}
				label={locale.displayName}
				onChange={() => {
					onChangeTargetLanguage(!checked, languageId);
				}}
			/>
		</ClayLayout.Col>
	);
};

const ExportTranslation = ({
	availableExportFileFormats,
	availableSourceLocales,
	availableTargetLocales,
	defaultSourceLanguageId,
	experiences,
	exportTranslationURL: initialExportTranslationURL,
	multipleExperiences,
	multiplePagesSelected,
	portletNamespace,
	redirectURL,
}) => {
	const [exportMimeType, setExportMimeType] = useState(
		availableExportFileFormats[0].mimeType
	);

	const [sourceLanguageId, setSourceLanguageId] = useState(
		defaultSourceLanguageId
	);

	const [selectedTargetLanguageIds, setSelectedTargetLanguageIds] = useState(
		[]
	);

	const [selectedExperiencesIds, setSelectedExperiencesIds] = useState(() =>
		experiences?.length ? experiences.map(({value}) => value) : []
	);

	const [selectedExperienceValue, setSelectedExperienceValue] =
		useState(EXPORT_DEFAULT);

	const exportTranslationURL = addParams(
		'download=true',
		initialExportTranslationURL
	);

	const onChangeTargetLanguage = (checked, selectedLanguageId) => {
		setSelectedTargetLanguageIds((languageIds) =>
			checked
				? languageIds.concat(selectedLanguageId)
				: languageIds.filter(
						(languageId) => languageId !== selectedLanguageId
					)
		);
	};

	const onChangeExperience = (checked, selectedExperienceId) => {
		setSelectedExperiencesIds((experiencesIds) =>
			checked
				? experiencesIds.concat(selectedExperienceId)
				: experiencesIds.filter(
						(experienceId) => experienceId !== selectedExperienceId
					)
		);
	};

	const onChangeExperienceValue = (value) => {
		setSelectedExperienceValue(value);
	};

	return (
		<ClayForm
			onSubmit={(event) => {
				event.preventDefault();

				const params = {
					exportMimeType,
					sourceLanguageId,
					targetLanguageIds: selectedTargetLanguageIds.join(','),
				};

				if (multiplePagesSelected) {
					params.segmentsExperienceIds = selectedExperienceValue;
				}
				else if (selectedExperiencesIds.length) {
					params.segmentsExperienceIds =
						selectedExperiencesIds.join(',');
				}

				location.href = addParams(params, exportTranslationURL);
			}}
		>
			<ClayForm.Group className="w-50">
				<label
					htmlFor={
						availableExportFileFormats.length > 1
							? `${portletNamespace}exportMimeType`
							: undefined
					}
				>
					{Liferay.Language.get('export-file-format')}
				</label>

				<ExportFileFormats
					availableExportFileFormats={availableExportFileFormats}
					exportMimeType={exportMimeType}
					portletNamespace={portletNamespace}
					setExportMimeType={setExportMimeType}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor={`${portletNamespace}sourceLanguageId`}>
					{Liferay.Language.get('original-language')}
				</label>

				<SourceLocales
					availableSourceLocales={availableSourceLocales}
					portletNamespace={portletNamespace}
					setSourceLanguageId={setSourceLanguageId}
					sourceLanguageId={sourceLanguageId}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<label className="mb-2">
					{Liferay.Language.get('languages-to-translate-to')}
				</label>

				<ClayLayout.Row>
					{availableTargetLocales.map((locale) => (
						<TargetLocale
							key={locale.languageId}
							locale={locale}
							onChangeTargetLanguage={onChangeTargetLanguage}
							portletNamespace={portletNamespace}
							selectedTargetLanguageIds={
								selectedTargetLanguageIds
							}
							sourceLanguageId={sourceLanguageId}
						/>
					))}
				</ClayLayout.Row>
			</ClayForm.Group>

			{multiplePagesSelected ? (
				<MultiplePagesExperiences
					multipleExperiences={multipleExperiences}
					onChangeExperience={onChangeExperienceValue}
					portletNamespace={portletNamespace}
					selectedExperienceValue={selectedExperienceValue}
				/>
			) : (
				<Experiences
					experiences={experiences}
					onChangeExperience={onChangeExperience}
					selectedExperiencesIds={selectedExperiencesIds}
				/>
			)}

			<ClayButton.Group spaced>
				<ClayButton
					disabled={
						!selectedTargetLanguageIds.length ||
						(experiences?.length > 1 &&
							!selectedExperiencesIds.length)
					}
					displayType="primary"
					type="submit"
				>
					{Liferay.Language.get('export')}
				</ClayButton>

				{redirectURL && (
					<ClayLink button displayType="secondary" href={redirectURL}>
						{Liferay.Language.get('cancel')}
					</ClayLink>
				)}
			</ClayButton.Group>
		</ClayForm>
	);
};

ExportTranslation.propTypes = {
	availableExportFileFormats: PropTypes.arrayOf(
		PropTypes.shape({
			displayName: PropTypes.string,
			mimeType: PropTypes.string,
		})
	).isRequired,
	availableSourceLocales: PropTypes.arrayOf(
		PropTypes.shape({
			displayName: PropTypes.string,
			languageId: PropTypes.string,
		})
	).isRequired,
	availableTargetLocales: PropTypes.arrayOf(
		PropTypes.shape({
			displayName: PropTypes.string,
			languageId: PropTypes.string,
		})
	).isRequired,
	defaultSourceLanguageId: PropTypes.string.isRequired,
	experiences: PropTypes.arrayOf(
		PropTypes.shape({
			label: PropTypes.string.isRequired,
			segment: PropTypes.string.isRequired,
			value: PropTypes.string.isRequired,
		})
	),
	multipleExperiences: PropTypes.bool,
	multiplePagesSelected: PropTypes.bool,
};

export default ExportTranslation;
