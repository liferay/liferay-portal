jest.mock('shared/util/request');

jest.mock('shared/util/feature-flags', () => ({
	ENABLE_LIFECYCLE_DASHBOARDS_API: false,
}));

import sendRequest from 'shared/util/request';
import {
	fetchAccountLifecycles,
	fetchLifecycle,
	fetchLifecycles,
	fetchLifecyclesPage,
} from '../lifecycle';

const LIFECYCLES = [
	{id: '1', name: 'Alpha'},
	{id: '2', name: 'Beta Growth'},
	{id: '3', name: 'growth hub'},
];

const mockedSendRequest = sendRequest as jest.Mock;

describe('the lifecycle API in the legacy format', () => {
	beforeEach(() => {
		mockedSendRequest.mockReset();
		mockedSendRequest.mockResolvedValue(LIFECYCLES);
	});

	it('lists every lifecycle of the channel without paging parameters', async () => {
		expect(
			await fetchLifecycles({
				channelId: '123',
				defaultAccountLifecycle: true,
				groupId: '23',
			})
		).toEqual(LIFECYCLES);

		expect(mockedSendRequest).toHaveBeenCalledWith({
			data: {channelId: '123'},
			method: 'GET',
			path: 'contacts/23/account-lifecycle',
		});
	});

	it('lists every lifecycle when no channel is given', async () => {
		await fetchLifecycles({groupId: '23'});

		expect(mockedSendRequest).toHaveBeenCalledWith({
			data: {},
			method: 'GET',
			path: 'contacts/23/account-lifecycle',
		});
	});

	it('gives an account the plain list of lifecycles', async () => {
		expect(
			await fetchAccountLifecycles({channelId: '123', groupId: '23'})
		).toEqual(LIFECYCLES);

		expect(mockedSendRequest.mock.calls[0][0].data).toEqual({
			channelId: '123',
		});
	});

	it('pages the lifecycles on the client', async () => {
		expect(
			await fetchLifecyclesPage({
				channelId: '123',
				groupId: '23',
				page: 2,
				pageSize: 2,
			})
		).toEqual({
			items: [{id: '3', name: 'growth hub'}],
			lastPage: 2,
			page: 2,
			pageSize: 2,
			totalCount: 3,
		});

		expect(mockedSendRequest).toHaveBeenCalledWith({
			data: {channelId: '123'},
			method: 'GET',
			path: 'contacts/23/account-lifecycle',
		});
	});

	it('searches the lifecycles by name on the client', async () => {
		const {items, totalCount} = await fetchLifecyclesPage({
			groupId: '23',
			page: 1,
			pageSize: 20,
			search: ' GROWTH ',
		});

		expect(items.map(({id}) => id)).toEqual(['2', '3']);
		expect(totalCount).toBe(2);
	});

	it('returns an empty first page without matches', async () => {
		expect(
			await fetchLifecyclesPage({
				groupId: '23',
				page: 1,
				pageSize: 20,
				search: 'none',
			})
		).toEqual({
			items: [],
			lastPage: 1,
			page: 1,
			pageSize: 20,
			totalCount: 0,
		});
	});

	it('loads one lifecycle from the same endpoint as the new format', async () => {
		mockedSendRequest.mockResolvedValue(LIFECYCLES[0]);

		expect(await fetchLifecycle({groupId: '23', lifecycleId: '1'})).toEqual(
			LIFECYCLES[0]
		);

		expect(mockedSendRequest).toHaveBeenCalledWith({
			method: 'GET',
			path: 'contacts/23/account-lifecycle/1',
		});
	});
});
