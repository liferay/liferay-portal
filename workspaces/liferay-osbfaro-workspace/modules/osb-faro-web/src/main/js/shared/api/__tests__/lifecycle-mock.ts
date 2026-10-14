import {
	createMockLifecycleApi,
	resetMockDefaultLifecycle,
} from '../lifecycle-mock';
import {getDefaultLifecycle, ILifecycleDetail} from '../lifecycle';

const LIFECYCLES: ILifecycleDetail[] = [
	{id: '1', name: 'growth hub'},
	{id: '2', name: 'Alpha'},
	{id: '3', name: 'Beta Growth'},
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

	it('requests the lifecycle and the list at the same time', () => {
		const {api, fetchLifecycle, fetchLifecycles} = createApi();

		api.fetchLifecycle({groupId: '23', lifecycleId: '1'});

		expect(fetchLifecycle).toHaveBeenCalled();
		expect(fetchLifecycles).toHaveBeenCalled();
	});
});
