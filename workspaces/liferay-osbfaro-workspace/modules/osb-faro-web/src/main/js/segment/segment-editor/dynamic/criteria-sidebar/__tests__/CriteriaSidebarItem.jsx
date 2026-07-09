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

		it('should not seed an invalid attribute flag for a Behavior criterion', () => {
			const {criterion} = beginDrag({
				defaultValue: {},
				name: 'download',
				property: {},
				type: PropertyTypes.Behavior
			});

			expect(every(criterion.valid, Boolean)).toBe(true);
			expect(validateSegmentInputs(criterion)).toBe(true);
		});
	});
});
