/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const DEFAULT_HEADERS = {
	'Content-Type': 'application/json',
};

const LAUNCH_ENTRIES_BASE_URL = '/o/launch-entries';
const LAUNCH_SETS_BASE_URL = '/o/launch-sets';

export interface Launch {
	dateModified?: string;
	description?: string;
	id: number;
	name: string;
	status?: {code: number};
}

export interface LaunchEntry {
	className: string;
	classPK: number;
	classVersion: string;
	id: number;
	r_launchSetToLaunchEntries_c_launchSetId: number;
}

export interface LaunchEntryContent {
	author: string;
	modified: string;
	space: string;
	status: number;
	title: string;
	type: string;
	version: string;
}

export async function createLaunch({
	description,
	name,
}: {
	description: string;
	name: string;
}): Promise<Launch> {
	const response = await fetch(LAUNCH_SETS_BASE_URL, {
		body: JSON.stringify({description, name, status: {code: 2}}),
		headers: DEFAULT_HEADERS,
		method: 'POST',
	});

	if (!response.ok) {
		const error = await response.json().catch(() => ({}));

		throw new Error(
			error.title || Liferay.Language.get('unable-to-create-a-launch')
		);
	}

	return response.json();
}

export async function createLaunchEntry({
	className,
	classPK,
	classVersion,
	launchSetId,
}: {
	className: string;
	classPK: number;
	classVersion: string;
	launchSetId: number;
}): Promise<LaunchEntry> {
	const response = await fetch(LAUNCH_ENTRIES_BASE_URL, {
		body: JSON.stringify({
			className,
			classPK,
			classVersion,
			r_launchSetToLaunchEntries_c_launchSetId: launchSetId,
		}),
		headers: DEFAULT_HEADERS,
		method: 'POST',
	});

	if (!response.ok) {
		const error = await response.json().catch(() => ({}));

		throw new Error(
			error.title ||
				Liferay.Language.get('unable-to-add-the-item-to-a-launch')
		);
	}

	return response.json();
}

export async function getLaunch(id: number): Promise<Launch> {
	const response = await fetch(`${LAUNCH_SETS_BASE_URL}/${id}`, {
		headers: DEFAULT_HEADERS,
	});

	if (!response.ok) {
		throw new Error(Liferay.Language.get('unable-to-load-a-launch'));
	}

	return response.json();
}

export async function listLaunchEntriesForAsset({
	className,
	classPK,
	classVersion,
}: {
	className: string;
	classPK: number;
	classVersion: string;
}): Promise<LaunchEntry[]> {
	const filter = encodeURIComponent(
		`className eq '${className}' and classPK eq ${classPK} and classVersion eq '${classVersion}'`
	);

	const response = await fetch(
		`${LAUNCH_ENTRIES_BASE_URL}?filter=${filter}`,
		{
			headers: DEFAULT_HEADERS,
		}
	);

	if (!response.ok) {
		throw new Error(Liferay.Language.get('unable-to-list-launch-entries'));
	}

	const data = await response.json();

	return data.items || [];
}

export async function listLaunches({
	pageSize = 50,
}: {pageSize?: number} = {}): Promise<Launch[]> {
	const response = await fetch(
		`${LAUNCH_SETS_BASE_URL}?pageSize=${pageSize}`,
		{
			headers: DEFAULT_HEADERS,
		}
	);

	if (!response.ok) {
		throw new Error(Liferay.Language.get('unable-to-list-launches'));
	}

	const data = await response.json();

	return data.items || [];
}

export async function listLaunchEntries(
	launchSetId: number
): Promise<LaunchEntry[]> {
	const filter = encodeURIComponent(
		`r_launchSetToLaunchEntries_c_launchSetId eq '${launchSetId}'`
	);

	const response = await fetch(
		`${LAUNCH_ENTRIES_BASE_URL}?filter=${filter}`,
		{
			headers: DEFAULT_HEADERS,
		}
	);

	if (!response.ok) {
		throw new Error(Liferay.Language.get('unable-to-list-launch-entries'));
	}

	const data = await response.json();

	return data.items || [];
}

export async function getLaunchEntryContent({
	className,
	classPK,
	classVersion,
	portletNamespace,
	resourceURL,
}: {
	className: string;
	classPK: number;
	classVersion: string;
	portletNamespace: string;
	resourceURL: string;
}): Promise<LaunchEntryContent> {
	const url = new URL(resourceURL, window.location.origin);

	url.searchParams.set(`${portletNamespace}className`, className);
	url.searchParams.set(`${portletNamespace}classPK`, String(classPK));
	url.searchParams.set(`${portletNamespace}classVersion`, classVersion);

	const response = await fetch(url.toString());

	if (!response.ok) {
		throw new Error(Liferay.Language.get('unable-to-load-a-launch-entry'));
	}

	return response.json();
}
