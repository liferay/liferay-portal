/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import CMSShareModalContent, {
	Collaborator,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/share_modal_content/CMSShareModalContent';

jest.useFakeTimers();

jest.mock('frontend-js-components-web', () => ({
	...(jest.requireActual('frontend-js-components-web') as object),
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	buildFragment: () => ({
		querySelector: () => globalThis.document.createElement('div'),
	}),
	dateUtils: {
		getFirstDayOfWeek: jest.fn(() => 0),
	},
	fetch: (...args: any[]) => (global.fetch as any)(...args),
	sub: jest.fn((str) => str),
	uuidv4: (jest.requireActual('frontend-js-web') as any).uuidv4,
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
	entryClassName: '11111-className',
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
	] as Collaborator[],
	itemId: 20,
	title: 'Test Document',
};

const renderComponent = (props = DEFAULT_PROPS) => {
	return render(<CMSShareModalContent {...props} />);
};

describe('CMSShareModalContent', () => {
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

		document.body.innerHTML =
			'<div class="alert-notifications alert-notifications-fixed"></div>';
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

	it('renders the cancel and share buttons', () => {
		const {getByText} = renderComponent();

		expect(getByText('cancel')).toBeInTheDocument();

		const shareButton = getByText('share');

		expect(shareButton).toBeInTheDocument();
		expect(shareButton).toBeDisabled();
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

	it('skips search results without embedded data', async () => {
		global.fetch = jest.fn().mockImplementation(() => {
			return Promise.resolve({
				items: [
					{
						entryClassName:
							'com.liferay.portal.kernel.model.UserGroup',
					},
					{
						embedded: {
							emailAddress: 'test3@liferay.com',
							id: 3,
							name: 'Test3 Test3',
						},
						entryClassName: 'com.liferay.portal.kernel.model.User',
					},
				],
			});
		});

		const {container, getByRole, getByText} = renderComponent();

		const input = container.querySelector<HTMLInputElement>(
			'input#collaboratorAutocomplete'
		)!;

		await act(async () => {
			fireEvent.change(input, {
				target: {value: 'Test'},
			});
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		await waitFor(() => {
			expect(getByRole('listbox')).toHaveTextContent('Test3 Test3');
		});

		expect(getByRole('option')).toBeInTheDocument();
		expect(getByText('share-x')).toBeInTheDocument();
	});

	it('calls submission when share is clicked', async () => {
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
			expect(getByText('share')).toBeEnabled();
		});

		await act(async () => {
			fireEvent.click(getByText('share'));
		});

		const fetchMock = global.fetch as jest.Mock;
		const submitCall = fetchMock.mock.calls.find(
			([url, options]) =>
				url === '/o/cms/basic-documents/20/collaborators' &&
				options?.method === 'POST'
		);

		expect(submitCall).toBeDefined();
		expect(JSON.parse(submitCall![1].body)).toEqual([
			{actionIds: ['VIEW'], id: 2, share: true, type: 'User'},
		]);

		expect(mockCloseModal).toHaveBeenCalledTimes(1);
	});

	it('shows default permissions when entryClassName is not ObjectEntryFolder', () => {
		const {getByLabelText, getByRole} = renderComponent();

		fireEvent.click(getByLabelText('edit-permissions'));

		expect(
			getByRole('option', {name: 'view-and-download'})
		).toBeInTheDocument();
		expect(
			getByRole('option', {name: 'view-download-and-comment'})
		).toBeInTheDocument();
		expect(
			getByRole('option', {name: 'view-download-comment-and-update'})
		).toBeInTheDocument();
	});

	it('shows objectEntryFolder-specific permissions when entryClassName is ObjectEntryFolder', () => {
		const folderProps = {
			...DEFAULT_PROPS,
			entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
		};

		const {getByLabelText, getByRole, queryByText} =
			renderComponent(folderProps);

		fireEvent.click(getByLabelText('edit-permissions'));

		expect(
			getByRole('option', {name: 'view-and-download'})
		).toBeInTheDocument();
		expect(
			getByRole('option', {name: 'view-download-and-update'})
		).toBeInTheDocument();

		expect(
			queryByText('view-download-and-comment')
		).not.toBeInTheDocument();
	});

	it('offers an invite-external-user option when the search text is a valid email without matches', async () => {
		const {container, getByText} = renderComponent();

		const input = container.querySelector<HTMLInputElement>(
			'input#collaboratorAutocomplete'
		)!;

		await act(async () => {
			fireEvent.change(input, {
				target: {value: 'external@example.com'},
			});
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		await waitFor(() => {
			expect(getByText('invite-external-user')).toBeInTheDocument();
		});
	});

	it('does not offer the invite-external-user option when the search text is not a valid email', async () => {
		const {container, queryByText} = renderComponent();

		const input = container.querySelector<HTMLInputElement>(
			'input#collaboratorAutocomplete'
		)!;

		await act(async () => {
			fireEvent.change(input, {
				target: {value: 'not-an-email'},
			});
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(queryByText('invite-external-user')).not.toBeInTheDocument();
	});

	it('adds an external user collaborator when the invite suggestion is selected', async () => {
		const {container, getByText} = renderComponent();

		const input = container.querySelector<HTMLInputElement>(
			'input#collaboratorAutocomplete'
		)!;

		await act(async () => {
			fireEvent.change(input, {
				target: {value: 'external@example.com'},
			});
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		await waitFor(() => {
			expect(getByText('invite-external-user')).toBeInTheDocument();
		});

		await act(async () => {
			fireEvent.click(getByText('invite-external-user'));
		});

		expect(
			container.querySelectorAll('.list-group-item-flex span.text-3')[0]
		).toHaveTextContent('external@example.com');
	});

	it('does not add a collaborator when pressing Enter on an invalid email', async () => {
		const {container} = renderComponent();

		const input = container.querySelector<HTMLInputElement>(
			'input#collaboratorAutocomplete'
		)!;

		await act(async () => {
			fireEvent.change(input, {
				target: {value: 'not-an-email'},
			});
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		await act(async () => {
			fireEvent.keyDown(input, {code: 'Enter', key: 'Enter'});
		});

		const chips = container.querySelectorAll(
			'.list-group-item-flex span.text-3'
		);

		expect(chips).toHaveLength(2);

		chips.forEach((chip) => {
			expect(chip).not.toHaveTextContent('not-an-email');
		});
	});

	it('does not offer the invite-external-user option when sharing an ObjectEntryFolder', async () => {
		const folderProps = {
			...DEFAULT_PROPS,
			entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
		};

		const {container, queryByText} = renderComponent(folderProps);

		const input = container.querySelector<HTMLInputElement>(
			'input#collaboratorAutocomplete'
		)!;

		await act(async () => {
			fireEvent.change(input, {
				target: {value: 'external@example.com'},
			});
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(queryByText('invite-external-user')).not.toBeInTheDocument();
	});

	it('renders existing external user collaborators as invited', () => {
		const emailProps = {
			...DEFAULT_PROPS,
			initialCollaborators: [
				{
					actionIds: 'VIEW',
					share: false,
					type: 'Email',
					user: {
						emailAddress: 'external@example.com',
						name: 'external@example.com',
					},
				},
			] as Collaborator[],
		};

		const {container, getByText} = renderComponent(emailProps);

		expect(getByText('invited')).toBeInTheDocument();

		expect(
			container.querySelectorAll<HTMLInputElement>(
				'li.list-group-item .autofit-col-expand'
			)[0]
		).toHaveTextContent('external@example.com');
	});

	it('restricts permission options to view-and-download for existing external user collaborators', () => {
		const emailProps = {
			...DEFAULT_PROPS,
			initialCollaborators: [
				{
					actionIds: 'VIEW',
					share: false,
					type: 'Email',
					user: {
						emailAddress: 'external@example.com',
						name: 'external@example.com',
					},
				},
			] as Collaborator[],
		};

		const {getByLabelText, getByRole, queryByText} =
			renderComponent(emailProps);

		fireEvent.click(getByLabelText('edit-permissions'));

		expect(
			getByRole('option', {name: 'view-and-download'})
		).toBeInTheDocument();
		expect(
			queryByText('view-download-and-comment')
		).not.toBeInTheDocument();
		expect(
			queryByText('view-download-comment-and-update')
		).not.toBeInTheDocument();
	});

	it('omits the id when saving a newly invited external user', async () => {
		const {container, getByText} = renderComponent();

		const input = container.querySelector<HTMLInputElement>(
			'input#collaboratorAutocomplete'
		)!;

		await act(async () => {
			fireEvent.change(input, {
				target: {value: 'external@example.com'},
			});
		});

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		await waitFor(() => {
			expect(getByText('invite-external-user')).toBeInTheDocument();
		});

		await act(async () => {
			fireEvent.click(getByText('invite-external-user'));
		});

		await act(async () => {
			fireEvent.click(getByText('share'));
		});

		const fetchMock = global.fetch as jest.Mock;
		const submitCall = fetchMock.mock.calls.find(
			([url, options]) =>
				url === '/o/cms/basic-documents/20/collaborators' &&
				options?.method === 'POST'
		);

		expect(submitCall).toBeDefined();

		const payload = JSON.parse(submitCall![1].body) as Array<{
			id?: number;
			type: string;
		}>;
		const externalEntry = payload.find((entry) => entry.type === 'Email');

		expect(externalEntry).toMatchObject({
			emailAddress: 'external@example.com',
			type: 'Email',
		});
		expect(externalEntry).not.toHaveProperty('id');
	});

	it('sends type="Email" and the email as id when saving an external user invite', async () => {
		const emailProps = {
			...DEFAULT_PROPS,
			initialCollaborators: [
				{
					actionIds: 'VIEW',
					share: false,
					type: 'Email',
					user: {
						emailAddress: 'external@example.com',
						name: 'external@example.com',
					},
				},
			] as Collaborator[],
		};

		const {getByLabelText, getByRole, getByText} =
			renderComponent(emailProps);

		fireEvent.click(getByLabelText('more-options'));

		waitFor(() => {
			expect(
				getByRole('menuitem', {name: 'allow-resharing'})
			).toBeInTheDocument();
		});

		await act(async () => {
			fireEvent.click(getByRole('menuitem', {name: 'allow-resharing'}));
		});

		await act(async () => {
			fireEvent.click(getByText('share'));
		});

		const fetchMock = global.fetch as jest.Mock;
		const submitCall = fetchMock.mock.calls.find(
			([url, options]) =>
				url === '/o/cms/basic-documents/20/collaborators' &&
				options?.method === 'POST'
		);

		expect(submitCall).toBeDefined();
		expect(JSON.parse(submitCall![1].body)).toEqual([
			{
				actionIds: ['VIEW'],
				emailAddress: 'external@example.com',
				share: true,
				type: 'Email',
			},
		]);
	});
});
