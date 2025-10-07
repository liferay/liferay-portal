/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {Context} from '../../../Context';
import {metricNameByType} from '../../../utils/metrics';
import {Data, PublishedVersionData} from './VisitorsBehavior';
import VisitorsBehaviorChart from './VisitorsBehaviorChart';
import {
	formatPublishedDate,
	getSelectedHistogram,
	mapPublishedDatesToHistogram,
	sortPublishedDates,
} from './utils';

interface IVisitorsBehaviorStateRendererProps {
	data: Data;
}

const VisitorsBehaviorStateRenderer: React.FC<
	IVisitorsBehaviorStateRendererProps
> = ({data}) => {
	const {filters, versions} = useContext(Context);

	let publishedVersionData: PublishedVersionData | null = null;
	let dates: {date: string; version: string}[] = [];

	const metricName = metricNameByType[filters.metric];

	const selectedHistogram = getSelectedHistogram(data, metricName);

	if (versions?.length && selectedHistogram) {
		dates = versions.map(
			({createDate, version}: {createDate: string; version: string}) => ({
				date: formatPublishedDate(createDate),
				version,
			})
		);

		publishedVersionData = {
			histogram: mapPublishedDatesToHistogram(
				sortPublishedDates(dates),
				selectedHistogram
			),
			total: versions.length,
		};
	}

	return (
		<VisitorsBehaviorChart
			data={data}
			publishedVersionData={publishedVersionData}
		/>
	);
};

export default VisitorsBehaviorStateRenderer;
