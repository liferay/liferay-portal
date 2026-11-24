import CriteriaGroup from '../CriteriaGroup';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {Conjunctions} from '../../utils/constants';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {selectPickerOption} from 'test/helpers';

jest.mock('../Conjunction', () => () => <div />);
jest.mock(
	'../CriteriaRow',
	() => props =>
		props.stepNumber !== undefined ? (
			<span className='criteria-step-number'>{props.stepNumber}</span>
		) : (
			<div />
		)
);
jest.mock('../DropZone', () => props => (
	<div data-disabled={String(!!props.disabled)} data-testid='drop-zone' />
));

const LIMIT_MESSAGE =
	'The maximum number of sequential criteria has been reached.';
const EXCEEDED_MESSAGE =
	'The maximum number of sequential criteria has been exceeded. Remove items to save the segment.';
const OR_LIMIT_MESSAGE =
	'The maximum number of OR conditions has been reached.';
const OR_EXCEEDED_MESSAGE =
	'The maximum number of OR conditions has been exceeded. Remove items to save the segment.';

const ALL_MESSAGES = [
	EXCEEDED_MESSAGE,
	LIMIT_MESSAGE,
	OR_EXCEEDED_MESSAGE,
	OR_LIMIT_MESSAGE
];

const makeCriteria = (conjunction, count) => ({
	conjunctionName: conjunction,
	criteriaGroupId: 'group',
	items: Array.from({length: count}, (_, i) => ({rowId: `r${i}`}))
});

const renderGroup = ({conjunction, count, root, sequential}) =>
	render(
		<DndProvider backend={HTML5Backend}>
			<CriteriaGroup
				criteria={makeCriteria(conjunction, count)}
				criteriaGroupId='group'
				onChange={() => {}}
				onMove={() => {}}
				root={root}
				sequential={sequential}
			/>
		</DndProvider>
	);

