/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import {SpacesBulkPermissionModalContentProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/DefaultPermissionTypes';
import SpacesBulkPermissionModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/SpacesBulkPermissionModalContent';
import * as BulkActionTrigger from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/triggerAssetBulkAction';

const renderComponent = async (
	props: SpacesBulkPermissionModalContentProps & {apiURL?: string}
) => {
	return render(<SpacesBulkPermissionModalContent {...props} />);
};

describe('SpacesBulkPermissionModalContent', () => {
	let batchUpdateSpy: jest.SpyInstance;

	beforeEach(() => {
		jest.useFakeTimers();

		batchUpdateSpy = jest.spyOn(
			BulkActionTrigger,
			'triggerAssetBulkAction'
		);
	});

	afterEach(() => {
		batchUpdateSpy.mockRestore();

		jest.useRealTimers();
	});

	it('Display modal elements', async () => {
		const props = {
			actions: [
				{key: 'UPDATE1', label: 'Update1'},
				{key: 'VIEW1', label: 'View1'},
			],
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: jest.fn(() => {}),
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						entryClassName: 'com.liferay.depot.model.DepotEntry',
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-6668c8b421c0',
					},
				],
				selectAll: false,
			},
		};

		renderComponent(props);

		expect(screen.getByTestId('button-cancel')).toBeInTheDocument();
		expect(screen.getByTestId('button-save')).toBeInTheDocument();
		expect(
			screen.getByTestId(`row-checkbox-admin_UPDATE1`)
		).toBeInTheDocument();
		expect(
			screen.getByTestId(`row-checkbox-admin_VIEW1`)
		).toBeInTheDocument();
		expect(
			screen.getByTestId(`row-checkbox-guest_UPDATE1`)
		).toBeInTheDocument();
		expect(
			screen.getByTestId(`row-checkbox-guest_VIEW1`)
		).toBeInTheDocument();
	});

	it('Preload data', async () => {
		const props = {
			actions: [
				{key: 'UPDATE1', label: 'Update1'},
				{key: 'VIEW1', label: 'View1'},
			],
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: jest.fn(() => {}),
			roles: [
				{
					actions: ['VIEW1'],
					key: 'admin',
					name: 'Administrator',
					type: '1',
				},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						entryClassName: 'com.liferay.depot.model.DepotEntry',
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-6668c8b421c0',
					},
				],
				selectAll: false,
			},
		};

		renderComponent(props);

		expect(
			screen.getByTestId(`row-checkbox-admin_UPDATE1`)
		).not.toBeChecked();
		expect(
			screen.getByTestId(`row-checkbox-admin_UPDATE1`)
		).not.toBeDisabled();
		expect(screen.getByTestId(`row-checkbox-admin_VIEW1`)).toBeChecked();
		expect(screen.getByTestId(`row-checkbox-admin_VIEW1`)).toBeDisabled();
		expect(
			screen.getByTestId(`row-checkbox-guest_UPDATE1`)
		).not.toBeChecked();
		expect(
			screen.getByTestId(`row-checkbox-guest_UPDATE1`)
		).not.toBeDisabled();
		expect(
			screen.getByTestId(`row-checkbox-guest_VIEW1`)
		).not.toBeChecked();
		expect(
			screen.getByTestId(`row-checkbox-guest_VIEW1`)
		).not.toBeDisabled();
	});

	it('Handle cancel button', async () => {
		const closeModalFn = jest.fn(() => {});

		const props = {
			actions: [
				{key: 'UPDATE1', label: 'Update1'},
				{key: 'VIEW1', label: 'View1'},
			],
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: closeModalFn,
			roles: [
				{key: 'admin', name: 'Administrator', type: '1'},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						entryClassName: 'com.liferay.depot.model.DepotEntry',
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
		const props = {
			actions: [
				{key: 'UPDATE1', label: 'Update1'},
				{key: 'VIEW1', label: 'View1'},
			],
			apiURL: '',
			className: 'com.liferay.depot.model.DepotEntry',
			closeModal: jest.fn(() => {}),
			roles: [
				{
					actions: ['VIEW1'],
					key: 'admin',
					name: 'Administrator',
					type: '1',
				},
				{key: 'guest', name: 'Guest', type: '2'},
			],
			selectedData: {
				items: [
					{
						embedded: {
							externalReferenceCode:
								'fa9f1559-8256-4313-8868-6668c8b421c0',
							id: 100,
						},
						entryClassName: 'com.liferay.depot.model.DepotEntry',
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-6668c8b421c0',
						siteId: 100,
					},
					{
						embedded: {
							externalReferenceCode:
								'fa9f1559-8256-4313-8868-432453543543',
							id: 200,
						},
						entryClassName: 'com.liferay.depot.model.DepotEntry',
						externalReferenceCode:
							'fa9f1559-8256-4313-8868-432453543543',
						siteId: 200,
					},
				],
				selectAll: false,
			},
		};

		renderComponent(props);

		await waitFor(() => {
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

			screen.getByTestId(`row-checkbox-admin_UPDATE1`).click();
			screen.getByTestId(`row-checkbox-guest_UPDATE1`).click();
		});

		expect(screen.getByTestId(`row-checkbox-admin_UPDATE1`)).toBeChecked();
		expect(screen.getByTestId(`row-checkbox-guest_UPDATE1`)).toBeChecked();

		screen.getByTestId('button-save').click();

		await waitFor(() => {
			expect(batchUpdateSpy).toHaveBeenCalledTimes(1);
			expect(batchUpdateSpy).toHaveBeenCalledWith({
				apiURL: props.apiURL,
				keyValues: {
					permissions: [
						{actionIds: ['VIEW1', 'UPDATE1'], roleName: 'admin'},
						{actionIds: ['UPDATE1'], roleName: 'guest'},
					],
				},
				onCreateError: expect.any(Function),
				onCreateSuccess: expect.any(Function),
				selectedData: props.selectedData,
				type: 'PermissionBulkAction',
			});
		});
	});
});
