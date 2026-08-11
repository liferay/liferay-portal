/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

type ColumnSizes = Record<string, number> | null;

type ResizeState = {
	nextColumnSizes: ColumnSizes;
	resizing: boolean;
	setNextColumnSizes: React.Dispatch<React.SetStateAction<ColumnSizes>>;
	setResizing: React.Dispatch<React.SetStateAction<boolean>>;
};

const INITIAL_STATE: ResizeState = {
	nextColumnSizes: null,
	resizing: false,
	setNextColumnSizes: () => {},
	setResizing: () => null,
};

const ResizeContext = React.createContext(INITIAL_STATE);

const ResizeContextProvider = ResizeContext.Provider;

const useResizing = () => {
	return useContext(ResizeContext).resizing;
};

const useSetResizing = () => {
	return useContext(ResizeContext).setResizing;
};

const useSetNextColumnSizes = () => {
	return useContext(ResizeContext).setNextColumnSizes;
};

const useNextColumnSizes = () => {
	return useContext(ResizeContext).nextColumnSizes;
};

export {
	ResizeContextProvider,
	useResizing,
	useSetResizing,
	useNextColumnSizes,
	useSetNextColumnSizes,
};
