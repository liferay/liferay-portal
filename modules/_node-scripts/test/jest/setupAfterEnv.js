/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-env jest */

import '@testing-library/jest-dom';

afterEach(() => {
	global.fetch?.mockRestore?.();
});

beforeEach(() => {
	jest.spyOn(global, 'fetch').mockImplementation((...args) => {
		throw new Error(
			`global.fetch was not mocked for this call: ${JSON.stringify(args)}`
		);
	});
});
