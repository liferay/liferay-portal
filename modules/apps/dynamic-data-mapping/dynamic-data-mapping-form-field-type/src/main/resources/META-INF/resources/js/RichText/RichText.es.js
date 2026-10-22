/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useConfig, useFormState} from 'data-engine-js-components-web';
import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import {
	CKEditor5ClassicEditor,
	ClassicEditor,
} from 'frontend-editor-ckeditor-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {getImagesSrcFromHtml} from '../util/getImageSrcFromHtml.ts';
import LocalesDropdown from '../util/localizable/LocalesDropdown';
import {
	convertStringToObject,
	getEditingValue,
	getISO639LanguageCode,
	getInitialInternalValue,
	normalizeLocaleId,
	transformAvailableLocalesAndValue,
} from '../util/localizable/transform.es';
import {sanitizeHTML} from '../util/sanitize.ts';

const INITIAL_DEFAULT_LOCALE = {
	icon: themeDisplay.getDefaultLanguageId(),
	localeId: themeDisplay.getDefaultLanguageId(),
};
const INITIAL_EDITING_LOCALE = {
	icon: normalizeLocaleId(themeDisplay.getDefaultLanguageId()),
	localeId: themeDisplay.getDefaultLanguageId(),
};

const ddmFormAdminPortlet =
	'_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet_';

const fieldNeedsToFireOnChange = ['predefinedValue', 'text'];

const skipsChangeValidation = (fieldName) => {
	return !fieldNeedsToFireOnChange.includes(fieldName);
};

