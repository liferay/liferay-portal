import ActivitiesChart from 'contacts/components/ActivitiesChart';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import EventMetricQuery, {
	EventMetricsData,
	EventMetricsVariables
} from 'shared/queries/EventMetricQuery';
import IntervalSelector from 'shared/components/IntervalSelector';
import moment from 'moment';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useState} from 'react';
import SearchInput from 'shared/components/SearchInput';
import Toolbar from 'shared/components/toolbar';
import URLConstants from 'shared/util/url-constants';
import UserSessionQuery, {
	UserSessionData,
	UserSessionVariables
} from 'shared/queries/UserSessionQuery';
import VerticalTimeline from 'shared/components/VerticalTimeline';
import {compose, withPaginationBar} from 'shared/hoc';
import {
	DEFAULT_DATE_FORMAT,
	formatUTCDate,
	getDateRangeLabel,
	getDateRangeLabelFromDate,
	getEndDate
} from 'shared/util/date';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {fetchPolicyDefinition} from 'shared/util/graphql';
import {formatSessions, getActivityLabel} from 'shared/util/activities';
import {getSafeRangeSelectors} from 'shared/util/util';
import {Individual} from 'shared/util/records';
import {Interval, RangeSelectors, SafeRangeSelectors} from 'shared/types';
import {isHourlyRangeKey} from 'shared/util/time';
import {isNil} from 'lodash';
import {mapListResultsToProps} from 'shared/util/mappers';
import {RangeKeyTimeRanges, SessionEntityTypes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {useQuery} from '@apollo/react-hooks';
import {useSelectedPoint} from 'shared/hooks/useSelectedPoint';
import {withEmpty} from 'cerebro-shared/hocs/utils';
import {withError, withLoading, WrapSafeResults} from 'shared/hoc/util';

const formatTimestamp = (timestamp: number) => {
	const date = new Date(timestamp);
	const hours = date.getUTCHours().toString().padStart(2, '0');
	const minutes = date.getUTCMinutes().toString().padStart(2, '0');
	const seconds = date.getUTCSeconds().toString().padStart(2, '0');

	return `${hours}:${minutes}:${seconds}`;
};

const PaginatedVerticalTimeline = compose<any>(
	withPaginationBar(),
	withLoading(),
	withError({page: false}),
	withEmpty()
)(VerticalTimeline);

interface IProfileCardProps extends React.HTMLAttributes<HTMLElement> {
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

const ProfileCard: React.FC<IProfileCardProps> = ({
	channelId,
	delta,
	entity: {id: entityId},
	interval,
	onChangeInterval,
	onDeltaChange,
	onPageChange,
	onQueryChange,
	onRangeSelectorsChange,
	page,
	query,
	rangeSelectors,
	resetPage,
	timeZoneId
}) => {
	const {hasSelectedPoint, onPointSelect, selectedPoint} = useSelectedPoint();
	const [searchValue, setSearchValue] = useState<string>('');

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
				...getSafeRangeSelectors(rangeSelectors)
			}
		}
	);

	const {
		error,
		items: activityHistory,
		loading,
		refetch,
		total: activityTotal
	} = mapListResultsToProps(activityResponse, ({eventMetric}) => ({
		items: eventMetric.totalEventsMetric.histogram.metrics?.map(
			({key, value}, index: number) => ({
				intervalInitDate: moment.utc(key).valueOf(),
				totalEvents: value,
				totalSessions:
					eventMetric?.totalSessionsMetric?.histogram?.metrics?.[
						index
					].value
			})
		),
		total: eventMetric.totalEventsMetric?.value
	}));

	const getDateRange = (
		{rangeEnd, rangeKey, rangeStart}: RangeSelectors,
		interval: Interval
	): SafeRangeSelectors => {
		const {intervalInitDate} = activityHistory[selectedPoint] || {};
		const endDate = getEndDate(intervalInitDate, interval);

		const hasSelectedDate = !isNil(endDate) && !isNil(intervalInitDate);

		if (hasSelectedDate) {
			const formattedRangeEnd = formatUTCDate(
				getEndDate(intervalInitDate, interval),
				DEFAULT_DATE_FORMAT
			);
			const formattedRangeStart = formatUTCDate(
				intervalInitDate,
				DEFAULT_DATE_FORMAT
			);

			if (rangeSelectors.rangeKey === RangeKeyTimeRanges.Last24Hours) {
				return getSafeRangeSelectors({
					rangeEnd: `${formattedRangeEnd}T${formatTimestamp(
						intervalInitDate + 59 * 60000
					)}`,
					rangeKey,
					rangeStart: `${formattedRangeStart}T${formatTimestamp(
						intervalInitDate
					)}`
				});
			}

			return getSafeRangeSelectors({
				rangeEnd: formattedRangeEnd,
				rangeKey,
				rangeStart: formattedRangeStart
			});
		}

		return getSafeRangeSelectors({rangeEnd, rangeKey, rangeStart});
	};

	const sessionsResponse = useQuery<UserSessionData, UserSessionVariables>(
		UserSessionQuery,
		{
			fetchPolicy: fetchPolicyDefinition(rangeSelectors),
			variables: {
				...getDateRange(rangeSelectors, interval),
				channelId,
				entityId,
				entityType: SessionEntityTypes.Individual,
				keywords: query,
				page: page - 1,
				size: delta
			}
		}
	);

	const sessionsMappedResults = mapListResultsToProps(
		sessionsResponse,
		({eventsByUserSessions: {totalEvents, userSessions}}) => ({
			items: formatSessions(userSessions),
			total: totalEvents
		})
	);

	const handleChangeSelection = (index: number | null) => {
		resetPage();
		onPointSelect(index);
	};

	const handleQuery = (query: string) => {
		onQueryChange(query);
		setSearchValue(query);
	};

	const selected = hasSelectedPoint || selectedPoint;

	const {intervalInitDate, totalEvents = 0} =
		activityHistory[selectedPoint] || {};

	const date = selected
		? getDateRangeLabelFromDate(intervalInitDate, interval)
		: getDateRangeLabel(activityHistory, interval, 'intervalInitDate');

	return (
		<WrapSafeResults
			className='flex-grow-1 loading-root'
			error={error}
			errorProps={{
				className: 'flex-grow-1',
				onReload: refetch
			}}
			loading={loading}
			page={false}
			pageDisplay={false}
		>
			<Card.Body>
				<div className='align-items-center d-flex justify-content-end mt-3'>
					<SearchInput
						autoFocus
						className='search-input mr-3'
						onChange={setSearchValue}
						onSubmit={handleQuery}
						placeholder={Liferay.Language.get('search')}
						value={searchValue}
					/>

					<IntervalSelector
						activeInterval={interval}
						className='mr-3'
						disabled={isHourlyRangeKey(rangeSelectors.rangeKey)}
						onChange={(interval: Interval) => {
							onChangeInterval(interval);

							handleChangeSelection(null);
						}}
					/>

					<DropdownRangeKey
						legacy={false}
						onRangeSelectorChange={rangeSelectors => {
							onRangeSelectorsChange(rangeSelectors);

							handleChangeSelection(null);
						}}
						rangeSelectors={rangeSelectors}
					/>
				</div>

				<div className='individuals-activities-chart'>
					<ActivitiesChart
						alwaysShowSelectedTooltip
						hasSelectedPoint={hasSelectedPoint}
						history={activityHistory}
						interval={interval}
						onPointSelect={handleChangeSelection}
						rangeSelectors={rangeSelectors}
						selectedPoint={selectedPoint}
					/>

					<div className='selected-info'>
						<div className='activities-date d-flex align-items-baseline'>
							<div className='h4'>
								{activityHistory?.length
									? sub(
											Liferay.Language.get(
												'individuals-events-x'
											),
											[date]
									  )
									: Liferay.Language.get(
											'individuals-events'
									  )}
							</div>

							{selected && (
								<ClayButton
									className='button-root'
									displayType='link'
									onClick={() => handleChangeSelection(null)}
									size='sm'
								>
									{Liferay.Language.get(
										'clear-date-selection'
									)}
								</ClayButton>
							)}
						</div>

						<div className='details'>
							{getActivityLabel(
								(selected
									? totalEvents
									: activityTotal
								)?.toLocaleString()
							)}
						</div>
					</div>
				</div>
			</Card.Body>

			<Toolbar
				onQueryChange={onQueryChange}
				onSearchValueChange={handleQuery}
				query={query}
				searchValue={searchValue}
				showCheckbox={false}
				showSearch={false}
				total={sessionsMappedResults.total as number}
			/>

			<PaginatedVerticalTimeline
				{...sessionsMappedResults}
				delta={delta}
				initialExpanded={false}
				noResultsRenderer={
					<NoResultsDisplay
						description={
							<>
								<span className='mr-1'>
									{Liferay.Language.get(
										'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
									)}
								</span>

								<ClayLink
									href={
										URLConstants.IndividualProfilesDocument
									}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'learn-more-about-individuals'
									)}
								</ClayLink>
							</>
						}
						spacer
						title={Liferay.Language.get(
							'there-are-no-events-found'
						)}
					/>
				}
				onDeltaChange={onDeltaChange}
				onPageChange={onPageChange}
				page={page}
				timeZoneId={timeZoneId}
			/>
		</WrapSafeResults>
	);
};

export default ProfileCard;
