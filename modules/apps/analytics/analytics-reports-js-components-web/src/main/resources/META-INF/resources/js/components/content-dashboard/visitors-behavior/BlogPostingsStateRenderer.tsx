/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useContext} from 'react';

import {Context} from '../../../Context';
import useFetch from '../../../hooks/useFetch';
import {metricNameByType} from '../../../utils/metrics';
import {Data} from './VisitorsBehavior';
import VisitorsBehaviorChart from './VisitorsBehaviorChart';
import {
	formatPublishedDate,
	getSelectedHistogram,
	mapPublishedDatesToHistogram,
} from './utils';

type BlogPostingsData = {
	datePublished: string;
};

interface IBlogPostingsStateRendererProps {
	data: Data;
}

interface IVisitorsBehaviorWithBlogDataProps {
	data: BlogPostingsData;
	visitorsBehaviorData: Data;
}

const VisitorsBehaviorWithBlogData: React.FC<
	IVisitorsBehaviorWithBlogDataProps
> = ({data, visitorsBehaviorData}) => {
	const {filters} = useContext(Context);

	const metricName = metricNameByType[filters.metric];
	const selectedHistogram = getSelectedHistogram(
		visitorsBehaviorData,
		metricName
	);

	let publishedVersionData = null;

	if (selectedHistogram) {
		const dates = [
			{
				date: formatPublishedDate(data.datePublished),
				version: '1.0',
			},
		];

		publishedVersionData = {
			histogram: mapPublishedDatesToHistogram(dates, selectedHistogram),
			total: 1,
		};
	}

	return (
		<VisitorsBehaviorChart
			data={visitorsBehaviorData}
			publishedVersionData={publishedVersionData}
		/>
	);
};

const BlogPostingsStateRenderer: React.FC<IBlogPostingsStateRendererProps> = ({
	data: visitorsBehaviorData,
}) => {
	const {assetId} = useContext(Context);

	const {data, loading} = useFetch<BlogPostingsData>(
		`/o/headless-delivery/v1.0/blog-postings/${assetId}`
	);

	if (loading) {
		return <ClayLoadingIndicator className="my-5" />;
	}

	if (!data) {
		return null;
	}

	return (
		<VisitorsBehaviorWithBlogData
			data={data}
			visitorsBehaviorData={visitorsBehaviorData}
		/>
	);
};

export default BlogPostingsStateRenderer;
