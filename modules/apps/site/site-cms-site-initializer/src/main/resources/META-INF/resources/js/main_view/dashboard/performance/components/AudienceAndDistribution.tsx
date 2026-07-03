/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React, {useContext} from 'react';

import {BaseCard} from '../../common/BaseCard';
import {SectionHeader} from '../../common/SectionHeader';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {DownloadButton} from './DownloadButton';

export function AudienceAndDistribution() {
	return (
		<>
			<ClayLayout.Row className="mb-3">
				<ClayLayout.Col size={12}>
					<SectionHeader
						ariaLevel={2}
						description={Liferay.Language.get(
							'identify-where-your-audience-is-coming-from-and-what-content-theyre-engaging-with'
						)}
						icon="globe-pin"
						role="heading"
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

	const depotEntryIds = space.value === 'all' ? undefined : [space.value];

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
			description={description}
			title={title}
			uppercaseTitle={false}
		/>
	);
}
