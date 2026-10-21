/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {screen} from '@testing-library/react';
import {escapeHTML} from 'frontend-js-web';

import '@testing-library/jest-dom';

import {
	openErrorToast,
	openSuccessToast,
} from '../../src/main/resources/META-INF/resources/js/utils/toasts';

describe('toasts', () => {
	beforeAll(() => {
		Liferay.Util.escapeHTML = escapeHTML;
	});

	afterAll(() => {
		delete (Liferay.Util as Partial<typeof Liferay.Util>).escapeHTML;
	});

	afterEach(() => {
		document.body.innerHTML = '';
	});

	it('opens an error toast with the message as text', () => {
		openErrorToast('Unable to add <b>tool</b>');

		const alert = screen.getByRole('alert');

		expect(alert).toHaveTextContent('error');
		expect(alert).toHaveTextContent('Unable to add <b>tool</b>');
		expect(alert.querySelector('b')).toBeNull();
	});

	it('opens a success toast', () => {
		openSuccessToast('Tool added');

		const alert = screen.getByRole('alert');

		expect(alert).toHaveTextContent('success');
		expect(alert).toHaveTextContent('Tool added');
	});

	it('renders the message as HTML only when asked to', () => {
		openSuccessToast('Added <b>tool</b>', {
			dangerouslySetMessageHTML: true,
		});

		expect(screen.getByRole('alert').querySelector('b')).toHaveTextContent(
			'tool'
		);
	});
});
