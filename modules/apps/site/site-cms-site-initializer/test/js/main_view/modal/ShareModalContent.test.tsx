/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import ShareModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/share_modal_content/ShareModalContent';

jest.useFakeTimers();

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	dateUtils: {
		getFirstDayOfWeek: jest.fn(() => 0),
	},
	sub: jest.fn((str) => str),
}));

const mockCloseModal = jest.fn();

const DEFAULT_PROPS = {
	autocompleteURL: '/search',
	closeModal: mockCloseModal,
	collaboratorURL: '/o/cms/basic-documents/{objectEntryId}/collaborators',
	creator: {
		contentType: 'UserAccount',
		id: '1',
		name: 'Test1 Test1',
	},
	initialCollaborators: [
		{
			actionIds: 'VIEW',
			share: false,
			type: 'User',
			user: {
				externalReferenceCode: 'ERC_2',
				id: '2',
				name: 'Test2 Test2',
				roles: [],
			},
		},
	],
	itemId: 20,
	title: 'Test Document',
};

const renderComponent = (props = DEFAULT_PROPS) => {
	return render(<ShareModalContent {...props} />);
};

describe('ShareModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));

		global.fetch = jest.fn().mockImplementation(() => {
			return Promise.resolve({
				json: () => Promise.resolve({items: []}),
				ok: true,
			});
		});
	});

	it('checks the accessibility of the share content', async () => {
		const {container} = renderComponent();

		await act(async () => {
			checkAccessibility({bestPractices: true, context: container});
		});
	});

	it('renders the modal header and collaborators header', () => {
		const {getByText} = renderComponent();

		expect(getByText('share-x')).toBeInTheDocument();
		expect(getByText('add-people-to-collaborate')).toBeInTheDocument();
		expect(getByText('who-has-access (x-users)')).toBeInTheDocument();
	});

	it('renders the list of users with access', () => {
		const {container} = renderComponent();

		expect(
			container.querySelectorAll<HTMLInputElement>(
				'li.list-group-item .autofit-col-expand'
			)[0]
		).toHaveTextContent('Test2 Test2');

		expect(
			container.querySelectorAll<HTMLInputElement>(
				'li.list-group-item .autofit-col-expand'
			)[1]
		).toHaveTextContent('Test1 Test1');
		expect(
			container.querySelectorAll<HTMLInputElement>(
				'li.list-group-item'
			)[1]
		).toHaveTextContent(/owner/);
	});

	it('renders the cancel and save buttons', () => {
		const {getByText} = renderComponent();

		expect(getByText('cancel')).toBeInTheDocument();

		const saveButton = getByText('save');

		expect(saveButton).toBeInTheDocument();
		expect(saveButton).toBeDisabled();
	});

	it('calls search when the autocomplete input changes', async () => {
		const {container, getByRole} = renderComponent();

		const input = container.querySelector<HTMLInputElement>(
			'input#collaboratorAutocomplete'
		)!;

		await act(async () => {
			fireEvent.change(input, {
				target: {value: 'Test3'},
			});
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(global.fetch).toHaveBeenCalledWith(
			expect.stringContaining('search?search=Test3'),
			expect.objectContaining({
				method: 'GET',
			}),
			undefined
		);

		await waitFor(() => {
			expect(getByRole('listbox')).toBeInTheDocument();
		});
	});

	it('calls submission when save is clicked', async () => {
		const apiPostSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {}, error: null});

		const {getByLabelText, getByRole, getByText} = renderComponent();

		fireEvent.click(getByLabelText('more-options'));

		waitFor(() => {
			expect(
				getByRole('menuitem', {name: 'allow-resharing'})
			).toBeInTheDocument();
		});

		await act(async () => {
			fireEvent.click(getByRole('menuitem', {name: 'allow-resharing'}));
		});

		waitFor(() => {
			expect(getByText('save')).toBeEnabled();
		});

		await act(async () => {
			fireEvent.click(getByText('save'));
		});

		expect(apiPostSpy).toHaveBeenCalledWith(
			'/o/cms/basic-documents/20/collaborators',
			[{actionIds: ['VIEW'], id: '2', share: true, type: 'User'}]
		);

		expect(mockCloseModal).toHaveBeenCalledTimes(1);
	});
});
