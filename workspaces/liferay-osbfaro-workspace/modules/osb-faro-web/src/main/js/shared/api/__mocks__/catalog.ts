import type {ICatalogField, IFaroFDSResultsDisplay} from 'shared/api/catalog';

const mockCatalogField = (
	name: string,
	dataCategory: ICatalogField['dataCategory']
): ICatalogField => ({
	dataCategory,
	dataType: dataCategory,
	description: null,
	displayName: null,
	id: name,
	name,
	parentField: null,
	tableName: 'account',
});

export const fetchCatalogFields = jest.fn(
	(): Promise<IFaroFDSResultsDisplay<ICatalogField>> =>
		Promise.resolve({
			items: [
				mockCatalogField('accountName', 'Text'),
				mockCatalogField('industry', 'Text'),
				mockCatalogField('annualRevenue', 'Number'),
				mockCatalogField('lastActive', 'Date'),
			],
			lastPage: 1,
			page: 1,
			pageSize: 20,
			totalCount: 4,
		})
);
