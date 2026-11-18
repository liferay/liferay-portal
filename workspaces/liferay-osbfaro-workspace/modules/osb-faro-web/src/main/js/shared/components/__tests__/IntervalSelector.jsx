import IntervalSelector from '../IntervalSelector';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('IntervalSelector', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<IntervalSelector onChange={jest.fn()} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with an active item', () => {
		const {getByText} = render(
			<IntervalSelector activeInterval='W' onChange={jest.fn()} />
		);

		expect(getByText(/^w$/i)).not.toHaveClass('.active');
	});

	it('should call the onChange callback when an interval is clicked', () => {
		const spy = jest.fn();
		const {getByText} = render(<IntervalSelector onChange={spy} />);

		fireEvent.click(getByText(/^m$/i));

		expect(spy).toHaveBeenCalled();
	});

	it('should render as disabled', () => {
		const {container} = render(
			<IntervalSelector disabled onChange={jest.fn()} />
		);

		container.querySelectorAll('button').forEach(el => {
			expect(el).toBeDisabled();
		});
	});
});
