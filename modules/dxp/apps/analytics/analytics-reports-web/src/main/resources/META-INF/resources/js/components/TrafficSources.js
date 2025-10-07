/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useIsMounted, useStateSafe} from '@liferay/frontend-js-react-web';
import className from 'classnames';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useMemo, useState} from 'react';
import {Cell, Pie, PieChart, Tooltip} from 'recharts';

import {
	ChartDispatchContext,
	ChartStateContext,
} from '../context/ChartStateContext';
import ConnectionContext from '../context/ConnectionContext';
import {StoreDispatchContext, StoreStateContext} from '../context/StoreContext';
import {numberFormat} from '../utils/numberFormat';
import EmptyPieChart from './EmptyPieChart';
import Hint from './Hint';

const COLORS_MAP = {
	direct: '#FF73C3',
	organic: '#4B9FFF',
	paid: '#FFB46E',
	referral: '#FF5F5F',
	social: '#50D2A0',
};

const PIE_CHART_SIZES = {
	height: 140,
	innerRadius: 40,
	outerRadius: 70,
	paddingAngle: 1,
	width: 140,
};

/**
 * Used when the traffic source name is not within the COLORS_MAP
 */
const FALLBACK_COLOR = '#e92563';

const getColorByName = (name) => COLORS_MAP[name] || FALLBACK_COLOR;

