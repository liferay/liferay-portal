/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import RemoteImportModal from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/remote_import/RemoteImportModal';

const REMOTE_INSTANCES = [
	{id: 1, name: 'Production'},
	{id: 2, name: 'UAT'},
];

async function renderModal(remoteInstances = REMOTE_INSTANCES) {
	fetch.mockResponseOnce(JSON.stringify({items: remoteInstances}));

	const closeModal = jest.fn();

	const {container} = render(
		<RemoteImportModal closeModal={closeModal} exportProcessId={99} />
	);

	await screen.findByText('execute');

	return {closeModal, container};
}

describe('RemoteImportModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('has no accessibility violations', async () => {
		const {container} = await renderModal();

		await screen.findByLabelText(/^remote-environment/);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('explains when there are no remote environments', async () => {
		await renderModal([]);

		expect(
			await screen.findByText('no-remote-environments-have-been-added')
		).toBeInTheDocument();
		expect(screen.getByText('execute').closest('button')).toBeDisabled();
	});

	it('sends the export to the selected remote environment', async () => {
		const {closeModal} = await renderModal();

		await userEvent.selectOptions(
			await screen.findByLabelText(/^remote-environment/),
			'UAT'
		);

		fetch.mockResponseOnce(JSON.stringify({id: 7}));

		await userEvent.click(screen.getByText('execute'));

		expect(closeModal).toHaveBeenCalled();
		expect(JSON.parse(String(fetch.mock.calls[1][1]?.body))).toEqual({
			exportProcessId: 99,
			r_remoteInstanceToRemoteImportProcesses_c_remoteInstanceId: 2,
		});
		expect(Liferay.Util.openToast).toHaveBeenCalledWith({
			message:
				'the-remote-import-was-requested.-you-will-be-notified-when-it-starts-and-when-it-finishes',
			type: 'success',
		});
	});

	it('keeps the modal open when the remote import cannot be added', async () => {
		const {closeModal} = await renderModal();

		await screen.findByLabelText(/^remote-environment/);

		fetch.mockResponseOnce(
			JSON.stringify({title: 'Only a successful export can be sent'}),
			{status: 400}
		);

		await userEvent.click(screen.getByText('execute'));

		expect(closeModal).not.toHaveBeenCalled();
		expect(screen.getByRole('alert')).toBeInTheDocument();
	});
});
