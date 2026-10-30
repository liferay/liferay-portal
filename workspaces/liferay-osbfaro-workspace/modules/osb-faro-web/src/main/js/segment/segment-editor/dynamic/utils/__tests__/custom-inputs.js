import {
	createCustomValueMap,
	findLifecycleFromValue,
	getCompleteDate,
	getFilterCriterionIMap,
	getFilterCriterionIMapByPropertyNamePrefix,
	getIndexFromPropertyName,
	getIndexFromPropertyNamePrefix,
	getLifecycleERCFromValue,
	getLifecycleStageERCsFromValue,
	getOperator,
	getPropertyValue,
	hasAttributeFilterCriterion,
	removeItemsByIndex,
	setCompleteDate,
	setOperator,
	setPropertyValue,
} from '../custom-inputs';
import {
	Conjunctions,
	FunctionalOperators,
	RelationalOperators,
	TimeSpans,
} from '../../utils/constants';
import {createNewGroup} from '../utils';
import {fromJS} from 'immutable';

const mockValue = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
			{
				operatorName: RelationalOperators.EQ,
				propertyName: 'context/city',
				value: 'foo',
			},
			{
				operatorName: RelationalOperators.GT,
				propertyName: 'completeDate',
				value: TimeSpans.Last7Days,
			},
		],
	},
]);

const mockValueWithAttributePlaceholder = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
			{
				operatorName: FunctionalOperators.Contains,
				propertyName: 'attribute/',
				value: '',
			},
		],
	},
]);

const mockValueWithAttribute = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
			{
				operatorName: FunctionalOperators.Contains,
				propertyName: 'attribute/123',
				value: 'foo',
			},
		],
	},
]);

const getLifecycleValue = (items, conjunctionName) =>
	createCustomValueMap([
		{
			key: 'criterionGroup',
			value: fromJS(createNewGroup(items, conjunctionName)),
		},
	]);

