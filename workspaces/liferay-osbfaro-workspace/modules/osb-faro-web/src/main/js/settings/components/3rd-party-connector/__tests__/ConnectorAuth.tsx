jest.mock('shared/api/connector', () => ({
	createConnector: jest.fn(() => Promise.resolve({id: 'new-id'})),
	generateConnectorToken: jest.fn(() =>
		Promise.resolve({token: 'fake-token'})
	),
	updateConnector: jest.fn(() => Promise.resolve({})),
}));

import ConnectorAuth from '../ConnectorAuth';
import React from 'react';
import {ConnectorConfig} from '../types';
import {
	createConnector,
	generateConnectorToken,
	updateConnector,
} from 'shared/api/connector';
import {fireEvent, render, waitFor} from '@testing-library/react';

const buildConfig = (
	overrides: Partial<ConnectorConfig> = {}
): ConnectorConfig => ({
	columns: [],
	displayName: 'Acme',
	endpointPath: '/api/acme_accounts',
	entities: [],
	languages: {
		connectDescription: 'desc',
		connectTitle: 'Connect Acme',
		endpointHelper: 'helper text',
		endpointLabel: 'ENDPOINT_LABEL_ACME',
		tokenLabel: 'TOKEN_LABEL_ACME',
	},
	singleton: true,
	slug: 'acme',
	type: 'ACME',
	...overrides,
});

describe('ConnectorAuth', () => {
	beforeEach(() => {
		(generateConnectorToken as jest.Mock).mockClear();
		(createConnector as jest.Mock).mockClear();
		(updateConnector as jest.Mock).mockClear();
	});

	it('fetches the token using the connector slug on mount', async () => {
		const config = buildConfig();

		render(
			<ConnectorAuth
				addAlert={jest.fn() as any}
				config={config}
				groupId="23"
				onSubmit={jest.fn()}
			/>
		);

		await waitFor(() =>
			expect(generateConnectorToken).toHaveBeenCalledWith({
				groupId: '23',
				type: 'acme',
			})
		);
	});

	it('renders endpoint and token labels from the config', async () => {
		const config = buildConfig();

		const {getByText} = render(
			<ConnectorAuth
				addAlert={jest.fn() as any}
				config={config}
				groupId="23"
				onSubmit={jest.fn()}
			/>
		);

		await waitFor(() => expect(generateConnectorToken).toHaveBeenCalled());

		expect(getByText('ENDPOINT_LABEL_ACME')).toBeTruthy();
		expect(getByText('TOKEN_LABEL_ACME')).toBeTruthy();
	});

	it('uses the connector slug when creating a new data source', async () => {
		const config = buildConfig();
		const onSubmit = jest.fn();

		const {container} = render(
			<ConnectorAuth
				addAlert={jest.fn() as any}
				config={config}
				groupId="23"
				onSubmit={onSubmit}
			/>
		);

		await waitFor(() => expect(generateConnectorToken).toHaveBeenCalled());

		const form = container.querySelector('form')!;

		fireEvent.submit(form);

		await waitFor(() => expect(createConnector).toHaveBeenCalled());

		expect(createConnector).toHaveBeenCalledWith(
			'acme',
			expect.objectContaining({
				groupId: '23',
				name: 'Acme',
			})
		);
	});
});
