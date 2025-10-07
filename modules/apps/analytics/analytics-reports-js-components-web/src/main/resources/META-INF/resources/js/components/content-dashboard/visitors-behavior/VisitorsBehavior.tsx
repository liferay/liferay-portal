/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useContext} from 'react';

import {Context} from '../../../Context';
import useFetch from '../../../hooks/useFetch';
import {AssetTypes, MetricName} from '../../../types/global';
import {buildQueryString} from '../../../utils/buildQueryString';
import {assetMetrics} from '../../../utils/metrics';
import Title from '../../Title';
import BlogPostingsStateRenderer from './BlogPostingsStateRenderer';
import VisitorsBehaviorStateRenderer from './VisitorsBehaviorStateRenderer';

export type Histogram = {
	metricName: MetricName;
	metrics:
		| {
				value: number;
				valueKey: string;
		  }[]
		| [];
	totalValue: number;
};

export type Data = {
	histograms: Histogram[];
};

export type PublishedVersionData = {
	histogram: (string | null)[];
	total: number;
};

const VisitorsBehavior = () => {
	const {assetId, assetType, filters, groupId} = useContext(Context);

	const queryString = buildQueryString({
		assetId,
		identityType: filters.individual,
		rangeKey: filters.rangeSelector.rangeKey,
		selectedMetrics: assetMetrics[assetType as AssetTypes],
	});

	const {data, loading} = useFetch<{histograms: Histogram[]}>(
		`/o/analytics-reports-rest/v1.0/${groupId}/asset-metrics/${assetType}/histogram${queryString}`
	);

	let Component = VisitorsBehaviorStateRenderer;

	if (assetType === AssetTypes.Blog) {
		Component = BlogPostingsStateRenderer;
	}

	if (loading) {
		return <ClayLoadingIndicator className="my-5" />;
	}

	if (!data) {
		return null;
	}

	return (
		<div>
			<Title
				description={Liferay.Language.get(
					'total-daily-interactions-and-asset-updates'
				)}
				section
				value={Liferay.Language.get('visitors-behavior')}
			/>

			<Component data={data} />
		</div>
	);
};

export default VisitorsBehavior;
