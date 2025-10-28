/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-env browser */

require('regenerator-runtime/runtime');

global.Headers = require('./mocks/Headers');

global.Liferay = require('./mocks/Liferay');

// Temporary `createRange` mock until we update Jest 26 and jsdom >= 16.
// See: https://github.com/liferay/liferay-frontend-projects/issues/46

if (!global.createRange) {
	global.createRange = () => ({
		createContextualFragment(htmlString) {
			const div = document.createElement('div');

			div.innerHTML = `<br>${htmlString}`;
			div.removeChild(div.firstChild);

			const fragment = document.createDocumentFragment();

			while (div.firstChild) {
				fragment.appendChild(div.firstChild);
			}

			return fragment;
		},
	});
}

require('jest-fetch-mock').enableMocks();

global.fetch = require('jest-fetch-mock');

global.themeDisplay = global.Liferay.ThemeDisplay;

// JSDom does not support `Image.decode()` natively

if (!global.Image.prototype.decode) {
	Object.defineProperty(global.Image.prototype, 'decode', {
		get() {
			return () => Promise.resolve();
		},
	});
}

// JSDom does not support `crypto` natively

if (!global.crypto) {
	Object.defineProperty(global, 'crypto', {
		value: require('crypto'),
	});
}
