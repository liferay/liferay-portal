import React from 'react';
import WizardPage, {Step} from 'settings/components/base-page/WizardPage';
import {Alert} from 'shared/types';
import {AssignIndividualsDataToPropertiesStep} from 'settings/components/salesforce/steps/AssignIndividualsDataToChannelsStep';
import {ConnectLiferayDXPStep} from 'settings/components/liferay/steps/ConnectLiferayDXPStep';
import {ReviewSyncedDataStep} from 'settings/components/liferay/steps/ReviewSyncedDataStep';
import {updateLiferay} from 'shared/api/data-source';

const steps: Step[] = [
	{
		content: (props: any) => <ConnectLiferayDXPStep {...props} />,
		description: Liferay.Language.get(
			'connect-your-dxp-instance-to-analytics-cloud-to-start-tracking-users-visits-and-interactions-within-your-sites'
		),
		title: Liferay.Language.get('connect-your-dxp-analytics'),
	},
	{
		content: (props: any) => <ReviewSyncedDataStep {...props} />,
		description: Liferay.Language.get(
			'while-your-data-continues-syncing-in-the-background,-you-may-proceed-with-the-setup-process.-to-monitor-the-sync-status-at-any-time,-go-to-settings-in-data-sources'
		),
		title: Liferay.Language.get('review-synced-data'),
	},
	{
		content: (props: any) => (
			<AssignIndividualsDataToPropertiesStep
				{...props}
				onSubmit={(dataSource) => {
					if (
						dataSource?.sitesSelected ||
						dataSource?.contactsSelected
					) {
						props.addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get(
								'the-data-source-setup-is-now-complete,-and-you-will-begin-to-see-data-as-activities-occur-on-your-sites'
							),
						});
					}
					else {
						props.addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get(
								'the-data-source-setup-has-finished'
							),
						});
					}
				}}
				updateDataSourceFn={
					updateLiferay as (params: {
						[key: string]: any;
					}) => Promise<any>
				}
			/>
		),
		description: Liferay.Language.get(
			'properties-let-you-consolidate-data-from-individuals,-accounts,-campaigns,-sites,-and-commerce-channels-in-one-place.-an-individuals-data-is-available-in-every-property-they-are-assigned-to'
		),
		title: Liferay.Language.get('assign-individuals-data-to-properties'),
	},
];

const ConnectSalesforce = () => <WizardPage steps={steps} />;

export default ConnectSalesforce;
