/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	RangeSelector,
	RangeSelectors,
} from '@liferay/analytics-reports-js-components-web';
import React, {createContext, useMemo, useState} from 'react';

import {SpaceOption, initialSpace} from '../common/SpacePicker';
import {DashboardAdditionalProps} from './types';

const initialRange: RangeSelector = {
	rangeEnd: '',
	rangeKey: RangeSelectors.Last7Days,
	rangeStart: '',
};

type State = {
	additionalProps?: DashboardAdditionalProps;
	constants: {[key: string]: string};
	range: RangeSelector;
	setRange: (range: RangeSelector) => void;
	setSpace: (space: SpaceOption) => void;
	space: SpaceOption;
	spaceIds: string[];
};

const PerformanceContext = createContext<State>({
	additionalProps: undefined,
	constants: {},
	range: initialRange,
	setRange: () => {},
	setSpace: () => {},
	space: initialSpace,
	spaceIds: [],
});

PerformanceContext.displayName = 'PerformanceContext';

function PerformanceContextProvider({
	additionalProps,
	children,
	constants = {},
	spaceIds = [],
}: {
	additionalProps?: DashboardAdditionalProps;
	children: React.ReactNode;
	constants?: {[key: string]: string};
	spaceIds?: string[];
}) {
	const [range, setRange] = useState<RangeSelector>(initialRange);
	const [space, setSpace] = useState<SpaceOption>(initialSpace);

	const value = useMemo(
		() => ({
			additionalProps,
			constants,
			range,
			setRange,
			setSpace,
			space,
			spaceIds,
		}),
		[additionalProps, constants, range, space, spaceIds]
	);

	return (
		<PerformanceContext.Provider value={value}>
			{children}
		</PerformanceContext.Provider>
	);
}

export {PerformanceContext, PerformanceContextProvider};
