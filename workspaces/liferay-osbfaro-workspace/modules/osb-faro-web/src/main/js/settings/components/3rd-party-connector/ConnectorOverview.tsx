import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'settings/components/base-page/BasePage';
import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import ConnectorEntities from './ConnectorEntities';
import Loading from 'shared/components/Loading';
import React, {ComponentType, useEffect, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {AssignedPropertiesTable} from '../AssignedPropertiesTable';
import {Card} from 'shared/components/revamping/Card';
import {close, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {
	ConnectorAvailableDataAlertKind,
	getConnectorAvailableDataAlert,
} from './getConnectorAvailableDataAlert';
import {ConnectorConfig, ConnectorStatus} from './types';
import {
	ConnectorStatusItem,
	getInitialLogEntries,
} from './getConnectorStatusItems';
import {CopyInputValue} from '../CopyInputValue';
import {DataSource} from 'shared/util/records';
import {DataSourceEditableTitle} from '../data-source/DataSourceEditableTitle';
import {DataSourceStatuses} from 'shared/util/constants';
import {fetch} from 'shared/api/data-source';
import {generateConnectorToken, updateConnector} from 'shared/api/connector';
import {getConnectorConnectionStatusAlert} from './getConnectorConnectionStatusAlert';
import {getConnectorStatus} from './getConnectorStatus';
import {getConnectorStatusDisplay} from './getConnectorStatusDisplay';
import {revoke} from 'shared/api/api-tokens';
import {Text} from '@clayui/core';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDisconnectDataSource} from '../data-source/utils';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {withSelectionProvider} from 'shared/context/selection';

const connector = connect(null, {
	addAlert,
	close,
	open,
});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IConnectorOverviewProps extends PropsFromRedux {
	config: ConnectorConfig;
	dataSource: DataSource;
}

const ConnectorOverview: React.FC<IConnectorOverviewProps> = ({
	addAlert,
	close,
	config,
	dataSource: initialDataSource,
	open,
}) => {
	const [loading, setLoading] = useState(false);
	const [dataSource, setDataSource] = useState(initialDataSource);
	const [token, setToken] = useState('');

	const {groupId = '', id = ''} = useParams<{
		groupId: string;
		id: string;
	}>();
	const currentUser = useCurrentUser();

	const endpointURL = `${window.location.origin}${config.endpointPath}`;

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

	const connectorStatus = getConnectorStatus(dataSource);

	useEffect(() => {
		if (connectorStatus === ConnectorStatus.Disconnected) {
			return;
		}

		const fetchConnectorTokenForGroup = async () => {
			try {
				const data = await generateConnectorToken({
					groupId,
					type: config.slug,
				});

				if (data?.token) {
					setToken(data.token);
				}
			}
			catch (error) {
				addAlert({
					alertType: Alert.Types.Error,
					message: (error as Error).message,
					timeout: false,
				});
			}
		};

		fetchConnectorTokenForGroup();
	}, [config.slug, connectorStatus, groupId]);

	const handleGenerateToken = async () => {
		try {
			const data = await generateConnectorToken({
				groupId,
				type: config.slug,
			});

			if (data?.token) {
				setToken(data.token);
			}

			await updateConnector(config.slug, {
				groupId,
				id,
				status: DataSourceStatuses.Active,
			});

			await handleUpdateDataSource();
		}
		catch (error) {
			addAlert({
				alertType: Alert.Types.Error,
				message: (error as Error).message,
				timeout: false,
			});
		}
	};

	const {handleDisconnect} = useDisconnectDataSource({
		addAlert,
		beforeSubmit: async () => {
			if (token) {
				await revoke({groupId, token});
			}
		},
		close,
		groupId,
		id,
		onSubmit: async () => {
			await handleUpdateDataSource();
		},
		open,
	});

	const {display, label} = getConnectorStatusDisplay(dataSource);

	const updateDataSourceFn = (params: {[key: string]: any}) =>
		updateConnector(
			config.slug,
			params as Parameters<typeof updateConnector>[1]
		);

	return (
		<BasePage
			breadcrumbItems={[
				breadcrumbs.getDataSources({groupId}),
				breadcrumbs.getDataSourceName({
					active: true,
					label: dataSource.name ?? '',
				}),
			]}
			documentTitle={Liferay.Language.get('configure-data-source')}
		>
			<DataSourceEditableTitle
				dataSource={dataSource}
				description={`${Liferay.Language.get('id').toUpperCase()}: ${
					dataSource.id
				}`}
				displayType={display}
				editable={currentUser.isAdmin()}
				groupId={groupId}
				label={label}
				onUpdateName={async (name: string) => {
					await updateConnector(config.slug, {groupId, id, name});

					await handleUpdateDataSource();
				}}
			/>

			<Card title={Liferay.Language.get('authentication')}>
				{connectorStatus !== ConnectorStatus.Disconnected ? (
					<>
						<div className="mb-4">
							<Text color="secondary">
								<span className="mr-1">
									{Liferay.Language.get(
										'to-configure-your-data-source-utilize-the-token-and-endpoint-url-provided-by-liferay-data-platform'
									)}
								</span>

								<ClayLink
									href={URLConstants.DataSourceConnection}
									target="_blank"
								>
									<strong>
										{Liferay.Language.get(
											'learn-more-about-data-sources'
										)}
									</strong>

									<ClayIcon
										className="ml-1"
										symbol="shortcut"
									/>
								</ClayLink>
							</Text>
						</div>

						<ClayLayout.Row>
							<ClayLayout.Col size={6}>
								<CopyInputValue
									addAlert={addAlert}
									disabled={false}
									title={Liferay.Language.get('endpoint-url')}
									value={endpointURL}
								/>
							</ClayLayout.Col>

							<ClayLayout.Col size={6}>
								<CopyInputValue
									addAlert={addAlert}
									disabled={false}
									title={Liferay.Language.get('token')}
									value={token}
								/>
							</ClayLayout.Col>
						</ClayLayout.Row>
					</>
				) : (
					<>
						<div className="mb-4">
							<Text color="secondary">
								<span className="mr-1">
									{Liferay.Language.get(
										'generate-a-new-token-to-continue-configuring-this-data-source'
									)}
								</span>

								<ClayLink
									href={URLConstants.DataSourceConnection}
									target="_blank"
								>
									<strong>
										{Liferay.Language.get(
											'learn-more-about-data-sources'
										)}
									</strong>

									<ClayIcon
										className="ml-1"
										symbol="shortcut"
									/>
								</ClayLink>
							</Text>
						</div>

						<ClayButton
							aria-label={Liferay.Language.get('generate-token')}
							displayType="primary"
							onClick={handleGenerateToken}
							size="sm"
						>
							{Liferay.Language.get('generate-token')}
						</ClayButton>
					</>
				)}

				{currentUser.isAdmin() &&
					connectorStatus !== ConnectorStatus.Disconnected && (
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
				<ConnectorEntityList
					config={config}
					dataSource={dataSource}
					groupId={groupId}
				/>
			</Card>

			<Card
				innerPadding={false}
				title={Liferay.Language.get('assigned-properties')}
			>
				<AssignedPropertiesTable
					addAlert={addAlert}
					close={close}
					customColumns={config.columns ?? []}
					dataSource={dataSource}
					handleUpdateDataSource={handleUpdateDataSource}
					loading={loading}
					open={open}
					updateDataSourceFn={updateDataSourceFn}
				/>
			</Card>
		</BasePage>
	);
};

interface IConnectorStatusListProps {
	entries: ConnectorStatusItem[];
}

const ConnectorStatusList: React.FC<IConnectorStatusListProps> = ({
	entries,
}) => (
	<ClayList className="mb-0 mt-3">
		{entries.map(
			({bold, icon, iconDisplayType, secondaryText, title}, index) => (
				<ClayList.Item flex key={index}>
					<ClayList.ItemField>
						<ClaySticker displayType="unstyled">
							<ClayIcon
								className={
									iconDisplayType === 'success'
										? 'text-success'
										: 'text-secondary'
								}
								symbol={icon}
							/>
						</ClaySticker>
					</ClayList.ItemField>

					<ClayList.ItemField expand>
						<ClayList.ItemTitle
							className={
								bold
									? 'font-weight-bold'
									: 'font-weight-normal text-secondary'
							}
						>
							{title}
						</ClayList.ItemTitle>

						<ClayList.ItemText className="text-secondary">
							{secondaryText}
						</ClayList.ItemText>
					</ClayList.ItemField>
				</ClayList.Item>
			)
		)}
	</ClayList>
);

interface IConnectorEntityListProps {
	config: ConnectorConfig;
	dataSource: DataSource;
	groupId: string;
}

const getAvailableDataAlertStorageKey = (
	kind: ConnectorAvailableDataAlertKind,
	dataSourceId: string
) => `connector-overview:${kind}-alert-dismissed:${dataSourceId}`;

const ConnectorEntityList: React.FC<IConnectorEntityListProps> = ({
	config,
	dataSource,
	groupId,
}) => {
	const dataSourceId = dataSource.id ?? '';

	const [syncingAlertDismissed, setSyncingAlertDismissed] = useState(
		() =>
			window.localStorage.getItem(
				getAvailableDataAlertStorageKey('syncing', dataSourceId)
			) === 'true'
	);

	const [previouslySyncedAlertDismissed, setPreviouslySyncedAlertDismissed] =
		useState(
			() =>
				window.localStorage.getItem(
					getAvailableDataAlertStorageKey(
						'previously-synced',
						dataSourceId
					)
				) === 'true'
		);

	const handleDismissAvailableDataAlert = (
		kind: ConnectorAvailableDataAlertKind
	) => {
		window.localStorage.setItem(
			getAvailableDataAlertStorageKey(kind, dataSourceId),
			'true'
		);

		if (kind === 'syncing') {
			setSyncingAlertDismissed(true);
		}
		else {
			setPreviouslySyncedAlertDismissed(true);
		}
	};

	const countResponse = useRequest({
		dataSourceFn: async (params) => {
			const entries = await Promise.all(
				config.entities.map(async ({entity, fetchCount}) => {
					if (!fetchCount) {
						return [entity, 0] as const;
					}

					try {
						const count = await fetchCount({
							groupId: params.groupId,
							id: params.id!,
						});

						return [entity, count ?? 0] as const;
					}
					catch (error) {
						return [entity, 0] as const;
					}
				})
			);

			return Object.fromEntries(entries);
		},
		variables: {groupId, id: dataSource.id},
	});

	const counts = (countResponse.data ?? {}) as {
		[entity: string]: number | undefined;
	};

	const syncedCounts: {[entity: string]: number | undefined} = {};

	config.entities.forEach(({entity}) => {
		syncedCounts[entity] = counts[entity] ?? 0;
	});

	const totalCount = Object.values(counts).reduce<number>(
		(sum, c) => sum + (typeof c === 'number' ? c : 0),
		0
	);
	const hasData = totalCount > 0;

	const connectorStatus = getConnectorStatus(dataSource);

	if (countResponse.loading) {
		return <Loading spacer />;
	}

	const logEntries: ConnectorStatusItem[] = getInitialLogEntries(
		connectorStatus,
		totalCount
	);

	const connectionStatusAlert = getConnectorConnectionStatusAlert(
		dataSource,
		totalCount
	);

	const availableDataAlert = getConnectorAvailableDataAlert(
		dataSource,
		hasData
	);

	return (
		<div>
			<div className="mb-4">
				<Card.SubHeader
					title={Liferay.Language.get('connection-status')}
				/>

				<ClayAlert
					className="mt-3"
					displayType={connectionStatusAlert.displayType}
				>
					{connectionStatusAlert.message}
				</ClayAlert>

				<ConnectorStatusList entries={logEntries} />
			</div>

			<div>
				<Card.SubHeader
					title={Liferay.Language.get('available-data')}
				/>

				{availableDataAlert &&
					!(availableDataAlert.kind === 'syncing'
						? syncingAlertDismissed
						: previouslySyncedAlertDismissed) && (
						<ClayAlert
							displayType={availableDataAlert.displayType}
							onClose={() =>
								handleDismissAvailableDataAlert(
									availableDataAlert.kind
								)
							}
						>
							{availableDataAlert.message}
						</ClayAlert>
					)}

				<ConnectorEntities
					connectorStatus={connectorStatus}
					entities={config.entities}
					syncedCounts={syncedCounts}
				/>
			</div>
		</div>
	);
};

export default compose(
	connector,
	withSelectionProvider
)(ConnectorOverview) as ComponentType<{
	config: ConnectorConfig;
	dataSource: DataSource;
}>;
