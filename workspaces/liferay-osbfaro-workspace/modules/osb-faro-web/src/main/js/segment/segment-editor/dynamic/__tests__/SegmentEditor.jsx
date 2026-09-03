import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import SegmentEditor, {validateSegmentEditor} from '../index';
import {BrowserRouter} from 'react-router-dom';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor
} from '@testing-library/react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {Provider} from 'react-redux';
import {Segment} from 'shared/util/records';
import {SegmentStates} from 'shared/util/constants';
import {useBlocker} from 'react-router-dom';

jest.mock('segment/segment-editor/dynamic/criteria-sidebar/index');

jest.mock('uuid', () => ({
	v4: () => '00000000-0000-0000-0000-000000000000'
}));

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: jest.fn(() => ({state: 'unblocked'})),
}));

/**
 * Runs the guard the editor handed to `useBlocker` on its latest render,
 * answering whether the editor would stop the user from navigating away.
 */
function blocksNavigation() {
	const {calls} = useBlocker.mock;

	const shouldBlock = calls[calls.length - 1][0];

	return shouldBlock({
		currentLocation: {pathname: '/segments/create'},
		nextLocation: {pathname: '/event-analysis'}
	});
}

describe('SegmentEditor', () => {
	afterEach(cleanup);

	beforeEach(() => {
		useBlocker.mockClear();
	});

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DndProvider backend={HTML5Backend}>
						<SegmentEditor
							channelId='321'
							groupId='23'
							type='BATCH'
						/>
					</DndProvider>
				</BrowserRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with error message', () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DndProvider backend={HTML5Backend}>
						<SegmentEditor
							channelId='321'
							groupId='23'
							segment={data.getImmutableMock(
								Segment,
								data.mockSegment,
								123,
								{
									state: SegmentStates.Disabled
								}
							)}
							type='BATCH'
						/>
					</DndProvider>
				</BrowserRouter>
			</Provider>
		);

		expect(screen.getByText('Error:')).not.toBeNull();
	});

	it('renders the realtime segment with sequential card disabled', () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DndProvider backend={HTML5Backend}>
						<SegmentEditor
							channelId='321'
							groupId='23'
							type='REAL_TIME'
						/>
					</DndProvider>
				</BrowserRouter>
			</Provider>
		);

		expect(screen.getByText('Order')).toBeInTheDocument();
		expect(screen.getByTestId('toggle-switch-input')).toBeInTheDocument();
		expect(
			screen.getByText(
				'When this is enabled, the second event must come after the first event, with any number of events in between. When this is disabled, events can be completed in any order.'
			)
		).toBeInTheDocument();

		expect(
			screen.getByText(
				'Drag and drop criterion from the right to add rules.'
			)
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'Drag and drop over an existing criteria to form groups.'
			)
		).toBeInTheDocument();
	});

	it('renders the realtime segment with sequential card and user enable it', async () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DndProvider backend={HTML5Backend}>
						<SegmentEditor
							channelId='321'
							groupId='23'
							type='REAL_TIME'
						/>
					</DndProvider>
				</BrowserRouter>
			</Provider>
		);

		expect(screen.getByText('Order')).toBeInTheDocument();
		expect(screen.getByTestId('toggle-switch-input')).toBeInTheDocument();

		expect(
			screen.getByText(
				'When this is enabled, the second event must come after the first event, with any number of events in between. When this is disabled, events can be completed in any order.'
			)
		).toBeInTheDocument();

		fireEvent.click(screen.getByTestId('toggle-switch-input'));

		await waitFor(() => {
			expect(
				screen.queryByText(
					'Drag and drop criterion from the right to add rules.'
				)
			).toBeInTheDocument();

			expect(
				screen.queryByText(
					'Drag and drop over an existing criteria to form groups.'
				)
			).not.toBeInTheDocument();
		});
	});

	it('shows the Segment ERC popover with the description and the slug rule', () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DndProvider backend={HTML5Backend}>
						<SegmentEditor
							channelId='321'
							groupId='23'
							type='BATCH'
						/>
					</DndProvider>
				</BrowserRouter>
			</Provider>
		);

		expect(
			screen.getByText(
				'Unique key for referencing the segment definition.'
			)
		).toBeInTheDocument();

		expect(
			screen.getByText(
				'ERC must contain only lowercase letters, numbers, hyphens, and underscores.'
			)
		).toBeInTheDocument();
	});

	// A new segment carries no external reference code, so the form seeds the
	// field with a generated one. Measuring changes against the raw segment made
	// an untouched form look dirty and armed the unsaved changes guard on a
	// creation page nobody had touched yet.

	it('should let the user leave an untouched creation form', () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DndProvider backend={HTML5Backend}>
						<SegmentEditor
							channelId='321'
							groupId='23'
							type='BATCH'
						/>
					</DndProvider>
				</BrowserRouter>
			</Provider>
		);

		expect(blocksNavigation()).toBe(false);
	});

	it('should warn before leaving once the segment name changes', () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DndProvider backend={HTML5Backend}>
						<SegmentEditor
							channelId='321'
							groupId='23'
							type='BATCH'
						/>
					</DndProvider>
				</BrowserRouter>
			</Provider>
		);

		fireEvent.change(screen.getByPlaceholderText('Unnamed Segment'), {
			target: {value: 'Engaged Accounts'}
		});

		expect(blocksNavigation()).toBe(true);
	});

	it('should let the user leave an untouched edit form', () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DndProvider backend={HTML5Backend}>
						<SegmentEditor
							channelId='321'
							groupId='23'
							id='123'
							segment={data.getImmutableMock(
								Segment,
								data.mockSegment,
								123,
								{externalReferenceCode: 'engaged-accounts'}
							)}
							type='BATCH'
						/>
					</DndProvider>
				</BrowserRouter>
			</Provider>
		);

		expect(blocksNavigation()).toBe(false);
	});
});

