import BehaviorInput from '../BehaviorInput';
import mockStore from 'test/mock-store';
import React from 'react';
import {
	ACTIVITY_KEY,
	FunctionalOperators,
	RelationalOperators
} from '../../utils/constants';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {Map} from 'immutable';
import {Property, Segment} from 'shared/util/records';
import {Provider} from 'react-redux';
import {ReferencedObjectsProvider} from '../../context/referencedObjects';
import {SegmentTypes} from 'shared/util/constants';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: () => ({data: null, error: false, loading: false})
}));

jest.mock('../components/AttributeFilterSection', () => (props) => (
	<div data-testid='attribute-filter-section'>
		<span data-testid='event-id'>{props.eventId}</span>
		<button
			onClick={() =>
				props.onChange({
					criterion: {value: 'new value'},
					touched: {attribute: true, attributeValue: true},
					valid: {attribute: true, attributeValue: true}
				})
			}
		>
			{'trigger-change'}
		</button>
	</div>
));

const mockValue = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
			{
				operatorName: RelationalOperators.EQ,
				propertyName: ACTIVITY_KEY,
				value: 'test#test#123123123'
			}
		]
	},
	{key: 'operator', value: RelationalOperators.GE},
	{key: 'value', value: ''}
]);

const defaultProps = {
	onChange: jest.fn(),
	operatorRenderer: () => <div>{'test'}</div>,
	property: new Property(),
	referencedAssetsIMap: new Map(),
	segmentType: SegmentTypes.Batch,
	touched: {asset: false, occurenceCount: false},
	valid: {asset: false, occurenceCount: false},
	value: mockValue
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<ReferencedObjectsProvider
			segment={
				new Segment({
					referencedObjects: new Map({
						assets: new Map({'123_title': 'test'})
					})
				})
			}
		>
			<BehaviorInput {...defaultProps} {...props} />
		</ReferencedObjectsProvider>
	</Provider>
);

