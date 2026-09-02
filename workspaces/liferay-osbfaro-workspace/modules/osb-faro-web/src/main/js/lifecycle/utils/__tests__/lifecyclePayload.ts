import {
	buildCreateLifecyclePayload,
	buildStageFilter,
	buildStageFilterMetadata,
	buildUpdateLifecyclePayload,
	stageConfigsFromLifecycle,
} from 'lifecycle/utils/lifecyclePayload';
import {
	createDefaultStageConfigs,
	createStageCondition,
	IStageCondition,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';

const buildCondition = (
	condition: Partial<IStageCondition> = {}
): IStageCondition => ({...createStageCondition(), ...condition});

const buildStage = (
	conditions: Partial<IStageCondition>[] = [{}],
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

describe('buildStageFilter', () => {
	it('builds an equality filter for a text field', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: 'Technology',
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'is',
					},
				])
			)
		).toBe("(industry eq 'Technology')");
	});

	it('escapes single quotes in a text value', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: "O'Hara",
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'is',
					},
				])
			)
		).toBe("(industry eq 'O''Hara')");
	});

	it('builds a contains filter', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: 'Tech',
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'contains',
					},
				])
			)
		).toBe("(contains(industry, 'Tech'))");
	});

	it('builds a negated contains filter', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: 'Tech',
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'does-not-contain',
					},
				])
			)
		).toBe("(not contains(industry, 'Tech'))");
	});

	it('builds an unquoted numeric comparison', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: '1000',
						field: 'annualRevenue',
						fieldDataCategory: 'Number',
						operator: 'gt',
					},
				])
			)
		).toBe('(annualRevenue gt 1000)');
	});

	it('returns an empty filter when a numeric value is missing', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: null,
						field: 'annualRevenue',
						fieldDataCategory: 'Number',
						operator: 'gt',
					},
				])
			)
		).toBe('');
	});

	it('returns an empty filter when a numeric value is not a number', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: 'abc',
						field: 'annualRevenue',
						fieldDataCategory: 'Number',
						operator: 'gt',
					},
				])
			)
		).toBe('');
	});

	it('builds null checks for value-less operators', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'is-unknown',
					},
				])
			)
		).toBe('(industry eq null)');

		expect(
			buildStageFilter(
				buildStage([
					{
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'is-known',
					},
				])
			)
		).toBe('(industry ne null)');
	});

	it('builds a quoted boolean filter from the operator', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						field: 'hasActivePipeline',
						fieldDataCategory: 'Boolean',
						operator: 'true',
					},
				])
			)
		).toBe("(hasActivePipeline eq 'true')");
	});

	it('builds a date comparison with a quoted date', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: '2026-03-15',
						field: 'createdDate',
						fieldDataCategory: 'Date',
						operator: 'before',
					},
				])
			)
		).toBe("(createdDate lt '2026-03-15')");
	});

	it('joins several conditions with and when matching on all', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: 'Technology',
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'is',
					},
					{
						conditionValue: '1000',
						field: 'annualRevenue',
						fieldDataCategory: 'Number',
						operator: 'gt',
					},
				])
			)
		).toBe("((industry eq 'Technology') and (annualRevenue gt 1000))");
	});

	it('joins several conditions with or when matching on any', () => {
		expect(
			buildStageFilter(
				buildStage(
					[
						{
							conditionValue: 'Technology',
							field: 'industry',
							fieldDataCategory: 'Text',
							operator: 'is',
						},
						{
							conditionValue: '1000',
							field: 'annualRevenue',
							fieldDataCategory: 'Number',
							operator: 'gt',
						},
					],
					{matchLogic: MatchLogic.Any}
				)
			)
		).toBe("((industry eq 'Technology') or (annualRevenue gt 1000))");
	});

	it('leaves out conditions that are not complete', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: 'Technology',
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'is',
					},
					{field: 'annualRevenue', fieldDataCategory: 'Number'},
				])
			)
		).toBe("(industry eq 'Technology')");
	});

	it('leaves a lone condition unwrapped', () => {
		expect(
			buildStageFilter(
				buildStage([
					{
						conditionValue: 'Technology',
						field: 'industry',
						fieldDataCategory: 'Text',
						operator: 'is',
					},
				])
			)
		).toBe("(industry eq 'Technology')");
	});

	it('returns an empty filter when a stage has no conditions', () => {
		expect(buildStageFilter(buildStage([]))).toBe('');
	});
});

describe('buildStageFilterMetadata', () => {
	it('persists every condition and the match logic', () => {
		const metadata = JSON.parse(
			buildStageFilterMetadata(
				buildStage(
					[
						{
							conditionValue: 'Technology',
							field: 'industry',
							fieldDataCategory: 'Text',
							fieldDataType: 'STRING',
							operator: 'is',
						},
						{
							conditionValue: '1000',
							field: 'annualRevenue',
							fieldDataCategory: 'Number',
							fieldDataType: 'NUMERIC',
							operator: 'gt',
						},
					],
					{matchLogic: MatchLogic.Any}
				)
			)
		);

		expect(metadata.matchLogic).toBe(MatchLogic.Any);
		expect(metadata.conditions).toHaveLength(2);
		expect(metadata.conditions[1]).toEqual({
			conditionValue: '1000',
			field: 'annualRevenue',
			fieldDataCategory: 'Number',
			fieldDataType: 'NUMERIC',
			operator: 'gt',
		});
	});

	it('leaves the client side row key out of the saved metadata', () => {
		const metadata = JSON.parse(
			buildStageFilterMetadata(buildStage([{field: 'industry'}]))
		);

		expect(metadata.conditions[0]).not.toHaveProperty('key');
	});
});

