/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useConfig} from 'data-engine-js-components-web';
import {
	CKEditor5ClassicEditor,
	ClassicEditor,
	advancedClassicEditorConfig,
} from 'frontend-editor-ckeditor-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import FieldBase from '../FieldBase/ReactFieldBase.es';
import LocalesDropdown from '../util/localizable/LocalesDropdown';
import {
	convertStringToObject,
	getEditingValue,
	getInitialInternalValue,
	normalizeLocaleId,
	transformAvailableLocalesAndValue,
} from '../util/localizable/transform.es';

const INITIAL_DEFAULT_LOCALE = {
	icon: themeDisplay.getDefaultLanguageId(),
	localeId: themeDisplay.getDefaultLanguageId(),
};
const INITIAL_EDITING_LOCALE = {
	icon: normalizeLocaleId(themeDisplay.getDefaultLanguageId()),
	localeId: themeDisplay.getDefaultLanguageId(),
};

const ALERT_REGEX = /alert\((.*?)\)/;
const INNER_HTML_REGEX = /innerHTML\s*=\s*.*?/;
const PHP_CODE_REGEX = /<\?[\s\S]*?\?>/g;
const ASP_CODE_REGEX = /<%[\s\S]*?%>/g;
const ASP_NET_CODE_REGEX = /(<asp:[^]+>[\s|\S]*?<\/asp:[^]+>)|(<asp:[^]+\/>)/gi;
const HTML_TAG_WITH_ON_ATTRIBUTE_REGEX =
	/<[^>]+?(\s+\bon\w+=(?:'[^']*'|"[^"]*"|[^'"\s>]+))*\s*\/?>/gi;
const ON_ATTRIBUTE_REGEX = /(\s+\bon\w+=(?:'[^']*'|"[^"]*"|[^'"\s>]+))/gi;

const ddmFormAdminPortlet =
	'_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet_';

const fieldNeedsToFireOnChange = ['predefinedValue', 'text'];

const skipsChangeValidation = (fieldName) => {
	return !fieldNeedsToFireOnChange.includes(fieldName);
};

const RichText = ({
	availableLocales,
	defaultLocale = INITIAL_DEFAULT_LOCALE,
	editable,
	editingLanguageId,
	editingLocale = INITIAL_EDITING_LOCALE,
	editorConfig,
	evaluable,
	fieldName,
	id,
	label,
	locale,
	name,
	localizedObjectField,
	onBlur,
	onChange,
	onFocus,
	predefinedValue = '',
	readOnly,
	value,
	visible,
	...otherProps
}) => {
	const contents = useMemo(
		() => (editable ? predefinedValue : value ?? predefinedValue),
		[editable, predefinedValue, value]
	);

	const editorRef = useRef();

	const {portletNamespace} = useConfig();

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

	useEffect(() => {
		const editor = editorRef.current?.editor;

		if (editor) {
			editor.config.contentsLangDirection =
				Liferay.Language.direction[currentEditingLocale.localeId];
			editor.config.contentsLanguage = currentEditingLocale.localeId;
			editor.setData(currentInternalValue);
		}
		const {availableLocales} = {
			...transformAvailableLocalesAndValue({
				availableLocales: currentAvailableLocales,
				defaultLocale,
				value: currentValue,
			}),
		};

		setCurrentAvailableLocales(availableLocales);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [currentEditingLocale]);

	useEffect(() => {
		changeLanguage(editingLanguageId ?? defaultLocale?.localeId ?? locale);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [editingLanguageId, locale, predefinedValue]);

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

		const newValue = convertStringToObject(contents, localeId);

		setCurrentEditingLocale({
			...newEditingLocale,
			icon: normalizeLocaleId(newEditingLocale.localeId),
		});
		setCurrentInternalValue(
			getEditingValue({
				defaultLocale,
				editingLocale: newEditingLocale,
				fieldName,
				value: newValue,
			})
		);

		setCurrentValue(newValue);
	};

	const handleContentChange = (content) => {
		if (currentValue[currentEditingLocale?.localeId] !== content) {
			const newValue = {
				...currentValue,
				[currentEditingLocale.localeId]: content,
			};
			setCurrentInternalValue(content);

			const {availableLocales} = {
				...transformAvailableLocalesAndValue({
					availableLocales: currentAvailableLocales,
					defaultLocale,
					value: newValue,
				}),
			};

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

	function sanitezeHTML(html) {
		if (Liferay.FeatureFlags['LPD-31212']) {
			return html;
		}

		const sanitizedHtml = html
			.replace(HTML_TAG_WITH_ON_ATTRIBUTE_REGEX, (match) => {
				return match.replace(ON_ATTRIBUTE_REGEX, '');
			})
			.replace(ALERT_REGEX, '')
			.replace(INNER_HTML_REGEX, '')
			.replace(PHP_CODE_REGEX, '')
			.replace(ASP_CODE_REGEX, '')
			.replace(ASP_NET_CODE_REGEX, '');

		return sanitizedHtml;
	}

	const resetTranslation = useCallback(() => {
		editorRef.current.editor.setData(currentValue[defaultLocale.localeId]);
	}, [editorRef, currentValue, defaultLocale]);

	useEffect(() => {
		const handleRestoreState = () => {
			editorRef.current.editor.setData(value);
		};

		Liferay.after('ddm:restoreState', handleRestoreState);

		return () => {
			Liferay.detach('ddm:restoreState', handleRestoreState);
		};
	}, [value, currentValue]);

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
			fieldName={fieldName}
			id={id}
			label={label}
			name={name}
			readOnly={readOnly}
			style={readOnly ? {pointerEvents: 'none'} : null}
			visible={visible}
		>
			<ClayInput.Group>
				<ClayInput.GroupItem>
					{Liferay.FeatureFlags['LPD-11235'] ? (
						<CKEditor5ClassicEditor
							className="w-100"
							config={{
								...advancedClassicEditorConfig,
							}}
							data={
								currentValue
									? currentValue[
											currentEditingLocale?.localeId
										]
									: ''
							}
							onChange={(event, editor) =>
								handleContentChange(editor.getData())
							}
						/>
					) : (
						<ClassicEditor
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
							editorConfig={editorConfig}
							name={name}
							onBlur={onBlur}
							onChange={(content) => handleContentChange(content)}
							onFocus={onFocus}
							onSetData={(event) => {
								const editor = event.editor;

								if (editor.mode === 'source') {
									const value = event.data.dataValue;

									const sanitizedValue = sanitezeHTML(value);

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
							availableLocales={currentAvailableLocales}
							editingLocale={currentEditingLocale}
							fieldName={fieldName}
							onLanguageClicked={(localeId) => {
								changeLanguage(localeId);
							}}
						/>
					</ClayInput.GroupItem>
				)}
			</ClayInput.Group>
		</FieldBase>
	);
};

export default RichText;
