/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import Scope from '../../src/main/resources/META-INF/resources/js/components/Scope';
import {SCHEMA_SELECTED_EVENT} from '../../src/main/resources/META-INF/resources/js/constants';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const PORTLET_NAMESPACE = 'test';

function mockScopesResponses({assetLibraryItems, siteItems}) {
	fetch.mockResponse(async (request) => {
		if (request.url.includes('asset-library-scopes')) {
			return JSON.stringify({items: assetLibraryItems});
		}

		return JSON.stringify({items: siteItems});
	});
}

describe('Scope', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders asset library and site scopes together after a schema is selected', async () => {
		mockScopesResponses({
			assetLibraryItems: [{label: 'Asset Library A', value: 11}],
			siteItems: [{label: 'Site B', value: 22}],
		});

		render(<Scope portletNamespace={PORTLET_NAMESPACE} />);

		Liferay.fire(SCHEMA_SELECTED_EVENT, {
			isExport: true,
			schemaName: 'com.liferay.test.Entity',
		});

		await waitFor(() => {
			expect(screen.getByLabelText('scope')).toBeInTheDocument();
		});

		const options = screen.getAllByRole('option');

		expect(options).toHaveLength(2);
		expect(options[0]).toHaveValue('11');
		expect(options[0]).toHaveTextContent('Asset Library A');
		expect(options[1]).toHaveValue('22');
		expect(options[1]).toHaveTextContent('Site B');
	});

	it('renders nothing when neither endpoint returns items', async () => {
		fetch.mockResponse(JSON.stringify({}));

		render(<Scope portletNamespace={PORTLET_NAMESPACE} />);

		Liferay.fire(SCHEMA_SELECTED_EVENT, {
			isExport: false,
			schemaName: 'com.liferay.test.Entity',
		});

		await waitFor(() => {
			expect(fetch).toHaveBeenCalledTimes(2);
		});

		expect(screen.queryByLabelText('scope')).not.toBeInTheDocument();
	});

	it('clears the scopes when the schema is deselected', async () => {
		mockScopesResponses({
			assetLibraryItems: [],
			siteItems: [{label: 'Site B', value: 22}],
		});

		render(<Scope portletNamespace={PORTLET_NAMESPACE} />);

		Liferay.fire(SCHEMA_SELECTED_EVENT, {
			isExport: true,
			schemaName: 'com.liferay.test.Entity',
		});

		await waitFor(() => {
			expect(screen.getByLabelText('scope')).toBeInTheDocument();
		});

		Liferay.fire(SCHEMA_SELECTED_EVENT, {});

		await waitFor(() => {
			expect(screen.queryByLabelText('scope')).not.toBeInTheDocument();
		});
	});

	it('hides the scopes and opens a toast when a request fails', async () => {
		mockScopesResponses({
			assetLibraryItems: [],
			siteItems: [{label: 'Site B', value: 22}],
		});

		render(<Scope portletNamespace={PORTLET_NAMESPACE} />);

		Liferay.fire(SCHEMA_SELECTED_EVENT, {
			isExport: true,
			schemaName: 'com.liferay.test.Entity',
		});

		await waitFor(() => {
			expect(screen.getByLabelText('scope')).toBeInTheDocument();
		});

		fetch.mockResponse('', {status: 500});

		Liferay.fire(SCHEMA_SELECTED_EVENT, {
			isExport: true,
			schemaName: 'com.liferay.test.OtherEntity',
		});

		await waitFor(() => {
			expect(openToast).toHaveBeenCalledWith({
				message: 'an-unexpected-error-occurred',
				type: 'danger',
			});
		});

		expect(screen.queryByLabelText('scope')).not.toBeInTheDocument();
	});
});
