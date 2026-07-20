import ActivityChartEmptyState from 'shared/components/ActivityChartEmptyState';
import ActivityStreamCard from 'shared/components/ActivityStreamCard';
import ActivityStreamNoResults from 'shared/components/ActivityStreamNoResults';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import EventMetricQuery, {
	EventMetricsData,
	EventMetricsVariables,
} from 'shared/queries/EventMetricQuery';
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
} from 'shared/util/activities';
import {getSafeRangeSelectors} from 'shared/util/util';
import {getSessionsDateRange} from 'shared/util/activityDateRange';
import {Individual} from 'shared/util/records';
import {Interval, RangeSelectors} from 'shared/types';
import {mapListResultsToProps} from 'shared/util/mappers';
import {SessionEntityTypes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {useParams} from 'react-router-dom';
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
				...getSafeRangeSelectors(rangeSelectors),
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
					items: formatSessions(
						eventsByUserSessions?.userSessions ?? [],
						{
							channelId,
							groupId,
							rangeSelectors,
						}
					),
					total: eventsByUserSessions?.totalEvents ?? 0,
				})
			),
		[
			sessionsResponse.data,
			sessionsResponse.error,
			sessionsResponse.loading,
			channelId,
			groupId,
			rangeSelectors,
		]
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

	const selected = hasSelectedPoint || selectedPoint !== undefined;

	const {intervalInitDate} =
		(selectedPoint !== undefined && activityHistory[selectedPoint]) || {};

	const date = selected
		? getDateRangeLabelFromDate(intervalInitDate, interval)
		: getDateRangeLabel(activityHistory, interval, 'intervalInitDate');

	return (
		<ActivityStreamCard
			activityHistory={activityHistory}
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
			footerLabel={
				activityHistory?.length
					? sub(
							Liferay.Language.get(
								'the-individual-performed-the-events-during-x'
							),
							[date]
						)
					: Liferay.Language.get('individuals-events')
			}
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
											'check-back-later-to-see-if-data-has-been-received-from-your-data-sources,-or-try-a-different-date-range'
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
							title={Liferay.Language.get('no-data-was-found')}
						/>
					}
					onClearSearch={handleClearSearch}
				/>
			}
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
		/>
	);
};

export default ProfileCardWithDataCDP;
