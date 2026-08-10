import * as API from 'shared/api';
import BasePage from 'settings/components/base-page/BasePage';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import Label from 'shared/components/Label';
import ListComponent from 'shared/hoc/ListComponent';
import NoResultsDisplay, {
	getFormattedTitle,
} from 'shared/components/NoResultsDisplay';
import React, {useEffect, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {AlertTypes} from 'shared/components/Alert';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import {
	CREATE_DATE,
	createOrderIOMap,
	NAME,
	PROVIDER_TYPE,
} from 'shared/util/pagination';
import {DataSource} from 'shared/util/records';
import {DataSourceStates, DataSourceTypes, Sizes} from 'shared/util/constants';
import {formatDateToTimeZone, getCustomDateFormat} from 'shared/util/date';
import {fromJS} from 'immutable';
import {get} from 'lodash';
import {
	getConnectorConfig,
	listAvailableConnectors,
} from 'settings/components/3rd-party-connector/registry';
import {getConnectorStatusDisplay} from 'settings/components/3rd-party-connector/getConnectorStatusDisplay';
import {getDataSourceDisplayObject} from 'shared/util/data-sources';
import {isLDPPlan} from 'shared/util/subscriptions';
import {Link, useHistory, useParams} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';
import {useSubscriptionName} from 'shared/hooks/useSubscriptionName';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface StandaloneDataSourceDescriptor {
	label: string;
	requiresLDP?: boolean;
	type: DataSourceTypes;
}

const STANDALONE_DATA_SOURCES: StandaloneDataSourceDescriptor[] = [
	{
		label: Liferay.Language.get('liferay-dxp'),
		type: DataSourceTypes.Liferay,
	},
	{
		label: Liferay.Language.get('marketo-campaign'),
		requiresLDP: true,
		type: DataSourceTypes.MarketoCampaign,
	},
	{
		label: Liferay.Language.get('salesforce'),
		requiresLDP: true,
		type: DataSourceTypes.Salesforce,
	},
];

interface ICellProps {
	data: {[key: string]: any};
}

interface IDataSourceNameProps {
	data: {[key: string]: any};
	hrefFormatter: (params: {[key: string]: any}) => string;
}

export const DataSourceName: React.FC<IDataSourceNameProps> = ({
	data,
	hrefFormatter,
}) => (
	<td className="table-cell-expand">
		<div className="table-title">
			{disableRow(
				data as {[key: string]: any; state: DataSourceStates}
			) ? (
				<span className="text-truncate">{data.name}</span>
			) : (
				<Link className="text-truncate" to={hrefFormatter(data)}>
					{data.name}
				</Link>
			)}
		</div>
	</td>
);

export const StatusRenderer: React.FC<ICellProps> = ({data}) => {
	const dataSource = new DataSource(fromJS(data));

	const {display, label} = getConnectorConfig(data.providerType)
		? getConnectorStatusDisplay(dataSource)
		: getDataSourceDisplayObject(dataSource);

	return (
		<td>
			<Label display={display} uppercase>
				{label}
			</Label>
		</td>
	);
};

const dateFormatter = (date: string, timeZoneId: string): string =>
	formatDateToTimeZone(date, getCustomDateFormat(), timeZoneId);

export const disableRow = ({state}: {state: DataSourceStates}): boolean =>
	state === DataSourceStates.InProgressDeleting;

const getAlertMessage = (
	dataSource: {[key: string]: any},
	currentUser: {isAdmin: () => boolean},
	count: number,
	groupId: string
) => {
	const admin = currentUser.isAdmin();

	const {credentials, id, name} = dataSource;

	const email = get(credentials, ['oAuthOwner', 'emailAddress']);

	if (admin && count === 1) {
		return sub(
			Liferay.Language.get(
				'your-authorization-token-for-x-has-expired.-please-x-your-account-credentials'
			),
			[
				<b key="NAME">{name}</b>,
				<Link
					key="REAUTHORIZE"
					to={toRoute(Routes.SETTINGS_DATA_SOURCE, {
						groupId,
						id,
					})}
				>
					{Liferay.Language.get('reauthorize').toLowerCase()}
				</Link>,
			],
			false
		);
	}
	else if (admin && count > 1) {
		return Liferay.Language.get(
			'some-of-your-authorization-tokens-have-expired.-please-reauthorize-the-account-credentials-on-these-data-sources-to-prevent-syncing-interruptions'
		);
	}
	else if (count === 1) {
		return sub(
			Liferay.Language.get(
				'your-authorization-token-for-x-has-expired.-please-contact-your-oauth-administrator,-x,-to-reauthorize'
			),
			[<b key="NAME">{name}</b>, email],
			false
		);
	}
	else if (count > 1) {
		return Liferay.Language.get(
			'some-of-your-authorization-tokens-have-expired.-please-contact-your-oauth-administrator-to-reauthorize'
		);
	}
};

export const typeFormatter = (type: DataSourceTypes): string => {
	switch (type) {
		case DataSourceTypes.Csv:
			return Liferay.Language.get('.csv');
		case DataSourceTypes.Liferay:
			return Liferay.Language.get('liferay-portal');
		case DataSourceTypes.MarketoCampaign:
			return Liferay.Language.get('marketo-campaign');
		case DataSourceTypes.Salesforce:
			return Liferay.Language.get('salesforce');
		default:
			return getConnectorConfig(type)?.displayName ?? '';
	}
};

interface IDataSourceNavItem {
	label: string;
	onClick: () => void;
}

interface IAddDataSourceNavProps {
	items: IDataSourceNavItem[];
}

export const AddDataSourceNav: React.FC<IAddDataSourceNavProps> = ({items}) => {
	if (items.length === 1) {
		const [{onClick}] = items;

		return (
			<ClayButton displayType="primary" onClick={onClick} size="sm">
				{Liferay.Language.get('add-data-source')}
			</ClayButton>
		);
	}

	return (
		<ClayDropDownWithItems
			items={items}
			trigger={
				<ClayButton displayType="primary" size="sm">
					{Liferay.Language.get('add-data-source')}

					<ClayIcon className="ml-2" symbol="caret-bottom" />
				</ClayButton>
			}
		/>
	);
};

interface IDataSourceListProps extends React.HTMLAttributes<HTMLElement> {}

const DataSourceList: React.FC<IDataSourceListProps> = ({className}) => {
	const currentUser = useCurrentUser();
	const history = useHistory();
	const {groupId = ''} = useParams<{groupId: string}>();
	const [alerts, setAlerts] = useState<
		{
			iconSymbol: string;
			message: React.ReactNode;
			title: string;
			type: AlertTypes;
		}[]
	>([]);
	const subscriptionName = useSubscriptionName({groupId});
	const {timeZoneId} = useTimeZone();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const {data: invalidDataSources, loading: invalidDataSourcesLoading} =
		useRequest({
			dataSourceFn: API.dataSource.search,
			variables: {
				delta: 1,
				groupId,
				page: 1,
				states: [
					DataSourceStates.CredentialsInvalid,
					DataSourceStates.UrlInvalid,
				],
			},
		});

	useEffect(() => {
		if (invalidDataSources?.total) {
			setAlerts([
				{
					iconSymbol: 'warning-full',
					message: getAlertMessage(
						invalidDataSources?.items[0],
						currentUser,
						invalidDataSources?.total,
						groupId
					),
					title: Liferay.Language.get('warning'),
					type: AlertTypes.Warning,
				},
			]);
		}
	}, [invalidDataSourcesLoading]);

	const {data, error, loading} = useRequest({
		dataSourceFn: API.dataSource.search,
		variables: {
			delta,
			groupId,
			orderIOMap,
			page,
			query,
		},
	});

	const existingConnectorTypes = new Set<string>(
		(data?.items ?? []).map(
			(item: {provider: {type: string}}) => item.provider.type
		)
	);

	const ldpAllowed = isLDPPlan(subscriptionName);

	const connectorItems = listAvailableConnectors(
		existingConnectorTypes,
		subscriptionName
	).map((config) => ({
		label: config.displayName,
		onClick: () => {
			history.push(
				toRoute(Routes.SETTINGS_DATA_SOURCE_ONBOARDING, {
					groupId,
					id: config.type,
				})
			);
		},
	}));

	const dataSourceItems = STANDALONE_DATA_SOURCES.filter(
		({requiresLDP}) => !requiresLDP || ldpAllowed
	).map(({label, type}) => ({
		label,
		onClick: () => {
			history.push(
				toRoute(Routes.SETTINGS_DATA_SOURCE_ONBOARDING, {
					groupId,
					id: type,
				})
			);
		},
	}));

	const renderAddDataSourceNav = () => (
		<AddDataSourceNav items={[...dataSourceItems, ...connectorItems]} />
	);

	const renderNoResults = () => {
		const authorized = currentUser.isAdmin();

		const connectMessage = authorized ? (
			<>
				{Liferay.Language.get('add-a-data-source-to-get-started')}

				<ClayLink
					className="d-block mb-3"
					href={URLConstants.DataSourceConnection}
					key="DOCUMENTATION"
					target="_blank"
				>
					{Liferay.Language.get(
						'access-our-documentation-to-learn-more'
					)}
				</ClayLink>
			</>
		) : (
			Liferay.Language.get(
				'please-contact-your-workspace-administrator-to-add-data-sources'
			)
		);

		if (query) {
			return (
				<NoResultsDisplay
					icon={{symbol: 'sheets'}}
					title={getFormattedTitle(
						Liferay.Language.get('data-sources')
					)}
				/>
			);
		}
		else {
			return (
				<NoResultsDisplay
					description={connectMessage}
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite',
					}}
					primary
					title={Liferay.Language.get('no-data-sources-connected')}
				/>
			);
		}
	};

	return (
		<BasePage
			className={className}
			key="dataSourceListpage"
			pageDescription={
				ldpAllowed
					? Liferay.Language.get(
							'manage-and-connect-data-sources-to-bring-in-data-from-various-sources-into-liferay-data-platform'
						)
					: Liferay.Language.get(
							'manage-and-connect-data-sources-to-bring-in-data-from-various-sources-into-liferay-analytics-cloud'
						)
			}
			pageTitle={Liferay.Language.get('data-sources')}
		>
			<EmbeddedAlertList alerts={alerts} />

			<Card>
				<ListComponent
					checkDisabled={disableRow}
					columns={[
						{
							accessor: 'name',
							cellRenderer: DataSourceName,
							cellRendererProps: {
								hrefFormatter: (dataSource: {id: string}) =>
									toRoute(Routes.SETTINGS_DATA_SOURCE, {
										groupId,
										id: dataSource.id,
									}),
							},
							label: Liferay.Language.get('data-source-name'),
						},
						{
							accessor: PROVIDER_TYPE,
							dataFormatter: typeFormatter,
							label: Liferay.Language.get('type'),
						},
						{
							cellRenderer: StatusRenderer,
							label: Liferay.Language.get('status'),
							sortable: false,
						},
						{
							accessor: CREATE_DATE,
							dataFormatter: (date: string) =>
								dateFormatter(date, timeZoneId),
							label: Liferay.Language.get('date-added'),
						},
					]}
					delta={delta}
					entityLabel={Liferay.Language.get('data-sources')}
					error={error}
					items={data?.items}
					loading={loading}
					noResultsRenderer={renderNoResults()}
					orderByOptions={[
						{
							label: Liferay.Language.get('name'),
							value: NAME,
						},
						{
							label: Liferay.Language.get('source'),
							value: PROVIDER_TYPE,
						},
						{
							label: Liferay.Language.get('date-added'),
							value: CREATE_DATE,
						},
					]}
					orderIOMap={orderIOMap}
					page={page}
					query={query}
					renderNav={
						currentUser.isAdmin() ? renderAddDataSourceNav : null
					}
					rowIdentifier="id"
					showCheckbox={false}
					total={data?.total}
				/>
			</Card>
		</BasePage>
	);
};

export default DataSourceList;
