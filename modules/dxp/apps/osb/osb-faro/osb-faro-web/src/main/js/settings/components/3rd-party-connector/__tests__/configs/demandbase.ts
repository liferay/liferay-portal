import demandbaseConfig from '../../configs/demandbase';
import {DataSourceTypes} from 'shared/util/constants';
import {Entity} from '../../types';
import {fetchConnectorEntityCount} from 'shared/api/connector';

jest.mock('shared/api/connector', () => ({
	fetchConnectorEntityCount: jest.fn(() => Promise.resolve(7))
}));

describe('demandbase config', () => {
	beforeEach(() => {
		(fetchConnectorEntityCount as jest.Mock).mockClear();
	});

	it('uses the demandbase slug', () => {
		expect(demandbaseConfig.slug).toBe('demandbase');
	});

	it('uses the demandbase data source type enum', () => {
		expect(demandbaseConfig.type).toBe(DataSourceTypes.Demandbase);
	});

	it('targets the demandbase accounts endpoint', () => {
		expect(demandbaseConfig.endpointPath).toBe('/api/demandbase_accounts');
	});

	it('is flagged as singleton (only one instance allowed)', () => {
		expect(demandbaseConfig.singleton).toBe(true);
	});

	it('exposes the Accounts entity', () => {
		expect(demandbaseConfig.entities).toHaveLength(1);

		const [accounts] = demandbaseConfig.entities;

		expect(accounts.entity).toBe(Entity.Accounts);
		expect(typeof accounts.fetchCount).toBe('function');
	});

	it('delegates fetchCount to fetchConnectorEntityCount with the entity', async () => {
		await demandbaseConfig.entities[0].fetchCount!({
			groupId: '42',
			id: 'data-source-9'
		});

		expect(fetchConnectorEntityCount).toHaveBeenCalledWith(
			Entity.Accounts,
			{groupId: '42', id: 'data-source-9'}
		);
	});

	it('builds the language strings from the configured display name', () => {
		expect(demandbaseConfig.languages.connectTitle).toContain(
			demandbaseConfig.displayName
		);
		expect(demandbaseConfig.languages.tokenLabel).toContain(
			demandbaseConfig.displayName
		);
	});
});
