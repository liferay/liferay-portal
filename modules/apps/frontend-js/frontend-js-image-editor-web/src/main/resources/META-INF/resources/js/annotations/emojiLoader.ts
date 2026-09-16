/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

interface EmojiRow {
	c: string;

	g: number;

	n: string;
}

export interface EmojiEntry extends EmojiRow {

	/**
	 * The name the search reads, folded once here rather than on every
	 * keystroke.
	 */
	search: string;
}

export interface EmojiCatalog {
	byCharacter: Map<string, EmojiEntry>;
	entries: EmojiEntry[];
}

let pending: Promise<EmojiCatalog> | null = null;

/**
 * The catalogue is a resource of the module rather than part of its
 * bundle, and it is asked for the first time the picker opens: nineteen
 * hundred names are a quarter of the editor's weight, and most sessions
 * never open the picker at all. A dynamic import would not do, because
 * the portal build bundles without code splitting and would inline it.
 */
export function loadEmojiCatalog(): Promise<EmojiCatalog> {
	if (!pending) {
		pending = fetch('/o/frontend-js-image-editor-web/emoji.json')
			.then((response: Response) => {
				if (!response.ok) {
					throw new Error(
						`The emoji catalog answered ${response.status}`
					);
				}

				return response.json();
			})
			.then((rows: EmojiRow[]) => {
				const entries = rows.map((row) => ({
					...row,
					search: row.n.toLowerCase(),
				}));

				return {
					byCharacter: new Map(
						entries.map((entry) => [entry.c, entry])
					),
					entries,
				};
			})
			.catch((error: unknown) => {

				// A failed load must not poison the cache: the next time
				// the picker opens is a new chance to reach the catalog.

				pending = null;

				throw error;
			});
	}

	return pending;
}
