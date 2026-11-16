/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';

import {useToaster} from '../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/hooks/useToaster.es';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

describe('The useToaster hook should', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('Open a toast of each type', () => {
		const toaster = useToaster();

		toaster.danger();
		toaster.info('Alert Info', 'Info');
		toaster.success();
		toaster.warning('Alert Warning', 'Warning', 1000);

		expect(openToast.mock.calls).toEqual([
			[
				{
					autoClose: 5000,
					message: 'error',
					title: 'error:',
					type: 'danger',
				},
			],
			[
				{
					autoClose: 5000,
					message: 'Alert Info',
					title: 'Info',
					type: 'info',
				},
			],
			[
				{
					autoClose: 5000,
					message: 'success',
					title: 'success:',
					type: 'success',
				},
			],
			[
				{
					autoClose: 1000,
					message: 'Alert Warning',
					title: 'Warning',
					type: 'warning',
				},
			],
		]);
	});

	it('Escape HTML in the message and the title', () => {
		const toaster = useToaster();

		toaster.info('<b>message</b>', '<i>title</i>');

		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: '&lt;b&gt;message&lt;&#047;b&gt;',
				title: '&lt;i&gt;title&lt;&#047;i&gt;',
			})
		);
	});
});
