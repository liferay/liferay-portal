/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {
	AddSpaceMembers,
	AddSpaceMembersProps,
} from '../../../../src/main/resources/META-INF/resources/js/main/spaces/AddSpaceMembers';
import SpaceService from '../../../../src/main/resources/META-INF/resources/js/services/SpaceService';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/types/Space';
import {
	UserAccount,
	UserGroup,
} from '../../../../src/main/resources/META-INF/resources/js/types/UserAccount';

describe('AddSpaceMembers', () => {
	const testSpace = {
		id: '123',
		name: 'Test Space',
	};

	const testUsers = [
		{
			emailAddress: 'john.doe@example.com',
			id: '1',
			image: '/image/user_portrait',
			name: 'John Doe',
		},
		{
			emailAddress: 'jane.smith@example.com',
			id: '2',
			image: '/image/user_portrait',
			name: 'Jane Smith',
		},
	];

	const testUserGroups = [
		{
			id: '1',
			name: 'Group 1',
		},
		{
			id: '2',
			name: 'Group 2',
		},
	];

	const props: AddSpaceMembersProps = {
		assetLibraryCreatorUserId: testUsers[0].id,
		assetLibraryId: testSpace.id,
		assetLibraryName: testSpace.name,
		baseAssetLibraryURL: '/web/cms/e/space/28632/',
	};

	const LiferayOriginal = global.Liferay;
	const {ResizeObserver: ResizeObserverOriginal} = window;

	let getSpaceSpy: jest.SpyInstance;
	let getSpaceUsersSpy: jest.SpyInstance;
	let getSpaceUserGroupsSpy: jest.SpyInstance;

	beforeEach(() => {
		getSpaceSpy = jest
			.spyOn(SpaceService, 'getSpace')
			.mockResolvedValue(testSpace as Space);
		getSpaceUsersSpy = jest
			.spyOn(SpaceService, 'getSpaceUsers')
			.mockResolvedValue(testUsers as UserAccount[]);
		getSpaceUserGroupsSpy = jest
			.spyOn(SpaceService, 'getSpaceUserGroups')
			.mockResolvedValue(testUserGroups as UserGroup[]);
	});

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));

		global.Liferay = {
			Language: {
				get: jest.fn((key) => key),
			},
			ThemeDisplay: {
				...LiferayOriginal.ThemeDisplay,
				getUserId: jest.fn(() => '1'),
			},
		} as any;
	});

	afterEach(() => {
		getSpaceSpy.mockClear();
		getSpaceUsersSpy.mockClear();
		getSpaceUserGroupsSpy.mockClear();

		jest.clearAllMocks();
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
		jest.restoreAllMocks();
	});

	it('renders with correct title, description, buttons', async () => {
		await act(async () => render(<AddSpaceMembers {...props} />));

		expect(
			screen.getByRole('heading', {name: 'add-members-to-x'})
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'add-team-members-to-this-space-to-start-collaborating'
			)
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {
				name: 'continue',
			})
		).toBeInTheDocument();
	});

	it('lists users from a space', async () => {
		await act(async () => render(<AddSpaceMembers {...props} />));

		const usersList = screen.getByLabelText('who-has-access');
		expect(usersList).toBeInTheDocument();

		await waitFor(() => {
			const usersListItems = within(usersList).getAllByRole('listitem');
			expect(usersListItems).toHaveLength(testUsers.length);

			usersListItems.forEach((item, index) => {
				expect(item).toHaveTextContent(testUsers[index].name);
			});
		});
	});

	it('lists user groups from a space', async () => {
		await act(async () => render(<AddSpaceMembers {...props} />));

		await userEvent.click(
			screen.getByRole('combobox', {name: 'add-people-to-collaborate'})
		);

		await userEvent.click(screen.getByRole('option', {name: 'groups'}));

		const userGroupsList = screen.getByLabelText('who-has-access');
		expect(userGroupsList).toBeInTheDocument();

		await waitFor(() => {
			const userGroupsListItems =
				within(userGroupsList).getAllByRole('listitem');
			expect(userGroupsListItems).toHaveLength(testUsers.length);

			userGroupsListItems.forEach((item, index) => {
				expect(item).toHaveTextContent(testUsers[index].name);
			});
		});
	});
});
