export interface ILifecycleFilterValues {
	countryFilter: string;
	industryFilter: string;
	lifecycleStageFilter?: string;
}

const FILTER_CONFIG: {
	field: keyof ILifecycleFilterValues;
	fieldName: string;
	op: string;
}[] = [
	{field: 'countryFilter', fieldName: 'country', op: 'eq'},
	{field: 'industryFilter', fieldName: 'industry', op: 'eq'},
];

export const buildQueryString = (values: ILifecycleFilterValues): string =>
	FILTER_CONFIG.map(({field, fieldName, op}) => {
		const val = values[field];
		return val !== '' ? `${fieldName} ${op} '${val}'` : null;
	})
		.filter(Boolean)
		.join(' and ');
