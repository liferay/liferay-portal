import {
	createCustomValueMap,
	getCompleteDate,
	getFilterCriterionIMap,
	getFilterCriterionIMapByPropertyNamePrefix,
	getIndexFromPropertyName,
	getIndexFromPropertyNamePrefix,
	getOperator,
	getPropertyValue,
	hasAttributeFilterCriterion,
	removeItemsByIndex,
	setCompleteDate,
	setOperator,
	setPropertyValue,
} from '../custom-inputs';
import {
	FunctionalOperators,
	RelationalOperators,
	TimeSpans,
} from '../../utils/constants';

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
});
