/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayTable from '@clayui/table';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

import {Context} from '../../Context';
import useFetch from '../../hooks/useFetch';
import {MetricType} from '../../types/global';
import {buildQueryString} from '../../utils/buildQueryString';
import {getPercentage, toThousands} from '../../utils/math';
import EmptyState from '../EmptyState';
import Title from '../Title';

export type Metric = {
	metricType: MetricType;
	value: number;
};

export type TopPage = {
	canonicalUrl: string;
	defaultMetric: Metric;
	pageTitle: string;
	siteName?: string;
};

export type AssetData = {
	topPages: TopPage[];
	totalCount: number;
};

export type FormattedPage = {
	count: string;
	link: string;
	page: string;
	percentage: string;
};

type TopPagesMetricsTableProps = {
	data: AssetData;
};

const TopPagesMetricsTable: React.FC<TopPagesMetricsTableProps> = ({data}) => {
	const formattedData: FormattedPage[] = data.topPages.map((topPage) => ({
		count: toThousands(topPage.defaultMetric.value),
		link: topPage.canonicalUrl,
		page: topPage.pageTitle,
		percentage: `${getPercentage((topPage.defaultMetric.value / data.totalCount) * 100)}%`,
	}));

	return (
		<ClayTable hover={false} responsive>
			<ClayTable.Head>
				<ClayTable.Row>
					<ClayTable.Cell headingCell noWrap>
						<span>{Liferay.Language.get('page-title')}</span>
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('views')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{sub(Liferay.Language.get('x-of-x'), [
							'%',
							Liferay.Language.get('views'),
						])}
					</ClayTable.Cell>
				</ClayTable.Row>
			</ClayTable.Head>

			<ClayTable.Body>
				{formattedData.map((row) => (
					<ClayTable.Row key={row.page}>
						<ClayTable.Cell>
							<ClayLink
								displayType="tertiary"
								href={row.link}
								weight="semi-bold"
							>
								{row.page}
							</ClayLink>
						</ClayTable.Cell>

						<ClayTable.Cell align="right">
							{row.count}
						</ClayTable.Cell>

						<ClayTable.Cell align="right">
							{row.percentage}
						</ClayTable.Cell>
					</ClayTable.Row>
				))}
			</ClayTable.Body>
		</ClayTable>
	);
};

const TopPagesMetrics: React.FC = () => {
	const {externalReferenceCode, filters} = useContext(Context);

	const queryString = buildQueryString({
		externalReferenceCode,
		groupId: filters.channel,
		rangeKey: filters.rangeSelector.rangeKey,
	});

	const {data, loading} = useFetch<AssetData>(
		`/o/analytics-cms-rest/v1.0/object-entry-top-pages${queryString}`
	);

	return (
		<section className="mt-3 tab-focus" tabIndex={0}>
			<Title
				section
				value={Liferay.Language.get('top-pages-asset-appears-on')}
			/>

			<Text
				aria-labelledby={Liferay.Language.get(
					'this-metric-calculates-the-top-three-pages-that-generated-the-highest-number-of-views-for-the-asset'
				)}
				color="secondary"
				size={3}
				weight="normal"
			>
				{Liferay.Language.get(
					'this-metric-calculates-the-top-three-pages-that-generated-the-highest-number-of-views-for-the-asset'
				)}
			</Text>

			{loading ? (
				<ClayLoadingIndicator className="my-6" data-testid="loading" />
			) : data?.totalCount ? (
				<TopPagesMetricsTable data={data} />
			) : (
				<EmptyState
					className="pb-6"
					description={Liferay.Language.get(
						'there-is-no-data-available-for-the-applied-filters-or-from-the-data-source'
					)}
					maxWidth={320}
					title={Liferay.Language.get('no-data-available-yet')}
				>
					<ClayLink
						href="https://learn.liferay.com/w/content-management-system/analytics-and-monitoring"
						target="_blank"
					>
						<span className="mr-1">
							{Liferay.Language.get(
								'learn-more-about-asset-performance'
							)}
						</span>

						<ClayIcon fontSize={12} symbol="shortcut" />
					</ClayLink>
				</EmptyState>
			)}
		</section>
	);
};

export {TopPagesMetrics};
