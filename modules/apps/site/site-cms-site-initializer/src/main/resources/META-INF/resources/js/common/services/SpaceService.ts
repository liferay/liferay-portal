/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Space} from '../../common/types/Space';
import ApiHelper, {RequestResult} from './ApiHelper';

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
	externalReferenceCode: string,
	scopeKey: string
): Promise<Space> {
	const cacheKey = JSON.stringify([externalReferenceCode, scopeKey]);

	if (spaceCache.has(cacheKey)) {
		return spaceCache.get(cacheKey)!;
	}

	const fetchPromise = getSpace(externalReferenceCode).catch((error) => {
		spaceCache.delete(cacheKey);
		throw error;
	});

	spaceCache.set(cacheKey, fetchPromise);

	return fetchPromise;
}

async function getSpaceContents({
	page,
	pageSize,
	path,
	siteId,
}: {
	page?: number;
	pageSize?: number;
	path: string;
	siteId: number;
}): Promise<RequestResult<{totalCount: number}>> {
	const urlParams = new URLSearchParams();

	if (page) {
		urlParams.set('page', String(page));
	}

	if (pageSize) {
		urlParams.set('pageSize', String(pageSize));
	}

	return await ApiHelper.get<{
		totalCount: number;
	}>(`${path}/scopes/${siteId}?${urlParams.toString()}`);
}

async function getSpaces(): Promise<Space[]> {
	return await ApiHelper.getAll<Space>({
		filter: "type eq 'Space'",
		url: '/o/headless-asset-library/v1.0/asset-libraries',
	});
}

async function updateSpace(externalReferenceCode: string, body: any) {
	return await ApiHelper.patch(
		body,
		`/o/headless-asset-library/v1.0/asset-libraries/${externalReferenceCode}`
	);
}

export default {
	addSpace,
	getSpace,
	getSpaceContents,
	getSpaceWithCache,
	getSpaces,
	updateSpace,
};
