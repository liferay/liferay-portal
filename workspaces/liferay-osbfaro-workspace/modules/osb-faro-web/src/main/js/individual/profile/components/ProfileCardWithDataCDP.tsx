import ActivityChartEmptyState from 'shared/components/ActivityChartEmptyState';
import ActivityStreamCard from 'shared/components/ActivityStreamCard';
import ActivityStreamNoResults from 'shared/components/ActivityStreamNoResults';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import EventMetricQuery, {
	EventMetricsData,
	EventMetricsVariables,
} from 'shared/queries/EventMetricQuery';
import EventsTrendQuery, {
	EventsTrendData,
	EventsTrendVariables,
} from 'shared/queries/EventsTrendQuery';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useMemo, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import UserSessionQuery, {
	UserSessionData,
	UserSessionVariables,
} from 'shared/queries/UserSessionQuery';
import {fetchPolicyDefinition} from 'shared/util/graphql';
import {
	formatSessions,
	mapEventMetricToActivityHistory,
	buildCampaignUrls,
	buildTouchIndividualUrls,
	mergeCampaignDays,
} from 'shared/util/activities';
import {getSafeRangeSelectors} from 'shared/util/util';
import {getSessionsDateRange} from 'shared/util/activityDateRange';
import {Individual} from 'shared/util/records';
import {Interval, RangeSelectors} from 'shared/types';
import {mapListResultsToProps} from 'shared/util/mappers';
import {ENABLE_DAY_LEVEL_ACTIVITY} from 'shared/util/feature-flags';
import {SessionEntityTypes} from 'shared/util/constants';
import {useParams} from 'react-router-dom';
import {useCampaignTouchesByDay} from 'shared/hooks/useCampaignTouchesByDay';
import {useQuery} from '@apollo/client';
import {useSelectedPoint} from 'shared/hooks/useSelectedPoint';
import {getDateRangeLabel, getDateRangeLabelFromDate} from 'shared/util/date';

interface IProfileCardWithDataCDPProps
	extends React.HTMLAttributes<HTMLElement> {
	channelId: string;
	delta: number;
	entity: Individual;
	interval: Interval;
	onChangeInterval: (interval: Interval) => void;
	onDeltaChange: (delta: number) => void;
	onPageChange: (page: number) => void;
	onRangeSelectorsChange: (rangeSelectors: RangeSelectors) => void;
	onQueryChange: (query: string) => void;
	page: number;
	query: string;
	rangeSelectors: RangeSelectors;
	resetPage: () => void;
	tabId: string;
	timeZoneId?: string;
}

const ProfileCardWithDataCDP: React.FC<IProfileCardWithDataCDPProps> = ({
	channelId,
	delta,
	entity: {id: entityId},
	interval,
	onDeltaChange,
	onPageChange,
	onQueryChange,
	page,
	query,
	rangeSelectors,
	resetPage,
	timeZoneId,
}) => {
	const {hasSelectedPoint, onPointSelect, selectedPoint} = useSelectedPoint();
	const [searchValue, setSearchValue] = useState<string>('');

	const {groupId} = useParams<{groupId: string}>();

	const safeRangeSelectors = getSafeRangeSelectors(rangeSelectors);

	const activityResponse = useQuery<EventMetricsData, EventMetricsVariables>(
		EventMetricQuery,
		{
			fetchPolicy: fetchPolicyDefinition(rangeSelectors),
			variables: {
				channelId,
				entityId,
				entityType: SessionEntityTypes.Individual,
				interval,
				keywords: query,
				...safeRangeSelectors,
			},
		}
	);

	const {
		error,
		items: activityHistory,
		loading,
		refetch,
	} = mapListResultsToProps(activityResponse, ({eventMetric}) => ({
		items: mapEventMetricToActivityHistory(eventMetric),
	}));

	const trendResponse = useQuery<EventsTrendData, EventsTrendVariables>(
		EventsTrendQuery,
		{
			fetchPolicy: fetchPolicyDefinition(rangeSelectors),
			variables: {
				channelId,
				entityId,
				entityType: SessionEntityTypes.Individual,
				keywords: query,
				...safeRangeSelectors,
			},
		}
	);

	const campaignTouches = useCampaignTouchesByDay(
		{
			channelId,
			entityId,
			entityType: SessionEntityTypes.Individual,
			keywords: query,
			...getSessionsDateRange({
				activityHistory,
				interval,
				rangeSelectors,
				selectedPoint,
			}),
		},
		{skip: !ENABLE_DAY_LEVEL_ACTIVITY}
	);

	const sessionsResponse = useQuery<UserSessionData, UserSessionVariables>(
		UserSessionQuery,
		{
			fetchPolicy: fetchPolicyDefinition(rangeSelectors),
			variables: {
				...getSessionsDateRange({
					activityHistory,
					interval,
					rangeSelectors,
					selectedPoint,
				}),
				channelId,
				entityId,
				entityType: SessionEntityTypes.Individual,
				keywords: query,
				page: page - 1,
				size: delta,
			},
		}
	);

	const sessionsMappedResults = useMemo(
		() =>
			mapListResultsToProps(
				sessionsResponse,
				({eventsByUserSessions}) => ({
					items: mergeCampaignDays(
						formatSessions(
							eventsByUserSessions?.userSessions ?? [],
							{
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
			channelId,
			delta,
			page,
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

	const handleChangeSelection = (index: number | null) => {
		resetPage();
		onPointSelect(index ?? undefined);
	};

	const handleQuery = (query: string) => {
		onQueryChange(query);
		setSearchValue(query);
	};

	const handleClearSearch = () => {
		onQueryChange('');
		setSearchValue('');
	};

	const trendMetric =
		trendResponse.data?.eventsByUserSessions?.totalEventsMetric;

	const selected = hasSelectedPoint || selectedPoint !== undefined;

	const {intervalInitDate} =
		(selectedPoint !== undefined && activityHistory[selectedPoint]) || {};

	const date = selected
		? getDateRangeLabelFromDate(intervalInitDate, interval)
		: getDateRangeLabel(activityHistory, interval, 'intervalInitDate');

	return (
		<ActivityStreamCard
			activityHistory={activityHistory}
			campaignDays={campaignTouches.days}
			campaignUrls={campaignUrls}
			chartError={error}
			chartLoading={loading}
			delta={delta}
			emptyChartContent={
				<ActivityChartEmptyState
					linkHref={URLConstants.IndividualProfilesDocument}
					linkLabel={Liferay.Language.get(
						'learn-more-about-individuals'
					)}
					title={Liferay.Language.get(
						'there-is-no-data-for-individual-activities'
					)}
				/>
			}
			footerLabel={activityHistory?.length ? date : ''}
			individualUrls={individualUrls}
			interval={interval}
			noResultsRenderer={
				<ActivityStreamNoResults
					hasQuery={!!query}
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
											URLConstants.IndividualProfilesDocument
										}
										key="DOCUMENTATION"
										target="_blank"
									>
										{Liferay.Language.get(
											'learn-more-about-individuals'
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
			onSearchSubmit={handleQuery}
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

export default ProfileCardWithDataCDP;
