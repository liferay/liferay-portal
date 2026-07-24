/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ProjectMembersFDSPropsTransformer from '../../js/components/props_transformer/ProjectMembersFDSPropsTransformer';
import manageMembersAction from '../../js/components/props_transformer/actions/manageMembersAction';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	MembersFDSPropsTransformer: jest.fn(({creationMenu, ...otherProps}) => ({
		...otherProps,
		creationMenu,
		customRenderers: 'membersCustomRenderers',
		hideManagementBarInEmptyState: true,
	})),
	addOnClickToCreationMenuItems: jest.fn((items, actions) =>
		items.map((item: {data?: {action?: string}}) => ({
			...item,
			onClick: ({loadData}: {loadData?: () => void}) => {
				const action = item?.data?.action;

				if (action) {
					actions[action](item.data, loadData);
				}
			},
		}))
	),
}));

jest.mock(
	'../../js/components/props_transformer/actions/manageMembersAction',
	() => jest.fn()
);

describe('ProjectMembersFDSPropsTransformer', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('keeps the members props transformer output', () => {
		const props = ProjectMembersFDSPropsTransformer({
			creationMenu: {primaryItems: []},
		} as any);

		expect(props.customRenderers).toBe('membersCustomRenderers');
		expect(props.hideManagementBarInEmptyState).toBe(true);
	});

	it('opens the project members modal from the add members action', () => {
		const itemData = {
			action: 'addMembers',
			assetLibraryCreatorUserId: '1',
			externalReferenceCode: 'ERC',
			hasAssignMembersPermission: true,
			title: 'Members (3)',
		};

		const loadData = jest.fn();

		const props = ProjectMembersFDSPropsTransformer({
			creationMenu: {primaryItems: [{data: itemData}]},
		} as any);

		(props.creationMenu.primaryItems as any[])[0].onClick({loadData});

		expect(manageMembersAction).toHaveBeenCalledTimes(1);
		expect(manageMembersAction).toHaveBeenCalledWith(itemData, loadData);
	});
});
