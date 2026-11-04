jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {
	fetchAccountTopCategories,
	fetchIndividualTopCategories,
} from '../categories';

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

	describe('fetchAccountTopCategories with the last 24 hours', () => {
		it('sends rangeKey 0 instead of dropping it', () => {
			fetchAccountTopCategories({
				accountId: 'acc-1',
				channelId: '123',
				groupId: '456',
				rangeKey: 0,
				selectedMetric: 'viewsMetric',
			});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					data: expect.objectContaining({rangeKey: 0}),
				})
			);
		});
	});

	describe('fetchIndividualTopCategories with the last 24 hours', () => {
		it('sends rangeKey 0 instead of dropping it', () => {
			fetchIndividualTopCategories({
				channelId: '123',
				groupId: '456',
				individualId: 'ind-1',
				rangeKey: 0,
				selectedMetric: 'viewsMetric',
			});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					data: expect.objectContaining({rangeKey: 0}),
				})
			);
		});
	});

	describe('fetchAccountTopCategories with a custom range', () => {
		it('omits rangeKey when it is null', () => {
			fetchAccountTopCategories({
				accountId: 'acc-1',
				channelId: '123',
				groupId: '456',
				rangeEnd: '2026-08-10',
				rangeKey: null,
				rangeStart: '2026-08-01',
				selectedMetric: 'viewsMetric',
			});

			const {data} = sendRequest.mock.calls[0][0];

			expect(data).not.toHaveProperty('rangeKey');
			expect(data).toEqual(
				expect.objectContaining({
					rangeEnd: '2026-08-10',
					rangeStart: '2026-08-01',
				})
			);
		});
	});

	describe('fetchIndividualTopCategories with a custom range', () => {
		it('omits rangeKey when it is null', () => {
			fetchIndividualTopCategories({
				channelId: '123',
				groupId: '456',
				individualId: 'ind-1',
				rangeEnd: '2026-08-10',
				rangeKey: null,
				rangeStart: '2026-08-01',
				selectedMetric: 'viewsMetric',
			});

			const {data} = sendRequest.mock.calls[0][0];

			expect(data).not.toHaveProperty('rangeKey');
			expect(data).toEqual(
				expect.objectContaining({
					rangeEnd: '2026-08-10',
					rangeStart: '2026-08-01',
				})
			);
		});
	});
});
