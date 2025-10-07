/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import {PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import OptionFieldKeyValue from '../../../src/main/resources/META-INF/resources/js/OptionFieldKeyValue/OptionFieldKeyValue';

const globalLanguageDirection = Liferay.Language.direction;

const spritemap = 'icons.svg';

const OptionFieldKeyValueWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<OptionFieldKeyValue {...props} />
	</PageProvider>
);

describe('OptionFieldKeyValue', () => {

	// eslint-disable-next-line no-console
	const originalWarn = console.warn;

	beforeAll(() => {

		// eslint-disable-next-line no-console
		console.warn = (...args) => {
			if (/DataProvider: Trying/.test(args[0])) {
				return;
			}
			originalWarn.call(console, ...args);
		};

		Liferay.Language.direction = {
			en_US: 'rtl',
		};
	});

	afterAll(() => {

		// eslint-disable-next-line no-console
		console.warn = originalWarn;

		Liferay.Language.direction = globalLanguageDirection;
	});

	afterEach(cleanup);

	beforeEach(() => {
		jest.useFakeTimers();
		fetch.mockResponseOnce(JSON.stringify({}));
	});

	it('has a helptext', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				name="OptionFieldKeyValue"
				spritemap={spritemap}
				tip="Type something"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a label', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				label="label"
				name="OptionFieldKeyValue"
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a predefined Value', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				name="OptionFieldKeyValue"
				placeholder="Option 1"
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a value', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				name="OptionFieldKeyValue"
				spritemap={spritemap}
				value="value"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has an id', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				id="Id"
				name="OptionFieldKeyValue"
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('hides keyword input', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				name="OptionFieldKeyValue"
				readOnly={true}
				spritemap={spritemap}
			/>
		);

		const OptionFieldKeyValueInput =
			container.querySelectorAll('.key-value-input');

		expect(OptionFieldKeyValueInput.length).toBe(0);
	});

	it('is not editable', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				name="OptionFieldKeyValue"
				readOnly={true}
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is not required', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				name="OptionFieldKeyValue"
				required={false}
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders component with a key', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				keyword="key"
				name="OptionFieldKeyValue"
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders Label if showLabel is true', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				label="text"
				name="OptionFieldKeyValue"
				showLabel={true}
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('shows keyword input', () => {
		const {container} = render(
			<OptionFieldKeyValueWithProvider
				name="OptionFieldKeyValue"
				readOnly={true}
				showKeyword={true}
				spritemap={spritemap}
			/>
		);

		const OptionFieldKeyValueInput = container.querySelectorAll(
			'[id*="keyValueName"]:not([id*="_fieldFeedback"])'
		);

		expect(OptionFieldKeyValueInput.length).toBe(1);
	});
});
