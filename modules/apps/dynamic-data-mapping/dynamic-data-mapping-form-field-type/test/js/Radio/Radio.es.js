/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import {PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import Radio from '../../../src/main/resources/META-INF/resources/js/Radio/Radio.es';

const spritemap = 'icons.svg';

const defaultRadioConfig = {
	name: 'radioField',
	spritemap,
};

const RadioWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<Radio {...props} />
	</PageProvider>
);

describe('Field Radio', () => {

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

	it('does not have aria-invalid attribute on first render when it is required', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} required={true} />
		);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		const {container} = render(
			<RadioWithProvider
				{...defaultRadioConfig}
				required={true}
				value="value"
			/>
		);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('has a helptext', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} tip="Type something" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a label', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} label="label" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a placeholder', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} placeholder="Option 1" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a value', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} value="value" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has an id', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} id="Id" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is not editable', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} readOnly />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is not required', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} required={false} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders Label if showLabel is true', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} label="text" showLabel />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders no options when options is empty', () => {
		const {container} = render(
			<RadioWithProvider {...defaultRadioConfig} options={[]} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders option elements with data-option-reference attribute', () => {
		const {container} = render(
			<RadioWithProvider
				{...defaultRadioConfig}
				options={[
					{
						checked: false,
						disabled: false,
						id: 'id',
						inline: false,
						label: 'label',
						name: 'name',
						reference: 'option1Reference',
						showLabel: true,
						value: 'item',
					},
					{
						checked: false,
						disabled: false,
						id: 'id',
						inline: false,
						label: 'label2',
						name: 'name',
						reference: 'option2Reference',
						showLabel: true,
						value: 'item2',
					},
				]}
			/>
		);

		const radioInputElement1 = container.querySelector(
			`input[value][type="radio"][data-option-reference="option1Reference"]`
		);

		const radioInputElement2 = container.querySelector(
			`input[value][type="radio"][data-option-reference="option2Reference"]`
		);

		expect(radioInputElement1).toBeTruthy();
		expect(radioInputElement2).toBeTruthy();
	});

	it('renders options', () => {
		const {container} = render(
			<RadioWithProvider
				{...defaultRadioConfig}
				options={[
					{
						checked: false,
						disabled: false,
						id: 'id',
						inline: false,
						label: 'label',
						name: 'name',
						showLabel: true,
						value: 'item',
					},
					{
						checked: false,
						disabled: false,
						id: 'id',
						inline: false,
						label: 'label2',
						name: 'name',
						showLabel: true,
						value: 'item2',
					},
				]}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});
});
