import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountsDataSet from 'shared/components/AccountsDataSet';
import BasePage from 'shared/components/base-page';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {ClayButtonWithIcon} from '@clayui/button';
import FilterPicker from '../components/FilterPicker';
import LifecycleChart from 'lifecycle/components/LifecycleChart';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import OverviewSection from '../components/OverviewSection';
import React, {useContext} from 'react';
import SegmentDropdown from 'shared/components/SegmentDropdown';
import URLConstants from 'shared/util/url-constants';
import {
	AccountMetricType,
	IAccountMetric,
} from 'contacts/pages/account/utils/types';
import {ChannelContext} from 'shared/context/channel';
import {
	LifecycleContextProvider,
	useLifecycle,
} from '../context/LifecycleContext';
import {Routes, toRoute} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {Sizes} from 'shared/util/constants';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useHistory, useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {useSegmentFilter} from 'shared/hooks/useSegmentFilter';

const LifecycleEmptyState = ({
	authorized,
	description,
	groupId,
	title,
}: {
	authorized: boolean;
	description: string;
	groupId: string;
	title: string;
}) => (
	<NoResultsDisplay
		description={
			<>
				<p className="mb-2">{description}</p>

				<ClayLink
					className="d-block mb-3"
					decoration="underline"
					href={URLConstants.DataSourceConnection}
					target="_blank"
				>
					{Liferay.Language.get('learn-more-about-data-sources')}

					<span className="inline-item inline-item-after">
						<ClayIcon fontSize={8} symbol="shortcut" />
					</span>
				</ClayLink>
			</>
		}
		displayCard
		icon={{
			border: false,
			size: Sizes.XXXLarge,
			symbol: 'ac_satellite',
		}}
		spacer
		title={title}
	>
		{authorized ? (
			<ClayLink
				button
				className="button-root mt-1"
				displayType="primary"
				href={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {groupId})}
			>
				{Liferay.Language.get('connect-data-source')}
			</ClayLink>
		) : undefined}
	</NoResultsDisplay>
);

const ConfigureLifecycleEmptyState = ({
	channelId,
	groupId,
}: {
	channelId: string;
	groupId: string;
}) => (
	<NoResultsDisplay
		description={Liferay.Language.get(
			'complete-the-configuration-to-start-seeing-insights'
		)}
		displayCard
		icon={{
			border: false,
			size: Sizes.XXXLarge,
			symbol: 'ac_lifecycle_empty',
		}}
		spacer
		title={Liferay.Language.get('configure-a-new-lifecycle')}
	>
		<ClayLink
			button
			className="button-root mt-1"
			displayType="primary"
			href={toRoute(Routes.LIFECYCLE_CREATE, {channelId, groupId})}
		>
			{Liferay.Language.get('new-lifecycle')}
		</ClayLink>
	</NoResultsDisplay>
);

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
				segmentFilter={segmentId}
				segmentName={segmentName}
				stageSelectionNonce={stageSelectionNonce}
			/>
		</section>
	);
};

const BaseLifecycle = () => {
	const currentUser = useCurrentUser();
	const {selectedChannel} = useContext(ChannelContext);

	const history = useHistory();

	const {channelId, groupId} = useParams();

	const {empty: noDataSources, loading: dataSourcesLoading} =
		useDataSources();

	const {segmentId, segmentName, setSegment} = useSegmentFilter();

	const {data: lifecycles, loading: lifecyclesLoading} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycles,
		variables: {groupId: groupId!},
	});

	const lifecycle = lifecycles?.[0];

	const lifecycleId = lifecycle?.id;

	const hasLifecycles = !!lifecycles?.length;

	const processing = hasLifecycles && lifecycle?.processedDate == null;

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

	const loading = dataSourcesLoading || lifecyclesLoading;

	const title = lifecycle?.name || Liferay.Language.get('lifecycles');

	const hasContent =
		!loading &&
		!noDataSources &&
		hasLifecycles &&
		!processing &&
		!accountMetricsLoading &&
		!!totalAccounts;

	const renderBody = () => {
		if (loading) {
			return <Loading />;
		}

		if (noDataSources) {
			return (
				<LifecycleEmptyState
					authorized={authorized}
					description={
						authorized
							? Liferay.Language.get(
									'connect-a-data-source-to-sync-lifecycle-stages'
								)
							: Liferay.Language.get(
									'please-contact-your-workspace-administrator-to-add-data-sources'
								)
					}
					groupId={groupId!}
					title={Liferay.Language.get('no-data-sources-connected')}
				/>
			);
		}

		if (!hasLifecycles) {
			return (
				<ConfigureLifecycleEmptyState
					channelId={channelId!}
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
				<LifecycleEmptyState
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

	return (
		<LifecycleContextProvider lifecycleId={lifecycleId ?? ''}>
			<BasePage documentTitle={title}>
				<BasePage.Header
					breadcrumbs={[
						breadcrumbs.getHome({
							channelId: channelId!,
							groupId: groupId!,
							label: selectedChannel?.name,
						}),
					]}
					groupId={groupId!}
				>
					<BasePage.Row>
						<BasePage.Header.TitleSection
							className="mb-3"
							title={title}
						/>

						{hasLifecycles && authorized && (
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'lifecycle-configuration'
								)}
								borderless
								data-tooltip-align="top"
								displayType="secondary"
								onClick={() =>
									history.push(
										toRoute(Routes.LIFECYCLE_EDIT, {
											channelId,
											groupId,
											lifecycleId,
										})
									)
								}
								symbol="cog"
								title={Liferay.Language.get(
									'lifecycle-configuration'
								)}
							/>
						)}
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
						</div>
					</BasePage.SubHeader>
				)}
				<BasePage.Body>{renderBody()}</BasePage.Body>
			</BasePage>
		</LifecycleContextProvider>
	);
};

export default BaseLifecycle;
