/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CategorizationCommitSuggestion} from '../../../common/services/CategorizationSuggestionService';

export const AUTO_CATEGORIZE_AGENT = 'L_AUTO_CATEGORIZE';

export const CATEGORIZE_EVENT = 'cms:aiAssistant:categorize';

export const COMMIT_EVENT = 'cms:aiAssistant:commit';

export const GENERATE_TAGS_AGENT = 'L_GENERATE_TAGS';

export const REQUEST_CATEGORIZE_EVENT = 'cms:aiAssistant:requestCategorize';

export interface CategorizationAction {
	agent: 'categorize' | 'tag';
	count?: number;
	targets?: string[];
}

export interface CategorizationCommitPayload {
	agent: typeof AUTO_CATEGORIZE_AGENT | typeof GENERATE_TAGS_AGENT;
	scopeId?: number | string;
	suggestions: CategorizationCommitSuggestion[];
}

export type {CategorizationCommitSuggestion};

export interface CategorizeEventPayload {
	agent: typeof AUTO_CATEGORIZE_AGENT | typeof GENERATE_TAGS_AGENT;
	classNameId?: number;
	cmsGroupId: number | string;
	content: string;
	count?: number;
	currentCategoryIds?: number[];
	currentTagNames?: string[];
	scopeId: number | string;
	suppressUserMessage?: boolean;
	targets?: string[];
}

export interface RequestCategorizePayload {
	actions: CategorizationAction[];
}
