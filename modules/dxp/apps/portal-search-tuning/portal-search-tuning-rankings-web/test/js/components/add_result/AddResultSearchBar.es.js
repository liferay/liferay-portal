/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import AddResultSearchBar from '../../../../src/main/resources/META-INF/resources/js/components/add_result/AddResultSearchBar.es';

import '@testing-library/jest-dom';

const TEST_TEXT = 'search query test';

function renderTestAddResultSearchBar(props) {
	return render(
		<AddResultSearchBar
			onSearchKeyDown={jest.fn()}
			onSearchQueryChange={jest.fn()}
			onSearchSubmit={jest.fn()}
			searchQuery={TEST_TEXT}
			{...props}
		/>
	);
}

describe('AddResult', () => {
	it('shows a search input', () => {
		const {getByPlaceholderText} = renderTestAddResultSearchBar();

		const input = getByPlaceholderText('search-the-engine');

		expect(input.value).toEqual(TEST_TEXT);
	});

	it('calls the onSearchKeyDown function when a key is pressed in the search field', () => {
		const onSearchKeyDown = jest.fn();

		const {getByPlaceholderText} = renderTestAddResultSearchBar({
			onSearchKeyDown,
		});

		const input = getByPlaceholderText('search-the-engine');

		fireEvent.change(input, {target: {value: ' '}});

		fireEvent.keyDown(input, {key: 'Enter', keyCode: 13, which: 13});

		expect(onSearchKeyDown.mock.calls.length).toBe(1);
	});

	it('calls the onSearchQueryChange function when the search field changes', () => {
		const onSearchQueryChange = jest.fn();

		const {getByPlaceholderText} = renderTestAddResultSearchBar({
			onSearchQueryChange,
		});

		const input = getByPlaceholderText('search-the-engine');

		fireEvent.change(input, {target: {value: ' '}});

		fireEvent.keyDown(input, {key: 'Enter', keyCode: 13, which: 13});

		expect(onSearchQueryChange.mock.calls.length).toBe(1);
	});
});
