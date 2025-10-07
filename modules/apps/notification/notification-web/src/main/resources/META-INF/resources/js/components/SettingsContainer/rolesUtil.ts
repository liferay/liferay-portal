/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MultiSelectItem} from '@liferay/object-js-components-web';
import {createResourceURL, fetch} from 'frontend-js-web';

import {HEADERS} from '../../util/constants';

export interface Role {
	description: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	roleType: string;
}
export interface Roles {
	items: Role[];
	totalCount: number;
}
interface RolesGroup {
	accountRoles: LabelNameObject[];
	organizationRoles: LabelNameObject[];
	regularRoles: LabelNameObject[];
}

const roleGroupLabels = {
	accountRoles: Liferay.Language.get('account-roles'),
	organizationRoles: Liferay.Language.get('organization-roles'),
	regularRoles: Liferay.Language.get('regular-roles'),
};

export async function getEmailNotificationRoles(baseResourceURL: string) {
	const response = await fetch(
		createResourceURL(baseResourceURL, {
			p_p_resource_id:
				'/notification_templates/get_email_notification_roles',
		}).toString()
	);

	const rolesResponse = (await response.json()) as RolesGroup;
	const roles = [] as MultiSelectItem[];

	(
		Object.entries(rolesResponse) as [keyof RolesGroup, LabelNameObject[]][]
	).forEach(([roleGroupKey, roleValues]) => {
		roles.push({
			children: roleValues.map(({label, name}) => {
				return {
					checked: false,
					label,
					value: name,
				};
			}),
			label: roleGroupLabels[roleGroupKey],
			value: roleGroupKey,
		});
	});

	return roles;
}

export async function getRoles() {
	const apiURL = `/o/headless-admin-user/v1.0/roles`;
	const query = `${apiURL}?page=-1&restrictFields=rolePermissions`;

	const response = await fetch(query, {
		headers: HEADERS,
		method: 'GET',
	});

	return ((await response.json()) as Roles).items;
}

export function getUserNotificationRoles(
	rolesItems: Role[],
	recipients: {roleName: string}[]
) {
	const roles = {
		children: rolesItems
			.filter(({name}) => name !== 'Guest')
			.map(({name}) => {
				const selectedRole = !!recipients.find(
					({roleName}) => roleName === name
				);

				return {
					checked: selectedRole,
					label: name,
					value: name,
				};
			}),
		label: '',
		value: 'rolesList',
	} as MultiSelectItem;

	return roles;
}
