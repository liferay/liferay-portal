/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {screen} from '@testing-library/dom';
import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import CheckboxMultiple from '../../../src/main/resources/META-INF/resources/js/CheckboxMultiple/CheckboxMultiple.es';

const CheckboxMultipleWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<CheckboxMultiple {...props} />
	</PageProvider>
);

describe('Field Checkbox Multiple', () => {

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
	});

	beforeEach(() => {
		jest.useFakeTimers();
		fetch.mockResponseOnce(JSON.stringify({}));
	});

	afterAll(() => {

		// eslint-disable-next-line no-console
		console.warn = originalWarn;
	});

	afterEach(cleanup);

	it('appends id to field-feedback element id', () => {
		const {container} = render(
			<CheckboxMultipleWithProvider id="CheckboxMultipleId" />
		);

		expect(
			container.querySelector('#CheckboxMultipleId_fieldFeedback')
		).toBeInTheDocument();
	});

	it('applies the predefined value', () => {
		render(
			<CheckboxMultipleWithProvider
				options={[
					{
						label: 'Option1',
						value: 'Option1',
					},
					{
						label: 'Option2',
						value: 'Option2',
					},
				]}
				predefinedValue={['Option2']}
			/>
		);

		expect(screen.getByLabelText('Option1')).not.toBeChecked();
		expect(screen.getByLabelText('Option2')).toBeChecked();
	});

	it('call the onChange callback on the field change', () => {
		const handleFieldEdited = jest.fn();

		const {container} = render(
			<CheckboxMultipleWithProvider onChange={handleFieldEdited} />
		);

		userEvent.click(container.querySelector('input'));

		expect(handleFieldEdited).toHaveBeenCalled();
	});

	it('checks the predefinedValue if there is no value', () => {
		const {getByLabelText} = render(
			<CheckboxMultipleWithProvider
				options={[
					{
						label: 'Option 1',
						value: 'option1',
					},
					{
						label: 'Option 2',
						value: 'option2',
					},
					{
						label: 'Option 3',
						value: 'option3',
					},
				]}
				predefinedValue={['option1', 'option2']}
				value={[]}
			/>
		);

		expect(getByLabelText('Option 1')).toBeChecked();
		expect(getByLabelText('Option 2')).toBeChecked();
		expect(getByLabelText('Option 3')).not.toBeChecked();
	});

	it('does not have aria-invalid attribute on first render when it is required', () => {
		const {container} = render(<CheckboxMultipleWithProvider required />);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		const {container} = render(
			<CheckboxMultipleWithProvider
				options={[
					{
						label: 'Option 1',
						value: 'option1',
					},
					{
						label: 'Option 2',
						value: 'option2',
					},
					{
						label: 'Option 3',
						value: 'option3',
					},
				]}
				required={true}
				value={['option1', 'option2']}
			/>
		);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not render field label if showLabel is false', () => {
		render(
			<CheckboxMultipleWithProvider
				label="CheckboxMultipleLabel"
				showLabel={false}
			/>
		);

		const labelElements = screen.getAllByText('CheckboxMultipleLabel');

		expect(labelElements.length).toBe(1);
		expect(labelElements[0]).toHaveClass('sr-only');
	});

	it('has a helptext', () => {
		render(<CheckboxMultipleWithProvider tip="Help Text Content" />);

		const helpTextElements = screen.getAllByText('Help Text Content');

		expect(helpTextElements[0]).toBeVisible();
		expect(helpTextElements[1]).toHaveClass('sr-only');
	});

	it('has a value', () => {
		const {container} = render(
			<CheckboxMultipleWithProvider value={['Option1Value']} />
		);

		const hiddenInputElement = container.querySelector(
			'input[type="hidden"]'
		);

		expect(hiddenInputElement).toHaveAttribute('value', 'Option1Value');
	});

	it('is not editable', () => {
		render(
			<CheckboxMultipleWithProvider
				options={[
					{
						label: 'readOnlyOption',
						value: 'readOnlyOption',
					},
				]}
				readOnly={true}
			/>
		);

		expect(screen.getByLabelText('readOnlyOption')).toBeDisabled();
	});

	it('is not required', () => {
		const {container} = render(
			<CheckboxMultipleWithProvider
				label="CheckboxMultipleLabel"
				required={false}
			/>
		);

		expect(
			container.querySelector('.lexicon-icon.lexicon-icon-asterisk')
		).not.toBeInTheDocument();
	});

	it('is shown as a switcher', () => {
		const {container} = render(
			<CheckboxMultipleWithProvider showAsSwitcher />
		);

		expect(container.querySelector('input[role="switch"]')).toBeVisible();
	});

	it('is shown as checkbox', () => {
		const {container} = render(
			<CheckboxMultipleWithProvider showAsSwitcher={false} />
		);

		const checkboxElement = container.querySelector(
			'input[type="checkbox"]'
		);

		expect(checkboxElement).toBeVisible();
		expect(checkboxElement).not.toHaveAttribute('role', 'switch');
	});

	it('renders data-option-reference attribute regardless of the element being a switcher', () => {
		const otherProps = {
			options: [
				{
					label: 'Option 1',
					reference: 'option1Reference',
					value: 'option1',
				},
				{
					label: 'Option 2',
					reference: 'option2Reference',
					value: 'option2',
				},
			],
			predefinedValue: ['option1', 'option2'],
			value: [],
		};

		const allProps = [
			{showAsSwitcher: false, ...otherProps},
			{showAsSwitcher: true, ...otherProps},
		];

		allProps.forEach((props) => {
			const {container} = render(
				<CheckboxMultipleWithProvider {...props} />
			);

			const checkboxInputElement1 = container.querySelector(
				`input[value][type="checkbox"][data-option-reference="option1Reference"]`
			);

			const checkboxInputElement2 = container.querySelector(
				`input[value][type="checkbox"][data-option-reference="option2Reference"]`
			);

			expect(checkboxInputElement1).toBeTruthy();
			expect(checkboxInputElement2).toBeTruthy();
		});
	});

	it('renders field label if showLabel is true', () => {
		render(
			<CheckboxMultipleWithProvider
				label="CheckboxMultipleLabel"
				showLabel
			/>
		);

		const labelElements = screen.getAllByText('CheckboxMultipleLabel');

		expect(labelElements.length).toBe(2);
		expect(labelElements[0]).toBeVisible();
		expect(labelElements[1]).toHaveClass('sr-only');
	});

	it('uncheck all values if the user has edited the field to clear the predefinedValue', () => {
		const {getByLabelText} = render(
			<CheckboxMultipleWithProvider
				localizedValueEdited={{en_US: true}}
				options={[
					{
						label: 'Option 1',
						value: 'option1',
					},
					{
						label: 'Option 2',
						value: 'option2',
					},
					{
						label: 'Option 3',
						value: 'option3',
					},
				]}
				predefinedValue={['option1', 'option2']}
				value={[]}
			/>
		);

		expect(getByLabelText('Option 1')).not.toBeChecked();
		expect(getByLabelText('Option 2')).not.toBeChecked();
		expect(getByLabelText('Option 3')).not.toBeChecked();
	});

	it('uses value over predefinedValue if there is a value', () => {
		const {container, getByLabelText} = render(
			<CheckboxMultipleWithProvider
				options={[
					{
						label: 'Option 1',
						value: 'option1',
					},
					{
						label: 'Option 2',
						value: 'option2',
					},
					{
						label: 'Option 3',
						value: 'option3',
					},
				]}
				predefinedValue={['option1', 'option2']}
				value={['option3']}
			/>
		);

		expect(getByLabelText('Option 1')).not.toBeChecked();
		expect(getByLabelText('Option 2')).not.toBeChecked();
		expect(getByLabelText('Option 3')).toBeChecked();

		const hiddenInput = container.querySelector('input[type="hidden"]');

		expect(hiddenInput).toHaveAttribute('value', 'option3');
	});
});

describe('Smoke test', () => {
	test('field Checkbox Multiple renders the expected structure with default props', () => {
		const {container} = render(
			<CheckboxMultipleWithProvider
				accessibleProps={{'aria-required': false}}
				disabled={false}
				inline={false}
				isSwitcher={false}
				localizedValueEdited={false}
				name="namePropertyValue"
				options={[
					{
						label: 'Option1',
						reference: 'Option1Reference',
						value: 'Option1Value',
					},
					{
						label: 'Option2',
						reference: 'Option2Reference',
						value: 'Option2Value',
					},
				]}
				predefinedValue={[]}
				value={[]}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
