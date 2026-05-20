import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'settings/components/base-page/BasePage';
import ClayAlert, {DisplayType} from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import Loading from 'shared/components/Loading';
import React, {useEffect, useRef, useState} from 'react';
import SalesforceAccountsAndIndividuals from './SalesforceAccountsAndIndividuals';
import URLConstants from 'shared/util/url-constants';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {AssignedPropertiesTable} from '../AssignedPropertiesTable';
import {Card} from 'shared/components/revamping/Card';
import {ClayInput} from '@clayui/form';
import {close, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {ConnectSalesforceAuth} from './ConnectSalesforceAuth';
import {DataSource} from 'shared/util/records';
import {DataSourceEditableTitle} from '../data-source/DataSourceEditableTitle';
import {DataSourceStatuses} from 'shared/util/constants';
import {Entity} from '../3rd-party-connector/types';
import {fetch, updateSalesforce} from 'shared/api/data-source';
import {fetchConnectorEntityCount} from 'shared/api/connector';
import {getDataSourceDisplayObject} from 'shared/util/data-sources';
import {Text} from '@clayui/core';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDisconnectDataSource} from '../data-source/utils';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {withSelectionProvider} from 'shared/context/selection';

const connector = connect(null, {
	addAlert,
	close,
	open
});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface ISalesforceOverviewProps extends PropsFromRedux {
	dataSource: DataSource;
}

const SalesforceOverview: React.FC<ISalesforceOverviewProps> = ({
	addAlert,
	close,
	dataSource: initialDataSource,
	open
}) => {
	const [loading, setLoading] = useState(false);
	const [dataSource, setDataSource] = useState(initialDataSource);

	const {groupId = '', id = ''} = useParams<{
		groupId: string;
		id: string;
	}>();
	const currentUser = useCurrentUser();

	type Alert = {
		displayType: DisplayType;
		message: string;
	};

	const [alert, setAlert] = useState<Alert>({
		displayType: 'success',
		message: ''
	});

	const dataSourceActive = dataSource.status === DataSourceStatuses.Active;

	const enabledAllAccounts = dataSource.provider?.getIn(
		['accountsConfiguration', 'enableAllAccounts'],
		false
	);

	const enabledAllContacts = dataSource.provider?.getIn(
		['contactsConfiguration', 'enableAllContacts'],
		false
	);

	const enableAllLeads = dataSource.provider?.getIn(
		['contactsConfiguration', 'enableAllLeads'],
		false
	);

	const handleUpdateDataSource = async () => {
		try {
			setLoading(true);

			const newDataSource = await fetch({
				groupId,
				id
			});

			setDataSource(new DataSource(newDataSource));
		} catch (error) {
			addAlert({
				alertType: Alert.Types.Error,
				message: Liferay.Language.get(
					'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists,-please-contact-support'
				)
			});
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		const alert: Alert = {
			displayType: 'success',
			message: Liferay.Language.get(
				'you-have-successfully-authenticated-your-token-with-liferay-analytics-cloud.-you-can-now-select-the-data-to-sync'
			)
		};

		if (!dataSourceActive) {
			alert.displayType = 'warning';

			alert.message = Liferay.Language.get(
				'the-data-source-is-disconnected.-data-is-no-longer-being-synced-from-salesforce,-but-you-can-reconnect-to-resume-syncing'
			);
		} else if (enabledAllAccounts || enabledAllContacts || enableAllLeads) {
			alert.message = Liferay.Language.get(
				'all-data-coming-from-this-data-source-is-up-to-date.-there-are-no-errors-to-report'
			);
		}

		setAlert(alert);
	}, [
		dataSourceActive,
		enableAllLeads,
		enabledAllAccounts,
		enabledAllContacts
	]);

	const {handleDisconnect} = useDisconnectDataSource({
		addAlert,
		close,
		groupId,
		id,
		onSubmit: async () => {
			await handleUpdateDataSource();
		},
		open
	});

	const {display, label} = getDataSourceDisplayObject(dataSource);

	return (
		<BasePage
			breadcrumbItems={[
				breadcrumbs.getDataSources({groupId}),
				breadcrumbs.getDataSourceName({
					active: true,
					label: dataSource.name ?? ''
				})
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
					await updateSalesforce({groupId, id, name} as any);

					await handleUpdateDataSource();
				}}
			/>

			<Card title={Liferay.Language.get('authentication')}>
				<div className='mb-4'>
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
							<div className='mb-3'>
								<Text color='secondary' size={4}>
									{Liferay.Language.get(
										'to-reestablish-the-connection-between-salesforce-and-liferay-analytics-cloud,-generate-a-token-and-paste-the-code-on-the-input-below'
									)}
								</Text>

								<ClayLink
									className='ml-1'
									href={URLConstants.HelpConnectDxp}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'learn-more-about-data-sources'
									)}
								</ClayLink>
							</div>

							<ConnectSalesforceAuth
								addAlert={addAlert as unknown as Alert.AddAlert}
								buttonProps={{size: 'sm'}}
								dataSource={dataSource}
								onSubmit={handleUpdateDataSource}
							/>
						</>
					)}
				</div>

				<div className='mb-4'>
					<Card.SubHeader
						title={Liferay.Language.get('data-source-details')}
					/>

					<ClayInput.Group className='d-flex mt-3'>
						<ClayInput.GroupItem className='mr-3' shrink>
							<label htmlFor='dataSourceType'>
								{Liferay.Language.get('data-source-type')}
							</label>

							<ClayInput
								readOnly
								type='text'
								value={Liferay.Language.get('salesforce')}
							/>
						</ClayInput.GroupItem>

						<ClayInput.GroupItem className='ml-0' shrink>
							<label htmlFor='dataSourceId'>
								{Liferay.Language.get('data-source-id')}
							</label>

							<ClayInput
								readOnly
								type='text'
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
						displayType='danger'
						onClick={handleDisconnect}
						outline
						size='sm'
					>
						<ClayIcon className='mr-2' symbol='logout' />

						{Liferay.Language.get('disconnect-data-source')}
					</ClayButton>
				)}
			</Card>

			<Card title={Liferay.Language.get('synced-data')}>
				<AccountAndIndividuals
					currentUser={currentUser}
					dataSource={dataSource}
					groupId={groupId}
					loading={loading}
					onSubmit={async ({
						enabledAllAccounts,
						enabledAllIndividuals
					}: {
						enabledAllAccounts: boolean;
						enabledAllIndividuals: boolean;
					}) => {
						await updateSalesforce({
							accountsConfiguration: {
								enableAllAccounts: enabledAllAccounts
							},
							contactsConfiguration: {
								enableAllContacts: enabledAllIndividuals,
								enableAllLeads: enabledAllIndividuals
							},
							groupId,
							id: dataSource.id
						} as any);

						await handleUpdateDataSource();

						addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get(
								'synced-data-settings-have-been-saved'
							)
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
					updateDataSourceFn={
						updateSalesforce as (params: {
							[key: string]: any;
						}) => Promise<any>
					}
				/>
			</Card>
		</BasePage>
	);
};

