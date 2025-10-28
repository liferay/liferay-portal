/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import SearchBar from '../../../../src/main/resources/META-INF/resources/js/components/list/SearchBar.es';
import {resultsDataToMap} from '../../../../src/main/resources/META-INF/resources/js/utils/util.es';
import {
	FETCH_SEARCH_DOCUMENTS_URL,
	getMockResultsData,
} from '../../mocks/data.es';

import '@testing-library/jest-dom';

const DATA_MAP = resultsDataToMap(
	getMockResultsData(10, 0, '', false).documents
);

function renderTestSearchBar(props) {
	return render(
		<SearchBar
			dataMap={DATA_MAP}
			fetchDocumentsSearchURL={FETCH_SEARCH_DOCUMENTS_URL}
			onClickHide={jest.fn()}
			onClickPin={jest.fn()}
			onSelectAll={jest.fn()}
			onSelectClear={jest.fn()}
			resultIds={[102, 104, 103]}
			selectedIds={[]}
			{...props}
		/>
	);
}

describe('SearchBar', () => {
	it('does has an add result button when onAddResultSubmit is defined', () => {
		const {getByText} = renderTestSearchBar({onAddResultSubmit: jest.fn()});

		expect(getByText('add-result')).toBeInTheDocument();
	});

	it('does not have an add result button when onAddResultSubmit is not defined', () => {
		const {queryByText} = renderTestSearchBar();

		expect(queryByText('add-result')).toBeNull();
	});

	it('hides the add result button with selectedIds', () => {
		const {queryByText} = renderTestSearchBar({
			onAddResultSubmit: jest.fn(),
			selectedIds: [102],
		});

		expect(queryByText('add-result')).toBeNull();
	});

	it('shows what is selected with selectedIds', () => {
		const {getByText} = renderTestSearchBar({
			selectedIds: [102, 103],
		});

		expect(getByText('x-items-selected')).toBeInTheDocument();
	});

	it('shows only one selected with selectedIds', () => {
		const {getByText} = renderTestSearchBar({
			selectedIds: [102],
		});

		expect(getByText('x-item-selected')).toBeInTheDocument();
	});

	it('shows no items selected with empty selectedIds', () => {
		const {getByText, queryByText} = renderTestSearchBar();

		expect(getByText('select-items')).toBeInTheDocument();
		expect(queryByText('x-items-selected')).toBeNull();
		expect(queryByText('x-item-selected')).toBeNull();
	});
});
