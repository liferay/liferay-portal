/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import ColorPicker from '../../../src/main/resources/META-INF/resources/js/ColorPicker/ColorPicker.es';

const name = 'colorPicker';
const spritemap = 'icons.svg';

const ColorPickerWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<ColorPicker {...props} />
	</PageProvider>
);

describe('Field Color Picker', () => {

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

	it.skip('should call the onChange callback on the field change', () => {
		const handleFieldEdited = jest.fn();

		render(
			<ColorPickerWithProvider
				name={name}
				onChange={handleFieldEdited}
				spritemap={spritemap}
			/>
		);

		userEvent.click(document.body.querySelector('input'), {
			target: {value: 'ffffff'},
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(handleFieldEdited).toHaveBeenCalled();

		const inputEl = document.body.querySelector('input');
		expect(inputEl.value).toBe('ffffff');
	});

	it('does not have aria-invalid attribute on first render when it is required', () => {
		const {container} = render(<ColorPickerWithProvider required={true} />);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		const color = '#FF67AA';

		const {container} = render(
			<ColorPickerWithProvider required={true} value={color} />
		);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('renders field disabled', () => {
		const {container} = render(
			<ColorPickerWithProvider
				name={name}
				readOnly={true}
				spritemap={spritemap}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders field with helptext', () => {
		const {container} = render(
			<ColorPickerWithProvider
				name={name}
				spritemap={spritemap}
				tip="Helptext"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders field with label', () => {
		const {container} = render(
			<ColorPickerWithProvider
				label="label"
				name={name}
				spritemap={spritemap}
				tip="Helptext"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders with basic color', () => {
		const color = '#FF67AA';

		const {container} = render(
			<ColorPickerWithProvider
				name={name}
				readOnly
				spritemap={spritemap}
				value={color}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container.querySelector('input').value).toBe(color);
	});
});
