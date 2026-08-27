import React from 'react';
import {beginDrag, CriteriaSidebarItem} from '../CriteriaSidebarItem';
import {cleanup, render} from '@testing-library/react';
import {every} from 'lodash';
import {PropertyTypes} from '../../utils/constants';
import {validateSegmentInputs} from '../../utils/utils';

const connectDnd = jest.fn(el => el);

jest.unmock('react-dom');

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
	});
});
