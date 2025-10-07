/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Alignments, Colors, Weights} from '../../../types/global';
import {formatDate} from '../../../utils/date';
import ChartTooltip from '../../ChartTooltip';
import {RangeSelectors} from '../../RangeSelectorsDropdown';
import {CircleDot, PublishedVersionDot} from '../../metrics/Dots';
import {FormattedData} from '../../metrics/utils';
import {VisitorsBehaviorDataKey} from './utils';

interface IVisitorsBehaviorChartTooltipProps
	extends React.HTMLAttributes<HTMLDivElement> {
	formattedData: FormattedData;
	index: number;
	rangeSeletor: RangeSelectors;
	title?: string;
}

const VisitorsBehaviorChartTooltip: React.FC<
	IVisitorsBehaviorChartTooltipProps
> = ({formattedData, index, rangeSeletor, style, title}) => {
	const payload = formattedData.combinedData?.[index];

	if (!payload) {
		return null;
	}

	const metricData = formattedData.data[VisitorsBehaviorDataKey.Metric];
	const publishedVersionData =
		formattedData.data[VisitorsBehaviorDataKey.PublishedVersionData];

	const header = [
		{
			columns: [
				{
					label: title ?? '',
					weight: Weights.Semibold,
					width: 155,
				},
				{
					align: Alignments.Right,
					label: formatDate(new Date(payload.x ?? 0), rangeSeletor),
					width: 55,
				},
			],
		},
	];

	const rows = [
		{
			columns: [
				{
					label: () => (
						<>
							<span className="mr-2">
								<CircleDot
									displayOutsideOfRecharts
									stroke={metricData?.color ?? 'none'}
								/>
							</span>

							{metricData.title}
						</>
					),
				},
				{
					align: Alignments.Right,
					label: metricData.format?.(
						payload[VisitorsBehaviorDataKey.Metric]
					),
				},
			],
		},
	];

	if (payload[VisitorsBehaviorDataKey.PublishedVersionValue]) {
		rows.push({
			columns: [
				{
					label: () => (
						<>
							<span className="mr-2" style={{marginLeft: -4}}>
								<PublishedVersionDot
									displayOutsideOfRecharts
									stroke={Colors.Black}
								/>
							</span>

							<span style={{marginLeft: -2}}>
								{publishedVersionData.title}
							</span>
						</>
					),
				},
				{
					align: Alignments.Right,
					label: payload[
						VisitorsBehaviorDataKey.PublishedVersionValue
					],
				},
			],
		});
	}

	return (
		<div
			className="bb-tooltip-container metrics-chart__tooltip"
			style={{...style, maxWidth: 400, position: 'static'}}
		>
			<ChartTooltip header={header} rows={rows} />
		</div>
	);
};

export default VisitorsBehaviorChartTooltip;
