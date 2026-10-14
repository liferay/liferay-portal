import type {ILifecycle, ILifecycleDetail, ILifecycleStage} from './lifecycle';

const MOCK_AUTHOR_NAME = 'Anneta Smith';

const MOCK_MODIFIED_DATE = Date.UTC(2026, 9, 2, 17, 53);

const MOCK_FILTER_METADATA_BY_POSITION: {[position: number]: string} = {
	0: JSON.stringify({
		conditions: [
			'Inquiry',
			'Known Prospect',
			'Marketing Qualified Lead (MQL)',
		].map((conditionValue) => ({
			conditionValue,
			field: 'salesforce/lead_Funnel_Stage__c',
			fieldDataCategory: 'Text',
			fieldDataType: 'STRING',
			operator: 'is',
		})),
		matchLogic: 'any',
	}),
	3: JSON.stringify({
		conditions: [
			{
				conditionValue: '1',
				field: 'salesforce/closedWonOpportunityCount',
				fieldDataCategory: 'Number',
				fieldDataType: 'INTEGER',
				operator: 'eq',
			},
			{
				conditionValue: 'its',
				field: 'accountName',
				fieldDataCategory: 'Text',
				fieldDataType: 'STRING',
				operator: 'contains',
			},
		],
		matchLogic: 'all',
	}),
};

let mockDefaultLifecycleId: string | null = null;

export const resetMockDefaultLifecycle = () => {
	mockDefaultLifecycleId = null;
};

const withMockStageTriggers = (stages?: ILifecycleStage[]) => {
	if (!stages) {
		return stages;
	}

	const positions = [...stages]
		.sort((a, b) => a.displayOrder - b.displayOrder)
		.map(({id}) => id);

	return stages.map((stage) => {
		const filterMetadata =
			MOCK_FILTER_METADATA_BY_POSITION[positions.indexOf(stage.id)];

		return filterMetadata
			? {
					...stage,
					accountLifecycleStageRule: {
						filterString: '',
						...stage.accountLifecycleStageRule,
						filterMetadata,
					},
				}
			: stage;
	});
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
			authorName: MOCK_AUTHOR_NAME,
			modifiedDate: MOCK_MODIFIED_DATE,
			...lifecycle,
			default: lifecycle.id === defaultLifecycleId,
			stages: withMockStageTriggers(lifecycle.stages),
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
				stages: withMockStageTriggers(lifecycle.stages),
			};
		},
		fetchLifecycles: fetchMockLifecycles,
		setDefaultLifecycle: async ({lifecycleId}: {lifecycleId: string}) => {
			mockDefaultLifecycleId = lifecycleId;
		},
	};
};
