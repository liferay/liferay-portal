import * as API from 'shared/api';
import BasePage from 'settings/components/BasePage';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import Label from 'shared/components/Label';
import ListComponent from 'shared/hoc/ListComponent';
import Nav from 'shared/components/Nav';
import NoResultsDisplay, {
	getFormattedTitle
} from 'shared/components/NoResultsDisplay';
import React, {useEffect, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	CREATE_DATE,
	createOrderIOMap,
	NAME,
	PROVIDER_TYPE
} from 'shared/util/pagination';
import {DataSource} from 'shared/util/records';
import {
	DataSourceStates,
	DataSourceStatuses,
	DataSourceTypes,
	Sizes
} from 'shared/util/constants';
import {formatDateToTimeZone} from 'shared/util/date';
import {fromJS} from 'immutable';
import {get} from 'lodash';
import {
	getDataSourceDisplayObject,
	validAnalyticsConfig,
	validContactsConfig
} from 'shared/util/data-sources';
import {Link, useParams} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface ICellProps {
	data: {[key: string]: any};
}

const AnalyticsCell: React.FC<ICellProps> = ({data}) => (
	<td>
		{validAnalyticsConfig(new DataSource(fromJS(data))) && (
			<ClayIcon className='icon-root' symbol='check' />
		)}
	</td>
);

const ContactsCell: React.FC<ICellProps> = ({data}) => (
	<td>
		{validContactsConfig(new DataSource(fromJS(data))) &&
			data.status === DataSourceStatuses.Active && (
				<ClayIcon className='icon-root' symbol='check' />
			)}
	</td>
);

interface IDataSourceNameProps {
	data: {[key: string]: any};
	hrefFormatter: (params: {[key: string]: any}) => string;
}

export const DataSourceName: React.FC<IDataSourceNameProps> = ({
	data,
	hrefFormatter
}) => (
	<td className='table-cell-expand'>
		<div className='table-title'>
			{disableRow(
				data as {[key: string]: any; state: DataSourceStates}
			) ? (
				<span className='text-truncate'>{data.name}</span>
			) : (
				<Link className='text-truncate' to={hrefFormatter(data)}>
					{data.name}
				</Link>
			)}
		</div>
	</td>
);

export const StatusRenderer: React.FC<ICellProps> = ({data}) => {
	const {display, label} = getDataSourceDisplayObject(
		new DataSource(fromJS(data)),
		true
	);

	return (
		<td>
			<Label display={display} uppercase>
				{label}
			</Label>
		</td>
	);
};

const dateFormatter = (date: string, timeZoneId: string): string =>
	formatDateToTimeZone(date, 'll', timeZoneId);

export const disableRow = ({state}: {state: DataSourceStates}): boolean =>
	state === DataSourceStates.InProgressDeleting;

const getAlertMessage = (dataSource, currentUser, count, groupId) => {
	const admin = currentUser.isAdmin();

	const {credentials, id, name} = dataSource;

	const email = get(credentials, ['oAuthOwner', 'emailAddress']);

	if (admin && count === 1) {
		return sub(
			Liferay.Language.get(
				'your-authorization-token-for-x-has-expired.-please-x-your-account-credentials'
			),
			[
				<b key='NAME'>{name}</b>,
				<Link
					key='REAUTHORIZE'
					to={toRoute(Routes.SETTINGS_DATA_SOURCE, {
						groupId,
						id
					})}
				>
					{Liferay.Language.get('reauthorize').toLowerCase()}
				</Link>
			],
			false
		);
	} else if (admin && count > 1) {
		return Liferay.Language.get(
			'some-of-your-authorization-tokens-have-expired.-please-reauthorize-the-account-credentials-on-these-data-sources-to-prevent-syncing-interruptions'
		);
	} else if (count === 1) {
		return sub(
			Liferay.Language.get(
				'your-authorization-token-for-x-has-expired.-please-contact-your-oauth-administrator,-x,-to-reauthorize'
			),
			[<b key='NAME'>{name}</b>, email],
			false
		);
	} else if (count > 1) {
		return Liferay.Language.get(
			'some-of-your-authorization-tokens-have-expired.-please-contact-your-oauth-administrator-to-reauthorize'
		);
	}
};

