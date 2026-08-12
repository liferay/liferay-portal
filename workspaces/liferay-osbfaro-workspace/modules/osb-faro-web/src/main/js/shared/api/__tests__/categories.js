jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {fetchIndividualTopCategories} from '../categories';

describe('Categories API', () => {
	beforeEach(() => {
		sendRequest.mockClear();
	});

	describe('fetchIndividualTopCategories', () => {
		it('scopes the top categories to the given individual', () => {
			fetchIndividualTopCategories({
				channelId: '123',
				groupId: '456',
				individualId: 'ind-1',
				rangeKey: 30,
				selectedMetric: 'impressionsMetric',
			});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					channelId: '123',
					individualId: 'ind-1',
					pageSize: 5,
					rangeKey: 30,
					selectedMetric: 'impressionsMetric',
					sort: 'impressionsMetric,desc',
				},
				method: 'GET',
				path: 'contacts/456/asset-summary-categories',
			});
		});

		it('sends the custom range instead of the range key', () => {
			fetchIndividualTopCategories({
				channelId: '123',
				groupId: '456',
				individualId: 'ind-1',
				rangeEnd: '2026-08-10',
				rangeStart: '2026-08-01',
				selectedMetric: 'viewsMetric',
			});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					data: expect.objectContaining({
						rangeEnd: '2026-08-10',
						rangeStart: '2026-08-01',
					}),
				})
			);
		});
	});
});