describe('validateSegmentEditor', () => {
	const emptyCriteria = {items: []};
	const criteriaWithItems = {items: [{valid: true}]};
	const criteriaWithInvalidItems = {items: [{valid: false}, {valid: true}]};
	const criteriaWithItemsObject = {
		items: [{valid: {bar: true, foo: true}}]
	};
	const criteriaWithInvalidItemsObject = {
		items: [{valid: {bar: false, foo: false}}]
	};
	const criteriaWith5Items = data.mockNewCriteria(5, {valid: true});
	const criteriaWith6Items = data.mockNewCriteria(6, {valid: true});

	it.each`
		criteria                          | error
		${null}                           | ${'Empty Fields'}
		${undefined}                      | ${'Empty Fields'}
		${emptyCriteria}                  | ${'Empty Fields'}
		${criteriaWithInvalidItems}       | ${'Empty Fields'}
		${criteriaWithInvalidItemsObject} | ${'Empty Fields'}
		${criteriaWithItemsObject}        | ${undefined}
		${criteriaWithItems}              | ${undefined}
	`('should return $error for $criteria', ({criteria, error}) => {
		expect(validateSegmentEditor(criteria)).toBe(error);
	});

	it('should block save when sequential is enabled and criteria exceed the limit', () => {
		expect(validateSegmentEditor(criteriaWith6Items, true)).toBe(
			'The maximum number of sequential criteria has been exceeded. Remove items to save the segment.'
		);
	});

	it('should allow save when sequential is enabled and criteria are at the limit', () => {
		expect(validateSegmentEditor(criteriaWith5Items, true)).toBe(undefined);
	});

	it('should allow save when criteria exceed the limit but sequential is disabled', () => {
		expect(validateSegmentEditor(criteriaWith6Items, false)).toBe(
			undefined
		);
	});

	it('should block save when sequential is enabled and a nested OR group has more than 3 items', () => {
		const criteriaWithExceededNestedOr = {
			conjunctionName: 'and',
			criteriaGroupId: 'root',
			items: [
				{
					conjunctionName: 'or',
					criteriaGroupId: 'nested',
					items: [
						{rowId: 'r0', valid: true},
						{rowId: 'r1', valid: true},
						{rowId: 'r2', valid: true},
						{rowId: 'r3', valid: true}
					]
				}
			]
		};

		expect(validateSegmentEditor(criteriaWithExceededNestedOr, true)).toBe(
			'The maximum number of OR conditions has been exceeded. Remove items to save the segment.'
		);
	});

	it('should prioritize the OR error when both the root AND and a nested OR exceed their limits', () => {
		const both = {
			conjunctionName: 'and',
			criteriaGroupId: 'root',
			items: Array.from({length: 6}, (_, i) => ({
				rowId: `r${i}`,
				valid: true
			})).concat({
				conjunctionName: 'or',
				criteriaGroupId: 'nested',
				items: Array.from({length: 4}, (_, i) => ({
					rowId: `n${i}`,
					valid: true
				}))
			})
		};

		expect(validateSegmentEditor(both, true)).toBe(
			'The maximum number of OR conditions has been exceeded. Remove items to save the segment.'
		);
	});

	it('should allow save when a nested OR group exceeds the limit but sequential is disabled', () => {
		const criteriaWithExceededNestedOr = {
			conjunctionName: 'and',
			criteriaGroupId: 'root',
			items: [
				{
					conjunctionName: 'or',
					criteriaGroupId: 'nested',
					items: [
						{rowId: 'r0', valid: true},
						{rowId: 'r1', valid: true},
						{rowId: 'r2', valid: true},
						{rowId: 'r3', valid: true}
					]
				}
			]
		};

		expect(validateSegmentEditor(criteriaWithExceededNestedOr, false)).toBe(
			undefined
		);
	});
});
