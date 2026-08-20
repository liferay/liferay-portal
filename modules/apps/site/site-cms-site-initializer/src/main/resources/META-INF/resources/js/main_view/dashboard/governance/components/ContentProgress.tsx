/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {BarChart, BarDatum, ChartState} from '@liferay/frontend-js-charts-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import {
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
} from '../../../../common/utils/constants';
import {getStatusSelectedData} from '../../../quick_filters/quickFilterUpdates';
import {BaseCard} from '../../common/BaseCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {StatusFacetBucket} from '../GovernanceService';
import getAllSectionHref, {getSpaceFilters} from '../getAllSectionHref';
import {GovernanceAdditionalProps} from '../types';

const CHART_HEIGHT = 36;

const CONTENT_STATUS: {label: string; value: number}[] = [
	{
		label: Liferay.Language.get('draft'),
		value: WORKFLOW_STATUS.DRAFT,
	},
	{
		label: Liferay.Language.get('pending'),
		value: WORKFLOW_STATUS.PENDING,
	},
	{
		label: Liferay.Language.get('approved'),
		value: WORKFLOW_STATUS.APPROVED,
	},
	{
		label: Liferay.Language.get('scheduled'),
		value: WORKFLOW_STATUS.SCHEDULED,
	},
];

function getSegments(
	buckets: StatusFacetBucket[],
	getStatusHref: (label: string, value: number) => string
): BarDatum[] {
	const frequencies = new Map(
		buckets.map((bucket) => [Number(bucket.term), bucket.frequency])
	);

	const segments: BarDatum[] = CONTENT_STATUS.map(({label, value}) => ({
		href: getStatusHref(label, value),
		label,
		value: frequencies.get(value) ?? 0,
	}));

	const knownValues = new Set(CONTENT_STATUS.map(({value}) => value));

	const otherBuckets = buckets.filter(
		(bucket) => !knownValues.has(Number(bucket.term))
	);

	if (otherBuckets.length) {
		segments.push({
			label: Liferay.Language.get('others'),
			value: otherBuckets.reduce(
				(acc, bucket) => acc + bucket.frequency,
				0
			),
		});
	}

	return segments.filter(({value}) => value > 0);
}

export function ContentProgress({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const [buckets, setBuckets] = useState<StatusFacetBucket[] | null>(null);
	const [error, setError] = useState<string | null>(null);
	const {space} = useContext(GovernanceContext);

	useEffect(() => {
		let stale = false;

		async function fetchContentProgress() {
			setBuckets(null);
			setError(null);

			const {data, error} = await GovernanceService.getContentProgress(
				additionalProps.contentProgressFilter,
				space.siteId
			);

			if (stale) {
				return;
			}

			setBuckets(data?.searchFacets?.statusFacet ?? []);
			setError(error);
		}

		fetchContentProgress();

		return () => {
			stale = true;
		};
	}, [additionalProps.contentProgressFilter, space.siteId]);

	const segments = useMemo(() => {
		const spaceFilters = getSpaceFilters(space);

		return getSegments(buckets ?? [], (label, value) =>
			getAllSectionHref(additionalProps.allSectionFDSName, {
				filters: [
					{
						id: FDS_FILTER_ID.STATUS,
						selectedData: getStatusSelectedData(label, value),
					},
					...spaceFilters,
				],
			})
		);
	}, [additionalProps.allSectionFDSName, buckets, space]);

	return (
		<BaseCard
			className="cms-content-progress custom-empty-state"
			description={Liferay.Language.get(
				'this-is-the-progress-of-content-creation-and-completion-across-the-selected-spaces'
			)}
			title={Liferay.Language.get('content-progress')}
			uppercaseTitle={false}
		>
			<ChartState
				empty={!segments.length}
				error={error}
				height={CHART_HEIGHT}
				loading={!buckets && !error}
			>
				<BarChart
					data={segments}
					height={CHART_HEIGHT}
					legend="list"
					legendValue="name"
					rounded
					size="inline"
					stacked
					title=""
				/>
			</ChartState>
		</BaseCard>
	);
}
