/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import SearchOptions from '../../src/main/resources/META-INF/resources/js/SearchOptions';

const DEFAULT_PROPS = {
	portletNamespace: 'namespace',
	searchIn: 'search-in',
	searchInOptions: [{label: 'Search In', value: 'search-in'}],
	searchLocation: null,
	searchLocationOptions: null,
	searchResults: 'search-results',
	searchURL: 'search-url',
};

const renderComponent = (props) => {
	const data = {...DEFAULT_PROPS, ...props};

	return render(<SearchOptions {...data} />);
};

describe('SearchOptions', () => {
	it('renders only search in and search results selectors if there is no any search location option', () => {
		renderComponent();

		const selectors = document.querySelectorAll('.form-control-select');

		expect(selectors.length).toBe(1);

		expect(screen.getByText('Search In')).toBeInTheDocument();
	});

	it('renders search location selector if it has any location option', () => {
		renderComponent({
			searchLocation: 'search-location',
			searchLocationOptions: [
				{label: 'Search Location', value: 'search-location'},
			],
		});

		const selectors = document.querySelectorAll('.form-control-select');

		expect(selectors.length).toBe(2);

		expect(screen.getByText('Search Location')).toBeInTheDocument();
	});
});
