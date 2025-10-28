/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {LegacyRef, useEffect, useState} from 'react';

import {TranslationManagerProps} from './Types';
import useTranslationProgress from './useTranslationProgress';

const META_FIELD_NAMES = {
	description: 'descriptionMapAsXML',
	friendlyURL: 'friendlyURL',
	title: 'titleMapAsXML',
};

const Trigger = React.forwardRef(
	({children, ...otherProps}: any, ref: LegacyRef<HTMLButtonElement>) => (
		<button
			{...otherProps}
			aria-label={Liferay.Language.get('select-a-filter')}
			className="btn btn-block btn-secondary btn-sm form-control-select"
			ref={ref}
			tabIndex={0}
		>
			{children}
		</button>
	)
);

export default function TranslationFilter({
	defaultLanguageId: initialDefaultLanguageId,
	fields: initialFields,
	locales,
	namespace,
	selectedLanguageId: initialSelectedLanguageId,
}: TranslationManagerProps) {
	const [active, setActive] = useState(false);
	const [selectedKey, setSelectedKey] = useState(() => 'all-fields');

	const {
		defaultLanguageId,
		selectedLanguageId,
		translationProgress,
		translations,
		updateTranslations,
	} = useTranslationProgress({
		defaultLanguageId: initialDefaultLanguageId,
		fields: initialFields,
		locales,
		namespace,
		selectedLanguageId: initialSelectedLanguageId,
	});

	const handleSelection = (option: React.Key) => {
		Liferay.fire('inputLocalized:translationFilterChange', {option});

		const contentWrapper = document.getElementById(namespace + 'content');
		const descriptionNode = document.getElementById(
			namespace + META_FIELD_NAMES.description + 'Wrapper'
		);
		const emptyPlaceholder = document.getElementById('emptyPlaceHolder');
		const friendlyURLNode = document.getElementById(
			namespace + META_FIELD_NAMES.friendlyURL + 'Wrapper'
		);
		const metadataWrapper = document.getElementById(namespace + 'metadata');
		const titleField = translations.find(
			(item) => item.fieldName === META_FIELD_NAMES.title
		);

		if (
			contentWrapper &&
			descriptionNode &&
			emptyPlaceholder &&
			friendlyURLNode &&
			metadataWrapper &&
			titleField
		) {
			switch (option) {
				case 'translated':
					descriptionNode.hidden = true;
					friendlyURLNode.hidden = true;

					if (!titleField?.languages.includes(selectedLanguageId)) {
						metadataWrapper.hidden = true;

						if (
							!translationProgress?.translatedItems[
								selectedLanguageId
							]
						) {
							contentWrapper.hidden = true;
							emptyPlaceholder.hidden = false;
						}
						else {
							contentWrapper.hidden = false;
							emptyPlaceholder.hidden = true;
						}
					}
					else {
						emptyPlaceholder.hidden = true;
						metadataWrapper.hidden = false;

						if (
							(translationProgress?.translatedItems[
								selectedLanguageId
							] ?? 0) > 1
						) {
							contentWrapper.hidden = false;
						}
						else {
							contentWrapper.hidden = true;
						}
					}
					break;
				case 'untranslated':
					descriptionNode.hidden = true;
					friendlyURLNode.hidden = true;

					if (titleField?.languages.includes(selectedLanguageId)) {
						metadataWrapper.hidden = true;

						if (
							(translationProgress?.translatedItems[
								selectedLanguageId
							] ?? 0) < (translationProgress?.totalItems ?? 0)
						) {
							contentWrapper.hidden = false;
							emptyPlaceholder.hidden = true;
						}
						else {
							contentWrapper.hidden = true;
							emptyPlaceholder.hidden = false;
						}
					}
					else {
						emptyPlaceholder.hidden = true;
						metadataWrapper.hidden = false;

						if (
							(translationProgress?.translatedItems[
								selectedLanguageId
							] ?? 0) <
							(translationProgress?.totalItems ?? 0) - 1
						) {
							contentWrapper.hidden = false;
						}
						else {
							contentWrapper.hidden = true;
						}
					}
					break;
				default:
					contentWrapper.hidden = false;
					descriptionNode.hidden = false;
					friendlyURLNode.hidden = false;
					metadataWrapper.hidden = false;
					emptyPlaceholder.hidden = true;
			}
		}

		setSelectedKey(option as string);
	};

	useEffect(() => {
		handleSelection('all-fields');

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedLanguageId]);

	return (
		<>
			{selectedLanguageId !== defaultLanguageId && (
				<Picker
					active={active}
					as={Trigger}
					id="picker"
					messages={{
						itemDescribedby: Liferay.Language.get(
							'you-are-currently-on-a-text-element,-inside-of-a-list-box'
						),
						itemSelected: Liferay.Language.get('x-selected'),
						scrollToBottomAriaLabel:
							Liferay.Language.get('scroll-to-bottom'),
						scrollToTopAriaLabel:
							Liferay.Language.get('scroll-to-top'),
					}}
					onActiveChange={(active: boolean) => {
						if (active) {
							updateTranslations();
						}

						setActive(active);
					}}
					onSelectionChange={handleSelection}
					selectedKey={selectedKey}
				>
					<Option key="all-fields">
						{Liferay.Language.get('all-fields')}
					</Option>

					<Option key="translated">
						{Liferay.Language.get('translated')}
					</Option>

					<Option key="untranslated">
						{Liferay.Language.get('untranslated')}
					</Option>
				</Picker>
			)}
		</>
	);
}
