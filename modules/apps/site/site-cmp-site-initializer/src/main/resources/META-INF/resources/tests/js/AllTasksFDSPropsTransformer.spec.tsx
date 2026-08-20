/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import BulkEditAssigneeModalContent from '../../js/components/modal/BulkEditAssigneeModalContent';
import BulkEditWorkflowAssigneeModalContent from '../../js/components/modal/BulkEditWorkflowAssigneeModalContent';
import BulkEditWorkflowDueDateModalContent from '../../js/components/modal/BulkEditWorkflowDueDateModalContent';
import AllTasksFDSPropsTransformer from '../../js/components/props_transformer/AllTasksFDSPropsTransformer';

const mockDeleteItemAction = jest.fn();
const mockOpenCMPModal = jest.fn();

jest.mock('@liferay/site-cms-site-initializer', () => ({
	SimpleActionLinkRenderer: jest.fn(),
	addOnClickToCreationMenuItems: jest.fn(),
	deleteAssetEntriesBulkAction: jest.fn(),
	deleteItemAction: (...args: any[]) => mockDeleteItemAction(...args),
}));

jest.mock('../../js/utils/openCMPModal', () => ({
	openCMPModal: (...args: any[]) => mockOpenCMPModal(...args),
}));

const liferayLanguageGet = Liferay.Language.get;

Liferay.Language.get = (key: string) =>
	key === 'delete-task-confirmation-body'
		? 'You are about to delete the task "{0}."'
		: liferayLanguageGet(key);

jest.mock('../../js/components/modal/BulkEditAssigneeModalContent', () => ({
	__esModule: true,
	default: () => null,
}));

jest.mock('../../js/components/modal/EditAssigneeModalContent', () => ({
	__esModule: true,
	default: () => null,
}));

const _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN =
	'com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken';

const baseProps = {
	additionalProps: {states: []},
	apiURL: '/o/search/v1.0/search',
	bulkActions: [
		{
			data: {id: 'assign-to', permissionKey: 'update'},
			label: 'Assign To',
		},
		{data: {id: 'delete', permissionKey: 'delete'}, label: 'Delete'},
		{
			data: {id: 'update-due-date', permissionKey: 'update'},
			label: 'Update Due Date',
		},
		{
			data: {id: 'update-state', permissionKey: 'update'},
			label: 'Update State',
		},
	],
	creationMenu: {primaryItems: []},
	id: 'test-fds',
	itemsActions: [],
	views: [{default: true, initialPaginationDelta: 20, name: 'table'}],
};

const getBulkAction = (id: string) => {
	const result = AllTasksFDSPropsTransformer(baseProps as any);

	return (result.bulkActions as any[]).find(
		(action) => action.data.id === id
	);
};

const workflowItem = {
	embedded: {id: 1},
	entryClassName: _CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN,
};
const projectItem = {
	embedded: {id: 1, r_cmpProjectToCMPTasks_c_cmpProjectId: 456},
	entryClassName: 'com.liferay.object.model.ObjectEntry',
};

