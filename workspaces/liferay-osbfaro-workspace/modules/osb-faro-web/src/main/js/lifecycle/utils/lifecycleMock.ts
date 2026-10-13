import {ILifecycleDetail, ILifecycleStagePayload} from 'shared/api/lifecycle';

const LIFECYCLE_NAME = 'Growth and Retention Hub';

const buildRule = (
	stageName: string,
	filterString: string,
	condition?: {
		conditionValue: string;
		field: string;
		fieldDataCategory: string;
		fieldDataType: string;
		operator: string;
	}
) => ({
	filterMetadata: JSON.stringify({
		conditions: condition ? [condition] : [],
		matchLogic: 'all',
	}),
	filterString,
	name: `${LIFECYCLE_NAME} Stage ${stageName} Criteria`,
});

export const MOCK_LIFECYCLE: ILifecycleDetail = {
	authorName: 'Anneta Smith',
	description: 'Lifecycle for marketing-qualified accounts',
	id: '4100',
	modifiedDate: 1791556330000,
	name: LIFECYCLE_NAME,
	processedDate: 1791460800000,
	segmentId: '900',
	stages: [
		{
			accountLifecycleStageRule: buildRule('Expansion', ''),
			description: 'Healthy accounts ready for add-ons',
			displayOrder: 4,
			id: '4104',
			maxDuration: null,
			name: 'Expansion',
		},
		{
			accountLifecycleStageRule: buildRule(
				'Pipeline',
				'(account.annualRevenue gt 5000000)',
				{
					conditionValue: '5000000',
					field: 'account.annualRevenue',
					fieldDataCategory: 'Number',
					fieldDataType: 'NUMERIC',
					operator: 'gt',
				}
			),
			description: 'Accounts with an active deal',
			displayOrder: 3,
			id: '4103',
			maxDuration: 60,
			name: 'Pipeline',
		},
		{
			accountLifecycleStageRule: buildRule(
				'Qualified',
				'(account.annualRevenue gt 1000000)',
				{
					conditionValue: '1000000',
					field: 'account.annualRevenue',
					fieldDataCategory: 'Number',
					fieldDataType: 'NUMERIC',
					operator: 'gt',
				}
			),
			description: 'Accounts that passed qualification',
			displayOrder: 2,
			id: '4102',
			maxDuration: null,
			name: 'Qualified',
		},
		{
			accountLifecycleStageRule: buildRule(
				'Aware',
				"(contains(account.industry, 'Software'))",
				{
					conditionValue: 'Software',
					field: 'account.industry',
					fieldDataCategory: 'Text',
					fieldDataType: 'STRING',
					operator: 'contains',
				}
			),
			description: 'Cold accounts showing early intent',
			displayOrder: 1,
			id: '4101',
			maxDuration: 90,
			name: 'Aware',
		},
	],
};

let mockLifecycle = MOCK_LIFECYCLE;

let stageCount = 0;

export const getMockLifecycle = (id: string): Promise<ILifecycleDetail> =>
	Promise.resolve({...mockLifecycle, id});

export const saveMockLifecycle = ({
	description,
	id = mockLifecycle.id,
	name,
	stages,
}: {
	description: string;
	id?: string;
	name: string;
	stages: ILifecycleStagePayload[];
}): Promise<ILifecycleDetail> => {
	mockLifecycle = {
		...mockLifecycle,
		description,
		id,
		modifiedDate: Date.now(),
		name,
		stages: stages.map((stage) => ({
			...stage,
			id: stage.id ?? `mock-stage-${++stageCount}`,
		})),
	};

	return Promise.resolve(mockLifecycle);
};

export const resetMockLifecycle = () => {
	mockLifecycle = MOCK_LIFECYCLE;
};
