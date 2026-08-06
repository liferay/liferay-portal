import React from 'react';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {toLocale, toRounded} from 'shared/util/numbers';
import {UsageMetricBarChart} from './UsageMetricBarChart';

interface ICurrentUsageProps {
	count: number;
	items: {
		[key: string]: {color: string; label: string; value: number};
	};
	legendText: React.ReactNode;
	limit: number;
	percentageText: (percentage: number) => string;
}

export const CurrentUsage = ({
	count,
	items,
	legendText,
	limit,
	percentageText,
}: ICurrentUsageProps) => {
	const percentage =
		limit > 0 ? (count / limit >= 1 ? 100 : (count / limit) * 100) : 0;

	return (
		<>
			<div className="d-flex justify-content-between mb-1">
				<Text color="secondary" size={3}>
					{Liferay.Language.get('current-usage').toUpperCase()}
				</Text>

				<Text color="secondary" size={3}>
					{`${sub(Liferay.Language.get('x-of-x'), [
						toLocale(count),
						toLocale(limit),
					])} - ${sub(percentageText(percentage), [
						toRounded(percentage),
					])}`}
				</Text>
			</div>

			<UsageMetricBarChart
				items={items}
				showLegend={false}
				total={limit}
			/>

			<Text color="secondary" size={3}>
				{legendText}
			</Text>
		</>
	);
};
