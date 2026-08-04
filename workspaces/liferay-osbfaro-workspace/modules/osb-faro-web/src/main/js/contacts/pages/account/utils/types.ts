import {TrendClassification} from 'segment/types';

export enum AccountIndividualMetricType {
	AnonymousIndividuals = 'anonymousIndividualsCount',
	FirstTimeIndividuals = 'firstTimeIndividualsCount',
	InactiveIndividuals = 'inactiveIndividualsCount',
	KnownIndividuals = 'knownIndividualsCount',
	ReturningIndividuals = 'returningIndividualsCount',
	TotalIndividuals = 'totalIndividualsCount',
}
export enum AccountMetricType {
	Active = 'activeCount',
	New = 'newCount',
	Total = 'totalCount',
}

export interface IAccountIndividualMetric {
	metricType: AccountIndividualMetricType;
	value: number;
}
export interface IAccountMetric extends Metric {
	metricType: AccountMetricType;
}

export type Metric = {
	trend: Trend;
	value: number;
};

export type Trend = {
	percentage: number;
	trendClassification: TrendClassification;
};
