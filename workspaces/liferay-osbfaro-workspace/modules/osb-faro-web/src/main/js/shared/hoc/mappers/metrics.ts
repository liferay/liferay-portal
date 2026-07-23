import {formatItem, getVariables, safeResultToProps} from 'shared/util/mappers';
import {get, isEmpty} from 'lodash';
import {getSortFromOrderIOMap} from 'shared/util/pagination';
import {
	getVariableDefinitions,
	GQLQuery,
	removeUnusedVariables,
} from 'shared/util/graphql';

type GraphQLOptions = {variables: {[key: string]: any}};

export const getMapPropsToOptions: (
	gqlQuery: GQLQuery,
	options?: object
) => (props: {[key: string]: any}) => GraphQLOptions =
	(gqlQuery, options = {}) =>
	({
		accountId,
		delta,
		filters,
		interestId,
		orderIOMap,
		page,
		query,
		rangeSelectors,
		router: {params, query: routerQuery},
	}) => {
		const {variables} = getVariables({
			accountId,
			filters,
			params,
			rangeSelectors,
		});

		// LRAC-6976 POC TEMP

		const useDB = get(routerQuery, 'useDB', null) === 'true';

		let unfilteredVariables: any = {
			...variables,
			keywords: query,
			size: delta,
			sort: getSortFromOrderIOMap(orderIOMap),
			start: (page - 1) * delta,
			terms: interestId,
		};

		// LRAC-6976 POC TEMP

		if (useDB) {
			unfilteredVariables = {...unfilteredVariables, useDB};
		}

		const validVariables: Record<string, boolean> = gqlQuery
			? getVariableDefinitions(gqlQuery)
			: {};

		return {
			variables: isEmpty(validVariables)
				? unfilteredVariables
				: removeUnusedVariables(unfilteredVariables, validVariables),
			...options,
		};
	};

export const getMapResultToProps = (
	getResults: (result: any) => {items: any; total: any}
) =>
	safeResultToProps((result) => {
		const {items, total} = getResults(result);

		const formattedItems = items && items.map(formatItem);

		return {
			empty: !items.length,
			items: formattedItems,
			total,
		};
	}) as any;

const getMetricsMapper = (
	getResults: (result: any) => {items: any; total: any},
	options: object = {},
	gqlQuery: GQLQuery | null = null
) => ({
	options: getMapPropsToOptions(gqlQuery as GQLQuery, options),
	props: getMapResultToProps(getResults),
});

export default getMetricsMapper;
