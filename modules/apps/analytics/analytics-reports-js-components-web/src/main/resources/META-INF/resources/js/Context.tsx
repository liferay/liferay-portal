/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useReducer} from 'react';

import {
	RangeSelector,
	RangeSelectors,
} from './components/RangeSelectorsDropdown';
import {Individuals, MetricType} from './types/global';

export type State = {
	changeChannelFilter: (value: any) => void;
	changeIndividualFilter: (value: any) => void;
	changeMetricFilter: (value: any) => void;
	changeRangeSelectorFilter: (value: RangeSelector) => void;
	filters: {
		channel: string;
		individual: Individuals;
		metric: MetricType;
		rangeSelector: RangeSelector;
	};
	[key: string]: any;
};

enum Types {
	ChangeChannelFilter = 'CHANGE_CHANNEL_FILTER',
	ChangeIndividualFilter = 'CHANGE_INDIVIDUAL_FILTER',
	ChangeMetricFilter = 'CHANGE_METRIC_FILTER',
	ChangeRangeSelectorFilter = 'CHANGE_RANGE_SELECTOR_FILTER',
}

type Action = {
	payload: any;
	type: Types;
};

const initialState: State = {
	changeChannelFilter: () => {},
	changeIndividualFilter: () => {},
	changeMetricFilter: () => {},
	changeRangeSelectorFilter: () => {},
	filters: {
		channel: '',
		individual: Individuals.AllIndividuals,
		metric: MetricType.Undefined,
		rangeSelector: {
			rangeEnd: '',
			rangeKey: RangeSelectors.Last7Days,
			rangeStart: '',
		},
	},
};

export const Context = createContext(initialState);

Context.displayName = 'Context';

const reducer = (state: State, action: Action): State => {
	switch (action.type) {
		case Types.ChangeChannelFilter: {
			return {
				...state,
				filters: {
					...state.filters,
					channel: action.payload,
				},
			};
		}

		case Types.ChangeIndividualFilter: {
			return {
				...state,
				filters: {
					...state.filters,
					individual: action.payload,
				},
			};
		}

		case Types.ChangeMetricFilter: {
			return {
				...state,
				filters: {
					...state.filters,
					metric: action.payload,
				},
			};
		}

		case Types.ChangeRangeSelectorFilter: {
			return {
				...state,
				filters: {
					...state.filters,
					rangeSelector: action.payload,
				},
			};
		}

		default: {
			throw new Error('Unknown Action');
		}
	}
};

interface IContextProviderProps extends React.HTMLAttributes<HTMLElement> {
	customState: {
		[key: string]: any;
	};
}

const ContextProvider: React.FC<IContextProviderProps> = ({
	children,
	customState,
}) => {
	const [state, dispatch] = useReducer(reducer, initialState);

	const changeChannelFilter = (payload: any) => {
		dispatch({
			payload,
			type: Types.ChangeChannelFilter,
		});
	};

	const changeIndividualFilter = (payload: any) => {
		dispatch({
			payload,
			type: Types.ChangeIndividualFilter,
		});
	};

	const changeMetricFilter = (payload: any) => {
		dispatch({
			payload,
			type: Types.ChangeMetricFilter,
		});
	};

	const changeRangeSelectorFilter = (payload: any) => {
		dispatch({
			payload,
			type: Types.ChangeRangeSelectorFilter,
		});
	};

	return (
		<Context.Provider
			value={{
				...customState,
				...state,
				changeChannelFilter,
				changeIndividualFilter,
				changeMetricFilter,
				changeRangeSelectorFilter,
			}}
		>
			{children}
		</Context.Provider>
	);
};

export {ContextProvider};
