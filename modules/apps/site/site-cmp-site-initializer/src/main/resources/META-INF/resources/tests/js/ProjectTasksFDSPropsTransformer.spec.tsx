/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ProjectTasksFDSPropsTransformer from '../../js/components/props_transformer/ProjectTasksFDSPropsTransformer';

const mockDeleteItemAction = jest.fn();

jest.mock('@liferay/site-cms-site-initializer', () => ({
	SimpleActionLinkRenderer: jest.fn(),
	addOnClickToCreationMenuItems: jest.fn(),
	deleteAssetEntriesBulkAction: jest.fn(),
	deleteItemAction: (...args: any[]) => mockDeleteItemAction(...args),
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
	creationMenu: {primaryItems: []},
	id: 'test-fds',
	itemsActions: [],
	views: [{default: true, initialPaginationDelta: 20, name: 'table'}],
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
});
