/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams, fetch} from 'frontend-js-web';

import {
	FetchMembersArgs,
	Page,
	Role,
	ServiceResult,
	UserAccount,
	UserGroup,
} from '../types';

const BASE_PATH = '/o/headless-asset-library/v1.0/asset-libraries';

interface RequestOptions extends RequestInit {
	params?: Record<string, string>;
}

async function readErrorMessage(response: Response): Promise<string> {
	try {
		const data = await response.json();

		return data.title || data.message || response.statusText;
	}
	catch (error) {
		return response.statusText;
	}
}

async function request<T>(
	path: string,
	{params, ...init}: RequestOptions = {}
): Promise<T> {
	const pathContext = Liferay.ThemeDisplay.getPathContext() || '';

	const response = await fetch(
		addParams(params ?? {}, `${pathContext}${BASE_PATH}${path}`),
		{
			headers: new Headers({
				'Accept': 'application/json',
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
				'Content-Type': 'application/json',
			}),
			...init,
		}
	);

	if (!response.ok) {
		throw new Error(await readErrorMessage(response));
	}

	if (response.status === 204) {
		return null as unknown as T;
	}

	return response.json();
}

async function mutate(path: string, init: RequestInit): Promise<ServiceResult> {
	try {
		await request(path, init);

		return {error: null};
	}
	catch (error) {
		return {error: (error as Error).message};
	}
}

function toParams(values: {
	fields?: string;
	keywords?: string;
	nestedFields?: string;
	page?: number;
	pageSize?: number;
}): Record<string, string> {
	const params: Record<string, string> = {};

	if (values.fields) {
		params.fields = values.fields;
	}

	if (values.keywords) {
		params.keywords = values.keywords;
	}

	if (values.nestedFields) {
		params.nestedFields = values.nestedFields;
	}

	if (values.page) {
		params.page = String(values.page);
	}

	if (values.pageSize) {
		params.pageSize = String(values.pageSize);
	}

	return params;
}

export function getMemberGroups({
	externalReferenceCode,
	keywords,
	nestedFields,
	page,
	pageSize,
}: FetchMembersArgs): Promise<Page<UserGroup>> {
	return request(`/${externalReferenceCode}/user-groups`, {
		params: toParams({keywords, nestedFields, page, pageSize}),
	});
}

export function getMembers({
	externalReferenceCode,
	keywords,
	nestedFields,
	page,
	pageSize,
}: FetchMembersArgs): Promise<Page<UserAccount>> {
	return request(`/${externalReferenceCode}/user-accounts`, {
		params: toParams({keywords, nestedFields, page, pageSize}),
	});
}

export function getRoles({
	externalReferenceCode,
	fields,
	pageSize,
}: {
	externalReferenceCode: string;
	fields?: string;
	pageSize?: number;
}): Promise<Page<Role>> {
	return request(`/${externalReferenceCode}/roles`, {
		params: toParams({fields, pageSize}),
	});
}

export function linkMember({
	externalReferenceCode,
	memberExternalReferenceCode,
}: {
	externalReferenceCode: string;
	memberExternalReferenceCode: string;
}): Promise<ServiceResult> {
	return mutate(
		`/${externalReferenceCode}/user-accounts/${memberExternalReferenceCode}`,
		{method: 'PUT'}
	);
}

export function linkMemberGroup({
	externalReferenceCode,
	memberGroupExternalReferenceCode,
}: {
	externalReferenceCode: string;
	memberGroupExternalReferenceCode: string;
}): Promise<ServiceResult> {
	return mutate(
		`/${externalReferenceCode}/user-groups/${memberGroupExternalReferenceCode}`,
		{method: 'PUT'}
	);
}

export function unlinkMember({
	externalReferenceCode,
	memberExternalReferenceCode,
}: {
	externalReferenceCode: string;
	memberExternalReferenceCode: string;
}): Promise<ServiceResult> {
	return mutate(
		`/${externalReferenceCode}/user-accounts/${memberExternalReferenceCode}`,
		{method: 'DELETE'}
	);
}

export function unlinkMemberGroup({
	externalReferenceCode,
	memberGroupExternalReferenceCode,
}: {
	externalReferenceCode: string;
	memberGroupExternalReferenceCode: string;
}): Promise<ServiceResult> {
	return mutate(
		`/${externalReferenceCode}/user-groups/${memberGroupExternalReferenceCode}`,
		{method: 'DELETE'}
	);
}

export function updateMemberGroupRoles({
	externalReferenceCode,
	memberGroupExternalReferenceCode,
	roleNames,
}: {
	externalReferenceCode: string;
	memberGroupExternalReferenceCode: string;
	roleNames: string[];
}): Promise<ServiceResult> {
	return mutate(
		`/${externalReferenceCode}/user-groups/${memberGroupExternalReferenceCode}/roles`,
		{
			body: JSON.stringify(roleNames.map((name) => ({name}))),
			method: 'PUT',
		}
	);
}

export function updateMemberRoles({
	externalReferenceCode,
	memberExternalReferenceCode,
	roleNames,
}: {
	externalReferenceCode: string;
	memberExternalReferenceCode: string;
	roleNames: string[];
}): Promise<ServiceResult> {
	return mutate(
		`/${externalReferenceCode}/user-accounts/${memberExternalReferenceCode}/roles`,
		{
			body: JSON.stringify(roleNames.map((name) => ({name}))),
			method: 'PUT',
		}
	);
}
