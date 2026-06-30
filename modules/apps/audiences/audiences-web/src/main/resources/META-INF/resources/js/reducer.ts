/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {AudiencesCriteria, AudiencesCriteriaJSON, Rule} from './types';

export interface State {
	conjunction: string;
	name: string;
	rules: Rule[];
}

export type Action =
	| {audiencesCriteria: AudiencesCriteria; index?: number; type: 'ADD_RULE'}
	| {conjunction: string; type: 'SET_CONJUNCTION'}
	| {index: number; rule: Rule; type: 'CHANGE_RULE'}
	| {index: number; type: 'DELETE_RULE'}
	| {index: number; type: 'DUPLICATE_RULE'}
	| {name: string; type: 'SET_NAME'}
	| {rules: Rule[]; type: 'REORDER_RULES'};

export function createRule(audiencesCriteria: AudiencesCriteria): Rule {
	return {
		attribute: audiencesCriteria.key,
		id: `rule-${uuidv4()}`,
		operator: audiencesCriteria.operators[0] || '',
		value: audiencesCriteria.options[0]?.value || '',
	};
}

export function initState({
	json,
	name = '',
}: {
	json?: AudiencesCriteriaJSON;
	name?: string;
}): State {
	return {
		conjunction: json?.conjunction ?? 'AND',
		name,
		rules: (json?.rules ?? [])
			.filter((rule) => Boolean(rule.attribute))
			.map((rule) => ({
				attribute: rule.attribute,
				id: `rule-${uuidv4()}`,
				operator: rule.operator,
				value: rule.value,
			})),
	};
}

export function reducer(state: State, action: Action): State {
	switch (action.type) {
		case 'ADD_RULE': {
			const rules = [...state.rules];

			rules.splice(
				action.index ?? rules.length,
				0,
				createRule(action.audiencesCriteria)
			);

			return {...state, rules};
		}
		case 'CHANGE_RULE':
			return {
				...state,
				rules: state.rules.map((rule, index) =>
					index === action.index ? action.rule : rule
				),
			};
		case 'DELETE_RULE':
			return {
				...state,
				rules: state.rules.filter(
					(_rule, index) => index !== action.index
				),
			};
		case 'DUPLICATE_RULE': {
			const rules = [...state.rules];

			rules.splice(action.index + 1, 0, {
				...state.rules[action.index],
				id: `rule-${uuidv4()}`,
			});

			return {...state, rules};
		}
		case 'REORDER_RULES':
			return {...state, rules: action.rules};
		case 'SET_CONJUNCTION':
			return {...state, conjunction: action.conjunction};
		case 'SET_NAME':
			return {...state, name: action.name};
		default:
			return state;
	}
}

export function serializeCriteria(state: State): string {
	return JSON.stringify({
		conjunction: state.conjunction,
		rules: state.rules.map((rule) => ({
			attribute: rule.attribute,
			operator: rule.operator,
			value: rule.value,
		})),
	});
}
