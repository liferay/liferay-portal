/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {MembersListItem} from '../../../../src/main/resources/META-INF/resources/js/main/spaces/MemberListItem';

describe('MemberListItem', () => {
	const testUserAccount = {
		emailAddress: 'brian.smith@example.com',
		id: 'user',
		name: 'Brian Smith',
	};

	const testUserGroup = {
		id: 'group',
		name: 'Sample Group',
	};

	const props = {
		currentUserId: testUserAccount.id,
		emptyMessage: 'No users',
		onRemoveItem: jest.fn(),
	};

	it('renders default message when items is empty', () => {
		render(<MembersListItem {...props} itemType="user" items={[]} />);

		expect(screen.getByRole('listitem')).toHaveTextContent('No users');
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
			within(listItemElement).getByRole('button', {name: 'remove-x'})
		).toBeInTheDocument();
	});

	it('renders the word owner when listing the user is the owner of the space', () => {
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
	});

	it('calls onRemoveItem when remove button is clicked', async () => {
		const onRemoveItem = jest.fn();

		render(
			<MembersListItem
				currentUserId={testUserAccount.id}
				emptyMessage="No users"
				itemType="user"
				items={[testUserAccount]}
				onRemoveItem={onRemoveItem}
			/>
		);

		expect(onRemoveItem).not.toHaveBeenCalled();

		await userEvent.click(screen.getByRole('button', {name: 'remove-x'}));

		expect(onRemoveItem).toHaveBeenCalledTimes(1);
		expect(onRemoveItem).toHaveBeenCalledWith(testUserAccount);
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

		expect(
			within(listItemElement).getByRole('button', {name: 'remove-x'})
		).toBeInTheDocument();
	});
});
