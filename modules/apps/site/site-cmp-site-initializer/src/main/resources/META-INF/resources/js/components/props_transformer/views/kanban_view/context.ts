/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IItemsActions} from '@liferay/frontend-data-set-web';
import React from 'react';

import {IColumn, ITask} from '../../../../utils/types';

interface IKanbanContext {
	boardData: {[k: string]: IColumn};
	changeTaskStatus: (
		task: ITask,
		newStatus: {
			key: string;
			name: string;
		}
	) => void;
	cmpProjectObjectDefinitionId: number;
	cmpProjectObjectEntryId: string;
	hasAddTaskPermission: boolean;
	itemsActions: IItemsActions[];
	loadData: Function;
}

export const KanbanViewContext = React.createContext({} as IKanbanContext);
