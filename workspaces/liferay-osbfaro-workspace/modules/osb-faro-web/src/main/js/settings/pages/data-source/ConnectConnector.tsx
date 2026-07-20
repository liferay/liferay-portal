import React from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import WizardPage, {Step} from 'settings/components/base-page/WizardPage';
import {Alert} from 'shared/types';
import {AssignIndividualsDataToPropertiesStep} from 'settings/components/salesforce/steps/AssignIndividualsDataToChannelsStep';
import {ConnectorAuthStep} from 'settings/components/3rd-party-connector/steps/ConnectorAuthStep';
import {ConnectorConfig} from 'settings/components/3rd-party-connector/types';
import {getConnectorConfig} from 'settings/components/3rd-party-connector/registry';
import {updateConnector} from 'shared/api/connector';
import {useParams} from 'react-router-dom';

const buildSteps = (config: ConnectorConfig): Step[] => [
	{
		content: (props: any) => (
			<ConnectorAuthStep {...props} config={config} />
		),
		description: config.languages.connectDescription,
		title: config.languages.connectTitle,
	},
	{
		content: (props: any) => (
			<AssignIndividualsDataToPropertiesStep
				{...props}
				onSubmit={() => {
					props.addAlert({
						alertType: Alert.Types.Success,
						message: Liferay.Language.get(
							'the-data-source-setup-has-finished'
						),
					});
				}}
				updateDataSourceFn={(params: {[key: string]: any}) =>
					updateConnector(
						config.slug,
						params as Parameters<typeof updateConnector>[1]
					)
				}
			/>
		),
		description: Liferay.Language.get(
			'properties-let-you-consolidate-data-from-individuals,-accounts,-campaigns,-sites,-and-commerce-channels-in-one-place.-an-individuals-data-is-available-in-every-property-they-are-assigned-to'
		),
		title: Liferay.Language.get('assign-individuals-data-to-properties'),
	},
];

const ConnectConnector = () => {
	const {id = ''} = useParams<{id: string}>();

	const config = getConnectorConfig(id);

	if (!config) {
		return <RouteNotFound />;
	}

	return <WizardPage steps={buildSteps(config)} />;
};

export default ConnectConnector;
