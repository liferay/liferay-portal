/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useReducer} from 'react';

export type State = {
	changeLanguageDropdown: (value: string) => void;
	changeSpaceDropdown: (value: string) => void;
	filters: {
		languageId: string;
		spaceId: string;
	};
};

enum Types {
	ChangeLanguageDropdown = 'CHANGE_LANGUAGE_DROPDOWN',
	ChangeSpaceDropdown = 'CHANGE_SPACE_DROPDOWN',
}

type Action = {
	payload: any;
	type: Types;
};

const initialState: State = {
	changeLanguageDropdown: () => {},
	changeSpaceDropdown: () => {},
	filters: {
		languageId: 'all',
		spaceId: 'all',
	},
};

export const ViewDashboardContext = createContext(initialState);

ViewDashboardContext.displayName = 'ViewDashboardContext';

const reducer = (state: State, action: Action): State => {
	switch (action.type) {
		case Types.ChangeLanguageDropdown: {
			return {
				...state,
				filters: {
					...state.filters,
					languageId: action.payload,
				},
			};
		}

		case Types.ChangeSpaceDropdown: {
			return {
				...state,
				filters: {
					...state.filters,
					spaceId: action.payload,
				},
			};
		}

		default: {
			throw new Error('Unknown Action');
		}
	}
};

const ViewDashboardContextProvider: React.FC<
	React.HTMLAttributes<HTMLElement>
> = ({children}) => {
	const [state, dispatch] = useReducer(reducer, initialState);

	const changeLanguageDropdown = (payload: string) => {
		dispatch({
			payload,
			type: Types.ChangeLanguageDropdown,
		});
	};

	const changeSpaceDropdown = (payload: string) => {
		dispatch({
			payload,
			type: Types.ChangeSpaceDropdown,
		});
	};

	return (
		<ViewDashboardContext.Provider
			value={{
				...state,
				changeLanguageDropdown,
				changeSpaceDropdown,
			}}
		>
			{children}
		</ViewDashboardContext.Provider>
	);
};

export {ViewDashboardContextProvider};
