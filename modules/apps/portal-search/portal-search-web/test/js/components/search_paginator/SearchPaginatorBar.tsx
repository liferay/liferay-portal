/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import SearchPaginatorBar from '../../../../src/main/resources/META-INF/resources/js/components/search_paginator/SearchPaginatorBar';

const LANGUAGE_KEYS: Record<string, string> = {
	'showing-x-to-x-of-x-entries': 'Showing {0} to {1} of {2} entries.',
	'x-plus': '{0}+',
};

function renderSearchPaginatorBar({
	activePage = 1,
	activePageItemsCount = 5,
	showDeltasDropDown = true,
	totalItemsApproximate = false,
}: {
	activePage?: number;
	activePageItemsCount?: number;
	showDeltasDropDown?: boolean;
	totalItemsApproximate?: boolean;
}) {
	render(
		<SearchPaginatorBar
			activeDelta={5}
			activePage={activePage}
			activePageItemsCount={activePageItemsCount}
			deltas={[
				{href: 'http://localhost/search?delta=5', label: 5},
				{href: 'http://localhost/search?delta=10', label: 10},
			]}
			paginationURLTemplate="http://localhost/search?start={0}"
			showDeltasDropDown={showDeltasDropDown}
			totalItems={9}
			totalItemsApproximate={totalItemsApproximate}
		>
			<nav aria-label="pagination" />
		</SearchPaginatorBar>
	);
}

describe('SearchPaginatorBar', () => {
	const defaultLanguageGet = Liferay.Language.get.getMockImplementation();

	beforeEach(() => {
		jest.clearAllMocks();

		Liferay.Language.get.mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	afterEach(() => {
		Liferay.Language.get.mockImplementation(defaultLanguageGet);
	});

	it('ends the range at the last result on a full page', () => {
		renderSearchPaginatorBar({totalItemsApproximate: true});

		expect(
			screen.getByText('Showing 1 to 5 of 9+ entries.')
		).toBeInTheDocument();
	});

	it('ends the range at the last result on a page the max result window cuts off', () => {
		renderSearchPaginatorBar({
			activePage: 2,
			activePageItemsCount: 3,
			totalItemsApproximate: true,
		});

		expect(
			screen.getByText('Showing 6 to 8 of 9+ entries.')
		).toBeInTheDocument();
	});

	it('ends the range at the last result on the last page', () => {
		renderSearchPaginatorBar({activePage: 2, activePageItemsCount: 4});

		expect(
			screen.getByText('Showing 6 to 9 of 9 entries.')
		).toBeInTheDocument();
	});

	it('shows the items per page', () => {
		renderSearchPaginatorBar({});

		expect(
			screen.getByRole('combobox', {name: 'items-per-page'})
		).toBeInTheDocument();
	});

	it('hides the items per page when they cannot be changed', () => {
		renderSearchPaginatorBar({showDeltasDropDown: false});

		expect(
			screen.queryByRole('combobox', {name: 'items-per-page'})
		).not.toBeInTheDocument();
	});

	it('shows the pagination', () => {
		renderSearchPaginatorBar({});

		expect(
			screen.getByRole('navigation', {name: 'pagination'})
		).toBeInTheDocument();
	});
});
