/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-env jest */

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	navigate: jest.fn(),
	sub: (...args) => globalThis.Liferay.Util.sub(...args),
}));
