import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'settings/components/base-page/BasePage';
import ClayAlert, {DisplayType} from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import React, {useEffect, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {AssignedPropertiesTable} from '../AssignedPropertiesTable';
import {Card} from 'shared/components/revamping/Card';
import {ClayInput} from '@clayui/form';
import {close, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {CopyInputValue} from '../CopyInputValue';
import {DataSource} from 'shared/util/records';
import {DataSourceEditableTitle} from '../data-source/DataSourceEditableTitle';
import {DataSourceStatuses} from 'shared/util/constants';
import {
	fetch,
	fetchChannelsMetric,
	fetchToken,
	updateLiferay,
} from 'shared/api/data-source';
import {getDataSourceDisplayObject} from 'shared/util/data-sources';
import {ReviewSyncedDataFragment} from './ReviewSyncedDataFragment';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDisconnectDataSource} from '../data-source/utils';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const TIMEOUT_INTERVAL = 5000;

const connector = connect(null, {
	addAlert,
	close,
	open,
});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface ILiferayeOverviewProps extends PropsFromRedux {
	dataSource: DataSource;
}

const LiferayOverview: React.FC<ILiferayeOverviewProps> = ({
	addAlert,
	close,
	dataSource: initialDataSource,
	open,
}) => {
	const [dataSource, setDataSource] = useState(initialDataSource);
	const [, setLoading] = useState(false);
	const {groupId = '', id = ''} = useParams<{groupId: string; id: string}>();
	const currentUser = useCurrentUser();
	const [token, setToken] = useState('');

	const {display, label} = getDataSourceDisplayObject(dataSource);

	type Alert = {
		displayType: DisplayType;
		message: string;
	};

	const [alert, setAlert] = useState<Alert>({
		displayType: 'success',
		message: '',
	});

	const dataSourceActive = dataSource.status === DataSourceStatuses.Active;

	const {handleDisconnect} = useDisconnectDataSource({
		addAlert,
		close,
		groupId,
		id,
		onSubmit: async () => {
			await handleUpdateDataSource();
		},
		open,
	});

	const handleUpdateDataSource = async () => {
		try {
			setLoading(true);

			const newDataSource = await fetch({
				groupId,
				id,
			});

			setDataSource(new DataSource(newDataSource));
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
		}
	};

	useEffect(() => {
		const alert: Alert = {
			displayType: 'success',
			message: Liferay.Language.get(
				'you-have-successfully-authenticated-your-token-with-liferay-analytics-cloud.-you-can-now-select-the-data-to-sync'
			),
		};

		if (!dataSourceActive) {
			alert.displayType = 'warning';

			alert.message = sub(
				Liferay.Language.get(
					'the-data-source-is-disconnected.-data-is-no-longer-being-synced-from-x,-but-you-can-reconnect-to-resume-syncing'
				),
				[Liferay.Language.get('liferay-dxp')]
			) as string;
		}
		else if (dataSource?.sitesSelected || dataSource?.contactsSelected) {
			alert.message = Liferay.Language.get(
				'all-data-coming-from-this-data-source-is-up-to-date.-there-are-no-errors-to-report'
			);
		}

		setAlert(alert);
	}, [dataSource, dataSourceActive]);

	let _tokenRequest:
		| ReturnType<typeof setTimeout>
		| Promise<string | void>
		| undefined;

	const getNextToken = async (prevToken?: string) => {
		const nextToken = await fetchToken(groupId, id);

		if (!prevToken || prevToken === nextToken) {
			_tokenRequest = setTimeout(
				() => getNextToken(nextToken),
				TIMEOUT_INTERVAL
			);
		}
		else {
			handleUpdateDataSource();
		}

		return nextToken;
	};

	useEffect(() => {
		if (!dataSourceActive) {
			_tokenRequest = getNextToken().then(setToken);
		}

		return () => {
			clearTimeout(_tokenRequest as ReturnType<typeof setTimeout>);
		};
	}, [dataSourceActive]);

	const {data: channelsMetric} = useRequest({
		dataSourceFn: fetchChannelsMetric,
		variables: {groupId, id},
	});

	return (
		<BasePage
			breadcrumbItems={[
				breadcrumbs.getDataSources({groupId}),
				breadcrumbs.getDataSourceName({
					active: true,
					label: dataSource.name || '',
				}),
			]}
			documentTitle={Liferay.Language.get('configure-data-source')}
		>
			<DataSourceEditableTitle
				dataSource={dataSource}
				displayType={display}
				editable={currentUser?.isAdmin()}
				groupId={groupId}
				label={label}
				onUpdateName={async (name: string) => {
					await updateLiferay({groupId, id, name} as any);

					await handleUpdateDataSource();
				}}
			/>

			<Card title={Liferay.Language.get('authentication')}>
				<div className="mb-4">
					<Card.SubHeader
						title={Liferay.Language.get('connection-status')}
					/>

					{alert && (
						<ClayAlert displayType={alert.displayType as any}>
							{alert.message}
						</ClayAlert>
					)}

					{!dataSourceActive && (
						<>
							<div className="mb-4">
								<Text color="secondary" size={4}>
									{Liferay.Language.get(
										'to-reestablish-the-connection-between-the-liferay-dxp-instance-and-liferay-analytics-cloud,-copy-the-token-below-and-go-to-dxp-instance-settings-analytics-cloud-to-continue-the-data-source-configuration'
									)}
								</Text>

								<ClayLink
									className="ml-1"
									href={URLConstants.HelpConnectDxp}
									key="DOCUMENTATION"
									target="_blank"
								>
									{Liferay.Language.get(
										'learn-more-about-data-sources'
									)}
								</ClayLink>
							</div>

							<label htmlFor="token">
								<Text weight="semi-bold">
									{Liferay.Language.get(
										'analytics-cloud-token'
									)}
								</Text>

								<div>
									<Text color="secondary" weight="normal">
										{Liferay.Language.get(
											'copy-this-token-to-the-dxp-instance-you-would-like-to-connect'
										)}
									</Text>
								</div>
							</label>

							<CopyInputValue
								addAlert={addAlert}
								disabled={false}
								value={token}
							/>
						</>
					)}
				</div>

				<div className="mb-4">
					<Card.SubHeader
						title={Liferay.Language.get('data-source-details')}
					/>

					<ClayInput.Group className="d-flex mt-3">
						<ClayInput.GroupItem className="mr-3" shrink>
							<label htmlFor="dataSourceType">
								{Liferay.Language.get('data-source-type')}
							</label>

							<ClayInput
								readOnly
								type="text"
								value={Liferay.Language.get('liferay-portal')}
							/>
						</ClayInput.GroupItem>

						<ClayInput.GroupItem className="ml-0" shrink>
							<label htmlFor="dataSourceId">
								{Liferay.Language.get('data-source-id')}
							</label>

							<ClayInput
								readOnly
								type="text"
								value={dataSource.id}
							/>
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</div>

				{currentUser.isAdmin() && dataSourceActive && (
					<ClayButton
						aria-label={Liferay.Language.get(
							'disconnect-data-source'
						)}
						displayType="danger"
						onClick={handleDisconnect}
						outline
						size="sm"
					>
						<ClayIcon className="mr-2" symbol="logout" />

						{Liferay.Language.get('disconnect-data-source')}
					</ClayButton>
				)}
			</Card>

			<Card title={Liferay.Language.get('synced-data')}>
				<ReviewSyncedDataFragment
					channelsMetric={channelsMetric}
					contactsSelected={dataSource.contactsSelected}
					sitesSelected={dataSource.sitesSelected}
				/>
			</Card>

			<Card
				innerPadding={false}
				title={Liferay.Language.get('assigned-properties')}
			>
				<AssignedPropertiesTable
					addAlert={addAlert}
					close={close}
					customColumns={[
						{
							accessor: 'groupsCount',
							className: 'text-right',
							label: Liferay.Language.get('sites'),
							sortable: false,
						},
						{
							accessor: 'individualDataSourcesCount',
							className: 'text-right',
							label: Liferay.Language.get('individuals'),
							sortable: false,
						},
					]}
					dataSource={dataSource}
					handleUpdateDataSource={handleUpdateDataSource}
					open={open}
					updateDataSourceFn={
						updateLiferay as (params: {
							[key: string]: any;
						}) => Promise<any>
					}
				/>
			</Card>
		</BasePage>
	);
};

export default compose<React.ComponentType<any>>(connector)(LiferayOverview);
