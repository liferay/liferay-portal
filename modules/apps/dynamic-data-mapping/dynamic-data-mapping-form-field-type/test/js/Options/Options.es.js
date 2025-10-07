/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import Options from '../../../src/main/resources/META-INF/resources/js/Options/Options.es';

const DEFAULT_OPTION_NAME_REGEX = /^Option[0-9]{1,}$/;

const globalLanguageDirection = Liferay.Language.direction;

const spritemap = 'icons.svg';

const OptionsWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: themeDisplay.getLanguageId()}}>
		<Options {...props} />
	</PageProvider>
);

const optionsValue = {
	[themeDisplay.getLanguageId()]: [
		{
			id: 'option1',
			label: 'Option 1',
			reference: 'Option1',
			value: 'Option1',
		},
		{
			id: 'option2',
			label: 'Option 2',
			reference: 'Option2',
			value: 'Option2',
		},
	],
};

describe('Options', () => {

	// eslint-disable-next-line no-console
	const originalWarn = console.warn;

	afterAll(() => {

		// eslint-disable-next-line no-console
		console.warn = originalWarn;

		Liferay.Language.direction = globalLanguageDirection;
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

		Liferay.Language.direction = {
			en_US: 'rtl',
		};
	});

	beforeEach(() => {
		jest.useFakeTimers();
		fetch.mockResponseOnce(JSON.stringify({}));
	});

	describe('Normalize option reference during the onBlur event', () => {
		it('changes to the option value when the reference is duplicated', () => {
			const {getAllByRole} = render(
				<OptionsWithProvider
					name="options"
					onChange={jest.fn()}
					spritemap={spritemap}
					value={{
						[themeDisplay.getLanguageId()]: [
							{
								id: 'option1',
								label: 'Option 1',
								reference: 'Reference1',
								value: 'Option1',
							},
							{
								id: 'option2',
								label: 'Option 2',
								reference: 'Reference2',
								value: 'Option2',
							},
						],
					}}
				/>
			);

			const textboxes = getAllByRole('textbox');

			const referenceInputs = textboxes.filter((element) =>
				element.id.includes('keyValueReference')
			);

			expect(referenceInputs[0].value).toBe('Reference1');
			expect(referenceInputs[1].value).toBe('Reference2');

			fireEvent.input(referenceInputs[0], {
				target: {value: 'Reference2'},
			});

			fireEvent.blur(referenceInputs[0]);

			act(() => {
				jest.runAllTimers();
			});

			expect(referenceInputs[0].value).toBe('Option1');
			expect(referenceInputs[1].value).toBe('Reference2');
		});

		it('changes to the option value when the reference is empty', () => {
			const {getAllByRole} = render(
				<OptionsWithProvider
					name="options"
					onChange={jest.fn()}
					spritemap={spritemap}
					value={{
						[themeDisplay.getLanguageId()]: [
							{
								id: 'id',
								label: 'Label',
								reference: 'Reference',
								value: 'Value',
							},
						],
					}}
				/>
			);

			const textboxes = getAllByRole('textbox');

			const referenceInputs = textboxes.filter((element) =>
				element.id.includes('keyValueReference')
			);

			expect(referenceInputs[0].value).toBe('Reference');

			fireEvent.input(referenceInputs[0], {target: {value: ''}});

			fireEvent.blur(referenceInputs[0]);

			act(() => {
				jest.runAllTimers();
			});

			expect(referenceInputs[0].value).toEqual('Value');
		});
	});

	it.skip('adds a value to the value property when the label is empty', () => {
		const {getAllByRole} = render(
			<OptionsWithProvider
				generateOptionValueUsingOptionLabel={true}
				name="options"
				onChange={jest.fn()}
				showKeyword={true}
				spritemap={spritemap}
				value={{
					[themeDisplay.getLanguageId()]: [
						{
							id: 'bar',
							label: 'Display Name',
							reference: 'Reference',
							value: 'Name',
						},
					],
				}}
			/>
		);

		const textboxes = getAllByRole('textbox');

		const labels = textboxes.filter((element) =>
			element.id.includes('keyValueDisplayName')
		);

		fireEvent.input(labels[0], {target: {value: ''}});

		const newTextboxes = getAllByRole('textbox');

		const values = newTextboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		expect(values[0].value).toBe('Reference');
	});

	it('checks if the initial value of the option reference matches the option value', () => {
		const {getAllByRole} = render(
			<OptionsWithProvider
				name="options"
				showKeyword={true}
				spritemap={spritemap}
				value={optionsValue}
			/>
		);

		const textboxes = getAllByRole('textbox');

		const referenceInputs = textboxes.filter((element) =>
			element.id.includes('keyValueReference')
		);

		expect(referenceInputs[1].value).toEqual(
			expect.stringMatching(DEFAULT_OPTION_NAME_REGEX)
		);

		const valueInputs = textboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		expect(referenceInputs[1].value).toBe(valueInputs[1].value);
	});

	it('deduplication of value happens when the user leaves the value field', () => {
		const {getAllByRole} = render(
			<OptionsWithProvider
				name="options"
				onChange={jest.fn()}
				showKeyword={true}
				spritemap={spritemap}
				value={{
					[themeDisplay.getLanguageId()]: [
						{
							id: 'bar',
							label: 'Bar',
							value: 'Bar',
						},
						{
							id: 'foo',
							label: 'Foo',
							value: 'Foo',
						},
					],
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const textboxes = getAllByRole('textbox');

		const optionNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		fireEvent.input(optionNameInputs[1], {target: {value: 'Bar'}});

		expect(optionNameInputs[0].value).toEqual(optionNameInputs[1].value);

		fireEvent.blur(optionNameInputs[1]);

		act(() => {
			jest.runAllTimers();
		});

		expect(optionNameInputs[0].value).not.toEqual(
			optionNameInputs[1].value
		);
	});

	it('does not changes the option value when the option label changes', () => {
		const {getAllByRole, getByDisplayValue} = render(
			<OptionsWithProvider
				name="options"
				onChange={jest.fn()}
				showKeyword={true}
				spritemap={spritemap}
				value={{
					[themeDisplay.getLanguageId()]: [
						{
							id: 'option1',
							label: 'Option 1',
							value: 'Option1',
						},
					],
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const textboxes = getAllByRole('textbox');

		const displayNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueDisplayName')
		);

		userEvent.type(getByDisplayValue('Option 1'), 'Option 2');

		expect(displayNameInputs[0].value).toEqual('Option 2');

		const optionNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		expect(optionNameInputs[0].value).toEqual('Option1');
	});

	it('does show an empty option when translating', () => {
		const {container} = render(
			<OptionsWithProvider
				defaultLanguageId={themeDisplay.getLanguageId()}
				editingLanguageId="pt_BR"
				name="options"
				onChange={jest.fn()}
				spritemap={spritemap}
				value={{
					[themeDisplay.getLanguageId()]: [
						{
							id: 'option',
							label: 'Option',
							value: 'Option',
						},
					],
					pt_BR: [
						{
							id: 'option',
							label: 'Option',
							value: 'Option',
						},
					],
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const labelInputs = container.querySelectorAll('.ddm-field-text');

		expect(labelInputs.length).toEqual(2);
	});

	it('edits the value of an option based on the label', () => {
		const {getAllByRole} = render(
			<OptionsWithProvider
				generateOptionValueUsingOptionLabel={true}
				name="options"
				onChange={jest.fn()}
				showKeyword={true}
				spritemap={spritemap}
				value={{
					[themeDisplay.getLanguageId()]: [
						{
							id: 'option',
							label: 'Option',
							value: 'Option',
						},
					],
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const textboxes = getAllByRole('textbox');

		const displayNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueDisplayName')
		);

		fireEvent.change(displayNameInputs[0], {
			target: {
				value: 'Hello',
			},
		});

		act(() => {
			jest.runAllTimers();
		});

		const optionNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		expect(optionNameInputs[0].value).toEqual('Hello');
	});

	it('new options are added with an unique value', () => {
		const {container, getAllByRole} = render(
			<OptionsWithProvider
				name="options"
				onChange={jest.fn()}
				showKeyword={true}
				spritemap={spritemap}
				value={{
					[themeDisplay.getLanguageId()]: [
						{
							id: 'option1',
							label: 'Option 1',
							value: 'Option1',
						},
					],
				}}
			/>
		);

		const addOptionButton = container.querySelector('.add-option-button');

		addOptionButton.click();

		act(() => {
			jest.runAllTimers();
		});

		const textboxes = getAllByRole('textbox');

		const optionNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		expect(optionNameInputs[0].value).not.toEqual(
			optionNameInputs[1].value
		);
	});

	it('removes an option when click on remove button', () => {
		const {container} = render(
			<OptionsWithProvider
				defaultLanguageId={themeDisplay.getLanguageId()}
				editingLanguageId="pt_BR"
				name="options"
				onChange={jest.fn()}
				spritemap={spritemap}
				value={{
					...optionsValue,
					pt_BR: [
						{
							id: 'option1',
							label: 'Option 1',
							reference: 'Option1',
							value: 'Option1',
						},
						{
							id: 'option2',
							label: 'Option 2',
							reference: 'Option2',
							value: 'Option2',
						},
					],
				}}
			/>
		);

		let options = container.querySelectorAll('.ddm-field-options');

		expect(options.length).toEqual(2);

		const removeOptionButton = document.querySelector(
			'.ddm-option-entry .close'
		);

		fireEvent.click(removeOptionButton);

		options = container.querySelectorAll('.ddm-field-options');

		expect(options.length).toEqual(1);
	});

	it('shows the options', () => {
		const {container, getAllByRole} = render(
			<OptionsWithProvider
				name="options"
				showKeyword={true}
				spritemap={spritemap}
				value={optionsValue}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const textboxes = getAllByRole('textbox');

		const referenceInputs = textboxes.filter((element) =>
			element.id.includes('keyValueReference')
		);

		expect(referenceInputs[1].value).toEqual(
			expect.stringMatching(DEFAULT_OPTION_NAME_REGEX)
		);

		referenceInputs[1].setAttribute('value', 'Any<String>');

		const optionNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		expect(optionNameInputs[1].value).toEqual(
			expect.stringMatching(DEFAULT_OPTION_NAME_REGEX)
		);

		optionNameInputs[1].setAttribute('value', 'Any<String>');

		expect(container).toMatchSnapshot();
	});

	it('shows the options with editable value', () => {
		const {getAllByRole, getByDisplayValue} = render(
			<OptionsWithProvider
				keywordReadOnly={false}
				name="options"
				onChange={jest.fn()}
				showKeyword={true}
				spritemap={spritemap}
				value={{
					[themeDisplay.getLanguageId()]: [
						{
							id: 'option1',
							label: 'Option 1',
							reference: 'Reference1',
							value: 'Option1',
						},
					],
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		userEvent.type(getByDisplayValue('Option1'), 'Option2');

		const textboxes = getAllByRole('textbox');

		const optionNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		expect(optionNameInputs[0].disabled).toBeFalsy();
		expect(optionNameInputs[0].value).toEqual('Option2');
	});

	it('shows the options with not editable value', () => {
		const {getAllByRole} = render(
			<OptionsWithProvider
				keywordReadOnly={true}
				name="options"
				showKeyword={true}
				spritemap={spritemap}
				value={{
					[themeDisplay.getLanguageId()]: [
						{
							id: 'option1',
							label: 'Option 1',
							value: 'Option1',
						},
					],
				}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const textboxes = getAllByRole('textbox');

		const optionNameInputs = textboxes.filter((element) =>
			element.id.includes('keyValueName')
		);

		expect(optionNameInputs[0].disabled).toBeTruthy();
		expect(optionNameInputs[0].value).toEqual('Option1');
	});
});
