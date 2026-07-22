import ClayAlert from '@clayui/alert';
import ClayForm from '@clayui/form';
import React, {useEffect, useState} from 'react';
import {Alert, Modal} from 'shared/types';
import {CopyInputValue} from '../../CopyInputValue';
import {DataSourceStatuses} from 'shared/util/constants';
import {disconnect, fetchToken} from 'shared/api/data-source';
import {modalTypes} from 'shared/actions/modals';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {updateSearchParams} from 'settings/components/base-page/utis';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useQueryParams} from 'shared/hooks/useQueryParams';
import {useWizardPage} from '../../base-page/WizardPageContext';
import {WizardPageButtonGroup} from 'settings/components/base-page/WizardPageButtonGroup';

interface IConnectLiferayDXPStepProps {
	addAlert: Alert.AddAlert;
	close: Modal.close;
	groupId: string;
	onNext: () => void;
	open: Modal.open;
}

const ConnectLiferayDXPStep = ({
	addAlert,
	close,
	groupId,
	onNext,
	open,
}: IConnectLiferayDXPStepProps) => {
	const {dataSource, refetchDataSource} = useWizardPage();
	const {dataSourceId} = useQueryParams();
	const history = useHistoryAdapter();

	const [token, setToken] = useState('');

	useEffect(() => {
		const fetchTokenFn = async () => {
			try {
				const token = await fetchToken(groupId, dataSourceId);

				setToken(token);
			}
			catch (error) {
				addAlert({
					alertType: Alert.Types.Error,
					message: (error as Error).message,
					timeout: false,
				});
			}
		};

		fetchTokenFn();
	}, [groupId]);

	if (
		!dataSource ||
		dataSource.get('status', null) !== DataSourceStatuses.Active
	) {
		return (
			<ClayForm
				onSubmit={async (event) => {
					event.preventDefault();

					try {
						const nextToken = await fetchToken(groupId);

						if (token === nextToken) {
							addAlert({
								alertType: Alert.Types.Error,
								message: sub(
									Liferay.Language.get(
										'first-paste-the-token-into-your-x-instance-in-order-to-continue-with-the-data-source-setup'
									),
									[Liferay.Language.get('liferay-dxp')]
								) as string,
							});
						}
						else {
							onNext();

							addAlert({
								alertType: Alert.Types.Success,
								message: Liferay.Language.get(
									'token-authenticated-successfully'
								),
							});
						}
					}
					catch (error) {
						addAlert({
							alertType: Alert.Types.Error,
							message: Liferay.Language.get(
								'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists,-please-contact-support'
							),
						});
					}
				}}
			>
				<label htmlFor="token">
					<Text weight="semi-bold">
						{sub(
							Liferay.Language.get(
								'copy-this-token-to-your-x-instance'
							),
							[Liferay.Language.get('liferay-dxp')]
						)}
					</Text>
				</label>

				<CopyInputValue
					addAlert={addAlert}
					disabled={false}
					value={token}
				/>

				<WizardPageButtonGroup
					nextButtonLabel={Liferay.Language.get('continue')}
					onCancel={() => {
						history.push(
							toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
								groupId,
							})
						);
					}}
					prevButtonLabel={Liferay.Language.get('cancel')}
				/>
			</ClayForm>
		);
	}

	return (
		<ClayForm
			onSubmit={async (event) => {
				event.preventDefault();

				onNext();
			}}
		>
			<ClayAlert
				displayType="success"
				title={Liferay.Language.get('success')}
			>
				{Liferay.Language.get('token-authenticated-successfully')}
			</ClayAlert>

			<label htmlFor="token">
				<Text weight="semi-bold">
					{sub(
						Liferay.Language.get(
							'copy-this-token-to-your-x-instance'
						),
						[Liferay.Language.get('liferay-dxp')]
					)}
				</Text>
			</label>

			<CopyInputValue addAlert={addAlert} disabled value={token} />

			<WizardPageButtonGroup
				nextButtonLabel={Liferay.Language.get('continue')}
				onCancel={() => {
					open(modalTypes.CONFIRMATION_MODAL, {
						message: (
							<Text as="p" size={4}>
								{sub(
									Liferay.Language.get(
										'this-action-will-stop-syncing-data-from-your-x-instance-to-this-analytics-cloud-workspace.-the-data-that-was-already-synced-will-remain-available-in-the-properties-the-data-source-was-connected-to.-are-you-sure-you-want-to-continue'
									),
									[Liferay.Language.get('liferay-dxp')]
								)}
							</Text>
						),
						modalVariant: 'modal-warning',
						onClose: close,
						onSubmit: async () => {
							try {
								await disconnect({
									groupId,
									id: dataSourceId,
								});

								updateSearchParams(history, 'dataSourceId', '');

								refetchDataSource(dataSourceId);

								addAlert({
									alertType: Alert.Types.Success,
									message: Liferay.Language.get(
										'data-source-disconnected'
									),
								});
							}
							catch (error) {
								addAlert({
									alertType: Alert.Types.Error,
									message: Liferay.Language.get(
										'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists-please-contact-support'
									),
								});
							}
							finally {
								close();
							}
						},
						submitButtonDisplay: 'warning',
						submitMessage: Liferay.Language.get('disconnect'),
						title: Liferay.Language.get('disconnect-data-source'),
						titleIcon: 'warning-full',
					});
				}}
				prevButtonLabel={Liferay.Language.get('disconnect-data-source')}
			/>
		</ClayForm>
	);
};

export {ConnectLiferayDXPStep};
