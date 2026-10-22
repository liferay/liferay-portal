import DataValue from '../DataValue';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('DataValue', () => {
	afterEach(cleanup);

	it.each([null, undefined, ''])(
		'should render the no data value in the secondary text color for %p',
		(value) => {
			render(<DataValue value={value} />);

			expect(screen.getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([NaN, Infinity])(
		'should render the no data value for a non-finite number %p',
		(value) => {
			render(<DataValue type="number" value={value} />);

			expect(screen.getByText('–')).toHaveClass('text-secondary');
		}
	);

	it('should render a real zero', () => {
		render(<DataValue type="count" value={0} />);

		expect(screen.getByText('0')).toBeInTheDocument();
	});

	it('should render the processing label while the value is processing', () => {
		render(<DataValue processing type="count" value={0} />);

		expect(screen.getByText('Processing')).toBeInTheDocument();
		expect(screen.queryByText('0')).not.toBeInTheDocument();
	});

	it('should render the value with a custom format', () => {
		render(<DataValue format={(value) => `${value} items`} value={3} />);

		expect(screen.getByText('3 items')).toBeInTheDocument();
	});

	it('should not call the custom format for a missing value', () => {
		const format = jest.fn();

		render(<DataValue format={format} value={null} />);

		expect(format).not.toHaveBeenCalled();
	});
});
