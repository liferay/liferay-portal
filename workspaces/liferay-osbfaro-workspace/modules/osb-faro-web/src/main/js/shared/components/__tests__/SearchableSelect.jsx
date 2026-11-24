import React from 'react';
import SearchableSelect from '../SearchableSelect';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {times} from 'lodash';

describe('SearchableSelect', () => {
	afterEach(cleanup);

	const items = times(5, i => ({name: `item${i}`, value: i}));

	it('should render', () => {
		const {getByText} = render(
			<SearchableSelect
				buttonPlaceholder='Bar'
				inputPlaceholder='Foo'
				items={items}
			/>
		);

		fireEvent.click(getByText(/Bar/));

		expect(document.body).toMatchSnapshot();
	});

	it('should render with a subheader', () => {
		const {getByText} = render(
			<SearchableSelect
				buttonPlaceholder='Bar'
				inputPlaceholder='Foo'
				items={[{name: 'foo bar', subheader: true}, ...items]}
			/>
		);

		fireEvent.click(getByText(/Bar/));

		expect(getByText('foo bar')).toBeTruthy();
	});
});
