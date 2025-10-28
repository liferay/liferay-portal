/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {MembersListItem} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/MemberListItem';
import {
	SPACE_MEMBER_ROLE_NAME,
	SpaceMembersPermissionSelect,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersPermissionSelect';

jest.mock('frontend-js-web', () => ({
	sub: (str: string, arg: string) => str.replace('x', arg),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersPermissionSelect',
	() => ({
		...(jest.requireActual(
			'../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersPermissionSelect'
		) as object),
		SpaceMembersPermissionSelect: jest.fn(() => null),
	})
);

describe('MemberListItem', () => {
	const mockRoles = [
		{
			externalReferenceCode: 'space-member-external-reference-code',
			id: 100,
			name: SPACE_MEMBER_ROLE_NAME,
			name_i18n: {'en-US': 'Space Member US'},
		},
		{
			externalReferenceCode: 'role-1-external-reference-code',
			id: 101,
			name: 'Role 1',
			name_i18n: {'en-US': 'Role 1 US'},
		},
	];

	const testUserAccount = {
		emailAddress: 'brian.smith@example.com',
		externalReferenceCode: 'USER_ERC',
		id: 'user',
		image: '/images/brian_smith.png',
		name: 'Brian Smith',
		roles: [mockRoles[0]],
	};

	const testUserGroup = {
		externalReferenceCode: 'USER_GROUP_ERC',
		id: 'group',
		name: 'Sample Group',
		numberOfUserAccounts: '5',
		roles: [mockRoles[0]],
	};

	const props = {
		currentUserId: testUserAccount.id,
		hasAssignMembersPermission: true,
		onRemoveItem: jest.fn(),
		onUpdateItemRoles: jest.fn(),
		roles: mockRoles,
	};

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly when items is user', () => {
		render(
			<MembersListItem
				{...props}
				itemType="user"
				items={[testUserAccount]}
			/>
		);

		const listItemElement = screen.getByRole('listitem');
		expect(listItemElement).toBeInTheDocument();
		expect(listItemElement).toHaveTextContent(
			`${testUserAccount.name}(you)`
		);

		expect(
			within(listItemElement).getByRole('button', {name: 'remove-user'})
		).toBeInTheDocument();

		const image = within(listItemElement).getByAltText(
			testUserAccount.name
		);
		expect(image).toHaveAttribute('src', testUserAccount.image);

		expect(SpaceMembersPermissionSelect).toHaveBeenCalled();
	});

	it('renders a user with fallback image and without the (you) tag', () => {
		const anotherUser = {
			emailAddress: 'another.user@example.com',
			externalReferenceCode: 'ANOTHER_USER_ERC',
			id: 'another-user-id',
			name: 'Another User',
			roles: [],
		};

		render(
			<MembersListItem {...props} itemType="user" items={[anotherUser]} />
		);

		const listItemElement = screen.getByRole('listitem');
		expect(listItemElement).toHaveTextContent(anotherUser.name);
		expect(listItemElement).not.toHaveTextContent('(you)');

		const image = within(listItemElement).getByAltText(anotherUser.name);
		expect(image).toHaveAttribute('src', '/image/user_portrait');

		expect(SpaceMembersPermissionSelect).toHaveBeenCalledWith(
			expect.objectContaining({
				selectedRoles: [SPACE_MEMBER_ROLE_NAME],
			}),
			{}
		);
	});

	it('renders the word owner and hides the remove button with permission select when the user is the owner', () => {
		render(
			<MembersListItem
				{...props}
				assetLibraryCreatorUserId={testUserAccount.id}
				itemType="user"
				items={[testUserAccount]}
			/>
		);

		expect(screen.getByRole('listitem')).toHaveTextContent(
			`${testUserAccount.name}(you)(owner)`
		);

		expect(
			screen.queryByRole('button', {name: /remove/i})
		).not.toBeInTheDocument();

		expect(SpaceMembersPermissionSelect).not.toHaveBeenCalled();
	});

	it('renders correctly when items is group', () => {
		render(
			<MembersListItem
				{...props}
				itemType="group"
				items={[testUserGroup]}
			/>
		);

		const listItemElement = screen.getByRole('listitem');
		expect(listItemElement).toBeInTheDocument();
		expect(listItemElement).toHaveTextContent(testUserGroup.name);
		expect(listItemElement).toHaveTextContent(
			`(${testUserGroup.numberOfUserAccounts}-members)`
		);

		expect(
			within(listItemElement).getByRole('button', {name: 'remove-group'})
		).toBeInTheDocument();

		expect(SpaceMembersPermissionSelect).toHaveBeenCalled();
	});

	it('renders correctly when items is group and there is no members', () => {
		const testUserGroupWithoutMembers = {
			externalReferenceCode: 'USER_GROUP_ERC',
			id: 'group',
			name: 'Sample Group',
			roles: [],
		};

		render(
			<MembersListItem
				{...props}
				itemType="group"
				items={[testUserGroupWithoutMembers]}
			/>
		);

		const listItemElement = screen.getByRole('listitem');
		expect(listItemElement).toBeInTheDocument();
		expect(listItemElement).toHaveTextContent(
			testUserGroupWithoutMembers.name
		);
		expect(listItemElement).toHaveTextContent('(0-members)');

		expect(SpaceMembersPermissionSelect).toHaveBeenCalledWith(
			expect.objectContaining({
				selectedRoles: [SPACE_MEMBER_ROLE_NAME],
			}),
			{}
		);
	});

	it('does not render the remove button when hasAssignMembersPermission is false', () => {
		const anotherUser = {
			emailAddress: 'another.user@example.com',
			externalReferenceCode: 'ANOTHER_USER_ERC',
			id: 'another-user-id',
			name: 'Another User',
			roles: [],
		};

		render(
			<MembersListItem
				{...props}
				hasAssignMembersPermission={false}
				itemType="user"
				items={[anotherUser]}
			/>
		);

		expect(
			screen.queryByRole('button', {name: /remove/i})
		).not.toBeInTheDocument();
	});

	it.each([
		['user', [testUserAccount]],
		['group', [testUserGroup]],
	])(
		'calls onRemoveItem when the remove %s button is clicked',
		async (itemType, items) => {
			const onRemoveItem = jest.fn();

			render(
				<MembersListItem
					{...props}
					itemType={itemType as 'user' | 'group'}
					items={items}
					onRemoveItem={onRemoveItem}
				/>
			);

			expect(onRemoveItem).not.toHaveBeenCalled();

			await userEvent.click(
				screen.getByRole('button', {name: `remove-${itemType}`})
			);

			expect(onRemoveItem).toHaveBeenCalledTimes(1);
			expect(onRemoveItem).toHaveBeenCalledWith(items[0]);
		}
	);

	it('calls onUpdateItemRoles when a role is changed', async () => {
		const onUpdateItemRoles = jest.fn();

		(SpaceMembersPermissionSelect as jest.Mock).mockImplementation(
			({onChange}: {onChange: (roles: string[]) => void}) => (
				<button
					onClick={() => onChange([SPACE_MEMBER_ROLE_NAME, 'Role 1'])}
				>
					Change Role
				</button>
			)
		);

		render(
			<MembersListItem
				{...props}
				itemType="user"
				items={[testUserAccount]}
				onUpdateItemRoles={onUpdateItemRoles}
			/>
		);

		await userEvent.click(
			screen.getByRole('button', {name: 'Change Role'})
		);

		expect(onUpdateItemRoles).toHaveBeenCalledWith(testUserAccount, [
			SPACE_MEMBER_ROLE_NAME,
			'Role 1',
		]);
	});
});
