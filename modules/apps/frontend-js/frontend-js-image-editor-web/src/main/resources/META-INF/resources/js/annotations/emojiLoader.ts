/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {EmojiEntry} from './emojiData';

export interface EmojiCatalog {
	byCharacter: Map<string, EmojiEntry>;
	entries: EmojiEntry[];
	searchKeys: string[];
}

let cache: EmojiCatalog | null = null;

export async function loadEmojiCatalog(): Promise<EmojiCatalog> {
	if (!cache) {
		const {EMOJI} = await import('./emojiData');

		cache = {
			byCharacter: new Map(EMOJI.map((entry) => [entry.c, entry])),
			entries: EMOJI,
			searchKeys: EMOJI.map((entry) => entry.n.toLowerCase()),
		};
	}

	return cache;
}
