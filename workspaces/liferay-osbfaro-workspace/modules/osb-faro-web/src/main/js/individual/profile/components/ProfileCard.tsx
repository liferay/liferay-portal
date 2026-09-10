import ActivitiesChart from 'contacts/components/ActivitiesChart';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import DayList from 'shared/components/DayList';
import EventMetricQuery, {
	EventMetricsData,
	EventMetricsVariables,
} from 'shared/queries/EventMetricQuery';
import IntervalSelector from 'shared/components/IntervalSelector';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useState} from 'react';
import SearchInput from 'shared/components/SearchInput';
import Toolbar from 'shared/components/toolbar';
import URLConstants from 'shared/util/url-constants';
import UserSessionQuery, {
	UserSessionData,
	UserSessionVariables,
} from 'shared/queries/UserSessionQuery';
import {compose, withPaginationBar} from 'shared/hoc';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {fetchPolicyDefinition} from 'shared/util/graphql';
import {
	formatSessions,
	getActivityLabel,
	mapEventMetricToActivityHistory,
} from 'shared/util/activities';
import {getDateRangeLabel, getDateRangeLabelFromDate} from 'shared/util/date';
import {getSafeRangeSelectors} from 'shared/util/util';
import {getSessionsDateRange} from 'shared/util/activityDateRange';
import {Individual} from 'shared/util/records';
import {Interval, RangeSelectors} from 'shared/types';
import {isHourlyRangeKey} from 'shared/util/time';
import {mapListResultsToProps} from 'shared/util/mappers';
import {SessionEntityTypes, Sizes} from 'shared/util/constants';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useQuery} from '@apollo/client';
import {useSelectedPoint} from 'shared/hooks/useSelectedPoint';
import {withEmpty} from 'cerebro-shared/hocs/utils';
import {withError, withLoading, WrapSafeResults} from 'shared/hoc/util';

const PaginatedDayList = compose<any>(
	withPaginationBar(),
	withLoading(),
	withError({page: false}),
	withEmpty()
)(DayList);

interface IProfileCardProps extends React.HTMLAttributes<HTMLElement> {
	channelId: string;
	delta: number;
	entity: Individual;
	interval: Interval;
	groupId: string;
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
	groupId,
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
	timeZoneId,
}) => {
	const {hasSelectedPoint, onPointSelect, selectedPoint} = useSelectedPoint();
	const [searchValue, setSearchValue] = useState<string>('');

	const LDPEnabled = useLDPEnabled({groupId});

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
		total: activityTotal,
	} = mapListResultsToProps(activityResponse, ({eventMetric}) => ({
		items: mapEventMetricToActivityHistory(eventMetric),
		total: eventMetric.totalEventsMetric?.value,
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

	const sessionsMappedResults = mapListResultsToProps(
		sessionsResponse,
		({eventsByUserSessions: {totalPageGroupsMetric, userSessions}}) => ({
			items: formatSessions(userSessions, {
				channelId,
				groupId,
				rangeSelectors,
			}),
			total: totalPageGroupsMetric?.value ?? 0,
		})
	);

	const handleChangeSelection = (index: number | null) => {
		resetPage();
		onPointSelect(index ?? undefined);
	};

	const handleQuery = (query: string) => {
		onQueryChange(query);
		setSearchValue(query);
	};

	const selected = hasSelectedPoint || selectedPoint;

	const {intervalInitDate, totalEvents = 0} =
		(selectedPoint !== undefined && activityHistory[selectedPoint]) || {};

	const date = selected
		? getDateRangeLabelFromDate(intervalInitDate, interval)
		: getDateRangeLabel(activityHistory, interval, 'intervalInitDate');

	const renderNoResults = () => {
		if (sessionsMappedResults?.loading) {
			return (
				<NoResultsDisplay>
					<Loading key="LOADING" />
				</NoResultsDisplay>
			);
		}

		if (!sessionsMappedResults?.items?.length) {
			if (query) {
				return (
					<NoResultsDisplay
						description={Liferay.Language.get(
							'review-your-search-and-try-again'
						)}
						icon={{
							border: false,
							size: Sizes.XXXLarge,
							symbol: 'ac_no_results_found',
						}}
						spacer
						title={Liferay.Language.get('no-results-were-found')}
					>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={() => {
								onQueryChange('');
								setSearchValue('');
							}}
						>
							{Liferay.Language.get('clear-search')}
						</ClayButton>
					</NoResultsDisplay>
				);
			}

			return (
				<NoResultsDisplay
					description={
						<>
							<span className="mr-1">
								{Liferay.Language.get(
									'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources,-or-you-can-try-a-different-date-range'
								)}
							</span>

							<ClayLink
								href={URLConstants.IndividualProfilesDocument}
								key="DOCUMENTATION"
								target="_blank"
							>
								{Liferay.Language.get(
									'learn-more-about-individuals'
								)}
							</ClayLink>
						</>
					}
					spacer
					title={Liferay.Language.get(
						'there-is-no-activity-on-the-selected-period'
					)}
				/>
			);
		}
	};

	return (
		<WrapSafeResults
			className="flex-grow-1 loading-root"
			error={error}
			errorProps={{
				className: 'flex-grow-1',
				onReload: refetch,
			}}
			loading={loading}
			page={false}
			pageDisplay={false}
		>
			<Card.Body>
				<div className="align-items-center d-flex justify-content-end mt-3">
					<SearchInput
						autoFocus
						className="search-input mr-3"
						onChange={setSearchValue}
						onSubmit={handleQuery}
						placeholder={Liferay.Language.get('search')}
						value={searchValue}
					/>

					<IntervalSelector
						activeInterval={interval}
						className="mr-3"
						disabled={isHourlyRangeKey(rangeSelectors.rangeKey)}
						onChange={(interval: Interval) => {
							onChangeInterval(interval);

							handleChangeSelection(null);
						}}
					/>

					<DropdownRangeKey
						legacy={false}
						onRangeSelectorChange={(rangeSelectors) => {
							onRangeSelectorsChange(rangeSelectors);

							handleChangeSelection(null);
						}}
						rangeSelectors={rangeSelectors}
					/>
				</div>

				<div className="individuals-activities-chart">
					<ActivitiesChart
						alwaysShowSelectedTooltip
						history={activityHistory}
						interval={interval}
						LDPEnabled={LDPEnabled}
						onPointSelect={handleChangeSelection}
						rangeSelectors={rangeSelectors}
						selectedPoint={selectedPoint}
					/>

					<div className="selected-info">
						<div className="activities-date d-flex align-items-baseline">
							{!!activityHistory?.length && (
								<div className="h4">{date}</div>
							)}

							{selected && (
								<ClayButton
									className="button-root"
									displayType="link"
									onClick={() => handleChangeSelection(null)}
									size="sm"
								>
									{Liferay.Language.get(
										'clear-date-selection'
									)}
								</ClayButton>
							)}
						</div>

						<div className="details">
							{getActivityLabel(
								(selected ? totalEvents : activityTotal) ?? 0
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

			<PaginatedDayList
				{...sessionsMappedResults}
				delta={delta}
				initialExpanded={false}
				LDPEnabled={LDPEnabled}
				noResultsRenderer={renderNoResults()}
				onDeltaChange={onDeltaChange}
				onPageChange={onPageChange}
				page={page}
				resultsMessagePlural={Liferay.Language.get(
					'showing-x-to-x-of-x-page-entries'
				)}
				resultsMessageSingular={Liferay.Language.get(
					'showing-x-to-x-of-x-page-entry'
				)}
				timeZoneId={timeZoneId}
			/>
		</WrapSafeResults>
	);
};

export default ProfileCard;
