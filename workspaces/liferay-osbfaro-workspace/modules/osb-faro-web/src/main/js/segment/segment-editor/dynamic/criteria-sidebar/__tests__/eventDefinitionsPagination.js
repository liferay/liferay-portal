import client from 'shared/apollo/client';
import {eventDefinitionsPagination} from '../eventDefinitionsPagination';
import {EventTypes} from 'event-analysis/utils/types';

jest.mock('shared/apollo/client', () => ({
	__esModule: true,
	default: {query: jest.fn()},
}));

describe('eventDefinitionsPagination', () => {
	afterEach(() => {
		client.query.mockReset();
	});

	describe('api', () => {
		it('should request the custom events for the given page', async () => {
			client.query.mockResolvedValueOnce({
				data: {
					eventDefinitions: {
						eventDefinitions: [
							{displayName: 'Signed Up', name: 'signedUp'},
						],
						total: 42,
					},
				},
			});

			const result = await eventDefinitionsPagination.api({
				channelId: '123',
				groupId: '456',
				keywords: 'sig',
				page: 3,
				pageSize: 10,
			});

			expect(client.query).toHaveBeenCalledWith(
				expect.objectContaining({
					variables: expect.objectContaining({
						eventType: EventTypes.Custom,
						hidden: false,
						keyword: 'sig',
						page: 2,
						size: 10,
					}),
				})
			);

			expect(result.totalCount).toBe(42);
			expect(result.items).toEqual([
				{displayName: 'Signed Up', name: 'signedUp'},
			]);
		});

		it('should resolve to an empty page when the response carries no data', async () => {
			client.query.mockResolvedValueOnce({data: undefined});

			const result = await eventDefinitionsPagination.api({
				channelId: '123',
				groupId: '456',
				page: 1,
				pageSize: 10,
			});

			expect(result).toEqual({items: [], totalCount: 0});
		});
	});

	describe('createProperty', () => {
		it('should build an event Property from the event definition', () => {
			const property = eventDefinitionsPagination.createProperty({
				displayName: 'Signed Up',
				name: 'signedUp',
			});

			expect(property.name).toBe('signedUp');
			expect(property.label).toBe('Signed Up');
			expect(property.propertyKey).toBe('event');
		});
	});
});
