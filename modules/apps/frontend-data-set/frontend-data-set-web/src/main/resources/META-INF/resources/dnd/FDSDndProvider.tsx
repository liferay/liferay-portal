/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {ReactNode} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

const FDSDndProvider = ({children}: {children: ReactNode}) => {
	return (

		// @ts-ignore

		<DndProvider backend={HTML5Backend} context={window}>
			{children}
		</DndProvider>
	);
};

export default FDSDndProvider;