describe('CriteriaGroup', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup />
			</DndProvider>
		);

		expect(container).toMatchSnapshot();
	});

	it.each`
		conjunction         | count | root     | sequential | expectedMessage        | expectedDropDisabled
		${Conjunctions.And} | ${5}  | ${true}  | ${true}    | ${LIMIT_MESSAGE}       | ${'true'}
		${Conjunctions.And} | ${6}  | ${true}  | ${true}    | ${EXCEEDED_MESSAGE}    | ${'true'}
		${Conjunctions.Or}  | ${3}  | ${false} | ${true}    | ${OR_LIMIT_MESSAGE}    | ${'true'}
		${Conjunctions.Or}  | ${4}  | ${false} | ${true}    | ${OR_EXCEEDED_MESSAGE} | ${'true'}
		${Conjunctions.And} | ${4}  | ${true}  | ${true}    | ${null}                | ${'false'}
		${Conjunctions.Or}  | ${2}  | ${false} | ${true}    | ${null}                | ${'false'}
		${Conjunctions.And} | ${5}  | ${true}  | ${false}   | ${null}                | ${'false'}
		${Conjunctions.Or}  | ${5}  | ${false} | ${false}   | ${null}                | ${'false'}
	`(
		'renders $expectedMessage and disabled=$expectedDropDisabled for $conjunction with $count items (root=$root, sequential=$sequential)',
		({
			conjunction,
			count,
			expectedDropDisabled,
			expectedMessage,
			root,
			sequential
		}) => {
			renderGroup({conjunction, count, root, sequential});

			if (expectedMessage) {
				expect(screen.getByText(expectedMessage)).toBeInTheDocument();

				ALL_MESSAGES.filter(m => m !== expectedMessage).forEach(m => {
					expect(screen.queryByText(m)).not.toBeInTheDocument();
				});
			} else {
				ALL_MESSAGES.forEach(m => {
					expect(screen.queryByText(m)).not.toBeInTheDocument();
				});
			}

			const dropZones = screen.getAllByTestId('drop-zone');

			expect(dropZones.length).toBeGreaterThan(0);
			expect(
				dropZones.every(
					dz => dz.dataset.disabled === expectedDropDisabled
				)
			).toBeTrue();
		}
	);

	it.each`
		scenario                   | count | sequential
		${'has a single item'}     | ${1}  | ${false}
		${'is a sequential group'} | ${3}  | ${true}
	`(
		'should not render the group operator picker when the group $scenario',
		({count, sequential}) => {
			renderGroup({
				conjunction: Conjunctions.And,
				count,
				root: true,
				sequential
			});

			expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
			expect(screen.queryByRole('group')).not.toBeInTheDocument();
		}
	);

	it('should make each nested group announce its own operator', () => {
		const rootCriteria = {
			conjunctionName: Conjunctions.And,
			criteriaGroupId: 'root',
			items: [
				{rowId: 'r0', valid: true},
				{
					conjunctionName: Conjunctions.Or,
					criteriaGroupId: 'nested',
					items: [
						{rowId: 'n0', valid: true},
						{rowId: 'n1', valid: true}
					]
				}
			]
		};

		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup
					criteria={rootCriteria}
					criteriaGroupId='root'
					onChange={() => {}}
					onMove={() => {}}
					root
				/>
			</DndProvider>
		);

		expect(
			screen.getByRole('group', {
				name: /^all of these criteria must be met\.$/i
			})
		).toBeInTheDocument();
		expect(
			screen.getByRole('group', {
				name: /^any of these criteria must be met\.$/i
			})
		).toBeInTheDocument();
	});

	it('should change the conjunction of the group when another match is chosen', () => {
		const onChange = jest.fn();

		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup
					criteria={makeCriteria(Conjunctions.And, 2)}
					criteriaGroupId='group'
					onChange={onChange}
					onMove={() => {}}
					root
				/>
			</DndProvider>
		);

		selectPickerOption(/^match logic$/i, /^any$/i);

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({conjunctionName: Conjunctions.Or})
		);
	});

	it('should render numbered step stickers on each top-level criterion when root and sequential', () => {
		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup
					criteria={makeCriteria(Conjunctions.And, 3)}
					criteriaGroupId='group'
					onChange={() => {}}
					onMove={() => {}}
					root
					sequential
				/>
			</DndProvider>
		);

		const stickers = container.querySelectorAll('.criteria-step-number');

		expect(stickers.length).toBe(3);
		expect(Array.from(stickers).map(s => s.textContent)).toEqual([
			'1',
			'2',
			'3'
		]);
	});

	it('should not render step stickers on a nested group', () => {
		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup
					criteria={makeCriteria(Conjunctions.And, 3)}
					criteriaGroupId='group'
					onChange={() => {}}
					onMove={() => {}}
					root={false}
					sequential
				/>
			</DndProvider>
		);

		expect(container.querySelectorAll('.criteria-step-number').length).toBe(
			0
		);
	});

	it('should not render step stickers when sequential is enabled but only one top-level criterion exists', () => {
		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup
					criteria={makeCriteria(Conjunctions.And, 1)}
					criteriaGroupId='group'
					onChange={() => {}}
					onMove={() => {}}
					root
					sequential
				/>
			</DndProvider>
		);

		expect(container.querySelectorAll('.criteria-step-number').length).toBe(
			0
		);
	});

	it('should not render step stickers when sequential is disabled', () => {
		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup
					criteria={makeCriteria(Conjunctions.And, 3)}
					criteriaGroupId='group'
					onChange={() => {}}
					onMove={() => {}}
					root
					sequential={false}
				/>
			</DndProvider>
		);

		expect(container.querySelectorAll('.criteria-step-number').length).toBe(
			0
		);
	});

	it('should propagate the parent step number to each row inside a nested top-level group', () => {
		const nestedGroup = {
			conjunctionName: Conjunctions.Or,
			criteriaGroupId: 'nested',
			items: [
				{rowId: 'n0', valid: true},
				{rowId: 'n1', valid: true}
			]
		};

		const rootCriteria = {
			conjunctionName: Conjunctions.And,
			criteriaGroupId: 'root',
			items: [
				{rowId: 'r0', valid: true},
				nestedGroup,
				{rowId: 'r2', valid: true}
			]
		};

		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup
					criteria={rootCriteria}
					criteriaGroupId='root'
					onChange={() => {}}
					onMove={() => {}}
					root
					sequential
				/>
			</DndProvider>
		);

		const stickers = container.querySelectorAll('.criteria-step-number');

		expect(Array.from(stickers).map(s => s.textContent)).toEqual([
			'1',
			'2',
			'2',
			'3'
		]);
	});

	it('should render both alerts when the root hits the sequential limit and contains a nested OR at its limit', () => {
		const nestedOrGroup = {
			conjunctionName: Conjunctions.Or,
			criteriaGroupId: 'nested-or',
			items: [
				{rowId: 'n0', valid: true},
				{rowId: 'n1', valid: true},
				{rowId: 'n2', valid: true}
			]
		};

		const rootWithNestedOr = {
			conjunctionName: Conjunctions.And,
			criteriaGroupId: 'root',
			items: [
				{rowId: 'r0', valid: true},
				nestedOrGroup,
				{rowId: 'r2', valid: true},
				{rowId: 'r3', valid: true},
				{rowId: 'r4', valid: true}
			]
		};

		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaGroup
					criteria={rootWithNestedOr}
					criteriaGroupId='root'
					onChange={() => {}}
					onMove={() => {}}
					root
					sequential
				/>
			</DndProvider>
		);

		expect(screen.getByText(LIMIT_MESSAGE)).toBeInTheDocument();
		expect(screen.getByText(OR_LIMIT_MESSAGE)).toBeInTheDocument();
	});
});
