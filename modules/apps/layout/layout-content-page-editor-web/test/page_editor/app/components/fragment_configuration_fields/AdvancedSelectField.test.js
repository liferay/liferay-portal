/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {AdvancedSelectField} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_configuration_fields/AdvancedSelectField';
import StoreMother from '../../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

jest.mock('@liferay/layout-js-components-web', () => ({
	...jest.requireActual('@liferay/layout-js-components-web'),
	isValidStyleValue: jest.fn(() => true),
}));

const FIELD = {
	cssProperty: 'font-size',
	defaultValue: '',
	icon: 'font-size',
	inherited: true,
	label: 'font-size',
	name: 'fontSize',
	type: 'select',
};

const OPTIONS = [
	{
		label: 'Inherited',
		value: '',
	},
	{
		label: 'Font Size Small',
		value: 'fontSizeSm',
	},
	{
		label: 'Font Size Base',
		value: 'fontSizeBase',
	},
	{
		label: 'Font Size Large',
		value: 'fontSizeLg',
	},
];

const TOKEN_VALUES = {
	fontSizeBase: {
		cssVariable: 'font-size-base',
		editorType: 'Length',
		label: 'Font Size Base',
		name: 'fontSizeBase',
		tokenSetLabel: 'Font Size',
		value: '0.875rem',
	},
	fontSizeLg: {
		cssVariable: 'font-size-lg',
		editorType: 'Length',
		label: 'Font Size Large',
		name: 'fontSizeLg',
		tokenSetLabel: 'Font Size',
		value: '1.125rem',
	},
	fontSizeSm: {
		cssVariable: 'font-size-sm',
		editorType: 'Length',
		label: 'Font Size Small',
		name: 'fontSizeSm',
		tokenSetLabel: 'Font Size',
		value: '1rem',
	},
};

const ITEM = {
	config: {
		landscapeMobile: {
			styles: {
				fontSize: 'fontSizeLg',
			},
		},
		styles: {
			fontSize: 'fontSizeDesktop',
		},
		tablet: {
			styles: {
				fontSize: 'fontSizeBase',
			},
		},
	},
};

const renderAdvancedSelectField = ({
	canDetachTokenValues = true,
	field = FIELD,
	onValueSelect = () => {},
	selectedViewportSize = 'desktop',
	value = '',
} = {}) =>
	render(
		<StoreMother.Component
			getState={() => ({
				permissions: {UPDATE: canDetachTokenValues ? true : false},
				selectedViewportSize,
			})}
		>
			<AdvancedSelectField
				field={field}
				item={ITEM}
				onValueSelect={onValueSelect}
				options={OPTIONS}
				tokenValues={TOKEN_VALUES}
				value={value}
			/>
		</StoreMother.Component>
	);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableViewportSizes: {
				desktop: {label: 'Desktop'},
				tablet: {label: 'tablet'},
			},
		},
	})
);

