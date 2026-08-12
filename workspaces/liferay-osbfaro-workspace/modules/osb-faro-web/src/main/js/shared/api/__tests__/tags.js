jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {fetchIndividualTopTags} from '../tags';

describe('Tags API', () => {
	beforeEach(() => {
		sendRequest.mockClear();
	});

	describe('fetchIndividualTopTags', () => {
		it('scopes the top tags to the given individual', () => {
			fetchIndividualTopTags({
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
				path: 'contacts/456/asset-summary-tags',
			});
		});

		it('sends the custom range instead of the range key', () => {
			fetchIndividualTopTags({
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
