/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import Pagination from '../../src/main/resources/META-INF/resources/js/data_set/pagination/Pagination';
import {IDataSet} from '../../src/main/resources/META-INF/resources/js/utils/types';

const DATA_SET = {
	defaultItemsPerPage: 20,
	externalReferenceCode: 'test-data-set-erc',
	id: '38718',
	label: 'Test Data Set',
	listOfItemsPerPage: '10, 20, 50',
	restApplication: '/data-set-admin/data-sets',
	restEndpoint: '/',
	restSchema: 'DataSet',
} as IDataSet;

const DEFAULT_PROPS = {
	backURL: '/back',
	cellClientExtensionRenderers: [],
	dataSet: DATA_SET,
	fieldTreeItems: [],
	filterClientExtensionRenderers: [],
	filterableFieldTreeItems: [],
	manageUserViewsURL: '/manage-user-views',
	namespace: 'testNamespace_',
	onActiveSectionChange: jest.fn(),
	onDataSetUpdate: jest.fn(),
	resolvedRESTSchemas: [],
	restApplications: [DATA_SET.restApplication],
	saveDataSetSortURL: '/save-data-set-sort',
	saveDataSetTableSectionsURL: '/save-data-set-table-sections',
	spritemap: '/spritemap.svg',
	visualizationModeClientExtensions: [],
};

describe('Pagination', () => {
	let patchRequestBodies: Array<{
		listOfItemsPerPage: string;
		showPagination: boolean;
	}>;

	beforeEach(() => {
		patchRequestBodies = [];

		fetch.mockResponse(async (request) => {
			const body = await request.json();

			patchRequestBodies.push(body);

			return JSON.stringify({...DATA_SET, ...body});
		});
	});

	it('shows the pagination by default', () => {
		render(<Pagination {...DEFAULT_PROPS} />);

		expect(screen.getByLabelText('show-pagination')).toBeChecked();
		expect(
			screen.getByLabelText(/list-of-items-per-page/, {
				selector: 'textarea',
			})
		).toBeEnabled();
	});

	it('disables the list of items per page when the pagination is hidden', () => {
		render(
			<Pagination
				{...DEFAULT_PROPS}
				dataSet={{...DATA_SET, showPagination: false}}
			/>
		);

		expect(screen.getByLabelText('show-pagination')).not.toBeChecked();
		expect(
			screen.getByLabelText(/list-of-items-per-page/, {
				selector: 'textarea',
			})
		).toBeDisabled();
		expect(screen.getByLabelText(/default-items-per-page/)).toBeEnabled();
	});

	it('saves the pagination setting', async () => {
		render(<Pagination {...DEFAULT_PROPS} />);

		userEvent.click(screen.getByLabelText('show-pagination'));

		await waitFor(() =>
			expect(
				screen.getByLabelText(/list-of-items-per-page/, {
					selector: 'textarea',
				})
			).toBeDisabled()
		);

		userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => expect(patchRequestBodies).toHaveLength(1));

		expect(patchRequestBodies[0].showPagination).toBe(false);
	});

	it('keeps the saved list of items per page when the pagination is hidden', async () => {
		render(<Pagination {...DEFAULT_PROPS} />);

		userEvent.clear(
			screen.getByLabelText(/list-of-items-per-page/, {
				selector: 'textarea',
			})
		);
		userEvent.type(
			screen.getByLabelText(/list-of-items-per-page/, {
				selector: 'textarea',
			}),
			'abc'
		);

		userEvent.click(screen.getByLabelText('show-pagination'));

		userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => expect(patchRequestBodies).toHaveLength(1));

		expect(patchRequestBodies[0].listOfItemsPerPage).toBe('10, 20, 50');
	});
});
