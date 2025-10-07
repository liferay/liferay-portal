/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import {PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import Paragraph from '../../../src/main/resources/META-INF/resources/js/Paragraph/Paragraph.es';

const spritemap = 'icons.svg';

const defaultParagraphConfig = {
	name: 'textField',
	spritemap,
};

const ParagraphWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<Paragraph {...props} />
	</PageProvider>
);

describe('Field Paragraph', () => {

	// eslint-disable-next-line no-console
	const originalWarn = console.warn;

	afterAll(() => {

		// eslint-disable-next-line no-console
		console.warn = originalWarn;
	});

	afterEach(cleanup);

	beforeAll(() => {

		// eslint-disable-next-line no-console
		console.warn = (...args) => {
			if (/DataProvider: Trying/.test(args[0])) {
				return;
			}
			originalWarn.call(console, ...args);
		};
	});

	beforeEach(() => {
		jest.useFakeTimers();
		fetch.mockResponseOnce(JSON.stringify({}));
	});

	it('has a key', () => {
		const {container} = render(
			<ParagraphWithProvider {...defaultParagraphConfig} key="key" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a label', () => {
		const {container} = render(
			<ParagraphWithProvider {...defaultParagraphConfig} label="label" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a placeholder', () => {
		const {container} = render(
			<ParagraphWithProvider
				{...defaultParagraphConfig}
				placeholder="Placeholder"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a value', () => {
		const {container} = render(
			<ParagraphWithProvider {...defaultParagraphConfig} value="value" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has an id', () => {
		const {container} = render(
			<ParagraphWithProvider {...defaultParagraphConfig} id="Id" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is not required', () => {
		const {container} = render(
			<ParagraphWithProvider
				{...defaultParagraphConfig}
				required={false}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is readOnly', () => {
		const {container} = render(
			<ParagraphWithProvider {...defaultParagraphConfig} readOnly />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders Label if showLabel is true', () => {
		const {container} = render(
			<ParagraphWithProvider
				{...defaultParagraphConfig}
				label="text"
				showLabel
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});
});
