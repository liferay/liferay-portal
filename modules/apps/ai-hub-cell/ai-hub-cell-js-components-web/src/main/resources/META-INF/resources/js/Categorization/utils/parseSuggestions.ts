/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CandidateCategory,
	CategorizationContext,
	ECategorizationAgent,
	Suggestion,
} from '../types';

const DEFAULT_COUNT = 3;

interface RawSuggestion {
	confidence?: number;
	id?: number | string;
	isNew?: boolean;
	name?: string;
}

export function stripCodeFences(text: string): string {
	const trimmed = text.trim();

	const match = trimmed.match(/```(?:json)?\s*([\s\S]*?)```/i);

	return match ? match[1].trim() : trimmed;
}

function parseCategories(
	rawSuggestions: RawSuggestion[],
	candidateCategories: CandidateCategory[],
	appliedCategoryIds: number[],
	count: number
): Suggestion[] {
	const applied = new Set(appliedCategoryIds);

	const candidatesById = new Map<number, CandidateCategory>();

	candidateCategories.forEach((candidate) =>
		candidatesById.set(candidate.id, candidate)
	);

	const seen = new Set<number>();
	const suggestions: Suggestion[] = [];

	rawSuggestions.forEach((rawSuggestion) => {
		if (suggestions.length >= count) {
			return;
		}

		const id =
			typeof rawSuggestion.id === 'string'
				? Number(rawSuggestion.id)
				: rawSuggestion.id;

		if (
			id === undefined ||
			!Number.isInteger(id) ||
			applied.has(id) ||
			seen.has(id)
		) {
			return;
		}

		const candidate = candidatesById.get(id);

		if (!candidate) {
			return;
		}

		seen.add(id);

		suggestions.push({id, name: candidate.name});
	});

	return suggestions;
}

function parseTags(
	rawSuggestions: RawSuggestion[],
	appliedTags: string[],
	count: number
): Suggestion[] {
	const applied = new Set(
		appliedTags.map((appliedTag) => appliedTag.toLowerCase())
	);

	const seen = new Set<string>();
	const suggestions: Suggestion[] = [];

	rawSuggestions.forEach((rawSuggestion) => {
		if (suggestions.length >= count) {
			return;
		}

		const name = (rawSuggestion.name ?? '').trim();

		if (
			!name ||
			applied.has(name.toLowerCase()) ||
			seen.has(name.toLowerCase())
		) {
			return;
		}

		seen.add(name.toLowerCase());

		suggestions.push({isNew: Boolean(rawSuggestion.isNew), name});
	});

	return suggestions;
}

export function parseSuggestions(
	agent: ECategorizationAgent,
	data: string,
	context: CategorizationContext
): Suggestion[] {
	let rawSuggestions: RawSuggestion[] = [];

	try {
		const parsed = JSON.parse(stripCodeFences(data));

		if (Array.isArray(parsed)) {
			rawSuggestions = parsed;
		}
		else if (Array.isArray(parsed?.suggestions)) {
			rawSuggestions = parsed.suggestions;
		}
	}
	catch {
		return [];
	}

	const sorted = [...rawSuggestions].sort(
		(a, b) => (b.confidence ?? 0) - (a.confidence ?? 0)
	);

	const requestedCount = context.count ?? DEFAULT_COUNT;

	const count =
		Number.isInteger(requestedCount) && requestedCount > 0
			? requestedCount
			: DEFAULT_COUNT;

	if (agent === ECategorizationAgent.AUTO_CATEGORIZE) {
		return parseCategories(
			sorted,
			context.candidateCategories ?? [],
			context.appliedCategoryIds ?? [],
			count
		);
	}

	return parseTags(sorted, context.appliedTags ?? [], count);
}