describe('BehaviorInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<DefaultComponent />
		);

		fireEvent.click(getByText('at least'));
		fireEvent.click(getByText('ever'));

		expect(getAllByText('at least')[1]).toBeTruthy();
		expect(getByText('at most')).toBeTruthy();

		expect(getByText('since')).toBeTruthy();
		expect(getByText('before')).toBeTruthy();
		expect(getByText('between')).toBeTruthy();
		expect(getAllByText('ever')[1]).toBeTruthy();
		expect(getAllByText('on')[0]).toBeTruthy();

		expect(container).toMatchSnapshot();
	});

	it('should render w/ data', () => {
		const {container} = render(
			<DefaultComponent
				referencedAssetsIMap={
					new Map({
						assets: new Map({'123_title': 'test'})
					})
				}
				valid={{asset: true, occurenceCount: true}}
				value={mockValue.set('value', 123)}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with has-error for occurenceCount', () => {
		const {container} = render(
			<DefaultComponent touched={{asset: false, occurenceCount: true}} />
		);

		expect(
			container.querySelector('.form-group-item-shrink.has-error')
		).toBeTruthy();
	});

	describe('handlePageAssetSelect', () => {
		const renderWithRef = onChange => {
			const ref = React.createRef();

			render(
				<Provider store={mockStore()}>
					<ReferencedObjectsProvider segment={new Segment({})}>
						<BehaviorInput
							{...defaultProps}
							onChange={onChange}
							ref={ref}
						/>
					</ReferencedObjectsProvider>
				</Provider>
			);

			return ref;
		};

		it('should store a single selection as a flat activityKey item', () => {
			const onChange = jest.fn();
			const ref = renderWithRef(onChange);

			ref.current.handlePageAssetSelect({
				applicationId: 'Page',
				eventId: 'pageViewed',
				selections: [
					{
						activityKey: 'Page#pageViewed#p-1',
						id: 'p-1',
						name: 'Home'
					}
				]
			});

			const {valid, value} = onChange.mock.calls[0][0];
			const item = value.getIn(['criterionGroup', 'items', 0]);

			expect(item.get('propertyName')).toBe(ACTIVITY_KEY);
			expect(item.get('value')).toBe('Page#pageViewed#p-1');
			expect(valid.asset).toBe(true);
		});

		it('should store multiple selections as an "or" group of activityKey items', () => {
			const onChange = jest.fn();
			const ref = renderWithRef(onChange);

			ref.current.handlePageAssetSelect({
				applicationId: 'Page',
				eventId: 'pageViewed',
				selections: [
					{
						activityKey: 'Page#pageViewed#p-1',
						id: 'p-1',
						name: 'Home'
					},
					{
						activityKey: 'Page#pageViewed#p-2',
						id: 'p-2',
						name: 'About'
					}
				]
			});

			const group = onChange.mock.calls[0][0].value.getIn([
				'criterionGroup',
				'items',
				0
			]);

			expect(group.get('conjunctionName')).toBe('or');

			expect(
				group
					.get('items')
					.map(item => item.get('value'))
					.toArray()
			).toEqual(['Page#pageViewed#p-1', 'Page#pageViewed#p-2']);
		});

		it('should store a single-type applicationId/eventId filter when there is no specific selection', () => {
			const onChange = jest.fn();
			const ref = renderWithRef(onChange);

			ref.current.handlePageAssetSelect({
				applicationId: 'Document',
				eventId: 'documentDownloaded',
				selections: []
			});

			const {valid, value} = onChange.mock.calls[0][0];
			const items = value.getIn(['criterionGroup', 'items']);

			expect(items.get(0).get('propertyName')).toBe('applicationId');
			expect(items.get(0).get('value')).toBe('Document');
			expect(items.get(1).get('propertyName')).toBe('eventId');
			expect(items.get(1).get('value')).toBe('documentDownloaded');

			// A single type is a complete rule, so the criterion is valid.

			expect(valid.asset).toBe(true);
		});

		it('should clear the asset filter and mark it invalid when no type is selected', () => {
			const onChange = jest.fn();
			const ref = renderWithRef(onChange);

			ref.current.handlePageAssetSelect({
				applicationId: '',
				eventId: '',
				selections: []
			});

			const {valid, value} = onChange.mock.calls[0][0];

			const propertyNames = value
				.getIn(['criterionGroup', 'items'])
				.map(item => item.get('propertyName'))
				.toArray();

			// No asset type -> no applicationId/eventId/activityKey items, and the
			// asset validity is false so the segment cannot be saved yet.

			expect(propertyNames).not.toContain('applicationId');
			expect(propertyNames).not.toContain(ACTIVITY_KEY);
			expect(valid.asset).toBe(false);
		});
	});

	describe('attribute filter', () => {
		it('should show the "Add Event Attribute" button when there is no attribute criterion', () => {
			const {getByText, queryByTestId} = render(<DefaultComponent />);

			expect(getByText('Add Event Attribute')).toBeTruthy();
			expect(queryByTestId('attribute-filter-section')).toBeNull();
		});

		it('should reveal the attribute filter section when the button is clicked', () => {
			const {getByText, getByTestId, queryByText} = render(
				<DefaultComponent />
			);

			fireEvent.click(getByText('Add Event Attribute'));

			expect(getByTestId('attribute-filter-section')).toBeTruthy();
			expect(queryByText('Add Event Attribute')).toBeNull();
		});

		it('should initialize with the section shown when a real attribute criterion is already present', () => {
			const valueWithAttribute = createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: ACTIVITY_KEY,
							value: 'test#test#123123123'
						},
						{
							operatorName: FunctionalOperators.Contains,
							propertyName: 'attribute/2',
							value: 'foo'
						}
					]
				},
				{key: 'operator', value: RelationalOperators.GE},
				{key: 'value', value: ''}
			]);

			const {getByTestId, queryByText} = render(
				<DefaultComponent value={valueWithAttribute} />
			);

			expect(getByTestId('attribute-filter-section')).toBeTruthy();
			expect(queryByText('Add Event Attribute')).toBeNull();
		});

		it('should not reveal the section for only the seeded attribute/ placeholder', () => {
			const valueWithPlaceholder = createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: ACTIVITY_KEY,
							value: 'test#test#123123123'
						},
						{
							operatorName: FunctionalOperators.Contains,
							propertyName: 'attribute/',
							value: ''
						}
					]
				},
				{key: 'operator', value: RelationalOperators.GE},
				{key: 'value', value: ''}
			]);

			const {getByText, queryByTestId} = render(
				<DefaultComponent value={valueWithPlaceholder} />
			);

			expect(getByText('Add Event Attribute')).toBeTruthy();
			expect(queryByTestId('attribute-filter-section')).toBeNull();
		});

		describe('handleAttributeConjunctionChange', () => {
			const renderWithRef = (onChange, value) => {
				const ref = React.createRef();

				render(
					<Provider store={mockStore()}>
						<ReferencedObjectsProvider segment={new Segment({})}>
							<BehaviorInput
								{...defaultProps}
								onChange={onChange}
								ref={ref}
								value={value ?? defaultProps.value}
							/>
						</ReferencedObjectsProvider>
					</Provider>
				);

				return ref;
			};

			it('should merge the criterion into the existing attribute item', () => {
				const onChange = jest.fn();
				const valueWithAttribute = createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [
							{
								operatorName: RelationalOperators.EQ,
								propertyName: ACTIVITY_KEY,
								value: 'test#test#123123123'
							},
							{
								operatorName: FunctionalOperators.Contains,
								propertyName: 'attribute/2',
								value: 'foo'
							}
						]
					},
					{key: 'operator', value: RelationalOperators.GE},
					{key: 'value', value: ''}
				]);
				const ref = renderWithRef(onChange, valueWithAttribute);

				ref.current.handleAttributeConjunctionChange({
					criterion: {value: 'bar'},
					touched: {attribute: true, attributeValue: true},
					valid: {attribute: true, attributeValue: true}
				});

				const {touched, valid, value} = onChange.mock.calls[0][0];
				const items = value.getIn(['criterionGroup', 'items']);

				expect(items.get(1).get('propertyName')).toBe('attribute/2');
				expect(items.get(1).get('value')).toBe('bar');
				expect(touched.attribute).toBe(true);
				expect(valid.attributeValue).toBe(true);
			});

			it('should append a new attribute item when none exists yet', () => {
				const onChange = jest.fn();
				const ref = renderWithRef(onChange);

				ref.current.handleAttributeConjunctionChange({
					criterion: {
						operatorName: FunctionalOperators.Contains,
						propertyName: 'attribute/5',
						value: 'baz'
					},
					touched: {attribute: true, attributeValue: false},
					valid: {attribute: true, attributeValue: false}
				});

				const {value} = onChange.mock.calls[0][0];
				const items = value.getIn(['criterionGroup', 'items']);
				const lastItem = items.get(items.size - 1);

				expect(lastItem.get('propertyName')).toBe('attribute/5');
				expect(lastItem.get('value')).toBe('baz');
			});
		});
	});
});
