/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import LimitedSearchPaginator from '../../../../src/main/resources/META-INF/resources/js/components/search_paginator/LimitedSearchPaginator';

function renderSearchPaginator({
	activePage,
	totalItems,
	totalItemsApproximate = false,
}: {
	activePage: number;
	totalItems: number;
	totalItemsApproximate?: boolean;
}) {
	render(
		<LimitedSearchPaginator
			activeDelta={5}
			activePage={activePage}
			activePageItemsCount={5}
			deltas={[]}
			paginationURLTemplate="http://localhost/search?start={0}"
			showDeltasDropDown={false}
			totalItems={totalItems}
			totalItemsApproximate={totalItemsApproximate}
		/>
	);
}

describe('LimitedSearchPaginator', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('shows a window of pages around the active page', () => {
		renderSearchPaginator({activePage: 6, totalItems: 100});

		for (const page of ['4', '5', '7', '8']) {
			expect(screen.getByText(page)).toHaveAttribute(
				'href',
				`http://localhost/search?start=${page}`
			);
		}

		expect(screen.getByText('6')).toHaveAttribute('aria-current', 'page');
		expect(screen.queryByText('3')).not.toBeInTheDocument();
		expect(screen.queryByText('9')).not.toBeInTheDocument();
	});

	it('starts the window at the first page', () => {
		renderSearchPaginator({activePage: 1, totalItems: 100});

		for (const page of ['1', '2', '3', '4', '5']) {
			expect(screen.getByText(page)).toBeInTheDocument();
		}

		expect(screen.queryByText('6')).not.toBeInTheDocument();
	});

	it('links the previous page past the first page', () => {
		renderSearchPaginator({activePage: 2, totalItems: 9});

		expect(
			screen.getByRole('link', {name: 'previous-page'})
		).toHaveAttribute('href', 'http://localhost/search?start=1');
	});

	it('does not link a page before the first page', () => {
		renderSearchPaginator({activePage: 1, totalItems: 9});

		expect(
			screen.queryByRole('link', {name: 'previous-page'})
		).not.toBeInTheDocument();
	});

	it('links the next page within the total', () => {
		renderSearchPaginator({activePage: 1, totalItems: 9});

		expect(screen.getByRole('link', {name: 'next-page'})).toHaveAttribute(
			'href',
			'http://localhost/search?start=2'
		);
	});

	it('does not link a page past an approximate total', () => {
		renderSearchPaginator({
			activePage: 2,
			totalItems: 9,
			totalItemsApproximate: true,
		});

		expect(screen.queryByText('3')).not.toBeInTheDocument();
		expect(
			screen.queryByRole('link', {name: 'next-page'})
		).not.toBeInTheDocument();
	});
});
