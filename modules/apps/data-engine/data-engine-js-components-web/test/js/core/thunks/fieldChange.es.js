/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fieldChange from '../../../../src/main/resources/META-INF/resources/js/core/thunks/fieldChange.es';

const DEFAULT_LANGUAGE_ID = 'en_US';

const EDITING_LANGUAGE_ID = 'es_ES';

const createPages = (localizedValue) => [
	{
		rows: [
			{
				columns: [
					{
						fields: [
							{
								fieldName: 'Text1',
								localizable: true,
								localizedValue,
								name: 'Text1',
								type: 'text',
								visible: true,
							},
						],
					},
				],
			},
		],
	},
];

const changeField = async (localizedValue, value) => {
	const dispatched = [];

	await fieldChange({
		containerId: 'editWebContent',
		defaultLanguageId: DEFAULT_LANGUAGE_ID,
		editingLanguageId: EDITING_LANGUAGE_ID,
		focusedField: {},
		formId: 'formId',
		objectFields: [],
		pages: createPages(localizedValue),
		portletNamespace: '_portletNamespace_',
		properties: {
			fieldInstance: {
				evaluable: false,
				fieldName: 'Text1',
				isDisposed: () => false,
				name: 'Text1',
				type: 'text',
			},
			key: 'value',
			value,
		},
		rules: [],
		submitButtonId: 'submitButtonId',
		viewMode: false,
	})((action) => dispatched.push(action));

	const {payload} = dispatched.find((action) =>
		Array.isArray(action.payload)
	);

	return payload[0].rows[0].columns[0].fields[0];
};

describe('fieldChange', () => {
	it('does not record an edit when an untranslated locale is left untouched', async () => {
		const field = await changeField({[DEFAULT_LANGUAGE_ID]: ''}, '');

		expect(
			field.localizedValueEdited?.[EDITING_LANGUAGE_ID]
		).toBeUndefined();
		expect(field.localizedValue[EDITING_LANGUAGE_ID]).toBeUndefined();
	});

	it('does not record an edit when the default language value is left untouched', async () => {
		const field = await changeField(
			{[DEFAULT_LANGUAGE_ID]: 'Hello'},
			'Hello'
		);

		expect(
			field.localizedValueEdited?.[EDITING_LANGUAGE_ID]
		).toBeUndefined();
		expect(field.localizedValue[EDITING_LANGUAGE_ID]).toBeUndefined();
	});

	it('records an edit when the locale is given its own value', async () => {
		const field = await changeField(
			{[DEFAULT_LANGUAGE_ID]: 'Hello'},
			'Hola'
		);

		expect(field.localizedValueEdited[EDITING_LANGUAGE_ID]).toBe(true);
		expect(field.localizedValue[EDITING_LANGUAGE_ID]).toBe('Hola');
	});

	it('records an edit when an existing translation is cleared', async () => {
		const field = await changeField(
			{[DEFAULT_LANGUAGE_ID]: 'Hello', [EDITING_LANGUAGE_ID]: 'Hola'},
			''
		);

		expect(field.localizedValueEdited[EDITING_LANGUAGE_ID]).toBe(true);
		expect(field.localizedValue[EDITING_LANGUAGE_ID]).toBe('');
	});
});
