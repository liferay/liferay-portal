import IntervalSelector from 'shared/components/IntervalSelector';
import React from 'react';
import withInterval from '../WithInterval';
import {cleanup, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('WithInterval', () => {
	afterEach(cleanup);

	it('should render the original component', () => {
		const WrappedComponent = withInterval(() => 'wrapped component text');

		const {container} = render(<WrappedComponent />);

		expect(container.textContent).toBe('wrapped component text');
	});

	it('should pass interval as a prop to the wrapped component', () => {
		const WrappedComponent = withInterval(IntervalSelector);

		const {getByText} = render(<WrappedComponent activeInterval='W' />);

		expect(getByText(/^w$/i).classList.contains('active')).toBe(true);
	});
});
