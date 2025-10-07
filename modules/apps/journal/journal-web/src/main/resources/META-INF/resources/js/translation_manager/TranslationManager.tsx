/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {TranslationAdminSelector} from 'frontend-js-components-web';
import React, {useRef} from 'react';

import {TranslationManagerProps} from './Types';
import useTranslationProgress from './useTranslationProgress';

export default function TranslationManager({
	defaultLanguageId: initialDefaultLanguageId,
	fields: initialFields,
	locales,
	namespace,
	selectedLanguageId: initialSelectedLanguageId,
}: TranslationManagerProps) {
	const {
		defaultLanguageId,
		selectedLanguageId,
		translationProgress,
		updateTranslations,
	} = useTranslationProgress({
		defaultLanguageId: initialDefaultLanguageId,
		fields: initialFields,
		locales,
		namespace,
		selectedLanguageId: initialSelectedLanguageId,
	});
	const lastSelectedLanguageIdRef = useRef(selectedLanguageId);

	const handleSelectedLanguageIdChange = (
		languageId: Liferay.Language.Locale
	) => {
		if (languageId !== lastSelectedLanguageIdRef.current) {
			lastSelectedLanguageIdRef.current = languageId;

			Liferay.fire('inputLocalized:localeChanged', {
				item: document.querySelector(
					`[data-languageid="${languageId}"][data-value="${languageId}"]`
				),
			});

			const descriptionMessage = document.getElementById(
				`${namespace}descriptionNotTranslatableMessage`
			);
			const friendlyURLMessage = document.getElementById(
				`${namespace}friendlyURLNotTranslatableMessage`
			);

			if (descriptionMessage && friendlyURLMessage) {
				if (selectedLanguageId !== defaultLanguageId) {
					descriptionMessage.hidden = false;
					friendlyURLMessage.hidden = false;
				}
				else {
					descriptionMessage.hidden = true;
					friendlyURLMessage.hidden = true;
				}
			}
		}
	};

	return (
		<div className={classNames({'translation-manager': true})}>
			<TranslationAdminSelector
				activeLanguageIds={locales.map(({id}) => id)}
				availableLocales={locales}
				defaultLanguageId={defaultLanguageId}
				displayType="HORIZONTAL"
				onSelectedLanguageIdChange={handleSelectedLanguageIdChange}
				onSelectorActiveChange={updateTranslations}
				selectedLanguageId={selectedLanguageId}
				translationProgress={translationProgress}
			/>
		</div>
	);
}