describe('buildCreateLifecyclePayload', () => {
	it('maps every stage with its order, type, and derived rule', () => {
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
		expect(payload.stages[0].accountLifecycleStageRule).toHaveProperty(
			'filterString'
		);
		expect(payload.stages[0].accountLifecycleStageRule).toHaveProperty(
			'filterMetadata'
		);
		expect(payload.stages[0].accountLifecycleStageRule.name).toBe(
			'My Lifecycle Stage AWARE Criteria'
		);
	});

	it('sends a null maxDuration when the stage limit is disabled', () => {
		const payload = buildCreateLifecyclePayload({
			channelId: '123',
			groupId: '23',
			name: 'My Lifecycle',
			stageConfigs: [buildStage([{}], {maxTimeEnabled: false})],
		});

		expect(payload.stages[0].maxDuration).toBeNull();
	});
});

describe('buildUpdateLifecyclePayload', () => {
	it('includes the stage id when present and omits it otherwise', () => {
		const payload = buildUpdateLifecyclePayload({
			groupId: '23',
			lifecycleId: '9',
			name: 'My Lifecycle',
			stageConfigs: [
				buildStage([{}], {id: 'stage-1'}),
				buildStage([{}], {id: null}),
			],
		});

		expect(payload.groupId).toBe('23');
		expect(payload.lifecycleId).toBe('9');
		expect(payload.stages[0].id).toBe('stage-1');
		expect(payload.stages[1]).not.toHaveProperty('id');
	});
});

describe('stageConfigsFromLifecycle', () => {
	it('rebuilds every stage config from the saved rule metadata', () => {
		const configs = stageConfigsFromLifecycle([
			{
				accountLifecycleStageRule: {
					filterMetadata: JSON.stringify({
						conditions: [
							{
								conditionValue: '1000',
								field: 'account.annualRevenue',
								fieldDataCategory: 'Number',
								fieldDataType: 'NUMERIC',
								operator: 'gt',
							},
							{
								conditionValue: 'Technology',
								field: 'account.industry',
								fieldDataCategory: 'Text',
								fieldDataType: 'STRING',
								operator: 'is',
							},
						],
						matchLogic: MatchLogic.Any,
					}),
					filterString:
						"(account.annualRevenue gt 1000) or (account.industry eq 'Technology')",
				},
				description: 'Saved description',
				displayOrder: 1,
				id: 'stage-1',
				maxDuration: 30,
				stageType: 'AWARE',
			},
		]);

		expect(configs).toHaveLength(6);

		const [aware] = configs;

		expect(aware.id).toBe('stage-1');
		expect(aware.description).toBe('Saved description');
		expect(aware.matchLogic).toBe(MatchLogic.Any);
		expect(aware.conditions).toHaveLength(2);
		expect(aware.conditions[0].field).toBe('account.annualRevenue');
		expect(aware.conditions[0].operator).toBe('gt');
		expect(aware.conditions[0].conditionValue).toBe('1000');
		expect(aware.conditions[1].field).toBe('account.industry');
		expect(aware.maxTimeDays).toBe(30);
		expect(aware.maxTimeEnabled).toBe(true);
	});

	it('reads a trigger saved before a stage could hold several conditions', () => {
		const [aware] = stageConfigsFromLifecycle([
			{
				accountLifecycleStageRule: {
					filterMetadata: JSON.stringify({
						conditionValue: '1000',
						field: 'account.annualRevenue',
						fieldDataCategory: 'Number',
						fieldDataType: 'NUMERIC',
						operator: 'gt',
					}),
					filterString: '(account.annualRevenue gt 1000)',
				},
				description: 'Saved description',
				displayOrder: 1,
				id: 'stage-1',
				maxDuration: 30,
				stageType: 'AWARE',
			},
		]);

		expect(aware.matchLogic).toBe(MatchLogic.All);
		expect(aware.conditions).toHaveLength(1);
		expect(aware.conditions[0].field).toBe('account.annualRevenue');
		expect(aware.conditions[0].operator).toBe('gt');
		expect(aware.conditions[0].conditionValue).toBe('1000');
	});

	it('gives every rebuilt condition its own row key', () => {
		const [aware] = stageConfigsFromLifecycle([
			{
				accountLifecycleStageRule: {
					filterMetadata: JSON.stringify({
						conditions: [{field: 'a'}, {field: 'b'}],
						matchLogic: MatchLogic.All,
					}),
					filterString: '',
				},
				description: 'Saved description',
				displayOrder: 1,
				id: 'stage-1',
				maxDuration: 30,
				stageType: 'AWARE',
			},
		]);

		expect(aware.conditions[0].key).not.toBe(aware.conditions[1].key);
	});

	it('falls back to defaults for stages absent from the response', () => {
		const configs = stageConfigsFromLifecycle([]);

		expect(configs).toHaveLength(6);
		expect(configs.every((config) => config.id === null)).toBe(true);
		expect(configs[0].conditions).toHaveLength(1);
		expect(configs[0].conditions[0].field).toBeNull();
		expect(configs[0].matchLogic).toBe(MatchLogic.All);
	});

	it('disables the stage limit when maxDuration is null', () => {
		const [aware] = stageConfigsFromLifecycle([
			{
				description: 'Saved',
				displayOrder: 1,
				id: 'stage-1',
				maxDuration: null,
				stageType: 'AWARE',
			},
		]);

		expect(aware.maxTimeEnabled).toBe(false);
	});
});
