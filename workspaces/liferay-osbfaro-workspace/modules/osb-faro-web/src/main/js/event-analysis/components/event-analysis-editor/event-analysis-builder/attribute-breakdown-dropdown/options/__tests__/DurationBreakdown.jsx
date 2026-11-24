import DurationBreakdown from '../DurationBreakdown';
import React from 'react';
import {render} from '@testing-library/react';

describe('DurationBreakdown', () => {
	it('should render', () => {
		const {container} = render(<DurationBreakdown onSubmit={jest.fn()} />);

		expect(container).toMatchSnapshot();
	});
});
