/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback, useContext, useRef, useState} from 'react';

import {Position} from '../processors/setCursorPosition';

type EditableProcessorState = {
	editableClickPosition: Position | null;
	editableUniqueId: string | null;
};

const INITIAL_STATE: EditableProcessorState = {
	editableClickPosition: null,
	editableUniqueId: null,
};

const EditableProcessorDispatchContext = React.createContext<
	React.Dispatch<React.SetStateAction<EditableProcessorState>>
>(() => {});
const EditableProcessorRefContext = React.createContext<
	React.MutableRefObject<EditableProcessorState | null>
>({current: null});
const EditableProcessorStateContext = React.createContext(INITIAL_STATE);

export function EditableProcessorContextProvider({
	children,
}: {
	children: React.ReactNode;
}) {
	const [state, setState] = useState(INITIAL_STATE);
	const ref = useRef<EditableProcessorState | null>(null);

	// eslint-disable-next-line react-compiler/react-compiler
	ref.current = state;

	return (
		<EditableProcessorDispatchContext.Provider value={setState}>
			<EditableProcessorRefContext.Provider value={ref}>
				<EditableProcessorStateContext.Provider value={state}>
					{children}
				</EditableProcessorStateContext.Provider>
			</EditableProcessorRefContext.Provider>
		</EditableProcessorDispatchContext.Provider>
	);
}

export function useEditableProcessorClickPosition() {
	const state = useContext(EditableProcessorStateContext);

	return state.editableClickPosition;
}

export function useEditableProcessorUniqueId() {
	return useContext(EditableProcessorStateContext).editableUniqueId;
}

export function useIsProcessorEnabled() {
	const ref = useContext(EditableProcessorRefContext);

	return useCallback(
		(editableUniqueId: string | null = null) =>
			editableUniqueId
				? ref.current?.editableUniqueId === editableUniqueId
				: !!ref.current?.editableUniqueId,
		[ref]
	);
}

export function useSetEditableProcessorUniqueId() {
	const setState = useContext(EditableProcessorDispatchContext);

	return useCallback(
		(
			editableUniqueIdOrNull: string | null,
			editableClickPosition: Position | null = null
		) => {
			setState({
				editableClickPosition,
				editableUniqueId: editableUniqueIdOrNull,
			});
		},
		[setState]
	);
}
