/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import Share from '../../../../src/main/resources/META-INF/resources/js/components/SidebarPanelInfoView/Share';

describe('Share', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders the html provided by fetchSharingButtonURL', async () => {
		window.fetch = jest.fn().mockReturnValue(
			Promise.resolve({
				ok: true,
				text: jest
					.fn()
					.mockReturnValue(Promise.resolve('<button>share</button>')),
			})
		);

		const {getByText} = render(
			<Share fetchSharingButtonURL="https://example" />
		);

		await waitFor(() => {
			expect(getByText('share')).toBeInTheDocument();
			expect(fetch).toHaveBeenCalled();
		});
	});

	it('calls onError prop if the is an error', async () => {
		window.fetch = jest.fn().mockReturnValue(
			Promise.resolve({
				ok: false,
				text: jest
					.fn()
					.mockReturnValue(Promise.resolve('<button>share</button>')),
			})
		);

		const handleError = jest.fn();

		render(
			<Share
				fetchSharingButtonURL="https://example"
				onError={handleError}
			/>
		);

		await waitFor(() => {
			expect(handleError).toHaveBeenCalled();
		});
	});
});
