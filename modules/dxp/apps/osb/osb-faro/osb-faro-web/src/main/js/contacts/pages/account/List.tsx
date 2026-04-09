import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import Link from '@clayui/link';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import TotalAccounts from 'contacts/components/account/TotalAccounts';
import URLConstants from 'shared/util/url-constants';
import {columns, pagination, useSnapshots} from 'shared/util/frontend-data-set';
import {isNil} from 'lodash/fp';
import {LifecycleStages} from './utils/constants';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {toThousands} from 'shared/util/numbers';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useFrontendDataSet} from 'shared/hooks/useFrontendDataSet';
import {useRequest} from 'shared/hooks/useRequest';

const lifecycleStagesLabelMap = {
	[LifecycleStages.AT_RISK]: {
		displayType: 'danger',
		label: Liferay.Language.get('at-risk')
	},
	[LifecycleStages.AWARE]: {
		displayType: 'secondary',
		label: Liferay.Language.get('aware')
	},
	[LifecycleStages.ENGAGED]: {
		displayType: 'warning',
		label: Liferay.Language.get('engaged')
	},
	[LifecycleStages.ESTABLISHED]: {
		displayType: 'success',
		label: Liferay.Language.get('established')
	},
	[LifecycleStages.ONBOARDING]: {
		displayType: 'secondary',
		label: Liferay.Language.get('onboarding')
	},
	[LifecycleStages.PIPELINE]: {
		displayType: 'info',
		label: Liferay.Language.get('pipeline')
	}
};

const lifecycleStageFilter = {
	items: Object.entries(lifecycleStagesLabelMap).map(([stage]) => ({
		label: lifecycleStagesLabelMap[stage as LifecycleStages].label,
		value: stage
	}))
};

interface IListProps {
	channelId: string;
	groupId: string;
}

