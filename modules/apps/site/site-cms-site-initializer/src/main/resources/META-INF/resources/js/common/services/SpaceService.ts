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

async function getSpace(externalReferenceCode: string): Promise<Space> {
	const url = `/o/headless-asset-library/v1.0/asset-libraries/${externalReferenceCode}`;

	const {data, error} = await ApiHelper.get<Space>(url);

	if (data) {
		return data;
	}

	throw new Error(error || 'Failed to fetch space data.');
}

const spaceCache = new Map<string, Promise<Space>>();

async function getSpaceWithCache(
	externalReferenceCode: string
): Promise<Space> {
	if (spaceCache.has(externalReferenceCode)) {
		return spaceCache.get(externalReferenceCode)!;
	}

	const fetchPromise = getSpace(externalReferenceCode).catch((error) => {
		spaceCache.delete(externalReferenceCode);
		throw error;
	});

	spaceCache.set(externalReferenceCode, fetchPromise);

	return fetchPromise;
}

async function getSpaceUserGroups({
	externalReferenceCode,
	keywords,
	nestedFields,
	page,
	pageSize,
}: {
	externalReferenceCode: string;
	keywords?: string;
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

	if (keywords) {
		urlParams.set('keywords', keywords);
	}

	const {data, error} = await ApiHelper.get<{
		items: UserGroup[];
		lastPage: number;
		page: number;
		totalCount: number;
	}>(
		`/o/headless-asset-library/v1.0/asset-libraries/${externalReferenceCode}/user-groups?${urlParams.toString()}${nestedFields ? '&nestedFields=' + nestedFields : ''}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function getSpaceUsers({
	externalReferenceCode,
	keywords,
	nestedFields,
	page,
	pageSize,
}: {
	externalReferenceCode: string;
	keywords?: string;
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

	if (keywords) {
		urlParams.set('keywords', keywords);
	}

	const {data, error} = await ApiHelper.get<{
		items: UserAccount[];
		lastPage: number;
		page: number;
		totalCount: number;
	}>(
		`/o/headless-asset-library/v1.0/asset-libraries/${externalReferenceCode}/user-accounts?${urlParams.toString()}${nestedFields ? '&nestedFields=' + nestedFields : ''}`
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
		`/o/headless-asset-library/v1.0/asset-libraries/${spaceExternalReferenceCode}/user-accounts/${userExternalReferenceCode}`
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
		`/o/headless-asset-library/v1.0/asset-libraries/${spaceExternalReferenceCode}/user-groups/${userGroupExternalReferenceCode}`
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
		`/o/headless-asset-library/v1.0/asset-libraries/${spaceExternalReferenceCode}/user-accounts/${userExternalReferenceCode}`
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
		`/o/headless-asset-library/v1.0/asset-libraries/${spaceExternalReferenceCode}/user-groups/${userGroupExternalReferenceCode}`
	);
}

async function updateSpace(externalReferenceCode: string, body: any) {
	return await ApiHelper.patch(
		body,
		`/o/headless-asset-library/v1.0/asset-libraries/${externalReferenceCode}`
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
		`/o/headless-asset-library/v1.0/asset-libraries/${spaceExternalReferenceCode}/user-accounts/${userExternalReferenceCode}/roles`,
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
		`/o/headless-asset-library/v1.0/asset-libraries/${spaceExternalReferenceCode}/user-groups/${userGroupExternalReferenceCode}/roles`,
		body
	);
}

export default {
	addSpace,
	getSpace,
	getSpaceUserGroups,
	getSpaceUsers,
	getSpaceWithCache,
	getSpaces,
	linkUserGroupToSpace,
	linkUserToSpace,
	unlinkUserFromSpace,
	unlinkUserGroupFromSpace,
	updateSpace,
	updateUserGroupRoles,
	updateUserRoles,
};
