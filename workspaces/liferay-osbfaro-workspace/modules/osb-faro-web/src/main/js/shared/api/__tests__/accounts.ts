jest.mock('shared/util/request');

import sendRequest from 'shared/util/request';
import {fetchOverviewMetrics} from '../accounts';

describe('Accounts API', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockClear();
	});

	describe('fetchOverviewMetrics', () => {
		it('reads the metrics from the account overview of the workspace', () => {
			fetchOverviewMetrics({channelId: '123', groupId: '456'});

			expect(sendRequest).toHaveBeenCalledWith({
				data: {channelId: '123'},
				method: 'GET',
				path: 'contacts/456/account/overview',
			});
		});

		it('scopes the metrics to the given channel', () => {
			fetchOverviewMetrics({channelId: '789', groupId: '456'});

			expect(sendRequest).toHaveBeenCalledWith(
				expect.objectContaining({
					data: {channelId: '789'},
				})
			);
		});
	});
});
