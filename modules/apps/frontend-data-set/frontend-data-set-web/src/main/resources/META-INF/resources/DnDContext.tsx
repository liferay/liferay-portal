/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {IFileDropSettings} from './utils/types';

export type THandleFileDrop = (droppedItem: any, dropTarget?: any) => void;

export interface IFrontendDataSetDropContext {
	fileDropSettings: IFileDropSettings;
	handleFileDrop: THandleFileDrop;
}

const DnDContext = React.createContext({
	fileDropSettings: {
		enabled: false,
		isDropTarget: () => true,
	},
	handleFileDrop: () => {},
} as unknown as IFrontendDataSetDropContext);

export default DnDContext;
