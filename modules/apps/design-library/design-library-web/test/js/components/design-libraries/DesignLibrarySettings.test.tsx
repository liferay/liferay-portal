/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import DesignLibrarySettings from '../../../../src/main/resources/META-INF/resources/js/components/design-libraries/DesignLibrarySettings';

const mockOpenToast = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openToast: (...args: any[]) => mockOpenToast(...args),
	useId: () => 'id',
}));

const XSS_NAME = '<img src=x onerror=alert(document.domain)>';

const mockGet = jest.fn();
const mockUpdate = jest.fn();

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/services/DesignLibraryService',
	() => ({
		get: (...args: any[]) => mockGet(...args),
		update: (...args: any[]) => mockUpdate(...args),
	})
);

const DEFAULT_PROPS = {
	backURL: '/back',
	externalReferenceCode: 'erc',
	groupId: '123',
	portletId: 'portletId',
};

function buildDesignLibrary(name: string) {
	return {description: 'description', externalReferenceCode: 'erc', name};
}

describe('DesignLibrarySettings', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'x-was-saved-successfully'
				? '{0} was saved successfully.'
				: key
		);

		mockGet.mockResolvedValue(buildDesignLibrary(XSS_NAME));
		mockUpdate.mockResolvedValue(buildDesignLibrary(XSS_NAME));
	});

	afterEach(() => {
		(Liferay.Language.get as jest.Mock).mockImplementation(
			(key: string) => key
		);
	});

	it('displays the name as plain text in the success toast', async () => {
		render(<DesignLibrarySettings {...DEFAULT_PROPS} />);

		const saveButton = await screen.findByRole('button', {name: 'save'});

		await userEvent.click(saveButton);

		await waitFor(() => expect(mockOpenToast).toHaveBeenCalled());

		const [{message}] = mockOpenToast.mock.calls[0];

		const container = document.createElement('div');

		container.innerHTML = message;

		expect(container.querySelector('img')).toBeNull();
		expect(container.textContent).toContain(XSS_NAME);
	});
});
