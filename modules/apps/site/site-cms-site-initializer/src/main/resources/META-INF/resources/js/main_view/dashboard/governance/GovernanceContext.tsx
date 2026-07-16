/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useMemo, useState} from 'react';

import {SpaceOption, initialSpace} from '../common/SpacePicker';

type State = {
	setSpace: (space: SpaceOption) => void;
	space: SpaceOption;
};

const GovernanceContext = createContext<State>({
	setSpace: () => {},
	space: initialSpace,
});

GovernanceContext.displayName = 'GovernanceContext';

function GovernanceContextProvider({children}: {children: React.ReactNode}) {
	const [space, setSpace] = useState<SpaceOption>(initialSpace);

	const value = useMemo(() => ({setSpace, space}), [space]);

	return (
		<GovernanceContext.Provider value={value}>
			{children}
		</GovernanceContext.Provider>
	);
}

export {GovernanceContext, GovernanceContextProvider};
