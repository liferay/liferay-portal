/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import React from 'react';

import {useToast} from '../../../src/main/resources/META-INF/resources/js/hooks/useToast.es';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

describe('useToast', () => {
	afterEach(() => {
		openToast.mockClear();
	});

	it('opens a toast with the escaped message and title', () => {
		useToast()({
			displayType: 'danger',
			message: '<b>message',
			title: '<i>title',
		});

		expect(openToast).toHaveBeenCalledWith({
			message: '&lt;b&gt;message',
			title: '&lt;i&gt;title',
			toastProps: undefined,
			type: 'danger',
		});
	});

	it('passes the action as the toast actions', () => {
		useToast()({
			action: <button>action</button>,
			displayType: 'info',
			message: 'message',
		});

		expect(openToast.mock.calls[0][0].toastProps.actions).toBeDefined();
	});

	it('returns the same function on every call', () => {
		expect(useToast()).toBe(useToast());
	});
});
