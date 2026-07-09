import ClayAlert from '@clayui/alert';
import ClayForm from '@clayui/form';
import React from 'react';
import {Alert, Modal} from 'shared/types';
import {ConnectMarketoCampaignAuth} from 'settings/components/marketo/ConnectMarketoCampaignAuth';
import {DataSourceStatuses} from 'shared/util/constants';
import {disconnect} from 'shared/api/data-source';
import {modalTypes} from 'shared/actions/modals';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {updateSearchParams} from 'settings/components/base-page/utis';
import {useHistory} from 'react-router-dom';
import {useWizardPage} from '../../base-page/WizardPageContext';
import {WizardPageButtonGroup} from 'settings/components/base-page/WizardPageButtonGroup';

interface IConnectMarketoCampaignStepProps {
	addAlert: Alert.AddAlert;
	close: Modal.close;
	groupId: string;
	onNext: () => void;
	open: Modal.open;
}

const ConnectMarketoCampaignStep = ({
	addAlert,
	close,
	groupId,
	onNext,
	open,
}: IConnectMarketoCampaignStepProps) => {
	const history = useHistory();
	const {dataSource, refetchDataSource} = useWizardPage();

	if (!dataSource) {
		return (
			<ConnectMarketoCampaignAuth
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

				<ConnectMarketoCampaignAuth
					addAlert={addAlert}
					buttonProps={{block: true}}
					dataSource={dataSource}
					disabled
					onSubmit={onNext}
				/>

				<WizardPageButtonGroup
					nextButtonLabel={Liferay.Language.get('continue')}
					onCancel={() => {
						open(modalTypes.CONFIRMATION_MODAL, {
							message: (
								<Text as="p" size={4}>
									{sub(
										Liferay.Language.get(
											'this-action-will-stop-syncing-data-from-x-to-this-workspace.-the-data-that-was-already-synced-will-remain-available-in-the-properties-the-data-source-was-connected-to.-are-you-sure-you-want-to-continue'
										),
										[Liferay.Language.get('marketo')]
									)}
								</Text>
							),
							modalVariant: 'modal-warning',
							onClose: close,
							onSubmit: async () => {
								try {
									await disconnect({
										groupId,
										id: dataSource.id,
									});

									refetchDataSource(dataSource.id || '');

									addAlert({
										alertType: Alert.Types.Success,
										message: Liferay.Language.get(
											'data-source-disconnected'
										),
									});

									close();
								}
								catch (error) {
									addAlert({
										alertType: Alert.Types.Error,
										message: Liferay.Language.get(
											'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists-please-contact-support'
										),
									});
								}
							},
							submitButtonDisplay: 'warning',
							submitMessage: Liferay.Language.get('disconnect'),
							title: Liferay.Language.get(
								'disconnect-data-source'
							),
							titleIcon: 'warning-full',
						});
					}}
					prevButtonLabel={Liferay.Language.get(
						'disconnect-data-source'
					)}
				/>
			</ClayForm>
		);
	}

	return (
		<ConnectMarketoCampaignAuth
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

export {ConnectMarketoCampaignStep};
