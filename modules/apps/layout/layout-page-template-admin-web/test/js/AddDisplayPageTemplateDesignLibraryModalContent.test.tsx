/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AddDisplayPageTemplateDesignLibraryModalContent from '../../src/main/resources/META-INF/resources/js/AddDisplayPageTemplateDesignLibraryModalContent';

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

const FORM_SUBMIT_URL = '/add_display_page';

const NAMESPACE = '_namespace_';

const DEFAULT_PROPS = {
	closeModal: jest.fn(),
	formSubmitURL: FORM_SUBMIT_URL,
	mappingTypes: [
		{
			id: 'type-with-subtype',
			label: 'Type with subtype',
			subtypes: [
				{
					id: 'subtype',
					label: 'Subtype',
				},
			],
		},
		{
			id: 'type-without-subtype',
			label: 'Type without subtype',
			subtypes: [],
		},
	],
	namespace: NAMESPACE,
};

async function fillForm(container: HTMLElement) {
	await userEvent.type(
		container.querySelector(`#${NAMESPACE}name`)!,
		'Display Page Template'
	);
	await userEvent.selectOptions(
		container.querySelector(`#${NAMESPACE}classNameId`)!,
		'type-without-subtype'
	);
}

function renderComponent(props = {}) {
	return render(
		<AddDisplayPageTemplateDesignLibraryModalContent
			{...DEFAULT_PROPS}
			{...props}
		/>
	);
}

async function submit() {
	await userEvent.click(screen.getByText('save'));
}

describe('AddDisplayPageTemplateDesignLibraryModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('navigates to the editor of the created display page template', async () => {
		mockFetch.mockResolvedValue({
			json: () => Promise.resolve({redirectURL: '/editor?p_l_mode=edit'}),
		});

		const {container} = renderComponent();

		await fillForm(container);

		await submit();

		await waitFor(() => expect(mockNavigate).toHaveBeenCalledTimes(1));

		expect(mockFetch).toHaveBeenCalledWith(
			FORM_SUBMIT_URL,
			expect.objectContaining({method: 'POST'})
		);
		expect(mockNavigate).toHaveBeenCalledWith('/editor?p_l_mode=edit', {
			beforeScreenFlip: DEFAULT_PROPS.closeModal,
		});
	});

	it('opens an error toast when the request fails', async () => {
		mockFetch.mockRejectedValue(new Error('Network down'));

		const {container} = renderComponent();

		await fillForm(container);

		await submit();

		await waitFor(() => expect(mockOpenToast).toHaveBeenCalledTimes(1));

		expect(mockOpenToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);
		expect(mockNavigate).not.toHaveBeenCalled();
	});

	it('requires a name and a content type before submitting', async () => {
		renderComponent();

		await submit();

		expect(screen.getAllByText('this-field-is-required')).toHaveLength(2);
		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('requires a subtype when the content type has subtypes', async () => {
		const {container} = renderComponent();

		await userEvent.type(
			container.querySelector(`#${NAMESPACE}name`)!,
			'Display Page Template'
		);
		await userEvent.selectOptions(
			container.querySelector(`#${NAMESPACE}classNameId`)!,
			'type-with-subtype'
		);

		await submit();

		expect(screen.getAllByText('this-field-is-required')).toHaveLength(1);
		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('surfaces the validation error the server reports', async () => {

		// The action command nests the message under "error", so reading the
		// top level would swallow it

		mockFetch.mockResolvedValue({
			json: () =>
				Promise.resolve({
					error: {name: 'that-name-is-already-taken'},
				}),
		});

		const {container} = renderComponent();

		await fillForm(container);

		await submit();

		await waitFor(() =>
			expect(
				screen.getByText('that-name-is-already-taken')
			).toBeInTheDocument()
		);

		expect(mockNavigate).not.toHaveBeenCalled();
	});
});
