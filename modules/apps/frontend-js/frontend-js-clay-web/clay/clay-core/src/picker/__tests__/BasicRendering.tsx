/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Icon from '@clayui/icon';
import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import {Option, Picker} from '../../';

describe('Picker basic rendering', () => {
	afterEach(cleanup);

	it('render static content', () => {
		render(
			<Picker>
				<Option key="apple">Apple</Option>

				<Option key="banana">Banana</Option>

				<Option key="blueberry">Blueberry</Option>
			</Picker>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('render dynamic content', () => {
		const {container} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		expect(container).toMatchSnapshot();
	});

	it('render dynamic content using native selector', () => {
		window.innerWidth = 600;

		const {container} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']} native>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		expect(container).toMatchSnapshot();
	});

	it('render static content using native selector', () => {
		window.innerWidth = 600;

		const {container} = render(
			<Picker native>
				<Option key="apple">Apple</Option>

				<Option key="banana">Banana</Option>

				<Option key="blueberry">Blueberry</Option>
			</Picker>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders component with selected by default using native selector', () => {
		window.innerWidth = 600;

		const {getByRole} = render(
			<Picker defaultSelectedKey="apple" native>
				<Option key="apple">Apple</Option>

				<Option key="banana">Banana</Option>

				<Option key="blueberry">Blueberry</Option>
			</Picker>
		);

		const combobox = getByRole('combobox') as HTMLSelectElement;

		expect(combobox.value).toBe('apple');
	});

	it('renders open component by default', () => {
		render(
			<Picker defaultActive items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('renders component with selected by default', () => {
		const {getByRole} = render(
			<Picker
				defaultSelectedKey="Apple"
				items={['Apple', 'Banana', 'Blueberry']}
			>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const selectedValue = getByRole('combobox');

		expect(selectedValue.innerHTML).toBe('Apple');
	});

	it('renders component with selected by default and open', () => {
		const {getAllByRole} = render(
			<Picker
				defaultActive
				defaultSelectedKey="Apple"
				items={['Apple', 'Banana', 'Blueberry']}
			>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const [apple] = getAllByRole('option');

		expect(apple!.getAttribute('aria-selected')).toBe('true');
		expect(apple!.textContent).toBe('Apple');
	});

	it('renders the component as disabled', () => {
		const {getByRole} = render(
			<Picker
				defaultSelectedKey="Banana"
				disabled
				items={['Apple', 'Banana', 'Blueberry']}
			>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const selectedValue = getByRole('combobox');

		expect(selectedValue.getAttribute('disabled')).toBe('');
		expect(selectedValue.textContent).toBe('Banana');
	});

	it('renders component with custom placeholder', () => {
		const {getByRole} = render(
			<Picker
				items={['Apple', 'Banana', 'Blueberry']}
				placeholder="Select a fruit"
			>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const selectedValue = getByRole('combobox');

		expect(selectedValue.textContent).toBe('Select a fruit');
	});

	it('render component with label id', () => {
		const {getByRole} = render(
			<>
				<label htmlFor="picker" id="picker-label">
					Choose a fruit
				</label>
				<Picker
					aria-labelledby="picker-label"
					id="picker"
					items={['Apple', 'Banana', 'Blueberry']}
					placeholder="Select a fruit"
				>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			</>
		);

		const selectedValue = getByRole('combobox') as HTMLButtonElement;
		const label = selectedValue.labels[0];

		expect(selectedValue.getAttribute('id')).toBe('picker');
		expect(selectedValue.getAttribute('aria-labelledby')).toBe(
			'picker-label'
		);
		expect(label!.getAttribute('id')).toBe('picker-label');
		expect(label!.getAttribute('for')).toBe('picker');
	});

	it('render component with custom trigger', () => {
		const Trigger = React.forwardRef<
			HTMLDivElement,
			React.HTMLAttributes<HTMLDivElement>
		>(({children, ...otherProps}, ref) => (
			<div ref={ref} {...otherProps} tabIndex={0}>
				<Icon className="mr-2" symbol="user" />

				{children}
			</div>
		));

		Trigger.displayName = 'Trigger';

		const {getByRole} = render(
			<Picker as={Trigger} items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const svg = getByRole('presentation');
		const selectedValue = getByRole('combobox');

		expect(selectedValue.textContent).toBe('Select an option');
		expect(svg.tagName).toBe('svg');
		expect(selectedValue.getAttribute('aria-activedescendant')).toBe('');
		expect(selectedValue.getAttribute('aria-haspopup')).toBe('listbox');
		expect(selectedValue.getAttribute('role')).toBe('combobox');
		expect(selectedValue.getAttribute('aria-expanded')).toBe('false');
		expect(selectedValue.getAttribute('tabindex')).toBe('0');
	});

	it('render option with a custom attribute prefixed by "data-"', () => {
		const {getByRole} = render(
			<Picker>
				<Option data-attribute="data-attribute-value" />
			</Picker>
		);

		const combobox = getByRole('combobox');

		userEvent.click(combobox);

		const option = getByRole('option');

		expect(option.getAttribute('data-attribute')).toBe(
			'data-attribute-value'
		);
	});

	it('renders option buttons with type="button"', () => {
		const {getAllByRole, getByRole} = render(
			<Picker defaultActive items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		expect(getByRole('combobox').getAttribute('type')).toBe('button');

		const options = getAllByRole('option');

		expect(options).toHaveLength(3);

		options.forEach((option) => {
			expect(option.tagName).toBe('BUTTON');
			expect(option.getAttribute('type')).toBe('button');
		});
	});

	it('does not submit a wrapping form when an option is clicked', () => {
		const onSubmit = jest.fn((event) => event.preventDefault());

		const {getAllByRole, getByRole} = render(
			<form onSubmit={onSubmit}>
				<Picker defaultActive items={['Apple', 'Banana', 'Blueberry']}>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			</form>
		);

		userEvent.click(getAllByRole('option')[1]!);

		expect(onSubmit).not.toHaveBeenCalled();
		expect(getByRole('combobox').textContent).toBe('Banana');
	});

	it('render option a link when item has href', () => {
		const items = [
			{
				href: '#1',
				label: 1,
			},
			{
				label: 2,
			},
			{
				href: '#3',
				label: 3,
			},
			{
				label: 4,
			},
		];

		const {getByRole, getByText} = render(
			<Picker items={items}>
				{(item) => (
					<Option href={item?.href} key={item.label}>
						{item.label}
					</Option>
				)}
			</Picker>
		);

		const combobox = getByRole('combobox');

		userEvent.click(combobox);

		const link1 = getByText('1').closest('a');
		const link3 = getByText('3').closest('a');

		expect(link1).toHaveAttribute('href', '#1');
		expect(link3).toHaveAttribute('href', '#3');

		expect(getByText('2').closest('a')).toBeNull();
		expect(getByText('4').closest('a')).toBeNull();
	});

	it('does not render the keyboard arrows indicator by default', async () => {
		const {getByRole} = render(
			<Picker>
				<Option key="apple">Apple</Option>

				<Option key="banana">Banana</Option>
			</Picker>
		);

		await userEvent.click(getByRole('combobox'));

		expect(
			document.querySelector('.clay-keyboard-arrows-indicator')
		).not.toBeInTheDocument();
	});

	it('renders the keyboard arrows indicator alongside the menu when enabled', async () => {
		const {getByRole} = render(
			<Picker displayKeyboardArrowsIndicator>
				<Option key="apple">Apple</Option>

				<Option key="banana">Banana</Option>
			</Picker>
		);

		await userEvent.click(getByRole('combobox'));

		const indicator = document.querySelector(
			'.clay-keyboard-arrows-indicator'
		);

		expect(indicator).toBeInTheDocument();
		expect(indicator).toHaveClass('clay-keyboard-arrows-vertical');
		expect(indicator).toHaveClass(
			'clay-keyboard-arrows-indicator-floating'
		);
	});

	it('sets tabindex 0 on the trigger by default', () => {
		const {getByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		expect(getByRole('combobox').getAttribute('tabindex')).toBe('0');
	});

	it('honors the tabIndex prop on the trigger', () => {
		const {getByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']} tabIndex={-1}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		expect(getByRole('combobox').getAttribute('tabindex')).toBe('-1');
	});
});
