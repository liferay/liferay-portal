import {DataSourceTypes} from 'shared/util/constants';
import {
	getConnectorConfig,
	listAvailableConnectors,
	listConnectors,
} from '../registry';
import {SubscriptionNames} from 'shared/util/subscriptions';

describe('connector registry', () => {
	describe('getConnectorConfig', () => {
		it('returns the demandbase config when looked up by enum value', () => {
			const config = getConnectorConfig(DataSourceTypes.Demandbase);

			expect(config).toBeDefined();
			expect(config?.slug).toBe('demandbase');
			expect(config?.type).toBe(DataSourceTypes.Demandbase);
		});

		it('returns the hubspot config when looked up by enum value', () => {
			const config = getConnectorConfig(DataSourceTypes.Hubspot);

			expect(config).toBeDefined();
			expect(config?.slug).toBe('hubspot');
			expect(config?.type).toBe(DataSourceTypes.Hubspot);
		});

		it('returns the marketo config when looked up by enum value', () => {
			const config = getConnectorConfig(
				DataSourceTypes.MarketoEventStream
			);

			expect(config).toBeDefined();
			expect(config?.slug).toBe('marketo');
			expect(config?.type).toBe(DataSourceTypes.MarketoEventStream);
		});

		it('resolves each connector from the provider type literal the backend sends', () => {
			expect(getConnectorConfig('DEMANDBASE')?.slug).toBe('demandbase');
			expect(getConnectorConfig('HUBSPOT')?.slug).toBe('hubspot');
			expect(getConnectorConfig('MARKETO')?.slug).toBe('marketo');
		});

		it('is case-insensitive on the lookup key', () => {
			expect(getConnectorConfig('demandbase')?.slug).toBe('demandbase');
			expect(getConnectorConfig('HuBsPoT')?.slug).toBe('hubspot');
		});

		it('returns undefined for unknown types', () => {
			expect(getConnectorConfig('UNKNOWN')).toBeUndefined();
		});

		it('returns undefined when given empty input', () => {
			expect(getConnectorConfig('')).toBeUndefined();
			expect(getConnectorConfig(undefined)).toBeUndefined();
		});
	});

	describe('listConnectors', () => {
		it('returns every registered connector config', () => {
			const slugs = listConnectors()
				.map((c) => c.slug)
				.sort();

			expect(slugs).toEqual(['demandbase', 'hubspot', 'marketo']);
		});
	});

	describe('listAvailableConnectors', () => {
		it('hides singleton connectors that already exist on the data source list', () => {
			const existing = new Set([DataSourceTypes.Demandbase]);

			const slugs = listAvailableConnectors(
				existing,
				SubscriptionNames.LiferayDataPlatformPrivateBeta
			).map((c) => c.slug);

			expect(slugs).not.toContain('demandbase');
			expect(slugs).toContain('hubspot');
		});

		it('returns all connectors when none are present yet', () => {
			const slugs = listAvailableConnectors(
				new Set(),
				SubscriptionNames.LiferayDataPlatformPrivateBeta
			)
				.map((c) => c.slug)
				.sort();

			expect(slugs).toEqual(['demandbase', 'hubspot', 'marketo']);
		});

		it('hides every connector that requires LDP when the plan is non-LDP', () => {
			const slugs = listAvailableConnectors(
				new Set(),
				SubscriptionNames.LiferayAnalyticsCloudEnterprise
			).map((c) => c.slug);

			const ldpRequired = listConnectors()
				.filter((config) => config.requiresLDP)
				.map((c) => c.slug);

			ldpRequired.forEach((slug) => {
				expect(slugs).not.toContain(slug);
			});
		});

		it('shows LDP-only connectors on an LDP plan', () => {
			const slugs = listAvailableConnectors(
				new Set(),
				SubscriptionNames.LiferayDataPlatformPrivateBeta
			)
				.map((c) => c.slug)
				.sort();

			expect(slugs).toEqual(['demandbase', 'hubspot', 'marketo']);
		});

		it('hides LDP-only connectors while the subscription is still loading', () => {
			const slugs = listAvailableConnectors(new Set(), null).map(
				(c) => c.slug
			);

			expect(slugs).toEqual([]);
		});
	});
});
