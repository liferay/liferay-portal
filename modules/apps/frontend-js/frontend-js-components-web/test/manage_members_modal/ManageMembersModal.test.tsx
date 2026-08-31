/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import ManageMembersModal from '../../src/main/resources/META-INF/resources/manage_members_modal/ManageMembersModal';
import {MembersSelectOptions} from '../../src/main/resources/META-INF/resources/manage_members_modal/MembersSelectOptions';
import {
	AddMembersInputApi,
	MemberType,
	MembersConfig,
	Role,
	UserAccount,
	UserGroup,
} from '../../src/main/resources/META-INF/resources/manage_members_modal/types';
import openToast from '../../src/main/resources/META-INF/resources/toast/openToast';

jest.mock(
	'../../src/main/resources/META-INF/resources/toast/openToast',
	() => ({
		__esModule: true,
		default: jest.fn(),
	})
);

const ROLES: Role[] = [
	{externalReferenceCode: 'L_ASSET_LIBRARY_MEMBER', id: 10, name: 'Member'},
	{externalReferenceCode: 'r-editor', id: 11, name: 'Editor'},
	{externalReferenceCode: 'L_ASSET_LIBRARY_OWNER', id: 12, name: 'Owner'},
];

const ALICE: UserAccount = {
	emailAddress: 'alice@liferay.com',
	externalReferenceCode: 'u-alice',
	id: '1',
	imageId: '0',
	name: 'Alice Adams',
	roles: [],
};

const BOB: UserAccount = {
	emailAddress: 'bob@liferay.com',
	externalReferenceCode: 'u-bob',
	id: '2',
	imageId: '0',
	name: 'Bob Brown',
	roles: [],
};

const MARKETING: UserGroup = {
	externalReferenceCode: 'g-mkt',
	id: '3',
	name: 'Marketing',
	numberOfUserAccounts: '2',
	roles: [],
};

const CANDIDATE_USER: UserAccount = {
	emailAddress: 'carol@liferay.com',
	externalReferenceCode: 'c-user',
	id: '99',
	imageId: '0',
	name: 'Carol Candidate',
	roles: [],
};

const CANDIDATE_GROUP: UserGroup = {
	externalReferenceCode: 'c-group',
	id: '88',
	name: 'Sales Team',
	numberOfUserAccounts: '5',
	roles: [],
};

const CONFIG: MembersConfig = {
	defaultRoleExternalReferenceCode: 'L_ASSET_LIBRARY_MEMBER',
	excludedRoleExternalReferenceCodes: ['L_ASSET_LIBRARY_OWNER'],
	messages: {
		addGroupError: 'add-group-error {0}',
		addGroupSuccess: 'add-group-success {0}',
		addUserError: 'add-user-error {0}',
		addUserSuccess: 'add-user-success {0}',
		removeGroupError: 'remove-group-error {0}',
		removeGroupSuccess: 'remove-group-success {0}',
		removeUserError: 'remove-user-error {0}',
		removeUserSuccess: 'remove-user-success {0}',
		updateGroupError: 'update-group-error {0}',
		updateSuccess: 'update-success {0}',
		updateUserError: 'update-user-error {0}',
	},
};

function page<T>(items: T[], lastPage = 1, currentPage = 1) {
	return {items, lastPage, page: currentPage, totalCount: items.length};
}

// The portal-wide jest setup replaces the global fetch with a jest-fetch-mock
// instance; cast it to reach the mock controls without importing the package.

const fetchMock = fetch as unknown as jest.Mock<Promise<Response>> & {
	mockResponse: (
		handle: (
			request: Request
		) => Promise<string | {body?: string; status?: number}>
	) => void;
};

// Routes the headless-asset-library membership endpoints through the
// portal-wide jest-fetch-mock. GETs return the configured pages; link, unlink,
// and role-update mutations resolve as a 204.

