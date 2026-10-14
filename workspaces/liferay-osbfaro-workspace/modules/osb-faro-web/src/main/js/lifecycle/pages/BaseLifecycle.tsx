import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountsDataSet from 'shared/components/accounts-data-set/AccountsDataSet';
import BasePage from 'shared/components/base-page';
import DataSourceEmptyState from 'shared/components/DataSourceEmptyState';
import ErrorPage from 'shared/pages/ErrorPage';
import FilterPicker from '../components/FilterPicker';
import LifecycleChart from 'lifecycle/components/LifecycleChart';
import TrailingNinetyDayRange from 'shared/components/TrailingNinetyDayRange';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import OverviewSection from '../components/OverviewSection';
import React, {useContext} from 'react';
import SegmentDropdown from 'shared/components/SegmentDropdown';
import {
	AccountMetricType,
	IAccountMetric,
} from 'contacts/pages/account/utils/types';
import {ChannelContext} from 'shared/context/channel';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect, ConnectedProps} from 'react-redux';
import {
	LifecycleContextProvider,
	useLifecycle,
} from '../context/LifecycleContext';
import {NoDataSourcesEmptyState} from 'lifecycle/components/LifecycleEmptyStates';
import {useParams} from 'react-router-dom';
import {RangeKeyTimeRanges, Sizes} from 'shared/util/constants';
import {Routes, toRoute} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useRequest} from 'shared/hooks/useRequest';
import {useSegmentFilter} from 'shared/hooks/useSegmentFilter';

const ProcessingLifecycleEmptyState = () => (
	<NoResultsDisplay
		description={Liferay.Language.get(
			'your-configuration-is-complete.-metrics-will-appear-in-the-dashboard-once-processing-is-complete'
		)}
		displayCard
		icon={{
			border: false,
			size: Sizes.XXXLarge,
			symbol: 'ac_no_sites',
		}}
		spacer
		title={Liferay.Language.get('your-dashboard-is-almost-ready')}
	/>
);

const LifecycleOverview = () => {
	const {filters, lifecycleId} = useLifecycle();

	const {groupId} = useParams();

	const {data: overviewData, loading: overviewLoading} = useRequest({
		dataSourceFn: API.lifecycle.fetchOverviewMetrics,
		variables: {
			country: filters.countryFilter,
			groupId: groupId!,
			industry: filters.industryFilter,
			lifecycleId,
		},
	});

	return <OverviewSection loading={overviewLoading} metrics={overviewData} />;
};

const LifecycleStagesSection = () => {
	const {filters, lifecycleId} = useLifecycle();

	const {groupId} = useParams();

	const {segmentId} = useSegmentFilter();

	const {
		data: stagesData,
		error: stagesError,
		loading: stagesLoading,
	} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycleStages,
		variables: {
			country: filters.countryFilter,
			groupId,
			industry: filters.industryFilter,
			lifecycleId,
			segmentId,
		},
	});

	return (
		<LifecycleChart
			error={!!stagesError}
			loading={stagesLoading}
			stages={stagesData}
		/>
	);
};

const LifecycleAccounts = () => {
	const {filters, lifecycleId, stageSelectionNonce} = useLifecycle();

	const {channelId, groupId} = useParams();

	const {segmentId, segmentName} = useSegmentFilter();

	return (
		<section>
			<SectionHeader
				icon="box-container"
				title={Liferay.Language.get('accounts')}
			/>

			<AccountsDataSet
				accountLifecycleId={lifecycleId}
				apiURL={`/o/faro/contacts/${groupId!}/account-lifecycle/${lifecycleId}/accounts`}
				channelId={channelId!}
				countryFilter={filters.countryFilter}
				groupId={groupId!}
				industryFilter={filters.industryFilter}
				lifecycleStageFilter={filters.lifecycleStageFilter}
				rangeKeyFilter={RangeKeyTimeRanges.Last30Days}
				segmentFilter={segmentId}
				segmentName={segmentName}
				stageSelectionNonce={stageSelectionNonce}
			/>
		</section>
	);
};

