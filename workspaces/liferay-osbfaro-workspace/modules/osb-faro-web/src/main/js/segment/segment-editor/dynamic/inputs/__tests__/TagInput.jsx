
import mockStore from 'test/mock-store';
import React from 'react';
import TagInput, {
	buildValue,
	getAssetTypeFromValue,
	getConjunctionCriterionFromValue,
	getEventTypeFromValue
} from '../TagInput';
import {cleanup, render} from '@testing-library/react';
import {
	createCustomValueMap,
	getIndexFromPropertyName
} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const TAG_ID = 'tag-id';
const TAG_NAME = 'My Tag';

const DAY_CRITERION = {
	operatorName: 'gt',
	propertyName: 'day',
	touched: false,
	valid: true,
	value: '2023-01-01'
};

const BASE_ITEMS = [
	{
		operatorName: 'eq',
		propertyName: 'tags/id',
		touched: false,
		valid: true,
		value: TAG_ID
	},
	{
		operatorName: 'eq',
		propertyName: 'tags/name',
		touched: false,
		valid: true,
		value: TAG_NAME
	}
];

function makeValue(items, {count = 1, operator = 'ge'} = {}) {
	return createCustomValueMap([
		{key: 'criterionGroup', value: items},
		{key: 'operator', value: operator},
		{key: 'value', value: count}
	]);
}

