import AutocompleteInput from '../AutocompleteInput';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {mockListGeometry, scrollListToBottom} from 'test/infinite-scroll';

jest.unmock('react-dom');

const PAGE_SIZE = 2;

const getOptionIds = () =>
	screen.getAllByRole('option').map((option) => option.id);

describe('AutocompleteInput', () => {
	afterEach(cleanup);

	it('loads values page by page as the list is scrolled', async () => {
		const restoreListGeometry = mockListGeometry();

		// The empty query claims more values than it returns, so only the
		// short second page can end it. The "p" query fails on its second
		// page.

		const paginatedDataSourceFn = jest.fn(({page, query}) => {
			if (!query) {
				return Promise.resolve({
					items: page === 1 ? ['Brazil', 'Chile'] : ['Mexico'],
					total: 100,
				});
			}

			return page === 1
				? Promise.resolve({items: ['Peru', 'Spain'], total: 4})
				: Promise.reject(new Error('Request failed'));
		});

		const {rerender} = render(
			<AutocompleteInput
				onChange={jest.fn()}
				pageSize={PAGE_SIZE}
				paginatedDataSourceFn={paginatedDataSourceFn}
				value=""
			/>
		);

		fireEvent.focus(screen.getByRole('combobox'));

		await screen.findByText(/^chile$/i);

		expect(paginatedDataSourceFn).toHaveBeenLastCalledWith({
			page: 1,
			pageSize: PAGE_SIZE,
			query: '',
		});

		scrollListToBottom();

		await screen.findByText(/^mexico$/i);

		expect(paginatedDataSourceFn).toHaveBeenLastCalledWith({
			page: 2,
			pageSize: PAGE_SIZE,
			query: '',
		});
		expect(getOptionIds()).toEqual(['Brazil', 'Chile', 'Mexico']);

		scrollListToBottom();

		expect(paginatedDataSourceFn).toHaveBeenCalledTimes(2);

		rerender(
			<AutocompleteInput
				onChange={jest.fn()}
				pageSize={PAGE_SIZE}
				paginatedDataSourceFn={paginatedDataSourceFn}
				value="p"
			/>
		);

		await waitFor(() => expect(getOptionIds()).toEqual(['Peru', 'Spain']));

		expect(paginatedDataSourceFn).toHaveBeenLastCalledWith({
			page: 1,
			pageSize: PAGE_SIZE,
			query: 'p',
		});

		scrollListToBottom();

		await waitFor(() =>
			expect(
				document.querySelector('.dropdown-menu .loading-animation')
			).not.toBeInTheDocument()
		);

		scrollListToBottom();

		expect(paginatedDataSourceFn).toHaveBeenCalledTimes(4);

		restoreListGeometry();
	});
});
