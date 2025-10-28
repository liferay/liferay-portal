/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useModal} from '@clayui/modal';
import {
	fireEvent,
	getByPlaceholderText,
	render,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React from 'react';

import AddResultModal from '../../../../src/main/resources/META-INF/resources/js/components/add_result/AddResultModal.es';
import {
	FETCH_SEARCH_DOCUMENTS_URL,
	getMockResultsData,
} from '../../mocks/data.es';

import '@testing-library/jest-dom';

const MODAL_ID = 'add-result-modal';
const RESULTS_LIST_ID = 'add-result-items';

const START_ID = 100;

const AddResultModalWithModalMock = (props) => {
	const {observer, onClose} = useModal({
		onClose: jest.fn(),
	});

	return (
		<AddResultModal
			fetchDocumentsSearchURL={FETCH_SEARCH_DOCUMENTS_URL}
			observer={observer}
			onAddResultSubmit={jest.fn()}
			onClose={onClose}
			{...props}
		/>
	);
};

/**
 * Function that triggers the search in the modal, in order to see
 * list of results.
 * @param {function} getByTestId The query for the modal.
 */
async function openResultsList(findByTestId) {
	const modal = await findByTestId(MODAL_ID);

	const input = getByPlaceholderText(modal, 'search-the-engine');

	fireEvent.change(input, {target: {value: 'test'}});

	fireEvent.keyDown(input, {key: 'Enter', keyCode: 13, which: 13});

	await findByTestId(RESULTS_LIST_ID);
}

describe('AddResultModal', () => {
	beforeEach(() => {
		fetch.mockResponse(JSON.stringify(getMockResultsData()));
	});

	it('renders the modal', async () => {
		const {findByTestId} = render(<AddResultModalWithModalMock />);

		const modalElement = await findByTestId(MODAL_ID);

		expect(modalElement).toBeInTheDocument();
	});

	it('prompts a message to search in the modal', async () => {

		// This is a temporary mock to get the initial message to display.
		// There currently isn't a way to disable the initial fetch, so the
		// current workaround is showing an initial message if a refetch
		// hasn't been called.
		//
		// This should be removed after disabling the initial fetch.

		fetch.mockResponse(JSON.stringify({}));

		const {findByTestId} = render(<AddResultModalWithModalMock />);

		const modal = await findByTestId(MODAL_ID);

		expect(modal).toHaveTextContent('search-the-engine');

		expect(modal).toHaveTextContent('search-the-engine-to-display-results');
	});

	it('searches for results and calls the onAddResultSubmit function after add is pressed', async () => {
		const onAddResultSubmit = jest.fn();

		const {findByTestId, getByTestId, getByText} = render(
			<AddResultModalWithModalMock
				onAddResultSubmit={onAddResultSubmit}
			/>
		);

		await openResultsList(findByTestId);

		fireEvent.click(
			getByTestId('100').querySelector('.custom-control-input')
		);

		fireEvent.click(getByText('add'));

		expect(onAddResultSubmit.mock.calls.length).toBe(1);
	});

	it('disables the add button when the selected results are empty', async () => {
		const {findByTestId, getByText} = render(
			<AddResultModalWithModalMock />
		);

		await findByTestId(MODAL_ID);

		expect(getByText('add')).toBeDisabled();
	});

	it('shows the results in the modal after enter key is pressed', async () => {
		const {findByTestId} = render(<AddResultModalWithModalMock />);

		await openResultsList(findByTestId);

		const modal = await findByTestId(MODAL_ID);

		expect(modal).toHaveTextContent('100 This is a Document Example');
		expect(modal).toHaveTextContent('109 This is a Web Content Example');
	});

	it('does not show the prompt in the modal after enter key is pressed', async () => {
		const {findByTestId} = render(<AddResultModalWithModalMock />);

		await openResultsList(findByTestId);

		const modal = await findByTestId(MODAL_ID);

		expect(modal).not.toHaveTextContent('sorry,-no-results-were-found');
	});

	it('closes the modal when the cancel button gets clicked', async () => {
		const {findByTestId, getByText, queryByTestId} = render(
			<AddResultModalWithModalMock />
		);

		await findByTestId(MODAL_ID);

		fireEvent.click(getByText('cancel'));

		await waitForElementToBeRemoved(queryByTestId(MODAL_ID));

		expect(queryByTestId(MODAL_ID)).toBeNull();
	});

	it('shows next page results in the modal after navigation is pressed', async () => {
		const onAddResultSubmit = jest.fn();

		const {findByTestId} = render(
			<AddResultModalWithModalMock
				onAddResultSubmit={onAddResultSubmit}
			/>
		);

		await openResultsList(findByTestId);

		const modal = await findByTestId(MODAL_ID);

		fetch.mockResponse(JSON.stringify(getMockResultsData(10, 10)));

		fireEvent.click(modal.querySelector('.lexicon-icon-angle-right'));

		const nextPageStartId = START_ID + 10;

		await findByTestId(`${nextPageStartId}`);

		expect(modal).not.toHaveTextContent('100 This is a Document Example');
		expect(modal).not.toHaveTextContent(
			'109 This is a Web Content Example'
		);
		expect(modal).toHaveTextContent('110 This is a Document Example');
		expect(modal).toHaveTextContent('119 This is a Web Content Example');
	});

	it('updates results count in the modal after page delta is pressed', async () => {
		const onAddResultSubmit = jest.fn();

		const {findByTestId, queryAllByText} = render(
			<AddResultModalWithModalMock
				onAddResultSubmit={onAddResultSubmit}
			/>
		);

		await openResultsList(findByTestId);

		const modal = await findByTestId(MODAL_ID);

		fetch.mockResponse(JSON.stringify(getMockResultsData(50, 0)));

		await findByTestId(RESULTS_LIST_ID);

		fireEvent.click(queryAllByText('x-items')[0]);
		fireEvent.click(queryAllByText('x-items')[4]);

		await findByTestId('110');

		expect(modal).toHaveTextContent('149 This is a Web Content Example');
	});
});
