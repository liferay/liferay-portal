/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React, {useContext} from 'react';

import '@testing-library/jest-dom';

import FrontendDataSet from '../src/main/resources/META-INF/resources/FrontendDataSet';
import FrontendDataSetContext from '../src/main/resources/META-INF/resources/FrontendDataSetContext';
import EVENTS from '../src/main/resources/META-INF/resources/utils/eventsDefinitions';
import recentSearches from '../src/main/resources/META-INF/resources/utils/recentSearches';

const ID = 'test-fds';

// The Data Set state lives in an atom keyed by the Data Set name, and atoms
// outlive a test, so every test works against a name of its own

let fdsCount = 0;

const VIEWS = [
	{
		contentRenderer: 'table',
		default: true,
		label: 'Table',
		name: 'table',
		schema: {
			fields: [
				{
					fieldName: 'name',
					label: 'Name',
				},
			],
		},
	},
];

function itemsResponse(names: Array<string>) {
	return JSON.stringify({
		items: names.map((name, index) => ({id: index + 1, name})),
		lastPage: 1,
		page: 1,
		pageSize: 20,
		totalCount: names.length,
	});
}

function mockPendingRequests() {
	const requests: Array<{
		resolve: (body: string) => void;
		signal?: AbortSignal | null;
	}> = [];

	fetch.mockResponse(
		(request) =>
			new Promise<string>((resolve) => {
				requests.push({resolve, signal: request.signal});
			})
	);

	return requests;
}

async function settle() {
	await act(async () => {
		await new Promise((resolve) => setTimeout(resolve, 0));
	});
}

// The shared Liferay mock records listeners instead of dispatching to them

function refreshFromTheOutside(id: string) {
	const calls = (Liferay.on as jest.Mock).mock.calls.filter(
		([eventName]) => eventName === EVENTS.UPDATE_DISPLAY
	);

	const [, handleRefreshFromTheOutside] = calls[calls.length - 1];

	act(() => handleRefreshFromTheOutside({id}));
}

