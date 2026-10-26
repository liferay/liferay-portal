type FilterTypes = 'devices' | 'location';

export type Filters = {[key in FilterTypes]: string};

export type RawFilters = {[key in FilterTypes]: string[]};

/**
 * Get Filters
 */
export const getFilters = (
	filters: RawFilters = {devices: [], location: []}
): {[key in FilterTypes]: string} =>
	(['devices', 'location'] as FilterTypes[]).reduce(
		(acc, cur) =>
			filters[cur] && filters[cur].length
				? {...acc, [cur]: filters[cur][0]}
				: acc,
		{devices: 'Any', location: 'Any'}
	);
