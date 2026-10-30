/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useState} from 'react';

import PIMConnectorSettingsField, {
	IPIMConnectorSettingsField,
	PIMConnectorSettingsValue,
} from '../../../src/main/resources/META-INF/resources/js/edit_connector/PIMConnectorSettingsField';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	RequiredMark: () => <span>required</span>,
}));

const OPTIONS = [
	{label: 'Blue', value: 'blue'},
	{label: 'Red', value: 'red'},
];

function PIMConnectorSettingsFieldWrapper({
	field,
	initialValue = null,
	onChange,
}: {
	field: IPIMConnectorSettingsField;
	initialValue?: PIMConnectorSettingsValue | null;
	onChange: (value: PIMConnectorSettingsValue) => void;
}) {
	const [value, setValue] = useState(initialValue);

	return (
		<PIMConnectorSettingsField
			field={field}
			onChange={(newValue) => {
				onChange(newValue);
				setValue(newValue);
			}}
			value={value}
		/>
	);
}

describe('PIMConnectorSettingsField', () => {
	it('toggles a boolean field', async () => {
		const onChange = jest.fn();

		render(
			<PIMConnectorSettingsFieldWrapper
				field={{
					businessType: 'Boolean',
					label: 'Active',
					name: 'active',
					required: false,
				}}
				onChange={onChange}
			/>
		);

		const toggle = screen.getByRole('switch', {name: 'Active'});

		expect(toggle).not.toBeChecked();

		await userEvent.click(toggle);

		expect(onChange).toHaveBeenLastCalledWith(true);
		expect(toggle).toBeChecked();
	});

	it('adds and removes the values of a multiselect picklist field', async () => {
		const onChange = jest.fn();

		render(
			<PIMConnectorSettingsFieldWrapper
				field={{
					businessType: 'MultiselectPicklist',
					label: 'Colors',
					name: 'colors',
					options: OPTIONS,
					required: false,
				}}
				initialValue={['blue']}
				onChange={onChange}
			/>
		);

		expect(screen.getByRole('checkbox', {name: 'Blue'})).toBeChecked();
		expect(screen.getByRole('checkbox', {name: 'Red'})).not.toBeChecked();

		await userEvent.click(screen.getByRole('checkbox', {name: 'Red'}));

		expect(onChange).toHaveBeenLastCalledWith(['blue', 'red']);

		await userEvent.click(screen.getByRole('checkbox', {name: 'Blue'}));

		expect(onChange).toHaveBeenLastCalledWith(['red']);
	});

	it('selects an option of a picklist field', async () => {
		const onChange = jest.fn();

		render(
			<PIMConnectorSettingsFieldWrapper
				field={{
					businessType: 'Picklist',
					label: 'Color',
					name: 'color',
					options: OPTIONS,
					required: true,
				}}
				onChange={onChange}
			/>
		);

		const select = screen.getByLabelText(/Color/);

		expect(select).toBeRequired();
		expect(select).toHaveDisplayValue('select-an-option');

		await userEvent.selectOptions(select, 'red');

		expect(onChange).toHaveBeenLastCalledWith('red');
		expect(select).toHaveDisplayValue('Red');
	});

	it('renders a text area for a long text field', async () => {
		const onChange = jest.fn();

		render(
			<PIMConnectorSettingsFieldWrapper
				field={{
					businessType: 'LongText',
					label: 'Description',
					name: 'description',
					required: false,
				}}
				onChange={onChange}
			/>
		);

		const textArea = screen.getByLabelText('Description');

		expect(textArea.tagName).toBe('TEXTAREA');

		await userEvent.type(textArea, 'Hello');

		expect(onChange).toHaveBeenLastCalledWith('Hello');
	});

	it('renders an input of the type that matches the business type', () => {
		render(
			<>
				<PIMConnectorSettingsField
					field={{
						businessType: 'Encrypted',
						label: 'Client Secret',
						name: 'clientSecret',
						required: true,
					}}
					onChange={jest.fn()}
				/>

				<PIMConnectorSettingsField
					field={{
						businessType: 'Integer',
						label: 'Batch Size',
						name: 'batchSize',
						required: false,
					}}
					onChange={jest.fn()}
				/>
			</>
		);

		const clientSecretInput = screen.getByLabelText(/Client Secret/);

		expect(clientSecretInput).toHaveAttribute('type', 'password');
		expect(clientSecretInput).toBeRequired();

		const batchSizeInput = screen.getByLabelText('Batch Size');

		expect(batchSizeInput).toHaveAttribute('type', 'number');
		expect(batchSizeInput).not.toBeRequired();

		expect(screen.getAllByText('required')).toHaveLength(1);
	});
});
