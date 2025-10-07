/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Space} from '../../common/types/Space';
import {UserAccount, UserGroup} from '../../common/types/UserAccount';
import ApiHelper from './ApiHelper';

async function addSpace({
	description,
	name,
	settings,
}: {
	description?: string;
	name: string;
	settings?: {logoColor: string};
}) {
	return await ApiHelper.post<{id: number}>(
		'/o/headless-asset-library/v1.0/asset-libraries',
		{
			description,
			name,
			settings,
			type: 'Space',
		}
	);
}

async function getSpace({
	externalReferenceCode,
	spaceId,
}: {
	externalReferenceCode?: string;
	spaceId?: number | string;
}): Promise<Space> {
	let url = `/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${externalReferenceCode}`;

	if (spaceId) {
		url = `/o/headless-asset-library/v1.0/asset-libraries/${spaceId}`;
	}

	const {data, error} = await ApiHelper.get<Space>(url);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function getSpaceUserGroups({
	externalReferenceCode,
	nestedFields,
	page,
	pageSize,
}: {
	externalReferenceCode: string;
	nestedFields?: string;
	page?: number;
	pageSize?: number;
}): Promise<{
	items: UserGroup[];
	lastPage: number;
	page: number;
	totalCount: number;
}> {
	const urlParams = new URLSearchParams();

	if (page) {
		urlParams.set('page', String(page));
	}

	if (pageSize) {
		urlParams.set('pageSize', String(pageSize));
	}

	const {data, error} = await ApiHelper.get<{
		items: UserGroup[];
		lastPage: number;
		page: number;
		totalCount: number;
	}>(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${externalReferenceCode}/user-groups?${urlParams.toString()}${nestedFields ? '&nestedFields=' + nestedFields : ''}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function getSpaceUsers({
	externalReferenceCode,
	nestedFields,
	page,
	pageSize,
}: {
	externalReferenceCode: string;
	nestedFields?: string;
	page?: number;
	pageSize?: number;
}): Promise<{
	items: UserAccount[];
	lastPage: number;
	page: number;
	totalCount: number;
}> {
	const urlParams = new URLSearchParams();

	if (page) {
		urlParams.set('page', String(page));
	}

	if (pageSize) {
		urlParams.set('pageSize', String(pageSize));
	}

	const {data, error} = await ApiHelper.get<{
		items: UserAccount[];
		lastPage: number;
		page: number;
		totalCount: number;
	}>(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${externalReferenceCode}/user-accounts?${urlParams.toString()}${nestedFields ? '&nestedFields=' + nestedFields : ''}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function getSpaces(): Promise<Space[]> {
	return await ApiHelper.getAll<Space>({
		filter: "type eq 'Space'",
		url: '/o/headless-asset-library/v1.0/asset-libraries',
	});
}

async function linkUserToSpace({
	spaceExternalReferenceCode,
	userExternalReferenceCode,
}: {
	spaceExternalReferenceCode: string;
	userExternalReferenceCode: string;
}) {
	return await ApiHelper.put(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${spaceExternalReferenceCode}/user-accounts/by-external-reference-code/${userExternalReferenceCode}`
	);
}

async function linkUserGroupToSpace({
	spaceExternalReferenceCode,
	userGroupExternalReferenceCode,
}: {
	spaceExternalReferenceCode: string;
	userGroupExternalReferenceCode: string;
}) {
	return await ApiHelper.put(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${spaceExternalReferenceCode}/user-groups/by-external-reference-code/${userGroupExternalReferenceCode}`
	);
}

async function unlinkUserFromSpace({
	spaceExternalReferenceCode,
	userExternalReferenceCode,
}: {
	spaceExternalReferenceCode: string;
	userExternalReferenceCode: string;
}) {
	return await ApiHelper.delete(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${spaceExternalReferenceCode}/user-accounts/by-external-reference-code/${userExternalReferenceCode}`
	);
}

async function unlinkUserGroupFromSpace({
	spaceExternalReferenceCode,
	userGroupExternalReferenceCode,
}: {
	spaceExternalReferenceCode: string;
	userGroupExternalReferenceCode: string;
}) {
	return await ApiHelper.delete(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${spaceExternalReferenceCode}/user-groups/by-external-reference-code/${userGroupExternalReferenceCode}`
	);
}

async function updateSpace(externalReferenceCode: string, body: any) {
	return await ApiHelper.patch(
		body,
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${externalReferenceCode}`
	);
}

async function updateUserRoles(payload: {
	roleNames: string[];
	spaceExternalReferenceCode: string;
	userExternalReferenceCode: string;
}) {
	const {roleNames, spaceExternalReferenceCode, userExternalReferenceCode} =
		payload;

	const body = roleNames.map((roleName) => ({
		name: roleName,
	}));

	return await ApiHelper.put(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${spaceExternalReferenceCode}/user-accounts/by-external-reference-code/${userExternalReferenceCode}/roles`,
		body
	);
}

async function updateUserGroupRoles(payload: {
	roleNames: string[];
	spaceExternalReferenceCode: string;
	userGroupExternalReferenceCode: string;
}) {
	const {
		roleNames,
		spaceExternalReferenceCode,
		userGroupExternalReferenceCode,
	} = payload;

	const body = roleNames.map((roleName) => ({
		name: roleName,
	}));

	return await ApiHelper.put(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${spaceExternalReferenceCode}/user-groups/by-external-reference-code/${userGroupExternalReferenceCode}/roles`,
		body
	);
}

export default {
	addSpace,
	getSpace,
	getSpaceUserGroups,
	getSpaceUsers,
	getSpaces,
	linkUserGroupToSpace,
	linkUserToSpace,
	unlinkUserFromSpace,
	unlinkUserGroupFromSpace,
	updateSpace,
	updateUserGroupRoles,
	updateUserRoles,
};
