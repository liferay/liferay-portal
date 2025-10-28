/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render, waitFor} from '@testing-library/react';
import {fetch, runScriptsInElement} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import ManageCollaborators from '../../../../src/main/resources/META-INF/resources/js/components/SidebarPanelInfoView/ManageCollaborators';

jest.mock('frontend-js-web', () => ({
	fetch: jest.fn().mockReturnValue({
		ok: true,
		text: jest
			.fn()
			.mockReturnValue('<button>Manage Collaborators</button>'),
	}),
	runScriptsInElement: jest.fn(),
}));

const fetchSharingCollaboratorsURL =
	'http://localhost:8080/fetch-manage-collaborators-button-url';

const _getComponent = () => {
	return (
		<ManageCollaborators
			fetchSharingCollaboratorsURL={fetchSharingCollaboratorsURL}
			onError={() => {}}
		/>
	);
};

describe('Manage collaborators component', () => {
	afterEach(() => {
		jest.restoreAllMocks();
		cleanup();
	});

	it('call the endpoint and renders', async () => {
		const {getByText} = render(_getComponent());

		expect(fetch).toHaveBeenCalledWith(fetchSharingCollaboratorsURL);

		await waitFor(() => {
			expect(getByText('Manage Collaborators')).toBeInTheDocument();
			expect(runScriptsInElement).toHaveBeenCalled();
		});
	});

	it('handles the API error', async () => {
		(fetch as jest.Mock).mockImplementation(() => {
			return {
				ok: false,
				text: null,
			};
		});

		const {queryByText} = render(_getComponent());

		expect(queryByText('Manage Collaborators')).not.toBeInTheDocument();
	});
});
