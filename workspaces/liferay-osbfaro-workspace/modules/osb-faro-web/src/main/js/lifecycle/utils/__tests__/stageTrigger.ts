import {getStageTrigger} from '../stageTrigger';
import {ILifecycleStage} from 'shared/api/lifecycle';

const buildStage = (
	filterMetadata: object | string | null
): ILifecycleStage => ({
	accountLifecycleStageRule: {
		filterMetadata:
			filterMetadata && typeof filterMetadata === 'object'
				? JSON.stringify(filterMetadata)
				: (filterMetadata as string | null),
		filterString: '',
	},
	description: '',
	externalReferenceCode: '1',
	id: '1',
	maxDuration: null,
	priority: 1,
	stageName: 'Aware',
});

describe('getStageTrigger', () => {
	it('reads each condition with its operator label', () => {
		const {conditions} = getStageTrigger(
			buildStage({
				conditions: [
					{
						conditionValue: 'Inquiry',
						field: 'salesforce/lead_Funnel_Stage__c',
						fieldDataCategory: 'Text',
						fieldDataType: 'STRING',
						operator: 'is',
					},
					{
						conditionValue: '10',
						field: 'employees',
						fieldDataCategory: 'Number',
						fieldDataType: 'INTEGER',
						operator: 'gt',
					},
				],
				matchLogic: 'any',
			})
		);

		expect(conditions).toEqual([
			expect.objectContaining({
				field: 'salesforce/lead_Funnel_Stage__c',
				operator: 'is',
				value: 'Inquiry',
			}),
			expect.objectContaining({
				field: 'employees',
				operator: 'greater than',
				value: '10',
			}),
		]);
	});

	it('joins the conditions with OR when any may match', () => {
		const {connector} = getStageTrigger(
			buildStage({conditions: [], matchLogic: 'any'})
		);

		expect(connector).toMatch(/^or$/i);
	});

	it('joins the conditions with AND when all must match', () => {
		const {connector} = getStageTrigger(
			buildStage({conditions: [], matchLogic: 'all'})
		);

		expect(connector).toMatch(/^and$/i);
	});

	it('drops the value of valueless operators', () => {
		const {conditions} = getStageTrigger(
			buildStage({
				conditionValue: 'ignored',
				field: 'industry',
				fieldDataCategory: 'Text',
				fieldDataType: 'STRING',
				operator: 'is-known',
			})
		);

		expect(conditions).toEqual([
			expect.objectContaining({operator: 'is known', value: null}),
		]);
	});

	it('falls back to the raw operator of an unknown field type', () => {
		const {conditions} = getStageTrigger(
			buildStage({
				conditionValue: 'x',
				field: 'custom',
				fieldDataCategory: null,
				fieldDataType: null,
				operator: 'matches',
			})
		);

		expect(conditions).toEqual([
			expect.objectContaining({operator: 'matches'}),
		]);
	});

	it('returns no conditions for a stage without a trigger', () => {
		expect(getStageTrigger(buildStage(null)).conditions).toEqual([]);
		expect(getStageTrigger(buildStage('not json')).conditions).toEqual([]);
	});
});
