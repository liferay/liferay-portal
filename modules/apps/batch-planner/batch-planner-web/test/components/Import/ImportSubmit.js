/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	act,
	configure,
	fireEvent,
	render,
	waitFor,
} from '@testing-library/react';
import fetchMock from 'fetch-mock';
import React from 'react';

import {getImportTaskStatusURL} from '../../../src/main/resources/META-INF/resources/js/BatchPlannerImport';
import {
	POLL_INTERVAL,
	PROCESS_STARTED,
} from '../../../src/main/resources/META-INF/resources/js/constants';
import ImportSubmit from '../../../src/main/resources/META-INF/resources/js/import/ImportSubmit';

const BASE_PROPS = {
	evaluateForm: () => {},
	fieldsSelections: {},
	fileContent: [],
	formDataQuerySelector: 'form',
	formImportURL: 'https://formUrl.test',
	formIsValid: true,
	portletNamespace: 'test',
};

const internalFieldName = 'name';
const externalFieldName = 'external';

let mockApi;
const externalReferenceCode = '1234';

configure({asyncUtilTimeout: 5000});

describe('ImportSubmit', () => {
	beforeEach(() => {
		const form = document.createElement('form');

		form.innerHTML = `
			 <input name="${BASE_PROPS.portletNamespace}internalFieldName_${internalFieldName}" value="${internalFieldName}" />
			 <input name="${BASE_PROPS.portletNamespace}externalFieldName_${externalFieldName}" value="${externalFieldName}" />
		 `;

		document.body.appendChild(form);

		mockApi = fetchMock.mock(BASE_PROPS.formImportURL, () => ({
			externalReferenceCode,
		}));
	});

	afterEach(() => {
		fetchMock.restore();
	});

	it('must show modal preview ', () => {
		const {getByText} = render(<ImportSubmit {...BASE_PROPS} />);

		act(() => {
			fireEvent.click(getByText(Liferay.Language.get('next')));
		});
		waitFor(() => {
			expect(
				document.querySelector('.modal-content')
			).toBeInTheDocument();
		});
	});

	it('must start polling import status and enable button when import process is completed', async () => {
		jest.useFakeTimers();
		const importTaskStatusURL = getImportTaskStatusURL(
			externalReferenceCode
		);

		mockApi.mock(
			importTaskStatusURL,
			{
				body: {
					className:
						'com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product',
					contentType: 'CSV',
					endTime: null,
					errorMessage: null,
					executeStatus: PROCESS_STARTED,
					externalReferenceCode,
					processedItemsCount: 25,
					startTime: '2021-11-10T10:36:08Z',
					totalItemsCount: 50,
				},
			},
			{sendAsJson: false}
		);
		const {getByText} = render(<ImportSubmit {...BASE_PROPS} />);

		act(() => {
			fireEvent.click(getByText(Liferay.Language.get('next')));
		});

		jest.useRealTimers();

		waitFor(() => {
			expect(
				getByText(Liferay.Language.get('start-import'))
			).toBeInTheDocument();

			const button = document.querySelector(
				`[data-testid*="start-import"]`
			);
			button.click();

			act(async () => {
				jest.advanceTimersByTime(POLL_INTERVAL);
			});

			expect(mockApi.called(importTaskStatusURL)).toBeTruthy();

			expect(
				getByText(Liferay.Language.get('done'), {
					selector: 'button',
				})
			).not.toBeDisabled();
		});
	});
});
