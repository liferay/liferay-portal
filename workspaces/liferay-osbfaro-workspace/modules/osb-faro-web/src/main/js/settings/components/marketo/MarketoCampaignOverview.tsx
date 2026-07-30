import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'settings/components/base-page/BasePage';
import ClayAlert, {DisplayType} from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import Loading from 'shared/components/Loading';
import MarketoCampaignEntities from './MarketoCampaignEntities';
import React, {useEffect, useRef, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {AssignedPropertiesTable} from '../AssignedPropertiesTable';
import {Card} from 'shared/components/revamping/Card';
import {ClayInput} from '@clayui/form';
import {close, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {ConnectMarketoCampaignAuth} from './ConnectMarketoCampaignAuth';
import {DataSource} from 'shared/util/records';
import {DataSourceEditableTitle} from '../data-source/DataSourceEditableTitle';
import {DataSourceStatuses} from 'shared/util/constants';
import {Entity} from '../3rd-party-connector/types';
import {fetch, updateMarketoCampaign} from 'shared/api/data-source';
import {fetchConnectorEntityCount} from 'shared/api/connector';
import {getDataSourceDisplayObject} from 'shared/util/data-sources';
import {sub} from 'shared/util/lang';
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

interface IMarketoCampaignOverviewProps extends PropsFromRedux {
	dataSource: DataSource;
}

const MarketoCampaignOverview: React.FC<IMarketoCampaignOverviewProps> = ({
	addAlert,
	close,
	dataSource: initialDataSource,
	open,
}) => {
	const [loading, setLoading] = useState(false);
	const [dataSource, setDataSource] = useState(initialDataSource);

	const {groupId = '', id = ''} = useParams<{
		groupId: string;
		id: string;
	}>();
	const currentUser = useCurrentUser();

	type ConnectionAlert = {
		displayType: DisplayType;
		message: string;
	};

	const [alert, setAlert] = useState<ConnectionAlert>({
		displayType: 'success',
		message: '',
	});

	const dataSourceActive = dataSource.status === DataSourceStatuses.Active;

	const enableAllLeads = dataSource.provider?.getIn(
		['contactsConfiguration', 'enableAllLeads'],
		false
	);

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
		const connectionAlert: ConnectionAlert = {
			displayType: 'success',
			message: Liferay.Language.get(
				'you-have-successfully-authenticated-your-credentials-with-liferay-analytics-cloud.-you-can-now-select-the-data-to-sync'
			),
		};

		if (!dataSourceActive) {
			connectionAlert.displayType = 'warning';

			connectionAlert.message = sub(
				Liferay.Language.get(
					'the-data-source-is-disconnected.-data-is-no-longer-being-synced-from-x,-but-you-can-reconnect-to-resume-syncing'
				),
				[Liferay.Language.get('marketo')]
			) as string;
		}
		else if (enableAllLeads) {
			connectionAlert.message = Liferay.Language.get(
				'all-data-coming-from-this-data-source-is-up-to-date.-there-are-no-errors-to-report'
			);
		}

		setAlert(connectionAlert);
	}, [dataSourceActive, enableAllLeads]);

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

	const {display, label} = getDataSourceDisplayObject(dataSource);

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
				displayType={display}
				editable={currentUser.isAdmin()}
				groupId={groupId}
				label={label}
				onUpdateName={async (name: string) => {
					await updateMarketoCampaign({groupId, id, name});

					await handleUpdateDataSource();
				}}
			/>

			<Card title={Liferay.Language.get('authentication')}>
				<div className="mb-4">
					<Card.SubHeader
						title={Liferay.Language.get('connection-status')}
					/>

					{alert && (
						<ClayAlert displayType={alert.displayType}>
							{alert.message}
						</ClayAlert>
					)}

					{!dataSourceActive && (
						<>
							<div className="mb-3">
								<Text color="secondary" size={4}>
									{sub(
										Liferay.Language.get(
											'to-reestablish-the-connection-between-x-and-liferay-analytics-cloud,-check-your-credentials-and-paste-on-the-input-below'
										),
										[Liferay.Language.get('marketo')]
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

							<ConnectMarketoCampaignAuth
								addAlert={addAlert}
								buttonProps={{size: 'sm'}}
								dataSource={dataSource}
								onSubmit={handleUpdateDataSource}
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
								value={Liferay.Language.get('marketo-campaign')}
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
				<SyncedIndividuals
					currentUser={currentUser}
					dataSource={dataSource}
					groupId={groupId}
					loading={loading}
					onSubmit={async ({
						enabledIndividuals,
					}: {
						enabledIndividuals: boolean;
					}) => {
						await updateMarketoCampaign({
							contactsConfiguration: {
								enableAllLeads: enabledIndividuals,
							},
							groupId,
							id: dataSource.id,
						});

						await handleUpdateDataSource();

						addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get(
								'synced-data-settings-have-been-saved'
							),
						});
					}}
				/>
			</Card>

			<Card
				innerPadding={false}
				title={Liferay.Language.get('assigned-properties')}
			>
				<AssignedPropertiesTable
					addAlert={addAlert}
					close={close}
					dataSource={dataSource}
					handleUpdateDataSource={handleUpdateDataSource}
					loading={loading}
					open={open}
					updateDataSourceFn={updateMarketoCampaign}
				/>
			</Card>
		</BasePage>
	);
};

const SyncedIndividuals = ({
	currentUser,
	dataSource,
	groupId,
	loading,
	onSubmit,
}: {
	currentUser: any;
	dataSource: DataSource;
	groupId: string;
	loading: boolean;
	onSubmit: (params: {enabledIndividuals: boolean}) => void;
}) => {
	const MARKETO_COMPANIES_ENTITY = Entity.Accounts;
	const MARKETO_LEADS_ENTITY = Entity.Users;

	const [enabledIndividuals, setEnabledIndividuals] = useState(
		dataSource.provider?.getIn(
			['contactsConfiguration', 'enableAllLeads'],
			false
		)
	);

	const hasChangesRef = useRef<boolean | null>(null);
	const enabledIndividualsPrevValue = useRef(enabledIndividuals);

	const dataSourceActive = dataSource.status === DataSourceStatuses.Active;

	const leadsCountResponse = useRequest({
		dataSourceFn: (params) =>
			fetchConnectorEntityCount(MARKETO_LEADS_ENTITY, params),
		skipRequest: !dataSource.id,
		variables: {groupId, id: dataSource.id ?? ''},
	});

	const companiesCountResponse = useRequest({
		dataSourceFn: (params) =>
			fetchConnectorEntityCount(MARKETO_COMPANIES_ENTITY, params),
		skipRequest: !dataSource.id,
		variables: {groupId, id: dataSource.id ?? ''},
	});

	if (companiesCountResponse.error || leadsCountResponse.error) {
		return <ErrorDisplay />;
	}

	if (companiesCountResponse.loading || leadsCountResponse.loading) {
		return <Loading spacer />;
	}

	const individualsSyncedCount =
		(leadsCountResponse.data ?? 0) + (companiesCountResponse.data ?? 0);

	return (
		<div>
			{!hasChangesRef.current &&
				dataSourceActive &&
				!enabledIndividuals && (
					<ClayAlert displayType="warning" title="Warning">
						{Liferay.Language.get(
							'the-data-source-setup-is-almost-complete.-sync-data-to-start-seeing-results-as-activities-occur-on-your-sites'
						)}
					</ClayAlert>
				)}

			{hasChangesRef.current && (
				<ClayAlert displayType="info">
					{Liferay.Language.get(
						'this-configuration-is-not-saved-yet'
					)}
				</ClayAlert>
			)}

			<div className="mb-2">
				<Text color="secondary" size={4}>
					{sub(
						Liferay.Language.get(
							'to-configure-your-x-data-source,-go-to-your-x-environment-to-update-this-app-connection'
						),
						[Liferay.Language.get('marketo')]
					)}
				</Text>
			</div>

			<div className="mt-3 text-dark">
				<Text size={2} weight="semi-bold">
					{Liferay.Language.get('select-items-to-sync').toUpperCase()}
				</Text>
			</div>

			<MarketoCampaignEntities
				disabled={!dataSourceActive || !currentUser.isAdmin()}
				enabledIndividuals={enabledIndividuals}
				individualsSyncedCount={individualsSyncedCount}
				onIndividualsChange={() => {
					const newValue = !enabledIndividuals;

					setEnabledIndividuals(newValue);

					hasChangesRef.current =
						enabledIndividualsPrevValue.current !== newValue;
				}}
			/>

			{dataSourceActive && currentUser.isAdmin() && (
				<ClayButton
					className="mt-3"
					loading={loading}
					onClick={async () => {
						hasChangesRef.current = false;

						await onSubmit({enabledIndividuals});
					}}
					size="sm"
				>
					{Liferay.Language.get('save')}
				</ClayButton>
			)}
		</div>
	);
};

export default compose(
	connector,
	withSelectionProvider
)(MarketoCampaignOverview);
