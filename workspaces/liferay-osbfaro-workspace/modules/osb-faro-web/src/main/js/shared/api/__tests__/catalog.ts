jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {CATALOG_FIELDS_MAX_PAGE_SIZE, fetchCatalogFields} from '../catalog';

describe('Catalog API', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockClear();
	});

	describe('fetchCatalogFields', () => {
		it('reads the fields from the main application', () => {
			fetchCatalogFields({groupId: '23'});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					method: 'GET',
					path: 'main/23/catalog/fields',
				})
			);
		});

		it('defaults to the account table and sorts by display name', () => {
			fetchCatalogFields({groupId: '23'});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					page: 1,
					pageSize: 20,
					sort: 'displayName:asc',
					tableName: 'account',
				},
				method: 'GET',
				path: 'main/23/catalog/fields',
			});
		});

		it('sends the whole catalog in a single page when asked', () => {
			fetchCatalogFields({
				groupId: '23',
				pageSize: CATALOG_FIELDS_MAX_PAGE_SIZE,
			});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					data: expect.objectContaining({
						pageSize: CATALOG_FIELDS_MAX_PAGE_SIZE,
					}),
				})
			);
		});

		it('passes the free text search as the query param', () => {
			fetchCatalogFields({groupId: '23', query: 'revenue'});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					data: expect.objectContaining({query: 'revenue'}),
				})
			);
		});

		it('omits the query param when no search is given', () => {
			fetchCatalogFields({groupId: '23'});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					data: expect.not.objectContaining({
						query: expect.anything(),
					}),
				})
			);
		});
	});
});
