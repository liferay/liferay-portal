/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, cleanup, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {PageProvider} from 'data-engine-js-components-web';
import React from 'react';

import Text from '../../../src/main/resources/META-INF/resources/js/Text/Text.es';

const globalLanguageDirection = Liferay.Language.direction;

const spritemap = 'icons.svg';

const defaultTextConfig = {
	name: 'textField',
	spritemap,
};

const TextWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<Text {...props} />
	</PageProvider>
);

describe('Field Text', () => {

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

	describe('Confirmation Field', () => {
		it('does not show the confirmation field', () => {
			render(<TextWithProvider {...defaultTextConfig} />);

			const confirmationField = document.getElementById(
				'textFieldconfirmationField_fieldDetails'
			);

			expect(confirmationField).toBeNull();
		});

		it('shows the confirmation field if the requireConfirmation property is enabled', () => {
			const {container} = render(
				<TextWithProvider
					{...defaultTextConfig}
					direction="horizontal"
					requireConfirmation={true}
				/>
			);

			const confirmationField = document.getElementById(
				'textFieldconfirmationField'
			);

			expect(confirmationField).not.toBeNull();

			expect(container.firstChild).toHaveClass('row');

			expect(
				container.firstChild.querySelector('.col-md-6')
			).not.toBeNull();
		});

		it('shows the confirmation field in vertical mode', () => {
			const {container} = render(
				<TextWithProvider
					{...defaultTextConfig}
					direction="vertical"
					requireConfirmation={true}
				/>
			);

			expect(
				container.firstChild.querySelector('.col-md-12')
			).not.toBeNull();
		});
	});

	it('does not have aria-invalid attribute on first render when it is required', () => {
		const {container} = render(
			<TextWithProvider {...defaultTextConfig} required={true} />
		);

		const textInputTag = container.querySelector('.ddm-field-text');

		expect(textInputTag.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		const {container} = render(
			<TextWithProvider
				{...defaultTextConfig}
				required={true}
				value="test"
			/>
		);

		const textInputTag = container.querySelector('.ddm-field-text');

		expect(textInputTag.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not render a counter when show counter is false, there is a maxLength and value is different from empty', () => {
		const {queryByText} = render(
			<TextWithProvider
				{...defaultTextConfig}
				maxLength={10}
				value="test"
			/>
		);

		expect(queryByText('4/10 characters')).not.toBeInTheDocument();
	});

	it('does not render html autocomplete attribute', () => {
		const {container} = render(<TextWithProvider {...defaultTextConfig} />);

		act(() => {
			jest.runAllTimers();
		});

		const textInputTag = container.querySelector('.ddm-field-text');

		expect(textInputTag.hasAttribute('autocomplete')).toBe(false);
	});

	it('emits a field edit with correct parameters', () => {
		const onChange = jest.fn();

		const {container} = render(
			<TextWithProvider
				{...defaultTextConfig}
				key="input"
				onChange={onChange}
			/>
		);

		const input = container.querySelector('input');

		fireEvent.change(input, {
			target: {
				value: 'test',
			},
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(onChange).toHaveBeenCalled();
	});

	it('has a helptext', () => {
		const {container} = render(
			<TextWithProvider {...defaultTextConfig} tip="Type something" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a label', () => {
		const {container} = render(
			<TextWithProvider {...defaultTextConfig} label="label" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a placeholder', () => {
		const {container} = render(
			<TextWithProvider
				{...defaultTextConfig}
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
			<TextWithProvider {...defaultTextConfig} value="value" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has an id', () => {
		const {container} = render(
			<TextWithProvider {...defaultTextConfig} id="Id" />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('hides autocomplete dropdown menu when container layout is hidden', () => {
		const props = {
			autocomplete: true,
			options: [
				{label: 'Option 1', value: 'Option1'},
				{label: 'Option 2', value: 'Option2'},
			],
			value: 'Option',
			...defaultTextConfig,
		};

		render(
			<div className="ddm-page-container-layout hide">
				<TextWithProvider {...props} />
			</div>
		);

		act(() => {
			jest.runAllTimers();
		});

		const autocompleteDropdownMenu = document.body.querySelector(
			'.autocomplete-dropdown-menu'
		);

		const classList = autocompleteDropdownMenu.classList;

		expect(classList.contains('show')).toBeFalsy();
	});

	it('hides autocomplete dropdown menu when focus is changed', () => {
		const onChange = jest.fn();

		const props = {
			autocomplete: true,
			options: [
				{label: 'Option 1', value: 'Option1'},
				{label: 'Option 2', value: 'Option2'},
			],
			value: '',
			...defaultTextConfig,
		};

		const {container} = render(
			<div className="ddm-page-container-layout">
				<TextWithProvider {...props} key="input" onChange={onChange} />
			</div>
		);

		const input = container.querySelector('input');

		fireEvent.change(input, {
			target: {
				value: 'Option',
			},
		});

		act(() => {
			jest.runAllTimers();
		});

		const autocompleteDropdownMenu = document.querySelector(
			'.autocomplete-dropdown-menu'
		);

		expect(
			autocompleteDropdownMenu.classList.contains('show')
		).toBeTruthy();

		const body = document.body;

		userEvent.click(body);

		act(() => {
			jest.runAllTimers();
		});

		expect(autocompleteDropdownMenu.classList.contains('show')).toBeFalsy();
	});

	it('hides autocomplete dropdown menu when input is empty', () => {
		const onChange = jest.fn();

		const props = {
			autocomplete: true,
			options: [
				{label: 'Option 1', value: 'Option1'},
				{label: 'Option 2', value: 'Option2'},
			],
			value: 'Option',
			...defaultTextConfig,
		};

		const {container} = render(
			<div className="ddm-page-container-layout">
				<TextWithProvider {...props} key="input" onChange={onChange} />
			</div>
		);

		const input = container.querySelector('input');

		fireEvent.change(input, {
			target: {
				value: '',
			},
		});

		act(() => {
			jest.runAllTimers();
		});

		const autocompleteDropdownMenu = document.querySelector(
			'.autocomplete-dropdown-menu'
		);

		expect(autocompleteDropdownMenu.classList.contains('show')).toBeFalsy();
	});

	it('is not readOnly', () => {
		const {container} = render(
			<TextWithProvider {...defaultTextConfig} readOnly={false} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is not required', () => {
		const {container} = render(
			<TextWithProvider {...defaultTextConfig} required={false} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is readOnly', () => {
		const {container} = render(
			<TextWithProvider {...defaultTextConfig} readOnly={true} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('normalizes the field if it contains invalid characters', () => {
		const onChange = jest.fn();

		const {container} = render(
			<TextWithProvider
				{...defaultTextConfig}
				key="input"
				normalizeField={true}
				onChange={onChange}
			/>
		);

		const input = container.querySelector('input');

		fireEvent.change(input, {
			target: {
				value: 'Field¿êReference',
			},
		});

		expect(input.value).toEqual('FieldReference');
	});

	it('normalizes the value of the field if it contains invalid characters', () => {
		const onChange = jest.fn();

		const {container} = render(
			<TextWithProvider
				{...defaultTextConfig}
				invalidCharacters="[1-8]"
				key="input"
				onChange={onChange}
			/>
		);

		const input = container.querySelector('input');

		fireEvent.change(input, {
			target: {
				value: '+9 (129) 993-9999',
			},
		});

		expect(input.value).toEqual('+9 (9) 99-9999');
	});

	it('renders a counter when show counter is true, there is a maxLength and value length is greater than the maximum length', () => {
		render(
			<TextWithProvider
				{...defaultTextConfig}
				maxLength={2}
				showCounter={true}
				valid={true}
				value="test"
			/>
		);
		const error = screen.queryAllByText('4/2 characters')[0];
		expect(error).toHaveClass('form-feedback-item');
	});

	it('renders a counter when show counter is true, there is a maxLength and value is empty', () => {
		const {getByText} = render(
			<TextWithProvider
				{...defaultTextConfig}
				maxLength={10}
				showCounter={true}
				valid={true}
				value=""
			/>
		);

		expect(getByText('0/10 characters')).toBeInTheDocument();
	});

	it('renders a counter when show counter is true, there is a maxLength and value is different from empty', () => {
		const {getByText} = render(
			<TextWithProvider
				{...defaultTextConfig}
				maxLength={10}
				showCounter={true}
				valid={true}
				value="test"
			/>
		);

		expect(getByText('4/10 characters')).toBeInTheDocument();
	});

	it('renders autocomplete dropdown menu', () => {
		const onChange = jest.fn();

		const props = {
			autocomplete: true,
			options: [
				{label: 'Option 1', value: 'Option1'},
				{label: 'Option 2', value: 'Option2'},
			],
			value: '',
			...defaultTextConfig,
		};

		const {container} = render(
			<div className="ddm-page-container-layout">
				<TextWithProvider {...props} key="input" onChange={onChange} />
			</div>
		);

		const input = container.querySelector('input');

		fireEvent.change(input, {
			target: {
				value: 'Option',
			},
		});

		act(() => {
			jest.runAllTimers();
		});

		const autocompleteDropdownMenu = document.querySelector(
			'.autocomplete-dropdown-menu'
		);

		expect(
			autocompleteDropdownMenu.classList.contains('show')
		).toBeTruthy();
	});

	it('renders html autocomplete attribute', () => {
		const {container} = render(
			<TextWithProvider
				{...defaultTextConfig}
				htmlAutocompleteAttribute="name"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const textInputTag = container.querySelector('.ddm-field-text');

		expect(textInputTag.getAttribute('autocomplete')).toBe('name');
	});

	it('renders label if showLabel is true', () => {
		const {container} = render(
			<TextWithProvider {...defaultTextConfig} label="text" showLabel />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});
});
