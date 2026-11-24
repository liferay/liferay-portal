import React from 'react';
import {beginDrag, CriteriaSidebarItem} from '../CriteriaSidebarItem';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {KeyboardMovementContext} from '../../context/keyboardMovement';
import {every} from 'lodash';
import {PropertyTypes} from '../../utils/constants';
import {validateSegmentInputs} from '../../utils/utils';

const connectDnd = jest.fn(el => el);

describe('CriteriaSidebarItem', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<CriteriaSidebarItem
				connectDragSource={connectDnd}
				label='Page Views'
				propertyKey='user'
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders the property type icon inside a sticker without a drag handle', () => {
		const {container} = render(
			<CriteriaSidebarItem
				connectDragSource={connectDnd}
				label='Click'
				propertyKey='event'
				type={PropertyTypes.Event}
			/>
		);

		const sticker = container.querySelector('.sticker');

		expect(sticker.querySelector('.lexicon-icon-click')).toBeTruthy();
		expect(container.querySelector('.lexicon-icon-drag')).toBeNull();
	});

	it('shows a campaign with the same check icon as an interest', () => {
		const {container} = render(
			<CriteriaSidebarItem
				connectDragSource={connectDnd}
				label='Spring Sale'
				propertyKey='campaign'
				type={PropertyTypes.Campaign}
			/>
		);

		expect(
			container.querySelector('.sticker .lexicon-icon-check')
		).toBeTruthy();
	});

	it('marks the item as dragging while it is being dragged', () => {
		const {container} = render(
			<CriteriaSidebarItem
				connectDragSource={connectDnd}
				dragging
				label='Click'
				propertyKey='event'
				type={PropertyTypes.Event}
			/>
		);

		expect(
			container.querySelector('.criteria-sidebar-item-root.dragging')
		).toBeTruthy();
	});

	it('exposes the item as a menu item labelled with its add action', () => {
		render(
			<CriteriaSidebarItem
				connectDragSource={connectDnd}
				label='Click'
				propertyKey='event'
				type={PropertyTypes.Event}
			/>
		);

		const item = screen.getByRole('menuitem', {name: 'Add Click'});

		expect(item).toHaveAttribute('tabindex', '-1');
	});

	it.each(['Enter', ' '])(
		'starts moving the criterion with the keyboard when "%s" is pressed',
		key => {
			const startMovement = jest.fn();

			render(
				<KeyboardMovementContext.Provider
					value={{source: null, startMovement, target: null}}
				>
					<CriteriaSidebarItem
						connectDragSource={connectDnd}
						defaultValue={{}}
						label='Click'
						name='click'
						property={{label: 'Click'}}
						propertyKey='event'
						type={PropertyTypes.Event}
					/>
				</KeyboardMovementContext.Provider>
			);

			fireEvent.keyDown(screen.getByRole('menuitem'), {key});

			expect(startMovement).toHaveBeenCalledTimes(1);

			const [{criterion, property}] = startMovement.mock.calls[0];

			expect(criterion.propertyName).toBe('click');
			expect(criterion.type).toBe(PropertyTypes.Event);
			expect(property).toEqual({label: 'Click'});
		}
	);

	it('replaces the native drag image with an empty image', () => {
		const connectDragPreview = jest.fn();

		render(
			<CriteriaSidebarItem
				connectDragPreview={connectDragPreview}
				connectDragSource={connectDnd}
				label='Click'
				propertyKey='event'
				type={PropertyTypes.Event}
			/>
		);

		expect(connectDragPreview).toHaveBeenCalledWith(
			expect.any(Image),
			{captureDraggingState: true}
		);
	});

	describe('beginDrag', () => {
		it('should not seed an invalid attributeValue flag for an Event criterion', () => {
			const {criterion} = beginDrag({
				defaultValue: {},
				name: 'blogViewed',
				property: {},
				type: PropertyTypes.Event
			});

			expect(every(criterion.valid, Boolean)).toBe(true);
			expect(validateSegmentInputs(criterion)).toBe(true);
		});

		it('should seed a Behavior criterion invalid until an asset type is chosen', () => {
			const {criterion} = beginDrag({
				defaultValue: {},
				name: 'download',
				property: {},
				type: PropertyTypes.Behavior
			});

			// The asset flag starts invalid so Save stays disabled until the
			// user picks a type; every other flag is seeded valid.

			expect(criterion.valid.asset).toBe(false);
			expect(criterion.valid.dateFilter).toBe(true);
			expect(criterion.valid.occurenceCount).toBe(true);

			expect(validateSegmentInputs(criterion)).toBe(false);
		});

		it('should seed a Channel criterion valid, with only a customInput flag and no dateFilter flag', () => {
			const {criterion} = beginDrag({
				defaultValue: {},
				name: 'context/channel',
				property: {},
				type: PropertyTypes.SessionChannel
			});

			// Channel's default value is already a concrete option, so the
			// criterion starts valid and does not block Save.

			expect(criterion.touched).toEqual({customInput: false});
			expect(criterion.valid).toEqual({customInput: true});
			expect(validateSegmentInputs(criterion)).toBe(true);
		});

		it('should seed a UTM Parameter criterion with only a customInput flag and no dateFilter flag', () => {
			const {criterion} = beginDrag({
				defaultValue: {},
				name: 'attribute/utmParameter',
				property: {},
				type: PropertyTypes.SessionUtmParameter
			});

			expect(criterion.touched).toEqual({customInput: false});
			expect(criterion.valid).toEqual({customInput: false});
			expect(validateSegmentInputs(criterion)).toBe(false);
		});

		it('should seed a Campaign criterion valid when the dragged campaign carries its ID', () => {
			const {criterion} = beginDrag({
				defaultValue: {},
				name: 'id',
				property: {id: 'c1'},
				type: PropertyTypes.Campaign
			});

			expect(criterion.valid).toBe(true);
			expect(validateSegmentInputs(criterion)).toBe(true);
		});

		it('should seed a Campaign criterion invalid when no campaign is set', () => {
			const {criterion} = beginDrag({
				defaultValue: {},
				name: 'id',
				property: {},
				type: PropertyTypes.Campaign
			});

			expect(criterion.valid).toBe(false);
			expect(validateSegmentInputs(criterion)).toBe(false);
		});

		it('should seed a Search Term criterion valid, since the term is already chosen on drop', () => {
			const {criterion} = beginDrag({
				defaultValue: {},
				name: 'shoes',
				property: {},
				type: PropertyTypes.SearchTerm
			});

			expect(criterion.valid).toBe(true);
			expect(validateSegmentInputs(criterion)).toBe(true);
		});
	});
});
