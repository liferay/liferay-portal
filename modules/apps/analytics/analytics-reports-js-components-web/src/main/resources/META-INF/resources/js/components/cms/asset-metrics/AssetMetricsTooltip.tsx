/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Alignments, Weights} from '../../../types/global';
import {formatDate} from '../../../utils/date';
import {toThousands} from '../../../utils/math';
import ChartTooltip from '../../ChartTooltip';
import {RangeSelectors} from '../../RangeSelectorsDropdown';
import {CircleDot} from '../../metrics/Dots';
import {FormattedData} from '../../metrics/utils';
import {MetricDataKey} from './AssetMetricsChart';

interface IAssetMetricsTooltipProps
	extends React.HTMLAttributes<HTMLDivElement> {
	formattedData: FormattedData;
	index: number;
	rangeSeletor: RangeSelectors;
	title?: string;
}

const AssetMetricsTooltip: React.FC<IAssetMetricsTooltipProps> = ({
	formattedData,
	index,
	rangeSeletor,
	style,
	title,
}) => {
	const payload = formattedData.combinedData?.[index];

	if (!payload) {
		return null;
	}

	const metricData = formattedData.data[MetricDataKey.Current];
	const prevMetricData = formattedData.data[MetricDataKey.Previous];

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

							{Liferay.Language.get('current-period')}
						</>
					),
				},
				{
					align: Alignments.Right,
					label: toThousands(
						payload[MetricDataKey.Current] as number
					),
				},
			],
		},
		{
			columns: [
				{
					label: () => (
						<>
							<span className="mr-2">
								<CircleDot
									displayOutsideOfRecharts
									stroke={prevMetricData?.color ?? 'none'}
								/>
							</span>

							{Liferay.Language.get('previous-period')}
						</>
					),
				},
				{
					align: Alignments.Right,
					label: toThousands(
						payload[MetricDataKey.Previous] as number
					),
				},
			],
		},
	];

	return (
		<div
			className="bb-tooltip-container metrics-chart__tooltip"
			style={{...style, maxWidth: 400, position: 'static'}}
		>
			<ChartTooltip header={header} rows={rows} />
		</div>
	);
};

export {AssetMetricsTooltip};
