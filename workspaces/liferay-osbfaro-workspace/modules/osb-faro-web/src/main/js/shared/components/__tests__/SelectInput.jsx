import React from 'react';
import SelectInput from '../SelectInput';
import {cleanup, render} from '@testing-library/react';

describe('SelectInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<SelectInput
				dataSourceFn={() => Promise.resolve([])}
				itemRenderer={jest.fn()}
			/>
		);

		expect(
			container.querySelector('.base-select-container')
		).toBeInTheDocument();
	});

	it('should render with the selected item', () => {
		const {queryByText} = render(
			<SelectInput
				dataSourceFn={() => Promise.resolve([])}
				itemRenderer={({name}) => name}
				selectedItem={{name: 'foo'}}
			/>
		);

		expect(queryByText('foo')).toBeTruthy();
	});
});
