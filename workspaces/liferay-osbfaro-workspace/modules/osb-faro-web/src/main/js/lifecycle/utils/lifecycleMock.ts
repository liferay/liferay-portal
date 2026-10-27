import {ILifecycleDetail, ILifecycleStagePayload} from 'shared/api/lifecycle';
import {buildStageRuleName} from 'lifecycle/utils/lifecyclePayload';
import {ILifecycleStage as ILifecycleStageMetric} from 'lifecycle/utils/types';
import {sortBy} from 'lodash';

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
	name: buildStageRuleName(LIFECYCLE_NAME, stageName),
});

const MOCK_LIFECYCLE: ILifecycleDetail = {
	authorName: 'Anneta Smith',
	description: 'Lifecycle for marketing-qualified accounts',
	externalReferenceCode: 'mock-lifecycle-erc',
	id: '4100',
	modifiedDate: 1791556330000,
	name: LIFECYCLE_NAME,
	processedDate: 1791460800000,
	segmentId: '900',
	stages: [
		{
			accountLifecycleStageRule: buildRule('Expansion', ''),
			description: 'Healthy accounts ready for add-ons',
			externalReferenceCode: 'mock-stage-erc-4104',
			id: '4104',
			maxDuration: null,
			priority: 4,
			stageName: 'Expansion',
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
			externalReferenceCode: 'mock-stage-erc-4103',
			id: '4103',
			maxDuration: 60,
			priority: 3,
			stageName: 'Pipeline',
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
			externalReferenceCode: 'mock-stage-erc-4102',
			id: '4102',
			maxDuration: null,
			priority: 2,
			stageName: 'Qualified',
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
			externalReferenceCode: 'mock-stage-erc-4101',
			id: '4101',
			maxDuration: 90,
			priority: 1,
			stageName: 'Aware',
		},
	],
};

let mockLifecycle = MOCK_LIFECYCLE;

let stageCount = 0;

export const getMockLifecycle = (): Promise<ILifecycleDetail> =>
	Promise.resolve(mockLifecycle);

export const saveMockLifecycle = ({
	description,
	name,
	stages,
}: {
	description: string;
	name: string;
	stages: ILifecycleStagePayload[];
}): Promise<ILifecycleDetail> => {
	mockLifecycle = {
		...mockLifecycle,
		description,
		id: String(Number(mockLifecycle.id) + 1),
		modifiedDate: Date.now(),
		name,
		stages: stages.map((stage) => {
			const stageId = stage.id ?? `mock-stage-${++stageCount}`;

			return {
				...stage,
				externalReferenceCode: `mock-stage-erc-${stageId}`,
				id: stageId,
			};
		}),
	};

	return Promise.resolve(mockLifecycle);
};

const MOCK_ACCOUNT_COUNTS = [120, 64, 41, 22, 9];

const MOCK_AVERAGE_DURATIONS = [9.8, 4.6, 4.5, 12, 0];

export const getMockLifecycleStages = (): Promise<ILifecycleStageMetric[]> => {
	const stages = sortBy(mockLifecycle.stages ?? [], 'priority');

	const counts = stages.map(
		(stage, index) => MOCK_ACCOUNT_COUNTS[index] ?? 5
	);

	const total = counts.reduce((sum, count) => sum + count, 0);

	return Promise.resolve(
		stages.map(({description, id, priority, stageName}, index) => {
			const nextCount = counts[index + 1];

			return {
				accountsCount: counts[index],
				averageStageDuration: MOCK_AVERAGE_DURATIONS[index] ?? 0,
				conversionRateToNextStage:
					nextCount === undefined
						? null
						: (nextCount / counts[index]) * 100,
				description,
				id,
				percentage: total ? (counts[index] / total) * 100 : 0,
				priority,
				stageName,
			};
		})
	);
};

export const getMockLifecycleStageFieldValues = () =>
	Promise.resolve({
		items: (mockLifecycle.stages ?? []).map(
			({externalReferenceCode, id, stageName}) => ({
				externalReferenceCode,
				id,
				stageName,
			})
		),
	});

export const resetMockLifecycle = () => {
	mockLifecycle = MOCK_LIFECYCLE;
};
