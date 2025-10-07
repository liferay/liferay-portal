/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import DefaultPermissionForm from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionForm';
import {DefaultPermissionFormProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionTypes';

const renderComponent = async (props: DefaultPermissionFormProps) => {
	return render(<DefaultPermissionForm {...props} />);
};

describe('DefaultPermissionForm', () => {
	it('Generate the empty correct permission form', async () => {
		const props = {
			actions: [
				{key: 'UPDATE', label: 'Update'},
				{key: 'VIEW', label: 'View'},
			],
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		props.actions.forEach((action) => {
			expect(
				screen.getByTestId(`head-cell-${action.key}`)
			).toBeInTheDocument();
		});

		props.roles.forEach((role) => {
			expect(
				screen.getByTestId(`row-cell-${role.key}`)
			).toBeInTheDocument();

			props.actions.forEach((action) => {
				expect(
					screen.getByTestId(`row-cell-${role.key}_${action.key}`)
				).toBeInTheDocument();

				expect(
					screen.getByTestId(`row-checkbox-${role.key}_${action.key}`)
				).toBeInTheDocument();
				expect(
					screen.getByTestId(`row-checkbox-${role.key}_${action.key}`)
				).not.toBeChecked();
				expect(
					screen.getByTestId(`row-checkbox-${role.key}_${action.key}`)
				).not.toBeDisabled();
			});
		});

		expect(
			screen.queryByTestId(`info-box-message`)
		).not.toBeInTheDocument();
		expect(
			screen.getByRole(`textbox`, {name: /search/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole(`textbox`, {name: /search/i})
		).not.toBeDisabled();
		expect(
			screen.getByRole(`navigation`, {name: /pagination/i})
		).toBeInTheDocument();
	});

	it('Show role icons', async () => {
		const props = {
			actions: [
				{key: 'UPDATE', label: 'Update'},
				{key: 'VIEW', label: 'View'},
			],
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		expect(screen.getByTestId(`row-cell-icon-admin`)).toBeVisible();
		expect(screen.getByTestId(`row-cell-icon-admin`)).toHaveClass(
			'lexicon-icon-user'
		);
		expect(screen.getByTestId(`row-cell-icon-guest`)).toBeVisible();
		expect(screen.getByTestId(`row-cell-icon-guest`)).toHaveClass(
			'lexicon-icon-globe'
		);
	});

	it('Preload checked permissions', async () => {
		const props = {
			actions: [
				{key: 'UPDATE', label: 'Update'},
				{key: 'VIEW', label: 'View'},
			],
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '1'},
				{key: 'owner', name: 'Owner', type: '1'},
			],
			values: {admin: ['UPDATE', 'VIEW'], owner: ['VIEW']},
		};

		renderComponent(props);

		expect(screen.getByTestId(`row-checkbox-admin_UPDATE`)).toBeChecked();
		expect(screen.getByTestId(`row-checkbox-admin_VIEW`)).toBeChecked();
		expect(
			screen.getByTestId(`row-checkbox-guest_UPDATE`)
		).not.toBeChecked();
		expect(screen.getByTestId(`row-checkbox-guest_VIEW`)).not.toBeChecked();
		expect(
			screen.getByTestId(`row-checkbox-owner_UPDATE`)
		).not.toBeChecked();
		expect(screen.getByTestId(`row-checkbox-owner_VIEW`)).toBeChecked();
	});

	it('Search', async () => {
		const props = {
			actions: [
				{key: 'UPDATE', label: 'Update'},
				{key: 'VIEW', label: 'View'},
			],
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '1'},
				{key: 'owner', name: 'Owner', type: '1'},
			],
		};

		renderComponent(props);

		const inputSearch = screen.getByTestId('input-search');

		await userEvent.clear(inputSearch);
		await userEvent.type(inputSearch, 'o');

		fireEvent.blur(inputSearch);

		await waitFor(() => {
			expect(screen.queryByTestId(`row-cell-admin`)).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-cell-guest`)
			).not.toBeInTheDocument();
			expect(screen.queryByTestId(`row-cell-owner`)).toBeInTheDocument();
		});

		await userEvent.clear(inputSearch);
		await userEvent.type(inputSearch, 'ow');

		fireEvent.blur(inputSearch);

		await waitFor(() => {
			expect(
				screen.queryByTestId(`row-cell-admin`)
			).not.toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-cell-guest`)
			).not.toBeInTheDocument();
			expect(screen.queryByTestId(`row-cell-owner`)).toBeInTheDocument();
		});

		await userEvent.clear(inputSearch);

		fireEvent.blur(inputSearch);

		await waitFor(() => {
			expect(screen.queryByTestId(`row-cell-admin`)).toBeInTheDocument();
			expect(screen.queryByTestId(`row-cell-guest`)).toBeInTheDocument();
			expect(screen.queryByTestId(`row-cell-owner`)).toBeInTheDocument();
		});
	});

	it('Handle the onChange parameter', async () => {
		let data = {};

		const onChangeFn = jest.fn((callbackData) => {
			data = callbackData;
		});

		const props = {
			actions: [
				{key: 'UPDATE', label: 'Update'},
				{key: 'VIEW', label: 'View'},
			],
			onChange: onChangeFn,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '1'},
			],
		};

		renderComponent(props);

		const adminUpdateCheckbox = screen.getByTestId(
			`row-checkbox-admin_UPDATE`
		);
		const adminViewCheckbox = screen.getByTestId(`row-checkbox-admin_VIEW`);
		const guestUpdateCheckbox = screen.getByTestId(
			`row-checkbox-guest_UPDATE`
		);
		const guestViewCheckbox = screen.getByTestId(`row-checkbox-guest_VIEW`);

		expect(adminUpdateCheckbox).not.toBeChecked();
		expect(adminViewCheckbox).not.toBeChecked();
		expect(guestUpdateCheckbox).not.toBeChecked();
		expect(guestViewCheckbox).not.toBeChecked();

		adminUpdateCheckbox.click();

		await waitFor(() => {
			expect(adminUpdateCheckbox).toBeChecked();
			expect(adminViewCheckbox).not.toBeChecked();
			expect(guestUpdateCheckbox).not.toBeChecked();
			expect(guestViewCheckbox).not.toBeChecked();
			expect(onChangeFn).toHaveBeenCalledTimes(1);

			expect(data).toHaveProperty('admin#UPDATE', true);
			expect(data).not.toHaveProperty('admin#VIEW');
			expect(data).not.toHaveProperty('guest#UPDATE');
			expect(data).not.toHaveProperty('guest#VIEW');
		});

		guestUpdateCheckbox.click();

		await waitFor(() => {
			expect(adminUpdateCheckbox).toBeChecked();
			expect(adminViewCheckbox).not.toBeChecked();
			expect(guestUpdateCheckbox).toBeChecked();
			expect(guestViewCheckbox).not.toBeChecked();
			expect(onChangeFn).toHaveBeenCalledTimes(2);

			expect(data).toHaveProperty('admin#UPDATE', true);
			expect(data).not.toHaveProperty('admin#VIEW');
			expect(data).toHaveProperty('guest#UPDATE', true);
			expect(data).not.toContain('guest#VIEW');
		});

		adminViewCheckbox.click();

		await waitFor(() => {
			expect(adminUpdateCheckbox).toBeChecked();
			expect(adminViewCheckbox).toBeChecked();
			expect(guestUpdateCheckbox).toBeChecked();
			expect(guestViewCheckbox).not.toBeChecked();
			expect(onChangeFn).toHaveBeenCalledTimes(3);

			expect(data).toHaveProperty('admin#UPDATE', true);
			expect(data).toHaveProperty('admin#VIEW', true);
			expect(data).toHaveProperty('guest#UPDATE', true);
			expect(data).not.toHaveProperty('guest#VIEW');
		});
	});

	it('Disable fields', async () => {
		const props = {
			actions: [
				{key: 'UPDATE', label: 'Update'},
				{key: 'VIEW', label: 'View'},
			],
			disabled: true,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		props.roles.forEach((role) => {
			props.actions.forEach((action) => {
				expect(
					screen.getByTestId(`row-checkbox-${role.key}_${action.key}`)
				).toBeInTheDocument();
				expect(
					screen.getByTestId(`row-checkbox-${role.key}_${action.key}`)
				).toBeDisabled();
			});
		});

		expect(screen.getByRole(`textbox`, {name: /search/i})).toBeDisabled();
	});

	it('Disable guest unsupported checkboxes', async () => {
		const props = {
			actions: [
				{guestUnsupported: true, key: 'DELETE', label: 'View'},
				{guestUnsupported: true, key: 'UPDATE', label: 'Update'},
				{key: 'VIEW', label: 'View'},
			],
			roles: [
				{key: 'Admin', name: 'Administrator', type: '1'},
				{key: 'Guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		expect(screen.getByTestId(`row-checkbox-Guest_DELETE`)).toBeDisabled();
		expect(screen.getByTestId(`row-checkbox-Guest_UPDATE`)).toBeDisabled();
		expect(
			screen.getByTestId(`row-checkbox-Guest_VIEW`)
		).not.toBeDisabled();
		expect(
			screen.getByTestId(`row-checkbox-Admin_DELETE`)
		).not.toBeDisabled();
		expect(
			screen.getByTestId(`row-checkbox-Admin_UPDATE`)
		).not.toBeDisabled();
		expect(
			screen.getByTestId(`row-checkbox-Admin_VIEW`)
		).not.toBeDisabled();
	});

	it('Display info message', async () => {
		const props = {
			actions: [
				{guestUnsupported: true, key: 'DELETE', label: 'View'},
				{guestUnsupported: true, key: 'UPDATE', label: 'Update'},
				{key: 'VIEW', label: 'View'},
			],
			infoBoxMessage: 'test',
			roles: [
				{key: 'Admin', name: 'Administrator', type: '1'},
				{key: 'Guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		expect(screen.getByTestId(`info-box-message`)).toBeVisible();
	});
});