describe('TagInput', () => {
	describe('buildValue', () => {
		it('should include applicationId and eventId items for any asset type', () => {
			const value = buildValue(
				'all',
				'any',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(
				getIndexFromPropertyName(value, 'applicationId')
			).toBeGreaterThanOrEqual(0);
			expect(
				getIndexFromPropertyName(value, 'eventId')
			).toBeGreaterThanOrEqual(0);
		});

		it('should set a single applicationId and eventId for a specific asset type and event', () => {
			const value = buildValue(
				'view',
				'web-content',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			const appIdx = getIndexFromPropertyName(value, 'applicationId');
			const eventIdx = getIndexFromPropertyName(value, 'eventId');

			expect(appIdx).toBeGreaterThanOrEqual(0);
			expect(
				value.getIn(['criterionGroup', 'items', appIdx, 'value']).toJS()
			).toEqual(['WebContent']);

			expect(eventIdx).toBeGreaterThanOrEqual(0);
			expect(
				value
					.getIn(['criterionGroup', 'items', eventIdx, 'value'])
					.toJS()
			).toEqual(['webContentViewed']);
		});

		it('should set applicationId and all compatible eventIds when event type is all', () => {
			const value = buildValue(
				'all',
				'web-content',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			const appIdx = getIndexFromPropertyName(value, 'applicationId');
			const eventIdx = getIndexFromPropertyName(value, 'eventId');

			expect(appIdx).toBeGreaterThanOrEqual(0);
			expect(
				value.getIn(['criterionGroup', 'items', appIdx, 'value']).toJS()
			).toEqual(['WebContent']);

			expect(eventIdx).toBeGreaterThanOrEqual(0);
			expect(
				value
					.getIn(['criterionGroup', 'items', eventIdx, 'value'])
					.toJS()
			).toEqual(
				expect.arrayContaining([
					'webContentViewed',
					'webContentImpressionMade'
				])
			);
		});

		it('should always include tags/id, tags/name and day items', () => {
			const value = buildValue(
				'all',
				'any',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(
				getIndexFromPropertyName(value, 'tags/id')
			).toBeGreaterThanOrEqual(0);
			expect(
				getIndexFromPropertyName(value, 'tags/name')
			).toBeGreaterThanOrEqual(0);
			expect(
				getIndexFromPropertyName(value, 'day')
			).toBeGreaterThanOrEqual(0);
		});

		it('should store the provided tagId and tagName', () => {
			const value = buildValue(
				'all',
				'any',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			const tagIdIdx = getIndexFromPropertyName(value, 'tags/id');
			const tagNameIdx = getIndexFromPropertyName(value, 'tags/name');

			expect(
				value.getIn(['criterionGroup', 'items', tagIdIdx, 'value'])
			).toBe(TAG_ID);
			expect(
				value.getIn(['criterionGroup', 'items', tagNameIdx, 'value'])
			).toBe(TAG_NAME);
		});

		it('should store the occurrence operator and count at the top level', () => {
			const value = buildValue(
				'all',
				'any',
				'le',
				5,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(value.get('operator')).toBe('le');
			expect(value.get('value')).toBe(5);
		});
	});

	describe('getAssetTypeFromValue', () => {
		it('should return any when value is undefined', () => {
			expect(getAssetTypeFromValue(undefined)).toBe('any');
		});

		it('should return any when applicationId has multiple entries', () => {
			const value = buildValue(
				'all',
				'any',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(getAssetTypeFromValue(value)).toBe('any');
		});

		it('should return the correct asset type for a specific single applicationId', () => {
			const value = buildValue(
				'view',
				'web-content',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(getAssetTypeFromValue(value)).toBe('web-content');
		});

		it('should return the correct asset type for blogs', () => {
			const value = buildValue(
				'all',
				'blogs',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(getAssetTypeFromValue(value)).toBe('blogs');
		});

		it('should return any for an unrecognized applicationId', () => {
			const value = makeValue([
				...BASE_ITEMS,
				{
					operatorName: 'in',
					propertyName: 'applicationId',
					touched: false,
					valid: true,
					value: ['UnknownApp']
				},
				DAY_CRITERION
			]);

			expect(getAssetTypeFromValue(value)).toBe('any');
		});
	});

	describe('getEventTypeFromValue', () => {
		it('should return all when value is undefined', () => {
			expect(getEventTypeFromValue(undefined)).toBe('all');
		});

		it('should return all when eventId has multiple entries', () => {
			const value = buildValue(
				'all',
				'any',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(getEventTypeFromValue(value)).toBe('all');
		});

		it('should return the correct event type for a specific single eventId', () => {
			const value = buildValue(
				'view',
				'web-content',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(getEventTypeFromValue(value)).toBe('view');
		});

		it('should return download for a download event', () => {
			const value = buildValue(
				'download',
				'documents-and-media',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(getEventTypeFromValue(value)).toBe('download');
		});

		it('should return all when all compatible eventIds are set', () => {
			const value = buildValue(
				'all',
				'web-content',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			expect(getEventTypeFromValue(value)).toBe('all');
		});
	});

	describe('getConjunctionCriterionFromValue', () => {
		it('should return the default criterion when value is undefined', () => {
			const result = getConjunctionCriterionFromValue(undefined);

			expect(result.propertyName).toBe('day');
			expect(result.operatorName).toBe('gt');
		});

		it('should return the day criterion stored in the value', () => {
			const value = buildValue(
				'all',
				'any',
				'ge',
				1,
				DAY_CRITERION,
				TAG_ID,
				TAG_NAME
			);

			const result = getConjunctionCriterionFromValue(value);

			expect(result.propertyName).toBe('day');
			expect(result.operatorName).toBe('gt');
			expect(result.value).toBe('2023-01-01');
		});

		it('should return the default criterion when value has no day item', () => {
			const value = makeValue(BASE_ITEMS);

			const result = getConjunctionCriterionFromValue(value);

			expect(result.propertyName).toBe('day');
			expect(result.operatorName).toBe('gt');
		});
	});

	describe('render', () => {
		afterEach(cleanup);

		it('should call onChange on mount with a built value when value has no criterionGroup', () => {
			const onChange = jest.fn();

			render(
				<Provider store={mockStore()}>
					<TagInput
						displayValue='My Tag'
						onChange={onChange}
						operatorRenderer={() => <div />}
						property={new Property({name: TAG_ID})}
					/>
				</Provider>
			);

			expect(onChange).toHaveBeenCalled();

			const {value} = onChange.mock.calls[0][0];

			expect(
				getIndexFromPropertyName(value, 'tags/id')
			).toBeGreaterThanOrEqual(0);
			expect(
				getIndexFromPropertyName(value, 'day')
			).toBeGreaterThanOrEqual(0);
		});

		it('should call onChange on mount when value has operator and count but no tags/id (getDefaultValue stub)', () => {
			const onChange = jest.fn();

			const stubValue = createCustomValueMap([
				{key: 'operator', value: 'ge'},
				{key: 'value', value: 1}
			]);

			render(
				<Provider store={mockStore()}>
					<TagInput
						displayValue='My Tag'
						onChange={onChange}
						operatorRenderer={() => <div />}
						property={new Property({name: TAG_ID})}
						value={stubValue}
					/>
				</Provider>
			);

			expect(onChange).toHaveBeenCalled();

			const {value} = onChange.mock.calls[0][0];

			const tagIdIdx = getIndexFromPropertyName(value, 'tags/id');

			expect(tagIdIdx).toBeGreaterThanOrEqual(0);
			expect(
				value.getIn(['criterionGroup', 'items', tagIdIdx, 'value'])
			).toBe(TAG_ID);
			expect(
				getIndexFromPropertyName(value, 'tags/name')
			).toBeGreaterThanOrEqual(0);
			expect(
				getIndexFromPropertyName(value, 'applicationId')
			).toBeGreaterThanOrEqual(0);
			expect(
				getIndexFromPropertyName(value, 'day')
			).toBeGreaterThanOrEqual(0);
		});
	});
});
