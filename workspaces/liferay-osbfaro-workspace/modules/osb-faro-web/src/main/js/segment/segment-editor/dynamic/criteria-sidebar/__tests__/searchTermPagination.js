import client from 'shared/apollo/client';
import {searchTermPagination} from '../searchTermPagination';

jest.mock('shared/apollo/client', () => ({
	__esModule: true,
	default: {query: jest.fn()},
}));

describe('searchTermPagination', () => {
	afterEach(() => {
		client.query.mockReset();
	});

	describe('api', () => {
		it('should page and pass keywords through to the query', async () => {
			client.query.mockResolvedValueOnce({
				data: {
					searchTerms: {
						compositions: [{count: 5, name: 'shoes'}],
						totalCount: 42,
					},
				},
			});

			const result = await searchTermPagination.api({
				channelId: '123',
				groupId: '456',
				keywords: 'sho',
				page: 3,
				pageSize: 12,
			});

			expect(client.query).toHaveBeenCalledWith(
				expect.objectContaining({
					variables: expect.objectContaining({
						channelId: '123',
						keywords: 'sho',
						size: 12,
						start: 24,
					}),
				})
			);

			expect(result).toEqual({
				items: [{id: 'shoes', name: 'shoes'}],
				totalCount: 42,
			});
		});

		it('should send undefined keywords rather than an empty string', async () => {
			client.query.mockResolvedValueOnce({
				data: {searchTerms: {compositions: [], totalCount: 0}},
			});

			await searchTermPagination.api({
				channelId: '123',
				groupId: '456',
				keywords: '',
				page: 1,
				pageSize: 12,
			});

			expect(client.query).toHaveBeenCalledWith(
				expect.objectContaining({
					variables: expect.objectContaining({keywords: undefined}),
				})
			);
		});

		it('should resolve to an empty page when the request fails', async () => {
			client.query.mockRejectedValueOnce(new Error('Unknown argument'));

			const result = await searchTermPagination.api({
				channelId: '123',
				groupId: '456',
				page: 1,
				pageSize: 12,
			});

			expect(result).toEqual({items: [], totalCount: 0});
		});
	});

	describe('createProperty', () => {
		it('should build a search term Property from the item name', () => {
			const property = searchTermPagination.createProperty({
				id: 'shoes',
				name: 'shoes',
			});

			expect(property.name).toBe('shoes');
			expect(property.label).toBe('shoes');
			expect(property.propertyKey).toBe('search-term');
			expect(property.type).toBe('search-term');
		});
	});
});
