/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import FrontendDataSetContext from '../../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import MainSearch from '../../../src/main/resources/META-INF/resources/management_bar/controls/MainSearch';
import recentSearches from '../../../src/main/resources/META-INF/resources/utils/recentSearches';

const DEBOUNCE_DELAY = 300;

const FDS_NAME = 'test-fds';
const OTHER_FDS_NAME = 'other-test-fds';

describe('MainSearch', () => {
	let onClear: jest.Mock;
	let onSearch: jest.Mock;
	let user: ReturnType<typeof userEvent.setup>;

	function renderMainSearch({
		apiURL = '/o/products',
		recentSearches = false,
		searchAsYouType = false,
	} = {}) {
		render(
			<FrontendDataSetContext.Provider
				value={
					{
						apiURL,
						id: FDS_NAME,
						onSearch,
						recentSearches,
						searchAsYouType,
					} as any
				}
			>
				<MainSearch onClear={onClear} />
			</FrontendDataSetContext.Provider>
		);

		return screen.getByRole('searchbox');
	}

	function elapse(milliseconds: number) {
		act(() => {
			jest.advanceTimersByTime(milliseconds);
		});
	}

	beforeEach(() => {
		jest.useFakeTimers();

		recentSearches.clear(FDS_NAME);
		recentSearches.clear(OTHER_FDS_NAME);

		onClear = jest.fn();
		onSearch = jest.fn();

		user = userEvent.setup({advanceTimers: jest.advanceTimersByTime});
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('searches once for a burst of keystrokes when search as you type is enabled', async () => {
		const input = renderMainSearch({searchAsYouType: true});

		await user.type(input, 'abc');

		expect(onSearch).not.toHaveBeenCalled();

		elapse(DEBOUNCE_DELAY);

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('does not search while typing when search as you type is disabled', async () => {
		const input = renderMainSearch();

		await user.type(input, 'abc');

		elapse(DEBOUNCE_DELAY);

		expect(onSearch).not.toHaveBeenCalled();
	});

	it('searches on Enter when search as you type is disabled', async () => {
		const input = renderMainSearch();

		await user.type(input, 'abc{Enter}');

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('ignores Enter when search as you type is enabled', async () => {
		const input = renderMainSearch({searchAsYouType: true});

		await user.type(input, 'abc{Enter}');

		expect(onSearch).not.toHaveBeenCalled();

		elapse(DEBOUNCE_DELAY);

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('searches on the search button when search as you type is disabled', async () => {
		const input = renderMainSearch({searchAsYouType: false});

		await user.type(input, 'abc');
		await user.click(screen.getByRole('button', {name: 'search'}));

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('ignores the search button when search as you type is enabled', async () => {
		const input = renderMainSearch({searchAsYouType: true});

		await user.type(input, 'abc');
		await user.click(screen.getByRole('button', {name: 'search'}));

		expect(onSearch).not.toHaveBeenCalled();

		elapse(DEBOUNCE_DELAY);

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'abc'});
	});

	it('drops the pending search when the input is cleared', async () => {
		const input = renderMainSearch({searchAsYouType: true});

		await user.type(input, 'abc');
		await user.clear(input);

		elapse(DEBOUNCE_DELAY);

		expect(onClear).toHaveBeenCalled();
		expect(onSearch).not.toHaveBeenCalled();
	});

	it('searches on every keystroke when the items are filtered client side', async () => {
		const input = renderMainSearch({apiURL: '', searchAsYouType: true});

		await user.type(input, 'ab');

		expect(onSearch).toHaveBeenCalledTimes(2);
		expect(onSearch).toHaveBeenLastCalledWith({query: 'ab'});
	});

	it('searches client side items on Enter when search as you type is disabled', async () => {
		const input = renderMainSearch({apiURL: '', searchAsYouType: false});

		await user.type(input, 'ab');

		expect(onSearch).not.toHaveBeenCalled();

		await user.type(input, '{Enter}');

		expect(onSearch).toHaveBeenCalledTimes(1);
		expect(onSearch).toHaveBeenCalledWith({query: 'ab'});
	});

	describe('recent searches', () => {
		function storeQueries(queries: Array<string>, fdsName = FDS_NAME) {
			queries.forEach((query) => recentSearches.add(fdsName, query));
		}

		it('lists the stored queries when the empty input is focused', async () => {
			storeQueries(['vans', 'adidas', 'nike']);

			const input = renderMainSearch({recentSearches: true});

			await user.click(input);

			expect(screen.getByRole('menu')).toBeInTheDocument();

			expect(
				screen
					.getAllByRole('menuitem', {name: /nike|adidas|vans/})
					.map((menuItem) => menuItem.textContent)
			).toEqual(['nike', 'adidas', 'vans']);
		});

		it('lists nothing when the Data Set does not ask for recent searches', async () => {
			storeQueries(['nike']);

			const input = renderMainSearch();

			await user.click(input);

			expect(screen.queryByRole('menu')).not.toBeInTheDocument();
		});

		it('lists nothing when the Data Set has no stored queries', async () => {
			const input = renderMainSearch({recentSearches: true});

			await user.click(input);

			expect(screen.queryByRole('menu')).not.toBeInTheDocument();
		});

		it('leaves out the queries stored for another Data Set', async () => {
			storeQueries(['nike']);
			storeQueries(['adidas'], OTHER_FDS_NAME);

			const input = renderMainSearch({recentSearches: true});

			await user.click(input);

			expect(
				screen.getByRole('menuitem', {name: 'nike'})
			).toBeInTheDocument();
			expect(
				screen.queryByRole('menuitem', {name: 'adidas'})
			).not.toBeInTheDocument();
		});

		it('fills the input and searches for the clicked query', async () => {
			storeQueries(['nike']);

			const input = renderMainSearch({recentSearches: true});

			await user.click(input);
			await user.click(screen.getByRole('menuitem', {name: 'nike'}));

			expect(input).toHaveValue('nike');
			expect(onSearch).toHaveBeenCalledWith({query: 'nike'});
			expect(screen.queryByRole('menu')).not.toBeInTheDocument();
		});

		it('opens the list again when the already focused input is clicked', async () => {
			storeQueries(['nike']);

			const input = renderMainSearch({recentSearches: true});

			await user.type(input, 'reebok{Enter}');
			await user.clear(input);

			expect(screen.queryByRole('menu')).not.toBeInTheDocument();

			// The input never lost the focus, so only the click can reopen it

			await user.click(input);

			expect(
				screen.getByRole('menuitem', {name: 'nike'})
			).toBeInTheDocument();
		});

		it('keeps only the stored queries matching what the user typed', async () => {
			storeQueries(['adidas', 'nike air', 'nike sb']);

			const input = renderMainSearch({recentSearches: true});

			await user.type(input, 'nik');

			expect(
				screen
					.getAllByRole('menuitem', {name: /nike/})
					.map((menuItem) => menuItem.textContent)
			).toEqual(['nike sb', 'nike air']);

			expect(
				screen.queryByRole('menuitem', {name: 'adidas'})
			).not.toBeInTheDocument();
		});

		it('lists nothing when no stored query matches what the user typed', async () => {
			storeQueries(['nike']);

			const input = renderMainSearch({recentSearches: true});

			await user.type(input, 'reebok');

			expect(screen.queryByRole('menu')).not.toBeInTheDocument();
		});

		it('removes a single query without closing the list', async () => {
			storeQueries(['adidas', 'nike']);

			const input = renderMainSearch({recentSearches: true});

			await user.click(input);
			await user.click(
				screen.getAllByRole('menuitem', {name: 'clear-search'})[0]
			);

			expect(
				screen.queryByRole('menuitem', {name: 'nike'})
			).not.toBeInTheDocument();
			expect(
				screen.getByRole('menuitem', {name: 'adidas'})
			).toBeInTheDocument();
			expect(recentSearches.get(FDS_NAME)).toEqual(['adidas']);
		});

		it('removes every query at once', async () => {
			storeQueries(['adidas', 'nike']);

			const input = renderMainSearch({recentSearches: true});

			await user.click(input);
			await user.click(screen.getByRole('button', {name: 'clear-all'}));

			expect(screen.queryByRole('menu')).not.toBeInTheDocument();
			expect(recentSearches.get(FDS_NAME)).toEqual([]);
		});

		it('closes the list when the user clicks outside the search bar', async () => {
			storeQueries(['nike']);

			const input = renderMainSearch({recentSearches: true});

			await user.click(input);

			expect(screen.getByRole('menu')).toBeInTheDocument();

			await user.click(document.body);

			expect(screen.queryByRole('menu')).not.toBeInTheDocument();
		});
	});
});