const AccountAndIndividuals = ({
	currentUser,
	dataSource,
	groupId,
	loading,
	onSubmit
}: {
	currentUser: any;
	dataSource: DataSource;
	groupId: string;
	loading: boolean;
	onSubmit: (params: {
		enabledAllAccounts: boolean;
		enabledAllIndividuals: boolean;
	}) => void;
}) => {
	const [enabledAllAccounts, setEnabledAllAccount] = useState(
		dataSource.provider?.getIn(
			['accountsConfiguration', 'enableAllAccounts'],
			false
		)
	);

	const [enabledAllIndividuals, setEnabledAllIndividuals] = useState(
		dataSource.provider?.getIn(
			['contactsConfiguration', 'enableAllContacts'],
			false
		) ||
			dataSource.provider?.getIn(
				['contactsConfiguration', 'enableAllLeads'],
				false
			)
	);

	const accountsCountResponse = useRequest({
		dataSourceFn: params =>
			fetchConnectorEntityCount(Entity.Accounts, params),
		variables: {groupId, id: dataSource.id!}
	});

	const userCountResponse = useRequest({
		dataSourceFn: params => fetchConnectorEntityCount(Entity.Users, params),
		variables: {groupId, id: dataSource.id!}
	});

	const hasChangesRef = useRef<boolean | null>(null);
	const enabledAllAccountsPrevValue = useRef(enabledAllAccounts);
	const enabledAllIndividualsPrevValue = useRef(enabledAllIndividuals);

	const dataSourceActive = dataSource.status === DataSourceStatuses.Active;

	if (accountsCountResponse.error || userCountResponse.error) {
		return <ErrorDisplay />;
	}

	if (accountsCountResponse.loading || userCountResponse.loading) {
		return <Loading spacer />;
	}

	return (
		<div>
			{!hasChangesRef.current &&
				dataSourceActive &&
				!enabledAllAccounts &&
				!enabledAllIndividuals && (
					<ClayAlert displayType='warning' title='Warning'>
						{Liferay.Language.get(
							'the-data-source-setup-is-almost-complete.-sync-data-to-start-seeing-results-as-activities-occur-on-your-sites'
						)}
					</ClayAlert>
				)}

			{hasChangesRef.current && (
				<ClayAlert displayType='info'>
					{Liferay.Language.get(
						'this-configuration-is-not-saved-yet'
					)}
				</ClayAlert>
			)}

			<div className='mb-2'>
				<Text color='secondary' size={4}>
					{Liferay.Language.get(
						'to-configure-your-salesforce-data-source,-go-to-your-salesforce-environment-to-update-this-app-connection'
					)}
				</Text>
			</div>

			<div className='mt-3 text-dark'>
				<Text size={2} weight='semi-bold'>
					{Liferay.Language.get('select-items-to-sync').toUpperCase()}
				</Text>
			</div>

			<SalesforceAccountsAndIndividuals
				accountsSyncedCount={accountsCountResponse.data}
				disabled={!dataSourceActive || !currentUser.isAdmin()}
				enabledAccount={enabledAllAccounts}
				enabledIndividual={enabledAllIndividuals}
				individualsSyncedCount={userCountResponse.data}
				onAccountChange={() => {
					const newValue = !enabledAllAccounts;

					setEnabledAllAccount(newValue);

					hasChangesRef.current =
						enabledAllAccountsPrevValue.current !== newValue ||
						enabledAllIndividualsPrevValue.current !==
							enabledAllIndividuals;
				}}
				onIndividualChange={() => {
					const newValue = !enabledAllIndividuals;

					setEnabledAllIndividuals(newValue);

					hasChangesRef.current =
						enabledAllIndividualsPrevValue.current !== newValue ||
						enabledAllAccountsPrevValue.current !==
							enabledAllAccounts;
				}}
			/>

			{dataSourceActive && currentUser.isAdmin() && (
				<ClayButton
					className='mt-3'
					loading={loading}
					onClick={async () => {
						hasChangesRef.current = false;

						await onSubmit({
							enabledAllAccounts,
							enabledAllIndividuals
						});
					}}
					size='sm'
				>
					{Liferay.Language.get('save')}
				</ClayButton>
			)}
		</div>
	);
};

export default compose(connector, withSelectionProvider)(SalesforceOverview);
