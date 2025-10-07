/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useContext} from 'react';

import {Context} from '../../../Context';
import useFetch from '../../../hooks/useFetch';
import {MetricName, MetricType} from '../../../types/global';
import {buildQueryString} from '../../../utils/buildQueryString';
import Title from '../../Title';
import StackedBarChart from '../stacked-bar/StackedBarChart';
import {formatData} from './utils';

export type Data = {
	deviceMetrics: {
		metricName: MetricName;
		metrics: {value: number; valueKey: string}[];
	}[];
};

const TITLE: {
	[key in MetricType]: string;
} = {
	[MetricType.Comments]: Liferay.Language.get('comments-by-technology'),
	[MetricType.Downloads]: Liferay.Language.get('downloads-by-technology'),
	[MetricType.Impressions]: Liferay.Language.get('impressions-by-technology'),
	[MetricType.Undefined]: Liferay.Language.get('undefined'),
	[MetricType.Views]: Liferay.Language.get('views-by-technology'),
};

const Technology = () => {
	const {assetId, assetType, filters, groupId} = useContext(Context);

	const queryString = buildQueryString({
		assetId,
		identityType: filters.individual,
		rangeKey: filters.rangeSelector.rangeKey,
	});

	const {data, loading} = useFetch<Data>(
		`/o/analytics-reports-rest/${groupId}/asset-metrics/${assetType}/devices${queryString}`
	);

	const title = TITLE[filters.metric];

	if (loading) {
		return <ClayLoadingIndicator className="my-5" />;
	}

	if (!data) {
		return null;
	}

	const formattedData = formatData(data, filters.metric);

	return (
		<div>
			<Title
				description={Liferay.Language.get(
					'the-total-number-of-downloads-is-broken-down-by-device-types-during-the-selected-time-period'
				)}
				section
				value={title}
			/>

			<StackedBarChart data={formattedData} tooltipTitle={title} />
		</div>
	);
};

export default Technology;
