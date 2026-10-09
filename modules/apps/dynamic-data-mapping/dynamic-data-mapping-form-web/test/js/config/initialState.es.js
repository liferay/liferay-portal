/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {initState} from '../../../src/main/resources/META-INF/resources/js/config/initialState.es';

const getPages = (...fields) => [{rows: [{columns: [{fields, size: 12}]}]}];

const getField = (pages) => pages[0].rows[0].columns[0].fields[0];

const getInitialState = (languageId) =>
	initState(
		{
			availableLanguageIds: ['en_US', 'ja_JP'],
			defaultLanguageId: languageId,
			editingLanguageId: languageId,
			pages: getPages({
				fieldName: 'Paragraph12345678',
				instanceId: 'abcd1234',
				label: 'Description',
				name: '_portlet_ddm$$Paragraph12345678$abcd1234$0$$en_US',
				settingsContext: {
					pages: getPages({
						fieldName: 'label',
						localizable: true,
						localizedValue: {en_US: 'Description', ja_JP: '説明文'},
						type: 'text',
						value: 'Description',
					}),
				},
				type: 'paragraph',
			}),
			paginationMode: 'multi-pages',
			rules: [],
		},
		{view: 'formBuilder'}
	);

describe('initState', () => {
	it('keeps the field properties when the editing language is the rendered language', () => {
		const {pages} = getInitialState('en_US');

		expect(getField(pages).label).toBe('Description');
	});

	it('localizes the field properties for the editing language', () => {
		const {pages} = getInitialState('ja_JP');

		expect(getField(pages).label).toBe('説明文');
	});
});
