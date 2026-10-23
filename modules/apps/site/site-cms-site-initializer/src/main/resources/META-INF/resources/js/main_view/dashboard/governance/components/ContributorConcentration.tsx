/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import {BarChart, BarDatum, ChartState} from '@liferay/frontend-js-charts-web';
import classNames from 'classnames';
import React, {useContext, useEffect, useRef, useState} from 'react';

import useResizeObserver from '../../../../common/hooks/useResizeObserver';
import {BaseCard} from '../../common/BaseCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {Contributors} from '../GovernanceService';
import {GovernanceAdditionalProps} from '../types';

const BAR_ROW_HEIGHT = 32;

const CHART_VERTICAL_PADDING = 40;

function getBars({contributors, totalCount}: Contributors): BarDatum[] {
	const bars: BarDatum[] = [];

	let contributorsCount = 0;
	let deletedUsersCount = 0;

	for (const {frequency, name} of contributors) {
		contributorsCount += frequency;

		if (name === null) {
			deletedUsersCount += frequency;
		}
		else {
			bars.push({label: name, value: frequency});
		}
	}

	if (deletedUsersCount) {
		bars.push({
			label: Liferay.Language.get('deleted-users'),
			value: deletedUsersCount,
		});
	}

	const othersCount = totalCount - contributorsCount;

	if (othersCount > 0) {
		bars.push({label: Liferay.Language.get('others'), value: othersCount});
	}

	return bars.sort((a, b) => b.value - a.value);
}

export function ContributorConcentration({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const [contributors, setContributors] = useState<Contributors | null>(null);
	const [error, setError] = useState<string | null>(null);
	const [chartWidth, setChartWidth] = useState<number>();
	const [showPercentages, setShowPercentages] = useState(false);
	const {groupIds} = useContext(GovernanceContext);

	const chartContainerRef = useRef<HTMLDivElement>(null);

	useResizeObserver(chartContainerRef, (element) =>
		setChartWidth(element.clientWidth)
	);

	useEffect(() => {
		let stale = false;

		async function fetchContributors() {
			setContributors(null);
			setError(null);

			const {data, error} = await GovernanceService.getContributors(
				additionalProps.contentProgressFilter,
				groupIds
			);

			if (stale) {
				return;
			}

			setContributors(data);
			setError(error);
		}

		fetchContributors();

		return () => {
			stale = true;
		};
	}, [additionalProps.contentProgressFilter, groupIds]);

	const bars = contributors ? getBars(contributors) : [];

	const chartHeight = bars.length * BAR_ROW_HEIGHT + CHART_VERTICAL_PADDING;

	const percentagesLabel = showPercentages
		? Liferay.Language.get('hide-percentages')
		: Liferay.Language.get('show-percentages');

	return (
		<BaseCard
			Preferences={
				<ClayButtonWithIcon
					aria-label={percentagesLabel}
					className={classNames({active: showPercentages})}
					disabled={!bars.length}
					displayType="secondary"
					onClick={() => setShowPercentages(!showPercentages)}
					size="xs"
					symbol="percentage-symbol"
					title={percentagesLabel}
				/>
			}
			className="cms-contributor-concentration custom-empty-state h-100"
			description={Liferay.Language.get(
				'this-is-the-share-of-content-created-by-each-contributor-across-the-selected-spaces'
			)}
			title={Liferay.Language.get('contributor-concentration')}
			uppercaseTitle={false}
		>
			<div ref={chartContainerRef}>
				{contributors && !bars.length ? (
					<ClayEmptyState
						description={Liferay.Language.get(
							'there-is-no-contributor-activity-to-measure-concentration-against'
						)}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state.svg`}
						imgSrcReducedMotion={null}
						title={Liferay.Language.get('nothing-to-compare-yet')}
					/>
				) : (
					<ChartState
						error={error}
						height={chartHeight}
						loading={!contributors && !error}
					>
						<BarChart
							data={bars}
							height={chartHeight}
							legend={showPercentages ? 'list' : 'none'}
							legendValue="percent"
							orientation="horizontal"
							rounded
							size="inline"
							title=""
							width={chartWidth}
						/>
					</ChartState>
				)}
			</div>
		</BaseCard>
	);
}
