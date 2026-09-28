/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {loadModule} from '../../../../src/main/resources/META-INF/resources/liferay/utils/client_extensions/loadModule';

const AMD_MODULE_NAME = 'amd-field-sample@1.0.0/index';

function Field() {
	return null;
}

describe('loadModule', () => {
	beforeEach(() => {
		Liferay.FeatureFlags['LPD-48372'] = true;
	});

	afterEach(() => {
		delete (Liferay as any).Loader;
		delete Liferay.FeatureFlags['LPD-48372'];
	});

	it('loads the default export of an AMD module', async () => {
		(Liferay as any).Loader = {
			require: jest.fn((moduleName, resolve) =>
				resolve({__esModule: true, default: Field})
			),
		};

		await expect(loadModule(AMD_MODULE_NAME)).resolves.toBe(Field);

		expect((Liferay as any).Loader.require).toHaveBeenCalledWith(
			AMD_MODULE_NAME,
			expect.any(Function),
			expect.any(Function)
		);
	});

	it('loads an AMD module without a default export', async () => {
		(Liferay as any).Loader = {
			require: jest.fn((moduleName, resolve) => resolve(Field)),
		};

		await expect(loadModule(AMD_MODULE_NAME)).resolves.toBe(Field);
	});

	it('rejects when the AMD loader fails to load the module', async () => {
		const error = new Error('Missing dependency');

		(Liferay as any).Loader = {
			require: jest.fn((moduleName, resolve, reject) => reject(error)),
		};

		await expect(loadModule(AMD_MODULE_NAME)).rejects.toBe(error);
	});

	it('rejects an AMD module when the AMD loader is disabled', async () => {
		Liferay.FeatureFlags['LPD-48372'] = false;

		(Liferay as any).Loader = {
			require: jest.fn(),
		};

		await expect(loadModule(AMD_MODULE_NAME)).rejects.toThrow(
			`Unable to load AMD module "${AMD_MODULE_NAME}" because the AMD loader is disabled`
		);
	});
});
