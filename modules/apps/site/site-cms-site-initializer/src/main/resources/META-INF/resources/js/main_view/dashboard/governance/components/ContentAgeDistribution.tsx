/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {BarChart, ChartState} from '@liferay/frontend-js-charts-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';

import useResizeObserver from '../../../../common/hooks/useResizeObserver';
import {getImage} from '../../../../common/utils/getImage';
import {BaseCard} from '../../common/BaseCard';
import PickerTrigger from '../../common/PickerTrigger';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {ContentAgeDateField} from '../GovernanceService';
import {GovernanceAdditionalProps} from '../types';

const CHART_HEIGHT = 240;

const DATE_FIELDS: {key: ContentAgeDateField; label: string}[] = [
	{key: 'createDate', label: Liferay.Language.get('creation-date')},
	{key: 'modified', label: Liferay.Language.get('modified-date')},
];

function getRangeLabels() {
	return [
		sub(Liferay.Language.get('less-than-x-months'), 3),
		sub(Liferay.Language.get('x-to-x-months'), 3, 6),
		sub(Liferay.Language.get('x-to-x-months'), 6, 12),
		sub(Liferay.Language.get('x-to-x-years'), 1, 2),
		sub(Liferay.Language.get('more-than-x-years'), 2),
	];
}

export function ContentAgeDistribution({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const [chartWidth, setChartWidth] = useState<number>();
	const [dateField, setDateField] =
		useState<ContentAgeDateField>('createDate');
	const [error, setError] = useState<string | null>(null);
	const [frequencies, setFrequencies] = useState<number[] | null>(null);
	const {groupIds} = useContext(GovernanceContext);

	const chartContainerRef = useRef<HTMLDivElement>(null);

	useResizeObserver(chartContainerRef, (element) =>
		setChartWidth(element.clientWidth)
	);

	useEffect(() => {
		let stale = false;

		async function fetchContentAgeDistribution() {
			setError(null);
			setFrequencies(null);

			const {data, error} =
				await GovernanceService.getContentAgeDistribution(
					additionalProps.contentProgressFilter,
					dateField,
					groupIds
				);

			if (stale) {
				return;
			}

			setError(error);
			setFrequencies(data);
		}

		fetchContentAgeDistribution();

		return () => {
			stale = true;
		};
	}, [additionalProps.contentProgressFilter, dateField, groupIds]);

	const data = useMemo(
		() =>
			getRangeLabels().map((label, index) => ({
				label,
				value: frequencies?.[index] ?? 0,
			})),
		[frequencies]
	);

	return (
		<BaseCard
			Preferences={
				<Picker
					aria-label={Liferay.Language.get('date')}
					as={PickerTrigger}
					items={DATE_FIELDS}
					onSelectionChange={(key) =>
						setDateField(key as ContentAgeDateField)
					}
					selectedKey={dateField}
					triggerClassName="mb-2"
				>
					{(item: (typeof DATE_FIELDS)[number]) => (
						<Option key={item.key}>{item.label}</Option>
					)}
				</Picker>
			}
			className="cms-content-age-distribution custom-empty-state h-100"
			description={Liferay.Language.get(
				'this-is-the-distribution-of-approved-content-by-time-since-it-was-created-or-last-updated'
			)}
			title={Liferay.Language.get('content-age-distribution')}
			uppercaseTitle={false}
		>
			<div ref={chartContainerRef}>
				{frequencies && !frequencies.some(Boolean) ? (
					<ClayEmptyState
						description={Liferay.Language.get(
							'there-is-no-approved-content-in-the-selected-spaces'
						)}
						imgSrc={getImage(
							'content_age_empty_state_illustration.svg'
						)}
						imgSrcReducedMotion={null}
						title={Liferay.Language.get('nothing-to-analyze-yet')}
					/>
				) : (
					<ChartState
						error={error}
						height={CHART_HEIGHT}
						loading={!frequencies && !error}
					>
						<BarChart
							data={data}
							height={CHART_HEIGHT}
							legend="list"
							title=""
							width={chartWidth}
						/>
					</ChartState>
				)}
			</div>
		</BaseCard>
	);
}
