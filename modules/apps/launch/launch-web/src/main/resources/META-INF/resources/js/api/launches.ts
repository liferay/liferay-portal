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

async function readJSON(response: Response, message: string) {
	const text = await response.text();

	if (!response.ok || !text) {
		throw new Error(message);
	}

	try {
		return JSON.parse(text);
	}
	catch (error) {
		throw new Error(message);
	}
}

export interface Launch {
	dateModified?: string;
	description?: string;
	id: number;
	name: string;
	status?: {code: number};
}

const WORKFLOW_STATUS_APPROVED = 0;

export function isPublished(launch: Launch): boolean {
	return launch.status?.code === WORKFLOW_STATUS_APPROVED;
}

export interface LaunchEntry {
	baseVersionKey: string;
	className: string;
	classPK: number;
	draftId: number;
	id: number;
	r_launchSetToLaunchEntries_c_launchSetId: number;
	versionKey?: string;
}

export interface LaunchEntrySummary {
	author: string;
	editURL: string | null;
	modified: string;
	space: string;
	status: number;
	title: string;
	type: string;
	version: string;
}

export interface LaunchEntryVersion {
	label: string;
	modified: string;
	status: number;
	userName: string;
	versionKey: string;
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
	baseVersionKey,
	className,
	classPK,
	launchSetId,
}: {
	baseVersionKey: string;
	className: string;
	classPK: number;
	launchSetId: number;
}): Promise<LaunchEntry> {
	const response = await fetch(LAUNCH_ENTRIES_BASE_URL, {
		body: JSON.stringify({
			baseVersionKey,
			className,
			classPK,
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
}: {
	className: string;
	classPK: number;
}): Promise<LaunchEntry[]> {
	const filter = encodeURIComponent(
		`className eq '${className}' and classPK eq ${classPK}`
	);

	const response = await fetch(
		`${LAUNCH_ENTRIES_BASE_URL}?filter=${filter}&pageSize=-1`,
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

export async function listLaunchEntries({
	className,
	classPK,
	launchSetId,
}: {
	className: string;
	classPK: number;
	launchSetId: number;
}): Promise<LaunchEntry[]> {
	const filter = encodeURIComponent(
		`className eq '${className}' and classPK eq ${classPK} and r_launchSetToLaunchEntries_c_launchSetId eq '${launchSetId}'`
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

export async function getLaunchEntrySummary({
	className,
	classPK,
	draftId,
	portletNamespace,
	resourceURL,
	versionKey,
}: {
	className: string;
	classPK: number;
	draftId: number;
	portletNamespace: string;
	resourceURL: string;
	versionKey?: string;
}): Promise<LaunchEntrySummary> {
	const url = new URL(resourceURL, window.location.origin);

	url.searchParams.set(`${portletNamespace}className`, className);
	url.searchParams.set(`${portletNamespace}classPK`, String(classPK));
	url.searchParams.set(`${portletNamespace}draftId`, String(draftId));
	url.searchParams.set(`${portletNamespace}redirect`, window.location.href);
	url.searchParams.set(`${portletNamespace}versionKey`, versionKey ?? '');

	return readJSON(
		await fetch(url.toString()),
		Liferay.Language.get('unable-to-load-a-launch-entry')
	);
}

export async function deleteLaunchEntry(id: number): Promise<void> {
	const response = await fetch(`${LAUNCH_ENTRIES_BASE_URL}/${id}`, {
		headers: DEFAULT_HEADERS,
		method: 'DELETE',
	});

	if (!response.ok) {
		throw new Error(
			Liferay.Language.get('unable-to-remove-a-launch-entry')
		);
	}
}

export async function updateLaunch({
	description,
	id,
	name,
}: {
	description: string;
	id: number;
	name: string;
}): Promise<Launch> {
	const response = await fetch(`${LAUNCH_SETS_BASE_URL}/${id}`, {
		body: JSON.stringify({description, name}),
		headers: DEFAULT_HEADERS,
		method: 'PATCH',
	});

	if (!response.ok) {
		throw new Error(Liferay.Language.get('unable-to-save-a-launch'));
	}

	return response.json();
}

export async function resolveLaunchEntry({
	className,
	portletNamespace,
	resourceURL,
	selectedItemData,
}: {
	className: string;
	portletNamespace: string;
	resourceURL: string;
	selectedItemData: Record<string, string>;
}): Promise<{baseVersionKey: string; classPK: number}> {
	const url = new URL(resourceURL, window.location.origin);

	url.searchParams.set(`${portletNamespace}className`, className);
	url.searchParams.set(
		`${portletNamespace}selectedItemData`,
		JSON.stringify(selectedItemData)
	);

	const {baseVersionKey, classPK} = await readJSON(
		await fetch(url.toString()),
		Liferay.Language.get('unable-to-read-the-selected-item')
	);

	if (!classPK) {
		throw new Error(
			Liferay.Language.get('this-item-does-not-support-launches')
		);
	}

	return {baseVersionKey, classPK};
}

export async function getLaunchEntryVersions({
	className,
	classPK,
	portletNamespace,
	resourceURL,
}: {
	className: string;
	classPK: number;
	portletNamespace: string;
	resourceURL: string;
}): Promise<LaunchEntryVersion[]> {
	const url = new URL(resourceURL, window.location.origin);

	url.searchParams.set(`${portletNamespace}className`, className);
	url.searchParams.set(`${portletNamespace}classPK`, String(classPK));

	return readJSON(
		await fetch(url.toString()),
		Liferay.Language.get('unable-to-list-the-versions-of-the-item')
	);
}

export async function rebaseLaunchEntry({
	baseVersionKey,
	className,
	classPK,
	id,
}: {
	baseVersionKey: string;
	className: string;
	classPK: number;
	id: number;
}): Promise<LaunchEntry> {
	const response = await fetch(`${LAUNCH_ENTRIES_BASE_URL}/${id}`, {
		body: JSON.stringify({
			baseVersionKey,
			className,
			classPK,
			draftId: 0,
		}),
		headers: DEFAULT_HEADERS,
		method: 'PATCH',
	});

	if (!response.ok) {
		throw new Error(
			Liferay.Language.get('unable-to-change-the-version-of-the-item')
		);
	}

	return response.json();
}
