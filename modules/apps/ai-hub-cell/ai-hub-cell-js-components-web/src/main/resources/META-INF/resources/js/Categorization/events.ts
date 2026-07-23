/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ECategorizationAgent, IntentAction, Suggestion} from './types';

export const CATEGORIZE_EVENT = 'cms:aiAssistant:categorize';

export const COMMIT_EVENT = 'cms:aiAssistant:commit';

export const REQUEST_CATEGORIZE_EVENT = 'cms:aiAssistant:requestCategorize';

export interface CategorizeEventPayload {
	agent: ECategorizationAgent;
	classNameId?: number;
	cmsGroupId: number | string;
	content: string;
	count?: number;
	currentCategoryIds?: number[];
	currentTagNames?: string[];
	scopeId: number;
	suppressUserMessage?: boolean;
	targets?: string[];
}

export interface CommitEventPayload {
	agent: ECategorizationAgent;
	scopeId: number;
	suggestions: Suggestion[];
}

export interface RequestCategorizePayload {
	actions: IntentAction[];
}
