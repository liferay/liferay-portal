/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface ContentDraft {
	editURL: string;
	symbol: string;
	title: string;
}

const EDIT_URL_SYMBOLS = {
	'/cms/edit_content_item': 'blogs',
	'edit_fragment_entry': 'code',
};

function getEditURLSymbol(url: string) {
	for (const [urlPart, symbol] of Object.entries(EDIT_URL_SYMBOLS)) {
		if (url.includes(urlPart)) {
			return symbol;
		}
	}

	return null;
}

export function parseContentDraftsMessage(markdown: string): {
	drafts: ContentDraft[];
	text: string;
} {
	const drafts: ContentDraft[] = [];

	const text = markdown
		.replace(/\[([^\]]+)\]\s*\(([^)]+)\)/g, (match, title, url) => {
			const editURL = url
				.trim()
				.split(/\s+["']/)[0]
				.replace(/\s+/g, '')
				.replace(/\\(.)/g, '$1');

			const symbol = getEditURLSymbol(editURL);

			if (!symbol) {
				return match;
			}

			drafts.push({editURL, symbol, title});

			return '';
		})
		.replace(/^[ \t]*[-*][ \t]*$/gm, '')
		.replace(/\n{3,}/g, '\n\n')
		.trim();

	return {drafts, text};
}

export default parseContentDraftsMessage;
