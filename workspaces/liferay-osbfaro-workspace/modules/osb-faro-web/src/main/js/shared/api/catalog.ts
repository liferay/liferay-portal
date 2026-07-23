import sendRequest from 'shared/util/request';

export type CatalogFieldDataCategory = 'Boolean' | 'Date' | 'Number' | 'Text';

export interface ICatalogField {
	dataCategory: CatalogFieldDataCategory;
	dataType: string;
	description: string;
	displayName: string;
	entity: string;
	id: string;
	name: string;
	parentField: string | null;
}

export interface IFaroFDSResultsDisplay<T> {
	items: T[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}

interface IFetchCatalogFields {
	entity?: string;
	groupId: string;
	page?: number;
	pageSize?: number;
	search?: string;
	sort?: string;
}

export const CATALOG_FIELDS_MAX_PAGE_SIZE = 200;

export function fetchCatalogFields({
	entity = 'account',
	groupId,
	page = 1,
	pageSize = 20,
	search,
	sort = 'displayName:asc',
}: IFetchCatalogFields): Promise<IFaroFDSResultsDisplay<ICatalogField>> {
	return sendRequest({
		data: {
			entity,
			page,
			pageSize,
			sort,
			...(search && {search}),
		},
		method: 'GET',
		path: `contacts/${groupId}/catalog/fields`,
	});
}
