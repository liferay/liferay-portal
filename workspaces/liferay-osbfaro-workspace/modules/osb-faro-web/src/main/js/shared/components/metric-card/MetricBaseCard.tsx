import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import MetricChart from './MetricChart';
import MetricTabs from './MetricTabs';
import React, {createContext, useContext, useReducer} from 'react';
import {DocumentNode} from '@apollo/client';
import {getMetricsChartData} from './util';
import {ICommonVariables, Interval, RangeSelectors} from 'shared/types';
import {Metric} from './metrics';
import {RawFilters} from 'shared/util/filter';
import {ReportContainer} from '../download-report/DownloadPDFReport';

const initialState = {
	activeItemIndex: 0,
	chartDataMapFn: null,
	compareToPrevious: false,
	metrics: [],
	queries: {
		MetricQuery: null,
		name: '',
		TabsQuery: null,
	},
	variables: () => ({}),
};

const MetricContext = createContext(initialState as any);

const MetricContextActions = createContext({
	changeActiveItemIndex: () => {},
	changeCompareToPrevious: () => {},
} as any);

export interface ICommonMetricProps {
	accountId?: string;
	emptyDescription?: React.ReactNode;
	emptyTitle?: string;
	filters: RawFilters;
	experienceId?: string;
	interval: Interval;
	rangeSelectors: RangeSelectors;
}

export interface IGenericMetricBaseCardProps {
	emptyDescription?: React.ReactNode;
	emptyTitle?: string;
	label: string;
	legacyDropdownRangeKey?: boolean;
	reportContainer?: ReportContainer;
	showIntervals?: boolean;
}

interface IMetricBaseCardProps<TChartData>
	extends IGenericMetricBaseCardProps,
		React.HTMLAttributes<HTMLElement> {
	chartDataMapFn?: TChartData | typeof getMetricsChartData;
	metrics: Metric[];
	queries: {
		MetricQuery: (metricName: string) => DocumentNode;
		name: string;
		TabsQuery: DocumentNode;
	};
	variables: (commonVariables: ICommonVariables) => any;
}

function MetricBaseCard<TChartData>({
	chartDataMapFn = getMetricsChartData,
	emptyDescription,
	emptyTitle,
	id,
	label,
	legacyDropdownRangeKey = false,
	metrics,
	queries,
	reportContainer,
	showIntervals = false,
	variables,
}: IMetricBaseCardProps<TChartData>): React.ReactElement {
	const [state, dispatch] = useReducer(reducer, initialState);

	const actions = {
		changeActiveItemIndex: (activeItemIndex: number) => {
			dispatch({
				payload: activeItemIndex,
				type: Actions.UpdateActiveItemIndex,
			});
		},
		changeCompareToPrevious: (compareToPrevious: boolean) => {
			dispatch({
				payload: compareToPrevious,
				type: Actions.UpdateCompareToPrevious,
			});
		},
	};

	return (
		<MetricContext.Provider
			value={{
				...state,
				chartDataMapFn,
				metrics,
				queries,
				variables,
			}}
		>
			<MetricContextActions.Provider value={actions}>
				<BaseCard
					className="analytics-metrics-card"
					id={id}
					label={label}
					legacyDropdownRangeKey={legacyDropdownRangeKey}
					minHeight={605}
					reportContainer={reportContainer}
					showInterval={showIntervals}
				>
					{({
						accountId,
						experienceId,
						filters,
						interval,
						rangeSelectors,
					}) => {
						const sharedProps: ICommonMetricProps = {
							accountId,
							emptyDescription,
							emptyTitle,
							experienceId,
							filters,
							interval,
							rangeSelectors,
						};

						return (
							<Card.Body className="analytics-metrics">
								<MetricTabs {...sharedProps} />

								<MetricChart {...sharedProps} />
							</Card.Body>
						);
					}}
				</BaseCard>
			</MetricContextActions.Provider>
		</MetricContext.Provider>
	);
}

type TMetricState = {
	activeItemIndex: number;
	chartDataMapFn: unknown;
	compareToPrevious: boolean;
	metrics: Metric[];
	queries: {
		MetricQuery: ((metricName: string) => DocumentNode) | null;
		name: string;
		TabsQuery: DocumentNode | null;
	};
	variables: (commonVariables: ICommonVariables) => any;
};

type TMetricAction = {
	payload: any;
	type: Actions;
};

export const reducer = (state: TMetricState, action: TMetricAction) => {
	const handlerFn = actionHandlers[action.type];

	if (handlerFn) {
		return handlerFn(state, action);
	}

	throw new Error('Unhandled action type: ${type}');
};

enum Actions {
	UpdateActiveItemIndex = 'UPDATE_ACTIVE_ITEM_INDEX',
	UpdateCompareToPrevious = 'UPDATE_COMPARE_TO_PREVIOUS',
}

const actionHandlers: Record<
	Actions,
	(state: TMetricState, action: TMetricAction) => TMetricState
> = {
	[Actions.UpdateActiveItemIndex]: (state, {payload}) => ({
		...state,
		activeItemIndex: payload,
	}),
	[Actions.UpdateCompareToPrevious]: (state, {payload}) => ({
		...state,
		compareToPrevious: payload,
	}),
};

export const useData = () => useContext(MetricContext);
export const useActions = () => useContext(MetricContextActions);

export default MetricBaseCard;