function mockMembersAPI({
	groups = [MARKETING],
	roles = ROLES,
	usersByPage = {1: page([ALICE])},
}: {
	groups?: UserGroup[];
	roles?: Role[];
	usersByPage?: Record<number, ReturnType<typeof page>>;
} = {}) {
	fetchMock.mockResponse((request) => {
		if (request.method !== 'GET') {
			return Promise.resolve({body: '', status: 204});
		}

		const {pathname, searchParams} = new URL(request.url);
		const pageNumber = Number(searchParams.get('page')) || 1;

		if (pathname.endsWith('/user-accounts')) {
			return Promise.resolve(
				JSON.stringify(usersByPage[pageNumber] ?? page([]))
			);
		}

		if (pathname.endsWith('/user-groups')) {
			return Promise.resolve(JSON.stringify(page(groups)));
		}

		if (pathname.endsWith('/roles')) {
			return Promise.resolve(JSON.stringify(page(roles)));
		}

		return Promise.resolve({body: '', status: 204});
	});
}

function findFetchCall(method: string, pathSuffix: string) {
	return fetchMock.mock.calls.find(([url, init]) => {
		const {pathname} = new URL(String(url));

		return (
			pathname.endsWith(pathSuffix) && (init?.method ?? 'GET') === method
		);
	});
}

function renderAddMembersInput({
	onAutocompleteItemSelected,
	onSelectChange,
	selectValue,
}: AddMembersInputApi) {
	return (
		<MembersSelectOptions
			label="member-type"
			onSelectChange={onSelectChange}
			selectValue={selectValue}
		>
			<button
				onClick={() =>
					onAutocompleteItemSelected(
						selectValue === MemberType.USERS
							? CANDIDATE_USER
							: CANDIDATE_GROUP
					)
				}
				type="button"
			>
				add-candidate
			</button>
		</MembersSelectOptions>
	);
}

function renderModal(
	props: Partial<React.ComponentProps<typeof ManageMembersModal>> = {}
) {
	return render(
		<ManageMembersModal
			config={CONFIG}
			emptyStateDescription="empty-state-description"
			externalReferenceCode="lib-erc"
			hasAssignMembersPermission
			headerTitle="manage-members"
			renderAddMembersInput={renderAddMembersInput}
			{...props}
		/>
	);
}