const List: React.FC<IListProps> = ({channelId, groupId}) => {
	const currentUser = useCurrentUser();
	const {selectedChannel} = useChannelContext();

	const {data: dataSourceData, loading: dataSourceLoading} = useRequest({
		dataSourceFn: API.dataSource.search,
		variables: {
			delta: 1,
			groupId
		}
	});

	const authorized = currentUser.isAdmin();

	const dataSourceConnected =
		!isNil(dataSourceData?.total) && dataSourceData?.total > 0;

	const NoDataSourcesConnected = () => (
		<NoResultsDisplay
			description={
				<>
					{Liferay.Language.get(
						'connect-a-data-source-to-start-syncing-accounts'
					)}

					{authorized && (
						<>
							<p>
								<Link
									className='d-block mb-3'
									href={URLConstants.DataSourceConnection}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'access-our-documentation-to-learn-more'
									)}
								</Link>
							</p>
							<Link
								button
								className='button-root'
								displayType='primary'
								href={toRoute(
									Routes.SETTINGS_DATA_SOURCE_LIST,
									{
										groupId
									}
								)}
							>
								{Liferay.Language.get('connect-data-source')}
							</Link>
						</>
					)}
				</>
			}
			displayCard
			icon={{
				border: false,
				size: Sizes.XXXLarge,
				symbol: 'ac_satellite'
			}}
			spacer
			title={Liferay.Language.get('no-data-sources-connected')}
		/>
	);

	const FrontendDataSet = useFrontendDataSet();

	const snapshots = useSnapshots('accounts-list-dataset');

	if (dataSourceLoading) {
		return <Loading />;
	}

	return (
		<BasePage documentTitle={Liferay.Language.get('accounts')}>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel && selectedChannel.name
					})
				]}
				groupId={groupId}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection
						title={Liferay.Language.get('accounts')}
					/>
				</BasePage.Row>
			</BasePage.Header>
			<BasePage.Body>
				{dataSourceConnected ? (
					<>
						<TotalAccounts groupId={groupId} />

						{FrontendDataSet && (
							<Card>
								<FrontendDataSet
									apiURL={`/o/contacts/${groupId}/account/search`}
									configInURLBehavior='off'
									customDataRenderers={{
										accountLifecycleStageRenderer: ({
											value
										}) =>
											value &&
											columns.cmsLabelRenderer({
												displayType:
													lifecycleStagesLabelMap[
														value
													].displayType,
												label:
													lifecycleStagesLabelMap[
														value
													].label
											}),
										accountNameRenderer: ({
											itemData,
											value
										}) =>
											columns.nameAndLinkRenderer({
												groupId,
												itemData,
												value
											}),
										annualRevenueRenderer: ({value}) => (
											<div>{toThousands(value)}</div>
										),
										dateRenderer: ({value}) =>
											columns.dateRenderer({value})
									}}
									emptyState={{
										description: Liferay.Language.get(
											'no-accounts-were-synced-from-the-connected-data-sources'
										),
										image: '/states/satellite.svg',
										title: Liferay.Language.get(
											'no-accounts-found'
										)
									}}
									filters={[
										{
											id: 'lifecycleStatus',
											items: lifecycleStageFilter.items,
											label: Liferay.Language.get(
												'status'
											),
											name: 'status',
											type: 'selection'
										},
										{
											apiURL: `/o/contacts/${groupId}/account/industries`,
											entityFieldType: 'string',
											id: 'name',
											itemKey: 'name',
											itemLabel: 'name',
											label: Liferay.Language.get(
												'industry'
											),
											multiple: true,
											type: 'selection'
										},
										{
											apiURL: `/o/contacts/${groupId}/account/countries`,
											entityFieldType: 'string',
											id: 'name',
											itemKey: 'name',
											itemLabel: 'name',
											label: Liferay.Language.get(
												'country'
											),
											multiple: true,
											type: 'selection'
										}
									]}
									id='accounts-list-dataset'
									loading={dataSourceLoading}
									pagination={pagination}
									showPagination
									snapshots={snapshots}
									snapshotsEnabled
									sort={[
										{
											active: true,
											direction: 'asc',
											key: 'accountName',
											label: Liferay.Language.get(
												'account'
											)
										}
									]}
									views={[
										{
											contentRenderer: 'table',
											default: true,
											label: Liferay.Language.get(
												'default-view'
											),
											name: 'table',
											schema: {
												fields: [
													{
														_key: 'accountName',
														contentRenderer:
															'accountNameRenderer',
														fieldName:
															'accountName',
														label: Liferay.Language.get(
															'account'
														),
														sortable: true,
														truncate: true
													},
													{
														_key: 'industry',
														fieldName: 'industry',
														label: Liferay.Language.get(
															'industry'
														),
														sortable: true
													},
													{
														_key: 'lifecycleStage',
														contentRenderer:
															'accountLifecycleStageRenderer',
														fieldName:
															'lifecycleStage',
														label: Liferay.Language.get(
															'lifecycle-stage'
														),
														sortable: true
													},
													{
														_key: 'annualRevenue',
														contentRenderer:
															'annualRevenueRenderer',
														fieldName:
															'annualRevenue',
														label: Liferay.Language.get(
															'annual-revenue'
														),
														sortable: true
													},
													{
														_key: 'country',
														fieldName: 'country',
														label: Liferay.Language.get(
															'country'
														),
														sortable: true
													},
													{
														_key: 'lastActive',
														contentRenderer:
															'dateRenderer',
														fieldName: 'lastActive',
														label: Liferay.Language.get(
															'last-active'
														),
														sortable: true
													},
													{
														_key: 'lastEnriched',
														contentRenderer:
															'dateRenderer',
														fieldName:
															'lastEnriched',
														label: Liferay.Language.get(
															'last-enriched'
														),
														sortable: true,
														visible: false
													}
												]
											},
											thumbnail: 'table'
										}
									]}
								/>
							</Card>
						)}
					</>
				) : (
					<NoDataSourcesConnected />
				)}
			</BasePage.Body>
		</BasePage>
	);
};

export default List;
