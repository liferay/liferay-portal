import type {ILifecycle, ILifecycleDetail} from './lifecycle';

let mockDefaultLifecycleId: string | null = null;

export const resetMockDefaultLifecycle = () => {
	mockDefaultLifecycleId = null;
};

interface ILifecycleRequests {
	fetchLifecycle: (params: {
		groupId: string;
		lifecycleId: string;
	}) => Promise<ILifecycleDetail>;
	fetchLifecycles: (params: {groupId: string}) => Promise<ILifecycle[]>;
	getDefaultLifecycle: (lifecycles: ILifecycle[]) => ILifecycle | undefined;
}

export const createMockLifecycleApi = ({
	fetchLifecycle,
	fetchLifecycles,
	getDefaultLifecycle,
}: ILifecycleRequests) => {
	const withMockFields = (
		lifecycles: ILifecycleDetail[]
	): ILifecycleDetail[] => {
		const defaultLifecycleId = getDefaultLifecycle(
			lifecycles.map((lifecycle) => ({
				...lifecycle,
				default: lifecycle.id === mockDefaultLifecycleId,
			}))
		)?.id;

		return lifecycles.map((lifecycle) => ({
			...lifecycle,
			default: lifecycle.id === defaultLifecycleId,
		}));
	};

	const fetchMockLifecycles = async (params: {groupId: string}) =>
		withMockFields(await fetchLifecycles(params));

	return {
		fetchLifecycle: async (params: {
			groupId: string;
			lifecycleId: string;
		}): Promise<ILifecycleDetail> => {
			const [lifecycle, lifecycles] = await Promise.all([
				fetchLifecycle(params),
				fetchMockLifecycles(params),
			]);

			return {
				...lifecycles.find(({id}) => id === lifecycle.id),
				...lifecycle,
			};
		},
		fetchLifecycles: fetchMockLifecycles,
		setDefaultLifecycle: async ({lifecycleId}: {lifecycleId: string}) => {
			mockDefaultLifecycleId = lifecycleId;
		},
	};
};
