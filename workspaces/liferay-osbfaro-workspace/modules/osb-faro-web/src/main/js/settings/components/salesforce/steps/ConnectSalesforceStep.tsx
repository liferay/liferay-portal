import ClayAlert from '@clayui/alert';
import ClayForm from '@clayui/form';
import DisconnectDataSourceModal from 'shared/components/modals/DisconnectDataSourceModal';
import React from 'react';
import {Alert} from 'shared/types';
import {ConnectSalesforceAuth} from 'settings/components/salesforce/ConnectSalesforceAuth';
import {DataSourceStatuses} from 'shared/util/constants';
import {disconnect} from 'shared/api/data-source';
import {Routes, toRoute} from 'shared/util/router';
import {updateSearchParams} from 'settings/components/base-page/utis';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useModal} from '@clayui/modal';
import {useWizardPage} from '../../base-page/WizardPageContext';
import {WizardPageButtonGroup} from 'settings/components/base-page/WizardPageButtonGroup';

interface IConnectSalesforceStepProps {
	addAlert: Alert.AddAlert;
	groupId: string;
	onNext: () => void;
}

const ConnectSalesforceStep = ({
	addAlert,
	groupId,
	onNext,
}: IConnectSalesforceStepProps) => {
	const history = useHistoryAdapter();
	const {dataSource, refetchDataSource} = useWizardPage();
	const modal = useModal();

	if (!dataSource) {
		return (
			<ConnectSalesforceAuth
				addAlert={addAlert}
				buttonProps={{block: true}}
				onCancel={() => {
					history.push(
						toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
							groupId,
						})
					);
				}}
				onSubmit={(dataSource) => {
					updateSearchParams(history, 'dataSourceId', dataSource.id);

					onNext();
				}}
			/>
		);
	}

	if (dataSource.get('status') === DataSourceStatuses.Active) {
		return (
			<ClayForm
				onSubmit={(event) => {
					event.preventDefault();

					onNext();
				}}
			>
				<ClayAlert
					displayType="success"
					title={Liferay.Language.get('success')}
				>
					{Liferay.Language.get(
						'connection-established-successfully'
					)}
				</ClayAlert>

				<ConnectSalesforceAuth
					addAlert={addAlert}
					buttonProps={{block: true}}
					dataSource={dataSource}
					disabled
					onSubmit={onNext}
				/>

				<WizardPageButtonGroup
					nextButtonLabel={Liferay.Language.get('continue')}
					onCancel={() => {
						modal.onOpenChange(true);
					}}
					prevButtonLabel={Liferay.Language.get(
						'disconnect-data-source'
					)}
				/>

				{modal.open && (
					<DisconnectDataSourceModal
						addAlert={addAlert}
						errorMessage={Liferay.Language.get(
							'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists,-please-contact-support'
						)}
						message={Liferay.Language.get(
							'this-action-will-stop-syncing-data-from-this-data-source-to-this-liferay-data-platform-workspace.-the-data-that-was-already-synced-will-remain-available-in-the-properties-the-data-source-was-connected-to.-are-you-sure-you-want-to-continue'
						)}
						observer={modal.observer}
						onClose={() => modal.onOpenChange(false)}
						onSubmit={async () => {
							await disconnect({
								groupId,
								id: dataSource.id,
							});

							refetchDataSource(dataSource.id || '');
						}}
					/>
				)}
			</ClayForm>
		);
	}

	return (
		<ConnectSalesforceAuth
			addAlert={addAlert}
			buttonProps={{block: true}}
			dataSource={dataSource}
			onCancel={() => {
				history.push(
					toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
						groupId,
					})
				);
			}}
			onSubmit={onNext}
		/>
	);
};

export {ConnectSalesforceStep};
