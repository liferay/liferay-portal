jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {AssetObjectTypes} from 'shared/util/constants';
import {fetchIndividualTopAssets} from '../assets';

describe('Assets API', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockClear();
	});

	describe('fetchIndividualTopAssets', () => {
		it('scopes the asset summary to the given individual', () => {
			fetchIndividualTopAssets({
				channelId: '123',
				groupId: '456',
				individualId: 'ind-1',
				objectType: AssetObjectTypes.Content,
				rangeKey: 30,
				selectedMetric: 'impressionsMetric',
			});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {
					channelId: '123',
					filter: "individualIds in ('ind-1')",
					objectType: 'content',
					pageSize: 5,
					rangeKey: 30,
					selectedMetric: 'impressionsMetric',
					sort: 'impressionsMetric,desc',
				},
				method: 'GET',
				path: 'contacts/456/asset-summary',
			});
		});

		it('sends the custom range instead of the range key', () => {
			fetchIndividualTopAssets({
				channelId: '123',
				groupId: '456',
				individualId: 'ind-1',
				rangeEnd: '2026-08-10',
				rangeKey: 30,
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
