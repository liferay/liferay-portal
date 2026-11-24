import Card from '../Card';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('Card', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<Card />);
		expect(container).toMatchSnapshot();
	});

	it('should render horizontal', () => {
		const {container} = render(<Card horizontal />);
		expect(container.querySelector('.horizontal')).toBeTruthy();
	});
});
