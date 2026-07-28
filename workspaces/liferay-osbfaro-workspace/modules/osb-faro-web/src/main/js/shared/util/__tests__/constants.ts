import {DataSourceTypes} from '../constants';

/**
 * The provider type literals the API sends on a data source's `providerType`.
 *
 * Backend sources of truth:
 *
 * - `com.liferay.osb.asah.common.model.ProviderType` (osb-asah)
 * - `*Provider.TYPE` under
 *   `osb-faro-engine-client/.../engine/client/model/provider` (osb-faro)
 *
 * `DataSourceTypes` values are matched against these literals to pick the
 * page for `/settings/data-source/:id`, so a value that drifts from this
 * list renders a blank page rather than failing loudly.
 */
const BACKEND_PROVIDER_TYPES = [
	'CSV',
	'DEMANDBASE',
	'HUBSPOT',
	'LIFERAY',
	'MARKETO',
	'MARKETO_CAMPAIGN',
	'SALESFORCE',
];

describe('DataSourceTypes', () => {
	it('matches the provider type literals the backend sends', () => {
		expect(Object.values(DataSourceTypes).sort()).toEqual(
			[...BACKEND_PROVIDER_TYPES].sort()
		);
	});

	it('maps the marketo event stream to the MARKETO literal the backend sends', () => {
		expect(DataSourceTypes.MarketoEventStream).toBe('MARKETO');
	});
});
