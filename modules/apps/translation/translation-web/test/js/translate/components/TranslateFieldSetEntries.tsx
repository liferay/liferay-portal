/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import TranslateFieldSetEntries from '../../../../src/main/resources/META-INF/resources/js/translate/components/TranslateFieldSetEntries';

jest.mock('frontend-editor-ckeditor-web', () => {
	const React = require('react');

	return {
		CKEditor5ClassicEditor: ({data, onReady}: any) => {
			const wrapperRef = React.useRef(null);

			React.useEffect(() => {
				const wrapper = wrapperRef.current;

				const textarea = wrapper.querySelector('textarea');

				textarea.value = data;

				const sourceEditingPlugin = {
					isSourceEditingMode: true,
					on: (event: string, callback: () => void) => {
						if (event === 'change:isSourceEditingMode') {
							callback();
						}
					},
				};

				onReady({
					getData: () =>
						textarea.value.startsWith('<')
							? textarea.value
							: `<p>${textarea.value}</p>`,
					plugins: {
						get: () => sourceEditingPlugin,
					},
					ui: {
						element: wrapper,
					},
				});

				// eslint-disable-next-line react-hooks/exhaustive-deps
			}, []);

			return (
				<div ref={wrapperRef}>
					<div className="ck-source-editing-area">
						<textarea aria-label="source" defaultValue={data} />
					</div>
				</div>
			);
		},
		ClassicEditor: () => null,
	};
});

const ID = 'infoField--description--0';
const ORIGINAL_TARGET = '<p>contenido original</p>';
const TRANSLATED_TARGET = '<p>contenido traducido</p>';

const infoFieldSetEntries = [
	{
		fields: [
			{
				editorConfiguration: {editorConfig: {}},
				html: true,
				id: 'infoField--description--',
				label: 'Description',
				multiline: false,
				sourceContent: ['<p>original content</p>'],
				sourceContentDir: 'ltr',
				targetContentDir: 'ltr',
				targetLanguageId: 'es_ES',
			},
		],
		legend: 'Basic Information',
	},
];

const buildComponent = (content: string) => (
	<TranslateFieldSetEntries
		autoTranslateEnabled={false}
		fetchAutoTranslateField={() => {}}
		infoFieldSetEntries={infoFieldSetEntries}
		onChange={() => {}}
		portletNamespace="_mock_TranslationPortlet_"
		targetFieldsContent={{
			[ID]: {content, message: '', status: ''},
		}}
	/>
);

describe('TranslateFieldSetEntries', () => {
	beforeAll(() => {
		Liferay.FeatureFlags['LPD-11235'] = false;
	});

	afterAll(() => {
		delete Liferay.FeatureFlags['LPD-11235'];
	});

	it('refreshes the source editing textarea when a translation arrives while in source mode', () => {
		const {rerender} = render(buildComponent(ORIGINAL_TARGET));

		const textarea = screen.getByLabelText('source');

		expect(textarea).toHaveValue(ORIGINAL_TARGET);

		rerender(buildComponent(TRANSLATED_TARGET));

		expect(textarea).toHaveValue(TRANSLATED_TARGET);

		expect((textarea.parentElement as HTMLElement).dataset.value).toBe(
			TRANSLATED_TARGET
		);
	});

	it('does not reformat the source while the user is typing', () => {
		const {rerender} = render(buildComponent(ORIGINAL_TARGET));

		const textarea = screen.getByLabelText('source');

		fireEvent.input(textarea, {target: {value: 'hola mundo'}});

		rerender(buildComponent('<p>hola mundo</p>'));

		expect(textarea).toHaveValue('hola mundo');
	});

	it('reflects an auto-translation that arrives after the user edited the source', () => {
		const {rerender} = render(buildComponent(ORIGINAL_TARGET));

		const textarea = screen.getByLabelText('source');

		fireEvent.input(textarea, {target: {value: 'hola mundo'}});

		rerender(buildComponent(TRANSLATED_TARGET));

		expect(textarea).toHaveValue(TRANSLATED_TARGET);
	});
});
