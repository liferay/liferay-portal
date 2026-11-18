import EmptyDropZone, {
	EmptyDropZone as UndecoratedEmptyDropZone,
} from '../EmptyDropZone';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

const connectDnd = jest.fn((element) => element);

jest.unmock('react-dom');

describe('EmptyDropZone', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<EmptyDropZone />
			</DndProvider>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render the canvas empty state while nothing is dragged', () => {
		const {container, getByText} = render(
			<DndProvider backend={HTML5Backend}>
				<EmptyDropZone />
			</DndProvider>
		);

		expect(
			container.querySelector(
				'.empty-drop-zone-target .canvas-empty-state-idle'
			)
		).toBeInTheDocument();
		expect(getByText(/^no conditions yet$/i)).toBeInTheDocument();
		expect(
			getByText(
				/^to create a new segment, drag items from the conditions library and drop them here\.$/i
			)
		).toBeInTheDocument();
	});

	it.each([
		[false, false, 'idle'],
		[false, true, 'idle'],
		[true, false, 'dragging'],
		[true, true, 'over'],
	])(
		'should map canDrop %s and hover %s to the %s drop state',
		(canDrop, hover, dropState) => {
			const {container} = render(
				<UndecoratedEmptyDropZone
					canDrop={canDrop}
					connectDropTarget={connectDnd}
					hover={hover}
				/>
			);

			expect(
				container.querySelector(
					`.empty-drop-zone-target .canvas-empty-state-${dropState}`
				)
			).toBeInTheDocument();
		}
	);
});
