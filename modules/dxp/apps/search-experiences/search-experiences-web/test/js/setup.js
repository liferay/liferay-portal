/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Extends the timeout session to prevent the following error: 'Timeout - Async
 * callback was not invoked within the 5000ms timeout specified by
 * jest.setTimeout.Error'
 */

jest.setTimeout(30000);

// Mock getClientRects (used by CodeMirror internally)

Object.defineProperty(HTMLElement.prototype, 'getClientRects', {
	configurable: true,
	value() {
		return [
			{
				bottom: 20,
				height: 20,
				left: 0,
				right: 100,
				top: 0,
				width: 100,
				x: 0,
				y: 0,
			},
		];
	},
});

// Mock document.createRange for CodeMirror

document.createRange = () => {
	const range = {
		commonAncestorContainer: document.createElement('div'),
		getBoundingClientRect: () => ({
			bottom: 20,
			height: 20,
			left: 0,
			right: 100,
			top: 0,
			width: 100,
			x: 0,
			y: 0,
		}),
		getClientRects: () => [
			{
				bottom: 20,
				height: 20,
				left: 0,
				right: 100,
				top: 0,
				width: 100,
				x: 0,
				y: 0,
			},
		],
		setEnd: jest.fn(),
		setStart: jest.fn(),
	};

	return range;
};
