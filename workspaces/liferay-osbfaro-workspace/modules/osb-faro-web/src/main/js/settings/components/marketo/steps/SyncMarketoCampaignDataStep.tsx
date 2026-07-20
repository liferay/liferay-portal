import ClayForm from '@clayui/form';
import MarketoCampaignEntities from 'settings/components/marketo/MarketoCampaignEntities';
import React, {useEffect, useState} from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {Text} from '@clayui/core';
import {updateMarketoCampaign} from 'shared/api/data-source';
import {useParams} from 'react-router-dom';
import {useWizardPage} from '../../base-page/WizardPageContext';
import {WizardPageButtonGroup} from 'settings/components/base-page/WizardPageButtonGroup';

interface ISyncMarketoCampaignDataStepProps {
	onNext: () => void;
	onPrev: () => void;
}

const SyncMarketoCampaignDataStep = ({
	onNext,
	onPrev,
}: ISyncMarketoCampaignDataStepProps) => {
	const [loading, setLoading] = useState(false);
	const {dataSource} = useWizardPage();
	const {groupId = ''} = useParams<{groupId: string}>();
	const [enabledIndividuals, setEnabledIndividuals] = useState(false);

	useEffect(() => {
		if (dataSource) {
			const contactsConfiguration = dataSource.provider?.get(
				'contactsConfiguration'
			);

			const individuals = contactsConfiguration?.get('enableAllLeads');

			setEnabledIndividuals(individuals);
		}
	}, []);

	return (
		<ClayForm
			onSubmit={async (event) => {
				event.preventDefault();

				if (!dataSource) {
					return;
				}

				try {
					setLoading(true);

					await updateMarketoCampaign({
						contactsConfiguration: {
							enableAllLeads: enabledIndividuals,
						},
						groupId,
						id: dataSource.id,
					} as any);
				}
				catch (error) {
					addAlert({
						alertType: Alert.Types.Error,
						message: Liferay.Language.get(
							'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists,-please-contact-support'
						),
					});
				}
				finally {
					setLoading(false);

					onNext();
				}
			}}
		>
			<div className="mb-2">
				<Text size={2} weight="semi-bold">
					{Liferay.Language.get('connection-status').toUpperCase()}
				</Text>
			</div>

			{dataSource && (
				<MarketoCampaignEntities
					enabledIndividuals={enabledIndividuals}
					onIndividualsChange={() =>
						setEnabledIndividuals(!enabledIndividuals)
					}
				/>
			)}

			<WizardPageButtonGroup
				nextButtonLabel={Liferay.Language.get('continue')}
				nextButtonLoading={loading}
				onCancel={onPrev}
				prevButtonLabel={Liferay.Language.get('previous')}
			/>
		</ClayForm>
	);
};

export {SyncMarketoCampaignDataStep};
