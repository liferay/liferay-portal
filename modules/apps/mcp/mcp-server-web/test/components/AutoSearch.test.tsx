/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useState} from 'react';

import '@testing-library/jest-dom';

import AutoSearch from '../../src/main/resources/META-INF/resources/js/components/AutoSearch';

function StatefulAutoSearch({
	initialQuery = '',
	onSearch,
}: {
	initialQuery?: string;
	onSearch: (value: string) => void;
}) {
	const [query, setQuery] = useState(initialQuery);

	return (
		<AutoSearch
			onSearch={(value) => {
				setQuery(value);

				onSearch(value);
			}}
			query={query}
		/>
	);
}

describe('AutoSearch', () => {
	it('renders a labelled search input with the query inside a search form', () => {
		render(<StatefulAutoSearch initialQuery="name" onSearch={jest.fn()} />);

		expect(screen.getByRole('searchbox', {name: 'search'})).toHaveValue(
			'name'
		);
		expect(screen.getByRole('search')).toBeInTheDocument();
		expect(screen.queryByRole('button')).toBeNull();
	});

	it('labels the input with the given label and keeps the search placeholder', () => {
		render(
			<AutoSearch label="search-fields" onSearch={jest.fn()} query="" />
		);

		expect(
			screen.getByRole('searchbox', {name: 'search-fields'})
		).toHaveAttribute('placeholder', 'search');
	});

	it('reports the query as it is typed', async () => {
		const onSearch = jest.fn();

		render(<StatefulAutoSearch onSearch={onSearch} />);

		await userEvent.type(
			screen.getByRole('searchbox', {name: 'search'}),
			'ab'
		);

		expect(onSearch).toHaveBeenLastCalledWith('ab');
		expect(screen.getByRole('searchbox', {name: 'search'})).toHaveValue(
			'ab'
		);
	});
});
