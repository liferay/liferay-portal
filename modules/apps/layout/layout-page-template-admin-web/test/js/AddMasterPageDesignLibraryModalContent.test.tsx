/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AddMasterPageDesignLibraryModalContent from '../../src/main/resources/META-INF/resources/js/AddMasterPageDesignLibraryModalContent';

const mockFetch = jest.fn();
const mockNavigate = jest.fn();
const mockOpenToast = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openToast: (props: any) => mockOpenToast(props),
}));

jest.mock('frontend-js-web', () => ({
	fetch: (url: string, options: any) => mockFetch(url, options),
	navigate: (url: string, options: any) => mockNavigate(url, options),
}));

const ADD_MASTER_LAYOUT_URL = '/add_master_layout';

const NAMESPACE = '_namespace_';

const DEFAULT_PROPS = {
	addMasterLayoutURL: ADD_MASTER_LAYOUT_URL,
	closeModal: jest.fn(),
	namespace: NAMESPACE,
};

const renderComponent = () =>
	render(<AddMasterPageDesignLibraryModalContent {...DEFAULT_PROPS} />);

const submitForm = async () => {
	await userEvent.click(screen.getByText('save'));
};

describe('AddMasterPageDesignLibraryModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('creates a master page once the form is valid', async () => {
		mockFetch
			.mockResolvedValueOnce({
				json: () =>
					Promise.resolve({error: 'that-name-is-already-taken'}),
			})
			.mockResolvedValueOnce({
				json: () =>
					Promise.resolve({redirectURL: '/editor?p_l_mode=edit'}),
			});

		const {container} = renderComponent();

		const nameInput = container.querySelector(`#${NAMESPACE}name`)!;

		expect(nameInput).toHaveFocus();

		await submitForm();

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
		expect(mockFetch).not.toHaveBeenCalled();

		await userEvent.type(nameInput, 'Master Page');

		expect(
			screen.queryByText('this-field-is-required')
		).not.toBeInTheDocument();

		await submitForm();

		expect(
			await screen.findByText('that-name-is-already-taken')
		).toBeInTheDocument();
		expect(mockNavigate).not.toHaveBeenCalled();

		await userEvent.type(nameInput, ' 2');

		await userEvent.dblClick(screen.getByText('save'));

		await waitFor(() => expect(mockNavigate).toHaveBeenCalledTimes(1));

		expect(screen.getByText('save')).toBeDisabled();
		expect(mockFetch).toHaveBeenCalledTimes(2);
		expect(mockFetch).toHaveBeenLastCalledWith(
			ADD_MASTER_LAYOUT_URL,
			expect.objectContaining({method: 'POST'})
		);

		const [, {body}] = mockFetch.mock.calls[1];

		expect(body.get(`${NAMESPACE}name`)).toBe('Master Page 2');
		expect(mockNavigate).toHaveBeenCalledWith('/editor?p_l_mode=edit', {
			beforeScreenFlip: DEFAULT_PROPS.closeModal,
		});
	});

	it('opens an error toast when the request fails', async () => {
		mockFetch.mockRejectedValue(new Error('Network down'));

		const {container} = renderComponent();

		await userEvent.type(
			container.querySelector(`#${NAMESPACE}name`)!,
			'Master Page'
		);

		await submitForm();

		await waitFor(() => expect(mockOpenToast).toHaveBeenCalledTimes(1));

		expect(mockOpenToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);
		expect(mockNavigate).not.toHaveBeenCalled();
	});
});
