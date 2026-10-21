/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';

import {DataMask, TreeFilter, TreeItem} from './types';

type ToastMessageOptions = {
	dangerouslySetMessageHTML?: boolean;
};

export function filterTree<T extends TreeItem<T>>(
	tree: T[],
	matches: (item: T) => boolean
): TreeFilter {
	const expandedKeys = new Set<string>();
	const visibleKeys = new Set<string>();

	const matchCount = collectVisibleKeys(
		tree,
		matches,
		expandedKeys,
		visibleKeys
	);

	return {expandedKeys, matchCount, visibleKeys};
}

export function isSystemMask(dataMask: DataMask | null): boolean {
	return dataMask?.maskType?.key === 'system';
}

export function openErrorToast(
	message: string,
	options?: ToastMessageOptions
): void {
	openToast({
		message: toToastMessage(message, options),
		type: 'danger',
	});
}

export function openSuccessToast(
	message: string,
	options?: ToastMessageOptions
): void {
	openToast({
		message: toToastMessage(message, options),
		type: 'success',
	});
}

export function required(value: string): string | undefined {
	return value?.trim()
		? undefined
		: Liferay.Language.get('this-field-is-required');
}

export function toIdentifier(name: string): string {
	return name
		.toLowerCase()
		.replace(/[^a-z0-9]+/g, '-')
		.replace(/^-+|-+$/g, '');
}

export function toODataStringLiteral(value: string): string {
	return `'${value.replace(/'/g, "''")}'`;
}

function collectVisibleKeys<T extends TreeItem<T>>(
	tree: T[],
	matches: (item: T) => boolean,
	expandedKeys: Set<string>,
	visibleKeys: Set<string>
): number {
	let matchCount = 0;

	for (const item of tree) {
		const childMatchCount = collectVisibleKeys(
			item.children ?? [],
			matches,
			expandedKeys,
			visibleKeys
		);

		if (childMatchCount) {
			expandedKeys.add(item.id);
		}

		const itemMatches = matches(item);

		if (itemMatches || childMatchCount) {
			visibleKeys.add(item.id);
		}

		matchCount += childMatchCount + Number(itemMatches);
	}

	return matchCount;
}

function toToastMessage(
	message: string,
	options?: ToastMessageOptions
): string {
	if (options?.dangerouslySetMessageHTML) {
		return message;
	}

	return Liferay.Util.escapeHTML(message);
}
