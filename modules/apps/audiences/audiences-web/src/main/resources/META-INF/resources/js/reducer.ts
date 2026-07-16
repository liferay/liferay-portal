/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	AudiencesCriteria,
	AudiencesCriteriaRulesGroup,
	CriteriaNode,
	Group,
	Rule,
} from './types';
import {
	addGroup,
	addRule,
	deleteEmptyGroups,
	deleteRule,
	duplicateRule,
	moveGroup,
	moveRule,
	parseRootGroup,
	reorderGroup,
	serializeGroup,
	setConjunction,
	unwrapRedundantGroups,
	updateRule,
} from './util/tree';

export interface State {
	externalReferenceCode: string;
	name: string;
	root: Group;
}

export type Action =
	| {
			audiencesCriteria: AudiencesCriteria;
			groupPath?: number[];
			index?: number;
			type: 'ADD_RULE';
	  }
	| {
			audiencesCriteria: AudiencesCriteria;
			targetId: string;
			type: 'ADD_GROUP';
	  }
	| {
			nodeId: string;
			targetGroupId: string;
			targetIndex: number;
			type: 'MOVE_RULE';
	  }
	| {nodeId: string; targetId: string; type: 'MOVE_GROUP'}
	| {conjunction: string; groupPath?: number[]; type: 'SET_CONJUNCTION'}
	| {externalReferenceCode: string; type: 'SET_EXTERNAL_REFERENCE_CODE'}
	| {groupPath?: number[]; items: CriteriaNode[]; type: 'REORDER_RULES'}
	| {name: string; type: 'SET_NAME'}
	| {path: number[]; rule: Rule; type: 'UPDATE_RULE'}
	| {path: number[]; type: 'DELETE_RULE'}
	| {path: number[]; type: 'DUPLICATE_RULE'};

export function initState({
	externalReferenceCode = '',
	name = '',
	rulesGroup,
}: {
	externalReferenceCode?: string;
	name?: string;
	rulesGroup?: AudiencesCriteriaRulesGroup;
}): State {
	return {
		externalReferenceCode,
		name,
		root: unwrapRedundantGroups(
			deleteEmptyGroups(
				parseRootGroup(
					rulesGroup
						? {
								conjunction: rulesGroup.conjunction ?? 'AND',
								rules: rulesGroup.rules ?? [],
							}
						: undefined
				)
			)
		),
	};
}

export function reducer(state: State, action: Action): State {
	switch (action.type) {
		case 'ADD_RULE':
			return {
				...state,
				root: addRule(
					state.root,
					action.groupPath ?? [],
					action.audiencesCriteria,
					action.index
				),
			};
		case 'ADD_GROUP':
			return {
				...state,
				root: addGroup(
					state.root,
					action.targetId,
					action.audiencesCriteria
				),
			};
		case 'DELETE_RULE':
			return {
				...state,
				root: unwrapRedundantGroups(
					deleteEmptyGroups(deleteRule(state.root, action.path))
				),
			};
		case 'DUPLICATE_RULE':
			return {...state, root: duplicateRule(state.root, action.path)};
		case 'MOVE_GROUP':
			return {
				...state,
				root: moveGroup(state.root, action.nodeId, action.targetId),
			};
		case 'MOVE_RULE':
			return {
				...state,
				root: moveRule(
					state.root,
					action.nodeId,
					action.targetGroupId,
					action.targetIndex
				),
			};
		case 'REORDER_RULES':
			return {
				...state,
				root: reorderGroup(
					state.root,
					action.groupPath ?? [],
					action.items
				),
			};
		case 'SET_CONJUNCTION':
			return {
				...state,
				root: setConjunction(
					state.root,
					action.groupPath ?? [],
					action.conjunction
				),
			};
		case 'SET_EXTERNAL_REFERENCE_CODE':
			return {
				...state,
				externalReferenceCode: action.externalReferenceCode,
			};
		case 'SET_NAME':
			return {...state, name: action.name};
		case 'UPDATE_RULE':
			return {
				...state,
				root: updateRule(state.root, action.path, action.rule),
			};
		default:
			return state;
	}
}

export function serializeCriteria(state: State): string {
	return JSON.stringify(serializeGroup(state.root));
}
