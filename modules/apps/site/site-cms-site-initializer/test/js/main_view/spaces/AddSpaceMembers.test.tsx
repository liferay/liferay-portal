/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import AdminUserService from '../../../../src/main/resources/META-INF/resources/js/common/services/AdminUserService';
import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import {
	AddSpaceMembers,
	AddSpaceMembersProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/AddSpaceMembers';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/AdminUserService'
);

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn((resource) => {
		const resolvers = {
			'/o/headless-admin-user/v1.0/user-accounts': () => {
				return [
					{
						emailAddress: 'user.one@liferay.com',
						externalReferenceCode: 'erc-user-one',
						id: 'user-one-1',
						name: 'User One',
					},
					{
						emailAddress: 'user.two@liferay.com',
						externalReferenceCode: 'erc-user-two',
						id: 'user-two-2',
						name: 'User Two',
					},
				];
			},
		};

		const url = new URL(resource);
		const headers = new Headers();
		headers.set('Content-Type', 'application/json');

		return Promise.resolve({
			headers,
			json: () =>
				Promise.resolve({
					items: resolvers[url.pathname as keyof typeof resolvers](),
					lastPage: 1,
					page: 1,
				}),
			ok: true,
			status: 200,
		});
	}),
}));

const mockLearnResources = {
	'site-cms-site-initializer': {
		'add-space-members': {
			en_US: {
				message: 'Test Message',
				url: 'https://learn.liferay.com/test-url',
			},
		},
	},
};

const mockedFetch = fetch as jest.Mock<unknown>;

describe('AddSpaceMembers', () => {
	const testSpace = {
		externalReferenceCode: 'ERC',
		id: '123',
		name: 'Test Space',
	};

	const testUsersResponse = {
		items: [],
		lastPage: 1,
		page: 1,
		totalCount: 0,
	};

	const testUserGroupsResponse = {
		items: [],
		lastPage: 1,
		page: 1,
		totalCount: 0,
	};

	const props: AddSpaceMembersProps = {
		assetLibraryCreatorUserId: '0',
		assetLibraryId: testSpace.id,
		assetLibraryName: testSpace.name,
		baseAssetLibraryURL: '/web/cms/e/space/28632',
		externalReferenceCode: testSpace.externalReferenceCode,
		hasAssignMembersPermission: true,
		learnResources: mockLearnResources,
	};

	const {ResizeObserver} = window;

	let getSpaceSpy: jest.SpyInstance;
	let getSpaceUsersSpy: jest.SpyInstance;
	let getSpaceUserGroupsSpy: jest.SpyInstance;
	let linkUserToSpaceSpy: jest.SpyInstance;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	beforeEach(() => {
		getSpaceSpy = jest
			.spyOn(SpaceService, 'getSpace')
			.mockResolvedValue(testSpace as unknown as Space);
		getSpaceUsersSpy = jest
			.spyOn(SpaceService, 'getSpaceUsers')
			.mockResolvedValue(testUsersResponse);
		getSpaceUserGroupsSpy = jest
			.spyOn(SpaceService, 'getSpaceUserGroups')
			.mockResolvedValue(testUserGroupsResponse);
		linkUserToSpaceSpy = jest
			.spyOn(SpaceService, 'linkUserToSpace')
			.mockResolvedValue({data: {}, error: null});

		jest.spyOn(AdminUserService, 'getUserRoles').mockResolvedValue({
			items: [],
			lastPage: 1,
			page: 1,
			pageSize: 1,
			totalCount: 0,
		});

		global.IntersectionObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		getSpaceSpy.mockClear();
		getSpaceUsersSpy.mockClear();
		getSpaceUserGroupsSpy.mockClear();
		linkUserToSpaceSpy.mockClear();

		jest.clearAllMocks();
	});

	afterAll(() => {
		jest.restoreAllMocks();
		mockedFetch.mockReset();

		delete (global as any).IntersectionObserver;
		window.ResizeObserver = ResizeObserver;
	});

	it('renders with correct title, description, buttons', async () => {
		await act(async () => render(<AddSpaceMembers {...props} />));

		expect(
			screen.getByRole('heading', {
				name: `add-members-to-x`,
			})
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'add-team-members-to-this-space-to-start-collaborating'
			)
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {
				name: 'continue-without-members',
			})
		).toBeInTheDocument();

		expect(screen.getByText('no-members-yet')).toBeInTheDocument();

		expect(
			screen.getByPlaceholderText('enter-name-or-email')
		).toBeInTheDocument();
	});

	it("must shows selected users after selecting an item from the 'enter-name-or-email' input", async () => {
		const user = userEvent.setup();

		render(<AddSpaceMembers {...props} />);

		await waitFor(() => screen.getByPlaceholderText('enter-name-or-email'));

		const input = screen.getByPlaceholderText('enter-name-or-email');

		expect(
			screen.queryByRole('list', {name: 'who-has-access'})
		).not.toBeInTheDocument();

		await user.click(input);

		expect(input).toHaveFocus();

		await waitFor(() => {
			screen.getByRole('option', {name: /user one (user.one)/i});
		});

		const options = screen.getAllByRole('option');

		expect(options).toHaveLength(2);

		expect(options[0]).toHaveTextContent('User One (user.one)');

		expect(options[1]).toHaveTextContent('User Two (user.two)');

		await user.click(options[0]);

		const whoHasAccess = await screen.findByRole('list', {
			name: 'who-has-access',
		});

		expect(whoHasAccess).toBeInTheDocument();

		const whoHasAccessItems = within(whoHasAccess).getAllByRole('listitem');

		expect(whoHasAccessItems).toHaveLength(1);

		expect(whoHasAccessItems[0]).toHaveTextContent('User One');
	});
});
