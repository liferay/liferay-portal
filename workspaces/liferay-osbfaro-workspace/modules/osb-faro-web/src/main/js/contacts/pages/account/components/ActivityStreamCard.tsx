import AccountEventMetricQuery, {
	AccountEventMetricsData,
	AccountEventMetricsVariables,
} from 'shared/queries/AccountEventMetricQuery';
import AccountEventsTrendQuery, {
	AccountEventsTrendData,
	AccountEventsTrendVariables,
} from 'shared/queries/AccountEventsTrendQuery';
import AccountUserSessionQuery, {
	AccountUserSessionData,
	AccountUserSessionVariables,
} from 'shared/queries/AccountUserSessionQuery';
import ActivityChartEmptyState from 'shared/components/ActivityChartEmptyState';
import ActivityStreamCard from 'shared/components/ActivityStreamCard';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ActivityStreamNoResults from 'shared/components/ActivityStreamNoResults';
import formatAccountSessions from '../utils/formatAccountSessions';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useEffect, useMemo, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {ChartView} from 'shared/components/ChartViewSelector';
import {fetchPolicyDefinition} from 'shared/util/graphql';
import {getSafeRangeSelectors} from 'shared/util/util';
import {getSessionsDateRange} from 'shared/util/activityDateRange';
import {Interval, RangeSelectors} from 'shared/types';
import {
	mapEventMetricToActivityHistory,
	buildCampaignUrls,
	buildTouchIndividualUrls,
	mergeCampaignDays,
} from 'shared/util/activities';
import {mapListResultsToProps} from 'shared/util/mappers';
import {ENABLE_DAY_LEVEL_ACTIVITY} from 'shared/util/feature-flags';
import {SessionEntityTypes} from 'shared/util/constants';
import {toThousands} from 'shared/util/numbers';
import {useParams} from 'react-router-dom';
import {useCampaignTouchesByDay} from 'shared/hooks/useCampaignTouchesByDay';
import {useQuery} from '@apollo/client';
import {useSelectedPoint} from 'shared/hooks/useSelectedPoint';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';
import {useTimeZone} from 'shared/hooks/useTimeZone';
import {getDateRangeLabel, getDateRangeLabelFromDate} from 'shared/util/date';

interface IActivityStreamCardProps {
	accountId: string;
	accountName?: string;
	channelId: string;
	chartView?: ChartView;
	interval: Interval;
	rangeSelectors: RangeSelectors;
}

