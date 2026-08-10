/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {KANBAN_COLUMN_ORDER} from '../../../../../utils/constants';
import {KanbanViewContext} from '../context';
import Column from './Column';
import TaskDragLayer from './TaskDragLayer';

export default function Board() {
	const {boardData} = useContext(KanbanViewContext);

	return (
		<div className="d-flex">
			<TaskDragLayer />

			{KANBAN_COLUMN_ORDER.map((state) => {
				const column = boardData[state];

				if (column) {
					return <Column column={column} key={state} />;
				}

				return null;
			})}
		</div>
	);
}
