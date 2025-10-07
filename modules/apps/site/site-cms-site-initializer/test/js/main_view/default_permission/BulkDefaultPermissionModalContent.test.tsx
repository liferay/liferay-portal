/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import CMSDefaultPermissionService from '../../../../src/main/resources/META-INF/resources/js/common/services/CMSDefaultPermissionService';
import BulkDefaultPermissionModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/BulkDefaultPermissionModalContent';
import {BulkDefaultPermissionModalContentProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionTypes';
import * as BulkActionTrigger from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/triggerAssetBulkAction';

const renderComponent = async (
	props: BulkDefaultPermissionModalContentProps & {apiURL?: string}
) => {
	return render(<BulkDefaultPermissionModalContent {...props} />);
};

describe('BulkDefaultPermissionModalContent', () => {
	let batchUpdateSpy: jest.SpyInstance;
	let getObjectEntrySpy: jest.SpyInstance;
	let getSpaceSpy: jest.SpyInstance;

	beforeEach(() => {
		jest.useFakeTimers();

		getObjectEntrySpy = jest
			.spyOn(CMSDefaultPermissionService, 'getObjectEntry')
			.mockImplementation(({className}) => {
				if (
					className === 'com.liferay.object.model.ObjectEntryFolder'
				) {
					return Promise.resolve({
						classExternalReferenceCode: 'ERC1',
						className: 'com.liferay.object.model.ObjectEntryFolder',
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
						id: 1,
						treePath: '/100/101',
					});
				}

				return Promise.resolve({
					classExternalReferenceCode: 'ERC2',
					className: 'com.liferay.depot.model.DepotEntry',
					defaultPermissions: JSON.stringify({
						L_CONTENTS: {
							admin: ['VIEW1'],
						},
						L_FILES: {
							admin: ['VIEW2'],
						},
						OBJECT_ENTRY_FOLDERS: {
							admin: ['VIEW3'],
							guest: [],
						},
					}),
					depotGroupId: 100,
					externalReferenceCode: 'ERC2',
					id: 2,
					treePath: '/100',
				});
			});

		getSpaceSpy = jest
			.spyOn(CMSDefaultPermissionService, 'getSpace')
			.mockResolvedValue({
				creatorUserId: '20103',
				description: 'This is a test space',
				externalReferenceCode: 'ERC2',
				id: 1,
				name: 'Test Space',
			});

		batchUpdateSpy = jest.spyOn(
			BulkActionTrigger,
			'triggerAssetBulkAction'
		);
	});

	afterEach(() => {
		batchUpdateSpy.mockRestore();
		getObjectEntrySpy.mockRestore();
		getSpaceSpy.mockRestore();

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
			className: 'com.liferay.object.model.ObjectEntryFolder',
			closeModal: jest.fn(() => {}),
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'ERC2',
							scopeId: 100,
							scopeKey: '',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-6668c8b421c0',
					},
				],
				selectAll: false,
			},
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
			className: 'com.liferay.object.model.ObjectEntryFolder',
			closeModal: jest.fn(() => {}),
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'ERC2',
							scopeId: 100,
							scopeKey: '123',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-6668c8b421c0',
					},
				],
				selectAll: false,
			},
		};

		renderComponent(props);

		await waitFor(() => {
			expect(getObjectEntrySpy).toHaveBeenCalledTimes(1);
			expect(getObjectEntrySpy).toHaveBeenCalledWith({
				classExternalReferenceCode:
					props.selectedData.items[0].embedded
						.parentObjectEntryFolderExternalReferenceCode,
				className: props.selectedData.items[0].entryClassName,
			});
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

	it('Load data from API when items have the same parent', async () => {
		const props = {
			actions: {
				L_CONTENTS: [],
				L_FILES: [],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'UPDATE3', label: 'Update3'},
					{key: 'VIEW3', label: 'View3'},
				],
			},
			className: 'com.liferay.object.model.ObjectEntryFolder',
			closeModal: jest.fn(() => {}),
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'PARENT_ERC',
							scopeId: 100,
							scopeKey: '123',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode: 'ITEM1_ERC',
					},
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'PARENT_ERC',
							scopeId: 100,
							scopeKey: '123',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode: 'ITEM2_ERC',
					},
				],
				selectAll: false,
			},
		};

		renderComponent(props);

		await waitFor(() => {
			expect(getObjectEntrySpy).toHaveBeenCalledTimes(1);
			expect(getObjectEntrySpy).toHaveBeenCalledWith({
				classExternalReferenceCode: 'PARENT_ERC',
				className: 'com.liferay.object.model.ObjectEntryFolder',
			});
			expect(getSpaceSpy).not.toHaveBeenCalled();
		});

		await waitFor(() => {
			expect(
				screen.getByTestId('row-checkbox-admin_UPDATE3')
			).toBeChecked();
			expect(
				screen.getByTestId('row-checkbox-admin_VIEW3')
			).toBeChecked();
			expect(
				screen.getByTestId('row-checkbox-guest_UPDATE3')
			).not.toBeChecked();
			expect(
				screen.getByTestId('row-checkbox-guest_VIEW3')
			).toBeChecked();
		});
	});

	it('Load data from API when items have different parents but same space', async () => {
		const props = {
			actions: {
				L_CONTENTS: [],
				L_FILES: [],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'UPDATE3', label: 'Update3'},
					{key: 'VIEW3', label: 'View3'},
				],
			},
			className: 'com.liferay.object.model.ObjectEntryFolder',
			closeModal: jest.fn(() => {}),
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'PARENT1_ERC',
							scopeId: 100,
							scopeKey: '123',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode: 'ITEM1_ERC',
					},
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'PARENT2_ERC',
							scopeId: 100,
							scopeKey: '123',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode: 'ITEM2_ERC',
					},
				],
				selectAll: false,
			},
		};

		renderComponent(props);

		await waitFor(() => {
			expect(getSpaceSpy).toHaveBeenCalledTimes(1);
			expect(getSpaceSpy).toHaveBeenCalledWith(100);
			expect(getObjectEntrySpy).toHaveBeenCalledTimes(1);
			expect(getObjectEntrySpy).toHaveBeenCalledWith({
				classExternalReferenceCode: 'ERC2',
				className: 'com.liferay.depot.model.DepotEntry',
			});
		});

		await waitFor(() => {
			expect(
				screen.getByTestId('row-checkbox-admin_UPDATE3')
			).not.toBeChecked();
			expect(
				screen.getByTestId('row-checkbox-admin_VIEW3')
			).toBeChecked();
			expect(
				screen.getByTestId('row-checkbox-guest_UPDATE3')
			).not.toBeChecked();
			expect(
				screen.getByTestId('row-checkbox-guest_VIEW3')
			).not.toBeChecked();
		});
	});

	it('Load default data when items are from different spaces', async () => {
		const props = {
			actions: {
				L_CONTENTS: [],
				L_FILES: [],
				OBJECT_ENTRY_FOLDERS: [
					{key: 'ADD_ENTRY', label: 'Add Entry'},
					{key: 'VIEW', label: 'View'},
				],
			},
			className: 'com.liferay.object.model.ObjectEntryFolder',
			closeModal: jest.fn(() => {}),
			roles: [
				{
					key: 'CMS Administrator',
					name: 'CMS Administrator',
					type: '1',
				},
				{key: 'Guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'PARENT1_ERC',
							scopeId: 100,
							scopeKey: '123',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode: 'ITEM1_ERC',
					},
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'PARENT2_ERC',
							scopeId: 200,
							scopeKey: '456',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode: 'ITEM2_ERC',
					},
				],
				selectAll: false,
			},
		};

		renderComponent(props);

		await waitFor(() => {
			expect(getSpaceSpy).not.toHaveBeenCalled();
			expect(getObjectEntrySpy).not.toHaveBeenCalled();

			expect(
				screen.getByTestId('row-checkbox-CMS Administrator_ADD_ENTRY')
			).toBeChecked();
			expect(
				screen.getByTestId('row-checkbox-CMS Administrator_VIEW')
			).toBeChecked();
			expect(
				screen.queryByTestId('row-checkbox-Guest_ADD_ENTRY')
			).not.toBeChecked();
			expect(
				screen.queryByTestId('row-checkbox-Guest_VIEW')
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
			className: 'com.liferay.object.model.ObjectEntryFolder',
			closeModal: closeModalFn,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'ERC2',
							scopeId: 100,
							scopeKey: '',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-6668c8b421c0',
					},
				],
				selectAll: false,
			},
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
			apiURL: '',
			className: 'com.liferay.object.model.ObjectEntryFolder',
			closeModal: closeModalFn,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'ERC2',
							scopeId: 100,
							scopeKey: '',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-6668c8b421c0',
					},
					{
						embedded: {
							parentObjectEntryFolderExternalReferenceCode:
								'ERC2',
							scopeId: 100,
							scopeKey: '',
						},
						entryClassName:
							'com.liferay.object.model.ObjectEntryFolder',
						externalReferenceCode:
							'def321f1-8868-8256-3313-421c06668c8b',
					},
				],
				selectAll: false,
			},
		};

		renderComponent(props);

		await waitFor(() => {
			expect(getObjectEntrySpy).toHaveBeenCalledTimes(1);
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

			screen.getByTestId(`row-checkbox-admin_UPDATE3`).click();
			screen.getByTestId(`row-checkbox-guest_UPDATE3`).click();
		});

		expect(
			screen.getByTestId(`row-checkbox-admin_UPDATE3`)
		).not.toBeChecked();
		expect(screen.getByTestId(`row-checkbox-guest_UPDATE3`)).toBeChecked();

		screen.getByTestId('button-save').click();

		await waitFor(() => {
			expect(batchUpdateSpy).toHaveBeenCalledTimes(1);
			expect(batchUpdateSpy).toHaveBeenCalledWith({
				apiURL: props.apiURL,
				keyValues: {
					defaultPermissions:
						'{"L_CONTENTS":{"admin":["VIEW1"]},"L_FILES":{"admin":["VIEW2"]},"OBJECT_ENTRY_FOLDERS":{"admin":["VIEW3"],"guest":["VIEW3","UPDATE3"]}}',
				},
				onCreateError: expect.any(Function),
				onCreateSuccess: expect.any(Function),
				selectedData: props.selectedData,
				type: 'DefaultPermissionBulkAction',
			});
		});
	});
});
