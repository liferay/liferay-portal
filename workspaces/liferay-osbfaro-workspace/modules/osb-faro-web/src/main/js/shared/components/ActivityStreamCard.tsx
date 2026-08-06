import ActivitiesChart from 'contacts/components/ActivitiesChart';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Loading from 'shared/components/Loading';
import React from 'react';
import SearchInput from 'shared/components/SearchInput';
import VerticalTimeline from 'shared/components/VerticalTimeline';
import {ActivityHistoryPoint} from 'shared/util/activities';
import {ChartView} from 'shared/components/ChartViewSelector';
import {compose, withPaginationBar} from 'shared/hoc';
import {getIcon, getStatsColor} from 'shared/util/metrics';
import {Interval, RangeSelectors} from 'shared/types';
import {isNil} from 'lodash';
import {mapListResultsToProps} from 'shared/util/mappers';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {formatPercent, toThousands} from 'shared/util/numbers';
import {TrendClassification} from 'segment/types';
import {withEmpty} from 'cerebro-shared/hocs/utils';
import {withError, withLoading, WrapSafeResults} from 'shared/hoc/util';

const PaginatedVerticalTimeline = compose<any>(
	withPaginationBar(),
	withLoading({spacer: true}),
	withError({page: false}),
	withEmpty()
)(VerticalTimeline);

export interface TrendSummary {
	classification?: TrendClassification;
	percentage: number;
	value: number;
}

interface IActivityStreamCardProps {
	activityHistory: ActivityHistoryPoint[];
	chartError?: unknown;
	chartLoading: boolean;
	chartTooltipRenderRows?: (
		data: ActivityHistoryPoint
	) => {label: string; value: string}[];
	chartView?: ChartView;
	delta: number;
	emptyChartContent?: React.ReactNode;
	footerLabel: React.ReactNode;
	interval: Interval;
	noResultsRenderer: React.ReactNode;
	onChartReload?: () => void;
	onClearDateSelection: () => void;
	onDeltaChange: (delta: number) => void;
	onPageChange: (page: number) => void;
	onPointSelect: (index: number | null) => void;
	onSearchChange: (value: string) => void;
	onSearchSubmit: (value: string) => void;
	page: number;
	rangeSelectors: RangeSelectors;
	searchValue: string;
	selected: boolean;
	selectedPoint?: number;
	sessionsMappedResults: ReturnType<typeof mapListResultsToProps>;
	timeZoneId?: string;
	trendSummary?: TrendSummary;
}

/**
 * Shared activity-stream card used by both the account and individual detail
 * pages. It renders a search box, an optional trend summary (activities count
 * plus change versus the previous period — account only), the interactive
 * activities chart, a chart footer, and the paginated vertical timeline. The
 * card is presentational: each page owns its own queries and feeds the mapped
 * data, labels, and handlers in. Card title/description come from the wrapping
 * BaseCard, so each page keeps its own heading.
 */
const ActivityStreamCard: React.FC<IActivityStreamCardProps> = ({
	activityHistory,
	chartError,
	chartLoading,
	chartTooltipRenderRows,
	chartView,
	delta,
	emptyChartContent,
	footerLabel,
	interval,
	noResultsRenderer,
	onChartReload,
	onClearDateSelection,
	onDeltaChange,
	onPageChange,
	onPointSelect,
	onSearchChange,
	onSearchSubmit,
	page,
	rangeSelectors,
	searchValue,
	selected,
	selectedPoint,
	sessionsMappedResults,
	timeZoneId,
	trendSummary,
}) => {
	const isChartEmpty =
		!activityHistory.length ||
		activityHistory.every(({totalEvents}) => !totalEvents);

	// The mapped total counts events, which stays positive even when a search
	// or filter leaves no sessions to show. Treat the session list as empty
	// whenever there are no session items so the timeline renders its empty
	// state (and hides pagination) instead of a blank list.

	const sessionsTotal = sessionsMappedResults.items?.length
		? sessionsMappedResults.total
		: 0;

	return (
		<>
			<Card.Body className="pb-0">
				<SearchInput
					onChange={onSearchChange}
					onSubmit={onSearchSubmit}
					placeholder={Liferay.Language.get('search')}
					value={searchValue}
				/>
			</Card.Body>

			{chartLoading ? (
				<Card.Body>
					<Loading spacer />
				</Card.Body>
			) : (
				<WrapSafeResults
					className="flex-grow-1 loading-root"
					error={chartError}
					errorProps={{
						className: 'flex-grow-1',
						onReload: onChartReload,
					}}
					loading={false}
					page={false}
					pageDisplay={false}
				>
					<Card.Body className="pt-0">
						<div className="activity-stream">
							{trendSummary && (
								<div className="trend-summary mb-2 mt-4">
									<div className="font-weight-semi-bold">
										<Text size={7}>
											{sub(
												Liferay.Language.get(
													'x-activities'
												),
												[
													toThousands(
														trendSummary.value
													),
												]
											)}
										</Text>
									</div>

									<div className="text-secondary">
										{!isNil(trendSummary.classification) &&
											trendSummary.classification !==
												TrendClassification.Neutral && (
												<ClayIcon
													style={{
														color: getStatsColor(
															trendSummary.classification
														),
													}}
													symbol={
														getIcon(
															trendSummary.percentage
														) ?? ''
													}
												/>
											)}

										{sub(
											Liferay.Language.get(
												'x-vs-previous-period'
											),
											[
												<span
													className="mr-1"
													key="percentage"
													style={{
														color: getStatsColor(
															trendSummary.classification ||
																''
														),
													}}
												>
													{formatPercent(
														Math.abs(
															trendSummary.percentage
														),
														1
													)}
												</span>,
											],
											false
										)}
									</div>
								</div>
							)}

							<div className="position-relative">
								<ActivitiesChart
									alwaysShowSelectedTooltip
									chartView={chartView}
									hideGrid={isChartEmpty}
									history={activityHistory}
									interval={interval}
									onPointSelect={onPointSelect}
									rangeSelectors={rangeSelectors}
									selectedPoint={selectedPoint}
									tooltipRenderRows={chartTooltipRenderRows}
								/>

								{isChartEmpty && emptyChartContent}
							</div>

							<div className="chart-footer mt-3">
								<div className="d-flex align-items-baseline">
									<Text
										color="secondary"
										size={4}
										weight="semi-bold"
									>
										{footerLabel}
									</Text>

									{selected && (
										<ClayButton
											className="ml-2 p-0"
											displayType="link"
											onClick={onClearDateSelection}
											size="sm"
										>
											{Liferay.Language.get(
												'clear-date-selection'
											)}
										</ClayButton>
									)}
								</div>
							</div>
						</div>
					</Card.Body>

					<Card.Body className="p-0">
						<PaginatedVerticalTimeline
							{...sessionsMappedResults}
							delta={delta}
							initialExpanded={false}
							noResultsRenderer={noResultsRenderer}
							onDeltaChange={onDeltaChange}
							onPageChange={onPageChange}
							page={page}
							timeZoneId={timeZoneId}
							total={sessionsTotal}
						/>
					</Card.Body>
				</WrapSafeResults>
			)}
		</>
	);
};

export default ActivityStreamCard;
