/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React from 'react';

import CategoryBreakdownChart from './CategoryBreakdownChart';
import ImpactMixChart from './ImpactMixChart';
import KPITile from './KPITile';
import {Metrics} from './types';

import './MetricsDashboard.scss';

export default function MetricsDashboard({
	metrics,
	scope,
}: {
	metrics: Metrics | null;
	scope: string;
}) {
	const empty = !metrics;

	const noDataAvailable = Liferay.Language.get('no-data-available');

	return (
		<div className="seo-studio-metrics-dashboard">
			<div className="seo-studio-metrics-dashboard-tiles">
				<KPITile
					description={sub(
						Liferay.Language.get(
							'total-number-of-insights-found-for-x-seo'
						),
						scope
					)}
					empty={empty}
					icon="click"
					title={sub(Liferay.Language.get('x-insights'), scope)}
					value={metrics ? metrics.totalInsights : noDataAvailable}
				/>

				<KPITile
					description={sub(
						Liferay.Language.get(
							'total-pages-impacted-by-x-seo-insights'
						),
						scope
					)}
					empty={empty}
					icon="view"
					title={Liferay.Language.get('pages-affected')}
					value={
						metrics ? metrics.affectedPagesCount : noDataAvailable
					}
				/>

				<KPITile
					description={sub(
						Liferay.Language.get(
							'average-number-of-x-seo-insights-identified-per-page'
						),
						scope
					)}
					empty={empty}
					icon="percentage-symbol"
					title={Liferay.Language.get('average-insights-per-page')}
					value={
						metrics
							? (
									metrics.averageInsightsPerAffectedPage ?? 0
								).toFixed(1)
							: noDataAvailable
					}
				/>

				<KPITile
					description={sub(
						Liferay.Language.get(
							'total-high-impact-x-seo-insights-requiring-attention'
						),
						scope
					)}
					empty={empty}
					icon="warning"
					title={Liferay.Language.get('high-impact-insights')}
					value={metrics ? metrics.criticalInsights : noDataAvailable}
				/>
			</div>

			<div className="seo-studio-metrics-dashboard-panels">
				<section className="seo-studio-metrics-dashboard-panel">
					<header className="seo-studio-metrics-dashboard-panel-header">
						<h3 className="seo-studio-metrics-dashboard-panel-title">
							{Liferay.Language.get('insights-by-category')}
						</h3>

						<p className="seo-studio-metrics-dashboard-panel-subtitle text-secondary">
							{Liferay.Language.get(
								'insights-by-category-description'
							)}
						</p>
					</header>

					<CategoryBreakdownChart
						categoryBreakdown={
							metrics ? metrics.categoryBreakdown : {}
						}
					/>
				</section>

				<section className="seo-studio-metrics-dashboard-panel">
					<header className="seo-studio-metrics-dashboard-panel-header">
						<h3 className="seo-studio-metrics-dashboard-panel-title">
							{Liferay.Language.get('impact-mix-per-category')}
						</h3>

						<p className="seo-studio-metrics-dashboard-panel-subtitle text-secondary">
							{Liferay.Language.get(
								'impact-mix-per-category-description'
							)}
						</p>
					</header>

					<ImpactMixChart
						impactMix={metrics ? metrics.impactMix : {}}
					/>
				</section>
			</div>
		</div>
	);
}
