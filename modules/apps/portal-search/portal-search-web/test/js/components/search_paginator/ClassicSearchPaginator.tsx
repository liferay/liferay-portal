/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ClassicSearchPaginator from '../../../../src/main/resources/META-INF/resources/js/components/search_paginator/ClassicSearchPaginator';

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
		<ClassicSearchPaginator
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

describe('ClassicSearchPaginator', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('links every page the total accounts for', () => {
		renderSearchPaginator({activePage: 1, totalItems: 15});

		expect(screen.getByText('2')).toHaveAttribute(
			'href',
			'http://localhost/search?start=2'
		);
		expect(screen.getByText('3')).toHaveAttribute(
			'href',
			'http://localhost/search?start=3'
		);
	});

	it('marks the active page', () => {
		renderSearchPaginator({activePage: 2, totalItems: 15});

		expect(screen.getByText('2')).toHaveAttribute('aria-current', 'page');
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
