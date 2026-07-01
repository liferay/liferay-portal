/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch as liferayFetch} from 'frontend-js-web';

import {buildPages} from '../util/contentModel';

import type {GeneratedPage} from '../types/GeneratedPage';
import type {Generation} from '../types/Generation';
import type {GenerationItem} from '../types/GenerationItem';

const MAX_BATCH_FILE_SIZE = 2 * 1024 * 1024;

interface CreateGenerationInput {
	prompt: string;
	title: string;
}

async function call<T>(url: string, init?: RequestInit): Promise<T> {
	const response = await liferayFetch(url, {
		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/json',
		},
		...init,
	});

	if (!response.ok) {
		let detail = '';

		try {
			const json = await response.json();

			detail = json.title || json.detail || '';
		}
		catch {}

		throw new Error(
			detail ||
				`${response.status} ${response.statusText || 'Request failed'}`
		);
	}

	if (response.status === 204) {
		return undefined as unknown as T;
	}

	return response.json();
}

export function commitGeneration(
	apiURL: string,
	generationId: number
): Promise<void> {
	return call(`${apiURL}/${generationId}/object-actions/commit`, {
		method: 'PUT',
	});
}

export function createGeneration(
	apiURL: string,
	{prompt, title}: CreateGenerationInput
): Promise<Generation> {
	return call(apiURL, {
		body: JSON.stringify({
			generationStatus: {key: 'refining'},
			prompt,
			title,
		}),
		method: 'POST',
	});
}

export function getGeneration(
	apiURL: string,
	generationId: number
): Promise<Generation> {
	return call(`${apiURL}/${generationId}`);
}

export async function getGenerationItems(
	apiURL: string,
	generationId: number
): Promise<GenerationItem[]> {
	const page = await call<{items?: GenerationItem[]}>(
		`${apiURL}/${generationId}/items?pageSize=100&sort=loadOrder:asc`
	);

	return page.items ?? [];
}

export async function getGenerationPages(
	items: GenerationItem[]
): Promise<GeneratedPage[]> {
	const pageGroups = await Promise.all(items.map(_getItemPages));

	return pageGroups.flat();
}

async function _getItemPages(item: GenerationItem): Promise<GeneratedPage[]> {
	let entries: Record<string, unknown>[] | null = null;

	const href = item.batchFile?.link?.href;

	if (href) {
		try {
			const response = await liferayFetch(href, {
				headers: {Accept: 'application/json'},
			});

			const contentLength = Number(
				response.headers.get('Content-Length')
			);

			if (response.ok && contentLength <= MAX_BATCH_FILE_SIZE) {
				const parsed = JSON.parse(await response.text());

				const list = Array.isArray(parsed) ? parsed : parsed?.items;

				if (Array.isArray(list)) {
					entries = list;
				}
			}
		}
		catch (exception) {
			entries = null;
		}
	}

	if (!entries && item.previewItem) {
		try {
			entries = [JSON.parse(item.previewItem)];
		}
		catch (exception) {
			entries = null;
		}
	}

	return entries ? buildPages(item, entries) : [];
}
