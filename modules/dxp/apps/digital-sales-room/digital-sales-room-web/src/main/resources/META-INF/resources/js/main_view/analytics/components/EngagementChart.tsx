/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import moment from 'moment';
import React from 'react';
import {
	CartesianGrid,
	Line,
	LineChart,
	ReferenceLine,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts';

import {
	IEngagementChartItem,
	IEngagementChartProps,
} from '../../../common/utils/types';
import EngagementChartTooltip from './EngagementChartTooltip';

const formatXAxisDate = (tickItem: string | number): string => {
	return moment(tickItem).format('MMM DD');
};

function EngagementChart({data}: IEngagementChartProps) {
	return (
		<ResponsiveContainer aspect={2} width="100%">
			<LineChart
				data={data}
				margin={{
					bottom: 5,
					left: 0,
					right: 20,
					top: 20,
				}}
			>
				<defs>
					<linearGradient
						id="lineGradient"
						x1="0"
						x2="1"
						y1="0"
						y2="0"
					>
						<stop offset="0%" stopColor="#E0C2FF" />

						<stop offset="50%" stopColor="#AA33FF" />

						<stop offset="100%" stopColor="#E0C2FF" />
					</linearGradient>
				</defs>

				{data?.map(
					(
						engagementChartItem: IEngagementChartItem,
						index: number
					) => {
						return (
							<ReferenceLine
								key={`bg-strip-${index}`}
								stroke="#F2E5FF"
								strokeOpacity={0.3}
								strokeWidth={50}
								x={engagementChartItem.date}
							/>
						);
					}
				)}

				<CartesianGrid
					stroke="#aaa"
					strokeDasharray="3 3"
					vertical={(props: any) => (
						<line
							key={props.key}
							stroke="none"
							x1={props.x1}
							x2={props.x2}
							y1={props.y1}
							y2={props.y2}
						/>
					)}
				/>

				<Line
					activeDot={{
						fill: '#AA33FF',
						r: 6,
						stroke: '#AA33FF',
					}}
					dataKey="numberOfVisits"
					dot={false}
					stroke="url(#lineGradient)"
					strokeWidth={4}
					type="monotone"
				/>

				<XAxis
					dataKey="date"
					dy={10}
					padding={{left: 30, right: 30}}
					tickFormatter={formatXAxisDate}
				/>

				<YAxis />

				<Tooltip
					content={<EngagementChartTooltip />}
					isAnimationActive={false}
				/>
			</LineChart>
		</ResponsiveContainer>
	);
}

export default EngagementChart;
