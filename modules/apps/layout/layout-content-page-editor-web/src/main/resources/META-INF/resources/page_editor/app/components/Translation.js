/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import Layout from '@clayui/layout';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useMemo} from 'react';

import {updateLanguageId} from '../actions/index';
import {BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR} from '../config/constants/backgroundImageFragmentEntryProcessor';
import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../config/constants/editableFragmentEntryProcessor';
import {TRANSLATION_STATUS_TYPE} from '../config/constants/translationStatusType';

const getEditableValues = (fragmentEntryLinks) =>
	Object.values(fragmentEntryLinks)
		.filter(
			(fragmentEntryLink) =>
				!fragmentEntryLink.masterLayout &&
				!fragmentEntryLink.removed &&
				fragmentEntryLink.editableValues
		)
		.map((fragmentEntryLink) => [
			...Object.values(
				fragmentEntryLink.editableValues[
					EDITABLE_FRAGMENT_ENTRY_PROCESSOR
				] || {}
			),
			...Object.values(
				fragmentEntryLink.editableValues[
					BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR
				] || {}
			),
		])
		.reduce(
			(editableValuesA, editableValuesB) => [
				...editableValuesA,
				...editableValuesB,
			],
			[]
		);

const isTranslated = (editableValue, languageId) => editableValue[languageId];

const getTranslationStatus = ({
	editableValuesLength,
	isDefault,
	translatedValuesLength,
}) => {
	if (isDefault) {
		return TRANSLATION_STATUS_TYPE.default;
	}
	else if (translatedValuesLength === 0) {
		return TRANSLATION_STATUS_TYPE.untranslated;
	}
	else if (translatedValuesLength < editableValuesLength) {
		return TRANSLATION_STATUS_TYPE.translating;
	}
	else if (translatedValuesLength === editableValuesLength) {
		return TRANSLATION_STATUS_TYPE.translated;
	}
};

const TRANSLATION_STATUS_LANGUAGE = {
	[TRANSLATION_STATUS_TYPE.default]: Liferay.Language.get('default'),
	[TRANSLATION_STATUS_TYPE.translated]: Liferay.Language.get('translated'),
	[TRANSLATION_STATUS_TYPE.translating]: Liferay.Language.get('translating'),
	[TRANSLATION_STATUS_TYPE.untranslated]:
		Liferay.Language.get('not-translated'),
};

const TRANSLATION_STATUS_DISPLAY_TYPE = {
	[TRANSLATION_STATUS_TYPE.default]: 'info',
	[TRANSLATION_STATUS_TYPE.translated]: 'success',
	[TRANSLATION_STATUS_TYPE.translating]: 'warning',
	[TRANSLATION_STATUS_TYPE.untranslated]: 'warning',
};

const TranslationItem = ({
	editableValuesLength,
	isDefault,
	languageIcon,
	languageId,
	languageLabel,
	selectedLanguageId,
	translatedValuesLength,
}) => {
	const status = getTranslationStatus({
		editableValuesLength,
		isDefault,
		translatedValuesLength,
	});

	const statusText =
		TRANSLATION_STATUS_TYPE[status] === TRANSLATION_STATUS_TYPE.translating
			? `${TRANSLATION_STATUS_LANGUAGE[status]} ${sub(
					Liferay.Language.get('x-of-x'),
					translatedValuesLength,
					editableValuesLength
				)}`
			: `${TRANSLATION_STATUS_LANGUAGE[status]}`;

	return (
		<Layout.ContentRow>
			<span className="sr-only">
				{`${sub(
					Liferay.Language.get('x-language-x'),
					languageLabel,
					statusText
				)}`}
			</span>

			<Layout.ContentCol expand>
				<Layout.ContentRow className="align-items-center d-flex">
					<ClayIcon
						aria-hidden="true"
						className="c-mt-0"
						symbol={languageIcon}
					/>

					<span
						aria-hidden="true"
						className={classNames('c-ml-2', {
							'font-weight-bold':
								selectedLanguageId === languageId,
						})}
					>
						{languageLabel}
					</span>
				</Layout.ContentRow>
			</Layout.ContentCol>

			<Layout.ContentCol>
				<ClayLabel
					aria-hidden="true"
					displayType={TRANSLATION_STATUS_DISPLAY_TYPE[status]}
				>
					{TRANSLATION_STATUS_LANGUAGE[status]}

					{TRANSLATION_STATUS_TYPE[status] ===
						TRANSLATION_STATUS_TYPE.translating &&
						` ${translatedValuesLength}/${editableValuesLength}`}
				</ClayLabel>
			</Layout.ContentCol>
		</Layout.ContentRow>
	);
};

