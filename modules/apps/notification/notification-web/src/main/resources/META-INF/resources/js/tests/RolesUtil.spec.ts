/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {MultiSelectItem} from '@liferay/object-js-components-web';

import {
	getCheckedChildren,
	handleMultiSelectItemsChange,
	uncheckMultiSelectItemChildrens,
} from '../components/SettingsContainer/multiSelectUtil';
import {getUserNotificationRoles} from '../components/SettingsContainer/rolesUtil';

it('Assert role names checked items', () => {
	const children = [
		{
			checked: false,
			label: 'Account Administrator',
			value: 'Account Administrator',
		},
		{
			checked: false,
			label: 'Account Member',
			value: 'Account Member',
		},
		{
			checked: false,
			label: 'Account Supplier',
			value: 'Account Supplier',
		},
		{
			checked: false,
			label: 'Buyer',
			value: 'Buyer',
		},
		{
			checked: false,
			label: 'Order Manager',
			value: 'Order Manager',
		},
	];

	const roleNames = [
		{roleName: 'Account Administrator'},
		{roleName: 'Account Manager'},
		{roleName: 'Account Member'},
		{roleName: 'Administrator'},
		{roleName: 'Analytics Administrator'},
		{roleName: 'Order Manager'},
		{roleName: 'Organization Administrator'},
		{roleName: 'Owner'},
	];

	const checkedChildren = getCheckedChildren(children, roleNames, 'roleName');

	expect(checkedChildren).toStrictEqual([
		{
			checked: true,
			label: 'Account Administrator',
			value: 'Account Administrator',
		},
		{
			checked: true,
			label: 'Account Member',
			value: 'Account Member',
		},
		{
			checked: false,
			label: 'Account Supplier',
			value: 'Account Supplier',
		},
		{
			checked: false,
			label: 'Buyer',
			value: 'Buyer',
		},
		{
			checked: true,
			label: 'Order Manager',
			value: 'Order Manager',
		},
	]);
});

it('Assert roles in User Notification', () => {
	const items = [
		{
			description: 'First User',
			externalReferenceCode: 'Label1',
			id: 1,
			name: 'Name1',
			roleType: 'regular',
		},
		{
			description: 'Second User',
			externalReferenceCode: 'Label2',
			id: 2,
			name: 'Name2',
			roleType: 'regular',
		},
		{
			description: 'Third User',
			externalReferenceCode: 'CustomStrictRole',
			id: 3,
			name: 'CustomStrictRole',
			roleType: 'regular',
		},
	];

	const recipients = [
		{
			roleName: 'Name1',
		},
	];

	const userNotificationRoles = getUserNotificationRoles(items, recipients);

	expect(userNotificationRoles.children).toStrictEqual([
		{
			checked: true,
			label: 'Name1',
			value: 'Name1',
		},
		{
			checked: false,
			label: 'Name2',
			value: 'Name2',
		},
		{
			checked: false,
			label: 'CustomStrictRole',
			value: 'CustomStrictRole',
		},
	]);
});

it('verify that handleMultiSelectRoleItemsChange generates new recipients in the correct data structure', () => {
	const itemsGroupMock = [
		{
			children: [
				{
					checked: true,
					label: 'Account Administrator',
					value: 'Account Administrator',
				},
				{
					checked: false,
					label: 'Account Member',
					value: 'Account Member',
				},
				{
					checked: true,
					label: 'Account Supplier',
					value: 'Account Supplier',
				},
				{
					checked: true,
					label: 'Buyer',
					value: 'Buyer',
				},
			],
			label: 'Account Roles',
			value: 'accountRoles',
		},
		{
			children: [
				{
					checked: false,
					label: 'Administrator',
					value: 'Administrator',
				},
				{
					checked: false,
					label: 'Owner',
					value: 'Owner',
				},
				{
					checked: true,
					label: 'Power User',
					value: 'Power User',
				},
				{
					checked: true,
					label: 'Supplier',
					value: 'Supplier',
				},
			],
			label: 'Regular Roles',
			value: 'regularRoles',
		},
	] as MultiSelectItem[];

	const newRecipients = handleMultiSelectItemsChange(
		itemsGroupMock,
		'roleName'
	);

	expect(newRecipients).toStrictEqual([
		{
			roleName: 'Account Administrator',
		},
		{
			roleName: 'Account Supplier',
		},
		{
			roleName: 'Buyer',
		},
		{
			roleName: 'Power User',
		},
		{
			roleName: 'Supplier',
		},
	]);
});

it('verify that uncheckMultiSelectItemChildrens is unchecking the items', () => {
	const itemsGroupMock = [
		{
			children: [
				{
					checked: true,
					label: 'Account Administrator',
					value: 'Account Administrator',
				},
				{
					checked: false,
					label: 'Account Member',
					value: 'Account Member',
				},
				{
					checked: false,
					label: 'Account Supplier',
					value: 'Account Supplier',
				},
				{
					checked: true,
					label: 'Buyer',
					value: 'Buyer',
				},
			],
			label: 'Account Roles',
			value: 'accountRoles',
		},
	] as MultiSelectItem[];

	const newRecipients = uncheckMultiSelectItemChildrens(itemsGroupMock);

	expect(newRecipients).toStrictEqual([
		{
			children: [
				{
					checked: false,
					label: 'Account Administrator',
					value: 'Account Administrator',
				},
				{
					checked: false,
					label: 'Account Member',
					value: 'Account Member',
				},
				{
					checked: false,
					label: 'Account Supplier',
					value: 'Account Supplier',
				},
				{
					checked: false,
					label: 'Buyer',
					value: 'Buyer',
				},
			],
			label: 'Account Roles',
			value: 'accountRoles',
		},
	]);
});
