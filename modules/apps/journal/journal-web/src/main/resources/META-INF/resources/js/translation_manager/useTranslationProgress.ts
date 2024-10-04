/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TranslationProgress} from 'frontend-js-components-web';
import {useCallback, useEffect, useMemo, useState} from 'react';

import {Field, TranslationManagerProps} from './Types';

export default function useTranslationProgress({
	defaultLanguageId: initialDefaultLanguageId,
	fields: initialFields,
	locales,
	namespace,
	selectedLanguageId: initialSelectedLanguageId,
}: TranslationManagerProps) {
	const [defaultLanguageId, setDefaultLanguageId] = useState(
		initialDefaultLanguageId
	);
	const [fields, setFields] = useState({
		titleMapAsXML: initialFields.titleMapAsXML,
	} as Record<string, Field>);
	const [translations, setTranslations] = useState(
		fieldToTranslations(initialFields)
	);
	const [translationProgress, setTranslationProgress] =
		useState<TranslationProgress | null>();

	const [selectedLanguageId, setSelectedLanguageId] =
		useState<Liferay.Language.Locale>(initialSelectedLanguageId);

	const updateTranslations = useCallback(() => {
		const localizableFields = getAllLocalizableFields(fields);

		const newTranslations = Object.keys(localizableFields).map(
			(fieldName) => {
				const languages = Array.from(
					document.querySelectorAll<HTMLInputElement>(
						`[type="hidden"][data-field-name="${fieldName}"]`
					)
				)
					.filter(
						(input) =>
							input.value || input.getAttribute('data-translated')
					)
					.map(
						(input) =>
							input.dataset.languageid as Liferay.Language.Locale
					);

				return {
					fieldName,
					languages,
				};
			}
		);

		setFields(localizableFields);
		setTranslations(newTranslations);
	}, [setTranslations, setFields, fields]);

	const translatedItems = useMemo(
		() =>
			locales.reduce((acc, locale) => {
				const translatedItems = translations.filter(({languages}) =>
					languages.includes(locale.id)
				).length;

				return {
					...acc,
					...(translatedItems && {[locale.id]: translatedItems}),
				};
			}, {}),
		[translations, locales]
	);

	useEffect(() => {
		const translationProgress = Object.keys(translatedItems).length
			? {
					totalItems: Object.keys(fields).length,
					translatedItems,
				}
			: null;

		setTranslationProgress(translationProgress);
	}, [fields, translatedItems, setTranslationProgress]);

	const defaultLocaleChangeHandler = useCallback(
		(event: any) => {
			const selectedLanguageId = event.item.getAttribute('data-value');

			const defaultLanguageIdInput = document.getElementById(
				`${namespace}defaultLanguageId`
			) as HTMLInputElement;

			if (defaultLanguageIdInput) {
				defaultLanguageIdInput.value = selectedLanguageId;
			}

			setDefaultLanguageId(selectedLanguageId);
			setSelectedLanguageId(selectedLanguageId);
			Liferay.fire('journal:updateSelectedLanguage', {
				item: document.querySelector(
					`[data-languageid="${selectedLanguageId}"][data-value="${selectedLanguageId}"]`
				),
			});
		},
		[namespace, setDefaultLanguageId, setSelectedLanguageId]
	);

	const localeChangeHandler = useCallback(
		(event: any) => {
			const selectedLanguageId = event.item.getAttribute('data-value');

			setSelectedLanguageId(selectedLanguageId);
		},
		[setSelectedLanguageId]
	);

	useEffect(() => {
		Liferay.on(
			'inputLocalized:updateTranslationStatus',
			updateTranslations
		);

		return () => {
			Liferay.detach(
				'inputLocalized:updateTranslationStatus',
				updateTranslations as () => void
			);
		};
	}, [updateTranslations]);

	useEffect(() => {
		Liferay.on(
			'inputLocalized:defaultLocaleChanged',
			defaultLocaleChangeHandler
		);
		Liferay.on('inputLocalized:localeChanged', localeChangeHandler);

		return () => {
			Liferay.detach(
				'inputLocalized:defaultLocaleChanged',
				defaultLocaleChangeHandler as () => void
			);
			Liferay.detach(
				'inputLocalized:localeChanged',
				localeChangeHandler as () => void
			);
		};
	}, [defaultLocaleChangeHandler, localeChangeHandler]);

	return useMemo(
		() => ({
			defaultLanguageId,
			selectedLanguageId,
			translationProgress,
			translations,
			updateTranslations,
		}),
		[
			defaultLanguageId,
			selectedLanguageId,
			translationProgress,
			translations,
			updateTranslations,
		]
	);
}

export function fieldToTranslations(fields: Record<string, Field>) {
	const translations = [];

	for (const fieldName in fields) {
		const languages = fields[fieldName]
			? (Object.keys(fields[fieldName]) as Liferay.Language.Locale[])
			: [];

		translations.push({
			fieldName,
			languages,
		});
	}

	return translations;
}
export function getAllLocalizableFields(initialFields: Record<string, Field>) {
	const ddmFields = Array.from(
		document.querySelectorAll<HTMLInputElement>(
			'[data-ddm-localizable-field-id]'
		)
	).reduce(
		(acc, field) => ({
			...acc,
			[`${field.dataset.fieldName}${field.dataset.ddmLocalizableFieldId}`]:
				{},
		}),
		{}
	);

	return {...initialFields, ...ddmFields};
}
