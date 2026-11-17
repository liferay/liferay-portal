import * as data from 'test/data';
import BehaviorDisplay from '../BehaviorDisplay';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {
	CustomFunctionOperators,
	FunctionalOperators,
	PropertyTypes,
	RelationalOperators,
	TimeSpans
} from 'segment/segment-editor/dynamic/utils/constants';
import {List, Map} from 'immutable';
import {Property, Segment} from 'shared/util/records';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {withReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '456'})
}));

jest.mock('shared/api', () => ({
	assets: {
		searchTypes: jest.fn(() =>
			Promise.resolve({
				items: [{id: 'C_ProductSheet', name: 'Product Sheet'}],
				totalCount: 1
			})
		)
	}
}));

describe('BehaviorDisplay', () => {
	const WrappedBehaviorDisplay =
		withReferencedObjectsProvider(BehaviorDisplay);

	const mockSegment = data.getImmutableMock(Segment, data.mockSegment, 0, {
		referencedObjects: {
			assets: {
				123: {
					description: null,
					id: '123',
					name: 'Cool beans Page',
					type: 'Page',
					url: 'https://www.liferay.com'
				}
			}
		}
	});

	const mockCriterion = {
		operatorName: CustomFunctionOperators.ActivitiesFilterByCount,
		propertyName: 'activityKey',
		value: Map({
			criterionGroup: Map({
				items: List([
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'activityKey',
						value: 'Page#pageViewed#123'
					}),
					Map({
						operatorName: RelationalOperators.GT,
						propertyName: 'day',
						value: TimeSpans.Last24Hours
					})
				])
			}),
			operator: RelationalOperators.GE,
			value: 2
		})
	};

	const mockProperty = data.getImmutableMock(Property, data.mockProperty, 1, {
		entityName: 'Individual',
		label: 'Viewed Page',
		name: 'pageViewed',
		propertykey: 'web',
		type: PropertyTypes.Behavior
	});

	afterEach(cleanup);

	it('renders', () => {
		const {container, queryByText} = render(
			<WrappedBehaviorDisplay
				criterion={mockCriterion}
				property={mockProperty}
				segment={mockSegment}
				segmentType={SegmentTypes.Batch}
			/>
		);

		expect(container).toMatchSnapshot();
		expect(queryByText('Foo Attribute String')).toBeNull();
	});

	it('renders the account wording in an account segment', () => {
		const {getByText, queryByText} = render(
			<WrappedBehaviorDisplay
				criterion={mockCriterion}
				property={mockProperty}
				segment={mockSegment}
				segmentCategory={SegmentCategories.Account}
				segmentType={SegmentTypes.Batch}
			/>
		);

		expect(getByText('Account')).toBeTruthy();
		expect(getByText('at least one individual who triggered')).toBeTruthy();
		expect(queryByText('Individual')).toBeNull();
	});

	it('renders the attribute filter when an attribute/<id> criterion is present', () => {
		const mockSegmentWithAttribute = data.getImmutableMock(
			Segment,
			data.mockSegment,
			0,
			{
				referencedObjects: {
					assets: {
						123: {
							description: null,
							id: '123',
							name: 'Cool beans Page',
							type: 'Page',
							url: 'https://www.liferay.com'
						}
					},
					attributes: {
						2: {
							dataType: 'STRING',
							displayName: 'Foo Attribute String',
							id: '2'
						}
					}
				}
			}
		);

		const mockCriterionWithAttribute = {
			operatorName: CustomFunctionOperators.ActivitiesFilterByCount,
			propertyName: 'activityKey',
			value: Map({
				criterionGroup: Map({
					items: List([
						Map({
							operatorName: RelationalOperators.EQ,
							propertyName: 'activityKey',
							value: 'Page#pageViewed#123'
						}),
						Map({
							operatorName: FunctionalOperators.Contains,
							propertyName: 'attribute/2',
							value: 'Test'
						}),
						Map({
							operatorName: RelationalOperators.GT,
							propertyName: 'day',
							value: TimeSpans.Last24Hours
						})
					])
				}),
				operator: RelationalOperators.GE,
				value: 2
			})
		};

		const {queryByText} = render(
			<WrappedBehaviorDisplay
				criterion={mockCriterionWithAttribute}
				property={mockProperty}
				segment={mockSegmentWithAttribute}
				segmentType={SegmentTypes.Batch}
			/>
		);

		expect(queryByText('Foo Attribute String')).toBeTruthy();
	});

	const getObjectEntryCriterion = objectDefinitionName => ({
		operatorName: CustomFunctionOperators.ActivitiesFilterByCount,
		propertyName: 'activityKey',
		value: Map({
			criterionGroup: Map({
				items: List([
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'applicationId',
						value: 'ObjectEntry'
					}),
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'objectDefinitionName',
						value: objectDefinitionName
					}),
					Map({
						operatorName: RelationalOperators.GT,
						propertyName: 'day',
						value: TimeSpans.Last24Hours
					})
				])
			}),
			operator: RelationalOperators.GE,
			value: 2
		})
	});

	it('renders the label of the object definition of any asset of that type', async () => {
		const {findByText, queryByText} = render(
			<WrappedBehaviorDisplay
				criterion={getObjectEntryCriterion('C_ProductSheet')}
				property={mockProperty}
				segment={mockSegment}
				segmentType={SegmentTypes.Batch}
			/>
		);

		expect(await findByText('Product Sheet')).toBeInTheDocument();
		expect(queryByText('ObjectEntry')).toBeNull();
	});

	it('renders the name of the object definition when its label is unknown', async () => {
		const {findByText} = render(
			<WrappedBehaviorDisplay
				criterion={getObjectEntryCriterion('C_UnknownStructure')}
				property={mockProperty}
				segment={mockSegment}
				segmentType={SegmentTypes.Batch}
			/>
		);

		expect(await findByText('C_UnknownStructure')).toBeInTheDocument();
	});
});
