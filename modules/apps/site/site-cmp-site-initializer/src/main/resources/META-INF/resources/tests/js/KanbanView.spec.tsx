/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import KanbanView from '../../js/components/props_transformer/views/kanban_view/KanbanView';

jest.mock('react-dnd', () => ({
	DndProvider: ({children}: any) => <>{children}</>,
	useDrag: () => [{}, jest.fn()],
	useDrop: () => {
		return [{}, jest.fn()];
	},
}));

describe('KanbanView mapping and lifecycle', () => {
	beforeEach(() => {
		(global as any).Liferay = (global as any).Liferay || {};
		(global as any).Liferay.fire = jest.fn();
	});

	it('fires quick filter visibility events', () => {
		const items = [
			{
				embedded: {
					cmpProjectToCMPTasks: {title: 'Title 1'},
					state: {key: 'in-progress'},
					title: 'Title 1',
				},
			},
			{
				embedded: {
					cmpProjectToCMPTasks: {title: 'Title 2'},
					state: {key: 'completed'},
					title: 'Title 2',
				},
			},
		] as any;

		const {unmount} = render(
			<KanbanView
				hasAddTaskPermission
				items={items}
				itemsActions={[]}
				projectId=""
				projectObjectDefinitionId={123}
			/>
		);

		expect((global as any).Liferay.fire).toHaveBeenCalledWith(
			'cmp-update-tasks-quick-filter-visibility',
			{visible: false}
		);

		unmount();

		expect((global as any).Liferay.fire).toHaveBeenCalledWith(
			'cmp-update-tasks-quick-filter-visibility',
			{visible: true}
		);
	});
});
