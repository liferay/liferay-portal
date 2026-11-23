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

import RemoteInstanceForm from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/remote_instances/RemoteInstanceForm';

const REMOTE_INSTANCE = {
	clientId: 'client-id',
	hostname: 'prod.example.com',
	id: 42,
	name: 'Production',
	port: 0,
	secureConnection: true,
};

function getRequestBody(index: number) {
	return JSON.parse(String(fetch.mock.calls[index][1]?.body));
}

function getRequestMethod(index: number) {
	return fetch.mock.calls[index][1]?.method;
}

function getRequestURL(index: number) {
	return new URL(String(fetch.mock.calls[index][0]), 'http://localhost');
}

describe('RemoteInstanceForm', () => {
	beforeEach(() => {
		fetch.resetMocks();
	});

	it('asks for the required fields before saving a new instance', async () => {
		render(<RemoteInstanceForm onBack={jest.fn()} remoteInstance={null} />);

		await userEvent.click(screen.getByText('save'));

		expect(screen.getAllByText('this-field-is-required')).toHaveLength(4);
		expect(fetch).not.toHaveBeenCalled();
	});

	it('rejects a port outside the valid range', async () => {
		render(<RemoteInstanceForm onBack={jest.fn()} remoteInstance={null} />);

		await userEvent.type(screen.getByLabelText('port'), '70000');
		await userEvent.click(screen.getByText('save'));

		expect(
			screen.getByText('the-remote-port-x-is-not-valid')
		).toBeInTheDocument();
	});

	it('creates the instance without requesting the secret back', async () => {
		fetch.mockResponseOnce(JSON.stringify(REMOTE_INSTANCE));

		const onBack = jest.fn();

		render(<RemoteInstanceForm onBack={onBack} remoteInstance={null} />);

		await userEvent.type(screen.getByLabelText(/^name/), 'Production');
		await userEvent.type(
			screen.getByLabelText(/^hostname/),
			'prod.example.com'
		);
		await userEvent.type(screen.getByLabelText(/^client-id/), 'client-id');
		await userEvent.type(screen.getByLabelText(/^client-secret/), 'secret');
		await userEvent.click(screen.getByText('save'));

		expect(onBack).toHaveBeenCalled();
		expect(fetch).toHaveBeenCalledTimes(1);
		expect(getRequestMethod(0)).toBe('POST');
		expect(getRequestBody(0)).toEqual({
			clientId: 'client-id',
			clientSecret: 'secret',
			hostname: 'prod.example.com',
			name: 'Production',
			port: null,
			secureConnection: true,
		});
		expect(getRequestURL(0).searchParams.get('fields')).not.toContain(
			'clientSecret'
		);
	});

	it('keeps the stored secret when it is left blank on edit', async () => {
		render(
			<RemoteInstanceForm
				onBack={jest.fn()}
				remoteInstance={REMOTE_INSTANCE}
			/>
		);

		expect(screen.getByLabelText(/^client-secret/)).toHaveValue('');
		expect(screen.getByLabelText('port')).toHaveValue('');
		expect(
			screen.getByText('leave-blank-to-keep-the-current-value')
		).toBeInTheDocument();

		fetch.mockResponseOnce(JSON.stringify(REMOTE_INSTANCE));

		await userEvent.type(screen.getByLabelText('port'), '8443');
		await userEvent.click(screen.getByText('save'));

		expect(getRequestMethod(0)).toBe('PATCH');
		expect(getRequestURL(0).pathname).toBe('/o/remote-instances/42');
		expect(getRequestBody(0)).not.toHaveProperty('clientSecret');
		expect(getRequestBody(0)).toHaveProperty('port', 8443);
	});

	it('does not let the secret be copied', async () => {
		render(<RemoteInstanceForm onBack={jest.fn()} remoteInstance={null} />);

		const clientSecretInput = screen.getByLabelText(/^client-secret/);

		expect(clientSecretInput).toHaveAttribute('type', 'password');

		const copyEvent = new Event('copy', {bubbles: true, cancelable: true});

		clientSecretInput.dispatchEvent(copyEvent);

		expect(copyEvent.defaultPrevented).toBe(true);
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<RemoteInstanceForm onBack={jest.fn()} remoteInstance={null} />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
