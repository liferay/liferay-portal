/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';
import {
	Bar,
	BarChart,
	CartesianGrid,
	ReferenceLine,
	ResponsiveContainer,
	XAxis,
	YAxis,
} from 'recharts';

import useAnalyticsQuery from '../../../common/hooks/useAnalyticsQuery';
import {IFrequencyChartItem} from '../../../common/utils/types';
import AnalyticsFrame from './AnalyticsFrame';
import Loader from './Loader';

interface IVisitFrequencyItem {
	count: number;
	name: string;
}

const margin = {
	bottom: 5,
	left: 20,
	right: 30,
	top: 20,
};

const getFrequencyLabel = (type: string): string => {
	if (type === 'DAILY') {
		return Liferay.Language.get('daily');
	}
	if (type === 'WEEKLY') {
		return Liferay.Language.get('weekly');
	}
	if (type === 'BIWEEKLY') {
		return Liferay.Language.get('biweekly');
	}
	if (type === 'MONTHLY') {
		return Liferay.Language.get('monthly');
	}

	return type;
};

const formatData = (
	visitFrequencyItems: IVisitFrequencyItem[]
): IFrequencyChartItem[] => {
	if (!visitFrequencyItems) {
		return [];
	}

	return visitFrequencyItems.map((visitFrequencyItem) => ({
		frequencyType: getFrequencyLabel(visitFrequencyItem.name),
		visitCount: visitFrequencyItem.count || 0,
	}));
};

function FrequencyChart() {
	const [data, setData] = useState<IFrequencyChartItem[]>([]);
	const [element, setElement] = useState<HTMLElement | null>(null);

	const {isLoading, response} = useAnalyticsQuery({
		element,
		query: {paths: [{key: 'visitFrequency', path: '/visit-frequency'}]},
		variables: {
			rangeKey: 30,
		},
	});

	useEffect(() => {
		if (response) {
			setData(formatData(response.visitFrequency?.visitFrequencyItems));
		}

		return () => {};
	}, [response, setData]);

	return (
		<AnalyticsFrame
			icon="liferay-ac"
			title={Liferay.Language.get('visits-frequency')}
		>
			<div ref={setElement}>
				{isLoading ? (
					<Loader />
				) : !data?.length ? (
					<p className="mt-3 text-center text-muted">
						{Liferay.Language.get('no-data-available')}
					</p>
				) : (
					<ResponsiveContainer aspect={1.4028} width="100%">
						<BarChart data={data} margin={margin}>
							{data.map(
								(
									frequencyChartItem: IFrequencyChartItem,
									index: number
								) => {
									return (
										<ReferenceLine
											key={`bg-strip-${index}`}
											stroke="#E5F1FF"
											strokeOpacity={0.3}
											strokeWidth={60}
											x={frequencyChartItem.frequencyType}
										/>
									);
								}
							)}

							<CartesianGrid
								stroke="#ccc"
								strokeDasharray="5 5"
								vertical={(props: any) => (
									<line
										key={props.key}
										stroke="none"
										x1={props.x1}
										x2={props.x2}
										y1={props.y1}
										y2={props.y2}
									/>
								)}
							/>

							<Bar
								barSize={60}
								dataKey="visitCount"
								fill="#97C5FF"
								radius={[4, 4, 0, 0]}
							/>

							<XAxis dataKey="frequencyType" />

							<YAxis />
						</BarChart>
					</ResponsiveContainer>
				)}
			</div>
		</AnalyticsFrame>
	);
}

export default FrequencyChart;