describe('ManageMembersModal', () => {
	const {
		IntersectionObserver: originalIntersectionObserver,
		ResizeObserver: originalResizeObserver,
	} = window;

	let intersectionObserverCallback: IntersectionObserverCallback | undefined;

	beforeAll(() => {
		const liferay = (globalThis as any).Liferay;

		liferay.ThemeDisplay = liferay.ThemeDisplay || {};
		liferay.ThemeDisplay.getBCP47LanguageId = () => 'en-US';
		liferay.ThemeDisplay.getPathContext = () => '';
		liferay.ThemeDisplay.getUserId = () => '0';

		liferay.Util = liferay.Util || {};
		liferay.Util.sub = (template: string, ...args: any[]) =>
			template.replace(/\{(\d+)\}/g, (_, index) => args[index] ?? '');

		window.IntersectionObserver = jest
			.fn()
			.mockImplementation((callback: IntersectionObserverCallback) => {
				intersectionObserverCallback = callback;

				return {
					disconnect: jest.fn(),
					observe: jest.fn(),
					unobserve: jest.fn(),
				};
			});
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterAll(() => {
		window.IntersectionObserver = originalIntersectionObserver;
		window.ResizeObserver = originalResizeObserver;
	});

	beforeEach(() => {
		jest.clearAllMocks();

		intersectionObserverCallback = undefined;

		mockMembersAPI();
	});

	it('shows the empty state when there are no members', async () => {
		mockMembersAPI({groups: [], usersByPage: {1: page([])}});

		renderModal();

		await waitFor(() =>
			expect(
				screen.getByText('empty-state-description')
			).toBeInTheDocument()
		);

		expect(screen.getByText('no-members-yet')).toBeInTheDocument();
	});

	it('adds a user and a group selected from the autocomplete', async () => {
		renderModal();

		await waitFor(() =>
			expect(screen.getByText('Alice Adams')).toBeInTheDocument()
		);

		await userEvent.click(
			screen.getByRole('button', {name: 'add-candidate'})
		);

		await waitFor(() =>
			expect(
				findFetchCall(
					'PUT',
					'/asset-libraries/lib-erc/user-accounts/c-user'
				)
			).toBeTruthy()
		);

		expect(
			within(screen.getByRole('list')).getByText('Carol Candidate')
		).toBeInTheDocument();

		await userEvent.selectOptions(
			screen.getByLabelText('member-type'),
			'groups'
		);

		await waitFor(() =>
			expect(screen.getByText('Marketing')).toBeInTheDocument()
		);

		await userEvent.click(
			screen.getByRole('button', {name: 'add-candidate'})
		);

		await waitFor(() =>
			expect(
				findFetchCall(
					'PUT',
					'/asset-libraries/lib-erc/user-groups/c-group'
				)
			).toBeTruthy()
		);

		expect(
			within(screen.getByRole('list')).getByText('Sales Team')
		).toBeInTheDocument();
	});

	it('shows a single toast when a member is added', async () => {
		renderModal();

		await waitFor(() =>
			expect(screen.getByText('Alice Adams')).toBeInTheDocument()
		);

		await userEvent.click(
			screen.getByRole('button', {name: 'add-candidate'})
		);

		await waitFor(() =>
			expect(
				findFetchCall(
					'PUT',
					'/asset-libraries/lib-erc/user-accounts/c-user/roles'
				)
			).toBeTruthy()
		);

		expect(openToast).toHaveBeenCalledTimes(1);
		expect(openToast).toHaveBeenCalledWith({
			message: expect.stringContaining('add-user-success'),
			type: 'success',
		});
	});

	it('shows the default role for a member with no assigned roles', async () => {
		renderModal();

		await waitFor(() =>
			expect(screen.getByText('Alice Adams')).toBeInTheDocument()
		);

		expect(
			screen.getByRole('button', {name: 'Member'})
		).toBeInTheDocument();
	});

	it('saves a permission change through the API', async () => {
		renderModal();

		await waitFor(() =>
			expect(screen.getByText('Alice Adams')).toBeInTheDocument()
		);

		await userEvent.click(screen.getByRole('button', {name: 'Member'}));

		await userEvent.click(screen.getByLabelText('Editor'));

		await waitFor(() => {
			const call = findFetchCall(
				'PUT',
				'/asset-libraries/lib-erc/user-accounts/u-alice/roles'
			);

			expect(call).toBeTruthy();
			expect(JSON.parse(call![1]!.body as string)).toEqual([
				{name: 'Member'},
				{name: 'Editor'},
			]);
		});

		expect(openToast).toHaveBeenCalledWith({
			message: expect.stringContaining('update-success'),
			type: 'success',
		});
	});

	it('removes a user through the API', async () => {
		renderModal();

		await waitFor(() =>
			expect(screen.getByText('Alice Adams')).toBeInTheDocument()
		);

		await userEvent.click(screen.getByRole('button', {name: 'remove-x'}));

		await waitFor(() =>
			expect(
				findFetchCall(
					'DELETE',
					'/asset-libraries/lib-erc/user-accounts/u-alice'
				)
			).toBeTruthy()
		);

		await waitFor(() =>
			expect(screen.queryByText('Alice Adams')).not.toBeInTheDocument()
		);
	});

	it('loads more users when the sentinel scrolls into view', async () => {
		mockMembersAPI({
			usersByPage: {1: page([ALICE], 2, 1), 2: page([BOB], 2, 2)},
		});

		renderModal();

		await waitFor(() =>
			expect(screen.getByText('Alice Adams')).toBeInTheDocument()
		);

		await act(async () => {
			intersectionObserverCallback?.(
				[{isIntersecting: true} as IntersectionObserverEntry],
				{} as IntersectionObserver
			);
		});

		await waitFor(() =>
			expect(screen.getByText('Bob Brown')).toBeInTheDocument()
		);

		const page2Call = fetchMock.mock.calls.find(([url, init]) => {
			const {pathname, searchParams} = new URL(String(url));

			return (
				pathname.endsWith('/user-accounts') &&
				searchParams.get('page') === '2' &&
				(init?.method ?? 'GET') === 'GET'
			);
		});

		expect(page2Call).toBeTruthy();
	});
});
