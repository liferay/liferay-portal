/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';

const AppContext = React.createContext();

const AppContextProvider = ({children, ...props}) => {
	const [reindexStatuses, setReindexStatuses] = useState([]);
	const [title, setTitle] = useState(Liferay.Language.get('metrics'));
	const [fetchDateModified, setFetchDateModified] = useState(false);

	const state = {
		...props,
		fetchDateModified,
		reindexStatuses,
		setFetchDateModified,
		setReindexStatuses,
		setTitle,
		title,
	};

	return <AppContext.Provider value={state}>{children}</AppContext.Provider>;
};

export {AppContext, AppContextProvider};
