import {getCatalogFieldLabel, ICatalogField} from 'shared/api/catalog';

export interface IViewField {
	contentRenderer?: string;
	fieldName: string;
	label: string;
	sortable: boolean;
	truncate?: boolean;
}

const DEFAULT_VIEW_FIELDS: IViewField[] = [
	{
		contentRenderer: 'accountNameRenderer',
		fieldName: 'accountName',
		label: Liferay.Language.get('account'),
		sortable: true,
		truncate: true,
	},
	{
		fieldName: 'industry',
		label: Liferay.Language.get('industry'),
		sortable: true,
	},
	{
		contentRenderer: 'accountLifecycleStageRenderer',
		fieldName: 'lifecycleStage',
		label: Liferay.Language.get('lifecycle-stage'),
		sortable: true,
	},
	{
		contentRenderer: 'annualRevenueRenderer',
		fieldName: 'annualRevenue',
		label: Liferay.Language.get('annual-revenue'),
		sortable: true,
	},
	{
		fieldName: 'country',
		label: Liferay.Language.get('country'),
		sortable: true,
	},
	{
		contentRenderer: 'dateRenderer',
		fieldName: 'firstActive',
		label: Liferay.Language.get('first-active'),
		sortable: true,
	},
	{
		contentRenderer: 'dateRenderer',
		fieldName: 'lastActive',
		label: Liferay.Language.get('last-active'),
		sortable: true,
	},
	{
		contentRenderer: 'activitiesCountRenderer',
		fieldName: 'activitiesCount',
		label: Liferay.Language.get('recent-activities'),
		sortable: true,
	},
	{
		contentRenderer: 'dateRenderer',
		fieldName: 'lastEnriched',
		label: Liferay.Language.get('last-enriched'),
		sortable: true,
	},
];

const DEFAULT_VIEW_FIELD_NAMES = new Set(
	DEFAULT_VIEW_FIELDS.map(({fieldName}) => fieldName)
);

const DATA_CATEGORY_RENDERERS: {[dataCategory: string]: string} = {
	date: 'dateRenderer',
};

const buildViewField = (field: ICatalogField): IViewField => ({
	contentRenderer:
		DATA_CATEGORY_RENDERERS[(field.dataCategory ?? '').toLowerCase()],
	fieldName: field.name,
	label: getCatalogFieldLabel(field),
	sortable: false,
});

const buildCatalogFields = (fieldCatalog: ICatalogField[]): IViewField[] => {
	const seen = new Set(DEFAULT_VIEW_FIELD_NAMES);

	return fieldCatalog
		.filter(({name}) => {
			if (seen.has(name)) {
				return false;
			}

			seen.add(name);

			return true;
		})
		.map(buildViewField);
};

export const buildViews = (fieldCatalog?: ICatalogField[]) => {
	const defaultView = {
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('default-view'),
		name: 'table',
		schema: {fields: DEFAULT_VIEW_FIELDS},
		thumbnail: 'table',
	};

	const catalogFields = fieldCatalog ? buildCatalogFields(fieldCatalog) : [];

	if (!catalogFields.length) {
		return [defaultView];
	}

	return [
		defaultView,
		{
			contentRenderer: 'table',
			label: Liferay.Language.get('all-attributes'),
			name: 'allAttributes',
			schema: {fields: [...DEFAULT_VIEW_FIELDS, ...catalogFields]},
			thumbnail: 'table',
		},
	];
};
