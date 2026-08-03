import {TrendClassification} from 'segment/types';

export enum AccountMetricType {
	Active = 'activeCount',
	New = 'newCount',
	Total = 'totalCount',
}
export enum AccountOverviewMetricType {
	AnonymousIndividuals = 'anonymousIndividualsCount',
	FirstTimeIndividuals = 'firstTimeIndividualsCount',
	InactiveIndividuals = 'inactiveIndividualsCount',
	KnownIndividuals = 'knownIndividualsCount',
	ReturningIndividuals = 'returningIndividualsCount',
	TotalIndividuals = 'totalIndividualsCount',
}

export interface IAccountMetric extends Metric {
	metricType: AccountMetricType;
}
export interface IAccountOverviewMetric {
	metricType: AccountOverviewMetricType;
	value: number;
}

export type Metric = {
	trend: Trend;
	value: number;
};

export type Trend = {
	percentage: number;
	trendClassification: TrendClassification;
};