export default function TrafficSources({dataProvider, onTrafficSourceClick}) {
	const [highlighted, setHighlighted] = useState(null);
	const isMounted = useIsMounted();

	const {validAnalyticsConnection} = useContext(ConnectionContext);

	const dispatch = useContext(StoreDispatchContext);

	const chartDispatch = useContext(ChartDispatchContext);

	const {languageTag, publishedToday} = useContext(StoreStateContext);

	const {pieChartLoading, timeSpanKey, timeSpanOffset} =
		useContext(ChartStateContext);

	const [trafficSources, setTrafficSources] = useStateSafe([]);

	const pieChartWrapperClasses = className('pie-chart-wrapper', {
		'pie-chart-wrapper--loading': pieChartLoading,
	});

	useEffect(() => {
		if (validAnalyticsConnection) {
			chartDispatch({
				payload: {
					loading: true,
				},
				type: 'SET_PIE_CHART_LOADING',
			});
			dataProvider()
				.then((trafficSources) => {
					if (isMounted()) {
						setTrafficSources(trafficSources);
					}
				})
				.catch(() => {
					if (isMounted()) {
						setTrafficSources([]);
						dispatch({type: 'ADD_WARNING'});
					}
				})
				.finally(() => {
					if (isMounted()) {
						chartDispatch({
							payload: {
								loading: false,
							},
							type: 'SET_PIE_CHART_LOADING',
						});
					}
				});
		}
	}, [
		chartDispatch,
		dispatch,
		dataProvider,
		setTrafficSources,
		timeSpanKey,
		timeSpanOffset,
		validAnalyticsConnection,
		isMounted,
	]);

	const fullPieChart = useMemo(
		() =>
			validAnalyticsConnection &&
			!publishedToday &&
			trafficSources?.some(({value}) => value),
		[publishedToday, trafficSources, validAnalyticsConnection]
	);

	const missingTrafficSourceValue = useMemo(
		() => trafficSources?.some(({value}) => value === undefined),
		[trafficSources]
	);

	useEffect(() => {
		if (missingTrafficSourceValue) {
			dispatch({type: 'ADD_WARNING'});
		}
	}, [dispatch, missingTrafficSourceValue]);

	function handleLegendMouseEnter(name) {
		setHighlighted(name);
	}

	function handleLegendMouseLeave() {
		setHighlighted(null);
	}

	return (
		<>
			<div className="mt-3 sheet-subtitle">
				{Liferay.Language.get('traffic-channel')}

				<Hint
					message={Liferay.Language.get('traffic-channels-help')}
					secondary={true}
					title={Liferay.Language.get('traffic-channel')}
				/>
			</div>

			{!fullPieChart && !missingTrafficSourceValue && (
				<div className="mb-3 text-secondary">
					{Liferay.Language.get(
						'your-page-has-no-incoming-traffic-from-traffic-channels-yet'
					)}
				</div>
			)}
			<div className={pieChartWrapperClasses}>
				{pieChartLoading && (
					<ClayLoadingIndicator
						className="chart-loading-indicator"
						small
					/>
				)}

				<div className="pie-chart-wrapper--legend">
					<table>
						<tbody>
							{trafficSources?.map((entry) => {
								const hasDetails =
									entry?.value > 0 && entry?.endpointURL;

								return (
									<tr key={entry.name}>
										<td
											className="px-0"
											onMouseOut={handleLegendMouseLeave}
											onMouseOver={() =>
												handleLegendMouseEnter(
													entry.name
												)
											}
										>
											<span
												className="pie-chart-wrapper--legend--dot"
												style={{
													backgroundColor:
														getColorByName(
															entry.name
														),
												}}
											></span>
										</td>

										<td
											className="c-py-1 text-secondary"
											onMouseOut={handleLegendMouseLeave}
											onMouseOver={() =>
												handleLegendMouseEnter(
													entry.name
												)
											}
										>
											{validAnalyticsConnection &&
											!publishedToday &&
											hasDetails ? (
												<ClayButton
													className="px-0 py-1 text-primary"
													displayType="link"
													onClick={() =>
														onTrafficSourceClick(
															trafficSources,
															entry.name,
															false
														)
													}
													small
												>
													{entry.title}
												</ClayButton>
											) : (
												<span>{entry.title}</span>
											)}
										</td>

										<td className="text-secondary">
											<Hint
												message={entry.helpMessage}
												title={entry.title}
											/>
										</td>

										<td className="font-weight-semi-bold">
											{validAnalyticsConnection &&
											!publishedToday &&
											entry.value !== undefined
												? numberFormat(
														languageTag,
														entry.value,
														{
															useCompact: true,
														}
													)
												: '-'}
										</td>
									</tr>
								);
							})}
						</tbody>
					</table>
				</div>

				{!fullPieChart && (
					<EmptyPieChart
						height={PIE_CHART_SIZES.height}
						innerRadius={PIE_CHART_SIZES.innerRadius}
						outerRadius={PIE_CHART_SIZES.outerRadius}
						width={PIE_CHART_SIZES.width}
					/>
				)}

				{fullPieChart && (
					<div className="pie-chart-wrapper--chart">
						<PieChart
							height={PIE_CHART_SIZES.height}
							width={PIE_CHART_SIZES.width}
						>
							<Pie
								cx="50%"
								cy="50%"
								data={trafficSources}
								dataKey="value"
								innerRadius={PIE_CHART_SIZES.innerRadius}
								isAnimationActive={false}
								nameKey="name"
								outerRadius={PIE_CHART_SIZES.outerRadius}
								paddingAngle={PIE_CHART_SIZES.paddingAngle}
							>
								{trafficSources.map((entry, i) => {
									const fillColor = getColorByName(
										entry.name
									);

									const cellClasses = className({
										dim:
											highlighted &&
											entry.name !== highlighted,
									});

									return (
										<Cell
											className={cellClasses}
											fill={fillColor}
											key={i}
											onMouseOut={handleLegendMouseLeave}
											onMouseOver={() =>
												handleLegendMouseEnter(
													entry.name
												)
											}
										/>
									);
								})}
							</Pie>

							<Tooltip
								animationDuration={0}
								content={<TrafficSourcesCustomTooltip />}
								formatter={(value, name, iconType) => {
									return [
										numberFormat(languageTag, value),
										name,
										iconType,
									];
								}}
								separator=": "
							/>
						</PieChart>
					</div>
				)}
			</div>
		</>
	);
}

function TrafficSourcesCustomTooltip(props) {
	const {formatter, payload, separator = ''} = props;

	return (
		<div className="custom-tooltip popover">
			<p className="mx-2 popover-header py-1">
				<b>
					{

						// eslint-disable-next-line @liferay/no-length-jsx-expression
						payload.length && payload[0].payload.title
					}
				</b>
			</p>

			<ul className="list-unstyled mb-0 p-2 popover-body">
				<>
					{payload.map((item) => {

						// eslint-disable-next-line no-unused-vars
						const [value, _name, iconType] = formatter
							? formatter(item.value, item.name, item.iconType)
							: [item.value, item.name, item.iconType];

						const {payload} = item;

						return (
							<React.Fragment key={item.name}>
								<li>
									{Liferay.Language.get('visitors')}

									{separator}

									<b>{value}</b>
								</li>

								<li>
									{Liferay.Language.get('traffic-share')}

									{separator}

									<b>{`${payload.share}%`}</b>
								</li>
							</React.Fragment>
						);
					})}
				</>
			</ul>
		</div>
	);
}

TrafficSources.propTypes = {
	dataProvider: PropTypes.func.isRequired,
	onTrafficSourceClick: PropTypes.func.isRequired,
};
