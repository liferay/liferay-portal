/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useState} from 'react';

import {config} from '../../../app/config/index';

const StyleBookDispatchContext = React.createContext(() => {});
const StyleBookStateContext = React.createContext({
	styleBookEntryERC: null,
	tokenValues: {},
});

export function StyleBookContextProvider({children}) {
	const [styleBook, setStyleBook] = useState({
		styleBookEntryERC: config.styleBookEntryERC,
		tokenValues: config.frontendTokens,
	});

	return (
		<StyleBookDispatchContext.Provider value={setStyleBook}>
			<StyleBookStateContext.Provider value={styleBook}>
				{children}
			</StyleBookStateContext.Provider>
		</StyleBookDispatchContext.Provider>
	);
}

export function useSetStyleBook() {
	return useContext(StyleBookDispatchContext);
}
export function useStyleBook() {
	return useContext(StyleBookStateContext);
}
