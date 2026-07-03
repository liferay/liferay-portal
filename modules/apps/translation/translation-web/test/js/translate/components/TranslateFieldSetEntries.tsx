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

	const normalize = (value: string) =>
		value === '' || value.startsWith('<') ? value : `<p>${value}</p>`;

	return {
		CKEditor5ClassicEditor: ({data, onReady}: any) => {
			const wrapperRef = React.useRef(null);
			const readyRef = React.useRef(false);
			const stateRef = React.useRef({dataFromRoots: '', model: ''});

			const getArea = () =>
				wrapperRef.current.querySelector('.ck-source-editing-area');

			const updateEditorData = () => {
				const editorState = stateRef.current;

				const newData = getArea().dataset.value;

				if (editorState.dataFromRoots !== newData) {
					editorState.model = normalize(newData);
					editorState.dataFromRoots = newData;
				}
			};

			const getData = () => {
				updateEditorData();

				return stateRef.current.model;
			};

			React.useEffect(() => {
				const area = getArea();
				const textarea = area.querySelector('textarea');
				const editorState = stateRef.current;

				area.dataset.value = data;
				textarea.value = data;
				editorState.dataFromRoots = data;
				editorState.model = data;

				textarea.addEventListener('input', () => {
					area.dataset.value = textarea.value;
				});

				onReady({
					getData,
					plugins: {
						get: () => ({
							isSourceEditingMode: true,
							on: (event: string, callback: () => void) => {
								if (event === 'change:isSourceEditingMode') {
									callback();
								}
							},
							updateEditorData,
						}),
					},
					ui: {
						element: wrapperRef.current,
					},
				});

				readyRef.current = true;

				// eslint-disable-next-line react-hooks/exhaustive-deps
			}, []);

			React.useEffect(() => {
				if (readyRef.current && getData() !== data) {
					stateRef.current.model = data;
				}

				// eslint-disable-next-line react-hooks/exhaustive-deps
			}, [data]);

			return (
				<div ref={wrapperRef}>
					<div className="ck-source-editing-area">
						<textarea aria-label="source" />
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

const SourceEditingHarness = ({initialContent}: {initialContent: string}) => {
	const [content, setContent] = React.useState(initialContent);

	return (
		<>
			<button onClick={() => setContent(TRANSLATED_TARGET)} type="button">
				translate
			</button>

			<TranslateFieldSetEntries
				autoTranslateEnabled={false}
				fetchAutoTranslateField={() => {}}
				infoFieldSetEntries={infoFieldSetEntries}
				onChange={({content: newContent}) => setContent(newContent)}
				portletNamespace="_mock_TranslationPortlet_"
				targetFieldsContent={{
					[ID]: {content, message: '', status: ''},
				}}
			/>
		</>
	);
};

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

	it('keeps populating the source view after repeated remove-and-translate cycles', () => {
		render(<SourceEditingHarness initialContent={ORIGINAL_TARGET} />);

		const textarea = screen.getByLabelText('source');

		for (let cycle = 0; cycle < 3; cycle++) {
			fireEvent.click(screen.getByText('translate'));

			expect(textarea).toHaveValue(TRANSLATED_TARGET);

			fireEvent.input(textarea, {target: {value: ''}});

			expect(textarea).toHaveValue('');
		}
	});
});
