import * as data from 'test/data';
import BaseResults from '../BaseResults';
import React from 'react';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {noop, times} from 'lodash';
import {SelectionProvider} from 'shared/context/selection';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

const MAX_LENGTH = 900;
const TOTAL = 5;
const INDIVIDUALS = times(TOTAL, i => data.mockIndividual(i));

const DefaultComponent = props => (
	<SelectionProvider>
		<StaticRouter>
			<BaseResults {...props} />
		</StaticRouter>
	</SelectionProvider>
);

describe('BaseResults', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(
			<DefaultComponent
				dataSourceFn={() =>
					Promise.resolve({items: INDIVIDUALS, total: TOTAL})
				}
				groupId='23'
				resultsRenderer={noop}
			/>
		);

		await waitFor(
			() => {
				expect(
					container.querySelector('.base-results-root')
				).toBeInTheDocument();
			},
			{timeout: 1000}
		);
	});

	it('should render w/ an error display', async () => {
		const {getByText} = render(
			<DefaultComponent
				dataSourceFn={() => Promise.reject({})}
				groupId='23'
				resultsRenderer={noop}
			/>
		);

		await waitFor(
			() => {
				expect(
					getByText('An unexpected error occurred.')
				).toBeInTheDocument();
			},
			{timeout: 1000}
		);
	});

	it('should render w/a no results display', async () => {
		const {getByText} = render(
			<DefaultComponent
				dataSourceFn={() => Promise.resolve({items: [], total: 0})}
				groupId='23'
				query='non-existent query'
				resultsRenderer={noop}
			/>
		);

		await waitFor(
			() => {
				expect(
					getByText('No results were found.')
				).toBeInTheDocument();
			},
			{timeout: 1000}
		);
	});

	it('should put a size limit on the query based on maxLength', () => {
		const {container} = render(
			<DefaultComponent
				dataSourceFn={() =>
					Promise.resolve({items: INDIVIDUALS, total: TOTAL})
				}
				groupId='23'
				maxLength={MAX_LENGTH}
				query={Array(2000).join('a')}
				resultsRenderer={noop}
			/>
		);

		expect(container.querySelector('input.input-root').value).toHaveLength(
			MAX_LENGTH
		);
	});

	it('should not render a subnav if there is a datasourceFn error', async () => {
		const {queryByText} = render(
			<DefaultComponent
				dataSourceFn={() => Promise.reject(new Error())}
				groupId='23'
				renderSubnav={() => <div>{'subnav content'}</div>}
				resultsRenderer={noop}
			/>
		);

		await waitFor(
			() => {
				// After error, subnav content should not be rendered
				expect(queryByText('subnav content')).toBeNull();
			},
			{timeout: 1000}
		);
	});

	it('should render with search disabled when disableSearch is TRUE', async () => {
		const {container} = render(
			<DefaultComponent
				dataSourceFn={() =>
					Promise.resolve({
						disableSearch: true,
						items: INDIVIDUALS,
						total: TOTAL
					})
				}
				groupId='23'
				resultsRenderer={noop}
			/>
		);

		await waitFor(
			() => {
				expect(container.querySelector('.search input').disabled).toBe(
					true
				);
			},
			{timeout: 1000}
		);
	});

	it('should not include disabled items when calculating whether all the items are checked', async () => {
		const {getByTestId} = render(
			<DefaultComponent
				checkDisabled={item => item.id === '0'}
				dataSourceFn={() =>
					Promise.resolve({items: INDIVIDUALS, total: TOTAL})
				}
				groupId='23'
				resultsRenderer={noop}
				showCheckbox
			/>
		);

		const selectAllCheckbox = getByTestId('select-all-checkbox');

		// Wait for items to load (loading state clears)
		await waitFor(
			() => {
				expect(selectAllCheckbox).not.toBeDisabled();
			},
			{timeout: 1000}
		);

		fireEvent.click(selectAllCheckbox);

		await waitFor(
			() => {
				expect(selectAllCheckbox.checked).toBe(true);
			},
			{timeout: 1000}
		);
	});

	it('should not re-invoke resultsRenderer while typing in the search input', async () => {
		const resultsRenderer = jest.fn(({items}) => (
			<ul data-testid='results'>
				{items.map(item => (
					<li key={item.id}>{item.name}</li>
				))}
			</ul>
		));

		const {container} = render(
			<DefaultComponent
				dataSourceFn={() =>
					Promise.resolve({items: INDIVIDUALS, total: TOTAL})
				}
				groupId='23'
				resultsRenderer={resultsRenderer}
			/>
		);

		await waitFor(
			() => {
				expect(resultsRenderer).toHaveBeenCalled();
				expect(resultsRenderer.mock.lastCall[0].loading).toBe(false);
			},
			{timeout: 1000}
		);

		const callsAfterLoad = resultsRenderer.mock.calls.length;

		const searchInput = container.querySelector('input.input-root');

		fireEvent.change(searchInput, {target: {value: 'a'}});
		fireEvent.change(searchInput, {target: {value: 'ab'}});
		fireEvent.change(searchInput, {target: {value: 'abc'}});

		expect(searchInput.value).toBe('abc');

		expect(resultsRenderer).toHaveBeenCalledTimes(callsAfterLoad);
	});
});
