/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useContext} from 'react';

import {Context} from '../../../Context';
import useFetch from '../../../hooks/useFetch';
import {MetricName} from '../../../types/global';
import {buildQueryString} from '../../../utils/buildQueryString';
import Title from '../../Title';
import InteractionsByPageChart from './InteractionsByPageChart';

export type Data = {
	assetAppearsOnHistograms: {
		appearsOnHistograms: {
			canonicalUrl: string;
			metrics:
				| {
						value: number;
						valueKey: string;
				  }[]
				| [];
			pageTitle: string;
			totalValue: number;
		}[];
		metricName: MetricName;
	}[];
};

const InteractionsByPage = () => {
	const {assetId, assetType, filters, groupId} = useContext(Context);

	const queryString = buildQueryString({
		assetId,
		identityType: filters.individual,
		rangeKey: filters.rangeSelector.rangeKey,
	});

	const {data, loading} = useFetch<Data>(
		`/o/analytics-reports-rest/v1.0/${groupId}/asset-metrics/${assetType}/appears-on/histogram${queryString}`
	);

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
					'top-three-pages-with-the-highest-individual-interactions-during-the-selected-time-period'
				)}
				section
				value={Liferay.Language.get('top-pages-asset-appears-on')}
			/>

			<InteractionsByPageChart data={data} />
		</div>
	);
};

export default InteractionsByPage;
