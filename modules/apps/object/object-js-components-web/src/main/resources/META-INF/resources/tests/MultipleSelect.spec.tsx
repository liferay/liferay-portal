/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {MultipleSelect} from '../components/Select/MultipleSelect';

const mockOptions = [
	{
		children: [
			{
				checked: false,
				label: 'Account Administrator',
				value: 'Account Administrator',
			},
			{
				checked: false,
				label: 'Account Member',
				value: 'Account Member',
			},
		],
		label: 'Account Roles',
		value: 'accountRoles',
	},
	{
		children: [
			{
				checked: true,
				label: 'Administrator',
				value: 'Administrator',
			},
			{
				checked: true,
				label: 'Analytics Administrator',
				value: 'Analytics Administrator',
			},
		],
		label: 'Regular Roles',
		value: 'regularRoles',
	},
	{
		children: [
			{
				checked: false,
				label: 'Account Manager',
				value: 'Account Manager',
			},
			{
				checked: true,
				label: 'Organization Administrator',
				value: 'Organization Administrator',
			},
		],
		label: 'Organization Roles',
		value: 'organizationRoles',
	},
];

it('check all options using select all', () => {
	render(
		<MultipleSelect
			disabled={false}
			id="multipleSelect"
			options={mockOptions}
			selectAllOption
			setOptions={(options) =>
				expect(options).toStrictEqual([
					{
						children: [
							{
								_key: 'Account Administrator',
								checked: true,
								label: 'Account Administrator',
								value: 'Account Administrator',
							},
							{
								_key: 'Account Member',
								checked: true,
								label: 'Account Member',
								value: 'Account Member',
							},
						],
						label: 'Account Roles',
						value: 'accountRoles',
					},
					{
						children: [
							{
								_key: 'Administrator',
								checked: true,
								label: 'Administrator',
								value: 'Administrator',
							},
							{
								_key: 'Analytics Administrator',
								checked: true,
								label: 'Analytics Administrator',
								value: 'Analytics Administrator',
							},
						],
						label: 'Regular Roles',
						value: 'regularRoles',
					},
					{
						children: [
							{
								_key: 'Account Manager',
								checked: true,
								label: 'Account Manager',
								value: 'Account Manager',
							},
							{
								_key: 'Organization Administrator',
								checked: true,
								label: 'Organization Administrator',
								value: 'Organization Administrator',
							},
						],
						label: 'Organization Roles',
						value: 'organizationRoles',
					},
				])
			}
		/>
	);

	fireEvent.click(screen.getByRole('combobox'));

	fireEvent.click(screen.getByLabelText('select-all'));

	expect(screen.getByLabelText('select-all')).toBeChecked();
});

it('check an option', () => {
	render(
		<MultipleSelect
			disabled={false}
			id="multipleSelect"
			options={mockOptions}
			selectAllOption
			setOptions={(options) =>
				expect(options).toStrictEqual([
					{
						children: [
							{
								_key: 'Account Administrator',
								checked: true,
								label: 'Account Administrator',
								value: 'Account Administrator',
							},
							{
								_key: 'Account Member',
								checked: false,
								label: 'Account Member',
								value: 'Account Member',
							},
						],
						label: 'Account Roles',
						value: 'accountRoles',
					},
					{
						children: [
							{
								_key: 'Administrator',
								checked: true,
								label: 'Administrator',
								value: 'Administrator',
							},
							{
								_key: 'Analytics Administrator',
								checked: true,
								label: 'Analytics Administrator',
								value: 'Analytics Administrator',
							},
						],
						label: 'Regular Roles',
						value: 'regularRoles',
					},
					{
						children: [
							{
								_key: 'Account Manager',
								checked: false,
								label: 'Account Manager',
								value: 'Account Manager',
							},
							{
								_key: 'Organization Administrator',
								checked: true,
								label: 'Organization Administrator',
								value: 'Organization Administrator',
							},
						],
						label: 'Organization Roles',
						value: 'organizationRoles',
					},
				])
			}
		/>
	);

	fireEvent.click(screen.getByRole('combobox'));

	fireEvent.click(screen.getByText('Account Administrator'));

	expect(screen.getByLabelText('select-all')).not.toBeChecked();
});

it('search an option', () => {
	render(
		<MultipleSelect
			disabled={false}
			id="multipleSelect"
			options={mockOptions}
			search
			searchPlaceholder="Search for an option"
			selectAllOption
			setOptions={() => {}}
		/>
	);

	fireEvent.click(screen.getByRole('combobox'));
	fireEvent.change(screen.getByPlaceholderText('Search for an option'), {
		target: {value: 'Account'},
	});

	expect(screen.getByText('Account Administrator')).toBeVisible();
	expect(screen.getByText('Account Member')).toBeVisible();
	expect(screen.getByText('Account Manager')).toBeVisible();

	expect(screen.getAllByText('Administrator')).toHaveLength(1);
	expect(screen.getAllByText('Analytics Administrator')).toHaveLength(1);
	expect(screen.getAllByText('Organization Administrator')).toHaveLength(1);
});

it('unchecked all options using unselect all', () => {
	render(
		<MultipleSelect
			disabled={false}
			id="multipleSelect"
			options={mockOptions}
			selectAllOption
			setOptions={(options) =>
				expect(options).toStrictEqual([
					{
						children: [
							{
								_key: 'Account Administrator',
								checked: false,
								label: 'Account Administrator',
								value: 'Account Administrator',
							},
							{
								_key: 'Account Member',
								checked: false,
								label: 'Account Member',
								value: 'Account Member',
							},
						],
						label: 'Account Roles',
						value: 'accountRoles',
					},
					{
						children: [
							{
								_key: 'Administrator',
								checked: false,
								label: 'Administrator',
								value: 'Administrator',
							},
							{
								_key: 'Analytics Administrator',
								checked: false,
								label: 'Analytics Administrator',
								value: 'Analytics Administrator',
							},
						],
						label: 'Regular Roles',
						value: 'regularRoles',
					},
					{
						children: [
							{
								_key: 'Account Manager',
								checked: false,
								label: 'Account Manager',
								value: 'Account Manager',
							},
							{
								_key: 'Organization Administrator',
								checked: false,
								label: 'Organization Administrator',
								value: 'Organization Administrator',
							},
						],
						label: 'Organization Roles',
						value: 'organizationRoles',
					},
				])
			}
		/>
	);

	fireEvent.click(screen.getByTitle('Clear All'));

	expect(screen.getByLabelText('select-all')).not.toBeChecked();
});
