import {InMemoryCache} from '@apollo/client';

/*
 * Custom merge function for the root metric fields (`site`, `blog`, ...).
 *
 * Each of these fields is selected by multiple metric-card queries that
 * share the same arguments but request different sub-fields of the same
 * nested `*Metric` object (one query selects `geolocation`, another
 * selects `browser`/`device`/`value`, etc.). Because the root metric
 * objects are not normalized (no stable `id` selected today), they share
 * a single cache entry — and Apollo's default `merge: true` only does a
 * shallow merge, so the second write would replace the first's nested
 * `*Metric` instead of fusing it. The losing query's cached data would
 * then be incomplete, and Apollo would refetch it as soon as the broadcast
 * after the second write reached its watcher (the symptom: a second
 * network request that only starts after the first one completes).
 *
 * A recursive merge fuses sibling sub-fields under the same nested object
 * so both queries' data coexist after their writes, suppressing the
 * cascade refetch.
 *
 * The canonical fix is server-side: each root metric type should expose
 * a stable, deterministic `id` derived from the resolver arguments so
 * InMemoryCache can normalize them automatically. Once the backend ships
 * that change, this typePolicies block can be removed and the metric-card
 * queries should select the new `id` field. Tracking ticket:
 * <add backend ticket id here once filed>.
 */

const isPlainObject = (value) =>
	value !== null && typeof value === 'object' && !Array.isArray(value);

const deepMergeMetric = (existing, incoming) => {
	if (!isPlainObject(existing) || !isPlainObject(incoming)) {
		return incoming;
	}

	const merged = {...existing};

	for (const key of Object.keys(incoming)) {
		merged[key] = deepMergeMetric(existing[key], incoming[key]);
	}

	return merged;
};

export {deepMergeMetric};

const metricRootField = {merge: deepMergeMetric};

const metricRootMerge = {
	blog: metricRootField,
	document: metricRootField,
	form: metricRootField,
	journal: metricRootField,
	objectEntry: metricRootField,
	page: metricRootField,
	site: metricRootField,
};

export const typePolicies = {
	Query: {
		fields: metricRootMerge,
	},
};

export default new InMemoryCache({typePolicies});
