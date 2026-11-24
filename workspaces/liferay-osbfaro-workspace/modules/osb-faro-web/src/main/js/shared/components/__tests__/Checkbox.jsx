import Checkbox from '../Checkbox';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('Checkbox', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<Checkbox />);
		expect(container).toMatchSnapshot();
	});

	it('should render with a label', () => {
		const {queryByText} = render(<Checkbox label='foo' />);
		expect(queryByText('foo')).toBeTruthy();
	});
});
