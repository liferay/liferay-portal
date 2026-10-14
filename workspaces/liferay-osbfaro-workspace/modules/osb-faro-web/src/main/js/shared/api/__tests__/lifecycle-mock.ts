import {
	createMockLifecycleApi,
	MOCK_LIFECYCLES,
	resetMockDefaultLifecycle,
} from '../lifecycle-mock';
import {ILifecycleDetail} from '../lifecycle';

const STAGES = [1, 2, 3, 4, 5].map((displayOrder) => ({
	description: '',
	displayOrder,
	externalReferenceCode: `stage-${displayOrder}`,
	id: `stage-${displayOrder}`,
	maxDuration: null,
	stageType: 'AWARE',
}));

const LIFECYCLES: ILifecycleDetail[] = [
	{id: '2', name: 'Alpha', processedDate: 1},
	{authorName: 'Ada Lovelace', id: '3', modifiedDate: 1, name: 'Beta Growth'},
	{id: '1', name: 'growth hub', stages: STAGES},
];

const toPage = (items: ILifecycleDetail[]) => ({
	items,
	lastPage: 1,
	page: 1,
	pageSize: 20,
	totalCount: items.length,
});

const createApi = ({
	lifecycleList,
	lifecycles = LIFECYCLES,
}: {
	lifecycleList: () => Promise<unknown>;
	lifecycles?: ILifecycleDetail[];
}) => {
	const fetchLifecycle = jest.fn(({lifecycleId}: {lifecycleId: string}) => {
		const lifecycle = lifecycles.find(({id}) => id === lifecycleId);

		return lifecycle
			? Promise.resolve(lifecycle)
			: Promise.reject(new Error('Not found'));
	});

	const fetchLifecycleList = jest.fn(lifecycleList);

	const fetchLifecycles = jest.fn(() =>
		Promise.resolve(lifecycles.slice(0, 1))
	);

	const fetchLifecyclesPage = jest.fn(() =>
		Promise.resolve(toPage(lifecycles))
	);

	const setDefaultLifecycle = jest.fn(
		({lifecycleId}: {lifecycleId: string}) =>
			Promise.resolve({
				...lifecycles.find(({id}) => id === lifecycleId)!,
				defaultAccountLifecycle: true,
			})
	);

	return {
		api: createMockLifecycleApi({
			fetchLifecycle,
			fetchLifecycleList,
			fetchLifecycles,
			fetchLifecyclesPage,
			setDefaultLifecycle,
		}),
		fetchLifecycle,
		fetchLifecycleList,
		fetchLifecycles,
		fetchLifecyclesPage,
		setDefaultLifecycle,
	};
};

const createPagedApi = () =>
	createApi({
		lifecycleList: () => Promise.resolve(toPage(LIFECYCLES.slice(0, 1))),
	});

const createOldBackendApi = () =>
	createApi({lifecycleList: () => Promise.resolve([...LIFECYCLES])});

const createEmptyApi = () =>
	createApi({
		lifecycleList: () => Promise.resolve(toPage([])),
		lifecycles: [],
	});

const getDefaultIds = async (api: ReturnType<typeof createApi>['api']) =>
	(
		await api.fetchLifecyclesPage({
			groupId: '23',
			page: 1,
			pageSize: 30,
		})
	).items
		.filter(({defaultAccountLifecycle}) => defaultAccountLifecycle)
		.map(({id}) => id);

