import marketoConfig from '../../configs/marketo';
import {DataSourceTypes} from 'shared/util/constants';
import {Entity} from '../../types';
import {fetchConnectorEntityCount} from 'shared/api/connector';

jest.mock('shared/api/connector', () => ({
	fetchConnectorEntityCount: jest.fn(() => Promise.resolve(123)),
}));

describe('marketo config', () => {
	beforeEach(() => {
		(fetchConnectorEntityCount as jest.Mock).mockClear();
	});

	it('uses the marketo slug', () => {
		expect(marketoConfig.slug).toBe('marketo');
	});

	it('uses the marketo data source type enum', () => {
		expect(marketoConfig.type).toBe(DataSourceTypes.MarketoEventStream);
	});

	it('targets the marketo webhooks endpoint', () => {
		expect(marketoConfig.endpointPath).toBe('/api/marketo_webhooks');
	});

	it('is flagged as singleton', () => {
		expect(marketoConfig.singleton).toBe(true);
	});

	it('exposes the Events entity', () => {
		expect(marketoConfig.entities).toHaveLength(1);

		const [events] = marketoConfig.entities;

		expect(events.entity).toBe(Entity.Events);
		expect(typeof events.fetchCount).toBe('function');
	});

	it('delegates fetchCount to fetchConnectorEntityCount with the entity', async () => {
		await marketoConfig.entities[0].fetchCount!({
			groupId: '23',
			id: 'data-source-1',
		});

		expect(fetchConnectorEntityCount).toHaveBeenCalledWith(Entity.Events, {
			groupId: '23',
			id: 'data-source-1',
		});
	});

	it('builds the language strings from the configured display name', () => {
		expect(marketoConfig.languages.connectTitle).toContain(
			marketoConfig.displayName
		);
		expect(marketoConfig.languages.tokenLabel).toContain(
			marketoConfig.displayName
		);
	});
});
