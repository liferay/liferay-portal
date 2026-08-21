/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

if (!globalThis.URL.createObjectURL) {
	globalThis.URL.createObjectURL = () => 'blob:jsdom-test';
	globalThis.URL.revokeObjectURL = () => {};
}

if (!globalThis.ResizeObserver) {
	globalThis.ResizeObserver = class {
		disconnect() {}
		observe() {}
		unobserve() {}
	} as unknown as typeof ResizeObserver;
}
