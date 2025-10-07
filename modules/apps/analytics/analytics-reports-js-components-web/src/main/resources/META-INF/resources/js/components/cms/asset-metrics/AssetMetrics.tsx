/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useContext, useState} from 'react';

import {Context} from '../../../Context';
import useFetch from '../../../hooks/useFetch';
import {MetricName, MetricType} from '../../../types/global';
import {buildQueryString} from '../../../utils/buildQueryString';
import EmptyState from '../../EmptyState';
import {RangeSelectors} from '../../RangeSelectorsDropdown';
import {AssetMetricsChart} from './AssetMetricsChart';
import {AssetMetricsTableView} from './AssetMetricsTableView';

export type Histogram = {
	metricName: string;
	metrics: {
		previousValue: number;
		previousValueKey: string;
		value: number;
		valueKey: string;
	}[];
	total: number;
	totalValue: number;
};

export interface ICommonProps {
	histogram: Histogram;
	metricName: MetricName;
	metricType: MetricType;
	rangeSelector: RangeSelectors;
	title: string;
}

const metricName: Partial<{
	[key in MetricType]: MetricName;
}> = {
	[MetricType.Views]: MetricName.Views,
	[MetricType.Impressions]: MetricName.Impressions,
	[MetricType.Downloads]: MetricName.Downloads,
};

const metricTitle: Partial<{
	[key in MetricType]: string;
}> = {
	[MetricType.Views]: Liferay.Language.get('views'),
	[MetricType.Impressions]: Liferay.Language.get('impressions'),
	[MetricType.Downloads]: Liferay.Language.get('downloads'),
};

const renderComponent = (Component: React.ComponentType<ICommonProps>) => {
	return (props: ICommonProps) => {
		if (!props.histogram) {
			return <></>;
		}

		return <Component {...props} />;
	};
};

const dropdownItems: {
	icon: string;
	name: string;
	renderer: (props: ICommonProps) => JSX.Element;
	value: string;
}[] = [
	{
		icon: 'analytics',
		name: Liferay.Language.get('chart'),
		renderer: renderComponent(AssetMetricsChart),
		value: 'chart',
	},
	{
		icon: 'table',
		name: Liferay.Language.get('table'),
		renderer: renderComponent(AssetMetricsTableView),
		value: 'table',
	},
];

const AssetMetrics = () => {
	const {externalReferenceCode, filters} = useContext(Context);

	const [dropdownActive, setDropdownActive] = useState(false);
	const [selectedItem, setSelectedItem] = useState(dropdownItems[0]);

	const queryParams = buildQueryString({
		externalReferenceCode,
		groupId: filters.channel,
		rangeKey: filters.rangeSelector.rangeKey,
		selectedMetrics: metricName[filters.metric] as string,
	});

	const {data, loading} = useFetch<{
		histograms: Histogram[];
	}>(
		`/o/analytics-cms-rest/v1.0/object-entry-histogram-metric${queryParams}`
	);

	const histogram = data?.histograms.find(
		({metricName: currentMetricName}) =>
			currentMetricName === metricName[filters.metric]
	);

	return (
		<>
			<div className="align-items-center d-flex justify-content-around mt-3">
				<span className="text-3 text-secondary">
					{Liferay.Language.get(
						'this-metric-calculates-the-total-number-of-times-an-asset-is-seen-by-visitors'
					)}
				</span>

				<ClayDropdown
					active={dropdownActive}
					closeOnClickOutside
					data-testid="btn-change-view"
					onActiveChange={setDropdownActive}
					trigger={
						<ClayButton
							aria-label={selectedItem.name}
							borderless
							displayType="secondary"
							onClick={() => {
								setDropdownActive(!dropdownActive);
							}}
							size="sm"
						>
							{selectedItem.icon && (
								<ClayIcon symbol={selectedItem.icon} />
							)}

							<ClayIcon className="mx-2" symbol="caret-bottom" />
						</ClayButton>
					}
				>
					{dropdownItems.map((item) => (
						<ClayDropdown.Item
							active={item.value === selectedItem.value}
							key={item.value}
							onClick={() => {
								setSelectedItem(item);
								setDropdownActive(false);
							}}
						>
							{item.icon && (
								<ClayIcon className="mr-2" symbol={item.icon} />
							)}

							{item.name}
						</ClayDropdown.Item>
					))}
				</ClayDropdown>
			</div>

			{loading && (
				<ClayLoadingIndicator className="my-6" data-testid="loading" />
			)}

			{!loading && (
				<>
					{histogram?.metrics.length === 0 ? (
						selectedItem.value === 'chart' ? (
							<main className="mt-3">
								{selectedItem.renderer({
									histogram,
									metricName: metricName[
										filters.metric
									] as MetricName,
									metricType: filters.metric,
									rangeSelector:
										filters.rangeSelector.rangeKey,
									title: metricTitle[
										filters.metric
									] as string,
								})}
							</main>
						) : (
							<EmptyState
								className="pb-6"
								description={Liferay.Language.get(
									'there-is-no-data-available-for-the-applied-filters-or-from-the-data-source'
								)}
								maxWidth={320}
								title={Liferay.Language.get(
									'no-data-available-yet'
								)}
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
						)
					) : (
						<main className="mt-3">
							{selectedItem.renderer({
								histogram: histogram as Histogram,
								metricName: metricName[
									filters.metric
								] as MetricName,
								metricType: filters.metric,
								rangeSelector: filters.rangeSelector.rangeKey,
								title: metricTitle[filters.metric] as string,
							})}
						</main>
					)}
				</>
			)}
		</>
	);
};
export {AssetMetrics};
