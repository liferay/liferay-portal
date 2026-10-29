/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';

import {
	openErrorToast,
	openSuccessToast,
} from '../../src/main/resources/META-INF/resources/js/utils/toasts';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

describe('toasts', () => {
	beforeEach(() => {
		(openToast as jest.Mock).mockReset();
	});

	it('opens an error toast with the message as text', () => {
		openErrorToast('Unable to add <b>tool</b>');

		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: 'Unable to add &lt;b&gt;tool&lt;&#047;b&gt;',
				type: 'danger',
			})
		);
	});

	it('opens a success toast', () => {
		openSuccessToast('Tool added');

		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: 'Tool added',
				type: 'success',
			})
		);
	});

	it('renders the message as HTML only when asked to', () => {
		openSuccessToast('Added <b>tool</b>', {
			dangerouslySetMessageHTML: true,
		});

		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: 'Added <b>tool</b>',
			})
		);
	});
});
