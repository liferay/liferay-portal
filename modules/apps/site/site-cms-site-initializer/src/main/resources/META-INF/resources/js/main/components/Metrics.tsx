/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import React from 'react';

import '../../../css/InfoPanel/Metrics.scss';

import Icon from '@clayui/icon';

import {toThousands} from '../dashboard/utils/number';
import {Metric} from './info_panel/tab_content/PerformanceTabContent';

interface IMetricsProps {
	metrics: Metric[];
	selectedMetric: string;
	setSelectedMetric: (title: string) => void;
}

function getComparisonClassName(comparison: number): string {
	if (comparison > 0) {
		return 'text-success';
	}

	if (comparison === 0) {
		return 'text-secondary';
	}

	return 'text-danger';
}

function formatComparisonNumber(total: number): string {
	return Math.abs(total).toString();
}

const Metrics: React.FC<IMetricsProps> = ({
	metrics,
	selectedMetric,
	setSelectedMetric,
}) => {
	const handleKeyDown = (
		event: React.KeyboardEvent<HTMLDivElement>,
		title: string
	) => {
		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();
			setSelectedMetric(title);
		}
	};

	return (
		<div className="d-flex flex-row justify-content-between">
			{metrics.map((metric) => {
				const isActive = metric.title === selectedMetric;
				const comparisonClassName = getComparisonClassName(
					metric.comparison
				);

				return (
					<div
						aria-pressed={isActive}
						className={`metrics-card rounded-lg fluid ${isActive ? 'active' : ''}`}
						key={metric.title}
						onClick={() => setSelectedMetric(metric.title)}
						onKeyDown={(event) =>
							handleKeyDown(event, metric.title)
						}
						role="button"
						tabIndex={0}
					>
						<Text size={3} weight="semi-bold">
							{metric.title.toUpperCase()}
						</Text>

						<div className="body">
							<Text size={7} weight="bold">
								{toThousands(metric.total)}
							</Text>

							<div className={comparisonClassName}>
								<>
									{metric.comparison > 0 && (
										<Icon symbol="caret-top" />
									)}
									{metric.comparison < 0 && (
										<Icon symbol="caret-bottom" />
									)}
									{formatComparisonNumber(metric.comparison)}%
								</>
							</div>
						</div>
					</div>
				);
			})}
		</div>
	);
};

export {Metrics};
