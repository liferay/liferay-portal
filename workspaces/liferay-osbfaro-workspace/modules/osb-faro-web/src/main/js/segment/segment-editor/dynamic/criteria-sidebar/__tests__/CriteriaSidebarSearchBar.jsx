import CriteriaSidebarSearchBar from '../CriteriaSidebarSearchBar';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('CriteriaSidebarSearchBar', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<CriteriaSidebarSearchBar onChange={jest.fn()} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ searchValue', () => {
		const searchValue = 'Page Views';

		const {getByDisplayValue} = render(
			<CriteriaSidebarSearchBar
				onChange={jest.fn()}
				searchValue={searchValue}
			/>
		);

		expect(getByDisplayValue(searchValue)).toBeTruthy();
	});
});
