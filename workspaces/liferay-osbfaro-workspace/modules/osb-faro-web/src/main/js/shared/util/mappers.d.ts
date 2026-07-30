/**
 * Ambient declarations for shared/util/mappers.js (legacy untyped helper).
 * Keep in sync with mappers.js until that file is migrated to TypeScript.
 */

import {ApolloError} from '@apollo/client';

export function formatItem<T extends object>(item: T): Record<string, unknown>;

interface IMapListResultsInput<TData = any> {
	data?: TData | null;
	error?: ApolloError | unknown;
	loading: boolean;
	refetch?: (...args: any[]) => any;
}

interface IMapListResultsOutput<TItem = any> {
	empty: boolean;
	error: ApolloError | unknown;
	items: TItem[];
	loading: boolean;
	refetch?: (...args: any[]) => any;
	total: number;
}

export function mapListResultsToProps<TData = any, TItem = any>(
	response: IMapListResultsInput<TData>,
	mapperFn?: (data: TData) => {items?: TItem[]; total?: number}
): IMapListResultsOutput<TItem>;

export function safeResultToProps<TData = any>(
	mapper: (data: TData, context?: any, ownProps?: any) => Record<string, any>
): (props: {data: any; ownProps: any}, context?: any) => Record<string, any>;

export function getVariables(args: {
	accountId?: string | null;
	assetId?: string;
	experienceId?: string;
	filters?: any;
	interval?: string;
	params: Record<string, string | undefined>;
	rangeSelectors?: any;
	segmentId?: string;
}): {variables: Record<string, any>};
