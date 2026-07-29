import {getFilters} from 'shared/util/filter';
import {getSafeDecodedURIComponent} from 'shared/util/util';
import {getSafeRangeSelectors, getSafeTouchpoint} from 'shared/util/util';
import {isNil, reduce} from 'lodash';

export const formatItem = (item) =>
	reduce(
		item,
		(acc, val, key) => {
			if (val && !isNil(val.value)) {
				acc[key] = val.value;
			}
			else {
				acc[key] = val;
			}

			return acc;
		},
		{}
	);

/**
 * mapListResultsToProps
 * @param {object} response
 * @param {object} response.data
 * @param {ApolloError=} response.error
 * @param {boolean} response.loading
 * @param {function} response.refetch
 */
export const mapListResultsToProps = (
	{data, error, loading, refetch},
	mapperFn = (val) => val
) => {
	if (data) {
		const {items, total} = mapperFn(data);

		const formattedItems = items && items.map(formatItem);

		return {
			empty: !items.length,
			error,
			items: formattedItems,
			loading,
			refetch,
			total,
		};
	}

	return {
		empty: true,
		error,
		items: [],
		loading,
		refetch,
		total: 0,
	};
};

/**
 * Safe Result To Props
 * @param {function} mapper
 */
export function safeResultToProps(mapper) {
	return ({data, ownProps}, context) => {
		let result = {};

		try {
			const {error, loading, refetch} = data;

			if (error) {
				console.error(error); // eslint-disable-line no-console
			}

			if (error || loading) {
				return {error, loading, refetch};
			}

			result = Object.assign(mapper(data, context, ownProps), {
				error: null,
				loading: false,
				refetch,
			});
		}
		catch (error) {
			result.error = error;
			console.error(error); // eslint-disable-line no-console
		}

		return result;
	};
}

/**
 * Get Variables
 * @description Method to return the formatted
 * variables to make the GraphQL request
 * @param {object} filters
 * @param {object} params
 * @param {string} rangeSelectors
 */
export function getVariables({
	accountId,
	assetId: assetIdFromProps,
	experienceId,
	filters,
	interval,
	params,
	rangeSelectors = {},
	segmentId,
}) {
	const {
		assetId: assetIdFromParams,
		channelId,
		title = '',
		touchpoint = '',
	} = params;
	const assetId = assetIdFromProps || assetIdFromParams;

	let variables = {
		title: getSafeDecodedURIComponent(title),
		touchpoint: getSafeTouchpoint(touchpoint),
		...getSafeRangeSelectors(rangeSelectors),
	};

	if (assetId) {
		variables = {
			...variables,
			assetId: getSafeDecodedURIComponent(assetId),
		};
	}

	if (filters) {
		variables = {
			...variables,
			...getFilters(filters),
		};
	}

	if (interval) {
		variables = {
			...variables,
			interval,
		};
	}

	if (channelId) {
		variables = {
			...variables,
			channelId,
		};
	}

	if (experienceId) {
		variables = {
			...variables,
			experienceId,
		};
	}

	if (accountId) {
		variables = {
			...variables,
			accountId,
		};
	}

	if (segmentId) {
		variables = {
			...variables,
			segmentId,
		};
	}

	return {variables};
}