describe('AllTasksFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('disables all bulk actions when a mixed selection of workflow and project tasks is selected', () => {
		const selectedItems = [workflowItem, projectItem];

		expect(
			getBulkAction('update-state').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(true);
		expect(
			getBulkAction('delete').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(true);
		expect(
			getBulkAction('assign-to').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(true);
		expect(
			getBulkAction('update-due-date').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(true);
	});

	it('disables update-state and delete bulk actions but not assign-to when all items are selected', () => {
		expect(
			getBulkAction('assign-to').isDisabled({
				allItemsSelectedActive: true,
			})
		).toBe(false);
		expect(
			getBulkAction('delete').isDisabled({allItemsSelectedActive: true})
		).toBe(true);
		expect(
			getBulkAction('update-state').isDisabled({
				allItemsSelectedActive: true,
			})
		).toBe(true);
	});

	it('disables update-state and delete bulk actions when a workflow task is selected', () => {
		const selectedItems = [workflowItem];

		expect(
			getBulkAction('update-state').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(true);
		expect(
			getBulkAction('delete').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(true);

		expect(
			getBulkAction('assign-to').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(false);
		expect(
			getBulkAction('update-due-date').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(false);
	});

	it('escapes the task title in the delete confirmation message', async () => {
		const result = AllTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			itemData: {
				...projectItem,
				embedded: {title: '<script>alert(1)</script>'},
			},
			loadData: jest.fn(),
		});

		const [confirmationMessage] = mockDeleteItemAction.mock.calls[0];

		expect(confirmationMessage).toContain(
			'&lt;script&gt;alert(1)&lt;&#047;script&gt;'
		);
		expect(confirmationMessage).not.toContain('<script>');
	});

	it('hides a bulk action when a selected project task lacks its permission', () => {
		const selectedItems = [
			{...projectItem, actions: {get: {}, update: {}}},
		];

		expect(getBulkAction('delete').isVisible({selectedItems})).toBe(false);

		expect(getBulkAction('update-state').isVisible({selectedItems})).toBe(
			true
		);
	});

	it('hides the assign-to bulk action when all items are selected or the selected tasks belong to more than one project', () => {
		expect(
			getBulkAction('assign-to').isVisible({
				allItemsSelectedActive: true,
			})
		).toBe(false);

		expect(
			getBulkAction('assign-to').isVisible({
				allItemsSelectedActive: false,
				selectedItems: [
					{...projectItem, actions: {update: {}}},
					{
						actions: {update: {}},
						embedded: {
							id: 2,
							r_cmpProjectToCMPTasks_c_cmpProjectId: 789,
						},
						entryClassName: 'com.liferay.object.model.ObjectEntry',
					},
				],
			})
		).toBe(false);
	});

	it('keeps bulk actions visible for a workflow task selection, deferring update-state and delete to the disabled state', () => {
		const selectedItems = [
			{
				...workflowItem,
				actions: {assignToUser: {}, get: {}, updateDueDate: {}},
			},
		];

		expect(getBulkAction('assign-to').isVisible({selectedItems})).toBe(
			true
		);
		expect(
			getBulkAction('update-due-date').isVisible({selectedItems})
		).toBe(true);

		expect(getBulkAction('delete').isVisible({selectedItems})).toBe(true);
		expect(getBulkAction('update-state').isVisible({selectedItems})).toBe(
			true
		);
	});

	it('keeps the assign-to bulk action visible and opens the modal scoped to the project when the selected tasks belong to a single project', async () => {
		const selectedItems = [
			{...projectItem, actions: {update: {}}},
			{
				actions: {update: {}},
				embedded: {id: 2, r_cmpProjectToCMPTasks_c_cmpProjectId: 456},
				entryClassName: 'com.liferay.object.model.ObjectEntry',
			},
		];

		expect(
			getBulkAction('assign-to').isVisible({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(true);

		const result = AllTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onBulkActionItemClick({
			action: {data: {id: 'assign-to'}},
			selectedData: {items: selectedItems, selectAll: false},
		});

		const {contentComponent} = mockOpenCMPModal.mock.calls[0][0];

		const element = contentComponent({closeModal: jest.fn()});

		expect(element.type).toBe(BulkEditAssigneeModalContent);
		expect(element.props.cmpProjectObjectEntryId).toBe(456);
	});

	it('keeps update-state and delete bulk actions enabled when a project task is selected', () => {
		const selectedItems = [projectItem];

		expect(
			getBulkAction('update-state').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(false);
		expect(
			getBulkAction('delete').isDisabled({
				allItemsSelectedActive: false,
				selectedItems,
			})
		).toBe(false);
	});

	it('opens BulkEditWorkflowAssigneeModalContent when assign-to is clicked for workflow tasks', async () => {
		const result = AllTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onBulkActionItemClick({
			action: {data: {id: 'assign-to'}},
			selectedData: {items: [workflowItem], selectAll: false},
		});

		const {contentComponent} = mockOpenCMPModal.mock.calls[0][0];

		expect(contentComponent({closeModal: jest.fn()}).type).toBe(
			BulkEditWorkflowAssigneeModalContent
		);
	});

	it('opens BulkEditWorkflowDueDateModalContent when update-due-date is clicked for workflow tasks', async () => {
		const result = AllTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onBulkActionItemClick({
			action: {data: {id: 'update-due-date'}},
			selectedData: {items: [workflowItem], selectAll: false},
		});

		const {contentComponent} = mockOpenCMPModal.mock.calls[0][0];

		expect(contentComponent({closeModal: jest.fn()}).type).toBe(
			BulkEditWorkflowDueDateModalContent
		);
	});
});
