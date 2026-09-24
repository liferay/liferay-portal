import React from 'react';
import SearchToolbar from '../index';
import {Map, Set} from 'immutable';
import {render} from '@testing-library/react';
import {withStaticRouter} from 'test/mock-router';

jest.unmock('react-dom');

const DefaultComponent = withStaticRouter(SearchToolbar);

describe('SearchToolbar', () => {
	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should NOT render with a search input when alwaysShowSearch is false', () => {
		const {queryByPlaceholderText} = render(
			<DefaultComponent
				alwaysShowSearch={false}
				selectEntirePageIndeterminate
			/>
		);

		expect(queryByPlaceholderText('Search')).toBeNull();
	});

	it('should render with a search input when alwaysShowSearch is true', () => {
		const {getByPlaceholderText} = render(
			<DefaultComponent alwaysShowSearch selectEntirePageIndeterminate />
		);

		expect(getByPlaceholderText('Search')).toBeTruthy();
	});

	it('should render as disabled', () => {
		const {getByPlaceholderText} = render(<DefaultComponent disabled />);

		expect(getByPlaceholderText('Search')).toBeDisabled();
	});

	it('should render w/ a search query bar when there is a query', () => {
		const {container, getByText} = render(
			<DefaultComponent
				alwaysShowSearch
				query='Test'
				selectEntirePageIndeterminate
			/>
		);

		expect(getByText('Test')).toBeTruthy();
		expect(container.querySelector('.management-bar-primary')).toBeTruthy();
		expect(container.querySelector('.items-selected')).toBeTruthy();
	});

	it('should render a list of filter tags when there are active filters', () => {
		const {getByText} = render(
			<DefaultComponent
				alwaysShowSearch
				filterBy={new Map({fooField: new Set(['fooValue'])})}
				filterByOptions={[
					{
						key: 'fooField',
						values: [{label: 'fooValue', value: 'fooValue'}]
					}
				]}
				selectEntirePageIndeterminate
			/>
		);

		expect(getByText('fooValue')).toBeTruthy();
	});
});
