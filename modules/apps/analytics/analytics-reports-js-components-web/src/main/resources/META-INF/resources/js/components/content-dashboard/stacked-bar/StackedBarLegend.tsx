/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import classNames from 'classnames';
import React from 'react';

import {toThousands} from '../../../utils/math';
import {ChartData, chartBgColors, chartColors} from './StackedBarChart';

interface IStackedBarLegendProps {
	data: ChartData;
	onActiveIndexChange: (activeIndex: number) => void;
}

const StackedBarLegend: React.FC<IStackedBarLegendProps> = ({
	data,
	onActiveIndexChange,
}) => {
	return (
		<div className="stacked-bar-legend">
			{data.data.map(({label, value}, index) => (
				<div
					className="stacked-bar-legend__item tab-focus"
					key={index}
					onBlur={() => onActiveIndexChange(-1)}
					onFocus={() => onActiveIndexChange(index)}
					onMouseEnter={() => onActiveIndexChange(index)}
					onMouseLeave={() => onActiveIndexChange(-1)}
					tabIndex={0}
				>
					<div
						className={classNames('stacked-bar-legend__icon')}
						style={{
							backgroundColor: chartColors[index],
							backgroundImage: chartBgColors[index]
								? `url('data:image/svg+xml;utf8,\
				<svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" patternUnits="userSpaceOnUse">\
				  <rect fill="none" width="10" height="10" />\
				  <path d="${chartBgColors[index]}" fill="none" stroke="white" stroke-width="1" stroke-opacity="0.5"/>\
				</svg>')`
								: '',
						}}
					/>

					<div className="stacked-bar-legend__label">
						<Text
							color="secondary"
							size={3}
						>{`${label}: ${toThousands(value)}`}</Text>
					</div>
				</div>
			))}
		</div>
	);
};

export default StackedBarLegend;
