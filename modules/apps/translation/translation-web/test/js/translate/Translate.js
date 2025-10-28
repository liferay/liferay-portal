/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import Translate from '../../../src/main/resources/META-INF/resources/js/translate/Translate';

const baseProps = {
	additionalFields: {
		redirect: 'http://redirect-url',
		sourceLanguageId: 'en_US',
		targetLanguageId: 'es_ES',
	},
	autoTranslateEnabled: true,
	currentUrl: 'http://current-url',
	getAutoTranslateURL: 'http://translation-url/auto_translate',
	infoFieldSetEntries: [
		{
			fields: [
				{
					editorConfiguration: null,
					html: false,
					id: 'infoField--title--',
					label: 'Title',
					multiline: false,
					sourceContent: ['mock title'],
					sourceContentDir: 'ltr',
					targetContent: ['mock title'],
					targetContentDir: 'ltr',
					targetLanguageId: 'es_ES',
				},
				{
					editorConfiguration: {},
					html: true,
					id: 'infoField--description--',
					label: 'Description',
					multiline: false,
					sourceContent: ['<p>mock summary</p>'],
					sourceContentDir: 'ltr',
					targetContent: ['<p>mock summary</p>'],
					targetContentDir: 'ltr',
					targetLanguageId: 'es_ES',
				},
			],
			legend: 'Basic Information',
		},
		{
			fields: [
				{
					editorConfiguration: {},
					html: true,
					id: 'infoField--content--',
					label: 'Content',
					multiline: true,
					sourceContent: ['<p>mock content</p>'],
					sourceContentDir: 'ltr',
					targetContent: ['<p>mock content</p'],
					targetContentDir: 'ltr',
					targetLanguageId: 'es_ES',
				},
				{
					editorConfiguration: {},
					html: true,
					id: 'infoField--repeteableContent--',
					label: 'Content',
					multiline: true,
					sourceContent: [
						'<p>mock source repeteable field 1</p>',
						'<p>mock source repeteable field 2</p>',
						'<p>mock source repeteable field 3</p>',
					],
					sourceContentDir: 'ltr',
					targetContent: [
						'<p>mock target repeteable field 1</p>',
						'<p>mock target repeteable field 2</p>',
						'<p>mock target repeteable field 3</p>',
					],
					targetContentDir: 'ltr',
					targetLanguageId: 'es_ES',
				},
				{
					editorConfiguration: null,
					html: false,
					id: 'infoField--text--',
					label: 'Text',
					multiline: false,
					sourceContent: ['mock text'],
					sourceContentDir: 'ltr',
					targetContent: ['mock text'],
					targetContentDir: 'ltr',
					targetLanguageId: 'es_ES',
				},
			],
			legend: 'Content with repeateable fields',
		},
	],
	portletId: 'mock_TranslationPortlet',
	portletNamespace: '_mock_TranslationPortlet_',
	publishButtonDisabled: false,
	publishButtonLabel: 'Publish',
	redirectURL: 'http://redirect-url',
	saveButtonDisabled: false,
	saveButtonLabel: 'Save as Draft',
	sourceLanguageId: 'en_US',
	sourceLanguageIdTitle: 'en-US',
	targetLanguageId: 'es_ES',
	targetLanguageIdTitle: 'es-ES',
	translateLanguagesSelectorData: {
		sourceAvailableLanguages: ['en_US', 'es_ES'],
		sourceLanguageId: 'en_US',
		targetAvailableLanguages: [
			'ar_SA',
			'ca_ES',
			'zh_CN',
			'nl_NL',
			'fi_FI',
			'fr_FR',
			'de_DE',
			'hu_HU',
			'ja_JP',
			'pt_BR',
			'es_ES',
			'sv_SE',
		],
		targetLanguageId: 'es_ES',
	},
	translationPermission: true,
	updateTranslationPortletURL: 'http://update-url',
	workflowActions: {
		PUBLISH: '1',
		SAVE_DRAFT: '2',
	},
};

const renderComponent = (props) => render(<Translate {...props} />);

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

