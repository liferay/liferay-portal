import React from 'react';
import WizardPage, {Step} from 'settings/components/base-page/WizardPage';
import {Alert} from 'shared/types';
import {AssignIndividualsDataToPropertiesStep} from 'settings/components/salesforce/steps/AssignIndividualsDataToChannelsStep';
import {ConnectMarketoCampaignStep} from 'settings/components/marketo/steps/ConnectMarketoCampaignStep';
import {sub} from 'shared/util/lang';
import {SyncMarketoCampaignDataStep} from 'settings/components/marketo/steps/SyncMarketoCampaignDataStep';
import {updateMarketoCampaign} from 'shared/api/data-source';

const steps: Step[] = [
	{
		content: (props: any) => <ConnectMarketoCampaignStep {...props} />,
		description: Liferay.Language.get(
			'to-connect-your-data-source-with-liferay-data-platform-enter-their-url-the-client-id-and-secret'
		),
		title: sub(Liferay.Language.get('connect-x'), [
			Liferay.Language.get('marketo'),
		]) as string,
	},

	{
		content: (props: any) => <SyncMarketoCampaignDataStep {...props} />,
		description: Liferay.Language.get(
			'select-which-marketo-data-you-would-like-to-sync-to-this-workspace'
		),
		title: sub(Liferay.Language.get('sync-x-data'), [
			Liferay.Language.get('marketo'),
		]) as string,
	},

	{
		content: ({addAlert, ...props}: any) => (
			<AssignIndividualsDataToPropertiesStep
				{...props}
				addAlert={addAlert}
				onSubmit={(dataSource) => {
					const contactsConfiguration = dataSource.provider?.get(
						'contactsConfiguration'
					);

					const individualsEnabled =
						contactsConfiguration?.get('enableAllLeads');

					if (individualsEnabled) {
						addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get(
								'the-data-source-setup-is-now-complete,-and-you-will-begin-to-see-data-as-activities-occur-on-your-sites'
							),
						});
					}
					else {
						addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get(
								'the-data-source-setup-has-finished'
							),
						});
					}
				}}
				updateDataSourceFn={updateMarketoCampaign}
			/>
		),
		description: Liferay.Language.get(
			'properties-let-you-consolidate-data-from-individuals,-accounts,-campaigns,-sites,-and-commerce-channels-in-one-place.-an-individuals-data-is-available-in-every-property-they-are-assigned-to'
		),
		title: Liferay.Language.get('assign-individuals-data-to-properties'),
	},
];

const ConnectMarketoCampaign = () => <WizardPage steps={steps} />;

export default ConnectMarketoCampaign;
