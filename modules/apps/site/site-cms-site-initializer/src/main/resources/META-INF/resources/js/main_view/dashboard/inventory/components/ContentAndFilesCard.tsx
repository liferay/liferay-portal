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
	toThousands,
} from '@liferay/analytics-reports-js-components-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import ApiHelper from '../../../../common/services/ApiHelper';
import {MetricValue} from '../../common/MetricValue';
import {InventoryContext} from '../InventoryContext';

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
	} = useContext(InventoryContext);

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

	const {breakdown, title: formattedTitle} = useMemo(
		() => ({
			breakdown: [
				{
					icon: 'vocabulary',
					label:
						metrics?.vocabulariesCount === 1
							? Liferay.Language.get('vocabulary')
							: Liferay.Language.get('vocabularies'),
					value: metrics?.vocabulariesCount ?? 0,
				},
				{
					icon: 'categories',
					label:
						metrics?.categoriesCount === 1
							? Liferay.Language.get('category')
							: Liferay.Language.get('categories'),
					value: metrics?.categoriesCount ?? 0,
				},
				{
					icon: 'tag',
					label:
						metrics?.tagsCount === 1
							? Liferay.Language.get('tag')
							: Liferay.Language.get('tags'),
					value: metrics?.tagsCount ?? 0,
				},
			],
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
					<MetricValue
						trend={{
							classification:
								metrics?.trend.classification ??
								TrendClassification.Neutral,
							percentage: metrics?.trend.percentage ?? 0,
						}}
						value={formattedTitle}
						valueClassName="text-lowercase"
					/>

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
