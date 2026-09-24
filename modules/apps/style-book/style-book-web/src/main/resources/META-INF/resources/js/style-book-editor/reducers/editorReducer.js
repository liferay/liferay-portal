/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ADD_REDO_ACTION,
	ADD_UNDO_ACTION,
	SET_CUSTOM_FRONTEND_TOKEN_DEFINITION,
	SET_DRAFT_STATUS,
	SET_TOKEN_VALUES,
	UPDATE_UNDO_REDO_HISTORY,
} from '../constants/actionTypes';

const MAX_UNDO_ACTIONS = 100;

export default function editorReducer(state, action) {
	switch (action.type) {
		case SET_DRAFT_STATUS: {
			const {value} = action;

			return {
				...state,
				draftStatus: value,
			};
		}

		case SET_TOKEN_VALUES: {
			const {tokens} = action;

			return {
				...state,
				frontendTokensValues: {
					...state.frontendTokensValues,
					...tokens,
				},
			};
		}

		case SET_CUSTOM_FRONTEND_TOKEN_DEFINITION: {
			const {customFrontendTokenDefinition} = action;

			return {
				...state,
				customFrontendTokenDefinition,
			};
		}

		case ADD_UNDO_ACTION: {
			const {isRedo = false, label, name, value} = action;

			const nextRedoHistory = isRedo ? state.redoHistory : [];
			const nextUndoHistory = state.undoHistory || [];

			return {
				...state,
				redoHistory: nextRedoHistory,
				undoHistory: [
					{
						label,
						name,
						value,
					},
					...nextUndoHistory.slice(0, MAX_UNDO_ACTIONS - 1),
				],
			};
		}

		case ADD_REDO_ACTION: {
			const {label, name, value} = action;
			const nextRedoHistory = state.redoHistory || [];

			return {
				...state,
				redoHistory: [
					{
						label,
						name,
						value,
					},
					...nextRedoHistory,
				],
			};
		}

		case UPDATE_UNDO_REDO_HISTORY: {
			return {
				...state,
				redoHistory: action.redoHistory ?? state.redoHistory,
				undoHistory: action.undoHistory ?? state.undoHistory,
			};
		}

		default:
			return state;
	}
}
