// To add a new third-party connector, see:
// https://liferay.atlassian.net/wiki/spaces/ENGAC/pages/4841275420/Third-Party+Connector+Framework+-+Adding+a+New+Data+Source+Connector+on+osb-faro-web+frontend

import demandbaseConfig from './configs/demandbase';
import hubspotConfig from './configs/hubspot';
import marketoConfig from './configs/marketo';
import {ConnectorConfig} from './types';
import {DataSourceTypes} from 'shared/util/constants';
import {isLDPPlan} from 'shared/util/subscriptions';

const connectorRegistry: Record<string, ConnectorConfig> = {
	[DataSourceTypes.Demandbase]: demandbaseConfig,
	[DataSourceTypes.Hubspot]: hubspotConfig,
	[DataSourceTypes.MarketoEventStream]: marketoConfig,
};

export function getConnectorConfig(
	type: string | undefined
): ConnectorConfig | undefined {
	if (!type) {
		return undefined;
	}

	return connectorRegistry[type.toUpperCase()];
}

export function listConnectors(): ConnectorConfig[] {
	return Object.values(connectorRegistry);
}

export function listAvailableConnectors(
	existingTypes: ReadonlySet<string>,
	subscriptionName: string | null = null
): ConnectorConfig[] {
	const ldpAllowed = isLDPPlan(subscriptionName);

	return listConnectors().filter((config) => {
		if (config.singleton && existingTypes.has(config.type)) {
			return false;
		}

		if (config.requiresLDP && !ldpAllowed) {
			return false;
		}

		return true;
	});
}

export {connectorRegistry};