describe('createMockLifecycleApi', () => {
	afterEach(resetMockDefaultLifecycle);

	it('gives the first stage an OR trigger and the fourth an AND trigger', async () => {
		const {stages} = await createPagedApi().api.fetchLifecycle({
			groupId: '23',
			lifecycleId: '1',
		});

		const [first, second, , fourth] = stages!.map(
			({accountLifecycleStageRule}) =>
				accountLifecycleStageRule?.filterMetadata
					? JSON.parse(accountLifecycleStageRule.filterMetadata)
					: null
		);

		expect(first).toEqual(
			expect.objectContaining({
				conditions: expect.arrayContaining([
					expect.objectContaining({conditionValue: 'Inquiry'}),
				]),
				matchLogic: 'any',
			})
		);
		expect(first.conditions).toHaveLength(3);
		expect(second).toBeNull();
		expect(fourth.matchLogic).toBe('all');
		expect(fourth.conditions).toHaveLength(2);
	});

	describe('when the backend is paged', () => {
		it('lists, pages and loads the lifecycles through the endpoints', async () => {
			const {
				api,
				fetchLifecycle,
				fetchLifecycleList,
				fetchLifecycles,
				fetchLifecyclesPage,
			} = createPagedApi();

			const params = {
				channelId: '123',
				groupId: '23',
				page: 2,
				pageSize: 10,
				search: 'smith',
			};

			await api.fetchLifecyclesPage(params);
			await api.fetchLifecycles({
				channelId: '123',
				defaultAccountLifecycle: true,
				groupId: '23',
			});
			await api.fetchLifecycle({groupId: '23', lifecycleId: '3'});

			expect(fetchLifecycleList).toHaveBeenCalledWith({
				channelId: '123',
				groupId: '23',
			});
			expect(fetchLifecyclesPage).toHaveBeenCalledWith(params);
			expect(fetchLifecycles).toHaveBeenCalledWith({
				channelId: '123',
				defaultAccountLifecycle: true,
				groupId: '23',
			});
			expect(fetchLifecycle).toHaveBeenCalledWith({
				groupId: '23',
				lifecycleId: '3',
			});
		});

		it('keeps the values the backend returns', async () => {
			expect(
				await createPagedApi().api.fetchLifecycle({
					groupId: '23',
					lifecycleId: '2',
				})
			).not.toHaveProperty('authorName');
		});

		it('sets the default through the endpoint', async () => {
			const {api, setDefaultLifecycle} = createPagedApi();

			expect(
				await api.setDefaultLifecycle({groupId: '23', lifecycleId: '3'})
			).toEqual(
				expect.objectContaining({
					defaultAccountLifecycle: true,
					id: '3',
				})
			);
			expect(setDefaultLifecycle).toHaveBeenCalledWith({
				groupId: '23',
				lifecycleId: '3',
			});
		});

		it('does not resolve its own lifecycles', async () => {
			await expect(
				createPagedApi().api.fetchLifecycle({
					groupId: '23',
					lifecycleId: MOCK_LIFECYCLES[0].id,
				})
			).rejects.toThrow();
		});
	});

	describe('when the backend returns the old plain array', () => {
		it('flags the first lifecycle as the default', async () => {
			const {api} = createOldBackendApi();

			expect(await getDefaultIds(api)).toEqual(['2']);
			expect(
				await api.fetchLifecycles({
					defaultAccountLifecycle: true,
					groupId: '23',
				})
			).toEqual([
				expect.objectContaining({
					defaultAccountLifecycle: true,
					id: '2',
					processedDate: 1,
				}),
			]);
		});

		it('fills in a fixed author and date, keeping the API values', async () => {
			const {
				items: [alpha, beta],
			} = await createOldBackendApi().api.fetchLifecyclesPage({
				groupId: '23',
				page: 1,
				pageSize: 20,
			});

			expect(alpha).toEqual(
				expect.objectContaining({
					authorName: expect.any(String),
					modifiedDate: expect.any(Number),
				})
			);
			expect(beta).toEqual(
				expect.objectContaining({
					authorName: 'Ada Lovelace',
					modifiedDate: 1,
				})
			);
		});

		it('pages, sorts and searches by name or author itself', async () => {
			const {api, fetchLifecyclesPage} = createOldBackendApi();

			expect(
				await api.fetchLifecyclesPage({
					groupId: '23',
					page: 2,
					pageSize: 2,
				})
			).toEqual(
				expect.objectContaining({
					items: [expect.objectContaining({id: '1'})],
					lastPage: 2,
					totalCount: 3,
				})
			);

			const search = async (text: string) =>
				(
					await api.fetchLifecyclesPage({
						groupId: '23',
						page: 1,
						pageSize: 20,
						search: text,
					})
				).items.map(({id}) => id);

			expect(await search(' GROWTH ')).toEqual(['3', '1']);
			expect(await search('lovelace')).toEqual(['3']);
			expect(fetchLifecyclesPage).not.toHaveBeenCalled();
		});

		it('moves the default and returns the patched lifecycle', async () => {
			const {api, setDefaultLifecycle} = createOldBackendApi();

			expect(
				await api.setDefaultLifecycle({groupId: '23', lifecycleId: '1'})
			).toEqual(
				expect.objectContaining({
					defaultAccountLifecycle: true,
					id: '1',
				})
			);
			expect(await getDefaultIds(api)).toEqual(['1']);
			expect(
				await api.fetchLifecycles({
					defaultAccountLifecycle: true,
					groupId: '23',
				})
			).toEqual([
				expect.objectContaining({
					defaultAccountLifecycle: true,
					id: '1',
				}),
			]);
			expect(
				await api.fetchLifecycle({groupId: '23', lifecycleId: '2'})
			).toEqual(
				expect.objectContaining({defaultAccountLifecycle: false})
			);
			expect(setDefaultLifecycle).not.toHaveBeenCalled();
		});
	});

	describe('without real lifecycles', () => {
		it('lists its own lifecycles, sorted by name and paged', async () => {
			const {items, lastPage, totalCount} =
				await createEmptyApi().api.fetchLifecyclesPage({
					groupId: '23',
					page: 1,
					pageSize: 20,
				});

			const names = items.map(({name}) => name!);

			expect(items).toHaveLength(20);
			expect(lastPage).toBeGreaterThan(1);
			expect(totalCount).toBe(MOCK_LIFECYCLES.length);
			expect(names).toEqual(
				[...names].sort((a, b) =>
					a.localeCompare(b, undefined, {sensitivity: 'base'})
				)
			);
		});

		it('searches its own lifecycles', async () => {
			const {items} = await createEmptyApi().api.fetchLifecyclesPage({
				groupId: '23',
				page: 1,
				pageSize: 20,
				search: ' ACCOUNTS ',
			});

			expect(items.length).toBeGreaterThan(1);
			expect(items.every(({name}) => /accounts/i.test(name!))).toBe(true);
		});

		it('returns an empty first page without matches', async () => {
			expect(
				await createEmptyApi().api.fetchLifecyclesPage({
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

		it('moves the default to the lifecycle set as default', async () => {
			const {api} = createEmptyApi();

			const lifecycleId = MOCK_LIFECYCLES[5].id;

			expect(
				await api.setDefaultLifecycle({groupId: '23', lifecycleId})
			).toEqual(
				expect.objectContaining({
					defaultAccountLifecycle: true,
					id: lifecycleId,
				})
			);
			expect(
				await api.fetchLifecycles({
					defaultAccountLifecycle: true,
					groupId: '23',
				})
			).toEqual([
				expect.objectContaining({
					defaultAccountLifecycle: true,
					id: lifecycleId,
				}),
			]);
		});

		it('resolves its own lifecycles by ID without the endpoint', async () => {
			const {api, fetchLifecycle} = createEmptyApi();

			const [mockLifecycle] = MOCK_LIFECYCLES;

			expect(
				await api.fetchLifecycle({
					groupId: '23',
					lifecycleId: mockLifecycle.id,
				})
			).toEqual(
				expect.objectContaining({
					id: mockLifecycle.id,
					name: mockLifecycle.name,
					stages: expect.arrayContaining([
						expect.objectContaining({stageType: 'AWARE'}),
					]),
				})
			);
			expect(fetchLifecycle).not.toHaveBeenCalled();
		});
	});

	describe.each([
		['returns an empty plain array', () => Promise.resolve([])],
		['returns something else', () => Promise.resolve(undefined)],
		['fails', () => Promise.reject(new Error('Server error'))],
	])('when the lifecycle list %s', (_name, lifecycleList) => {
		const createFailingApi = () =>
			createApi({lifecycleList, lifecycles: []}).api;

		it('returns its own default lifecycle', async () => {
			const [lifecycle] = await createFailingApi().fetchLifecycles({
				defaultAccountLifecycle: true,
				groupId: '23',
			});

			expect(MOCK_LIFECYCLES.map(({id}) => id)).toContain(lifecycle.id);
			expect(lifecycle.defaultAccountLifecycle).toBe(true);
		});

		it('pages its own lifecycles', async () => {
			expect(
				await createFailingApi().fetchLifecyclesPage({
					groupId: '23',
					page: 1,
					pageSize: 20,
				})
			).toEqual(
				expect.objectContaining({totalCount: MOCK_LIFECYCLES.length})
			);
		});

		it('resolves its own lifecycles by ID', async () => {
			expect(
				await createFailingApi().fetchLifecycle({
					groupId: '23',
					lifecycleId: MOCK_LIFECYCLES[0].id,
				})
			).toEqual(expect.objectContaining({id: MOCK_LIFECYCLES[0].id}));
		});
	});
});
