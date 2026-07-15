/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface ContentDraft {
	editURL: string;
	title: string;
}

function isContentEditURL(url: string) {
	return url.includes('/cms/edit_content_item');
}

export function parseContentDraftsMessage(markdown: string): {
	drafts: ContentDraft[];
	text: string;
} {
	const drafts: ContentDraft[] = [];

	const text = markdown
		.replace(/\[([^\]]+)\]\(([^)]+)\)/g, (match, title, url) => {
			const editURL = url.trim().split(/\s+/)[0];

			if (!isContentEditURL(editURL)) {
				return match;
			}

			drafts.push({editURL, title});

			return '';
		})
		.replace(/^[ \t]*[-*][ \t]*$/gm, '')
		.replace(/\n{3,}/g, '\n\n')
		.trim();

	return {drafts, text};
}

export default parseContentDraftsMessage;
