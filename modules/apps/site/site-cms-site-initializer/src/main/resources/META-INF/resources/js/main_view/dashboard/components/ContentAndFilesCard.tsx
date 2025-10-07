/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {
	RangeSelector,
	TrendClassification,
	buildQueryString,
	getSafeRangeSelector,
	getStatsColor,
	getStatsIcon,
	toThousands,
} from '@liferay/analytics-reports-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import {ViewDashboardContext} from '../ViewDashboardContext';

export interface IMetricsProps {
	categoriesCount: number;
	tagsCount: number;
	totalCount: number;
	trend: {
		classification: TrendClassification;
		percentage: number;
	};
	vocabulariesCount: number;
}

export interface IContentAndFilesCard {
	endpointURL: string;
	rangeSelector: RangeSelector;
	title: (totalCount: number) => string;
}

const ContentAndFilesCard: React.FC<IContentAndFilesCard> = ({
	endpointURL,
	rangeSelector,
	title,
}) => {
	const {
		filters: {language, space},
	} = useContext(ViewDashboardContext);

	const [loading, setLoading] = useState(true);
	const [metrics, setMetrics] = useState<IMetricsProps>();

	const queryParams = buildQueryString(
		{
			depotEntryId: space.value,
			languageId: language.value,
			...getSafeRangeSelector(rangeSelector),
		},
		{
			shouldIgnoreParam: (value) => value === 'all',
		}
	);

	useEffect(() => {
		async function getMetrics() {
			setLoading(true);

			const {data, error} = await ApiHelper.get<IMetricsProps>(
				`${endpointURL}${queryParams}`
			);

			if (data) {
				setMetrics(data);
			}

			if (error) {
				console.error(error);
			}

			setLoading(false);
		}

		getMetrics();
	}, [endpointURL, queryParams]);

	const {
		breakdown,
		percentage,
		statsColor,
		statsIcon,
		title: formattedTitle,
	} = useMemo(
		() => ({
			breakdown: [
				{
					icon: 'vocabulary',
					label: Liferay.Language.get('vocabularies'),
					value: metrics?.vocabulariesCount ?? 0,
				},
				{
					icon: 'categories',
					label: Liferay.Language.get('categories'),
					value: metrics?.categoriesCount ?? 0,
				},
				{
					icon: 'tag',
					label: Liferay.Language.get('tags'),
					value: metrics?.tagsCount ?? 0,
				},
			],
			percentage: Math.abs(metrics?.trend.percentage ?? 0),
			statsColor: getStatsColor(
				metrics?.trend.classification ?? TrendClassification.Neutral
			),
			statsIcon: getStatsIcon(metrics?.trend?.percentage ?? 0),
			title: title(metrics?.totalCount ?? 0),
		}),
		[metrics, title]
	);

	return (
		<div className="cms-dashboard__content-and-files-card">
			{loading ? (
				<div
					className="align-items-center d-flex"
					style={{minHeight: '102px'}}
				>
					<ClayLoadingIndicator
						data-testid="loading-animation"
						displayType="primary"
						shape="squares"
						size="md"
					/>
				</div>
			) : (
				<>
					<div className="text-lowercase">
						<Text size={7} weight="semi-bold">
							{formattedTitle}
						</Text>
					</div>

					<div>
						<Text color={statsColor} size={3}>
							{statsIcon && (
								<span className="mr-1">
									<ClayIcon
										aria-label={statsIcon}
										symbol={statsIcon}
									/>
								</span>
							)}

							<span>{percentage}%</span>
						</Text>

						<Text color="secondary" size={3}>
							<span
								className="text-lowercase"
								dangerouslySetInnerHTML={{
									__html: sub(
										Liferay.Language.get(
											'x-vs-previous-period'
										),
										`<span class='hide'>${percentage}</span>`
									),
								}}
							/>
						</Text>
					</div>

					<div className="d-flex flex-wrap mt-3">
						{breakdown.map(({icon, label, value}) => (
							<div
								className="cms-dashboard__content-and-files-card__breakdown mt-1"
								key={label}
							>
								<Text color="secondary" size={3}>
									<ClayIcon symbol={icon} />

									<span className="mx-1">
										{toThousands(value)}
									</span>

									<span>{label}</span>
								</Text>
							</div>
						))}
					</div>
				</>
			)}
		</div>
	);
};

export {ContentAndFilesCard};
