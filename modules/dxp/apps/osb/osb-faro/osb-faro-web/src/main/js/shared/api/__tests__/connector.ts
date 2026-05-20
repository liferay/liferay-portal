jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {
	createConnector,
	fetchConnectorEntityCount,
	generateConnectorToken,
	updateConnector
} from '../connector';

describe('Connector API', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockClear();
	});

	describe('createConnector', () => {
		it('builds the create path from the slug', () => {
			createConnector('demandbase', {
				credentials: {
					privateKey: 'token',
					publicKey: '',
					type: 'Token'
				},
				groupId: '23',
				name: 'Demandbase'
			});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					credentials: {
						privateKey: 'token',
						publicKey: '',
						type: 'Token'
					},
					name: 'Demandbase'
				},
				method: 'POST',
				path: 'contacts/23/data_source/demandbase'
			});
		});

		it('substitutes a different slug for hubspot', () => {
			createConnector('hubspot', {
				credentials: {
					privateKey: 'token',
					publicKey: '',
					type: 'Token'
				},
				groupId: '99',
				name: 'HubSpot'
			});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					path: 'contacts/99/data_source/hubspot'
				})
			);
		});
	});

	describe('updateConnector', () => {
		it('builds the patch path from id and slug', () => {
			updateConnector('demandbase', {
				credentials: {privateKey: 'tok'},
				groupId: '23',
				id: '5',
				name: 'Demandbase'
			});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					credentials: {privateKey: 'tok'},
					name: 'Demandbase'
				},
				method: 'PATCH',
				path: 'contacts/23/data_source/5/demandbase'
			});
		});
	});

	describe('fetchConnectorEntityCount', () => {
		it('builds the count path from the data source id and entity name', () => {
			fetchConnectorEntityCount('accounts', {
				groupId: '23',
				id: '7'
			});

			expect(sendRequest).toHaveBeenCalledWith({
				method: 'GET',
				path: 'contacts/23/data-source-metrics/7/accounts_count'
			});
		});

		it('works for arbitrary entity names', () => {
			fetchConnectorEntityCount('events', {
				groupId: '23',
				id: '7'
			});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					path: 'contacts/23/data-source-metrics/7/events_count'
				})
			);
		});
	});

	describe('generateConnectorToken', () => {
		it('encodes the slug as the token type', () => {
			generateConnectorToken({groupId: '23', type: 'demandbase'});

			expect(sendRequest).toHaveBeenCalledWith({
				method: 'POST',
				path: 'main/23/oauth2/tokens/new?type=demandbase&expiresIn='
			});
		});
	});
});
