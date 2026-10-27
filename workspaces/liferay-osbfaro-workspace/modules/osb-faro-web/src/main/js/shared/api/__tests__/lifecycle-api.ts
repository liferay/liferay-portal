jest.mock('shared/util/request');

jest.mock('shared/util/feature-flags', () => ({
	ENABLE_LIFECYCLE_STAGES_MOCK_DATA: false,
}));

import sendRequest from 'shared/util/request';
import {
	fetchAccountLifecycles,
	fetchLifecycles,
	fetchLifecyclesPage,
	setDefaultLifecycle,
} from '../lifecycle';

describe('fetchLifecyclesPage', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockClear();
	});

	it('requests a page of lifecycles sorted by name', () => {
		fetchLifecyclesPage({groupId: '23', page: 2, pageSize: 20});

		expect(sendRequest).toHaveBeenCalledWith({
			data: {page: 2, pageSize: 20, sort: 'name:asc'},
			method: 'GET',
			path: 'contacts/23/account-lifecycle',
		});
	});

	it('narrows the page to a channel and a trimmed search', () => {
		fetchLifecyclesPage({
			channelId: '123',
			groupId: '23',
			page: 1,
			pageSize: 20,
			search: '  growth ',
		});

		expect(sendRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				data: {
					channelId: '123',
					page: 1,
					pageSize: 20,
					search: 'growth',
					sort: 'name:asc',
				},
			})
		);
	});

	it('leaves a blank search out', () => {
		fetchLifecyclesPage({
			groupId: '23',
			page: 1,
			pageSize: 20,
			search: '   ',
		});

		expect(
			(sendRequest as jest.Mock).mock.calls[0][0].data
		).not.toHaveProperty('search');
	});
});

describe.each([
	['fetchLifecycles', fetchLifecycles],
	['fetchAccountLifecycles', fetchAccountLifecycles],
])('%s', (_name, fetchFirstLifecycles) => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockReset();
		(sendRequest as jest.Mock).mockResolvedValue({
			items: [{id: '1'}],
			lastPage: 3,
			page: 1,
			pageSize: 1,
			totalCount: 3,
		});
	});

	it('requests the first lifecycle by name and returns the items', async () => {
		expect(await fetchFirstLifecycles({groupId: '23'})).toEqual([
			{id: '1'},
		]);

		expect(sendRequest).toHaveBeenCalledWith({
			data: {page: 1, pageSize: 1, sort: 'name:asc'},
			method: 'GET',
			path: 'contacts/23/account-lifecycle',
		});
	});
});

describe('fetchLifecycles in a channel', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockReset();
		(sendRequest as jest.Mock).mockResolvedValue({items: []});
	});

	it('narrows the first lifecycle to the channel', async () => {
		expect(
			await fetchLifecycles({channelId: '123', groupId: '23'})
		).toEqual([]);

		expect((sendRequest as jest.Mock).mock.calls[0][0].data).toEqual({
			channelId: '123',
			page: 1,
			pageSize: 1,
			sort: 'name:asc',
		});
	});
});

describe('the default lifecycle of a channel', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockReset();
		(sendRequest as jest.Mock).mockResolvedValue({
			items: [{defaultAccountLifecycle: true, id: '2'}],
		});
	});

	it('filters the lifecycles by the default flag', async () => {
		expect(
			await fetchLifecycles({
				channelId: '123',
				defaultAccountLifecycle: true,
				groupId: '23',
			})
		).toEqual([{defaultAccountLifecycle: true, id: '2'}]);

		expect((sendRequest as jest.Mock).mock.calls[0][0].data).toEqual({
			channelId: '123',
			defaultAccountLifecycle: true,
			page: 1,
			pageSize: 1,
			sort: 'name:asc',
		});
	});

	it('is what an account reads when it knows its channel', async () => {
		await fetchAccountLifecycles({channelId: '123', groupId: '23'});

		expect((sendRequest as jest.Mock).mock.calls[0][0].data).toEqual(
			expect.objectContaining({
				channelId: '123',
				defaultAccountLifecycle: true,
			})
		);
	});
});

describe('setDefaultLifecycle', () => {
	beforeEach(() => {
		(sendRequest as jest.Mock).mockReset();
		(sendRequest as jest.Mock).mockResolvedValue({
			defaultAccountLifecycle: true,
			id: '5',
		});
	});

	it('patches the lifecycle as the default and returns it', async () => {
		expect(
			await setDefaultLifecycle({groupId: '23', lifecycleId: '5'})
		).toEqual({defaultAccountLifecycle: true, id: '5'});

		expect(sendRequest).toHaveBeenCalledWith({
			data: {defaultAccountLifecycle: true},
			method: 'PATCH',
			path: 'contacts/23/account-lifecycle/5',
		});
	});
});
