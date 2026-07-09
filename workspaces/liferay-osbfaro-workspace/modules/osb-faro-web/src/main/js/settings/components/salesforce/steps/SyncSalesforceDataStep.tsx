import ClayForm from '@clayui/form';
import React, {useEffect, useState} from 'react';
import SalesforceAccountsAndIndividuals from 'settings/components/salesforce/SalesforceAccountsAndIndividuals';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {Text} from '@clayui/core';
import {updateSalesforce} from 'shared/api/data-source';
import {useParams} from 'react-router-dom';
import {useWizardPage} from '../../base-page/WizardPageContext';
import {WizardPageButtonGroup} from 'settings/components/base-page/WizardPageButtonGroup';

interface ISyncSalesforceDataStepProps {
	onNext: () => void;
	onPrev: () => void;
}

const SyncSalesforceDataStep = ({
	onNext,
	onPrev,
}: ISyncSalesforceDataStepProps) => {
	const [loading, setLoading] = useState(false);
	const {dataSource} = useWizardPage();
	const {groupId = ''} = useParams<{groupId: string}>();
	const [enabledAccounts, setEnabledAccounts] = useState(false);
	const [enabledIndividuals, setEnabledIndividuals] = useState(false);

	useEffect(() => {
		if (dataSource) {
			const accounts = dataSource.provider?.getIn([
				'accountsConfiguration',
				'enableAllAccounts',
			]);

			const contactsConfiguration = dataSource.provider?.get(
				'contactsConfiguration'
			);

			const individuals =
				contactsConfiguration?.get('enableAllContacts') &&
				contactsConfiguration?.get('enableAllLeads');

			setEnabledAccounts(accounts);
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

					await updateSalesforce({
						accountsConfiguration: {
							enableAllAccounts: enabledAccounts,
						},
						contactsConfiguration: {
							enableAllContacts: enabledIndividuals,
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
				<SalesforceAccountsAndIndividuals
					enabledAccounts={enabledAccounts}
					enabledIndividuals={enabledIndividuals}
					onAccountsChange={() =>
						setEnabledAccounts(!enabledAccounts)
					}
					onIndividualsChange={() =>
						setEnabledIndividuals(!enabledIndividuals)
					}
					type="checkbox"
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

export {SyncSalesforceDataStep};