const typeFormatter = (type: DataSourceTypes): string => {
	switch (type) {
		case DataSourceTypes.Csv:
			return Liferay.Language.get('.csv');
		case DataSourceTypes.Liferay:
			return Liferay.Language.get('liferay-portal');
		case DataSourceTypes.Salesforce:
			return Liferay.Language.get('salesforce');
		default:
			return '';
	}
};

const DataSourceList = ({className}) => {
	const currentUser = useCurrentUser();
	const {groupId} = useParams();
	const [alerts, setAlerts] = useState([]);
	const {timeZoneId} = useTimeZone();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME)
	});

	const {
		data: invalidDataSources,
		loading: invalidDataSourcesLoading
	} = useRequest({
		dataSourceFn: API.dataSource.search,
		variables: {
			delta: 1,
			groupId,
			page: 1,
			states: [
				DataSourceStates.CredentialsInvalid,
				DataSourceStates.UrlInvalid
			]
		}
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
					type: 'warning'
				}
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
			query
		}
	});

	const renderNav = () => (
		<Nav>
			<Nav.Item>
				<ClayLink
					button
					className='button-root'
					displayType='primary'
					href={toRoute(Routes.SETTINGS_ADD_DATA_SOURCE, {
						groupId
					})}
					small
				>
					{Liferay.Language.get('add-data-source')}
				</ClayLink>
			</Nav.Item>
		</Nav>
	);

	const renderNoResults = () => {
		const authorized = currentUser.isAdmin();

		const connectMessage = authorized ? (
			<>
				{Liferay.Language.get('add-a-data-source-to-get-started')}

				<ClayLink
					className='d-block mb-3'
					href={URLConstants.DataSourceConnection}
					key='DOCUMENTATION'
					target='_blank'
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
		} else {
			return (
				<NoResultsDisplay
					description={connectMessage}
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite'
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
			groupId={groupId}
			key='dataSourceListpage'
			pageDescription={Liferay.Language.get(
				'manage-data-sources-that-are-synced-with-analytics-cloud'
			)}
			pageTitle={Liferay.Language.get('data-sources')}
		>
			<EmbeddedAlertList alerts={alerts} />

			<Card pageDisplay>
				<ListComponent
					checkDisabled={disableRow}
					columns={[
						{
							accessor: 'name',
							cellRenderer: DataSourceName,
							cellRendererProps: {
								hrefFormatter: dataSource =>
									toRoute(Routes.SETTINGS_DATA_SOURCE, {
										groupId,
										id: dataSource.id
									})
							},
							label: Liferay.Language.get('name')
						},
						{
							accessor: PROVIDER_TYPE,
							dataFormatter: typeFormatter,
							label: Liferay.Language.get('source')
						},
						{
							cellRenderer: ContactsCell,
							label: Liferay.Language.get('contacts'),
							sortable: false
						},
						{
							cellRenderer: AnalyticsCell,
							label: Liferay.Language.get('analytics'),
							sortable: false
						},
						{
							accessor: CREATE_DATE,
							dataFormatter: date =>
								dateFormatter(date, timeZoneId),
							label: Liferay.Language.get('date-added')
						},
						{
							cellRenderer: StatusRenderer,
							label: Liferay.Language.get('status'),
							sortable: false
						}
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
							value: NAME
						},
						{
							label: Liferay.Language.get('source'),
							value: PROVIDER_TYPE
						},
						{
							label: Liferay.Language.get('date-added'),
							value: CREATE_DATE
						}
					]}
					orderIOMap={orderIOMap}
					page={page}
					query={query}
					renderNav={currentUser.isAdmin() ? renderNav : null}
					rowIdentifier='id'
					showCheckbox={false}
					total={data?.total}
				/>
			</Card>
		</BasePage>
	);
};

export default DataSourceList;
