/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {waitFor} from '@testing-library/react';

import CMSAssetPermissionService from '../../../../src/main/resources/META-INF/resources/js/common/services/CMSAssetPermissionService';
import openResetAssetPermissionModal from '../../../../src/main/resources/META-INF/resources/js/main_view/default_permission/ResetPermissionModalContent';

describe('ResetPermissionModalContent', () => {
	let resetAssetPermissionSpy: jest.SpyInstance;

	beforeEach(() => {
		global.Liferay.Language = {
			...global.Liferay?.Language,
			get: (key: string) => key,
		};
		global.Liferay.Util = {
			...global.Liferay?.Util,
			openModal: jest.fn(),
			openToast: jest.fn(),
		};

		(Liferay.Util.openModal as jest.Mock).mockClear();
		(Liferay.Util.openToast as jest.Mock).mockClear();

		resetAssetPermissionSpy = jest.spyOn(
			CMSAssetPermissionService,
			'resetAssetPermission'
		);
	});

	afterEach(() => {
		resetAssetPermissionSpy.mockRestore();
	});

	it('handles OK button and successfully resets permissions', async () => {
		resetAssetPermissionSpy.mockResolvedValue({});

		const loadDataFn = jest.fn();

		const props = {
			className: 'com.liferay.object.model.ObjectEntryFolder',
			classPK: 12345,
			loadData: loadDataFn,
		};

		openResetAssetPermissionModal(props);

		expect(Liferay.Util.openModal).toHaveBeenCalledTimes(1);

		const modalConfig = (Liferay.Util.openModal as jest.Mock).mock
			.calls[0][0];
		const okButton = modalConfig.buttons.find(
			(button: any) => button.label === 'ok'
		);
		const processCloseFn = jest.fn();

		await okButton.onClick({processClose: processCloseFn});

		await waitFor(() => {
			expect(resetAssetPermissionSpy).toHaveBeenCalledWith({
				className: props.className,
				classPK: props.classPK,
			});
			expect(Liferay.Util.openToast).toHaveBeenCalledWith({
				message: 'permissions-reset-successfully',
				type: 'success',
			});
			expect(loadDataFn).toHaveBeenCalledTimes(1);
			expect(processCloseFn).toHaveBeenCalledTimes(1);
		});
	});

	it('handles OK button and shows error on failure', async () => {
		const error = new Error('Failed to reset');
		resetAssetPermissionSpy.mockRejectedValue(error);

		const loadDataFn = jest.fn();
		const props = {
			className: 'com.liferay.document.library.kernel.model.DLFolder',
			classPK: 0,
			loadData: loadDataFn,
		};

		openResetAssetPermissionModal(props);

		expect(Liferay.Util.openModal).toHaveBeenCalledTimes(1);

		const modalConfig = (Liferay.Util.openModal as jest.Mock).mock
			.calls[0][0];
		const okButton = modalConfig.buttons.find(
			(button: any) => button.label === 'ok'
		);
		const processCloseFn = jest.fn();

		await okButton.onClick({processClose: processCloseFn});

		await waitFor(() => {
			expect(resetAssetPermissionSpy).toHaveBeenCalledWith({
				className: props.className,
				classPK: props.classPK,
			});
			expect(Liferay.Util.openToast).toHaveBeenCalledWith({
				message: 'an-error-occurred',
				type: 'danger',
			});
			expect(loadDataFn).not.toHaveBeenCalled();
			expect(processCloseFn).toHaveBeenCalledTimes(1);
		});
	});

	it('handles CANCEL button', async () => {
		const loadDataFn = jest.fn();

		const props = {
			className: 'com.liferay.object.model.ObjectEntryFolder',
			classPK: 12345,
			loadData: loadDataFn,
		};

		openResetAssetPermissionModal(props);

		expect(Liferay.Util.openModal).toHaveBeenCalledTimes(1);

		const modalConfig = (Liferay.Util.openModal as jest.Mock).mock
			.calls[0][0];
		const cancelButton = modalConfig.buttons.find(
			(button: any) => button.type === 'cancel'
		);

		expect(cancelButton).toBeDefined();
		expect(cancelButton.onClick).toBeUndefined();

		expect(resetAssetPermissionSpy).not.toHaveBeenCalled();
		expect(loadDataFn).not.toHaveBeenCalled();
	});
});