const RichText = ({
	defaultLocale = INITIAL_DEFAULT_LOCALE,
	displayErrors,
	editable,
	editingLocale = INITIAL_EDITING_LOCALE,
	editorConfig,
	evaluable,
	fieldName,
	id,
	label = '',
	locale,
	name,
	localizedObjectField,
	onBlur,
	onChange,
	onFocus,
	predefinedValue = '',
	readOnly,
	tip = '',
	valid,
	value,
	visible,
	...otherProps
}) => {
	const {availableLocales, editingLanguageId} = useFormState();

	const editorRef = useRef();

	const contents = useMemo(
		() => (editable ? predefinedValue : value ?? predefinedValue),
		[editable, predefinedValue, value]
	);

	const [currentAvailableLocales, setCurrentAvailableLocales] =
		useState(availableLocales);
	const [currentEditingLocale, setCurrentEditingLocale] =
		useState(editingLocale);
	const [currentValue, setCurrentValue] = useState(
		convertStringToObject(
			contents,
			editingLanguageId ?? defaultLocale?.localeId ?? locale
		)
	);
	const [currentInternalValue, setCurrentInternalValue] = useState(
		getInitialInternalValue({
			editingLocale: currentEditingLocale,
			value: currentValue,
		})
	);
	const [ckEditor5Config, setCKEditor5Config] = useState({
		...editorConfig,
		language: {
			content: getISO639LanguageCode(editingLocale?.localeId),
		},
	});

	const {portletNamespace} = useConfig();

	useEffect(() => {
		if (!Liferay.FeatureFlags['LPD-11235']) {
			setCKEditor5Config({
				...ckEditor5Config,
				language: {
					content: getISO639LanguageCode(
						currentEditingLocale.localeId
					),
				},
			});

			const {availableLocales} = transformAvailableLocalesAndValue({
				availableLocales: currentAvailableLocales,
				defaultLocale,
				value: currentValue,
			});

			setCurrentAvailableLocales(availableLocales);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [currentEditingLocale.localeId]);

	useEffect(() => {
		if (Liferay.FeatureFlags['LPD-11235']) {
			const editor = editorRef.current?.editor;

			if (editor) {
				editor.config.contentsLangDirection =
					Liferay.Language.direction[currentEditingLocale.localeId];
				editor.config.contentsLanguage = currentEditingLocale.localeId;
				editor.setData(currentInternalValue);
			}

			const {availableLocales} = transformAvailableLocalesAndValue({
				availableLocales: currentAvailableLocales,
				defaultLocale,
				value: currentValue,
			});

			setCurrentAvailableLocales(availableLocales);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [currentEditingLocale]);

	/**
	 * This `useEffect` is needed to fix an issue on the Forms edit page causing
	 * the Rich Text field to replace any focused field when changing languages.
	 *
	 * `setCurrentInternalValue` needs to be set after `currentEditingLocale`
	 * and `currentValue` are updated otherwise `handleContentChange` could be
	 * called with stale data.
	 */
	useEffect(() => {
		if (!Liferay.FeatureFlags['LPD-11235']) {
			setCurrentInternalValue(
				getEditingValue({
					defaultLocale,
					editingLocale: currentEditingLocale,
					fieldName,
					value: currentValue,
				})
			);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [currentEditingLocale, currentValue]);

	const changeLanguage = (localeId) => {
		if (!localeId) {
			return;
		}

		let newEditingLocale = {};

		if (currentAvailableLocales) {
			const index = currentAvailableLocales?.findIndex(
				(availableLocale) => availableLocale.localeId === localeId
			);

			newEditingLocale = currentAvailableLocales[index];
		}
		else {
			newEditingLocale = {localeId};
		}

		const newLocaleEntry = convertStringToObject(contents, localeId);

		const newCurrentValue = {
			...currentValue,
			...newLocaleEntry,
		};

		setCurrentValue(newCurrentValue);

		setCurrentEditingLocale({
			...newEditingLocale,
			icon: normalizeLocaleId(newEditingLocale.localeId),
		});

		if (Liferay.FeatureFlags['LPD-11235']) {
			setCurrentInternalValue(
				getEditingValue({
					defaultLocale,
					editingLocale: newEditingLocale,
					fieldName,
					value: newCurrentValue,
				})
			);
		}
	};

	useEffect(() => {
		changeLanguage(editingLanguageId ?? defaultLocale?.localeId ?? locale);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [editingLanguageId, locale, predefinedValue]);

	const handleContentChange = (content) => {
		if (currentValue[currentEditingLocale?.localeId] !== content) {
			const newValue = {
				...currentValue,
				[currentEditingLocale.localeId]: content,
			};

			setCurrentInternalValue(content);

			const {availableLocales} = transformAvailableLocalesAndValue({
				availableLocales: currentAvailableLocales,
				defaultLocale,
				value: newValue,
			});

			setCurrentAvailableLocales(availableLocales);

			if (
				evaluable &&
				portletNamespace === ddmFormAdminPortlet &&
				skipsChangeValidation(fieldName)
			) {
				return;
			}

			if (
				currentValue[currentEditingLocale?.localeId] ||
				currentEditingLocale?.localeId === defaultLocale.localeId ||
				currentValue[defaultLocale.localeId] !== content
			) {
				const newValue = {
					...currentValue,
					[currentEditingLocale.localeId]: content,
				};

				setCurrentValue(newValue);

				onChange({
					target: {
						value: localizedObjectField
							? newValue
							: newValue[currentEditingLocale?.localeId],
					},
				});
			}
		}
	};

	const handleFileDrop = (event) => {
		const files = event.data.dataTransfer.$.files;

		if (files.length) {
			event.stop();
		}
	};

	const handleFilePaste = (event) => {
		const imagesSources = getImagesSrcFromHtml(event.data.dataValue);

		if (imagesSources.length) {
			event.stop();
		}
	};

	const resetTranslation = useCallback(
		({defaultLanguageId}) => {
			const data = currentValue[defaultLanguageId];

			if (!Liferay.FeatureFlags['LPD-11235']) {
				setCurrentInternalValue(data ?? '');
			}
			else {
				const editor = editorRef.current.editor;

				editor.setData(data ?? '', {
					callback: () => editor.resetUndo(),
					noSnapshot: true,
				});

				const newCurrentValue = {...currentValue};

				delete newCurrentValue[currentEditingLocale.localeId];

				setCurrentValue(newCurrentValue);
			}
		},
		[currentEditingLocale, currentValue, editorRef]
	);

	useEffect(() => {
		const handleRestoreState = () => {
			if (!Liferay.FeatureFlags['LPD-11235']) {
				setCurrentInternalValue(value ?? '');
			}
			else {
				editorRef.current.editor.setData(value);
			}
		};

		Liferay.after('ddm:restoreState', handleRestoreState);

		return () => {
			Liferay.detach('ddm:restoreState', handleRestoreState);
		};
	}, [currentValue, value]);

	useEffect(() => {
		Liferay.after('inputLocalized:resetTranslations', resetTranslation);

		return () => {
			Liferay.detach(
				'inputLocalized:resetTranslations',
				resetTranslation
			);
		};
	}, [resetTranslation]);

	return (
		<FieldBase
			{...otherProps}
			displayErrors={displayErrors}
			fieldName={fieldName}
			id={id}
			label={label}
			name={name}
			readOnly={readOnly}
			tip={tip}
			valid={valid}
			visible={visible}
		>
			<ClayInput.Group>
				<ClayInput.GroupItem>
					{!Liferay.FeatureFlags['LPD-11235'] ? (
						<CKEditor5ClassicEditor
							className="w-100"
							config={ckEditor5Config}
							data={currentInternalValue}
							disabled={readOnly}
							key={JSON.stringify(ckEditor5Config)}
							onBlur={onBlur}
							onChange={(event, editor) =>
								handleContentChange(editor.getData())
							}
						/>
					) : (
						<ClassicEditor
							ariaInvalid={displayErrors && !valid}
							ariaLabel={label}
							ariaRequired={otherProps.required}
							className="w-100"
							contents={
								currentValue
									? currentValue[
											currentEditingLocale?.localeId
										]
									: ''
							}
							editorConfig={
								readOnly
									? {
											...editorConfig,
											applicationTitle:
												(label || tip) &&
												`${label}, ${tip}`,
											removePlugins:
												'codemirror, autogrow',
											resize_enabled: true,
										}
									: {
											...editorConfig,
											applicationTitle:
												(label || tip) &&
												`${label}, ${tip}`,
											removePlugins: 'autogrow',
											resize_enabled: true,
										}
							}
							name={name}
							onBlur={onBlur}
							onChange={(content) => handleContentChange(content)}
							onDrop={(event) => handleFileDrop(event)}
							onFocus={onFocus}
							onPaste={(event) => handleFilePaste(event)}
							onSetData={(event) => {
								const editor = event.editor;

								if (editor.mode === 'source') {
									const value = event.data.dataValue;

									const sanitizedValue = sanitizeHTML(value);

									handleContentChange(sanitizedValue);

									event.data.dataValue = sanitizedValue;
								}
							}}
							readOnly={readOnly}
							ref={editorRef}
						/>
					)}
				</ClayInput.GroupItem>

				<input
					id={id}
					name={name}
					type="hidden"
					value={
						localizedObjectField
							? currentValue || ''
							: currentValue
								? currentValue[currentEditingLocale?.localeId]
								: ''
					}
				/>

				{localizedObjectField && (
					<ClayInput.GroupItem
						className="liferay-ddm-form-field-localizable-text"
						shrink
					>
						<LocalesDropdown
							availableLocales={
								localizedObjectField
									? availableLocales
									: currentAvailableLocales
							}
							fieldName={fieldName}
							onLanguageClicked={(localeId) => {
								changeLanguage(localeId);
							}}
							value={currentValue}
						/>
					</ClayInput.GroupItem>
				)}
			</ClayInput.Group>
		</FieldBase>
	);
};

export default RichText;
