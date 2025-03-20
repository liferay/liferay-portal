/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {debounce} from 'frontend-js-web';

import {convertToFormData, makeFetch} from './fetch.es';
import {PagesVisitor} from './visitors.es';

const EVALUATOR_URL =
	themeDisplay.getPathContext() +
	'/o/dynamic-data-mapping-form-context-provider/';

let controller = null;

export function mergeFieldOptions(field, newField) {
	let newValue = {...newField.value};

	Object.keys(newValue).forEach((languageId) => {
		newValue = {
			...newValue,
			[languageId]: newValue[languageId].map((option) => {
				const existingOption =
					field.value &&
					field.value[languageId] &&
					field.value[languageId].find(
						({value}) => value === option.value
					);

				return {
					...option,
					edited: existingOption && existingOption.edited,
				};
			}),
		};
	});

	return newValue;
}

const sanitizePagesCaptchaHTML = (pages) => {
	const visitor = new PagesVisitor(pages);

	visitor.mapFields((field) => {
		if (field.fieldName === '_CAPTCHA_') {
			delete field.html;
		}
	});
};

export function mergePages(
	defaultLanguageId,
	editingLanguageId,
	fieldName,
	newPages,
	sourcePages,
	viewMode
) {
	const newPagesVisitor = new PagesVisitor(newPages);
	const sourcePagesVisitor = new PagesVisitor(sourcePages);

	const sourceFieldsByName = new Map();
	const sourceFieldsByFieldName = new Map();

	sourcePagesVisitor.visitFields((field) => {
		sourceFieldsByName.set(field.name, field);
		sourceFieldsByFieldName.set(field.fieldName, field);
	});

	return newPagesVisitor.mapFields(
		(field) => {
			const sourceField =
				sourceFieldsByName.get(field.name) ??
				sourceFieldsByFieldName.get(field.fieldName) ??
				{};

			let fieldValue = field.valueChanged
				? field.value
				: sourceField.value;

			if (
				field.visible !== sourceField.visible &&
				field.visible &&
				viewMode
			) {
				fieldValue = '';
			}

			let newField = {
				...sourceField,
				...field,
				defaultLanguageId,
				displayErrors:
					sourceField.displayErrors || field.fieldName === fieldName,
				editingLanguageId,
				valid: field.valid !== false,
				value: fieldValue,
			};

			if (newField.type === 'options') {
				newField = {
					...newField,
					value: mergeFieldOptions(sourceField, newField),
				};
			}

			if (newField.localizable) {
				newField = {
					...newField,
					localizedValue: {
						...sourceField.localizedValue,
					},
				};
			}
			else {
				newField = {
					...newField,
					editOnlyInDefaultLanguage:
						sourceField.editOnlyInDefaultLanguage,
				};
			}

			return newField;
		},
		false,
		true
	);
}

const doEvaluate = debounce((fieldName, evaluatorContext, callback) => {
	const {
		containerId,
		defaultLanguageId,
		editingLanguageId,
		formId,
		groupId,
		nextPage,
		pages,
		portletNamespace,
		previousPage,
		viewMode,
	} = evaluatorContext;

	if (controller) {
		controller.abort();
	}

	if (window.AbortController) {
		controller = new AbortController();
	}

	sanitizePagesCaptchaHTML(pages);

	makeFetch({
		body: convertToFormData({
			languageId:
				containerId === 'editObjectEntry'
					? defaultLanguageId
					: editingLanguageId,
			p_auth: Liferay.authToken,
			p_l_id: themeDisplay.getPlid(),
			p_v_l_s_g_id: themeDisplay.getSiteGroupId(),
			portletNamespace,
			serializedFormContext: JSON.stringify({
				...evaluatorContext,
				formId,
				groupId: groupId ? groupId : themeDisplay.getScopeGroupId(),
				nextPage: nextPage ? nextPage : null,
				portletNamespace,
				previousPage: previousPage ? previousPage : null,
			}),
			trigger: fieldName,
		}),
		signal: controller && controller.signal,
		url: EVALUATOR_URL,
	})
		.then((newPages) => {
			if (newPages.statusCode) {
				callback(newPages);
			}

			const mergedPages = mergePages(
				defaultLanguageId,
				editingLanguageId,
				fieldName,
				newPages,
				pages,
				viewMode
			);

			callback(null, mergedPages);
		})
		.catch((error) => callback(error));
}, 200);

export function evaluate(fieldName, evaluatorContext) {
	return new Promise((resolve, reject) => {
		doEvaluate(fieldName, evaluatorContext, (error, pages) => {
			if (error) {
				return reject(error);
			}

			resolve(pages);
		});
	});
}
