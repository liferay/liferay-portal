import React from 'react';
import SegmentSticker from '../SegmentSticker';
import {render} from '@testing-library/react';
import {SegmentStates, SegmentTypes} from 'shared/util/constants';

jest.unmock('react-dom');

describe('SegmentSticker', () => {
	it('should render', () => {
		const {container} = render(
			<SegmentSticker segmentType={SegmentTypes.Batch} />
		);
		expect(container).toMatchSnapshot();
	});

	it('should render with a dynamic segment icon', () => {
		const {container} = render(
			<SegmentSticker segmentType={SegmentTypes.Batch} />
		);

		expect(container.querySelector('svg')).toHaveAttribute(
			'data-file',
			'individual_dynamic_segment.svg'
		);
	});

	it('should render with a disabled segment icon', () => {
		const {container} = render(
			<SegmentSticker state={SegmentStates.Disabled} />
		);

		expect(container.querySelector('use')).toHaveAttribute(
			'href',
			'#warning'
		);
	});
});
