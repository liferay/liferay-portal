/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useRef, useState} from 'react';

import {Colors, Textures} from '../../../types/global';
import StackedBarLegend from './StackedBarLegend';
import StackedBarTooltip from './StackedBarTooltip';

type ChartDataItem = {label: string; percentage: number; value: number};

export type ChartData = {
	data: ChartDataItem[];
	total: number;
};

interface IStackedBarChartProps {
	data: ChartData;
	tooltipTitle: string;
}

export type TooltipProps = {
	show: boolean;
	x: number;
	y: number;
};

export const chartBgColors = [
	null,
	Textures.VerticalLines,
	Textures.DiagonalLines,
	Textures.HorizontalLines,
];

export const chartColors = [
	Colors.Blue2,
	Colors.Indigo2,
	Colors.Yellow2,
	Colors.Pink2,
];

interface IStackedBarChartItemProps {
	activeIndex: number;
	color: Colors;
	data: ChartDataItem;
	index: number;
	onActiveIndexChange: (activeIndex: number) => void;
}

const StackedBarChartItem: React.FC<IStackedBarChartItemProps> = ({
	activeIndex,
	color,
	data,
	index,
	onActiveIndexChange,
}) => {
	const itemRef = useRef<HTMLDivElement>(null);

	const handleShowTooltip = (index: number) => {
		onActiveIndexChange(index);
	};

	const handleHideTooltip = () => {
		onActiveIndexChange(-1);
	};

	return (
		<div
			className={classNames('stacked-bar-chart__bar', 'tab-focus', {
				active: index === activeIndex,
			})}
			key={index}
			onBlur={handleHideTooltip}
			onFocus={() => handleShowTooltip(index)}
			onMouseEnter={() => handleShowTooltip(index)}
			onMouseLeave={handleHideTooltip}
			ref={itemRef}
			style={{
				backgroundColor: color,
				backgroundImage: chartBgColors[index]
					? `url('data:image/svg+xml;utf8,\
				<svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" patternUnits="userSpaceOnUse">\
				  <rect fill="none" width="10" height="10" />\
				  <path d="${chartBgColors[index]}" fill="none" stroke="white" stroke-width="1" stroke-opacity="0.5"/>\
				</svg>')`
					: '',
				width: `${data.percentage}%`,
			}}
			tabIndex={0}
		/>
	);
};

const StackedBarChart: React.FC<IStackedBarChartProps> = ({
	data,
	tooltipTitle,
}) => {
	const [activeIndex, setActiveIndex] = useState(-1);

	return (
		<div
			className={classNames('stacked-bar', {
				'active': activeIndex >= 0,
				'multiple-bars': data.data.length > 1,
			})}
		>
			<div className="stacked-bar-chart">
				{!data.data.length && (
					<StackedBarChartItem
						activeIndex={activeIndex}
						color={Colors.Gray}
						data={{label: '', percentage: 100, value: 0}}
						index={0}
						onActiveIndexChange={() => {}}
					/>
				)}

				{!!data.data.length &&
					data.data.map((item, index) => (
						<StackedBarChartItem
							activeIndex={activeIndex}
							color={chartColors[index]}
							data={item}
							index={index}
							key={index}
							onActiveIndexChange={setActiveIndex}
						/>
					))}
			</div>

			<StackedBarLegend
				data={data}
				onActiveIndexChange={setActiveIndex}
			/>

			{activeIndex >= 0 && (
				<StackedBarTooltip
					activeIndex={activeIndex}
					data={data}
					title={tooltipTitle}
				/>
			)}

			{/* Used on playwright to test data */}

			<div
				data-qa-chart-data={JSON.stringify(data)}
				data-testid="stacked-bar-chart-data"
			/>
		</div>
	);
};

export default StackedBarChart;
