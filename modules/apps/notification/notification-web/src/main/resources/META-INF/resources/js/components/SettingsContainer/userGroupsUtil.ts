/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MultiSelectItem} from '@liferay/object-js-components-web';
import {fetch} from 'frontend-js-web';

import {HEADERS} from '../../util/constants';

export async function getUserGroups(
	recipients?: Partial<UserNotificationRecipients>[]
) {
	const query =
		'/o/headless-admin-user/v1.0/user-groups?page=-1&sort=name:asc';

	const response = await fetch(query, {
		headers: HEADERS,
		method: 'GET',
	});

	const {items} = (await response.json()) as {items: {name: string}[]};

	let selectedUserGroups = new Set();

	if (recipients !== undefined) {
		selectedUserGroups = new Set(
			(recipients as Partial<UserNotificationRecipients>[]).map(
				(recipient) => recipient.userGroupName
			)
		);
	}

	const userGroups = {
		children: items.map(({name}) => ({
			checked: selectedUserGroups.has(name),
			label: name,
			value: name,
		})),
		label: Liferay.Language.get('user-groups'),
		value: 'user-groups',
	} as MultiSelectItem;

	return [userGroups];
}
