/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import DataSet from '../../src/main/resources/META-INF/resources/js/data_set/DataSet';
import {OBJECT_RELATIONSHIP} from '../../src/main/resources/META-INF/resources/js/utils/constants';

const DATA_SET = {
	externalReferenceCode: 'test-data-set-erc',
	id: '38718',
	label: 'Test Data Set',
	restApplication: '/data-set-admin/data-sets',
	restEndpoint: '/',
	restSchema: 'DataSet',
};

const OPEN_API = {
	components: {
		schemas: {
			DataSet: {
				'properties': {
					externalReferenceCode: {type: 'string'},
					label: {type: 'string'},
				},
				'type': 'object',
				'x-filterable': {
					label: {type: 'string'},
				},
			},
		},
	},
};

const DEFAULT_PROPS = {
	backURL: '/back',
	cellClientExtensionRenderers: [],
	dataSetERC: DATA_SET.externalReferenceCode,
	fdsViewId: '0',
	filterClientExtensionRenderers: [],
	learnResources: {},
	manageUserViewsURL: '/manage-user-views',
	namespace: 'testNamespace_',
	resolvedRESTSchemas: [],
	restApplications: [DATA_SET.restApplication],
	saveDataSetSortURL: '/save-data-set-sort',
	saveDataSetTableSectionsURL: '/save-data-set-table-sections',
	spritemap: '/spritemap.svg',
};

describe('DataSet', () => {
	let resolveOpenAPIRequest: () => void;

	beforeEach(() => {
		jest.clearAllMocks();

		const openAPIResponse = new Promise<string>((resolve) => {
			resolveOpenAPIRequest = () => resolve(JSON.stringify(OPEN_API));
		});

		fetch.mockResponse((request) => {
			if (request.url.includes('openapi.json')) {
				return openAPIResponse;
			}

			if (request.url.includes(OBJECT_RELATIONSHIP.DATA_SET_SORTS)) {
				return Promise.resolve(JSON.stringify({items: []}));
			}

			return Promise.resolve(JSON.stringify(DATA_SET));
		});
	});

	const requestedOpenAPI = () =>
		fetch.mock.calls.some(([resource]) =>
			String(resource).includes('openapi.json')
		);

	it('renders the details section while the OpenAPI schema request is still pending', async () => {
		render(<DataSet {...DEFAULT_PROPS} />);

		expect(await screen.findByDisplayValue(DATA_SET.label)).toBeVisible();

		expect(
			screen.getByDisplayValue(DATA_SET.externalReferenceCode)
		).toBeVisible();

		// The OpenAPI request was issued but never answered, so the details
		// section rendered without waiting for it.

		expect(requestedOpenAPI()).toBe(true);
	});

	it('waits for the OpenAPI schema request before rendering the sorting section', async () => {
		render(<DataSet {...DEFAULT_PROPS} />);

		await userEvent.click(
			await screen.findByRole('button', {name: 'sorting'})
		);

		expect(
			screen.queryByText('no-sorting-created-yet')
		).not.toBeInTheDocument();

		resolveOpenAPIRequest();

		expect(await screen.findByText('no-sorting-created-yet')).toBeVisible();
	});
});