describe('Translate', () => {
	afterEach(cleanup);

	it('renders with auto-translate enabled', () => {
		const {asFragment} = renderComponent(baseProps);

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders with experiences selector', () => {
		const {asFragment} = renderComponent({
			...baseProps,
			experiencesSelectorData: {
				label: 'Experience',
				options: [
					{
						label: 'Default',
						value: '0',
					},
					{
						label: 'Experience 1',
						value: '1',
					},
				],
				value: '0',
			},
		});

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders auto-translate field button disabled when the field sourceContent is empty', () => {
		const {getByText} = renderComponent({
			...baseProps,
			infoFieldSetEntries: [
				{
					...baseProps.infoFieldSetEntries[1],
					fields: [
						{
							...baseProps.infoFieldSetEntries[1].fields[0],
							sourceContent: [''],
						},
					],
				},
			],
		});

		expect(
			getByText(
				`auto-translate-${baseProps.infoFieldSetEntries[1].fields[0].label}-field`
			).closest('button')
		).toBeDisabled();
	});

	describe('given a valid server response', () => {
		beforeEach(() => {
			fetch.mockResponseOnce(
				JSON.stringify({
					fields: {
						'infoField--content--0':
							'<p>simulacro de contenido</p>',
						'infoField--description--0': '<p>resumen simulado</p>',
						'infoField--repeteableContent--0':
							'<p>campo repetible de fuente simulada 1</p>',
						'infoField--repeteableContent--1':
							'<p>campo repetible de fuente simulada 2</p>',
						'infoField--repeteableContent--2':
							'<p>campo repetible de fuente simulada 3</p>',
						'infoField--text--0':
							'Esto es un &quot;texto de ejemplo&quot;',
						'infoField--title--0': 'título simulado&#39;',
					},
					sourceLanguageId: 'en_US',
					targetLanguageId: 'es_ES',
				})
			);
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('when the user clicks on the auto-translate field button', () => {
			let infoFieldContent;
			let result;

			beforeEach(async () => {
				infoFieldContent = baseProps.infoFieldSetEntries[0].fields[0];
				result = renderComponent(baseProps);

				const {getByText} = result;

				const autoTranslateFieldButton = getByText(
					`auto-translate-${infoFieldContent.label}-field`
				).closest('button');

				await act(async () => {
					fireEvent.click(autoTranslateFieldButton);
				});
			});

			it('sends a POST request to the server', async () => {
				const [url, {body}] = fetch.mock.calls[0];
				const request = JSON.parse(body);

				expect(url).toBe(baseProps.getAutoTranslateURL);

				expect(request.fields[`${infoFieldContent.id}0`]).toBe(
					infoFieldContent.sourceContent[0]
				);

				expect(request.sourceLanguageId).toBe(
					baseProps.sourceLanguageId
				);
				expect(request.targetLanguageId).toBe(
					baseProps.targetLanguageId
				);
			});

			// LPS-133164

			it('updates the `Title` input of a non-html field with the translated message with unescaped characters', () => {
				const {getByDisplayValue} = result;

				expect(
					getByDisplayValue("título simulado'")
				).toBeInTheDocument();
			});

			it('renders a success message', () => {
				const {getByText} = result;

				expect(getByText('field-translated')).toBeInTheDocument();
			});
		});

		describe('when the user clicks on the auto-translate general button', () => {
			let result;

			beforeEach(async () => {
				result = renderComponent(baseProps);

				const {getByText} = result;
				const autoTranslateButton = getByText('auto-translate');

				await act(async () => {
					fireEvent.click(autoTranslateButton);
				});
			});

			// LPS-133164

			it('updates the `Title` input with a translated message containing unescaped HTML characters', () => {
				const {getByDisplayValue} = result;

				expect(
					getByDisplayValue("título simulado'")
				).toBeInTheDocument();
			});

			// LPD-52521

			it('updates the `Text` input with a translated message containing unescaped HTML characters', () => {
				const {getByDisplayValue} = result;

				expect(
					getByDisplayValue('Esto es un "texto de ejemplo"')
				).toBeInTheDocument();
			});

			it('renders a success message', () => {
				const {getByText} = result;

				expect(
					getByText('successfully-received-translations')
				).toBeInTheDocument();
			});
		});
	});

	describe('given an error server response', () => {
		beforeEach(() => {
			fetch.mockResponseOnce(
				JSON.stringify({
					error: {
						message: 'mocked error',
					},
				})
			);
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('when the user clicks on the auto-translate field button', () => {
			it('renders an error message', async () => {
				const {getByText} = renderComponent(baseProps);

				const autoTranslateFieldButton = getByText(
					`auto-translate-${baseProps.infoFieldSetEntries[0].fields[0].label}-field`
				).closest('button');

				await act(async () => {
					fireEvent.click(autoTranslateFieldButton);
				});

				expect(getByText('mocked error')).toBeInTheDocument();
			});
		});

		describe('when the user clicks on the auto-translate general button', () => {
			it('renders an error message', async () => {
				const {getByText} = renderComponent(baseProps);

				const autoTranslateFieldButton = getByText('auto-translate');

				await act(async () => {
					fireEvent.click(autoTranslateFieldButton);
				});

				expect(getByText('mocked error')).toBeInTheDocument();
			});
		});
	});

	it('renders with auto-translate disabled', () => {
		const {asFragment} = renderComponent({
			...baseProps,
			autoTranslateEnabled: false,
		});

		expect(asFragment()).toMatchSnapshot();
	});
});
