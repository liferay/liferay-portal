import CriteriaDragPreview from '../CriteriaDragPreview';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {DragTypes} from '../../utils/drag-types';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../../utils/constants';
import {useDragLayer} from 'react-dnd';

jest.mock('react-dnd', () => ({
	...jest.requireActual('react-dnd'),
	useDragLayer: jest.fn()
}));

const property = new Property({
	label: 'Click',
	name: 'click',
	propertyKey: 'event',
	type: PropertyTypes.Event
});

const mockDragLayer = state => {
	const monitor = {
		currentOffset: {x: 100, y: 200},
		isDragging: true,
		item: {property},
		itemType: DragTypes.Property,
		...state
	};

	useDragLayer.mockImplementation(collect =>
		collect({
			getClientOffset: () => monitor.currentOffset,
			getItem: () => monitor.item,
			getItemType: () => monitor.itemType,
			isDragging: () => monitor.isDragging
		})
	);
};

describe('CriteriaDragPreview', () => {
	afterEach(cleanup);

	it('renders the dragged property with its sticker at the pointer', () => {
		mockDragLayer();

		render(<CriteriaDragPreview />);

		const preview = screen.getByTestId('criteria-drag-preview');

		const sticker = preview.querySelector('.sticker');

		expect(preview).toHaveTextContent(/click/i);
		expect(sticker.querySelector('.lexicon-icon-click')).toBeTruthy();
		expect(preview.style.transform).toContain('translate(100px, 200px)');
	});

	it('renders nothing when no property is being dragged', () => {
		mockDragLayer({isDragging: false});

		render(<CriteriaDragPreview />);

		expect(screen.queryByTestId('criteria-drag-preview')).toBeNull();
	});

	it('renders nothing when a criteria row is being dragged', () => {
		mockDragLayer({itemType: DragTypes.CriteriaRow});

		render(<CriteriaDragPreview />);

		expect(screen.queryByTestId('criteria-drag-preview')).toBeNull();
	});
});
