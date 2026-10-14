import {
	createMockLifecycleApi,
	resetMockDefaultLifecycle,
} from '../lifecycle-mock';
import {getDefaultLifecycle, ILifecycleDetail} from '../lifecycle';

const STAGES = [1, 2, 3, 4, 5].map((displayOrder) => ({
	description: '',
	displayOrder,
	id: `stage-${displayOrder}`,
	maxDuration: null,
	stageType: 'AWARE',
}));

const LIFECYCLES: ILifecycleDetail[] = [
	{id: '1', name: 'growth hub', stages: STAGES},
	{id: '2', name: 'Alpha'},
	{authorName: 'Ada Lovelace', id: '3', modifiedDate: 1, name: 'Beta Growth'},
];

const createApi = () => {
	const fetchLifecycle = jest.fn(({lifecycleId}: {lifecycleId: string}) =>
		Promise.resolve(LIFECYCLES.find(({id}) => id === lifecycleId)!)
	);

	const fetchLifecycles = jest.fn(() => Promise.resolve(LIFECYCLES));

	return {
		api: createMockLifecycleApi({
			fetchLifecycle,
			fetchLifecycles,
			getDefaultLifecycle,
		}),
		fetchLifecycle,
		fetchLifecycles,
	};
};

const getDefaultIds = async () =>
	(await createApi().api.fetchLifecycles({groupId: '23'}))
		.filter((lifecycle) => lifecycle.default)
		.map(({id}) => id);

describe('createMockLifecycleApi', () => {
	afterEach(resetMockDefaultLifecycle);

	it('flags the default lifecycle through the shared rule', async () => {
		expect(await getDefaultIds()).toEqual(['1']);
	});

	it('moves the default to the lifecycle set as default', async () => {
		await createApi().api.setDefaultLifecycle({lifecycleId: '3'});

		expect(await getDefaultIds()).toEqual(['3']);
	});

	it('fills in a fixed author and date, keeping the API values', async () => {
		const [growth, , beta] = await createApi().api.fetchLifecycles({
			groupId: '23',
		});

		expect(growth).toEqual(
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

	it('gives the first stage an OR trigger and the fourth an AND trigger', async () => {
		const {stages} = await createApi().api.fetchLifecycle({
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

	it('requests the lifecycle and the list at the same time', () => {
		const {api, fetchLifecycle, fetchLifecycles} = createApi();

		api.fetchLifecycle({groupId: '23', lifecycleId: '1'});

		expect(fetchLifecycle).toHaveBeenCalled();
		expect(fetchLifecycles).toHaveBeenCalled();
	});
});
