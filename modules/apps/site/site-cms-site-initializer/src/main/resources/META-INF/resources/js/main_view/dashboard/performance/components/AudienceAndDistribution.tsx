/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {ChartState, MapChart, PieChart} from '@liferay/frontend-js-charts-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import {BaseCard} from '../../common/BaseCard';
import {SectionHeader} from '../../common/SectionHeader';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {PerformanceMetric} from '../types';
import {DownloadButton} from './DownloadButton';

export function AudienceAndDistribution() {
	return (
		<>
			<ClayLayout.Row className="mb-3">
				<ClayLayout.Col size={12}>
					<SectionHeader
						description={Liferay.Language.get(
							'identify-where-your-audience-is-coming-from-and-what-content-theyre-engaging-with'
						)}
						icon="globe-pin"
						title={Liferay.Language.get(
							'audience-and-distribution'
						)}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row className="mb-4">
				<ClayLayout.Col className="mb-3" lg={12} xl={6}>
					<Card
						description={Liferay.Language.get(
							'total-number-of-visitors-grouped-by-location'
						)}
						groupBy="location"
						title={Liferay.Language.get('views-by-location')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col className="mb-3" lg={12} xl={6}>
					<Card
						description={Liferay.Language.get(
							'total-views-distribution-across-content-categories'
						)}
						groupBy="categories"
						title={Liferay.Language.get('views-by-categorization')}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</>
	);
}

function Card({
	description,
	groupBy,
	title,
}: {
	description: string;
	groupBy: 'categories' | 'location';
	title: string;
}) {
	const {range, space} = useContext(PerformanceContext);

	const [metric, setMetric] = useState<PerformanceMetric>();
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	const depotEntryIds = useMemo(
		() => (space.value === 'all' ? undefined : [space.value]),
		[space.value]
	);

	useEffect(() => {
		async function fetchData() {
			setLoading(true);

			const {data, error} = await PerformanceService.getMetric({
				depotEntryIds,
				groupBy,
				metricType: 'viewsMetric',
				rangeKey: range.rangeKey,
			});

			setMetric(data ?? undefined);
			setError(error);
			setLoading(false);
		}

		fetchData();
	}, [depotEntryIds, groupBy, range.rangeKey]);

	const metrics = metric?.metrics ?? [];

	return (
		<BaseCard
			Preferences={
				<DownloadButton
					href={PerformanceService.getMetricExportURL({
						depotEntryIds,
						groupBy,
						metricType: 'viewsMetric',
						rangeKey: range.rangeKey,
					})}
				/>
			}
			className="d-flex flex-column h-100"
			contentClassName="flex-grow-1"
			description={description}
			title={title}
			uppercaseTitle={false}
		>
			<ChartState
				empty={!loading && !error && !metrics.length}
				error={error}
				loading={loading}
			>
				{groupBy === 'categories' ? (
					<PieChart
						className="cms-dashboard__pie-chart w-100"
						data={metrics.map(({value, valueKey}) => ({
							label: valueKey,
							value,
						}))}
						legend="table"
						title=""
					/>
				) : (
					<MapChart
						data={metrics.map(({value, valueKey}) => ({
							country: valueKey,
							value,
						}))}
						legend="table"
						title=""
						variant="choropleth"
					/>
				)}
			</ChartState>
		</BaseCard>
	);
}
