/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EVENT_TYPES as CORE_EVENT_TYPES,
	INITIAL_PAGES,
	INITIAL_STATE,
	PagesVisitor,
	RulesSupport,
	getUid,
	languageReducer,
} from 'data-engine-js-components-web';

export const BUILDER_INITIAL_STATE = {
	...INITIAL_STATE,

	// Flag that indicates the index of the rule being edited.

	currentRuleLoc: null,
	editingLanguageId: themeDisplay.getDefaultLanguageId(),
	fieldSets: [],
	fieldTypes: [],
	formInstanceId: 0,
	functionsMetadata: {},
	objectFields: [],
	paginationMode: 'multi-pages',
	rules: [],
};

/**
 * NormalizePages deals with manipulations of the Field to change behaviors or
 * fixes something specific to the structure that affects the Field.
 *
 * Called only at application startup, but adds an initial computing load
 * to traverse through all fields on the form as well as nested fields.
 */
const normalizePages = (pages) => {
	const visitor = new PagesVisitor(pages);

	return visitor.mapFields(
		({settingsContext, ...otherProps}) => {
			const visitor = new PagesVisitor(settingsContext.pages);

			// Inferences the edited property to `true` of the options of a Field with
			// the type `options`. This is an implementation that came from the old
			// implementation of the LayoutProvider, to remove this it is necessary
			// to refactor the Options field to better deal with states and location.

			return {
				...otherProps,
				localizedValue: {},
				settingsContext: {
					...settingsContext,
					pages: visitor.mapFields((field) => {
						if (field.type === 'options') {
							const languageIds = Object.keys(field.value);

							return {
								...field,
								value: languageIds.reduce(
									(previousValue, currentLanguageId) => ({
										...previousValue,
										[currentLanguageId]: field.value[
											currentLanguageId
										].map((option) => ({
											...option,
											edited: true,
										})),
									}),
									{}
								),
							};
						}

						return field;
					}),
				},
				value: undefined,
			};
		},
		true,
		true
	);
};

const localizePages = (
	pages,
	{availableLanguageIds, defaultLanguageId, editingLanguageId}
) => {
	const {pages: localizedPages} = languageReducer(
		{availableLanguageIds, defaultLanguageId},
		{
			payload: {editingLanguageId, pages},
			type: CORE_EVENT_TYPES.LANGUAGE.CHANGE,
		}
	);

	return localizedPages;
};

export function initState(
	{
		pages: initialPages,
		paginationMode: initialPaginationMode,
		rules: initialRules,
		successPageSettings: initialSuccessPageSettings,
		...otherProps
	},
	{view}
) {
	let pages = INITIAL_PAGES;

	if (initialPages.length) {
		const normalizedPages = normalizePages(initialPages);

		pages = localizePages(normalizedPages, otherProps);
	}

	// Before starting the application formats the rules there may be a broken rule with
	// an invalid field and it is necessary to remove it from the rule.

	const rules = initialRules.length
		? RulesSupport.formatRules(pages, initialRules)
		: initialRules;

	// The Forms application is also rendered for Element Set, so we have to configure some
	// components to behave differently. This can be removed when Element Set is deprecated.

	const paginationMode =
		view === 'fieldSets' ? 'single-page' : initialPaginationMode;

	const successPageSettings = {
		body: initialSuccessPageSettings?.body
			? initialSuccessPageSettings?.body
			: {
					[themeDisplay.getLanguageId()]: Liferay.Language.get(
						'your-information-was-successfully-received-thank-you-for-filling-out-the-form'
					),
				},
		enabled:
			view === 'fieldSets'
				? false
				: initialSuccessPageSettings?.enabled ?? true,
		title: initialSuccessPageSettings?.title
			? initialSuccessPageSettings?.title
			: {
					[themeDisplay.getLanguageId()]:
						Liferay.Language.get('thank-you'),
				},
	};

	return {
		pages: [

			// Adds new properties to pages for rendering and provides
			// a unique uid that will avoid `key` problems when rendering
			// pages.

			...pages.map((page, pageIndex) => ({
				...page,
				id: getUid(),

				// Deprecated property: The components that use this
				// information can consume the usePage which can keep
				// the index in context when iterating pages.

				pageIndex,

				// Deprecated property: Components that need this information can
				// directly consume `pages.length`.

				total: pages.length,
			})),

			// Adds the success page enabled by default.

			successPageSettings.enabled
				? {
						contentRenderer: 'success',
						id: getUid(),
						paginationItemRenderer: `${paginationMode}_success`,
						rows: [],
						successPageSettings,
					}
				: false,
		].filter(Boolean),
		paginationMode,
		rules,
		successPageSettings,
		...otherProps,
	};
}