const Trigger = React.forwardRef(
	({languageIcon, w3cLanguageId, ...otherProps}, ref) => (
		<ClayButton
			{...otherProps}
			aria-label={sub(
				Liferay.Language.get('select-a-language.-current-language-x'),
				w3cLanguageId
			)}
			className=""
			data-title={sub(
				Liferay.Language.get('select-x'),
				Liferay.Language.get('language')
			)}
			displayType="secondary"
			monospaced
			ref={ref}
			size="sm"
		>
			<ClayIcon symbol={languageIcon} />
		</ClayButton>
	)
);
export default function Translation({
	availableLanguages,
	defaultLanguageId,
	dispatch,
	fragmentEntryLinks,
	languageId,
	showNotTranslated = true,
}) {
	const editableValues = useMemo(
		() => getEditableValues(fragmentEntryLinks),
		[fragmentEntryLinks]
	);
	const languageValues = useMemo(() => {
		const availableLanguagesMut = {...availableLanguages};

		const defaultLanguage = availableLanguages[defaultLanguageId];

		delete availableLanguagesMut[defaultLanguageId];

		return Object.keys({
			[defaultLanguageId]: defaultLanguage,
			...availableLanguagesMut,
		})
			.filter(
				(languageId) =>
					showNotTranslated ||
					!!editableValues.filter(
						(editableValue) =>
							isTranslated(editableValue, languageId) ||
							languageId === defaultLanguageId
					).length
			)
			.map((languageId) => ({
				languageId,
				values: editableValues.filter((editableValue) =>
					isTranslated(editableValue, languageId)
				),
			}));
	}, [
		availableLanguages,
		defaultLanguageId,
		editableValues,
		showNotTranslated,
	]);

	const {languageIcon, w3cLanguageId} = availableLanguages[languageId];

	return (
		<Picker
			UNSAFE_menuClassName="cadmin translation-picker"
			as={Trigger}
			items={languageValues}
			languageIcon={languageIcon}
			messages={{
				itemDescribedby: Liferay.Language.get(
					'you-are-currently-on-a-text-element,-inside-of-a-list-box'
				),
				itemSelected: Liferay.Language.get('x-selected'),
				scrollToBottomAriaLabel:
					Liferay.Language.get('scroll-to-bottom'),
				scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
			}}
			onSelectionChange={(key) => {
				dispatch(
					updateLanguageId({
						languageId: key,
					})
				);
			}}
			selectedKey={languageId}
			w3cLanguageId={w3cLanguageId}
		>
			{(language) => (
				<Option
					id={language.languageId}
					key={language.languageId}
					textValue={
						availableLanguages[language.languageId].w3cLanguageId
					}
				>
					<TranslationItem
						editableValuesLength={editableValues.length}
						isDefault={language.languageId === defaultLanguageId}
						languageIcon={
							availableLanguages[language.languageId].languageIcon
						}
						languageId={language.languageId}
						languageLabel={
							availableLanguages[language.languageId]
								.w3cLanguageId
						}
						selectedLanguageId={languageId}
						translatedValuesLength={language.values.length}
					/>
				</Option>
			)}
		</Picker>
	);
}

Translation.propTypes = {
	availableLanguages: PropTypes.objectOf(
		PropTypes.shape({
			languageIcon: PropTypes.string.isRequired,
			w3cLanguageId: PropTypes.string.isRequired,
		})
	).isRequired,
	defaultLanguageId: PropTypes.string.isRequired,
	dispatch: PropTypes.func.isRequired,
	fragmentEntryLinks: PropTypes.object.isRequired,
	languageId: PropTypes.string.isRequired,
};
