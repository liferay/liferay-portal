/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import WebdavURLCopyButton from '../../../src/main/resources/META-INF/resources/js/document_library/WebdavURLCopyButton';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const mockOn = jest.fn();
const mockDestroy = jest.fn();

jest.mock('clipboard', () => {
	return jest.fn().mockImplementation(() => ({
		destroy: mockDestroy,
		on: mockOn,
	}));
});

describe('WebdavURLCopyButton', () => {
	const url = 'https://example.com/webdav';
	const id = 'test-input';

	beforeEach(() => {
		jest.clearAllMocks();
	});

	afterEach(cleanup);

	it('renders the input with the given URL', () => {
		const {getByRole} = render(<WebdavURLCopyButton id={id} url={url} />);

		expect(getByRole('textbox')).toHaveValue(url);
		expect(getByRole('button', {name: /copy-link/i})).toBeInTheDocument();
	});

	it('copy the URL to the clipboard and show a success toast', () => {
		const {getByLabelText} = render(
			<WebdavURLCopyButton id={id} url={url} />
		);

		fireEvent.click(getByLabelText('copy-link'));

		const successCallback = mockOn.mock.calls.find(
			([event]) => event === 'success'
		)[1];

		successCallback();

		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: Liferay.Language.get('copied-link-to-the-clipboard'),
			})
		);
	});

	it('shows error toast when copy fails', () => {
		render(<WebdavURLCopyButton id={id} url={url} />);

		const errorCallback = mockOn.mock.calls.find(
			([event]) => event === 'error'
		)[1];

		errorCallback();

		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: Liferay.Language.get('an-error-occurred'),
				title: Liferay.Language.get('error'),
				type: 'warning',
			})
		);
	});
});
