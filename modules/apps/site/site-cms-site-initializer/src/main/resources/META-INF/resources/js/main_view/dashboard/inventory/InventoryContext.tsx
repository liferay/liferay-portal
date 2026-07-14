/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useCallback, useReducer} from 'react';

import {initialSpace} from '../common/SpacePicker';

type Item = {
	externalReferenceCode?: string;
	label: string;
	value: string;
};

export type State = {
	changeLanguage: (item: Item) => void;
	changeSpace: (item: Item) => void;
	constants: {
		[key: string]: string;
	};
	filters: {
		[key: string]: Item;
	};
};

enum Types {
	changeLanguage = 'CHANGE_LANGUAGE',
	changeSpace = 'CHANGE_SPACE',
}

type Action = {
	payload: any;
	type: Types;
};

export const initialLanguage = {
	label: Liferay.Language.get('all-languages'),
	value: 'all',
};

const initialState: State = {
	changeLanguage: () => {},
	changeSpace: () => {},
	constants: {},
	filters: {
		language: initialLanguage,
		space: initialSpace,
	},
};

export const InventoryContext = createContext(initialState);

InventoryContext.displayName = 'InventoryContext';

const reducer = (state: State, action: Action): State => {
	switch (action.type) {
		case Types.changeLanguage: {
			return {
				...state,
				filters: {
					...state.filters,
					language: action.payload,
				},
			};
		}

		case Types.changeSpace: {
			return {
				...state,
				filters: {
					...state.filters,
					space: action.payload,
				},
			};
		}

		default: {
			throw new Error('Unknown Action');
		}
	}
};

interface IInventoryContextProvider extends React.HTMLAttributes<HTMLElement> {
	value: Partial<State>;
}

const InventoryContextProvider: React.FC<IInventoryContextProvider> = ({
	children,
	value,
}) => {
	const [state, dispatch] = useReducer(reducer, initialState);

	const changeLanguage = useCallback((payload: Item) => {
		dispatch({
			payload,
			type: Types.changeLanguage,
		});
	}, []);

	const changeSpace = useCallback((payload: Item) => {
		dispatch({
			payload,
			type: Types.changeSpace,
		});
	}, []);

	return (
		<InventoryContext.Provider
			value={{
				...state,
				...value,
				changeLanguage,
				changeSpace,
			}}
		>
			{children}
		</InventoryContext.Provider>
	);
};

export {InventoryContextProvider};
