import {buildLanguages} from '../buildLanguages';
import {ConnectorConfig, Entity} from '../types';
import {DataSourceTypes} from 'shared/util/constants';
import {fetchConnectorEntityCount} from 'shared/api/connector';

const SLUG = 'hubspot';

const displayName = Liferay.Language.get('hubspot');

const hubspotConfig: ConnectorConfig = {
	displayName,
	endpointPath: '/api/hubspot_webhooks',
	entities: [
		{
			entity: Entity.Events,
			fetchCount: params =>
				fetchConnectorEntityCount(Entity.Events, params)
		}
	],
	languages: buildLanguages(displayName),
	requiresLDP: true,
	singleton: true,
	slug: SLUG,
	type: DataSourceTypes.Hubspot
};

export default hubspotConfig;
