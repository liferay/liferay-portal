import {
	createStageCondition,
	IStageCondition,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';
import {
	getDuplicateStageKeys,
	getLifecycleErrorMessages,
	getStageErrorMessages,
	getStageErrors,
	hasStageChipError,
	isAtCharacterLimit,
} from 'lifecycle/utils/lifecycleValidation';

const completeCondition: Partial<IStageCondition> = {
	conditionValue: '1000',
	field: 'account.annualRevenue',
	fieldDataCategory: 'Number',
	fieldDataType: 'NUMERIC',
	operator: 'gt',
};

const buildStage = (
	stage: Partial<IStageConfig> = {},
	conditions: Partial<IStageCondition>[] = [completeCondition]
): IStageConfig => ({
	conditions: conditions.map((condition) => ({
		...createStageCondition(),
		...condition,
	})),
	description: 'Cold accounts',
	id: null,
	key: 'stage-key',
	matchLogic: MatchLogic.All,
	maxTimeDays: 90,
	maxTimeEnabled: true,
	name: 'Aware',
	...stage,
});

const VALID = {
	description: 'Qualified accounts',
	name: 'Hub',
	stageConfigs: [buildStage()],
};

describe('getStageErrors', () => {
	it('finds no errors in a complete stage', () => {
		expect(getStageErrors(buildStage())).toEqual({
			description: false,
			incompleteTrigger: false,
			name: false,
			trigger: false,
		});
	});

	it('flags a blank name and description', () => {
		expect(
			getStageErrors(buildStage({description: ' ', name: ' '}))
		).toEqual(expect.objectContaining({description: true, name: true}));
	});

	it('flags a stage with no complete trigger', () => {
		expect(getStageErrors(buildStage({}, [{}])).trigger).toBe(true);
	});

	it('flags a trigger whose field is picked but not completed', () => {
		expect(
			getStageErrors(
				buildStage({}, [
					completeCondition,
					{field: 'account.annualRevenue'},
				])
			)
		).toEqual(
			expect.objectContaining({incompleteTrigger: true, trigger: false})
		);
	});
});

describe('hasStageChipError', () => {
	it('shows the chip icon for a blank name', () => {
		expect(hasStageChipError(buildStage({name: ' '}))).toBe(true);
	});

	it('shows no chip icon for a named stage that is not configured yet', () => {
		expect(hasStageChipError(buildStage({}, [{}]))).toBe(false);
		expect(hasStageChipError(buildStage({description: ''}))).toBe(false);
	});
});

describe('getLifecycleErrorMessages', () => {
	it('returns nothing for a valid lifecycle', () => {
		expect(getLifecycleErrorMessages(VALID)).toEqual([]);
	});

	it('requires the lifecycle name and description', () => {
		expect(
			getLifecycleErrorMessages({...VALID, description: '', name: ''})
		).toEqual([
			expect.stringMatching(/lifecycle name field is required/i),
			expect.stringMatching(/lifecycle description field is required/i),
		]);
	});

	it('requires at least one stage', () => {
		expect(getLifecycleErrorMessages({...VALID, stageConfigs: []})).toEqual(
			[expect.stringMatching(/at least one stage/i)]
		);
	});

	it('uses singular messages when one stage has the error', () => {
		expect(
			getLifecycleErrorMessages({
				...VALID,
				stageConfigs: [buildStage({name: ''}, [{}]), buildStage()],
			})
		).toEqual([
			expect.stringMatching(/the stage name field is required/i),
			expect.stringMatching(/the stage trigger is required/i),
		]);
	});

	it('uses plural messages when several stages have the error', () => {
		expect(
			getLifecycleErrorMessages({
				...VALID,
				stageConfigs: [
					buildStage({description: '', name: ''}, [{}]),
					buildStage({description: '', name: ''}, [{}]),
				],
			})
		).toEqual([
			expect.stringMatching(/stage name fields are required/i),
			expect.stringMatching(/stage description fields are required/i),
			expect.stringMatching(/stage triggers are required/i),
		]);
	});

	it('asks once for unique stage names', () => {
		expect(
			getLifecycleErrorMessages({
				...VALID,
				stageConfigs: [
					buildStage({key: 'first'}),
					buildStage({key: 'second', name: 'aware'}),
					buildStage({key: 'third', name: 'AWARE'}),
				],
			})
		).toEqual([expect.stringMatching(/stage names should be unique/i)]);
	});

	it('reports incomplete triggers', () => {
		expect(
			getLifecycleErrorMessages({
				...VALID,
				stageConfigs: [
					buildStage({}, [
						completeCondition,
						{field: 'account.annualRevenue'},
					]),
				],
			})
		).toEqual([expect.stringMatching(/incomplete stage triggers/i)]);
	});
});

describe('getStageErrorMessages', () => {
	it('lists only the stage errors, as on the stage page', () => {
		expect(getStageErrorMessages(buildStage({name: ''}, [{}]))).toEqual([
			expect.stringMatching(/the stage name field is required/i),
			expect.stringMatching(/stage triggers are required/i),
		]);
	});

	it('returns nothing for a complete stage', () => {
		expect(getStageErrorMessages(buildStage())).toEqual([]);
	});

	it('asks for a unique name after the name error of a duplicate stage', () => {
		expect(getStageErrorMessages(buildStage({}, [{}]), true)).toEqual([
			expect.stringMatching(/stage names should be unique/i),
			expect.stringMatching(/stage triggers are required/i),
		]);
	});
});

describe('getDuplicateStageKeys', () => {
	it('returns every stage that shares a name, ignoring case and spaces', () => {
		expect(
			getDuplicateStageKeys([
				buildStage({key: 'aware', name: 'Aware'}),
				buildStage({key: 'engaged', name: 'Engaged'}),
				buildStage({key: 'aware-copy', name: '  aware '}),
				buildStage({key: 'early-adopter', name: 'Early  Adopter'}),
				buildStage({key: 'early-adopter-copy', name: 'early adopter'}),
			])
		).toEqual([
			'aware',
			'aware-copy',
			'early-adopter',
			'early-adopter-copy',
		]);
	});

	it('ignores stages without a name', () => {
		expect(
			getDuplicateStageKeys([
				buildStage({key: 'first', name: ''}),
				buildStage({key: 'second', name: ' '}),
			])
		).toEqual([]);
	});
});

describe('isAtCharacterLimit', () => {
	it('reaches the limit at the maximum length', () => {
		expect(isAtCharacterLimit('a'.repeat(23), 24)).toBe(false);
		expect(isAtCharacterLimit('a'.repeat(24), 24)).toBe(true);
	});
});
