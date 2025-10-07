/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LanguagePicker} from '@clayui/core';
import ClayDropDown from '@clayui/drop-down';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openConfirmModal} from '@liferay/layout-js-components-web';
import React, {
	Key,
	useCallback,
	useEffect,
	useMemo,
	useRef,
	useState,
} from 'react';

import './LocalizationSelect.scss';

import {ClayButtonWithIcon} from '@clayui/button';
import {openToast} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';

const EVENT_TRANSLATION_STATUS = 'localizationSelect:updateTranslationStatus';

type Props = {
	allowLocalizationManagement: boolean;
	autoTranslateURL: string;
	autoTranslationEnabled: boolean;
	defaultLanguageId: Liferay.Language.Locale;
	editMode: boolean;
	hideLanguageLabel: boolean;
	locales: Array<{
		id: Liferay.Language.Locale;
		label: string;
		name?: string;
		symbol: string;
	}>;
	size: string;
};

type Translations = {
	[key: string]: {total: number; translated: number};
};

type TranslationStatus = 'not-translated' | 'translating' | 'translated';

export function LocalizationSelect({
	allowLocalizationManagement,
	autoTranslateURL,
	autoTranslationEnabled,
	defaultLanguageId,
	editMode,
	hideLanguageLabel,
	locales,
	size,
}: Props) {
	const [autoTranslating, setAutoTranslating] = useState(false);
	const [translations, setTranslations] = useState<Translations>({});
	const [form, setForm] = useState<HTMLFormElement>();

	const [selectedLocaleId, setSelectedLocaleId] = useState(defaultLanguageId);

	const selectedLocaleLabel = useMemo(() => {
		return locales.find(({id}) => id === selectedLocaleId)?.label;
	}, [locales, selectedLocaleId]);

	const selectedLocaleStatus: TranslationStatus = useMemo(() => {
		if (!selectedLocaleLabel) {
			return 'not-translated';
		}

		const translation = translations[selectedLocaleLabel];

		if (!translation) {
			return 'not-translated';
		}

		const {total, translated} = translation;

		if (total === translated) {
			return 'translated';
		}

		return 'translating';
	}, [selectedLocaleLabel, translations]);

	const [relatedLocaleId, setRelatedLocaleId] =
		useState<Liferay.Language.Locale | null>(null);

	const [dropdownActive, setDropdownActive] = useState(false);
	const [dropdownTrigger, setDropdownTrigger] =
		useState<HTMLButtonElement | null>();

	const containerRef = useRef<HTMLDivElement>(null);

	const onSelectedLocaleChange = (localeId: Liferay.Language.Locale) => {
		setSelectedLocaleId(localeId);
		setDropdownActive(false);
	};

	const autoTranslate = useCallback(async () => {

		// Show a warning if there's any existing translation to the target language id

		// Take related language as source if it's present, otherwise take default

		const sourceLanguageId = relatedLocaleId || defaultLanguageId;

		// Get all inputs for auto translatable types in source language and prepare data

		const autoTranslatableTypes = [
			{isHtml: false, type: 'text'},
			{isHtml: false, type: 'long-text'},
			{isHtml: true, type: 'html'},
		];

		const fields: Record<string, string> = {};
		const html: Record<string, boolean> = {};

		// We will store whether the target language has any existing translation or not

		let hasTranslation = false;

		for (const {isHtml, type} of autoTranslatableTypes) {
			const element = form || document;

			const inputs = element.querySelectorAll<HTMLInputElement>(
				`[data-localizable="true"][data-field-type="${type}"] [type="hidden"][name$="_${sourceLanguageId}"]`
			);

			const translated = element.querySelectorAll<HTMLInputElement>(
				`[data-localizable="true"][data-field-type="${type}"] [type="hidden"][name$="_${selectedLocaleId}"]`
			);

			if (translated.length) {
				hasTranslation = true;
			}

			for (const input of inputs) {

				// Remove language suffix from name

				const name = input.name.replace(/_[a-z]{2}_[A-Z]{2}$/, '');

				fields[name] = input.value;
				html[name] = isHtml;
			}
		}

		if (
			hasTranslation &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('continue'),
				center: true,
				onCloseFocusElement: dropdownTrigger,
				status: 'warning',
				text: sub(
					Liferay.Language.get(
						'an-existing-x-translation-is-already-available'
					),
					selectedLocaleLabel
				),
				title: Liferay.Language.get('content-override'),
			}))
		) {
			return;
		}

		// Perform auto translate

		setAutoTranslating(true);

		const response = await fetch(autoTranslateURL, {
			body: JSON.stringify({
				fields,
				html,
				sourceLanguageId,
				targetLanguageId: selectedLocaleId,
			}),
			method: 'POST',
		}).then((response) => response.json());

		setAutoTranslating(false);

		if (response.error) {
			openToast({
				message: response.error.message,
				type: 'danger',
			});

			return;
		}

		openToast({
			message: sub(
				Liferay.Language.get(
					'x-translation-has-been-successfully-received'
				),
				selectedLocaleLabel
			),
			type: 'success',
		});

		// Fire event with translated values

		const normalizedFields = Object.fromEntries(
			Object.entries(response.fields).map(([key, value]) => [
				key,
				Liferay.Util.unescapeHTML(value as string),
			])
		);

		Liferay.fire('localizationSelect:autoTranslate', {
			fields: normalizedFields,
			formId: form?.id,
			languageId: selectedLocaleId,
		});
	}, [
		autoTranslateURL,
		defaultLanguageId,
		dropdownTrigger,
		form,
		relatedLocaleId,
		selectedLocaleId,
		selectedLocaleLabel,
	]);

	useEffect(() => {
		const form = containerRef.current?.closest(
			'.lfr-layout-structure-item-form'
		);

		if (form) {
			setForm(form as HTMLFormElement);
		}
	}, []);

	useEffect(() => {
		const updateTranslationStatus = ({
			languageId,
		}: {
			languageId: Liferay.Language.Locale;
		}) => {
			const element = form || document;

			const total = element.querySelectorAll(
				'[data-localizable="true"]'
			).length;

			const translated = new Set([
				...Array.from(
					element.querySelectorAll(
						`[data-localizable="true"] [type="file"][name$="_${languageId}"], ` +
							`[data-localizable="true"] [type="hidden"][name$="_${languageId}"]`
					)
				)
					.filter((input) => hasValue(input as HTMLInputElement))
					.map((input) => (input as HTMLInputElement).name),
			]).size;

			const label = locales.find(
				(locale) => locale.id === languageId
			)?.label;

			if (!label) {
				return;
			}

			setTranslations((previous) => {
				const nextTranslations: Translations = {
					...previous,
					[label]: {
						total,
						translated,
					},
				};

				if (!translated) {
					delete nextTranslations[label];
				}

				return nextTranslations;
			});
		};

		Liferay.on(EVENT_TRANSLATION_STATUS, updateTranslationStatus);

		for (const locale of locales) {
			updateTranslationStatus({languageId: locale.id});
		}

		return () => {
			Liferay.detach(EVENT_TRANSLATION_STATUS, updateTranslationStatus);
		};
	}, [defaultLanguageId, form, locales]);

	useEffect(() => {
		const onLocaleChanged = ({
			formId,
			languageId,
		}: {
			formId?: string;
			languageId: Liferay.Language.Locale;
		}) => {

			// If event is sent from another form, we will store the language as related

			if (formId && formId !== form?.id) {
				setRelatedLocaleId(languageId);

				return;
			}

			// Otherwise, store it as selected

			if (selectedLocaleId !== languageId) {
				setSelectedLocaleId(languageId);
			}
		};

		Liferay.on('localizationSelect:localeChanged', onLocaleChanged);

		return () => {
			Liferay.detach('localizationSelect:localeChanged', onLocaleChanged);
		};
	}, [form, selectedLocaleId]);

	return (
		<div className="align-items-center c-gap-2 d-flex" ref={containerRef}>
			<LanguagePicker
				active={dropdownActive}
				defaultLocaleId={defaultLanguageId}
				hideTriggerText={hideLanguageLabel}
				locales={locales}
				messages={{
					default: Liferay.Language.get('default'),
					option: Liferay.Language.get('x-language-x'),
					translated: Liferay.Language.get('translated'),
					translating: Liferay.Language.get('translating-x-x'),
					trigger: Liferay.Language.get(
						'select-a-language.-current-language-x'
					),
					untranslated: Liferay.Language.get('not-translated'),
				}}
				onActiveChange={(active: boolean) => {
					if (!editMode) {
						setDropdownActive(active);
					}
				}}
				onSelectedLocaleChange={(id: Key) => {
					onSelectedLocaleChange(id as Liferay.Language.Locale);

					Liferay.fire('localizationSelect:localeChanged', {
						formId: form?.id,
						languageId: id,
					});
				}}
				selectedLocaleId={selectedLocaleId}
				small={size === 'small'}
				translations={translations}
			/>

			{allowLocalizationManagement &&
			selectedLocaleId !== defaultLanguageId ? (
				<ClayDropDown
					hasLeftSymbols
					menuElementAttrs={{
						containerProps: {
							className: 'cadmin',
						},
					}}
					trigger={
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get(
								'localization-actions'
							)}
							displayType="secondary"
							monospaced
							ref={setDropdownTrigger}
							size={size === 'small' ? 'sm' : 'regular'}
							symbol="ellipsis-v"
							title={Liferay.Language.get('localization-actions')}
						/>
					}
				>
					<ClayDropDown.ItemList>
						{autoTranslationEnabled ? (
							<ClayDropDown.Item
								disabled={autoTranslating}
								onClick={autoTranslate}
								symbolLeft="stars"
							>
								<div className="align-items-center d-flex justify-content-between">
									{Liferay.Language.get('auto-translate')}

									{autoTranslating ? (
										<ClayLoadingIndicator className="m-0" />
									) : null}
								</div>
							</ClayDropDown.Item>
						) : null}

						<ClayDropDown.Item
							disabled={selectedLocaleStatus === 'translated'}
							onClick={async () => {
								if (
									await openConfirmModal({
										buttonLabel:
											Liferay.Language.get(
												'mark-as-translated'
											),
										center: true,
										onCloseFocusElement: dropdownTrigger,
										status: 'info',
										text: sub(
											Liferay.Language.get(
												'all-the-fields-for-x-will-be-marked-as-translated'
											),
											selectedLocaleLabel
										),
										title: sub(
											Liferay.Language.get(
												'mark-x-as-translated'
											),
											selectedLocaleLabel
										),
									})
								) {
									Liferay.fire(
										'localizationSelect:markAsTranslated',
										{
											formId: form?.id,
											languageId: selectedLocaleId,
										}
									);
								}
							}}
							symbolLeft="check-circle"
						>
							{Liferay.Language.get('mark-as-translated')}
						</ClayDropDown.Item>

						<ClayDropDown.Item
							disabled={selectedLocaleStatus === 'not-translated'}
							onClick={async () => {
								if (
									await openConfirmModal({
										buttonLabel:
											Liferay.Language.get('delete'),
										center: true,
										onCloseFocusElement: dropdownTrigger,
										status: 'danger',
										text: sub(
											Liferay.Language.get(
												'x-translation-will-be-deleted-and-content-fields-will-be-set-to-default-value'
											),
											selectedLocaleLabel
										),
										title: sub(
											Liferay.Language.get(
												'delete-x-translation'
											),
											selectedLocaleLabel
										),
									})
								) {
									Liferay.fire(
										'localizationSelect:resetTranslation',
										{
											formId: form?.id,
											languageId: selectedLocaleId,
										}
									);
								}
							}}
							symbolLeft="trash"
						>
							{Liferay.Language.get('reset-translation')}
						</ClayDropDown.Item>
					</ClayDropDown.ItemList>
				</ClayDropDown>
			) : null}
		</div>
	);
}

function hasValue(input: HTMLInputElement) {
	if (input.type === 'file') {
		return Boolean(input.files?.length);
	}

	return Boolean(input.getAttribute('value')?.length);
}
