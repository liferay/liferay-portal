/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const AUTO_CATEGORIZE_AGENT = 'L_AUTO_CATEGORIZE';

export const CATEGORIZE_EVENT = 'cms:aiAssistant:categorize';

export const COMMIT_EVENT = 'cms:aiAssistant:commit';

export const GENERATE_TAGS_AGENT = 'L_GENERATE_TAGS';

export interface CategorizationCommitPayload {
	agent: string;
	suggestions: CategorizationCommitSuggestion[];
}

export interface CategorizationCommitSuggestion {
	id?: number;
	isNew?: boolean;
	name: string;
}
