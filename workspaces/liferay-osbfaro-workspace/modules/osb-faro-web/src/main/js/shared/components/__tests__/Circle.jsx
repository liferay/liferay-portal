import Circle from '../Circle';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('Circle', () => {
	afterEach(cleanup);

	it('should render a Circle component', () => {
		const {container} = render(<Circle />);

		expect(container).toMatchSnapshot();
	});

	it('should render a Circle component with a red color', () => {
		const {container} = render(<Circle color='red' />);

		expect(container).toMatchSnapshot();
	});

	it('should render a Circle component with radius of 15px', () => {
		const {container} = render(<Circle size={15} />);

		expect(container).toMatchSnapshot();
	});
});
