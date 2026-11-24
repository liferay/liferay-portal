import optional from '../Optional';
import React from 'react';
import {render} from '@testing-library/react';

describe('Optional', () => {
	it('should render the original component', () => {
		const hoc = jest.fn(() => 'hoc component');
		const Optional = optional(hoc)(jest.fn(() => 'wrapped component'));

		const {queryByText} = render(<Optional id={null} />);

		expect(queryByText('hoc component')).toBeNull();
		expect(queryByText('wrapped component')).toBeTruthy();
	});

	it('should render the HOC component instead', () => {
		const hoc = jest.fn(() => () => 'hoc component');
		const Optional = optional(hoc)(jest.fn(() => 'wrapped component'));
		const {queryByText} = render(<Optional id={23} />);

		expect(queryByText('hoc component')).toBeTruthy();
		expect(queryByText('wrapped component')).toBeNull();
	});
});
