import CardTabs from 'shared/components/CardTabs';
import MetricStateRenderer from './MetricStateRenderer';
import React from 'react';
import {buildTabs, getMetricCardTabsData} from './util';
import {ICommonMetricProps, useActions, useData} from './MetricBaseCard';
import {Metric} from './metrics';
import {useMetricQuery} from './hooks';

interface IMetricTabsViewProps {
	activeItemIndex: number;
	changeActiveItemIndex: (index: number) => void;
	data: any;
	metrics: Metric[];
	queryName: string;
}

const MetricTabsView: React.FC<IMetricTabsViewProps> = React.memo(
	({activeItemIndex, changeActiveItemIndex, data, metrics, queryName}) => {
		const items = getMetricCardTabsData(data[queryName], metrics);

		return (
			<CardTabs
				activeTabId={activeItemIndex}
				className="analytics-metrics-tabs"
				tabs={buildTabs({
					activeItemIndex,
					items,
					onActiveItemIndexChange: changeActiveItemIndex,
				})}
			/>
		);
	}
);

const MetricTabsRenderer: React.FC<ICommonMetricProps> = ({
	accountId,
	experienceId,
	filters,
	interval,
	rangeSelectors,
	segmentId,
}) => {
	const {activeItemIndex, metrics, queries, variables} = useData();
	const {changeActiveItemIndex} = useActions();

	const {data, error, loading} = useMetricQuery({
		accountId,
		experienceId,
		filters,
		interval,
		Query: queries.TabsQuery,
		rangeSelectors,
		segmentId,
		variables,
	});

	return (
		<MetricStateRenderer error={error} loading={loading} spacer>
			<MetricTabsView
				activeItemIndex={activeItemIndex}
				changeActiveItemIndex={changeActiveItemIndex}
				data={data}
				metrics={metrics}
				queryName={queries.name}
			/>
		</MetricStateRenderer>
	);
};

export default MetricTabsRenderer;