const AccountActivityStreamCard: React.FC<IActivityStreamCardProps> = ({
	accountId,
	accountName,
	channelId,
	chartView,
	interval,
	rangeSelectors,
}) => {
	const {hasSelectedPoint, onPointSelect, selectedPoint} = useSelectedPoint();

	const [keywords, setKeywords] = useState<string>('');
	const [searchValue, setSearchValue] = useState<string>('');

	const {delta, onDeltaChange, onPageChange, page, resetPage} =
		useStatefulPagination();

	const {timeZoneId} = useTimeZone();

	const {groupId} = useParams<{groupId: string}>();

	const handleChangeSelection = (index: number | null) => {
		resetPage();
		onPointSelect(index ?? undefined);
	};

	useEffect(() => {
		handleChangeSelection(null);
	}, [
		interval,
		rangeSelectors.rangeEnd,
		rangeSelectors.rangeKey,
		rangeSelectors.rangeStart,
	]);

	const safeRangeSelectors = getSafeRangeSelectors(rangeSelectors);

	const metricResponse = useQuery<
		AccountEventMetricsData,
		AccountEventMetricsVariables
	>(AccountEventMetricQuery, {
		fetchPolicy: fetchPolicyDefinition(rangeSelectors),
		variables: {
			accountId,
			channelId,
			entityId: '',
			entityType: SessionEntityTypes.Individual,
			interval,
			keywords,
			...safeRangeSelectors,
		},
	});

	const {
		error,
		items: activityHistory,
		loading,
		refetch,
	} = mapListResultsToProps(metricResponse, ({eventMetric}) => ({
		items: mapEventMetricToActivityHistory(eventMetric),
	}));

	const trendResponse = useQuery<
		AccountEventsTrendData,
		AccountEventsTrendVariables
	>(AccountEventsTrendQuery, {
		fetchPolicy: fetchPolicyDefinition(rangeSelectors),
		variables: {
			accountId,
			channelId,
			entityId: '',
			entityType: SessionEntityTypes.Individual,
			keywords,
			...safeRangeSelectors,
		},
	});

	const campaignTouches = useCampaignTouchesByDay(
		{
			accountId,
			channelId,
			entityId: '',
			entityType: SessionEntityTypes.Individual,
			keywords,
			...getSessionsDateRange({
				activityHistory,
				interval,
				rangeSelectors,
				selectedPoint,
			}),
		},
		{skip: !ENABLE_DAY_LEVEL_ACTIVITY}
	);

	const sessionsResponse = useQuery<
		AccountUserSessionData,
		AccountUserSessionVariables
	>(AccountUserSessionQuery, {
		fetchPolicy: fetchPolicyDefinition(rangeSelectors),
		variables: {
			accountId,
			channelId,
			entityId: '',
			entityType: SessionEntityTypes.Individual,
			keywords,
			page: page - 1,
			size: delta,
			...getSessionsDateRange({
				activityHistory,
				interval,
				rangeSelectors,
				selectedPoint,
			}),
		},
	});

	const sessionsMappedResults = useMemo(
		() =>
			mapListResultsToProps(
				sessionsResponse,
				({eventsByUserSessions}) => ({
					items: mergeCampaignDays(
						formatAccountSessions(
							eventsByUserSessions?.userSessions ?? [],
							{
								accountId,
								accountName,
								channelId,
								groupId,
								rangeSelectors,
								timeZoneId,
							}
						),
						campaignTouches.days,
						{
							isFirstPage: page === 1,
							isLastPage:
								page * delta >=
								(eventsByUserSessions?.totalPageGroupsMetric
									?.value ?? 0),
							timeZoneId,
						}
					),
					total:
						eventsByUserSessions?.totalPageGroupsMetric?.value ?? 0,
				})
			),
		[
			sessionsResponse.data,
			sessionsResponse.error,
			sessionsResponse.loading,
			campaignTouches.days,
			accountId,
			delta,
			page,
			accountName,
			channelId,
			groupId,
			rangeSelectors,
			timeZoneId,
		]
	);

	const {
		onCampaignDeltaChange: handleCampaignDeltaChange,
		onCampaignPageChange: handleCampaignPageChange,
	} = campaignTouches;

	const individualUrls = useMemo(
		() =>
			buildTouchIndividualUrls(campaignTouches.days, {
				channelId,
				groupId,
			}),
		[campaignTouches.days, channelId, groupId]
	);

	const campaignUrls = useMemo(
		() => buildCampaignUrls(campaignTouches.days, {channelId, groupId}),
		[campaignTouches.days, channelId, groupId]
	);

	const handleQuerySubmit = (value: string) => {
		setKeywords(value);
		setSearchValue(value);
		resetPage();
	};

	const handleClearSearch = () => {
		setKeywords('');
		setSearchValue('');
		resetPage();
	};

	const trendMetric =
		trendResponse.data?.eventsByUserSessions?.totalEventsMetric;

	const selected = hasSelectedPoint || selectedPoint !== undefined;

	const {intervalInitDate: selectedIntervalInitDate} =
		(selectedPoint !== undefined && activityHistory[selectedPoint]) || {};

	const dateRangeLabel = selected
		? getDateRangeLabelFromDate(selectedIntervalInitDate, interval)
		: getDateRangeLabel(activityHistory, interval, 'intervalInitDate');

	return (
		<ActivityStreamCard
			activityHistory={activityHistory}
			campaignDays={campaignTouches.days}
			campaignUrls={campaignUrls}
			chartError={error}
			chartLoading={loading}
			chartTooltipRenderRows={({
				totalCampaignResponses,
				totalEvents,
				totalSessions,
			}) => [
				{
					label: Liferay.Language.get('events'),
					value: toThousands(totalEvents),
				},
				{
					label: Liferay.Language.get('sessions'),
					value: toThousands(totalSessions ?? 0),
				},
				...(ENABLE_DAY_LEVEL_ACTIVITY
					? [
							{
								label: Liferay.Language.get(
									'campaign-responses'
								),
								value: toThousands(totalCampaignResponses ?? 0),
							},
						]
					: []),
			]}
			chartView={chartView}
			delta={delta}
			emptyChartContent={
				<ActivityChartEmptyState
					linkHref={URLConstants.AccountActivitiesDocumentationLink}
					linkLabel={Liferay.Language.get(
						'learn-more-about-account-activities'
					)}
					title={Liferay.Language.get(
						'there-is-no-data-for-account-activities'
					)}
				/>
			}
			footerLabel={dateRangeLabel}
			individualUrls={individualUrls}
			interval={interval}
			noResultsRenderer={
				<ActivityStreamNoResults
					hasQuery={!!keywords}
					loading={sessionsMappedResults.loading}
					noData={
						<NoResultsDisplay
							description={
								<>
									<span>
										{Liferay.Language.get(
											'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources,-or-you-can-try-a-different-date-range'
										)}
									</span>

									<ClayLink
										className="d-block mb-3"
										decoration="underline"
										href={
											URLConstants.AccountsDocumentationLink
										}
										key="DOCUMENTATION"
										target="_blank"
									>
										{Liferay.Language.get(
											'learn-more-about-accounts'
										)}

										<span className="inline-item inline-item-after">
											<ClayIcon
												fontSize={8}
												symbol="shortcut"
											/>
										</span>
									</ClayLink>
								</>
							}
							spacer
							title={Liferay.Language.get(
								'there-is-no-activity-on-the-selected-period'
							)}
						/>
					}
					onClearSearch={handleClearSearch}
				/>
			}
			onCampaignDeltaChange={handleCampaignDeltaChange}
			onCampaignPageChange={handleCampaignPageChange}
			onChartReload={refetch}
			onClearDateSelection={() => handleChangeSelection(null)}
			onDeltaChange={onDeltaChange}
			onPageChange={onPageChange}
			onPointSelect={handleChangeSelection}
			onSearchChange={setSearchValue}
			onSearchSubmit={handleQuerySubmit}
			page={page}
			rangeSelectors={rangeSelectors}
			searchValue={searchValue}
			selected={selected}
			selectedPoint={selectedPoint}
			sessionsMappedResults={sessionsMappedResults}
			timeZoneId={timeZoneId}
			trendSummary={{
				classification: trendMetric?.trend?.trendClassification,
				percentage: trendMetric?.trend?.percentage ?? 0,
				value: trendMetric?.value ?? 0,
			}}
		/>
	);
};

export default AccountActivityStreamCard;
