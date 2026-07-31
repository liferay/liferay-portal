import sendRequest from 'shared/util/request';

export type CatalogFieldCapability = 'DISPLAY' | 'FILTER' | 'SORT';

export type CatalogFieldDataCategory = 'Boolean' | 'Date' | 'Number' | 'Text';

export interface ICatalogField {
	dataCategory: CatalogFieldDataCategory;
	dataType: string;
	description: string | null;
	displayName: string | null;
	id: string;
	name: string;
	parentField: string | null;
	tableName: string;
}

export interface IFaroFDSResultsDisplay<T> {
	items: T[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}

interface IFetchCatalogFields {
	capability?: CatalogFieldCapability;
	groupId: string;
	page?: number;
	pageSize?: number;
	query?: string;
	sort?: string;
	tableName?: string;
}

export const CATALOG_FIELDS_MAX_PAGE_SIZE = 200;

export const getCatalogFieldLabel = ({displayName, name}: ICatalogField) =>
	displayName || name;

export function fetchCatalogFields({
	capability,
	groupId,
	page = 1,
	pageSize = 20,
	query,
	sort = 'displayName:asc',
	tableName = 'account',
}: IFetchCatalogFields): Promise<IFaroFDSResultsDisplay<ICatalogField>> {
	return sendRequest({
		data: {
			page,
			pageSize,
			sort,
			tableName,
			...(capability && {capability}),
			...(query && {query}),
		},
		method: 'GET',
		path: `main/${groupId}/catalog/fields`,
	});
}
