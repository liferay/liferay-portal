/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import BulkEditWorkflowAssigneeModalContent from '../../js/components/modal/BulkEditWorkflowAssigneeModalContent';
import BulkEditWorkflowDueDateModalContent from '../../js/components/modal/BulkEditWorkflowDueDateModalContent';
import WorkflowTasksFDSPropsTransformer from '../../js/components/props_transformer/WorkflowTasksFDSPropsTransformer';

const mockOpenCMPModal = jest.fn();

jest.mock('../../js/utils/openCMPModal', () => ({
	openCMPModal: (...args: any[]) => mockOpenCMPModal(...args),
}));

const baseProps = {
	apiURL: '/o/search/v1.0/search',
	bulkActions: [
		{
			data: {id: 'update-due-date', permissionKey: 'updateDueDate'},
			label: 'Update Due Date',
		},
		{
			data: {id: 'assign-to', permissionKey: 'assignToUser'},
			label: 'Assign To',
		},
	],
	creationMenu: {
		primaryItems: [],
	},
	id: 'test-fds',
	itemsActions: [],
	views: [
		{default: true, initialPaginationDelta: 20, name: 'table'},
		{default: false, initialPaginationDelta: 20, name: 'kanban'},
	],
};

const getBulkAction = (id: string) => {
	const result = WorkflowTasksFDSPropsTransformer(baseProps as any);

	return (result.bulkActions as any[]).find(
		(action) => action.data.id === id
	);
};

describe('WorkflowTasksFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('disables bulk actions when allItemsSelectedActive is true', () => {
		const result = WorkflowTasksFDSPropsTransformer(baseProps as any);

		const bulkActions = result.bulkActions as any[];

		expect(bulkActions).toHaveLength(2);

		bulkActions.forEach((action) => {
			expect(action.isDisabled({allItemsSelectedActive: true})).toBe(
				true
			);
		});
	});

	it('does not crash when creationMenu is null', () => {
		const props = {...baseProps, creationMenu: null};

		expect(() =>
			WorkflowTasksFDSPropsTransformer(props as any)
		).not.toThrow();
	});

	it('does not open modal for unknown bulk action id', async () => {
		const result = WorkflowTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onBulkActionItemClick({
			action: {data: {id: 'unknown-action'}},
			selectedData: {items: []},
		});

		expect(mockOpenCMPModal).not.toHaveBeenCalled();
	});

	it('enables bulk actions when allItemsSelectedActive is false', () => {
		const result = WorkflowTasksFDSPropsTransformer(baseProps as any);

		const bulkActions = result.bulkActions as any[];

		bulkActions.forEach((action) => {
			expect(action.isDisabled({allItemsSelectedActive: false})).toBe(
				false
			);
		});
	});

	it('filters out the kanban view', () => {
		const result = WorkflowTasksFDSPropsTransformer(baseProps as any);

		expect(
			(result.views as any[]).every((v: any) => v.name !== 'kanban')
		).toBe(true);
		expect((result.views as any[]).length).toBe(1);
	});

	it('hides a bulk action when a selected workflow task lacks its permission', () => {
		const selectedItems = [{actions: {get: {}, updateDueDate: {}}}];

		expect(getBulkAction('assign-to').isVisible({selectedItems})).toBe(
			false
		);

		expect(
			getBulkAction('update-due-date').isVisible({selectedItems})
		).toBe(true);
	});

	it('marks all views as non-default', () => {
		const result = WorkflowTasksFDSPropsTransformer(baseProps as any);

		expect(
			(result.views as any[]).every((v: any) => v.default === false)
		).toBe(true);
	});

	it('opens BulkEditWorkflowAssigneeModalContent when assign-to is clicked', async () => {
		const result = WorkflowTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onBulkActionItemClick({
			action: {data: {id: 'assign-to'}},
			selectedData: {items: [{embedded: {id: 1}}]},
		});

		const {contentComponent} = mockOpenCMPModal.mock.calls[0][0];

		expect(contentComponent({closeModal: jest.fn()}).type).toBe(
			BulkEditWorkflowAssigneeModalContent
		);
	});

	it('opens BulkEditWorkflowDueDateModalContent when update-due-date is clicked', async () => {
		const result = WorkflowTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onBulkActionItemClick({
			action: {data: {id: 'update-due-date'}},
			selectedData: {items: [{embedded: {id: 1}}]},
		});

		const {contentComponent} = mockOpenCMPModal.mock.calls[0][0];

		expect(contentComponent({closeModal: jest.fn()}).type).toBe(
			BulkEditWorkflowDueDateModalContent
		);
	});
});
