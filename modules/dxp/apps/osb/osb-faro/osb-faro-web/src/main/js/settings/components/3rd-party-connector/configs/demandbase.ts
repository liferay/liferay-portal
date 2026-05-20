import {buildLanguages} from '../buildLanguages';
import {ConnectorConfig, Entity} from '../types';
import {DataSourceTypes} from 'shared/util/constants';
import {fetchConnectorEntityCount} from 'shared/api/connector';

const SLUG = 'demandbase';

const displayName = Liferay.Language.get('demandbase');

const demandbaseConfig: ConnectorConfig = {
	displayName,
	endpointPath: '/api/demandbase_accounts',
	entities: [
		{
			entity: Entity.Accounts,
			fetchCount: params =>
				fetchConnectorEntityCount(Entity.Accounts, params)
		}
	],
	languages: buildLanguages(displayName),
	requiresLDP: true,
	singleton: true,
	slug: SLUG,
	type: DataSourceTypes.Demandbase
};

export default demandbaseConfig;
