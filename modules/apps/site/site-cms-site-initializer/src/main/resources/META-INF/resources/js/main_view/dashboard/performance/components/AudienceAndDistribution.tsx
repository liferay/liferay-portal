/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayLayout from '@clayui/layout';
import {ChartState, MapChart, PieChart} from '@liferay/frontend-js-charts-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import {BaseCard} from '../../common/BaseCard';
import {SectionHeader} from '../../common/SectionHeader';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {PerformanceMetric} from '../types';
import {DownloadButton} from './DownloadButton';

const EMPTY_MESSAGES = {
	categories: {
		description: Liferay.Language.get(
			'views-will-break-down-by-category-once-your-content-starts-getting-traffic'
		),
		title: Liferay.Language.get('no-category-data-yet'),
	},
	location: {
		description: Liferay.Language.get(
			'once-people-view-your-content-youll-see-where-theyre-from-on-the-map'
		),
		title: Liferay.Language.get('no-views-by-location-yet'),
	},
};

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

	const empty = !metrics.length;

	const legend = empty ? 'none' : 'list';

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
			<ChartState error={error} loading={loading}>
				<div className="d-flex flex-column flex-grow-1 justify-content-center">
					{groupBy === 'categories' ? (
						<PieChart
							className="w-100"
							data={metrics.map(({value, valueKey}) => ({
								label: valueKey,
								value,
							}))}
							legend={legend}
							legendPosition="bottom"
							legendSwatchBorder={false}
							showCenterLabel={!empty}
							title=""
						/>
					) : (
						<MapChart
							data={metrics.map(({value, valueKey}) => ({
								country: valueKey,
								value,
							}))}
							legend={legend}
							legendPosition="bottom"
							legendSwatchBorder={false}
							title=""
							variant="choropleth"
						/>
					)}
				</div>

				{empty ? (
					<div className="mt-4 px-8 text-center">
						<div>
							<Text size={4} weight="semi-bold">
								{EMPTY_MESSAGES[groupBy].title}
							</Text>
						</div>

						<div>
							<Text color="secondary" size={3}>
								{EMPTY_MESSAGES[groupBy].description}
							</Text>
						</div>
					</div>
				) : null}
			</ChartState>
		</BaseCard>
	);
}