describe('FrontendDataSet', () => {
	let id: string;

	beforeEach(() => {
		jest.clearAllMocks();

		id = `${ID}-${++fdsCount}`;
	});

	it('cancels a search request superseded by a newer one', async () => {
		const requests = mockPendingRequests();

		render(<FrontendDataSet apiURL="/o/products" id={id} views={VIEWS} />);

		await waitFor(() => expect(requests).toHaveLength(1));

		act(() => requests[0].resolve(itemsResponse(['unfiltered'])));

		expect(await screen.findByText('unfiltered')).toBeInTheDocument();

		const input = screen.getByRole('searchbox');

		await userEvent.type(input, 'a{Enter}');

		await waitFor(() => expect(requests).toHaveLength(2));

		await userEvent.type(input, 'b{Enter}');

		await waitFor(() => expect(requests).toHaveLength(3));

		expect(requests[1].signal?.aborted).toBe(true);
		expect(requests[2].signal?.aborted).toBe(false);
	});

	it('ignores a refresh response that lands after a newer search', async () => {
		const requests = mockPendingRequests();

		render(<FrontendDataSet apiURL="/o/products" id={id} views={VIEWS} />);

		await waitFor(() => expect(requests).toHaveLength(1));

		act(() => requests[0].resolve(itemsResponse(['unfiltered'])));

		expect(await screen.findByText('unfiltered')).toBeInTheDocument();

		// A refresh is never aborted, so only the sequence guards it

		refreshFromTheOutside(id);

		await waitFor(() => expect(requests).toHaveLength(2));

		await userEvent.type(screen.getByRole('searchbox'), 'a{Enter}');

		await waitFor(() => expect(requests).toHaveLength(3));

		act(() => requests[2].resolve(itemsResponse(['newer'])));

		expect(await screen.findByText('newer')).toBeInTheDocument();

		act(() => requests[1].resolve(itemsResponse(['stale-refresh'])));

		await settle();

		expect(screen.queryByText('stale-refresh')).not.toBeInTheDocument();
		expect(screen.getByText('newer')).toBeInTheDocument();
	});

	it('keeps a stable onSearch identity across renders', async () => {
		const requests = mockPendingRequests();

		const identities: Array<Function> = [];

		// A cell renderer is the closest consumer to MainSearch, which
		// debounces "onSearch" and goes stale when its identity changes

		function OnSearchProbe() {
			const {onSearch} = useContext(FrontendDataSetContext);

			identities.push(onSearch);

			return null;
		}

		render(
			<FrontendDataSet
				apiURL="/o/products"
				customDataRenderers={{default: OnSearchProbe}}
				id={ID}
				views={VIEWS}
			/>
		);

		await waitFor(() => expect(requests).toHaveLength(1));

		act(() => requests[0].resolve(itemsResponse(['unfiltered'])));

		await settle();

		await userEvent.type(screen.getByRole('searchbox'), 'a{Enter}');

		await waitFor(() => expect(requests).toHaveLength(2));

		act(() => requests[1].resolve(itemsResponse(['filtered'])));

		await settle();

		expect(identities.length).toBeGreaterThan(1);
		expect(new Set(identities).size).toBe(1);
	});

	describe('recent searches', () => {
		async function typeAndSettle(
			requests: ReturnType<typeof mockPendingRequests>,
			keystrokes: string,
			names: Array<string>
		) {
			const requestCount = requests.length;

			await userEvent.type(screen.getByRole('searchbox'), keystrokes);

			await waitFor(() =>
				expect(requests).toHaveLength(requestCount + 1)
			);

			act(() => requests[requestCount].resolve(itemsResponse(names)));

			await settle();
		}

		async function search(
			requests: ReturnType<typeof mockPendingRequests>,
			query: string,
			names: Array<string>
		) {
			await typeAndSettle(requests, `${query}{Enter}`, names);
		}

		async function renderLoaded({
			recentSearches = true,
			searchAsYouType = false,
		} = {}) {
			const requests = mockPendingRequests();

			render(
				<FrontendDataSet
					apiURL="/o/products"
					id={id}
					recentSearches={recentSearches}
					searchAsYouType={searchAsYouType}
					views={VIEWS}
				/>
			);

			await waitFor(() => expect(requests).toHaveLength(1));

			act(() => requests[0].resolve(itemsResponse(['unfiltered'])));

			expect(await screen.findByText('unfiltered')).toBeInTheDocument();

			return requests;
		}

		it('remembers a query that returned results', async () => {
			const requests = await renderLoaded();

			await search(requests, 'nike', ['nike air']);

			expect(recentSearches.get(id)).toEqual(['nike']);
		});

		it('remembers nothing when the Data Set does not ask for recent searches', async () => {
			const requests = await renderLoaded({recentSearches: false});

			await search(requests, 'nike', ['hit']);

			expect(recentSearches.get(id)).toEqual([]);
		});

		it('does not remember a query that returned no results', async () => {
			const requests = await renderLoaded();

			await search(requests, 'reebok', []);

			expect(recentSearches.get(id)).toEqual([]);
		});

		it('remembers one query for a burst of keystrokes when searching as you type', async () => {
			const requests = await renderLoaded({searchAsYouType: true});

			await typeAndSettle(requests, 'lego star wars', ['hit']);

			expect(recentSearches.get(id)).toEqual(['lego star wars']);
		});

		it('keeps a remembered query when it is extended into one that finds nothing', async () => {
			const requests = await renderLoaded();

			await search(requests, 'nike', ['hit']);

			expect(recentSearches.get(id)).toEqual(['nike']);

			await search(requests, 'zzz', []);

			expect(recentSearches.get(id)).toEqual(['nike']);
		});

		it('forgets a remembered query once it stops returning results', async () => {
			const requests = await renderLoaded();

			await search(requests, 'nike', ['nike air']);

			expect(recentSearches.get(id)).toEqual(['nike']);

			// The same query returning nothing is what a filter narrowing the
			// results down to none looks like from here

			refreshFromTheOutside(id);

			await waitFor(() => expect(requests).toHaveLength(3));

			act(() => requests[2].resolve(itemsResponse([])));

			await settle();

			expect(recentSearches.get(id)).toEqual([]);
		});
	});
});
