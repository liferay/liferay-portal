/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import AddResult from '../../../../src/main/resources/META-INF/resources/js/components/add_result/AddResult.es';
import {
	FETCH_SEARCH_DOCUMENTS_URL,
	getMockResultsData,
} from '../../mocks/data.es';

import '@testing-library/jest-dom';

const MODAL_ID = 'add-result-modal';

describe('AddResult', () => {
	beforeEach(() => {
		fetch.mockResponse(JSON.stringify(getMockResultsData()));
	});

	it('shows an add result button', async () => {
		const {getByText} = render(
			<AddResult
				fetchDocumentsSearchURL={FETCH_SEARCH_DOCUMENTS_URL}
				onAddResultSubmit={jest.fn()}
			/>
		);

		expect(getByText('add-result')).toBeInTheDocument();
	});

	it('shows a modal when the add a result button gets clicked', async () => {
		const {findByTestId, getByText} = render(
			<AddResult
				fetchDocumentsSearchURL={FETCH_SEARCH_DOCUMENTS_URL}
				onAddResultSubmit={jest.fn()}
			/>
		);

		fireEvent.click(getByText('add-result'));

		const modal = await findByTestId(MODAL_ID);

		expect(modal).toBeInTheDocument();
	});
});
