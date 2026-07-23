import React from 'react';
import {Filters} from 'shared/util/filter';
import {Map, OrderedMap, Set} from 'immutable';
import {Modal} from './Modal';
import {OrderByDirections, RangeKeyTimeRanges} from 'shared/util/constants';

export {Alert} from './Alert';
export {Modal} from './Modal';

export interface IDataColumn {
	accessor: string;
	cellRenderer?: React.ComponentType<any>;
	cellRendererProps?: object;
	className?: string;
	dataFormatter?: (dataValue: React.ReactNode, data?: any) => React.ReactNode;
}

export interface IColumn extends IDataColumn {
	label: string;
	sortable?: boolean;
	title?: boolean;
}

export type Columns = IColumn[];

export type Composition = {
	count: number;
	name: string;
};

export type DisplayType = 'primary' | 'secondary' | 'link' | 'unstyled';

/**
 * FilterBy
 */
export type FilterByType = Map<string, Set<string>>;
export type FilterInputType = 'radio' | 'checkbox';
export type FilterOptionType = {
	label: string;
	key: string;
	type?: FilterInputType;
	values: {label: string; value: string}[];
};

export interface ICompositionBag {
	items: Composition[];
	maxCount: number;
	total: number;
	totalCount: number;
}

export interface IBasePageContext {
	accountId?: string | null;
	accountName?: string | null;
	experienceId?: string | null;
	filters: any;
	router: any;
	rangeSelectors?: RangeSelectors;
}

export interface IPagination {
	delta: number;
	filterBy?: FilterByType;
	orderIOMap: OrderedMap<string, OrderParams>;
	page: number;
	query: string;
}

export type Pagination = {
	delta: number;
	filterBy?: FilterByType;
	orderIOMap: OrderedMap<string, OrderParams>;
	page: number;
	query: string;
};

export type GraphQLPagination = {
	keywords: string;
	size: number;
	sort: OrderParams;
	start: number;
};

export interface IPaginationUnsorted extends Omit<IPagination, 'orderIOMap'> {}

export type RangeSelectors = {
	rangeEnd: string | null;
	rangeKey: RangeKeyTimeRanges;
	rangeStart: string | null;
};

export type RawRangeSelectors = {
	rangeEnd: string | null;
	rangeKey: number;
	rangeStart: string | null;
};

export type SafeRangeSelectors = {
	rangeEnd: string | null;
	rangeKey: number | null;
	rangeStart: string | null;
};

export interface RESTParams {
	delta?: number;
	groupId: string;
	page?: number;
	query?: string;
}

export type Router = {
	params: {
		assetId?: string;
		channelId?: string;
		groupId?: string;
		id?: string;
		interestId?: string;
		jobId?: string;
		tabId?: string;
		title?: string;
		touchpoint?: string;
		type?: string;
	};
	query: {
		field?: string;
		page?: string;
		query?: string;
		rangeEnd?: string;
		rangeKey?: RangeKeyTimeRanges;
		rangeStart?: string;
		sortOrder?: OrderByDirections;
		state?: string;
	};
};

export type Sort = {
	column: string;
	type: OrderByDirections;
};

export interface HasModal {
	close: Modal.close;
	open: Modal.open;
}

import {OrderParams} from 'shared/util/records';

export interface ICommonVariables extends SafeRangeSelectors, Filters {
	interval: Interval;
	type?: string;
}

export type Interval = 'D' | 'M' | 'W';
