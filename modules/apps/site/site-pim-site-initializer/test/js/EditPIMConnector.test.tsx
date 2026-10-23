/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import EditPIMConnector from '../../src/main/resources/META-INF/resources/js/EditPIMConnector';

const mockFetch = jest.fn();
const mockNavigate = jest.fn();

jest.mock('frontend-js-web', () => ({
	fetch: (...args: unknown[]) => mockFetch(...args),
	navigate: (...args: unknown[]) => mockNavigate(...args),
	sub: (template: string) => template,
}));

jest.mock('@liferay/site-cms-site-initializer', () => {
	const Toolbar = ({children, title}: any) => (
		<div>
			<h1>{title}</h1>

			{children}
		</div>
	);

	Toolbar.Item = ({children}: any) => <div>{children}</div>;

	return {
		RequiredMark: () => <span>required</span>,
		Toolbar,
	};
});

const PROPS = {
	apiURL: '/o/pim/connectors',
	backURL: '/web/cms/connectors',
	objectEntryId: 0,
	pimConnector: null,
	pimConnectors: [
		{
			key: 'liferay-commerce',
			name: 'Liferay Commerce',
			settings: {
				apiURL: '/o/pim/liferay-commerce-connector-settings',
				fields: [
					{
						businessType: 'Encrypted',
						label: 'Client Secret',
						name: 'clientSecret',
						required: true,
					},
					{
						businessType: 'Text',
						label: 'URL',
						name: 'url',
						required: true,
					},
				],
				title: 'Liferay Commerce Settings',
			},
		},
	],
	settings: null,
	title: 'New Connector',
};

const EDIT_PROPS = {
	...PROPS,
	objectEntryId: 42,
	pimConnector: {
		active: true,
		externalReferenceCode: 'CONNECTOR_ERC',
		key: 'liferay-commerce',
		name: 'Liferay',
	},
	settings: {
		externalReferenceCode: 'CONNECTOR_ERC',
		values: {
			clientSecret: 'TEMP_OBFUSCATION_VALUE',
			url: 'https://commerce.minium.com',
		},
	},
	title: 'Edit Liferay',
};

function getRequestBody(index: number) {
	return JSON.parse(mockFetch.mock.calls[index][1].body);
}

describe('EditPIMConnector', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockFetch.mockResolvedValue({
			json: async () => ({externalReferenceCode: 'CONNECTOR_ERC'}),
			ok: true,
		});
	});

	it('creates a connector and its settings', async () => {
		render(<EditPIMConnector {...PROPS} />);

		expect(
			screen.queryByText('Liferay Commerce Settings')
		).not.toBeInTheDocument();

		await userEvent.type(screen.getByLabelText(/name/), 'Liferay');
		await userEvent.selectOptions(
			screen.getByLabelText(/connector/),
			'liferay-commerce'
		);

		expect(
			screen.getByText('Liferay Commerce Settings')
		).toBeInTheDocument();
		expect(screen.getByText('save')).toBeDisabled();

		await userEvent.type(
			screen.getByLabelText(/URL/),
			'https://commerce.minium.com'
		);
		await userEvent.type(screen.getByLabelText(/Client Secret/), 'secret');

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(mockFetch.mock.calls[0][0]).toBe('/o/pim/connectors');
		expect(mockFetch.mock.calls[0][1].method).toBe('POST');
		expect(getRequestBody(0)).toEqual({
			active: false,
			key: 'liferay-commerce',
			name: 'Liferay',
		});

		expect(mockFetch.mock.calls[1][0]).toBe(
			'/o/pim/liferay-commerce-connector-settings/by-external-reference-code/CONNECTOR_ERC'
		);
		expect(mockFetch.mock.calls[1][1].method).toBe('PUT');
		expect(getRequestBody(1)).toEqual({
			clientSecret: 'secret',
			url: 'https://commerce.minium.com',
		});

		expect(mockNavigate).toHaveBeenCalledWith(PROPS.backURL);
	});

	it('prevents changing the connector type of an existing connector', () => {
		render(<EditPIMConnector {...EDIT_PROPS} />);

		expect(screen.getByLabelText(/connector/)).toBeDisabled();
		expect(
			screen.getByText(
				'the-connector-type-cannot-be-changed-after-the-connector-is-created'
			)
		).toBeInTheDocument();
		expect(screen.getByLabelText(/URL/)).toHaveValue(
			'https://commerce.minium.com'
		);
	});

	it('keeps the stored secret when it is not changed', async () => {
		render(<EditPIMConnector {...EDIT_PROPS} />);

		await userEvent.clear(screen.getByLabelText(/URL/));
		await userEvent.type(
			screen.getByLabelText(/URL/),
			'https://minium.com'
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(mockFetch.mock.calls[0][0]).toBe('/o/pim/connectors/42');
		expect(mockFetch.mock.calls[0][1].method).toBe('PUT');

		expect(mockFetch.mock.calls[1][1].method).toBe('PATCH');
		expect(getRequestBody(1)).toEqual({url: 'https://minium.com'});
	});

	it('stays on the page when the settings request fails', async () => {
		mockFetch
			.mockResolvedValueOnce({
				json: async () => ({externalReferenceCode: 'CONNECTOR_ERC'}),
				ok: true,
			})
			.mockResolvedValueOnce({ok: false});

		render(<EditPIMConnector {...EDIT_PROPS} />);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(mockNavigate).not.toHaveBeenCalled();
		expect(Liferay.Util.openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);
	});
});
