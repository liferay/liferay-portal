/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import React, {useContext, useReducer} from 'react';

import {
	ADD_REDO_ACTION,
	ADD_UNDO_ACTION,
	SET_DRAFT_STATUS,
	SET_TOKEN_VALUES,
	UPDATE_UNDO_REDO_HISTORY,
} from '../constants/actionTypes';
import {DRAFT_STATUS} from '../constants/draftStatusConstants';
import {UNDO_TYPES} from '../constants/undoTypes';
import editorReducer from '../reducers/editorReducer';
import saveDraft from '../saveDraft';

const StyleBookEditorDispatchContext = React.createContext(() => {});
export const StyleBookEditorStoreContext = React.createContext({
	draftStatus: null,
	frontendTokensValues: {},
	redoHistory: [],
	undoHistory: [],
});

export function StyleBookEditorContextProvider({children, initialState}) {
	const [state, dispatch] = useReducer(editorReducer, initialState);

	return (
		<StyleBookEditorDispatchContext.Provider value={dispatch}>
			<StyleBookEditorStoreContext.Provider value={state}>
				{children}
			</StyleBookEditorStoreContext.Provider>
		</StyleBookEditorDispatchContext.Provider>
	);
}

export function useDispatch() {
	return useContext(StyleBookEditorDispatchContext);
}

export function useDraftStatus() {
	return useContext(StyleBookEditorStoreContext).draftStatus;
}

export function useFrontendTokensValues() {
	return useContext(StyleBookEditorStoreContext).frontendTokensValues;
}

export function useRedoHistory() {
	return useContext(StyleBookEditorStoreContext).redoHistory;
}

export function useUndoHistory() {
	return useContext(StyleBookEditorStoreContext).undoHistory;
}

function internalSaveTokenValues({dispatch, frontendTokensValues, tokens}) {
	dispatch({
		type: SET_DRAFT_STATUS,
		value: DRAFT_STATUS.saving,
	});

	return saveDraft({...frontendTokensValues, ...tokens})
		.then(() => {
			dispatch({
				type: SET_DRAFT_STATUS,
				value: DRAFT_STATUS.draftSaved,
			});

			dispatch({
				tokens,
				type: SET_TOKEN_VALUES,
			});
		})
		.catch((error) => {
			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}

			dispatch({
				type: SET_DRAFT_STATUS,
				value: DRAFT_STATUS.notSaved,
			});

			openToast({
				message: error.message,
				type: 'danger',
			});
		});
}

export function useSaveTokenValue() {
	const dispatch = useDispatch();
	const frontendTokensValues = useFrontendTokensValues();

	return ({label, name, value}) => {
		const previousValue = frontendTokensValues[name];

		internalSaveTokenValues({
			dispatch,
			frontendTokensValues,
			tokens: {[name]: value},
		}).then(() => {
			dispatch({
				label,
				name,
				type: ADD_UNDO_ACTION,
				value: previousValue,
			});
		});
	};
}

export function useOnUndo() {
	const dispatch = useDispatch();
	const frontendTokensValues = useFrontendTokensValues();
	const undoHistory = useUndoHistory();

	return () => {
		const [lastUndo, ...undos] = undoHistory;
		const previousValue = frontendTokensValues[lastUndo.name];

		if (!lastUndo.value) {
			delete frontendTokensValues[lastUndo.name];
		}

		internalSaveTokenValues({
			dispatch,
			frontendTokensValues,
			tokens: lastUndo.value ? {[lastUndo.name]: lastUndo.value} : {},
		}).then(() => {
			dispatch({
				type: UPDATE_UNDO_REDO_HISTORY,
				undoHistory: undos,
			});
			dispatch({
				label: lastUndo.label,
				name: lastUndo.name,
				type: ADD_REDO_ACTION,
				value: previousValue,
			});
		});
	};
}

export function useOnRedo() {
	const dispatch = useDispatch();
	const frontendTokensValues = useFrontendTokensValues();
	const redoHistory = useRedoHistory();

	return () => {
		const [lastRedo, ...redos] = redoHistory;
		const previousValue = frontendTokensValues[lastRedo.name];

		internalSaveTokenValues({
			dispatch,
			frontendTokensValues,
			tokens: {[lastRedo.name]: lastRedo.value},
		}).then(() => {
			dispatch({
				redoHistory: redos,
				type: UPDATE_UNDO_REDO_HISTORY,
			});
			dispatch({
				isRedo: true,
				label: lastRedo.label,
				name: lastRedo.name,
				type: ADD_UNDO_ACTION,
				value: previousValue,
			});
		});
	};
}

export function useMultipleUndo() {
	const dispatch = useDispatch();
	const undoHistory = useUndoHistory();
	const redoHistory = useRedoHistory();
	const frontendTokensValues = useFrontendTokensValues();
	let tokens = {};

	return ({numberOfActions, type}) => {
		let remainingUndos;
		let undosToUndo;
		let updateHistoryAction;

		if (type === UNDO_TYPES.undo) {
			undosToUndo = undoHistory.slice(0, numberOfActions);

			remainingUndos = undoHistory.slice(
				numberOfActions,
				undoHistory.length
			);

			const nextRedoHistory = undosToUndo.map(({label, name}) => ({
				label,
				name,
				value: frontendTokensValues[name],
			}));

			updateHistoryAction = {
				redoHistory: [...nextRedoHistory.reverse(), ...redoHistory],
				type: UPDATE_UNDO_REDO_HISTORY,
				undoHistory: remainingUndos,
			};
		}
		else {
			undosToUndo = redoHistory.slice(0, numberOfActions);

			remainingUndos = redoHistory.slice(
				numberOfActions,
				redoHistory.length
			);

			const nextUndoHistory = undosToUndo.map(({label, name}) => ({
				label,
				name,
				value: frontendTokensValues[name],
			}));

			updateHistoryAction = {
				redoHistory: remainingUndos,
				type: UPDATE_UNDO_REDO_HISTORY,
				undoHistory: [...nextUndoHistory.reverse(), ...undoHistory],
			};
		}

		for (const undo of undosToUndo) {
			if (undo.value) {
				tokens = {...tokens, [undo.name]: undo.value};
			}
			else {
				delete frontendTokensValues[undo.name];
				delete tokens[undo.name];
			}
		}

		return internalSaveTokenValues({
			dispatch,
			frontendTokensValues,
			tokens,
		}).then(() => {
			dispatch(updateHistoryAction);
		});
	};
}
