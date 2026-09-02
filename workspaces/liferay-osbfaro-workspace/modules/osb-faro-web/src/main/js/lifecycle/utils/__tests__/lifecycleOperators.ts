import {
	isConditionComplete,
	isStageConfigured,
	OperatorType,
	resolveOperatorType,
} from 'lifecycle/utils/lifecycleOperators';
import {
	createStageCondition,
	IStageCondition,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';

const buildCondition = (
	condition: Partial<IStageCondition> = {}
): IStageCondition => ({...createStageCondition(), ...condition});

const buildStage = (
	conditions: Partial<IStageCondition>[],
	stage: Partial<IStageConfig> = {}
): IStageConfig => ({
	conditions: conditions.map(buildCondition),
	description: 'A stage',
	id: null,
	matchLogic: MatchLogic.All,
	maxTimeDays: 90,
	maxTimeEnabled: true,
	...stage,
});

const completeCondition = {
	conditionValue: 'Technology',
	field: 'industry',
	fieldDataCategory: 'Text',
	operator: 'is',
};

describe('resolveOperatorType', () => {
	it('resolves the data category sent by the catalog', () => {
		expect(resolveOperatorType('Text', 'STRING')).toBe(OperatorType.Text);
		expect(resolveOperatorType('Number', 'NUMERIC')).toBe(
			OperatorType.Number
		);
	});

	it('resolves a data category whose casing differs from the catalog', () => {
		expect(resolveOperatorType('TEXT', 'STRING')).toBe(OperatorType.Text);
		expect(resolveOperatorType('number', 'NUMERIC')).toBe(
			OperatorType.Number
		);
	});

	it('resolves a duration regardless of its casing', () => {
		expect(resolveOperatorType('Number', 'duration')).toBe(
			OperatorType.Duration
		);
	});

	it('resolves nothing when the data category is missing', () => {
		expect(resolveOperatorType(null, 'STRING')).toBeNull();
	});

	it('resolves nothing for a data category it does not know', () => {
		expect(resolveOperatorType('Geolocation', 'STRING')).toBeNull();
	});
});

describe('isConditionComplete', () => {
	it('accepts a condition with a field, an operator, and a value', () => {
		expect(isConditionComplete(buildCondition(completeCondition))).toBe(
			true
		);
	});

	it('accepts a value-less operator without a value', () => {
		expect(
			isConditionComplete(
				buildCondition({
					field: 'industry',
					fieldDataCategory: 'Text',
					operator: 'is-known',
				})
			)
		).toBe(true);
	});

	it('rejects a condition that is still empty', () => {
		expect(isConditionComplete(buildCondition())).toBe(false);
	});

	it('rejects a condition whose operator was never picked', () => {
		expect(isConditionComplete(buildCondition({field: 'industry'}))).toBe(
			false
		);
	});

	it('rejects a numeric value the filter could not express', () => {
		expect(
			isConditionComplete(
				buildCondition({
					conditionValue: '1e999',
					field: 'annualRevenue',
					fieldDataCategory: 'Number',
					operator: 'gt',
				})
			)
		).toBe(false);
	});

	it('rejects a numeric value that is only whitespace', () => {
		expect(
			isConditionComplete(
				buildCondition({
					conditionValue: '   ',
					field: 'annualRevenue',
					fieldDataCategory: 'Number',
					operator: 'gt',
				})
			)
		).toBe(false);
	});

	it('accepts a numeric value the filter can express', () => {
		expect(
			isConditionComplete(
				buildCondition({
					conditionValue: '1000',
					field: 'annualRevenue',
					fieldDataCategory: 'Number',
					operator: 'gt',
				})
			)
		).toBe(true);
	});

	it('rejects a condition whose value is still missing', () => {
		expect(
			isConditionComplete(
				buildCondition({field: 'industry', operator: 'is'})
			)
		).toBe(false);
	});
});

describe('isStageConfigured', () => {
	it('accepts a stage whose single condition is complete', () => {
		expect(isStageConfigured(buildStage([completeCondition]))).toBe(true);
	});

	it('accepts a stage whose conditions are all complete', () => {
		expect(
			isStageConfigured(
				buildStage([
					completeCondition,
					{
						conditionValue: '1000',
						field: 'annualRevenue',
						fieldDataCategory: 'Number',
						operator: 'gt',
					},
				])
			)
		).toBe(true);
	});

	it('rejects a stage when any one of its conditions is incomplete', () => {
		expect(
			isStageConfigured(
				buildStage([completeCondition, {field: 'annualRevenue'}])
			)
		).toBe(false);
	});

	it('rejects a stage with no conditions at all', () => {
		expect(isStageConfigured(buildStage([]))).toBe(false);
	});

	it('rejects a stage whose description was cleared', () => {
		expect(
			isStageConfigured(
				buildStage([completeCondition], {description: ' '})
			)
		).toBe(false);
	});
});
