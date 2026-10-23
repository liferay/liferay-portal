/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {BarChart, BarDatum, ChartState} from '@liferay/frontend-js-charts-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import {
	FDS_FILTER_ID,
	WORKFLOW_STATUS,
	WorkflowStatus,
} from '../../../../common/utils/constants';
import {getImage} from '../../../../common/utils/getImage';
import toDatePart from '../../../../common/utils/toDatePart';
import {getStatusSelectedData} from '../../../quick_filters/quickFilterUpdates';
import {BaseCard} from '../../common/BaseCard';
import PickerTrigger from '../../common/PickerTrigger';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {
	FacetBucket,
	MILLISECONDS_PER_DAY,
} from '../GovernanceService';
import getAllSectionHref, {getSpaceFilters} from '../getAllSectionHref';
import {GovernanceAdditionalProps} from '../types';

const CHART_HEIGHT = 36;

const DEFAULT_TIME_RANGE_DAYS = 7;

const TIME_RANGES = [
	{days: 1, label: sub(Liferay.Language.get('last-x-hours'), [24])},
	...[7, 28, 30, 90].map((days) => ({
		days,
		label: sub(Liferay.Language.get('last-x-days'), [days]),
	})),
];

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
	buckets: FacetBucket[],
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

	return segments
		.filter(({value}) => value > 0)
		.sort((segment1, segment2) => segment2.value - segment1.value);
}

export function ContentProgress({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const [buckets, setBuckets] = useState<FacetBucket[] | null>(null);
	const [error, setError] = useState<string | null>(null);
	const [timeRangeDays, setTimeRangeDays] = useState(DEFAULT_TIME_RANGE_DAYS);
	const {groupIds, space} = useContext(GovernanceContext);

	useEffect(() => {
		let stale = false;

		async function fetchContentProgress() {
			setBuckets(null);
			setError(null);

			const {data, error} = await GovernanceService.getContentProgress(
				additionalProps.contentProgressFilter,
				groupIds,
				timeRangeDays
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
	}, [additionalProps.contentProgressFilter, groupIds, timeRangeDays]);

	const segments = useMemo(() => {
		const createdSince = toDatePart(
			new Date(Date.now() - timeRangeDays * MILLISECONDS_PER_DAY)
		);
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
					{
						id: FDS_FILTER_ID.DATE_CREATED,
						selectedData: {
							exclude: false,
							from: createdSince,
							to: null,
						},
					},
					...spaceFilters,
				],
			})
		);
	}, [additionalProps.allSectionFDSName, buckets, space, timeRangeDays]);

	return (
		<BaseCard
			Preferences={
				<Picker
					aria-label={Liferay.Language.get('time-range')}
					as={PickerTrigger}
					items={TIME_RANGES}
					onSelectionChange={(key) => setTimeRangeDays(Number(key))}
					selectedKey={String(timeRangeDays)}
					triggerClassName="mb-2"
				>
					{(item: (typeof TIME_RANGES)[number]) => (
						<Option key={item.days}>{item.label}</Option>
					)}
				</Picker>
			}
			className="cms-content-progress custom-empty-state h-100"
			description={Liferay.Language.get(
				'this-is-the-progress-of-content-creation-and-completion-across-the-selected-spaces'
			)}
			title={Liferay.Language.get('content-progress')}
			uppercaseTitle={false}
		>
			{buckets && !segments.length ? (
				<ClayEmptyState
					description={Liferay.Language.get(
						'there-is-no-content-movement-to-measure-progress-against'
					)}
					imgSrc={getImage(
						'content_progress_empty_state_illustration.svg'
					)}
					imgSrcReducedMotion={null}
					title={Liferay.Language.get('nothing-to-track-yet')}
				/>
			) : (
				<ChartState
					error={error}
					height={CHART_HEIGHT}
					loading={!buckets && !error}
				>
					<BarChart
						data={segments}
						height={CHART_HEIGHT}
						legend="list"
						rounded
						size="inline"
						stacked
						title=""
					/>
				</ChartState>
			)}
		</BaseCard>
	);
}
