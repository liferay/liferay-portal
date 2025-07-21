/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React, {useContext} from 'react';

import {getStatsColor, getStatsIcon} from '../../../dashboard/utils/metrics';
import {toThousands} from '../../../dashboard/utils/number';
import {Metric} from '../PerformanceTabContent';
import {PerformanceTabContext} from './PerformanceTabContext';

export enum MetricType {
	Downloads = 'DOWNLOADS',
	Impressions = 'IMPRESSIONS',
	Views = 'VIEWS',
}

interface IMetricsProps {
	defaultMetric: Metric;
	selectedMetrics: Metric[];
}

const Metrics: React.FC<IMetricsProps> = ({selectedMetrics}) => {
	const {changeMetric, filters} = useContext(PerformanceTabContext);

	const handleKeyDown = (
		event: React.KeyboardEvent<HTMLDivElement>,
		metricType: MetricType
	) => {
		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();

			changeMetric(metricType);
		}
	};

	return (
		<div className="d-flex flex-row justify-content-between metrics-container">
			{selectedMetrics.map((metric) => {
				const statsColor = getStatsColor(
					metric.trend.trendClassification
				);

				const selected = metric.metricType === filters.metric;
				const statsIcon = getStatsIcon(metric.trend.percentage);

				return (
					<div
						aria-pressed={selected}
						className={classNames(
							'cursor-pointer flex-grow-1 metrics-card rounded-lg',
							{
								'selected tab-focus': selected,
							}
						)}
						key={metric.metricType}
						onClick={() => changeMetric(metric.metricType)}
						onKeyDown={(event) =>
							handleKeyDown(event, metric.metricType)
						}
						role="button"
						tabIndex={0}
					>
						<Text size={3} weight="semi-bold">
							{metric.metricType.toUpperCase()}
						</Text>

						<div className="mt-2">
							<Text size={7} weight="bold">
								{toThousands(metric.value)}
							</Text>

							<div>
								<Text color={statsColor}>
									<>
										{statsIcon && (
											<span className="mr-1">
												<ClayIcon symbol={statsIcon} />
											</span>
										)}
										{Math.abs(metric.trend.percentage)}%
									</>
								</Text>
							</div>
						</div>
					</div>
				);
			})}
		</div>
	);
};

export {Metrics};
