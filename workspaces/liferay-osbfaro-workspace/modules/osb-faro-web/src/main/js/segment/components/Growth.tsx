// @ts-nocheck - Fix it at this LRAC-13388

import * as API from 'shared/api';
import Card from 'shared/components/Card';
import ChartTooltip, {
	Alignments,
	Weights,
} from 'shared/components/chart-tooltip';
import ClayLink from '@clayui/link';
import ComposedChartWithEmptyState from 'shared/components/ComposedChartWithEmptyState';
import getCN from 'classnames';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useRef, useState} from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import URLConstants from 'shared/util/url-constants';
import {
	Area,
	AreaChart,
	CartesianGrid,
	Legend,
	ReferenceDot,
	ReferenceLine,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts';
import {
	AXIS,
	getAxisTickText,
	getYAxisLabel,
	getYAxisWidth,
} from 'shared/util/recharts';
import {
	changesListColumns,
	individualsListColumns,
} from 'shared/util/table-columns';
import {CHART_COLOR_NAMES} from 'shared/util/charts';
import {createDateKeysIMap} from 'shared/util/intervals';
import {DATE_CHANGED, NAME} from 'shared/util/pagination';
import {formatUTCDateFromUnix, getCustomDateFormat} from 'shared/util/date';
import {formatXAxisDate, getIntervals} from 'shared/util/charts';
import {get, isNil} from 'lodash';
import {getNetChange} from 'shared/util/change';
import {INDIVIDUALS} from 'shared/util/router';
import {OrderByDirections, RangeKeyTimeRanges} from 'shared/util/constants';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import {IndividualTypes} from 'segment/segment-editor/dynamic/utils/constants';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

const {
	greyjoy: CHART_BLACK,
	mormont: CHART_ORANGE,
	stark: CHART_BLUE,
} = CHART_COLOR_NAMES;

const INTERVAL = 'D';

type Data = {
	channelId: string;
	delta: number;
	groupId: string;
	id: string;
	individualTypes?: string[];
	modifiedDate: string;
	orderIOMap: string;
	page: number;
	query: string;
};

interface CHANGES_AGGREGATION_SHAPE {
	added: number;
	anonymousCount: number;
	knownCount: number;
	modifiedDate: number;
	removed: number;
	value: number;
}

const getAllMembers = (data: Data) => {
	const {
		channelId,
		delta,
		groupId,
		id,
		individualTypes,
		orderIOMap,
		page,
		query,
	} = data;

	return API.individuals.search({
		channelId,
		delta,
		groupId,
		individualSegmentId: id,
		individualTypes,
		orderIOMap,
		page,
		query,
	});
};

const getMemberChanges = (data: Data) => {
	const {delta, groupId, id, modifiedDate, orderIOMap, query} = data;

	return API.individualSegment.fetchMembershipChanges({
		delta,
		endDate: modifiedDate,
		groupId,
		id,
		orderIOMap,
		query,
		startDate: modifiedDate,
	});
};

interface ISegmentGrowthChartProps {
	alwaysShowSelectedTooltip?: boolean;
	data: Array<any>;
	hasSelectedPoint: boolean;
	height?: number;
	includeAnonymousUsers?: boolean;
	individualCounts?: {anonymousCount: number; knownCount: number};
	selectedPoint?: number;
	onSelectedPointChange?: (selectedPoint: number) => void;
}

interface ITooltipProps {
	active: boolean;
	payload: CHANGES_AGGREGATION_SHAPE[];
}

export const SegmentGrowthChart: React.FC<ISegmentGrowthChartProps> = ({
	data,
	hasSelectedPoint,
	height = 360,
	includeAnonymousUsers = true,
	individualCounts = {
		anonymousCount: 0,
		knownCount: 0,
	},
	onSelectedPointChange,
	selectedPoint,
}) => {
	const [legendHoverItem, setLegendHoverItem] = useState(null);
	const [mouseOutside, setMouseOutside] = useState(false);

	const {anonymousCount, knownCount} = individualCounts;

	const tooltipRef = useRef(null);

	const renderTooltip: React.FC<ITooltipProps> = ({active, payload}) => {
		if ((active && payload && !!payload.length) || hasSelectedPoint) {
			const {
				added,
				anonymousCount,
				knownCount,
				modifiedDate,
				removed,
				value,
			} = get(payload, [0, 'payload'], data[selectedPoint]);

			const change = [
				{
					label: Liferay.Language.get('added'),
					value: added,
				},
				{
					label: Liferay.Language.get('removed'),
					value: removed,
				},
			];

			const index = data.findIndex(
				(item) => item.modifiedDate === modifiedDate
			);

			const netChange = getNetChange(
				get(data[index - 1], 'value'),
				value
			);

			return (
				<div
					className="bb-tooltip-container"
					style={{position: 'static'}}
				>
					<ChartTooltip
						header={[
							{
								label: sub(
									Liferay.Language.get('as-of-x'),
									[
										formatUTCDateFromUnix(
											modifiedDate,
											getCustomDateFormat()
										),
									],
									false
								) as string,
								weight: Weights.Semibold,
							},
							{
								className: 'pb-0',
								label: () => (
									<span className="text-secondary">
										{sub(
											Liferay.Language.get(
												'x-total-members'
											),
											[
												<b className="mr-1" key="VALUE">
													{toLocale(value)}
												</b>,
											],
											false
										)}
									</span>
								),
							},
							...(includeAnonymousUsers
								? [
										{
											className: 'pb-0',
											label: () => (
												<span className="text-secondary">
													{sub(
														Liferay.Language.get(
															'x-anonymous-members'
														),
														[
															<b
																className="mr-1"
																key="VALUE"
															>
																{toLocale(
																	anonymousCount
																)}
															</b>,
														],
														false
													)}
												</span>
											),
										},
									]
								: []),
							{
								label: () => (
									<span className="text-secondary">
										{sub(
											Liferay.Language.get(
												'x-known-members'
											),
											[
												<b className="mr-1" key="VALUE">
													{toLocale(knownCount)}
												</b>,
											],
											false
										)}
									</span>
								),
							},
						].map((column) => ({
							columns: [column],
						}))}
						rows={(isNil(netChange)
							? change
							: [
									...change,
									{
										label: Liferay.Language.get(
											'net-change'
										),
										value: `${netChange[0]}(${netChange[1]}%)`,
									},
								]
						).map(({label, value}, i, array) => {
							const className =
								i < array.length - 1 ? 'pb-0' : null;

							return {
								columns: [
									{
										className,
										label,
										weight: Weights.Normal,
									},
									{
										align: Alignments.Right,
										className,
										label: value,
										weight: Weights.Semibold,
									},
								],
							};
						})}
					/>
				</div>
			);
		}

		return null;
	};

	interface ICommonAreaChartStyles {
		isAnimationActive: boolean;
		legendType: string;
		stackId: string;
	}

	const commonAreaChartStyles: ICommonAreaChartStyles = {
		isAnimationActive: true,
		legendType: 'circle',
		stackId: 'count',
	};

	const showFixedTooltip = hasSelectedPoint && mouseOutside;

	const dateKeysIMap = createDateKeysIMap(INTERVAL, data, 'modifiedDate');

	const intervals = getIntervals(
		RangeKeyTimeRanges.Last30Days,
		data.map(({modifiedDate}) => modifiedDate),
		INTERVAL,
		dateKeysIMap
	);

	const yAxisWidth = getYAxisWidth(data, 'value');

	const handleClick = (data) => {
		if (data?.activeTooltipIndex === undefined) {
			return;
		}

		onSelectedPointChange(data?.activeTooltipIndex);
	};

	return (
		<ComposedChartWithEmptyState
			emptyDescription={
				<>
					<span className="mr-1">
						{Liferay.Language.get(
							'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
						)}
					</span>

					<ClayLink
						href={URLConstants.SegmentsOverviewTabDocumentationLink}
						key="DOCUMENTATION"
						target="_blank"
					>
						{Liferay.Language.get(
							'learn-more-about-segment-membership'
						)}
					</ClayLink>
				</>
			}
			emptyTitle={Liferay.Language.get(
				'there-is-no-data-for-segment-membership'
			)}
			showEmptyState={!intervals.length}
		>
			<ResponsiveContainer height={height}>
				<AreaChart
					data={data}
					onClick={handleClick}
					onMouseLeave={() => setMouseOutside(true)}
					onMouseMove={() => setMouseOutside(false)}
				>
					<CartesianGrid
						stroke={AXIS.gridStroke}
						strokeDasharray="3 3"
						vertical={false}
					/>

					<XAxis
						axisLine={{stroke: AXIS.borderStroke}}
						dataKey="modifiedDate"
						domain={['dataMin', 'dataMax']}
						interval="preserveStart"
						padding={{left: 20, right: 20}}
						tick={getAxisTickText('x', (value) =>
							formatXAxisDate(
								value,
								RangeKeyTimeRanges.Last30Days,
								INTERVAL,
								dateKeysIMap
							)
						)}
						tickLine={false}
						tickMargin={12}
						ticks={intervals}
						type="number"
					/>

					<XAxis
						axisLine={{stroke: AXIS.borderStroke}}
						dataKey="modifiedDate"
						orientation="top"
						stroke={AXIS.gridStroke}
						tick={false}
						tickLine={false}
						xAxisId="top"
					/>

					<YAxis
						allowDecimals={false}
						axisLine={{stroke: AXIS.borderStroke}}
						label={getYAxisLabel(
							Liferay.Language.get('membership'),
							'left',
							yAxisWidth
						)}
						name={Liferay.Language.get('membership')}
						stroke={AXIS.gridStroke}
						tick={getAxisTickText('y')}
						tickCount={6}
						tickLine={false}
						type="number"
						width={yAxisWidth}
					/>

					<YAxis
						axisLine={{stroke: AXIS.borderStroke}}
						orientation="right"
						stroke={AXIS.gridStroke}
						tick={false}
						tickLine={false}
						type="number"
						width={1}
						yAxisId="right"
					/>

					<Legend
						align="right"
						formatter={(value, {count}) => (
							<span className="legend-text-color">
								{`${value}:`}

								<b className="ml-1">{toLocale(count)}</b>
							</span>
						)}
						iconSize={8}
						onMouseEnter={({dataKey}) =>
							setLegendHoverItem(dataKey)
						}
						onMouseLeave={() => setLegendHoverItem(null)}
						payload={[
							{
								color: CHART_BLUE,
								count: knownCount,
								dataKey: 'knownCount',
								type: 'circle',
								value: Liferay.Language.get('known-members'),
							},
							...(includeAnonymousUsers
								? [
										{
											color: CHART_ORANGE,
											count: anonymousCount,
											dataKey: 'anonymousCount',
											type: 'circle',
											value: Liferay.Language.get(
												'anonymous-members'
											),
										},
									]
								: []),
							{
								color: CHART_BLACK,
								count: anonymousCount + knownCount,
								dataKey: 'individualCount',
								type: 'circle',
								value: Liferay.Language.get('total-members'),
							},
						]}
						verticalAlign="bottom"
						wrapperStyle={{
							color: AXIS.textColor,
							fontSize: '14px',
							lineHeight: '21px',
							paddingBottom: '22px',
						}}
					/>

					<Tooltip
						content={renderTooltip}
						cursor={!intervals.length}
						ref={tooltipRef}
						wrapperStyle={
							showFixedTooltip
								? {
										visibility: 'visible',
									}
								: null
						}
					/>

					<ReferenceLine
						strokeWidth={1}
						x={
							showFixedTooltip
								? data[selectedPoint].modifiedDate
								: null
						}
					/>

					<ReferenceDot
						fill={CHART_BLUE}
						fillOpacity={
							legendHoverItem === 'anonymousCount' ? 0.1 : 1
						}
						isFront
						r={4}
						stroke="none"
						x={
							hasSelectedPoint
								? data[selectedPoint].modifiedDate
								: null
						}
						y={
							hasSelectedPoint
								? data[selectedPoint].knownCount
								: null
						}
					/>

					{includeAnonymousUsers && (
						<ReferenceDot
							fill={CHART_ORANGE}
							fillOpacity={
								legendHoverItem === 'knownCount' ? 0.1 : 1
							}
							isFront
							r={4}
							stroke="none"
							x={
								hasSelectedPoint
									? data[selectedPoint].modifiedDate
									: null
							}
							y={
								hasSelectedPoint
									? data[selectedPoint].knownCount +
										data[selectedPoint].anonymousCount
									: null
							}
						/>
					)}

					<Area
						{...commonAreaChartStyles}
						activeDot={{r: 4, stroke: CHART_BLUE}}
						dataKey="knownCount"
						fill={CHART_BLUE}
						fillOpacity={
							legendHoverItem === 'anonymousCount' ? 0.1 : 0.2
						}
						isAnimationActive={false}
						name={Liferay.Language.get('known-members')}
						stroke={CHART_BLUE}
						strokeOpacity={
							legendHoverItem === 'anonymousCount' ? 0.2 : 1
						}
					/>

					{includeAnonymousUsers && (
						<Area
							{...commonAreaChartStyles}
							activeDot={{r: 4, stroke: CHART_ORANGE}}
							dataKey="anonymousCount"
							fill={CHART_ORANGE}
							fillOpacity={
								legendHoverItem === 'knownCount' ? 0.1 : 0.2
							}
							isAnimationActive={false}
							name={Liferay.Language.get('anonymous-members')}
							stroke={CHART_ORANGE}
							strokeOpacity={
								legendHoverItem === 'knownCount' ? 0.2 : 1
							}
						/>
					)}
				</AreaChart>
			</ResponsiveContainer>
		</ComposedChartWithEmptyState>
	);
};

export const SelectedPointInfo: React.FC = () => (
	<div className="selected-point-info">
		<div className="h4">{Liferay.Language.get('known-members')}</div>
	</div>
);

interface ISegmentGrowthWithList {
	channelId: string;
	className: string;
	data: CHANGES_AGGREGATION_SHAPE[];
	groupId: string;
	hasSelectedPoint: boolean;
	id: string;
	includeAnonymousUsers?: boolean;
	individualCounts?: {anonymousCount: number; knownCount: number};
	selectedPoint: number;
	timeZoneId: string;
}

const SegmentGrowthWithList: React.FC<ISegmentGrowthWithList> = ({
	channelId,
	className,
	data,
	groupId,
	hasSelectedPoint,
	id,
	includeAnonymousUsers,
	individualCounts,
	selectedPoint,
	timeZoneId,
}) => {
	const [showMembershipList, setShowMembershipList] = useState(true);

	const fetchMembers = (params) => {
		const fetchMembersFn = hasSelectedPoint
			? getMemberChanges
			: getAllMembers;

		return fetchMembersFn(params);
	};

	const getColumns = () => {
		if (hasSelectedPoint) {
			return [
				changesListColumns.getIndividualName({channelId, groupId}),
				changesListColumns.individualEmail,
				changesListColumns.getDateFirst(timeZoneId),
				changesListColumns.getOperation(timeZoneId),
			];
		}

		return [
			individualsListColumns.getName({channelId, groupId}),
			individualsListColumns.email,
			individualsListColumns.getDateCreated(timeZoneId),
		];
	};

	const {modifiedDate} = get(data, selectedPoint, {modifiedDate: 0});

	const paginationParams = useStatefulPagination(null, {
		initialDelta: 20,
		initialOrderIOMap: hasSelectedPoint
			? OrderedMap({
					[DATE_CHANGED]: new OrderParams({
						field: DATE_CHANGED,
						sortOrder: OrderByDirections.Descending,
					}),
				})
			: OrderedMap({
					[NAME]: new OrderParams({
						field: NAME,
						sortOrder: OrderByDirections.Ascending,
					}),
				}),
		initialPage: 0,
	});

	const dateKeysIMap = createDateKeysIMap(INTERVAL, data, 'modifiedDate');

	const intervals = getIntervals(
		RangeKeyTimeRanges.Last30Days,
		data.map(({modifiedDate}) => modifiedDate),
		INTERVAL,
		dateKeysIMap
	);

	return (
		<Card.Body
			className={getCN('segment-growth-root', className)}
			noPadding
		>
			<div className="segment-growth-chart-container">
				<SegmentGrowthChart
					alwaysShowSelectedTooltip
					data={data}
					hasSelectedPoint={hasSelectedPoint}
					height={360}
					includeAnonymousUsers={includeAnonymousUsers}
					individualCounts={individualCounts}
					selectedPoint={selectedPoint}
				/>
			</div>

			{showMembershipList && (
				<>
					<SelectedPointInfo />
					<SearchableEntityTable
						{...paginationParams}
						columns={getColumns()}
						dataSourceFn={fetchMembers}
						dataSourceParams={{
							channelId,
							groupId,
							id,
							modifiedDate,
							...(includeAnonymousUsers === false
								? {individualTypes: [IndividualTypes.KNOWN]}
								: {}),
						}}
						entityType={INDIVIDUALS}
						noResultsRenderer={() => {

							// Check if intervals exists after fetch members
							// to show/hide membership list based on intervals of chart
							// to avoid render two empty states

							setShowMembershipList(
								!!individualCounts.knownCount ||
									!!intervals.length
							);

							return (
								<NoResultsDisplay
									description={
										<>
											<span className="mr-1">
												{Liferay.Language.get(
													'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
												)}
											</span>

											<ClayLink
												href={
													URLConstants.SegmentsMembershipDocumentationLink
												}
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
										'no-members-were-found-on-the-selected-time-period'
									)}
								/>
							);
						}}
						rowIdentifier="id"
					/>
				</>
			)}
		</Card.Body>
	);
};

export default SegmentGrowthWithList;