describe('AdvancedSelectField', () => {
	it('renders AdvancedSelectField', () => {
		renderAdvancedSelectField();

		expect(screen.getByLabelText('font-size')).toBeInTheDocument();
	});

	it('changes the value', async () => {
		renderAdvancedSelectField();
		const select = screen.getByLabelText('font-size');

		await userEvent.selectOptions(select, 'fontSizeSm');
		fireEvent.change(select);

		expect(select.options[1].selected).toBeTruthy();
	});

	it('displays always Detach button', () => {
		renderAdvancedSelectField();

		expect(screen.getByTitle('detach-style')).toBeInTheDocument();
	});

	it('only renders the inherited value indicator if the style is inherited and no value is selected', async () => {
		renderAdvancedSelectField();
		const select = screen.getByLabelText('font-size');

		expect(select.tagName).toBe('SELECT');
		expect(screen.getByTitle('inherited-value')).toBeInTheDocument();

		await userEvent.selectOptions(select, 'fontSizeSm');
		fireEvent.change(select);

		expect(screen.queryByTitle('inherited-value')).not.toBeInTheDocument();
	});

	it('renders a custom input when there is no value and the style is not inherited', () => {
		renderAdvancedSelectField({field: {...FIELD, inherited: false}});

		expect(screen.getByLabelText('font-size').tagName).toBe('INPUT');
		expect(screen.queryByTitle('inherited-value')).not.toBeInTheDocument();
	});

	it('renders an input with the token value when Detach button is clicked', async () => {
		renderAdvancedSelectField({value: 'fontSizeLg'});

		await userEvent.click(screen.getByTitle('detach-style'));

		const input = screen.getByLabelText('font-size');

		expect(input).toBeInTheDocument();
		expect(input).toHaveValue('1.125rem');
	});

	it('saves the value when a new value is typed and the user leaves the input', async () => {
		const onValueSelect = jest.fn();
		renderAdvancedSelectField({
			onValueSelect,
			value: 'mystyle',
		});
		const input = screen.getByLabelText('font-size');

		fireEvent.change(input, {target: {value: 'initial'}});
		fireEvent.blur(input);

		expect(onValueSelect).toBeCalledWith(FIELD.name, 'initial');
		expect(input).toHaveValue('initial');
	});

	it('saves the value when a new value is typed and Enter key is pressed', async () => {
		const onValueSelect = jest.fn();
		renderAdvancedSelectField({
			onValueSelect,
			value: 'mystyle',
		});
		const input = screen.getByLabelText('font-size');

		await userEvent.clear(input);
		await userEvent.type(input, 'initial');
		await userEvent.type(input, '{Enter}');

		expect(onValueSelect).toBeCalledWith(FIELD.name, 'initial');
		expect(input).toHaveValue('initial');
	});

	it('keeps the last value when the input is cleared', async () => {
		renderAdvancedSelectField({
			value: 'mystyle',
		});
		const input = screen.getByLabelText('font-size');

		await userEvent.clear(input);
		fireEvent.blur(input);

		expect(input).toHaveValue('mystyle');
	});

	it('renders the select when a token value is selected from the Value From Stylebook button', async () => {
		renderAdvancedSelectField({
			value: 'mystyle',
		});

		await userEvent.click(screen.getByTitle('value-from-stylebook'));
		await userEvent.click(screen.getByText('Font Size Base'));

		expect(screen.getByLabelText('font-size').tagName).toBe('SELECT');
	});

	it('renders the LengthField when the field has units', () => {
		renderAdvancedSelectField({
			field: {
				...FIELD,
				typeOptions: {
					showLengthField: true,
				},
			},
			value: 'mystyle',
		});

		expect(screen.getByTitle('select-units')).toBeInTheDocument();
	});

	it('does not render the Detach button when user does not have update permission', () => {
		renderAdvancedSelectField({
			canDetachTokenValues: false,
			value: 'fontSizeLg',
		});

		expect(screen.queryByTitle('detach-style')).not.toBeInTheDocument();
	});

	it('does not render the Value from Stylebook button when user does not have update permission', () => {
		renderAdvancedSelectField({
			canDetachTokenValues: false,
			value: 'my size',
		});

		expect(
			screen.queryByTitle('value-from-stylebook')
		).not.toBeInTheDocument();
	});

	it('clears the value when the "Reset" button is clicked and the viewport is Desktop', async () => {
		renderAdvancedSelectField({
			value: 'fontSizeLg',
		});

		await userEvent.click(screen.getByTitle('reset-to-initial-value'));

		const select = screen.getByLabelText('font-size');

		expect(select.tagName).toBe('SELECT');
		expect(select.nextSibling.textContent).toBe('');
	});

	it('sets the value of the previous viewport when the "Reset" button is clicked', async () => {
		renderAdvancedSelectField({
			selectedViewportSize: 'landscapeMobile',
			value: 'fontSizeSm',
		});

		await userEvent.click(screen.getByTitle('reset-to-tablet-value'));

		const select = screen.getByLabelText('font-size');

		expect(select.tagName).toBe('SELECT');
		expect(select.nextSibling.textContent).toBe('Font Size Base');
	});
});
