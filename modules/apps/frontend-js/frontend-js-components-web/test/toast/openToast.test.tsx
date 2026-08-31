/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import openToast from '../../src/main/resources/META-INF/resources/toast/openToast';

jest.mock('frontend-js-web', () => ({
	buildFragment: (html: string) => {
		const template = globalThis.document.createElement('template');

		template.innerHTML = html;

		return template.content;
	},
}));

describe('openToast', () => {
	beforeEach(() => {
		document.body.innerHTML = '';

		(globalThis as any).Liferay = {
			...(globalThis as any).Liferay,
			Icons: {spritemap: '/spritemap.svg'},
			component: () => {},
		};
	});

	it('does not move focus unless asked to', () => {
		openToast({message: 'saved', type: 'success'});

		expect(document.activeElement).not.toBe(
			document.querySelector('#ToastAlertContainer [role="alert"]')
		);
	});

	it('focuses the alert when autoFocus is set', () => {
		openToast({autoFocus: true, message: 'saved', type: 'success'});

		expect(document.activeElement).toBe(
			document.querySelector('#ToastAlertContainer [role="alert"]')
		);
	});

	it('renders the alert before returning', () => {
		openToast({message: 'saved', type: 'success'});

		expect(
			document.querySelector('#ToastAlertContainer .alert-success')
		).not.toBeNull();
	});
});
