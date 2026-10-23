/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventInfo} from '@ckeditor/ckeditor5-utils/dist/index.js';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import {
	TranslationAdminSelector,
	Translations,
	activeLanguageIdsAtom,
	selectedLanguageIdAtom,
} from 'frontend-js-components-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import ClassicEditor from '../ckeditor5/ClassicEditor';
import {LiferayEditorConfig, TEditor} from '../ckeditor5/utils/types';

interface IInputLocalizedProps extends Translations {
	adminMode?: boolean;
	autofillFromDefault?: boolean;
	componentId: string;
	contents: string;
	editorConfig?: LiferayEditorConfig;
	fieldPrefix?: string;
	fieldPrefixSeparator?: string;
	languagesDropdownVisible: boolean;
	name: string;
	onBlur?: (event: EventInfo, editor: TEditor) => void;
	onBlurMethod?: string;
	onChange?: (event: EventInfo, editor: TEditor) => void;
	onChangeMethod?: string;
	onFocus?: (event: EventInfo, editor: TEditor) => void;
	onFocusMethod?: string;
	portletNamespace: string;
	selectedLanguageId: Liferay.Language.Locale;
}

function InputLocalized({
	activeLanguageIds: initialActiveLanguageIds,
	adminMode = false,
	autofillFromDefault,
	availableLocales,
	componentId,
	defaultLanguageId,
	editorConfig,
	fieldPrefix,
	fieldPrefixSeparator,
	languagesDropdownVisible,
	name,
	onBlur,
	onBlurMethod,
	onChange,
	onChangeMethod,
	onFocus,
	onFocusMethod,
	portletNamespace,
	selectedLanguageId: initialSelectedLanguageId,
	translations: initialTranslations,
}: IInputLocalizedProps) {
	const [activeLanguageIds, setActiveLanguageIds] = useLiferayState(
		activeLanguageIdsAtom
	);
	const [selectedLanguageId, setSelectedLanguageId] = useLiferayState(
		selectedLanguageIdAtom
	);

	const [translations, setTranslations] = useState<
		Translations['translations'] | null
	>(initialTranslations);

	/**
	 * Sets initial values in global state atoms.
	 */
	useEffect(() => {
		setActiveLanguageIds(
			initialActiveLanguageIds?.length
				? initialActiveLanguageIds
				: availableLocales.map((locale) => locale.id)
		);

		setSelectedLanguageId(initialSelectedLanguageId || defaultLanguageId);
	}, [
		availableLocales,
		defaultLanguageId,
		initialActiveLanguageIds,
		initialSelectedLanguageId,
		setActiveLanguageIds,
		setSelectedLanguageId,
	]);

	/**
	 * These refs (`selectedLanguageIdRef` and `translationsRef`) provide a
	 * stable pointer to the latest state, preventing the Liferay.component
	 * registration from re-running on every state change.
	 */
	const selectedLanguageIdRef = useRef(selectedLanguageId);

	useEffect(() => {
		selectedLanguageIdRef.current = selectedLanguageId;
	}, [selectedLanguageId]);

	const translationsRef = useRef(translations);

	useEffect(() => {
		translationsRef.current = translations;
	}, [translations]);

	/**
	 * Exposes a public API with `Liferay.component()` so that other parts of
	 * the page (like the Web Content Undo/Redo feature) can programmatically
	 * get or set this component's state.
	 *
	 * To ensure backward compatibility, this API implements the same public
	 * methods as the legacy `Liferay.InputLocalized` AUI component. This allows
	 * existing features that were built for the old taglib to work with this
	 * new React component without modification.
	 *
	 * Currently, only the methods required by
	 * {@link journal-web/src/main/resources/META-INF/resources/js/UndoRedo.js}
	 * are fully implemented.
	 */
	useEffect(() => {
		if (!componentId) {
			return;
		}

		Liferay.component(
			portletNamespace + componentId,
			{
				_updateTranslationStatus: () => {

					// This is intentionally left empty since this originally
					// manually updates the UI, but this is done automatically
					// in React.

				},
				get: (value: string) => {
					if (value === 'translatedLanguages') {
						const translatedLanguageKeys = Object.keys(
							translationsRef.current || {}
						);

						return {
							add: (languageId: Liferay.Language.Locale) => {
								setTranslations((current) => {
									if (
										current &&
										current[languageId] !== undefined
									) {
										return current;
									}

									return {
										...(current || {}),
										[languageId]: '',
									} as Translations['translations'];
								});
							},
							has: (languageId: Liferay.Language.Locale) =>
								translatedLanguageKeys.includes(languageId),
							remove: (languageId: Liferay.Language.Locale) => {
								setTranslations((current) => {
									if (
										!current ||
										current[languageId] === undefined
									) {
										return current;
									}

									const newTranslations = {...current};

									delete newTranslations[languageId];

									return newTranslations as Translations['translations'];
								});
							},
							values: () => translatedLanguageKeys,
						};
					}
				},
				getValue: (languageId: Liferay.Language.Locale) => {
					return (
						translationsRef.current?.[
							languageId || selectedLanguageIdRef.current
						] || ''
					);
				},
				removeInputLanguage: (languageId: Liferay.Language.Locale) => {

					// This is the same as get().remove()

					setTranslations((current) => {
						if (!current || current[languageId] === undefined) {
							return current;
						}

						const newTranslations = {...current};

						delete newTranslations[languageId];

						return newTranslations as Translations['translations'];
					});
				},
				selectFlag: (languageId: Liferay.Language.Locale) => {
					setSelectedLanguageId(languageId);
				},
				updateInput: (value: string) => {
					setTranslations(
						(currentTranslations) =>
							({
								...(currentTranslations || {}),
								[selectedLanguageIdRef.current]: value,
							}) as Translations['translations']
					);
				},
				updateInputLanguage: (
					value: string,
					languageId: Liferay.Language.Locale
				) => {
					setTranslations(
						(currentTranslations) =>
							({
								...(currentTranslations || {}),
								[languageId]: value,
							}) as Translations['translations']
					);
				},
			},
			{
				destroyOnNavigate: true,
			}
		);

		return () => Liferay.destroyComponent(portletNamespace + componentId);
	}, [componentId, portletNamespace, setSelectedLanguageId]);

	/**
	 * This is to sync with web content edit page's TranslationManager
	 * component (journal-web module). Ideally we'd leverage the
	 * `selectedLanguageIdAtom`, but this is to connect with
	 * {@link journal-web/.../TranslationManager.tsx}'s
	 * `handleSelectedLanguageIdChange` function which doesn't currently connect
	 * with the `selectedLanguageIdAtom`.
	 */
	useEffect(() => {
		const handleLocaleChanged = (event: any) => {
			const selectedLanguageId = event.item.getAttribute('data-value');

			setSelectedLanguageId(selectedLanguageId);
		};

		Liferay.on('inputLocalized:localeChanged', handleLocaleChanged);

		return () => {
			Liferay.detach(
				'inputLocalized:localeChanged',
				handleLocaleChanged as () => void
			);
		};
	}, [setSelectedLanguageId]);

	/**
	 * When the `selectedLanguageId` is changed, this will autofill the value
	 * from the `defaultLanguageId` into the editor if there is no translation
	 * for it yet.
	 */
	useEffect(() => {
		if (!autofillFromDefault) {
			return;
		}

		setTranslations((currentTranslations) => {
			const selectedValue =
				currentTranslations?.[selectedLanguageId] || '';

			if (selectedValue === '') {
				const defaultValue =
					currentTranslations?.[defaultLanguageId] || '';

				if (defaultValue) {
					return {
						...(currentTranslations || {}),
						[selectedLanguageId]: defaultValue,
					} as Translations['translations'];
				}
			}

			return currentTranslations;
		});
	}, [autofillFromDefault, defaultLanguageId, selectedLanguageId]);

	const handleBlur = useCallback(
		(event: EventInfo, editor: TEditor) => {
			if (onBlur) {
				onBlur(event, editor);
			}

			if (
				onBlurMethod &&
				typeof (window as any)[onBlurMethod] === 'function'
			) {
				(window as any)[onBlurMethod](event, editor);
			}
		},
		[onBlur, onBlurMethod]
	);

	const handleChange = useCallback(
		(event: EventInfo, editor: TEditor) => {
			setTranslations(
				(translations) =>
					({
						...translations,
						[selectedLanguageId]: editor.getData(),
					}) as Translations['translations']
			);

			if (onChange) {
				onChange(event, editor);
			}

			if (
				onChangeMethod &&
				typeof (window as any)[onChangeMethod] === 'function'
			) {
				(window as any)[onChangeMethod](event, editor);
			}
		},
		[onChange, onChangeMethod, selectedLanguageId]
	);

	const handleFocus = useCallback(
		(event: EventInfo, editor: TEditor) => {
			if (onFocus) {
				onFocus(event, editor);
			}

			if (
				onFocusMethod &&
				typeof (window as any)[onFocusMethod] === 'function'
			) {
				(window as any)[onFocusMethod](event, editor);
			}
		},
		[onFocus, onFocusMethod]
	);

	return (
		<div className="c-gap-2 d-flex">
			<div className="w-100">
				<ClassicEditor
					config={{
						...editorConfig,
						language: {
							content: selectedLanguageId?.split('_')[0],
						},
					}}
					data={translations?.[selectedLanguageId] || ''}
					formInputEnabled={false}
					key={selectedLanguageId}
					onBlur={handleBlur}
					onChange={handleChange}
					onFocus={handleFocus}
				/>
			</div>

			{languagesDropdownVisible && (

				/**
				 * Note: TranslationAdminSelector's `onActiveLanguageIdsChange`
				 * and `onSelectedLanguageIdChange` need to be memoized to
				 * prevent a re-render loop that causes the UI to flicker
				 * between the old and new language. The
				 * `TranslationAdminSelector` component has a `useEffect` that
				 * calls `onActiveLanguageIdsChange` or
				 * `onSelectedLanguageIdChange` when they are changed.
				 * `useLiferayState` currently wraps the `set*` functions with
				 * `useCallback`.
				 *
				 * Temporary ignores the typescript check for
				 * `activeLanguageIds` to avoid the mismatch of readonly and
				 * mutable which would likely require a larger
				 * TranslationAdminSelector refactor. Converting this to a
				 * mutable `[...activeLanguageIds]` results in an infinite loop.
				 */
				<TranslationAdminSelector

					// @ts-ignore

					activeLanguageIds={activeLanguageIds}
					adminMode={adminMode}
					availableLocales={availableLocales}
					defaultLanguageId={defaultLanguageId}
					onActiveLanguageIdsChange={setActiveLanguageIds}
					onSelectedLanguageIdChange={setSelectedLanguageId}
					selectedLanguageId={selectedLanguageId}
					translations={translations}
				/>
			)}

			{Object.keys(translations || {}).map((languageId) => (
				<input
					data-field-name={name}
					data-languageid={languageId}
					id={`${portletNamespace}${name}_${languageId}`}
					key={languageId}
					name={`${portletNamespace}${fieldPrefix ?? ''}${
						fieldPrefixSeparator ?? ''
					}${name}_${languageId}${fieldPrefixSeparator ?? ''}`}
					type="hidden"
					value={
						translations?.[languageId as Liferay.Language.Locale]
					}
				/>
			))}
		</div>
	);
}

export default InputLocalized;
