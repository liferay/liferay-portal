/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, getByLabelText, render} from '@testing-library/react';
import React from 'react';

import List from '../../../../src/main/resources/META-INF/resources/js/components/list/List.es';
import {resultsDataToMap} from '../../../../src/main/resources/META-INF/resources/js/utils/util.es';
import {getMockResultsData} from '../../mocks/data.es';

import '@testing-library/jest-dom';

const DATA_MAP = resultsDataToMap(
	getMockResultsData(10, 0, '', false).documents
);

const mockLoad = jest.fn();

function renderTestList(props) {
	return render(
		<List
			dataLoading={false}
			dataMap={DATA_MAP}
			onClickHide={jest.fn()}
			onClickPin={jest.fn()}
			onLoadResults={mockLoad}
			resultIds={[102, 104, 103]}
			showLoadMore={true}
			{...props}
		/>
	);
}

describe('List', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('lists out results in order with expected titles', () => {
		const {container} = renderTestList();

		const listItems = container.querySelectorAll('.text-truncate-inline');

		expect(listItems[0]).toHaveTextContent(
			'102 This is a Document Example'
		);
		expect(listItems[1]).toHaveTextContent(
			'104 This is a Document Example'
		);
		expect(listItems[2]).toHaveTextContent(
			'103 This is a Web Content Example'
		);
	});

	it('has no loading icon when dataLoading is false', () => {
		const {container} = renderTestList();

		expect(container.querySelector('.loading-animation')).toBeNull();
		expect(container.querySelector('.load-more-button')).not.toBeNull();
	});

	it('has a loading icon when dataLoading is true', () => {
		const {container} = renderTestList({dataLoading: true});

		expect(container.querySelector('.loading-animation')).not.toBeNull();
		expect(container.querySelector('.load-more-button')).toBeNull();
	});

	it('calls the onLoadResults function when the loading button is clicked', () => {
		const {getByText} = renderTestList();

		const loadButton = getByText('load-more-results');

		fireEvent.click(loadButton);

		expect(mockLoad).toHaveBeenCalledTimes(1);
	});

	it('updates the selected ids', () => {
		const {getByTestId, getByText} = renderTestList();

		fireEvent.click(getByLabelText(getByTestId('102'), 'select'));
		fireEvent.click(getByLabelText(getByTestId('104'), 'select'));

		expect(getByText('x-items-selected')).toBeInTheDocument();
	});

	it('updates the selected ids back', () => {
		const {getByTestId, queryByText} = renderTestList();

		fireEvent.click(
			getByTestId('102').querySelector('.custom-control-input')
		);
		fireEvent.click(
			getByTestId('102').querySelector('.custom-control-input')
		);

		expect(queryByText('items-selected')).toBeNull();
	});

	it('focuses on the id', () => {
		const {getByTestId} = renderTestList();

		fireEvent.focus(getByTestId('102'));

		const focusedElement = document.activeElement;

		expect(focusedElement).toBe(getByTestId('102'));
	});

	it('adds classes of focus and reorder on the id', () => {
		const {getByTestId} = renderTestList();

		fireEvent.focus(getByTestId('102'));

		fireEvent.keyDown(getByTestId('102'), {code: 32, key: ' '});

		expect(getByTestId('102')).toHaveClass('result-ranking-item-focus');
		expect(getByTestId('102')).not.toHaveClass(
			'result-ranking-item-reorder'
		);
	});
});
