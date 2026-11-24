import Edit from '../Edit';
import React from 'react';
import {render, screen} from '@testing-library/react';
import {SegmentTypes} from 'shared/util/constants';

jest.mock('../edit/Dynamic', () => ({
	__esModule: true,
	default: ({type}: {type: string}) => (
		<div data-testid="dynamic-segment" data-type={type} />
	),
}));

describe('Edit', () => {
	it('should render', () => {
		render(<Edit groupId="23" />);

		expect(screen.getByTestId('dynamic-segment')).toBeInTheDocument();
	});

	it('should render a dynamic segment', () => {
		render(<Edit groupId="23" type={SegmentTypes.Batch} />);

		const dynamicSegment = screen.getByTestId('dynamic-segment');

		expect(dynamicSegment).toBeInTheDocument();
		expect(dynamicSegment).toHaveAttribute('data-type', SegmentTypes.Batch);
	});
});