const connector = connect(null, {close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

const BaseLifecycle: React.FC<PropsFromRedux> = ({close, open}) => {
	const currentUser = useCurrentUser();
	const {selectedChannel} = useContext(ChannelContext);

	const history = useHistoryAdapter();

	const {channelId, groupId, lifecycleId} = useParams();

	const {empty: noDataSources, loading: dataSourcesLoading} =
		useDataSources();

	const {segmentId, segmentName, setSegment} = useSegmentFilter();

	const {
		data: lifecycle,
		error: lifecycleError,
		loading: lifecycleLoading,
	} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycle,
		variables: {groupId: groupId!, lifecycleId: lifecycleId!},
	});

	const processing = lifecycle?.processedDate == null;

	const {data: accountMetrics, loading: accountMetricsLoading} = useRequest({
		dataSourceFn: API.accounts.fetchMetrics,
		skipRequest: noDataSources,
		variables: {channelId: channelId!, groupId: groupId!},
	});

	const totalAccounts =
		(accountMetrics as IAccountMetric[] | undefined)?.find(
			(metric) => metric.metricType === AccountMetricType.Total
		)?.value ?? 0;

	const authorized = currentUser.isAdmin();

	const loading = dataSourcesLoading || lifecycleLoading;

	const title = lifecycle?.name || Liferay.Language.get('lifecycles');

	const hasContent =
		!loading &&
		!noDataSources &&
		!processing &&
		!accountMetricsLoading &&
		!!totalAccounts;

	const renderBody = () => {
		if (loading) {
			return <Loading />;
		}

		if (noDataSources) {
			return (
				<NoDataSourcesEmptyState
					authorized={authorized}
					groupId={groupId!}
				/>
			);
		}

		if (processing) {
			return <ProcessingLifecycleEmptyState />;
		}

		if (accountMetricsLoading) {
			return <Loading />;
		}

		if (!totalAccounts) {
			return (
				<DataSourceEmptyState
					authorized={authorized}
					description={
						authorized
							? Liferay.Language.get(
									'connect-a-data-source-containing-account-data'
								)
							: Liferay.Language.get(
									'contact-an-administrator-to-connect-a-data-source-containing-account-data'
								)
					}
					groupId={groupId!}
					title={Liferay.Language.get('no-account-data-available')}
				/>
			);
		}

		return (
			<>
				<LifecycleOverview />

				<LifecycleStagesSection />

				<LifecycleAccounts />
			</>
		);
	};

	if (!lifecycleLoading && (lifecycleError || !lifecycle)) {
		return (
			<ErrorPage
				href={toRoute(Routes.LIFECYCLE, {channelId, groupId})}
				linkLabel={Liferay.Language.get('go-to-lifecycles')}
				message={Liferay.Language.get(
					'the-lifecycle-you-are-looking-for-does-not-exist'
				)}
				subtitle={Liferay.Language.get('lifecycle-not-found')}
			/>
		);
	}

	return (
		<LifecycleContextProvider key={lifecycleId} lifecycleId={lifecycleId}>
			<BasePage documentTitle={title}>
				<BasePage.Header
					breadcrumbs={[
						breadcrumbs.getHome({
							channelId: channelId!,
							groupId: groupId!,
							label: selectedChannel?.name,
						}),
						breadcrumbs.getLifecycles({
							channelId: channelId!,
							groupId: groupId!,
						}),
						...(lifecycle?.name
							? [
									breadcrumbs.getEntityName({
										label: lifecycle.name,
									}),
								]
							: []),
					]}
					groupId={groupId!}
				>
					<BasePage.Row>
						<BasePage.Header.TitleSection
							className="mb-3"
							title={title}
						>
							<BasePage.Header.PageActionsToolbar
								actions={[
									...(authorized
										? [
												{
													icon: {symbol: 'cog'},
													label: Liferay.Language.get(
														'lifecycle-configuration'
													),
													onClick: () =>
														history.push(
															toRoute(
																Routes.LIFECYCLE_EDIT,
																{
																	channelId,
																	groupId,
																	lifecycleId,
																}
															)
														),
												},
											]
										: []),
									{
										icon: {symbol: 'bell-on'},
										label: Liferay.Language.get(
											'manage-notifications'
										),
										onClick: () =>
											open(
												modalTypes.MANAGE_LIFECYCLE_NOTIFICATIONS_MODAL,
												{
													groupId,
													lifecycleId,
													onClose: close,
												}
											),
									},
								]}
							/>
						</BasePage.Header.TitleSection>
					</BasePage.Row>
				</BasePage.Header>
				{hasContent && (
					<BasePage.SubHeader>
						<div className="d-flex justify-content-between w-100">
							<div className="d-flex">
								<FilterPicker
									className="mr-3"
									entityLabel={Liferay.Language.get(
										'industries'
									)}
									fieldMappingFieldName="industry"
									filterKey="industryFilter"
								/>

								<FilterPicker
									className="mr-3"
									entityLabel={Liferay.Language.get(
										'countries'
									)}
									fieldMappingFieldName="country"
									filterKey="countryFilter"
								/>

								<SegmentDropdown
									initialSegmentId={segmentId}
									initialSegmentName={segmentName}
									onFilterChange={setSegment}
								/>
							</div>

							<TrailingNinetyDayRange />
						</div>
					</BasePage.SubHeader>
				)}
				<BasePage.Body>{renderBody()}</BasePage.Body>
			</BasePage>
		</LifecycleContextProvider>
	);
};

export default connector(BaseLifecycle);
