/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import DefaultPermissionModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionModalContent';
import {DefaultPermissionModalContentProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionTypes';
import * as BulkActionTrigger from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/triggerAssetBulkAction';

const renderComponent = async (
	props: DefaultPermissionModalContentProps & {apiURL?: string}
) => {
	return render(<DefaultPermissionModalContent {...props} />);
};

describe('DefaultPermissionModalContent', () => {
	let apiGetSpy: jest.SpyInstance;
	let apiPatchSpy: jest.SpyInstance;
	let apiPostSpy: jest.SpyInstance;

	beforeEach(() => {
		jest.useFakeTimers();

		apiGetSpy = jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				items: [
					{
						classExternalReferenceCode: 'ERC1',
						className: 'com.liferay.depot.model.DepotEntry',
						defaultPermissions: JSON.stringify({
							L_CONTENTS: {
								admin: ['VIEW1'],
							},
							L_FILES: {
								admin: ['VIEW2'],
							},
							OBJECT_ENTRY_FOLDERS: {
								admin: ['UPDATE3', 'VIEW3'],
								guest: ['VIEW3'],
							},
						}),
						depotGroupId: 100,
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-6668c8b421c0',
						id: 34794,
						treePath: '/100/101',
					},
				],
			},
			error: null,
		});
		apiPatchSpy = jest.spyOn(ApiHelper, 'patch');
		apiPostSpy = jest.spyOn(BulkActionTrigger, 'triggerAssetBulkAction');
	});

	afterEach(() => {
		apiGetSpy.mockRestore();
		apiPatchSpy.mockRestore();
		apiPostSpy.mockRestore();

		jest.useRealTimers();
	});

	it('Display modal elements', async () => {
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
			classExternalReferenceCode: 'ERC1',
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: jest.fn(() => {}),
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		expect(screen.getByRole('tab', {name: /folder/i})).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: /content/i})).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: /file/i})).toBeInTheDocument();
		expect(screen.getByTestId('button-cancel')).toBeInTheDocument();
		expect(screen.getByTestId('button-save')).toBeInTheDocument();
		expect(
			screen.getByTestId(`row-checkbox-admin_UPDATE3`)
		).toBeInTheDocument();
		expect(
			screen.getByTestId(`row-checkbox-admin_VIEW3`)
		).toBeInTheDocument();
		expect(
			screen.getByTestId(`row-checkbox-guest_UPDATE3`)
		).toBeInTheDocument();
		expect(
			screen.getByTestId(`row-checkbox-guest_VIEW3`)
		).toBeInTheDocument();
	});

	it('Load data from API', async () => {
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
			classExternalReferenceCode: 'ERC1',
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: jest.fn(() => {}),
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		await waitFor(() => {
			expect(apiGetSpy).toHaveBeenCalledTimes(1);
			expect(apiGetSpy).toHaveBeenCalledWith(
				`/o/cms/default-permissions?filter=(classExternalReferenceCode eq '${props.classExternalReferenceCode}') and (className eq '${props.className}')`
			);
		});

		await waitFor(() => {
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE3`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW3`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW3`)
			).toBeChecked();
		});

		screen.getByRole('tab', {name: /content/i}).click();

		await waitFor(() => {
			expect(screen.getByRole('tab', {name: /content/i})).toHaveClass(
				'active'
			);

			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE1`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW1`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE1`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW1`)
			).not.toBeChecked();
		});
	});

	it('Handle cancel button', async () => {
		const closeModalFn = jest.fn(() => {});

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
			classExternalReferenceCode: 'ERC1',
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: closeModalFn,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		await waitFor(() => {
			expect(screen.getByTestId('button-cancel')).toBeEnabled();
		});

		screen.getByTestId('button-cancel').click();

		await waitFor(() => {
			expect(closeModalFn).toHaveBeenCalledTimes(1);
		});
	});

	it('Handle save button', async () => {
		const closeModalFn = jest.fn(() => {});

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
			classExternalReferenceCode: 'ERC1',
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: closeModalFn,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		await waitFor(() => {
			expect(apiGetSpy).toHaveBeenCalledTimes(1);
		});

		await waitFor(() => {
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE3`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW3`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW3`)
			).toBeChecked();
		});

		screen.getByTestId(`row-checkbox-admin_UPDATE3`).click();

		expect(
			screen.getByTestId(`row-checkbox-admin_UPDATE3`)
		).not.toBeChecked();

		screen.getByTestId(`row-checkbox-guest_UPDATE3`).click();

		expect(screen.getByTestId(`row-checkbox-guest_UPDATE3`)).toBeChecked();

		screen.getByTestId('button-save').click();

		await waitFor(() => {
			expect(apiPatchSpy).toHaveBeenCalledTimes(1);
			expect(apiPatchSpy).toHaveBeenCalledWith(
				{
					defaultPermissions:
						'{"L_CONTENTS":{"admin":["VIEW1"]},"L_FILES":{"admin":["VIEW2"]},"OBJECT_ENTRY_FOLDERS":{"admin":["VIEW3"],"guest":["VIEW3","UPDATE3"]}}',
				},
				'/o/cms/default-permissions/by-external-reference-code/fa9f1559-8256-4313-8868-6668c8b421c0'
			);
			expect(apiPostSpy).not.toHaveBeenCalled();
			expect(closeModalFn).toHaveBeenCalledTimes(1);
		});
	});

	it('Handle propagate on save', async () => {
		const closeModalFn = jest.fn(() => {});

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
			apiURL: '',
			classExternalReferenceCode: 'ERC1',
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: closeModalFn,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
		};

		renderComponent(props);

		await waitFor(() => {
			expect(apiGetSpy).toHaveBeenCalledTimes(1);
		});

		await waitFor(() => {
			expect(
				screen.getByTestId(`row-checkbox-admin_UPDATE3`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-admin_VIEW3`)
			).toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_UPDATE3`)
			).not.toBeChecked();
			expect(
				screen.getByTestId(`row-checkbox-guest_VIEW3`)
			).toBeChecked();
		});

		screen.getByTestId(`row-checkbox-admin_UPDATE3`).click();

		expect(
			screen.getByTestId(`row-checkbox-admin_UPDATE3`)
		).not.toBeChecked();

		await waitFor(async () => {
			screen.getByTestId(`row-checkbox-guest_UPDATE3`).click();
		});

		expect(screen.getByTestId(`row-checkbox-guest_UPDATE3`)).toBeChecked();

		await waitFor(() => {
			screen.getByTestId('checkbox-propagate').click();

			screen.getByTestId('button-save').click();
		});

		await waitFor(() => {
			expect(apiPatchSpy).toHaveBeenCalledTimes(1);
			expect(apiPatchSpy).toHaveBeenCalledWith(
				{
					defaultPermissions:
						'{"L_CONTENTS":{"admin":["VIEW1"]},"L_FILES":{"admin":["VIEW2"]},"OBJECT_ENTRY_FOLDERS":{"admin":["VIEW3"],"guest":["VIEW3","UPDATE3"]}}',
				},
				'/o/cms/default-permissions/by-external-reference-code/fa9f1559-8256-4313-8868-6668c8b421c0'
			);

			expect(apiPostSpy).toHaveBeenCalledTimes(1);
			expect(apiPostSpy).toHaveBeenCalledWith({
				apiURL: props.apiURL,
				keyValues: {
					defaultPermissions:
						'{"L_CONTENTS":{"admin":["VIEW1"]},"L_FILES":{"admin":["VIEW2"]},"OBJECT_ENTRY_FOLDERS":{"admin":["VIEW3"],"guest":["VIEW3","UPDATE3"]}}',
					depotGroupId: 100,
					treePath: '/100/101',
				},
				onCreateError: expect.any(Function),
				onCreateSuccess: expect.any(Function),
				overrideDefaultErrorToast: true,
				selectedData: {
					selectAll: true,
				},
				type: 'DefaultPermissionBulkAction',
			});
		});
	});
});
