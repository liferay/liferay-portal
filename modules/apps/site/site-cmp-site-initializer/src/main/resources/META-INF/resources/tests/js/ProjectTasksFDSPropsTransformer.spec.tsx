/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import BulkEditAssigneeModalContent from '../../js/components/modal/BulkEditAssigneeModalContent';
import ProjectTasksFDSPropsTransformer from '../../js/components/props_transformer/ProjectTasksFDSPropsTransformer';

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

jest.mock('../../js/components/modal/BulkEditAssigneeModalContent', () => ({
	__esModule: true,
	default: () => null,
}));

jest.mock('../../js/components/modal/EditAssigneeModalContent', () => ({
	__esModule: true,
	default: () => null,
}));

const liferayLanguageGet = Liferay.Language.get;

Liferay.Language.get = (key: string) =>
	key === 'delete-task-confirmation-body'
		? 'You are about to delete the task "{0}."'
		: liferayLanguageGet(key);

const baseProps = {
	additionalProps: {states: []},
	apiURL: '/o/c/cmptasks',
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
	const result = ProjectTasksFDSPropsTransformer(baseProps as any);

	return (result.bulkActions as any[]).find(
		(action) => action.data.id === id
	);
};

const projectItem = {
	embedded: {id: 1, r_cmpProjectToCMPTasks_c_cmpProjectId: 456},
	entryClassName: 'com.liferay.object.model.ObjectEntry',
};

describe('ProjectTasksFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('escapes the task title in the delete confirmation message', async () => {
		const result = ProjectTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			itemData: {
				embedded: {title: '<script>alert(1)</script>'},
				entryClassName: 'com.liferay.object.model.ObjectEntry',
			},
			loadData: jest.fn(),
		});

		const [confirmationMessage] = mockDeleteItemAction.mock.calls[0];

		expect(confirmationMessage).toContain(
			'&lt;script&gt;alert(1)&lt;&#047;script&gt;'
		);
		expect(confirmationMessage).not.toContain('<script>');
	});

	it('hides a bulk action when a selected task lacks its permission', () => {
		expect(
			getBulkAction('delete').isVisible({
				selectedItems: [{actions: {get: {}, update: {}}}],
			})
		).toBe(false);
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

	it('keeps a bulk action visible when every selected task has its permission', () => {
		expect(
			getBulkAction('update-state').isVisible({
				selectedItems: [
					{actions: {get: {}, update: {}}},
					{actions: {delete: {}, get: {}, update: {}}},
				],
			})
		).toBe(true);
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

		const result = ProjectTasksFDSPropsTransformer(baseProps as any);

		await (result as any).onBulkActionItemClick({
			action: {data: {id: 'assign-to'}},
			selectedData: {items: selectedItems, selectAll: false},
		});

		const {contentComponent} = mockOpenCMPModal.mock.calls[0][0];

		const element = contentComponent({closeModal: jest.fn()});

		expect(element.type).toBe(BulkEditAssigneeModalContent);
		expect(element.props.cmpProjectObjectEntryId).toBe(456);
	});
});