describe('Custom Inputs Util', () => {
	describe('createCustomValueMap', () => {

		// This unit test is skipped because uuid is generated every time test is run.

		it.skip('should create an immutable valueIMap from a given param array', () => {
			expect(
				createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [
							{
								operatorName: RelationalOperators.EQ,
								propertyName: 'context/city',
								value: 'foo',
							},
							{
								operatorName: RelationalOperators.GT,
								propertyName: 'completeDate',
								value: TimeSpans.Last7Days,
							},
						],
					},
				])
			).toMatchSnapshot();
		});
	});

	describe('getFilterCriterionIMap', () => {
		it('should return the Filter Criterion Immutable Map', () => {
			expect(
				getFilterCriterionIMap(mockValue, 1).get('propertyName')
			).toBe('completeDate');
		});
	});

	describe('getIndexFromPropertyName', () => {
		it('should return the index of the first entry in the criterion list that matches the propertyName', () => {
			expect(getIndexFromPropertyName(mockValue, 'completeDate')).toBe(1);
		});
	});

	describe('getOperator', () => {
		it('should return the operator', () => {
			expect(getOperator(mockValue, 0)).toBe(RelationalOperators.EQ);
		});
	});

	describe('getCompleteDate', () => {
		it('should return the time period', () => {
			expect(getCompleteDate(mockValue)).toBe('last7Days');
		});
	});

	describe('getPropertyValue', () => {
		it('should return the value', () => {
			expect(getPropertyValue(mockValue, 'value', 0)).toBe('foo');
		});
	});

	describe('removeItemsByIndex', () => {
		it('should remove items by index from the criteria list', () => {
			const indexToRemove = getIndexFromPropertyName(
				mockValue,
				'completeDate'
			);

			const updatedMockValue = removeItemsByIndex(mockValue, [
				indexToRemove,
			]);

			expect(
				getIndexFromPropertyName(updatedMockValue, 'completeDate')
			).toBe(-1);
		});
	});

	describe('setOperator', () => {
		it('should set the operator', () => {
			const updatedValue = setOperator(
				mockValue,
				0,
				RelationalOperators.NE
			);

			expect(getOperator(updatedValue, 0)).toBe(RelationalOperators.NE);
		});
	});

	describe('setPropertyValue', () => {
		it('should update the value', () => {
			const newValue = 'new value foo';
			const updatedValue = setPropertyValue(
				mockValue,
				'value',
				0,
				newValue
			);

			expect(getPropertyValue(updatedValue, 'value', 0)).toBe(newValue);
		});
	});

	describe('setCompleteDate', () => {
		it('should update the time period', () => {
			const newTimePeriod = 'fooTimePeriod';
			const updatedValue = setCompleteDate(mockValue, newTimePeriod);

			expect(getCompleteDate(updatedValue)).toBe(newTimePeriod);
		});
	});

	describe('getIndexFromPropertyNamePrefix', () => {
		it('should return the index of the first entry whose propertyName starts with the prefix', () => {
			expect(
				getIndexFromPropertyNamePrefix(
					mockValueWithAttribute,
					'attribute/'
				)
			).toBe(0);
		});

		it('should return -1 when no entry matches the prefix', () => {
			expect(
				getIndexFromPropertyNamePrefix(mockValue, 'attribute/')
			).toBe(-1);
		});
	});

	describe('getFilterCriterionIMapByPropertyNamePrefix', () => {
		it('should return the Criterion Map whose propertyName starts with the prefix', () => {
			expect(
				getFilterCriterionIMapByPropertyNamePrefix(
					mockValueWithAttribute,
					'attribute/'
				).get('propertyName')
			).toBe('attribute/123');
		});

		it('should return undefined when no entry matches the prefix', () => {
			expect(
				getFilterCriterionIMapByPropertyNamePrefix(
					mockValue,
					'attribute/'
				)
			).toBeUndefined();
		});
	});

	describe('hasAttributeFilterCriterion', () => {
		it('should return true when a real attribute is selected', () => {
			expect(
				hasAttributeFilterCriterion(
					mockValueWithAttribute,
					'attribute/'
				)
			).toBe(true);
		});

		it('should return false when only the seeded placeholder is present', () => {
			expect(
				hasAttributeFilterCriterion(
					mockValueWithAttributePlaceholder,
					'attribute/'
				)
			).toBe(false);
		});

		it('should return false when there is no attribute item at all', () => {
			expect(hasAttributeFilterCriterion(mockValue, 'attribute/')).toBe(
				false
			);
		});
	});

	describe('getLifecycleERCFromValue', () => {
		it.each([
			[
				'a lifecycle without stages',
				[
					{
						operatorName: RelationalOperators.NE,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: null,
					},
				],
				undefined,
				'erc-lifecycle-100',
			],
			[
				'an "or" group of stages',
				[
					{
						conjunctionName: Conjunctions.Or,
						items: [
							{
								operatorName: RelationalOperators.EQ,
								propertyName:
									'lifecycleStatus@erc-lifecycle-100',
								value: 'erc-stage-1001',
							},
							{
								operatorName: RelationalOperators.EQ,
								propertyName:
									'lifecycleStatus@erc-lifecycle-100',
								value: 'erc-stage-1002',
							},
						],
					},
				],
				undefined,
				'erc-lifecycle-100',
			],
			[
				'a saved "or" criterion group of stages',
				[
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: 'erc-stage-1001',
					},
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: 'erc-stage-1002',
					},
				],
				Conjunctions.Or,
				'erc-lifecycle-100',
			],
			[
				'a stage saved without its lifecycle',
				[
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus',
						value: 'erc-stage-1001',
					},
				],
				undefined,
				undefined,
			],
		])(
			'should return the lifecycle of %s',
			(_, items, conjunctionName, expected) => {
				expect(
					getLifecycleERCFromValue(
						getLifecycleValue(items, conjunctionName)
					)
				).toBe(expected);
			}
		);
	});

	describe('getLifecycleStageERCsFromValue', () => {
		it.each([
			[
				'a lifecycle without stages',
				[
					{
						operatorName: RelationalOperators.NE,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: null,
					},
				],
				undefined,
				[],
			],
			[
				'a flat item',
				[
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: 'erc-stage-1001',
					},
				],
				undefined,
				['erc-stage-1001'],
			],
			[
				'an "or" group',
				[
					{
						conjunctionName: Conjunctions.Or,
						items: [
							{
								operatorName: RelationalOperators.EQ,
								propertyName:
									'lifecycleStatus@erc-lifecycle-100',
								value: 'erc-stage-1001',
							},
							{
								operatorName: RelationalOperators.EQ,
								propertyName:
									'lifecycleStatus@erc-lifecycle-100',
								value: 'erc-stage-1002',
							},
						],
					},
				],
				undefined,
				['erc-stage-1001', 'erc-stage-1002'],
			],
			[
				'a saved "or" criterion group',
				[
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: 'erc-stage-1001',
					},
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: 'erc-stage-1002',
					},
				],
				Conjunctions.Or,
				['erc-stage-1001', 'erc-stage-1002'],
			],
			[
				'a stage saved without its lifecycle',
				[
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus',
						value: 'erc-stage-1001',
					},
				],
				undefined,
				['erc-stage-1001'],
			],
		])(
			'should return the stages of %s',
			(_, items, conjunctionName, expected) => {
				expect(
					getLifecycleStageERCsFromValue(
						getLifecycleValue(items, conjunctionName)
					)
				).toEqual(expected);
			}
		);
	});

	describe('findLifecycleFromValue', () => {
		const lifecycles = [
			{
				accountLifecycleStages: [
					{externalReferenceCode: 'erc-stage-2001'},
				],
				externalReferenceCode: 'erc-lifecycle-200',
			},
			{
				accountLifecycleStages: [
					{externalReferenceCode: 'erc-stage-1001'},
					{externalReferenceCode: 'erc-stage-1002'},
				],
				externalReferenceCode: 'erc-lifecycle-100',
			},
		];

		it.each([
			[
				'the lifecycle of the criterion',
				lifecycles,
				[
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: 'erc-stage-1001',
					},
				],
				lifecycles[1],
			],
			[
				'the lifecycle of a stage saved without its lifecycle',
				lifecycles,
				[
					{
						operatorName: RelationalOperators.EQ,
						propertyName: 'lifecycleStatus',
						value: 'erc-stage-1001',
					},
				],
				lifecycles[1],
			],
			[
				'no lifecycle for a lifecycle that does not exist',
				lifecycles.slice(0, 1),
				[
					{
						operatorName: RelationalOperators.NE,
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: null,
					},
				],
				undefined,
			],
		])('should find %s', (_, channelLifecycles, items, expected) => {
			expect(
				findLifecycleFromValue(
					channelLifecycles,
					getLifecycleValue(items)
				)
			).toBe(expected);
		});
	});
});
