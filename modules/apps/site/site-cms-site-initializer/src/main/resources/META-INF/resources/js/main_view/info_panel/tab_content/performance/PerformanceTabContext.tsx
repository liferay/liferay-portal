/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useReducer} from 'react';

import {MetricType} from './Metrics';

export type State = {
	changeMetric: (payload: MetricType) => void;
	filters: {
		metric: MetricType | null;
	};
};

enum Types {
	ChangeMetric = 'CHANGE_METRIC_FILTER',
}

type Action = {
	payload: any;
	type: Types;
};

const initialState: State = {
	changeMetric: () => {},
	filters: {
		metric: null,
	},
};

export const PerformanceTabContext = createContext(initialState);

PerformanceTabContext.displayName = 'PerformanceTabContext';

const reducer = (state: State, action: Action): State => {
	switch (action.type) {
		case Types.ChangeMetric: {
			return {
				...state,
				filters: {
					...state.filters,
					metric: action.payload,
				},
			};
		}

		default: {
			throw new Error('Unknown Action');
		}
	}
};

const PerformanceTabProvider: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	children,
}) => {
	const [state, dispatch] = useReducer(reducer, initialState);

	const changeMetric = (payload: MetricType) => {
		dispatch({
			payload,
			type: Types.ChangeMetric,
		});
	};

	return (
		<PerformanceTabContext.Provider
			value={{
				...state,
				changeMetric,
			}}
		>
			{children}
		</PerformanceTabContext.Provider>
	);
};

export {PerformanceTabProvider};
