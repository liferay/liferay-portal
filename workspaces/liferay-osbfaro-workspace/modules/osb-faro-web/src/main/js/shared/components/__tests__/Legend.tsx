import Legend from '../Legend';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

const data = [
	{
		color: '#FFFFFF',
		name: 'Legend 1',
	},
	{
		color: '#EEEEEE',
		name: 'Legend 2',
	},
	{
		color: '#C9C9C9',
		name: 'Legend 3',
	},
];

describe('Legend', () => {
	afterEach(cleanup);

	it('should render a Legend component', () => {
		const {container} = render(<Legend data={data} />);

		expect(container).toMatchSnapshot();
	});
});
