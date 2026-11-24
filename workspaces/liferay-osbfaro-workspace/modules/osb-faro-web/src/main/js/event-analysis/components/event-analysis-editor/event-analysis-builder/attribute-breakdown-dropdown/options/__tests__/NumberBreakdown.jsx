import NumberBreakdown from '../NumberBreakdown';
import React from 'react';
import {render} from '@testing-library/react';

describe('NumberBreakdown', () => {
	it('should render', () => {
		const {container} = render(<NumberBreakdown onSubmit={jest.fn()} />);

		expect(container).toMatchSnapshot();
	});
});
