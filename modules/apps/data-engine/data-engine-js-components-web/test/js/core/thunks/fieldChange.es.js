/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fieldChange from '../../../../src/main/resources/META-INF/resources/js/core/thunks/fieldChange.es';

const DEFAULT_LANGUAGE_ID = 'en_US';

const EDITING_LANGUAGE_ID = 'es_ES';

const createPages = (localizedValue, localizable, name = 'Text1') => [
	{
		rows: [
			{
				columns: [
					{
						fields: [
							{
								fieldName: name,
								localizable,
								localizedValue,
								name,
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

const changeField = async (localizedValue, value, localizable = true) => {
	const dispatched = [];

	await fieldChange({
		containerId: 'editWebContent',
		defaultLanguageId: DEFAULT_LANGUAGE_ID,
		editingLanguageId: EDITING_LANGUAGE_ID,
		focusedField: {},
		formId: 'formId',
		objectFields: [],
		pages: createPages(localizedValue, localizable),
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
	const originalThemeDisplay = themeDisplay;

	beforeEach(() => {
		themeDisplay = {
			...originalThemeDisplay,
			getSiteGroupId: () => '20126',
		};
	});

	afterEach(() => {
		jest.useRealTimers();

		themeDisplay = originalThemeDisplay;
	});

	it('does not record a translation on a nonlocalizable field', async () => {
		const field = await changeField(undefined, 'new value', false);

		expect(field.localizedValue).toBeUndefined();
		expect(field.localizedValueEdited).toBeUndefined();
		expect(field.value).toBe('new value');
	});

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

	it('evaluates a configuration field in the default language', async () => {
		jest.useFakeTimers();

		const pages = createPages({[DEFAULT_LANGUAGE_ID]: 'Hello'}, true);

		fetch.mockResponseOnce(JSON.stringify(pages));

		const change = fieldChange({
			containerId: '_portletNamespace_editConfiguration',
			defaultLanguageId: DEFAULT_LANGUAGE_ID,
			editingLanguageId: EDITING_LANGUAGE_ID,
			focusedField: {},
			formId: 'formId',
			objectFields: [],
			pages,
			portletNamespace: '_portletNamespace_',
			properties: {
				fieldInstance: {
					evaluable: true,
					fieldName: 'Text1',
					isDisposed: () => false,
					name: 'Text1',
					type: 'text',
				},
				key: 'value',
				value: 'Hola',
			},
			rules: [],
			submitButtonId: 'submitButtonId',
			viewMode: false,
		})(() => {});

		jest.runAllTimers();

		await change;

		const [, {body}] = fetch.mock.calls[0];

		expect(body.get('languageId')).toBe(DEFAULT_LANGUAGE_ID);
	});

	it('keeps the values of each container when their evaluations overlap', async () => {
		jest.useFakeTimers();

		fetch.mockResponseOnce(
			JSON.stringify(createPages(undefined, false, 'TextA'))
		);
		fetch.mockResponseOnce(
			JSON.stringify(createPages(undefined, false, 'TextB'))
		);

		const evaluatedPages = {};

		const changeContainerField = (containerId, name, value) =>
			fieldChange({
				containerId,
				defaultLanguageId: DEFAULT_LANGUAGE_ID,
				editingLanguageId: DEFAULT_LANGUAGE_ID,
				focusedField: {},
				formId: 'formId',
				objectFields: [],
				pages: createPages(undefined, false, name),
				portletNamespace: '_portletNamespace_',
				properties: {
					fieldInstance: {
						evaluable: true,
						fieldName: name,
						isDisposed: () => false,
						name,
						type: 'text',
					},
					key: 'value',
					value,
				},
				rules: [],
				submitButtonId: 'submitButtonId',
				viewMode: true,
			})((action) => {
				if (action.type === 'field_evaluate') {
					evaluatedPages[containerId] = action.payload;
				}
			});

		const changes = Promise.all([
			changeContainerField('containerA', 'TextA', 'a'),
			changeContainerField('containerB', 'TextB', 'b'),
		]);

		jest.runAllTimers();

		await changes;

		expect(
			evaluatedPages.containerA[0].rows[0].columns[0].fields[0].value
		).toBe('a');
		expect(
			evaluatedPages.containerB[0].rows[0].columns[0].fields[0].value
		).toBe('b');
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
