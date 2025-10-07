/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import DefaultPermissionFormContainer from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionFormContainer';
import {
	DefaultAssetTypes,
	DefaultPermissionFormContainerProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionTypes';

const renderComponent = async (props: DefaultPermissionFormContainerProps) => {
	return render(<DefaultPermissionFormContainer {...props} />);
};

describe('DefaultPermissionFormContainer', () => {
	it('Show tabs', async () => {
		const props = {
			actions: {
				L_CONTENTS: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
				],
				L_FILES: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
					{key: 'VIEW2', label: 'View2'},
				],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
					{key: 'UPDATE2', label: 'Update2'},
					{key: 'VIEW2', label: 'View2'},
					{key: 'UPDATE3', label: 'Update3'},
					{key: 'VIEW3', label: 'View3'},
				],
			},
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '1'},
			],
		};

		renderComponent(props);

		expect(screen.getByRole('tab', {name: /folder/i})).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: /folder/i})).toHaveClass(
			'active'
		);
		expect(screen.getByRole('tab', {name: /content/i})).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: /content/i})).not.toHaveClass(
			'active'
		);
		expect(screen.getByRole('tab', {name: /file/i})).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: /file/i})).not.toHaveClass(
			'active'
		);
	});

	it('Update the checkboxes on tab change', async () => {
		const props = {
			actions: {
				L_CONTENTS: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
				],
				L_FILES: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
					{key: 'VIEW2', label: 'View2'},
				],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
					{key: 'UPDATE2', label: 'Update2'},
					{key: 'VIEW2', label: 'View2'},
					{key: 'UPDATE3', label: 'Update3'},
					{key: 'VIEW3', label: 'View3'},
				],
			},
			roles: [{key: 'admin', name: 'Administrator', type: '1'}],
		};

		renderComponent(props);

		await waitFor(() => {
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE2`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE3`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW2`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW3`)
			).toBeInTheDocument();
		});

		screen.getByRole('tab', {name: /content/i}).click();

		await waitFor(() => {
			expect(screen.getByRole('tab', {name: /content/i})).toHaveClass(
				'active'
			);
			expect(screen.getByRole('tab', {name: /file/i})).not.toHaveClass(
				'active'
			);
			expect(screen.getByRole('tab', {name: /folder/i})).not.toHaveClass(
				'active'
			);

			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE2`)
			).not.toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE3`)
			).not.toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW2`)
			).not.toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW3`)
			).not.toBeInTheDocument();
		});

		screen.getByRole('tab', {name: /file/i}).click();

		await waitFor(() => {
			expect(screen.getByRole('tab', {name: /content/i})).not.toHaveClass(
				'active'
			);
			expect(screen.getByRole('tab', {name: /file/i})).toHaveClass(
				'active'
			);
			expect(screen.getByRole('tab', {name: /folder/i})).not.toHaveClass(
				'active'
			);

			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE2`)
			).not.toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE3`)
			).not.toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW2`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW3`)
			).not.toBeInTheDocument();
		});

		screen.getByRole('tab', {name: /folder/i}).click();

		await waitFor(() => {
			expect(screen.getByRole('tab', {name: /content/i})).not.toHaveClass(
				'active'
			);
			expect(screen.getByRole('tab', {name: /file/i})).not.toHaveClass(
				'active'
			);
			expect(screen.getByRole('tab', {name: /folder/i})).toHaveClass(
				'active'
			);

			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE2`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE3`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW2`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW3`)
			).toBeInTheDocument();
		});
	});

	it('Preload checboxes on tab change', async () => {
		const props = {
			actions: {
				L_CONTENTS: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
				],
				L_FILES: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
					{key: 'VIEW2', label: 'View2'},
				],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
					{key: 'UPDATE2', label: 'Update2'},
					{key: 'VIEW2', label: 'View2'},
					{key: 'UPDATE3', label: 'Update3'},
					{key: 'VIEW3', label: 'View3'},
				],
			},
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '1'},
				{key: 'owner', name: 'Owner', type: '1'},
			],
			values: {
				L_CONTENTS: {admin: ['VIEW']},
				L_FILES: {
					admin: ['UPDATE', 'VIEW'],
					guest: ['VIEW'],
					owner: ['VIEW'],
				},
				OBJECT_ENTRY_FOLDERS: {
					admin: ['UPDATE', 'VIEW'],
					owner: ['VIEW'],
				},
			},
		};

		renderComponent(props);

		await waitFor(() => {
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE3`)
			).not.toBeChecked();
			expect(screen.getByTestId(`row-checkbox-admin_VIEW`)).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_UPDATE`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_UPDATE2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_UPDATE3`)
			).not.toBeChecked();
			expect(screen.getByTestId(`row-checkbox-owner_VIEW`)).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_VIEW2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_VIEW3`)
			).not.toBeChecked();
		});

		screen.getByRole('tab', {name: /content/i}).click();

		await waitFor(() => {
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE`)
			).not.toBeChecked();
			expect(screen.getByTestId(`row-checkbox-admin_VIEW`)).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_UPDATE`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_VIEW`)
			).not.toBeChecked();
		});

		screen.getByRole('tab', {name: /file/i}).click();

		await waitFor(() => {
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE`)
			).toBeChecked();
			expect(screen.getByTestId(`row-checkbox-admin_VIEW`)).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE`)
			).not.toBeChecked();
			expect(screen.getByTestId(`row-checkbox-guest_VIEW`)).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_UPDATE`)
			).not.toBeChecked();
			expect(screen.getByTestId(`row-checkbox-owner_VIEW`)).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_VIEW2`)
			).not.toBeChecked();
		});

		screen.getByRole('tab', {name: /folder/i}).click();

		await waitFor(() => {
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE3`)
			).not.toBeChecked();
			expect(screen.getByTestId(`row-checkbox-admin_VIEW`)).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_UPDATE`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_UPDATE2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_UPDATE3`)
			).not.toBeChecked();
			expect(screen.getByTestId(`row-checkbox-owner_VIEW`)).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_VIEW2`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-owner_VIEW3`)
			).not.toBeChecked();
		});
	});

	it('Dynamic tabs', async () => {
		const props = {
			actions: {
				L_CONTENTS: [
					{key: 'UPDATE1', label: 'Update1'},
					{key: 'VIEW1', label: 'View1'},
				],
				L_FILES: [
					{key: 'UPDATE2', label: 'Update2'},
					{key: 'VIEW2', label: 'View2'},
				],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'UPDATE3', label: 'Update3'},
					{key: 'VIEW3', label: 'View3'},
				],
			},
			roles: [{key: 'admin', name: 'Administrator', type: '1'}],
			types: [
				{key: DefaultAssetTypes.L_CONTENTS, label: 'content'},
				{key: DefaultAssetTypes.L_FILES, label: 'file'},
			],
			values: {
				L_CONTENTS: {admin: ['VIEW1']},
				L_FILES: {admin: ['VIEW2']},
				OBJECT_ENTRY_FOLDERS: {admin: ['UPDATE3', 'VIEW3']},
			},
		};

		renderComponent(props);

		expect(
			screen.queryByRole('tab', {name: /folder/i})
		).not.toBeInTheDocument();
		expect(
			screen.queryByRole('tab', {name: /content/i})
		).toBeInTheDocument();
		expect(screen.queryByRole('tab', {name: /file/i})).toBeInTheDocument();

		await waitFor(() => {
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE3`)
			).not.toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW3`)
			).not.toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE1`)
			).toBeInTheDocument();
			expect(
				screen.queryByTestId(`row-checkbox-admin_UPDATE1`)
			).not.toBeChecked();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW1`)
			).toBeChecked();
			expect(
				screen.queryByTestId(`row-checkbox-admin_VIEW1`)
			).toBeChecked();
		});
	});

	it.skip('Handle the onChange parameter', async () => {
		let data = {};

		const onChangeFn = jest.fn((callbackData) => {
			data = callbackData;
		});

		const props = {
			actions: {
				L_CONTENTS: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
				],
				L_FILES: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
					{key: 'VIEW2', label: 'View2'},
				],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'UPDATE', label: 'Update'},
					{key: 'VIEW', label: 'View'},
					{key: 'UPDATE2', label: 'Update2'},
					{key: 'VIEW2', label: 'View2'},
					{key: 'UPDATE3', label: 'Update3'},
					{key: 'VIEW3', label: 'View3'},
				],
			},
			onChange: onChangeFn,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '1'},
			],
			values: {
				L_CONTENTS: {admin: ['UPDATE']},
				L_FILES: {admin: ['UPDATE']},
				OBJECT_ENTRY_FOLDERS: {admin: ['UPDATE']},
			},
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

		expect(adminUpdateCheckbox).toBeChecked();
		expect(adminViewCheckbox).not.toBeChecked();
		expect(guestUpdateCheckbox).not.toBeChecked();
		expect(guestViewCheckbox).not.toBeChecked();

		await waitFor(() => {
			adminViewCheckbox.click();
		});

		await waitFor(() => {
			expect(onChangeFn).toHaveBeenCalledTimes(1);

			expect(data).toHaveProperty('L_CONTENTS', {admin: ['UPDATE']});
			expect(data).toHaveProperty('L_FILES', {admin: ['UPDATE']});
			expect(data).toHaveProperty('OBJECT_ENTRY_FOLDERS', {
				admin: ['UPDATE', 'VIEW'],
			});
		});

		await waitFor(() => {
			guestUpdateCheckbox.click();
		});

		await waitFor(() => {
			expect(onChangeFn).toHaveBeenCalledTimes(2);

			expect(data).toHaveProperty('L_CONTENTS', {admin: ['UPDATE']});
			expect(data).toHaveProperty('L_FILES', {admin: ['UPDATE']});
			expect(data).toHaveProperty('OBJECT_ENTRY_FOLDERS', {
				admin: ['UPDATE', 'VIEW'],
				guest: ['UPDATE'],
			});
		});

		await waitFor(() => {
			screen.getByRole('tab', {name: /file/i}).click();
		});

		await waitFor(() => {
			expect(screen.getByRole('tab', {name: /file/i})).toHaveClass(
				'active'
			);
		});

		await waitFor(() => {
			guestViewCheckbox.click();
		});

		await waitFor(() => {
			expect(onChangeFn).toHaveBeenCalledTimes(3);

			expect(data).toHaveProperty('L_CONTENTS', {admin: ['UPDATE']});
			expect(data).toHaveProperty('L_FILES', {
				admin: ['UPDATE'],
				guest: ['VIEW'],
			});
			expect(data).toHaveProperty('OBJECT_ENTRY_FOLDERS', {
				admin: ['UPDATE', 'VIEW'],
				guest: ['UPDATE'],
			});
		});
	});

	it('Disable fields', async () => {
		const props = {
			actions: {
				L_CONTENTS: [
					{key: 'UPDATE1', label: 'Update1'},
					{key: 'VIEW1', label: 'View1'},
				],
				L_FILES: [
					{key: 'UPDATE2', label: 'Update2'},
					{key: 'VIEW2', label: 'View2'},
				],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'UPDATE3', label: 'Update3'},
					{key: 'VIEW3', label: 'View3'},
				],
			},
			disabled: true,
			roles: [{key: 'admin', name: 'Administrator', type: '1'}],
		};

		renderComponent(props);

		expect(screen.getByTestId(`row-checkbox-admin_UPDATE3`)).toBeDisabled();
		expect(screen.getByTestId(`row-checkbox-admin_VIEW3`)).toBeDisabled();
		expect(screen.getByRole(`textbox`, {name: /search/i})).toBeDisabled();

		screen.getByRole('tab', {name: /file/i}).click();

		expect(screen.getByRole('tab', {name: /folder/i})).toHaveClass(
			'active'
		);
		expect(screen.getByRole('tab', {name: /file/i})).not.toHaveClass(
			'active'
		);
	});
});
