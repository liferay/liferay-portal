/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IntentAction, IntentVerdict} from '../types';
import {stripCodeFences} from './parseSuggestions';

const DEFAULT_COUNT = 3;
const MAX_COUNT = 10;
const MAX_TARGETS = 10;

export const PASSTHROUGH: IntentVerdict = {actions: [], passthrough: true};

interface RawAction {
	agent?: unknown;
	count?: unknown;
	targets?: unknown;
}

function clampCount(count: unknown): number {
	if (typeof count !== 'number' || !Number.isFinite(count)) {
		return DEFAULT_COUNT;
	}

	const rounded = Math.round(count);

	if (rounded < 1) {
		return 1;
	}

	if (rounded > MAX_COUNT) {
		return MAX_COUNT;
	}

	return rounded;
}

function parseTargets(targets: unknown): string[] {
	if (!Array.isArray(targets)) {
		return [];
	}

	const parsed: string[] = [];

	targets.forEach((target) => {
		if (parsed.length >= MAX_TARGETS || typeof target !== 'string') {
			return;
		}

		const trimmed = target.trim();

		if (trimmed) {
			parsed.push(trimmed);
		}
	});

	return parsed;
}

export function parseIntent(data: string): IntentVerdict {
	let parsed: {actions?: unknown; passthrough?: unknown};

	try {
		parsed = JSON.parse(stripCodeFences(data));
	}
	catch {
		return PASSTHROUGH;
	}

	if (!parsed || typeof parsed !== 'object' || parsed.passthrough === true) {
		return PASSTHROUGH;
	}

	if (!Array.isArray(parsed.actions)) {
		return PASSTHROUGH;
	}

	const actionsByAgent = new Map<'categorize' | 'tag', IntentAction>();

	(parsed.actions as RawAction[]).forEach((rawAction) => {
		const agent = rawAction?.agent;

		if (
			(agent === 'categorize' || agent === 'tag') &&
			!actionsByAgent.has(agent)
		) {
			actionsByAgent.set(agent, {
				agent,
				count: clampCount(rawAction.count),
				targets: parseTargets(rawAction.targets),
			});
		}
	});

	const actions: IntentAction[] = [];

	(['categorize', 'tag'] as const).forEach((agent) => {
		const action = actionsByAgent.get(agent);

		if (action) {
			actions.push(action);
		}
	});

	if (!actions.length) {
		return PASSTHROUGH;
	}

	return {actions, passthrough: false};
}
