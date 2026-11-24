import React from 'react';
import Select from '../Select';
import {cleanup, render} from '@testing-library/react';

describe('Select', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<Select />);
		expect(container).toMatchSnapshot();
	});

	it('should render with blank option', () => {
		const {container} = render(<Select showBlankOption />);
		expect(container).toMatchSnapshot();
	});

	it('should render with children', () => {
		const children = [
			<Select.Item key={1}>{'1'}</Select.Item>,
			<Select.Item key={2}>{'2'}</Select.Item>,
			<Select.Item key={3}>{'3'}</Select.Item>
		];

		const {queryByText} = render(<Select children={children} />);

		expect(queryByText('1')).toBeTruthy();
		expect(queryByText('2')).toBeTruthy();
		expect(queryByText('3')).toBeTruthy();
	});
});
