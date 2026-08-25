/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {BarChart, BarDatum, ChartState} from '@liferay/frontend-js-charts-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import {
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
	WorkflowStatus,
} from '../../../../common/utils/constants';
import {getStatusSelectedData} from '../../../quick_filters/quickFilterUpdates';
import {BaseCard} from '../../common/BaseCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {StatusFacetBucket} from '../GovernanceService';
import getAllSectionHref, {getSpaceFilters} from '../getAllSectionHref';
import {GovernanceAdditionalProps} from '../types';

const CHART_HEIGHT = 36;

const STATUS_LABELS: Record<WorkflowStatus, string> = {
	[WORKFLOW_STATUS.APPROVED]: Liferay.Language.get('approved'),
	[WORKFLOW_STATUS.DRAFT]: Liferay.Language.get('draft'),
	[WORKFLOW_STATUS.EXPIRED]: Liferay.Language.get('expired'),
	[WORKFLOW_STATUS.PENDING]: Liferay.Language.get('pending'),
	[WORKFLOW_STATUS.SCHEDULED]: Liferay.Language.get('scheduled'),
};

const CONTENT_STATUSES: WorkflowStatus[] = [
	WORKFLOW_STATUS.DRAFT,
	WORKFLOW_STATUS.PENDING,
	WORKFLOW_STATUS.APPROVED,
	WORKFLOW_STATUS.SCHEDULED,
];

function getSegments(
	buckets: StatusFacetBucket[],
	getStatusHref: (statuses: WorkflowStatus[]) => string
): BarDatum[] {
	const frequencies = new Map(
		buckets
			.filter(({frequency}) => frequency > 0)
			.map((bucket) => [
				Number(bucket.term) as WorkflowStatus,
				bucket.frequency,
			])
	);

	const segments: BarDatum[] = CONTENT_STATUSES.map((status) => ({
		href: getStatusHref([status]),
		label: STATUS_LABELS[status],
		value: frequencies.get(status) ?? 0,
	}));

	const otherEntries = [...frequencies].filter(
		([status]) => !CONTENT_STATUSES.includes(status)
	);

	if (otherEntries.length) {
		segments.push({
			href: getStatusHref(otherEntries.map(([status]) => status)),
			label: Liferay.Language.get('others'),
			value: otherEntries.reduce(
				(total, [, frequency]) => total + frequency,
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

		return getSegments(buckets ?? [], (statuses) =>
			getAllSectionHref(additionalProps.allSectionFDSName, {
				filters: [
					{
						id: FDS_FILTER_ID.STATUS,
						selectedData: getStatusSelectedData(
							statuses.map((status) => ({
								label: STATUS_LABELS[status],
								value: status,
							}))
						),
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
