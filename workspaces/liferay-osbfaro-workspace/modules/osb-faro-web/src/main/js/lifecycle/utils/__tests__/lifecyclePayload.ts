import {
	buildCreateLifecyclePayload,
	buildStageFilter,
} from 'lifecycle/utils/lifecyclePayload';
import {
	createDefaultStageConfigs,
	IStageConfig,
} from 'lifecycle/utils/stageConfiguration';

const baseStage: IStageConfig = {
	conditionValue: null,
	description: 'A stage',
	field: null,
	fieldDataCategory: null,
	fieldDataType: null,
	maxTimeDays: 90,
	maxTimeEnabled: true,
	operator: null,
};

describe('buildStageFilter', () => {
	it('builds an equality filter for a text field', () => {
		expect(
			buildStageFilter({
				...baseStage,
				conditionValue: 'Technology',
				field: 'industry',
				fieldDataCategory: 'Text',
				operator: 'is',
			})
		).toBe("(industry eq 'Technology')");
	});

	it('escapes single quotes in a text value', () => {
		expect(
			buildStageFilter({
				...baseStage,
				conditionValue: "O'Hara",
				field: 'industry',
				fieldDataCategory: 'Text',
				operator: 'is',
			})
		).toBe("(industry eq 'O''Hara')");
	});

	it('builds a contains filter', () => {
		expect(
			buildStageFilter({
				...baseStage,
				conditionValue: 'Tech',
				field: 'industry',
				fieldDataCategory: 'Text',
				operator: 'contains',
			})
		).toBe("(contains(industry, 'Tech'))");
	});

	it('builds a negated contains filter', () => {
		expect(
			buildStageFilter({
				...baseStage,
				conditionValue: 'Tech',
				field: 'industry',
				fieldDataCategory: 'Text',
				operator: 'does-not-contain',
			})
		).toBe("(not contains(industry, 'Tech'))");
	});

	it('builds an unquoted numeric comparison', () => {
		expect(
			buildStageFilter({
				...baseStage,
				conditionValue: '1000',
				field: 'annualRevenue',
				fieldDataCategory: 'Number',
				operator: 'gt',
			})
		).toBe('(annualRevenue gt 1000)');
	});

	it('returns an empty filter when a numeric value is missing', () => {
		expect(
			buildStageFilter({
				...baseStage,
				conditionValue: null,
				field: 'annualRevenue',
				fieldDataCategory: 'Number',
				operator: 'gt',
			})
		).toBe('');
	});

	it('returns an empty filter when a numeric value is not a number', () => {
		expect(
			buildStageFilter({
				...baseStage,
				conditionValue: 'abc',
				field: 'annualRevenue',
				fieldDataCategory: 'Number',
				operator: 'gt',
			})
		).toBe('');
	});

	it('builds null checks for value-less operators', () => {
		expect(
			buildStageFilter({
				...baseStage,
				field: 'industry',
				fieldDataCategory: 'Text',
				operator: 'is-unknown',
			})
		).toBe('(industry eq null)');

		expect(
			buildStageFilter({
				...baseStage,
				field: 'industry',
				fieldDataCategory: 'Text',
				operator: 'is-known',
			})
		).toBe('(industry ne null)');
	});

	it('builds a quoted boolean filter from the operator', () => {
		expect(
			buildStageFilter({
				...baseStage,
				field: 'hasActivePipeline',
				fieldDataCategory: 'Boolean',
				operator: 'true',
			})
		).toBe("(hasActivePipeline eq 'true')");
	});

	it('builds a date comparison with a quoted date', () => {
		expect(
			buildStageFilter({
				...baseStage,
				conditionValue: '2026-03-15',
				field: 'createdDate',
				fieldDataCategory: 'Date',
				operator: 'before',
			})
		).toBe("(createdDate lt '2026-03-15')");
	});
});

describe('buildCreateLifecyclePayload', () => {
	it('maps every stage with its order, type, and derived segment', () => {
		const payload = buildCreateLifecyclePayload({
			channelId: '123',
			groupId: '23',
			name: 'My Lifecycle',
			stageConfigs: createDefaultStageConfigs(),
		});

		expect(payload.channelId).toBe('123');
		expect(payload.name).toBe('My Lifecycle');
		expect(payload.stages).toHaveLength(6);
		expect(payload.stages[0].displayOrder).toBe(1);
		expect(payload.stages[0].stageType).toBe('AWARE');
		expect(payload.stages[0].segment).toHaveProperty('filter');
		expect(payload.stages[0].segment).toHaveProperty('filterMetadata');
	});

	it('sends a null maxDuration when the stage limit is disabled', () => {
		const payload = buildCreateLifecyclePayload({
			channelId: '123',
			groupId: '23',
			name: 'My Lifecycle',
			stageConfigs: [{...baseStage, maxTimeEnabled: false}],
		});

		expect(payload.stages[0].maxDuration).toBeNull();
	});
});
